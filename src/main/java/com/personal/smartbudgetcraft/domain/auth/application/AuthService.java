package com.personal.smartbudgetcraft.domain.auth.application;

import com.personal.smartbudgetcraft.domain.member.dao.MemberRepository;
import com.personal.smartbudgetcraft.domain.member.dto.request.MemberLoginReqDto;
import com.personal.smartbudgetcraft.domain.member.dto.request.MemberSignUpReqDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.config.redis.dao.RedisRepository;
import com.personal.smartbudgetcraft.global.config.security.TokenProvider;
import com.personal.smartbudgetcraft.global.config.security.data.TokenDto;
import com.personal.smartbudgetcraft.global.config.security.data.TokenReqDto;
import com.personal.smartbudgetcraft.global.error.BusinessException;
import com.personal.smartbudgetcraft.global.error.ErrorCode;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  // 시큐리티 설정에서 등록한 Bcrypt 인코더
  private final PasswordEncoder passwordEncoder;
  // 토큰 관리자
  private final TokenProvider tokenProvider;
  // 검증 관리자
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  // Refresh Token을 관리하는 Redis DAO
  private final RedisRepository redisRepository;

  /**
   * 회원 가입
   *
   * @param reqDto 회원가입 입력 데이터
   * @return 생성된 회원의 id
   */
  @Transactional
  public Long signup(MemberSignUpReqDto reqDto) {
    // 아이디 중복 서버에서 다시 확인
    checkDuplicateAccount(reqDto.getAccount());

    Member member = reqDto.toEntity(passwordEncoder);
    // 저장
    Member createdMember = memberRepository.save(member);

    return createdMember.getId();
  }

  /**
   * 회원 아이디 중복 확인
   *
   * @param account 확인할 회원 아이디
   */
  public void checkDuplicateAccount(String account) {
    if (memberRepository.findByAccount(account).isPresent()) {
      throw new BusinessException(account, "account", ErrorCode.MEMBER_ACCOUNT_DUPLICATE);
    }
  }

  /**
   * 로그인
   * 검증 후 JWT 토큰 발급
   *
   * @param reqDto 로그인 데이터 정보
   * @return 생성된 JWT 토큰
   */
  @Transactional
  public TokenDto login(MemberLoginReqDto reqDto) {
    // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
    UsernamePasswordAuthenticationToken authenticationToken = reqDto.toAuthentication();

    // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
    // authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행
    Authentication authentication = authenticationManagerBuilder.getObject()
        .authenticate(authenticationToken);

    // 3.유저의 계정 아이디로 유저 아이디 찾기
    // 위에서 검증을 했기 때문에 예외 처리를 안해도 된다.
    Long memberId = memberRepository.findByAccount(authentication.getName()).orElseThrow().getId();

    // 4. 인증 정보와 유저 아이디 기반 JWT 토큰 생성
    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication, memberId);

    // 5. Refresh Token 을 Redis DAO 에 저장
    redisRepository.setValues(String.valueOf(memberId), tokenDto.getRefreshToken(),
        Duration.ofMillis(TokenProvider.REFRESH_TOKEN_EXPIRE_TIME));

    // 6. 토큰 발급
    return tokenDto;
  }

  /**
   * Access 토큰 재발급
   *
   * @param tokenDto 토큰 dto
   * @return 재발급된 토큰
   */
  @Transactional
  public TokenDto reissue(Member member, TokenReqDto tokenDto) {

    // 1. Refresh Token 검증
    if (!tokenProvider.validateToken(tokenDto.getRefreshToken())) {
      throw new BusinessException(null, "token", ErrorCode.REFRESH_TOKEN_BAD_REQUEST);
    }

    // 2. Redis DAO 에 저장된 Refresh Token 인지 확인
    boolean isMatchRefreshToken = redisRepository.getValues(String.valueOf(member.getId()))
        .equals(tokenDto.getRefreshToken());

    if (!isMatchRefreshToken) {
      throw new BusinessException(null, "token", ErrorCode.REFRESH_TOKEN_MISMATCH);
    }

    // 3. 새로운 Token 생성
    Authentication authentication = tokenProvider.getAuthentication(tokenDto.getAccessToken());
    TokenDto newToken = tokenProvider.generateTokenDto(authentication, member.getId());

    // 4. 토큰 발급 - Refresh Token 은 새로 발급 X
    return TokenDto.builder()
        .accessToken(newToken.getAccessToken())
        .grantType(newToken.getGrantType())
        .refreshToken(tokenDto.getRefreshToken())
        .accessTokenExpiresIn(newToken.getAccessTokenExpiresIn())
        .build();
  }

  /**
   * 로그아웃 - Refresh Token 저장소 삭제
   *
   * @param member 로그인된 회원
   */
  public void logout(Member member) {
    redisRepository.deleteValues(String.valueOf(member.getId()));
  }
}
