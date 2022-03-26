# CBT 웹 서비스

<br/>

### 서비스 개요
<pre>
1. 정보처리기사와 같은 자격증 시험공부를 간단하게 할 수 있도록 기출문제와 해답을 제공하고
   특정 자격증에 대해 무작위로 문제를 풀 수 있도록 서비스를 제공함.
   
   또한 각 기출문제에 대해 출제일, 정답률등의 정보를 제공하고
   실제 시험과 동일하게 과목당 20문제씩 5과목을 공부할 수 있도록 서비스를 제공함.
   
2. 게시글 및 댓글 작성, 수정, 삭제, 조회등의 커뮤니티로써의 기본적인 기능을 제공함.
   각 사용자들은 자유게시판뿐만 아니라 특정 문제에 대해 자신의 의견 및 해법을 제시할 수 있음.
</pre>

<br/>

### 서비스 개발 목표
<pre>
1. 기존 세션기반 로그인 인증대신 JWT 토큰을 활용한 로그인 인증 구현 방법을 익히고
   이를 실용적인 서비스를 제공하는 웹 서비스 개발에 활용하고자함.
   
2. Redis에 대해 학습하고 스프링 프레임워크와 연동방법을 익히고
   이를 실제 서비스 개발에 효과적으로 활용하고자함.
   
3. 모바일 친화적인 CSR 방식의 렌더링을 채택함으로써 언제 어디서나 CBT 서비스를 제공받아
   쉽게 자격증 취득을 달성할 수 있도록 하는 효율적이고 생산적인 웹 서비스를 개발함.
</pre>

<br/>

### 서비스 특징
<pre>
1. 기존 세션방식 로그인대신, 두 가지의 JWT 토큰을 활용한 로그인 인증을 채택
   액세스 토큰, 리프레쉬 토큰 탈취에 대비하여 DB에 로그인 정보를 저장함.
   
   세션방식으로는 구현하지 못하던 자동로그인 기능을 JWT 토큰방식의 로그인을
   활용함으로써 사용자의 사용성을 높임.
   
   쿠키에 토큰을 저장하여 발급하므로 XSS, CSRF공격에 취약할 수 있으나
   이를 HTTPONLY 속성의 추가 및 난수 값의 도입을 통해 해결함
   
2. 기존 JSP를 활용한 SSR방식의 렌더링 대신 JQuery를 활용한 CSR방식의 렌더링 채택
   특히, 기존에 독자 개발한 fullpage.js 플러그인을 보완하여 적용함으로써
   모바일환경에서도 화면 스와이프를 통해 쉽게 사용할 수 있도록 함.
   
   일반 PC환경에서도 쉽게사용할 수 있도록 Drag & Drop 및 마우스 스크롤을 이용하여
   쉽게 섹션과 슬라이드를 이동할 수 있음
   
3. Redis를 활용한 액세스 토큰, 리프레쉬 토큰의 로그아웃 처리를 수행함
   기존 세션방식과는 달리 로그인정보를 클라이언트가 보유하므로 이에 대한
   새로운 로그아웃 처리 방법으로 Redis 블랙리스트를 활용함.
   
   그외에 RSA2048 암호화에 사용하는 공개키에 대응되는 비밀키를 Redis에 저장함으로써
   세션에 비밀키를 저장하지 못하는 문제점을 해소함.
</pre>

<br/>

# API 호출

<br/>

### 공개키 요청
로그인 및 회원가입시 민감한 정보를 RSA2048로 암호화하여 전송할 때 사용할 공개키 발급을 요청하는 API

<br/>

##### REQUEST
<pre>
URL : /restapi/user/getPublickey.do

dataType : json
</pre>

##### SUCCESS
<pre>
{
   flag : true,
   publickey : 공개키,
   content : 응답 메세지
}

세부사항

1. 요청에 성공하면 공개키에 대응되는 비밀키는 1분간 Redis에 저장되므로 해당 공개키는 1분간만 유효함.

2. 로그인 및 회원가입을 진행할 때 해당 공개키로 RSA2048 암호화를 수행후
   반드시 로그인 및 회원가입 API를 요청할 때 해당 공개키 또한 파라미터로 전달해야함.
</pre>

##### FAIL
<pre>
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

***

<br/>

### 로그인 요청
사용자의 ID, PW, 사용한 공개키를 전달받아 사용자를 인증하고 이에 대한 2가지 JWT 토큰을 발급하는 API

<br/>

##### REQUEST
<pre>
URL : /restapi/user/login.do

dataType : json

PARAMETER : {
   user_id : 사용자 ID
   user_pw : 서버로부터 전달받은 공개키로 RSA2048 암호화한 사용자 PW
   publickey : 서버로부터 전달받아 암호화에 사용한 공개키
}
</pre>

##### SUCCESS
<pre>
{
   flag : true,
   content : 응답 메세지
}

세부사항

1. 로그인 성공시 HTTPONLY 속성이 설정된 user_accesstoken, user_refreshtoken 쿠키가 발급되며
   각각의 쿠키는 2시간, 24 * 14시간(2주) 동안 유효함.
   
2. 로그인 인증여부가 필요한 API 요청시 매번 user_accesstoken을 같이 전달해야하며
   처음 API 요청시에 HTTP 401응답을 전달받으면 액세스토큰을 새로 발급받은 후
   다시 처음에 호출하고자 하던 API 호출을 진행함.
</pre>

##### FAIL
<pre>
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

***

<br/>

### 로그아웃 요청
자신이 발급받은 액세스 토큰과 리프레쉬 토큰에 대해 로그아웃 처리를 수행하는 API

<br/>

##### REQUEST
<pre>
URL : /restapi/token/logout.do

dataType : json

세부사항

1. 자신이 현재 사용중인 액세스 토큰(user_accesstoken)과
   리프레쉬 토큰(user_refreshtoken) 쿠키를 전달해야함.
</pre>

##### SUCCESS
<pre>
{
   flag : true,
   content : 응답 메세지
}

세부사항

1. 로그아웃시 전달해야할 토큰들은 자신이 처음 로그인후 발급 받았거나
   이후 갱신되어 새로 발급받은 액세스, 리프레쉬 토큰임.
</pre>

##### FAIL
<pre>
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

***

<br/>

### 토큰 갱신 요청
액세스 토큰의 기한 만료시에 새로운 액세스 토큰을 발급하는 API

<br/>

##### REQUEST
<pre>
URL : /restapi/token/refreshTokens.do

dataType : json

세부사항

1. 자신이 현재 사용중인 액세스 토큰(user_accesstoken)과
   리프레쉬 토큰(user_refreshtoken) 쿠키를 전달해야함.
</pre>

##### SUCCESS
<pre>
{
   flag : true,
   content : 응답 메세지,
   액세스(user_accesstoken) 토큰과 리프레쉬(user_refreshtoken) 토큰을 새로 발급함
}

세부사항

1. 해당 API를 정상적으로 호출한 이후에는 모든 로그인 인증 여부가 필요한 API 호출시
   반드시 새로 발급받은 액세스 토큰과 리프레쉬 토큰을 사용해야함.
</pre>

##### FAIL
<pre>
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

***

<br/>


### 회원가입 요청
사용자로부터 여러 정보를 전달받고 회원정보를 등록하는 API

<br/>

##### REQUEST
<pre>
URL : /restapi/user/join.do

dataType : json

PARAMETER : {
   user_id : 사용자 ID
   user_pw : 서버로부터 전달받은 공개키로 RSA2048 암호화한 사용자 PW,
   user_name : 사용자 이름,
   user_email : 사용자 이메일,
   question_id : 비밀번호 찾기 질문 번호,
   question_answer : 서버로부터 전달받은 공개키로 RSA2048 암호화한 사용자 비밀번호 찾기 질문에 대한 답,
   publickey : 서버로부터 전달받아 암호화에 사용한 공개키
}

세부사항

1. 사용자 이메일은 비밀번호 분실시 해당 이메일로 임시 비밀번호를 전달할 이메일 주소이므로
   이메일 주소는 회원가입시 기존에 등록되지 않은 것이어야함.
   
2. 비밀번호 찾기 질문은 해당 질문들에 대한 정보를 요청하는 API를 호출하여 조회할 수 있음.
</pre>

##### SUCCESS
<pre>
{
   flag : true,
   content : 응답 메세지
}
</pre>

##### FAIL
<pre>
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

***

<br/>

### 비밀번호 찾기 질문 목록 발급 요청
비밀번호 분실시 임시 비밀번호를 등록된 이메일로 전달받기 위한 비밀번호 찾기 질문 목록을 얻음

<br/>

##### REQUEST
<pre>
URL : /restapi/user/getQuestions.do

dataType : json
</pre>

##### SUCCESS
<pre>
{
   flag : true,
   content : 응답 메세지,
   list : [
      {
         question_id : 1,
         question_content : "기억에 남는 추억의 장소는?"
      },
      {
         question_id : 2,
         question_content : "자신의 인생 좌우명은?"
      },
      {
         question_id : 3,
         question_content : "가장 기억에 남는 선생님 성함은?"
      },
      
                           ....
   ]
}
</pre>

##### FAIL
<pre>
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

***

<br/>

### 문제 분류 목록 발급 요청
문제들의 분류 번호와 분류명을 얻음

<br/>

##### REQUEST
<pre>
URL : /restapi/user/getCategories.do

dataType : json

세부사항

1. 해당 API는 로그인이 필요한 기능이므로 반드시 액세스 토큰을 같이 전달해야함.

2. 액세스 토큰을 갖고 해당 API를 처음 호출했을때 HTTP 401 응답을 수신하면
   액세스 토큰이 만료되었을 수 있음.
   
   반드시 해당 액세스 토큰과 리프레쉬 토큰을 갖고 새로이 액세스 토큰과 리프레쉬 토큰을 재발급 받아야함.
   새로 발급받은 액세스 토큰으로 다시 한 번 요청을 시도해야함.
</pre>

##### SUCCESS
<pre>
{
   flag : true,
   content : 응답 메세지,
   list : [
      {
         category_id : 1,
         category_name : "정보처리기사 필기 1과목"
      },
      {
         category_id : 2,
         category_name : "정보처리기사 필기 2과목"
      },
      {
         category_id : 3,
         category_name : "정보처리기사 필기 3과목"
      },
      
                           ....
   ]
}
</pre>

##### FAIL
<pre>
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

***

<br/>

### 문제 목록 발급 요청
문제들의 목록과 그에 해당하는 보기들, 정답여부, 보기 선택횟수 등의 정보를 발급하는 API

<br/>

##### REQUEST
<pre>
URL : /restapi/user/getProblems.do

dataType : json

PARAMETER : {
   category_id : 문제 분류 번호,
   limit : 발급받을 문제의 수(전달하지 않거나 1이상의 정수가 아니면 기본 20문제를 발급함)
}

세부사항

1. 해당 API는 로그인이 필요한 기능이므로 반드시 액세스 토큰을 같이 전달해야함.

2. 액세스 토큰을 갖고 해당 API를 처음 호출했을때 HTTP 401 응답을 수신하면
   액세스 토큰이 만료되었을 수 있음.
   
   반드시 해당 액세스 토큰과 리프레쉬 토큰을 갖고 새로이 액세스 토큰과 리프레쉬 토큰을 재발급 받아야함.
   새로 발급받은 액세스 토큰으로 다시 한 번 요청을 시도해야함.
</pre>

##### SUCCESS
<pre>
{
   flag : true,
   content : 응답 메세지,
   problems : [
      {
         problem_id : 56,
         problem_content : "다음 중 자료사전(Data Dictionary)에서 선택의 의미를 나타내는 것은?",
         problem_image_name : null,
         answer_id : 1,
         answer_content : "[ ]",
         choices : [
            {
               choice_id : 1,
               choice_content : "[ ]",
               choice_yn : "Y",
               choice_count : 0,
            },
            {
               choice_id : 2,
               choice_content : "{ }",
               choice_yn : "N",
               choice_count : 0,
            },
            {
               choice_id : 3,
               choice_content : "＋",
               choice_yn : "N",
               choice_count : 0,
            },
            {
               choice_id : 4,
               choice_content : "＝",
               choice_yn : "N",
               choice_count : 0,
            }
         ]
      },
      {
         problem_id : 9,
         problem_content : "트랜잭션이 올바르게 처리되고 있는지 데이터를 감시하고 제어하는 미들웨어는?",
         problem_image_name : null,
         answer_id : 3,
         answer_content : "TP monitor",
         choices : [
            {
               choice_id : 1,
               choice_content : "RPC",
               choice_yn : "N",
               choice_count : 0,
            },
            {
               choice_id : 2,
               choice_content : "ORB",
               choice_yn : "N",
               choice_count : 0,
            },
            {
               choice_id : 3,
               choice_content : "TP monitor",
               choice_yn : "Y",
               choice_count : 0,
            },
            {
               choice_id : 4,
               choice_content : "HUB",
               choice_yn : "N",
               choice_count : 0,
            }
         ]
      },
      {
         problem_id : 63,
         problem_content : "바람직한 소프트웨어 설계 지침이 아닌 것은?"
         problem_image_name : null,
         answer_id : 3,
         answer_content : "모듈 간의 결합도는 강할수록 바람직하다.",
         choices : [
            {
               choice_id : 1,
               choice_content : "적당한 모듈의 크기를 유지한다.",
               choice_yn : "N",
               choice_count : 0,
            },
            {
               choice_id : 2,
               choice_content : "모듈 간의 접속 관계를 분석하여 복잡도와 중복을 줄인다.",
               choice_yn : "N",
               choice_count : 0,
            },
            {
               choice_id : 3,
               choice_content : "모듈 간의 결합도는 강할수록 바람직하다.",
               choice_yn : "Y",
               choice_count : 0,
            },
            {
               choice_id : 4,
               choice_content : "모듈 간의 효과적인 제어를 위해 설계에서 계층적 자료 조직이 제시되어야 한다.",
               choice_yn : "N",
               choice_count : 0,
            }
         ]
      },
                           ....
   ]
}
</pre>

##### FAIL
<pre>
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

***

<br/>
