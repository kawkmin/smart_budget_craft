# 재산 관리 서비스

![image](https://github.com/kawkmin/geoRecommendEats/assets/86940335/fdc74557-b1e8-447e-8faa-a5fe2848b654)

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot 3.1.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring rest docs-6DB33F?style=for-the-badge"/></a>
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Data JPA-gray?style=for-the-badge&logoColor=white"/></a>
<img src="https://img.shields.io/badge/QueryDSL-0078D4?style=for-the-badge&logo=Spring Data JPA&logoColor=white"/></a>
<img src="https://img.shields.io/badge/MySQL 8-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/></a>
<img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/Junit-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Mockito-6DB33F?style=for-the-badge"/></a>
<img src="https://img.shields.io/badge/discord-4A154B?style=for-the-badge&logo=discord&logoColor=white">

## 목차

1. [개발 기간](#개발-기간)
2. [코드 컨벤션 규칙](#코드-컨벤션-규칙)
3. [프로젝트 개요 및 서비스 개요](#프로젝트-개요-및-서비스-개요)
4. [요구사항 분석](#요구사항-분석)
5. [프로젝트 동작 예시](#프로젝트-동작-예시)
6. [프로젝트 일정관리](#프로젝트-일정관리)
7. [ERD](#erd)
8. [프로젝트 패키지 구조](#프로젝트-패키지-구조)
9. [API 명세](#api-명세)
10. [테스트](#테스트)
11. [TIL 및 회고](#til-및-회고)

## [📌개발 기간](#목차)

2023-11-09 ~ 2023-11-18 (10일)

## [📌코드 컨벤션 규칙](#목차)

<details>
    <summary>자세히 (클릭)</summary>

### 공통 API Response Dto

---

다음과 같이 응답 형식을 정합니다.

```java
public class ApiResponse {

  private static final String STATUS_SUCCESS = "success";
  private static final String STATUS_FAIL = "fail";
  private static final String STATUS_ERROR = "error";

    ...
}
```

```
//성공
{
  "status": "success",
  "message": null,
  "data": {
    data1
  }
}

//일반 에러
{
  "status": "fail",
  "message": "fail message",
  "data": null
}

//예외 발생
{
  "status": "error",
  "message": "error message",
  "data": null
}
```

### Commit Convention

---

- **Commit message**
    - 구글 자바 컨벤션

- **Branch name**
    - Github 브랜치 전략
    - issue Name 동일

- **issue & Pull Request Template 사용**
    - 지정된 템플릿을 사용하여, 설명과 할일 등을 명시하기

- **Git Projects 사용**
    - Git Projects를 사용하여, 진행상황 시각화하기

### Code convention

---

- [구글 JAVA 스타일](https://github.com/google/styleguide)

### 주석 Convention

---

- 아래와 같은 형식으로 주석달기

```java
/**
 * 동작 기능 설명
 *
 * @param 파라미터 명      파라미터에 관한 설명
 * ...
 * @return 반환 설명
 */
public 타입 메소드명 or 클래스명(파라미터 타입,파라미터 명){
```

- 구현하지 못한 부분 `TODO` 로 남기기

```java
//TODO or /** TODO
```

- 모든 클래스와 메소드, 변수에도 주석 달기

```java
public class 클래스명 {
  //...


  /**
   * 변수명 설명 (Entity의 id 등) 
   */
  private Long id;
```

</details> 

## [📌프로젝트 개요 및 서비스 개요](#목차)

**본 서비스는 사용자들이 개인 재무를 관리하고 지출을 추적하는 데 도움을 주는 서비스입니다.**

- 사용자들이 예산을 설정하고 지출을 모니터링하며 재무 목표를 달성하는 데 도움이 됩니다.

- 여러 통계를 확인하면서, 매일 지출 컨설팅을 받아 소비 습관을 바꾸는 데 도움이 됩니다.

<details>
    <summary>서비스 개요 자세히 (클릭)</summary>

``` markdown 

#### 1. 회원은 회원가입을 해야 서비스를 이용할 수 있습니다.

- 복잡한 절차 없이 회원가입이 가능합니다.

#### 2. 예산 설정 및 추천 서비스

- 월별 총 예산을 설정합니다.
- 본 서비스는 카테고리 별 예산을 추천하여 사용자의 과다 지출을 방지할 수 있습니다.

#### 3. 지출 작성

- 사용자는 지출 을 금액, 카테고리 등을 지정하여 등록 합니다.
- 언제든지 수정 및 삭제 할 수 있습니다.

#### 4. 지출 컨설팅

- 월별 설정한 예산을 기준으로 오늘 소비 가능한 지출 을 알려줍니다.
- 매일 발생한 지출 을 카테고리 별로 안내받습니다.

#### 5. 지출 통계

- `지난 달 대비` , `지난 요일 대비`, `다른 유저 대비` 등 여러 기준 카테고리 별 지출 통계를 확인 할 수 있습니다.
```

</details>

## [📌요구사항 분석](#목차)

서비스 개요에 맞게 요구사항을 분석하여 작성하였습니다.

개발 중 새로운 의문이 들면, 다시 기록하였습니다.

<details>
    <summary>요구사항 분석 자세히 (클릭)</summary>

``` markdown
### 1. 유저

1. 사용자 회원가입
    1. `계정명`, `패스워드` 입력하여 회원가입
2. 사용자 로그인
    1. 로그인시 `JWT` 발급
    2. 이후 모든 API 요청 Header에 JWT 포함 후 유효성 검증 필수
3. 사용자 로그아웃
    1. 로그아웃시 DB에 있는 Refresh 토큰 삭제

### 2. 예산설정 및 설계

1. 지출 카테고리 목록
    1. `식비` , `교통` 등 일반적인 지출 카테고리 목록 반환

2. 예산 설정
    1. `카테고리`, `예산` 로 설정.
    2. 예산 설계 추천 시스템 (API)
        - `카테고리` 를 지정 안할 때, `카테고리` 별 배분 금액 추천.
        - 기존 모든 유저들의 카테고리 별 예산을 통계를 구해, 자동으로 카테고리에 배분.
            - 이때, 통계가 10프로 이하인 카테고리들은 모두 묶어 기타로 제공.
        - 계산식
            - `(예산 * 카테고리의 통계(((카테고리의 총 예산 금액)/(전체 총 예산 금액))* 100)) /100`
    3. 수정/삭제 가능

### 3. 지출 기록

1. 지출 속성
    1. `지출 일시`, `지출 금액`, `카테고리`, `메모`, `합계제외여부` 필수 포함
    2. 추가적인 필드 사용 가능
2. 지출 CRUD
    1. `생성`, `수정`, `읽기(상세)`, `읽기(목록)`, `삭제`, `합계제외 변경`
3. 지출 읽기(목록)
    1. 조회 기준: `기간(필수 입력)`, `카테고리`, `금액(최소, 최대 금액)`
    2. 조회된 모든 지출의 `지출 합계`, `카테고리 별 지출 합계` 포함
    3. `합계제외` 처리한 지출은 목록에는 포함되지만, 모든 `지출 합계`에서는 제외
    4. 페이징 기능 포함

### 4. 지출 컨설팅

1. 오늘 지출 가능한 금액 추천
    1. `월별` 예산을 만족 시키는 오늘 사용가능한 `총액`과 `카테고리 별 금액` 추천
    2. 이번 달의 `남은 일수` 고려
    3. 0원 또는 음수이면 안되고 `최소 추천 금액`을 설정

2. 유저 상황에 맞는 `조언 멘트`

   기준치 계산 = `지금까지 사용한 지출 비율 계산` / `원래 사용해야 할 예산`
    1. 매달 1일 일 때
        - 이번 달에도 열심히 예산관리를 해봐요!
    2. 잘 아끼고 있을 때 (기준치 1.3이상)
        - 잘 아끼고 있어요. 현명한 소비를 하고 계시네요
    3. 적당히 사용 중 일 때 (기준치 1.3 ~ 1.0 이상)
        - 적당히 사용 중이시네요. 조절된 소비는 중요해요
    4. 기준을 넘었을 때 (기준치 1.0 미만)
        - 기준을 조금 넘었어요. 지출을 다시 검토해보는 것도 좋겠어요.
    5. 예산을 초과했을 때 (기준치 0.0)
        - 예산을 초과했어요. 소비 패턴을 다시 살펴보는 게 좋겠어요.
3. 100원 단위 `반올림한 금액`으로 추천
4. 스케쥴러 및 웹훅으로 `알람` 구현 (선택 사항)
5. 오늘 지출한 내용 안내
    1. `총액`, `카테고리 별 금액` 안내
    2. `적정 금액`, `지출 금액`, `위험도` 를 카테고리 별로 안내

### 5. 지출 통계

> 사용자의 통계데이터 생성을 위해 Dummy 데이터를 생성합니다.

- `지난 달` 대비 `총액`, `카테고리 별` 소비율
    - 지난 달의 오늘 일차까지 해당하는 과거 모든 데이터 기록 대비
- `지난 요일` 대비 소비율
    - 오늘 요일에 해당되는 과거 모든 데이터 기록 대비
- `다른 유저` 대비 소비율
    - 다른 유저의 오늘에 해당하는 예산
    - 다른 유저의 소비율과 나의 소비율 대한 평균 비율

### 6. 추가

- Docker 적용
- AWS EC2, RDS를 이용한 배포
- Discord 알람 사용

### + 의문

#### 1. 기존 모든 유저들의 카테고리 별 예산을 통계를 구할 때, 계산하는데 오래걸리지 않을까?

- 만약 서비스가 커질 것을 예상하면, 카테고리 별로 모든 유저들의 사용 %를 가진 컬럼으로 관리 할 수 있을 것 같다.

#### 2. 예산 설계 추천 시스템에서 꼭 회원이 요구한 돈에 딱 맞게 추천해줘야할까?

- 결국 소수점으로 떨어지에, 100 단위로 정확하게 주는건 불가능.
- 두가지 경우로 구현 가능
    1. 요구한 돈에서 +- 300원 오차 발생하지만, 정확한 퍼센트로 추천. (채택)
    2. 돈에 딱 맞게 추천하지만, 정확한 퍼센트는 아님.
```

</details>

## [📌프로젝트 동작 예시](#목차)

### `Discord webHook` 을 통해 구현하였습니다.

#### 1. 회원 상황에 맞는 멘트 호출

![image](https://github.com/kawkmin/geoRecommendEats/assets/86940335/ad1b559e-c9b9-427b-9f21-1ddd698d35cb)

#### 2. 회원의 오늘 카테고리별 지출 추천

![image](https://github.com/kawkmin/geoRecommendEats/assets/86940335/11b766d0-4c91-4d75-8403-1ab1e36ac118)

## [📌프로젝트 일정관리](#목차)

**Git Projects 사용**

[Git Projects 링크](https://github.com/users/kawkmin/projects/5)

<details>
    <summary> 처음 모습 (클릭)</summary>

![image](https://github.com/kawkmin/geoRecommendEats/assets/86940335/13b023b5-378d-4249-8cfb-390f18f79024)


</details>

<details>
    <summary> 종료 후 모습  (클릭)</summary>

![image](https://github.com/kawkmin/geoRecommendEats/assets/86940335/033462e9-7587-4b19-8e53-db4e990839a0)

</details>

## [📌ERD](#목차)

**Erd Cloud**

![image](https://github.com/kawkmin/geoRecommendEats/assets/86940335/29b1eebb-16d8-4834-bf9b-5422940fb4a8)

## [📌프로젝트 패키지 구조](#목차)

<details>
    <summary>자세히 (클릭)</summary>

``` 
src
├─ docs
│  └─ asciidoc
├─ main
│  ├─ java
│  │  └─ com
│  │     └─ personal
│  │        └─ smartbudgetcraft
│  │           ├─ domain
│  │           │  ├─ auth
│  │           │  │  ├─ api
│  │           │  │  └─ application
│  │           │  │     └─ security
│  │           │  ├─ category
│  │           │  │  └─ cost
│  │           │  │     ├─ dao
│  │           │  │     └─ entity
│  │           │  ├─ deposit
│  │           │  │  ├─ api
│  │           │  │  ├─ application
│  │           │  │  ├─ dao
│  │           │  │  ├─ dto
│  │           │  │  │  ├─ request
│  │           │  │  │  └─ response
│  │           │  │  └─ entity
│  │           │  ├─ expenditure
│  │           │  │  ├─ api
│  │           │  │  ├─ application
│  │           │  │  ├─ constant
│  │           │  │  ├─ dao
│  │           │  │  │  └─ querydsl
│  │           │  │  ├─ dto
│  │           │  │  │  ├─ request
│  │           │  │  │  └─ response
│  │           │  │  └─ entity
│  │           │  └─ member
│  │           │     ├─ api
│  │           │     ├─ application
│  │           │     ├─ dao
│  │           │     │  ├─ budgettracking
│  │           │     ├─ dto
│  │           │     │  ├─ request
│  │           │     │  └─ response
│  │           │     └─ entity
│  │           │        └─ budgettracking
│  │           ├─ global
│  │           │  ├─ config
│  │           │  │  ├─ jpa
│  │           │  │  ├─ p6spy
│  │           │  │  ├─ redis
│  │           │  │  │  ├─ dao
│  │           │  │  ├─ security
│  │           │  │  │  ├─ annotation
│  │           │  │  │  ├─ data
│  │           │  │  │  ├─ filter
│  │           │  │  │  ├─ handler
│  │           │  │  ├─ valid
│  │           │  │  │  └─ annotation
│  │           │  │  └─ web
│  │           │  ├─ dto
│  │           │  │  └─ response
│  │           │  ├─ entity
│  │           │  ├─ error
│  │           │  ├─ external
│  │           │  │  └─ discord
│  │           │  │     ├─ api
│  │           │  │     └─ application
│  │           │  └─ schedule
│  │           └─ SmartBudgetCraftApplication.java
│  └─ resources
...
```

</details>

## [📌API 명세](#목차)

**Spring Rest Docs 기반 API 명세서**

### [API 명세서 Html로 보기 (클릭)](https://kawkmin.github.io/smart_budget_craft/src/main/resources/static/index.html)

![image](https://github.com/kawkmin/geoRecommendEats/assets/86940335/c7af00e9-a19a-4549-84cd-704e9e86cf78)

## [📌테스트](#목차)

### ✅ 70/70 (2.745s)

**Mockito & JUnit5 기반 계층별 단위**

![image](https://github.com/kawkmin/geoRecommendEats/assets/86940335/3cd4bccb-ab19-46ce-9b60-ac8cc2010400)

## [📌TIL 및 회고](#목차)

[N+1을 해결하는 BatchSize와 fetchJoin의 차이는 무엇일까?](https://invincible-sesame-584.notion.site/N-1-BatchSize-fetchJoin-7a96b68f3ba34bec98542735cd9ddca1)

[필터링 query문 VS Stream](https://invincible-sesame-584.notion.site/query-VS-Stream-87067a68f32b4789aa920518cc2f2031)

[localDate와 Date 차이](https://invincible-sesame-584.notion.site/localDate-Date-9af9951b21204bd68d052d01a4c70796)

[정확한 계산 빅데시마 무조건 써야할까?](https://invincible-sesame-584.notion.site/a4806d5ba1464e898cf4472bfabe2ab8)

[RefreshToken은 왜 Redis를 권장할까?](https://invincible-sesame-584.notion.site/RefreshToken-Redis-0cfe1dd3c2c24d8cbaf961aba17937ff)
