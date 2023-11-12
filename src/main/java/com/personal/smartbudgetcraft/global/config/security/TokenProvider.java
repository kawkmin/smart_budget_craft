package com.personal.smartbudgetcraft.global.config.security;

import com.personal.smartbudgetcraft.global.config.security.data.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 유저 정보로 JWT 토큰을 만들거나 토큰을 바탕으로 유저 정보를 가져옴
 * JWT 토큰에 관련된 암호화, 복호화, 검증 로직 담당.
 */
@Slf4j
@Component
public class TokenProvider {

  private static final String AUTHORITIES_KEY = "auth";
  private static final String BEARER_TYPE = "Bearer";
  public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
  public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
  private final Key key; // 시큐리티 키


  /**
   * properties 시큐리티 키를 이용하여,암호화 키 생성
   *
   * @param secretKey propertise 에서 관리 하는 키
   */
  public TokenProvider(@Value("${jwt.secret}") String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 유저 정보를 넘겨 받아 `Access Token`과 `Refresh Token`을 생성
   *
   * @param authentication 유저 정보
   * @return JWT 토큰 Dto
   */
  public TokenDto generateTokenDto(Authentication authentication, Long memberId) {
    // Access 토큰 만료일
    long now = (new Date()).getTime();
    Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

    // Access Token 생성
    String accessToken = createAccessToken(authentication, memberId);

    // Refresh Token 생성
    String refreshToken = Jwts.builder()
        .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();

    // 토큰 Dto 생성
    return TokenDto.builder()
        .grantType(BEARER_TYPE)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
        .build();
  }

  /**
   * AccessToken 생성
   */
  public String createAccessToken(Authentication authentication, Long memberId) {
    // 권한 가져오기
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    // Access 토큰 만료일
    long now = (new Date()).getTime();
    Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

    String accessToken = Jwts.builder()
        .setSubject(String.valueOf(memberId))       // payload "sub": "1" (ex)
        .claim(AUTHORITIES_KEY, authorities)        // payload "auth": "ROLE_USER"
        .setExpiration(accessTokenExpiresIn)        // payload "exp": 151621022 (ex)
        .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
        .compact();
    return accessToken;
  }

  /**
   * JWT 토큰을 복호화하여 토큰에 들어 있는 정보 꺼냄
   *
   * @param accessToken access 토큰
   * @return SecurityContext 에서 사용하기 위한 UsernamePasswordAuthenticationToken 형태로 반환
   */
  public Authentication getAuthentication(String accessToken) {
    // 토큰 복호화
    Claims claims = parseClaims(accessToken);

    if (claims.get(AUTHORITIES_KEY) == null) {
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    }

    // 클레임에서 권한 정보 가져오기
    Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    // UserDetails 객체를 만들어서 Authentication 리턴
    UserDetails principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  /**
   * 토큰을 복호화 하여, 클레임 정보를 꺼냄
   *
   * @param accessToken access 토큰
   * @return 클레임
   */
  private Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build()
          .parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  /**
   * 토큰 검증
   *
   * @param token 검증할 토큰
   * @return 올바르면 true, 올바르지 않으면 false
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      log.debug("잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.debug("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.debug("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.debug("JWT 토큰이 잘못되었습니다.");
    }
    return false;
  }


  /**
   * 토큰에서 회원 id 찾기
   *
   * @param token 토큰
   * @return 찾은 회원 ID
   */
  public Long getIdFromToken(String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(BEARER_TYPE.length() + 1);
    }

    return Long.valueOf(
        String.valueOf(Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject())
    );
  }
}