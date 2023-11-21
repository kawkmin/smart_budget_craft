package com.personal.smartbudgetcraft.global.external.discord.api;

import com.personal.smartbudgetcraft.domain.member.entity.Member;
import com.personal.smartbudgetcraft.global.config.security.annotation.LoginMember;
import com.personal.smartbudgetcraft.global.dto.response.ApiResDto;
import com.personal.smartbudgetcraft.global.external.discord.application.WebHookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 디스코드 웹훅 관련 이벤트 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/webhook")
public class WebHookController {

  private final WebHookService webHookService;


  /**
   * 지출 조언 웹훅
   *
   * @param member 회원
   * @return 디스코드 웹훅 - 지출 조언
   */
  @PostMapping
  public ResponseEntity<ApiResDto> webHookAdviceComment(
      @LoginMember Member member
  ) {

    webHookService.callAdviceComment(member);
    return ResponseEntity.ok(ApiResDto.toSuccessForm(""));
  }

}
