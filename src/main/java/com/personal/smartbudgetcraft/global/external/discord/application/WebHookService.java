package com.personal.smartbudgetcraft.global.external.discord.application;

import com.personal.smartbudgetcraft.domain.expenditure.application.ExpenditureConsultingService;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.AdviceCommentResDto;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpenditureRecommendTodayResDto;
import com.personal.smartbudgetcraft.domain.expenditure.dto.response.ExpendituresRecommendTodayResDto;
import com.personal.smartbudgetcraft.domain.member.entity.Member;
import java.util.List;
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
    StringBuilder commentBuilder = new StringBuilder();
    commentBuilder.append("")
        .append(adviceComment).append("\n")
        .append(remainDayComment).append("\n")
        .append(remainDepositComment);
    String content = commentBuilder.toString();

    send(content);
  }

  /**
   * 오늘의 지출 추천 웹훅
   *
   * @param member 회원
   */
  public void callRecommendToday(Member member) {
    // 오늘의 추천 금액 결과 가져오기
    ExpendituresRecommendTodayResDto recommendTodayResDto =
        expenditureConsultingService.recommendTodayExpenditure(member);

    // 알람 내용 생성
    String content = generateCommentRecommendToday(member.getAccount(), recommendTodayResDto);

    // 알람 내보내기
    send(content);
  }

  /**
   * 오늘의 지출 추천 멘트 생성기
   *
   * @param memberAccount        회원 아이디 명
   * @param recommendTodayResDto 오늘의 추천 금액 결과 데이터 정보
   * @return 오늘의 지출 추천 멘트
   */
  private String generateCommentRecommendToday(String memberAccount,
      ExpendituresRecommendTodayResDto recommendTodayResDto
  ) {
    // 멘트 String builder
    StringBuilder commentBuilder = new StringBuilder();

    // 시작 멘트
    String startComment = "## 💰 " + memberAccount + "님의 오늘 지출 추천!";
    commentBuilder.append(startComment).append("\n");

    // 오늘의 추천 금액 멘트
    Integer recommendCost = recommendTodayResDto.getRecommendCost();
    String recommendCostComment = "### 오늘의 지출 추천 금액 : `" + recommendCost + "`원";
    commentBuilder.append(recommendCostComment).append("\n");

    // 카테고리별 추천 금액 멘트들
    commentBuilder.append("### 카테고리별 오늘의 지출 추천!!!").append("\n");
    commentBuilder.append("-----------------------------");
    List<ExpenditureRecommendTodayResDto> recommendExpenditures = recommendTodayResDto.getRecommendExpenditures();
    for (ExpenditureRecommendTodayResDto todayResDto : recommendExpenditures) {

      commentBuilder.append("\n");
      String categoryName = todayResDto.getCategoryName();
      Integer cost = todayResDto.getCost();
      String recommendComment =
          "> 📂 카테고리 : `" + categoryName + "`\n"
              + "> 💵 추천 금액 : `" + cost + "`원";

      commentBuilder.append(recommendComment).append("\n");
    }
    commentBuilder.append("-----------------------------").append("\n");

    // 남은 요일 멘트
    Integer remainDays = recommendTodayResDto.getRemainDays();
    String remainDayComment = "### 남은 요일 : `" + remainDays + "`일";
    commentBuilder.append(remainDayComment).append("\n");

    // 남은 예산 금액 멘트
    Integer remainTotalCost = recommendTodayResDto.getRemainTotalCost();
    String remainTotalCostComment = "### 남은 예산 금액 `" + remainTotalCost + "`원";
    commentBuilder.append(remainTotalCostComment).append("\n");

    return commentBuilder.toString();
  }

  /**
   * 디스코드 봇에 내용 데이터 전송
   *
   * @param content 전송할 내용
   */
  private void send(String content) {
    JSONObject data = new JSONObject();
    data.put("content", content);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<String> entity = new HttpEntity<>(data.toString(), headers);
    restTemplate.postForObject(url, entity, String.class);
  }
}
