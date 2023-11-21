package com.personal.smartbudgetcraft.global.external.discord.application;

import com.personal.smartbudgetcraft.domain.expenditure.application.ExpenditureConsultingService;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.AdviceCommentResDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WebHookService {

  @Value("${discord.webhookURL}")
  private String url;

  private final ExpenditureConsultingService expenditureConsultingService;


  /**
   * 예산 조언 관련 웹훅
   *
   * @param member 회원
   */
  public void callAdviceComment(Member member) {
    // 예산 조언 계산 결과 가져오기
    AdviceCommentResDto adviceResDto = expenditureConsultingService.adviceComment(member);

    // 조언 멘트 
    String adviceComment = "## 🔔 " + adviceResDto.getComment();

    // 남은 요일 멘트
    String remainDayComment = "> 남은 요일 : `" + adviceResDto.getRemainDays() + "`일";

    // 남은 예산 금액 멘트
    String remainDepositComment = "> 남은 예산 금액 `" + adviceResDto.getRemainDeposit() + "`원";

    // 문자열로 포맷팅
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("")
        .append(adviceComment).append("\n")
        .append(remainDayComment).append("\n")
        .append(remainDepositComment);
    String content = stringBuilder.toString();

    // Json Object로 변경 후 알람 보내기
    JSONObject data = new JSONObject();
    data.put("content", content);
    send(data);
  }

  /**
   * 디스코드 봇에 JSONObject 데이터 전송
   *
   * @param object JsonObject
   */
  private void send(JSONObject object) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<String> entity = new HttpEntity<>(object.toString(), headers);
    restTemplate.postForObject(url, entity, String.class);
  }
}
