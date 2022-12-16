# CBT 웹 서비스

<br/>

### 서비스 개요
<pre>
1. 정보처리기사와 같은 자격증 시험공부를 간단하게 할 수 있도록 기출문제와 해답을 제공하고
   특정 자격증에 대해 무작위로 문제를 풀 수 있도록 서비스를 제공함.
   
   또한 각 기출문제에 대해 출제일, 정답률등의 정보를 제공하고
   실제 시험과 동일하게 과목당 20문제씩 5과목을 공부할 수 있도록 서비스를 제공함.
   
2. 게시글 및 댓글 작성, 수정, 삭제, 조회 등의 커뮤니티로써의 기본적인 기능을 제공함.
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
   
4. 열정키움장학생에 선발되고자 자신의 능력을 보여줄 수 있는 웹 서비스 개발 또한 하나의  
</pre>

<br/>

### 서비스 특징
<pre>
1. 기존 세션방식 로그인대신, 두 가지의 JWT 토큰을 활용한 로그인 인증을 채택
   액세스 토큰, 리프레쉬 토큰 탈취에 대비하여 DB에 로그인 정보를 저장함.
   
   세션방식으로는 구현하지 못하던 자동로그인 기능을 JWT 토큰방식의 로그인을
   활용함으로써 사용자의 사용성을 높임.
   
   쿠키에 토큰을 저장하여 발급하므로 XSS, CSRF공격에 취약할 수 있으나
   이를 HTTPONLY 속성의 추가 및 난수 값의 도입을 통해 해결함.
   
2. 기존 JSP를 활용한 SSR방식의 렌더링 대신 JQuery를 활용한 CSR방식의 렌더링 채택
   특히, 기존에 독자 개발한 fullpage.js 플러그인을 보완하여 적용함으로써
   모바일환경에서도 화면 스와이프를 통해 쉽게 사용할 수 있도록 함.
   
   일반 PC환경에서도 쉽게사용할 수 있도록 Drag & Drop 및 마우스 스크롤을 이용하여
   쉽게 섹션과 슬라이드를 이동할 수 있음.
   
3. Redis를 활용한 액세스 토큰, 리프레쉬 토큰의 로그아웃 처리를 수행함
   기존 세션방식과는 달리 로그인정보를 클라이언트가 보유하므로 이에 대한
   새로운 로그아웃 처리 방법으로 Redis 블랙리스트를 활용함.
   
   그외에 RSA2048 암호화에 사용하는 공개키에 대응되는 비밀키를 Redis에 저장함으로써
   세션에 비밀키를 저장하지 못하는 문제점을 해소함.
</pre>

<br/>

***

<br/>

### 서비스 동작

<details>
<summary>화면전환</summary>

<br>

![스와이핑](https://user-images.githubusercontent.com/83106564/182387491-f253b16b-c332-49d4-a335-6948ed7f4150.gif)

</details>


<details>
<summary>회원가입</summary>

<br>

![회원가입1](https://user-images.githubusercontent.com/83106564/182387874-4847edb8-0b3a-42b4-be88-3c65da584ed1.gif)
![회원가입2](https://user-images.githubusercontent.com/83106564/182389533-30dfe3c8-cce8-409f-a095-b9e76dea6738.gif)

</details>

<details>
<summary>로그인</summary>

<br>

![로그인](https://user-images.githubusercontent.com/83106564/182390499-730dd4a3-2ea8-43a4-be67-e0f2e3a883f3.gif)
</details>

<a id="api_list">
   
   # API 명세서
   
</a>

<br/>

#### 사용자 관련 API

#### 1. [공개키 요청](#/anchor1)

#### 2. [로그인 요청](#/anchor2)

#### 3. [로그아웃 요청](#/anchor3)

#### 4. [토큰 갱신 요청](#/anchor4)

#### 5. [회원 가입 요청](#/anchor5)

#### 6. [비밀번호 찾기 질문 목록 발급 요청](#/anchor6)

<br/>

#### 문제 관련 API

#### 7. [문제 분류 목록 발급 요청](#/anchor7)

#### 8. [문제 목록 발급 요청](#/anchor8)

#### 9. [문제 채점 요청](#/anchor9)

#### 10. [문제 이미지 요청](#/anchor10)

#### 11. [문제 의견 요청](#/anchor11)

#### 12. [문제 의견 작성 요청](#/anchor12)

#### 13. [문제 의견 수정 요청](#/anchor13)

#### 14. [문제 의견 삭제 요청](#/anchor14)

<br/>

#### 게시글 관련 API

#### 15. [게시글 목록 발급 요청](#/anchor15)

#### 16. [게시글 작성 요청](#/anchor16)

#### 17. [게시글 조회 요청](#/anchor17)

#### 18. [게시글 삭제 요청](#/anchor18)

#### 19. [게시글 수정 요청](#/anchor19)

#### 20. [게시글 이미지 조회 요청](#/anchor20)

<br/>

***

<br/>

<a id="/anchor1">
   
   ### 공개키 요청
   
</a>

#### 로그인 및 회원가입시 민감한 정보를 RSA2048로 암호화하여 전송할 때 사용할 공개키 발급을 요청하는 API

<br/>

<pre>
GET /restapi/users/publickeys HTTP/1.1
{
   
}
</pre>

<pre>
HTTP/1.1 200 OK
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

<pre>
HTTP/1.1 400 Bad Request
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

####  [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor2">
   
   ### 로그인 요청
   
</a>

#### 사용자의 ID, PW, 사용한 공개키를 전달받아 사용자를 인증하고 이에 대한 2가지 JWT 토큰을 발급하는 API

<br/>

<pre>
POST /restapi/tokens HTTP/1.1
{
   user_id : 사용자 ID
   user_pw : 서버로부터 전달받은 공개키로 RSA2048 암호화한 사용자 PW
   publickey : 서버로부터 전달받아 암호화에 사용한 공개키
}
</pre>

<pre>
HTTP/1.1 201 Created
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

<pre>
HTTP/1.1 400 Bad Request
{
    flag : false,
    content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor3">
   
   ### 로그아웃 요청
   
</a>

#### 자신이 발급받은 액세스 토큰과 리프레쉬 토큰에 대해 로그아웃 처리를 수행하는 API

<br/>

<pre>
DELETE /restapi/tokens HTTP/1.1
{

}

세부사항

1. 로그아웃시 전달해야할 토큰들은 자신이 처음 로그인후 발급 받았거나
   이후 갱신되어 새로 발급받은 액세스, 리프레쉬 토큰임.
</pre>

<pre>
HTTP/1.1 200 OK
{
   flag : true,
   content : 응답 메세지
}
</pre>

<pre>
HTTP/1.1 401 Unauthorized
{
    flag : false,
    content : "응답메세지"
}

세부사항

1. 로그아웃에 사용했던 액세스 토큰 또는 리프레쉬 토큰에 문제가 있거나
   두 종류의 토큰을 같이 전달하지 않아서 발생할 수 있음.
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor4">
   
   ### 토큰 갱신 요청
   
</a>

#### 액세스 토큰의 기한 만료시에 새로운 액세스 토큰을 발급하는 API

<br/>

<pre>
PUT /restapi/tokens HTTP/1.1
{

}

세부사항

1. 자신이 현재 사용중인 액세스 토큰(user_accesstoken)과
   리프레쉬 토큰(user_refreshtoken) 쿠키를 전달해야함.
</pre>

<pre>
HTTP/1.1 200 OK
{
   flag : true,
   content : 응답 메세지
}

세부사항

1. 해당 API를 정상적으로 호출하면 액세스(user_accesstoken) 토큰과
   리프레쉬(user_refreshtoken) 토큰을 새로 발급함

2. 해당 API를 정상적으로 호출한 이후에는 모든 로그인 인증 여부가 필요한 API 호출시
   반드시 새로 발급받은 액세스 토큰과 리프레쉬 토큰을 사용해야함.
</pre>

<pre>
HTTP/1.1 401 Unauthorized
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor5">
   
   ### 회원 가입 요청
   
</a>

#### 사용자로부터 여러 정보를 전달받고 회원정보를 등록하는 API

<br/>

<pre>
POST /restapi/users HTTP/1.1
{
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

<pre>
HTTP/1.1 201 Created
{
   flag : true,
   content : 응답 메세지
}
</pre>

<pre>
HTTP/1.1 400 Bad Request
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor6">
   
   ### 비밀번호 찾기 질문 목록 발급 요청
   
</a>

#### 비밀번호 분실시 임시 비밀번호를 등록된 이메일로 전달받기 위한 비밀번호 찾기 질문 목록을 발급하는 API

<br/>

<pre>
GET /restapi/users/questions HTTP/1.1
{
   
}
</pre>

<pre>
HTTP/1.1 200 OK
{
   flag : true,
   content : 응답 메세지,
   list : [
      {
         question_id : "493e3234-d4f3-11ec-971d-2cfda159869a",
         question_content : "기억에 남는 추억의 장소는?"
      },
      {
         question_id : "493f84a4-d4f3-11ec-971d-2cfda159869a",
         question_content : "자신의 인생 좌우명은?"
      },
      {
         question_id : "49409b71-d4f3-11ec-971d-2cfda159869a",
         question_content : "가장 기억에 남는 선생님 성함은?"
      },
      
                           ....
   ]
}
</pre>

<pre>
HTTP/1.1 400 Bad Request
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor7">
   
   ### 문제 분류 목록 발급 요청
   
</a>

#### 문제들의 분류 번호와 분류명을 발급하는 API

<br/>

<pre>
GET /restapi/problems/categories HTTP/1.1
{
   
}

</pre>

<pre>
HTTP/1.1 200 OK
{
   flag : true,
   content : 응답 메세지,
   list : [
      {
         category_id : "79170a18-bb6a-4055-a4e2-50ebeaab3900",
         category_name : "정보처리기사 필기 1과목"
      },
      {
         category_id : "76db87b3-4151-47ac-a502-d02404a553fa",
         category_name : "정보처리기사 필기 2과목"
      },
      {
         category_id : "96b42835-41ab-4752-b32d-b16c98dbca1f",
         category_name : "정보처리기사 필기 3과목"
      },
      
                           ....
   ]
}
</pre>

<pre>
HTTP/1.1 400 Bad Request
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor8">
   
   ### 문제 목록 발급 요청
   
</a>

#### 문제들의 목록과 그에 해당하는 보기들, 정답여부, 보기 선택횟수 등의 정보를 발급하는 API

<br/>

<pre>
GET /restapi/problems?category_id={category_id}&limit={limit} HTTP/1.1
{

}

세부사항
   
1. category_id는 문제 분류 번호

2. limit는 발급받을 문제의 수(전달하지 않거나 1이상의 정수가 아니면 기본 20문제를 발급함)
</pre>

<pre>
HTTP/1.1 200 OK
{
   flag : true,
   content : 응답 메세지,
   problems : [
      {
         problem_id : "b2e31a97-b0a4-41dd-8cef-39379e8012d0",
         problem_content : "UML에서 활용되는 다이어그램 중, 시스템의 동작을 표현하는 행위(Behavioral) 다이어그램에 해당하지 않는 것은?",
         problem_image_name : null,
         answer_id : "37570347-1664-40d8-b82a-43cf9dd457a6"1,
         answer_rate : 100,
         choices : [
            {
               choice_id : "4d8e453e-cd8b-4d1d-bae6-20406801b28b",
               choice_content : "유스케이스 다이어그램(Use Case Diagram)",
               choice_yn : "N",
               pick_rate : 0
            },
            {
               choice_id : "53bdf55e-b871-4c18-a67c-76109973c622",
               choice_content : "시퀀스 다이어그램(Sequence Diagram)",
               choice_yn : "N",
               pick_rate : 0
            },
            {
               choice_id : "9e9717a2-a8cd-45b1-8350-d889e5259736",
               choice_content : "활동 다이어그램(Activity Diagram)",
               choice_yn : "N",
               pick_rate : 0
            },
            {
               choice_id : "37570347-1664-40d8-b82a-43cf9dd457a6",
               choice_content : "배치 다이어그램(Deployment Diagram)",
               choice_yn : "Y",
               pick_rate : 100
            }
         ]
      },
      {
         problem_id : "8b022abd-f1a5-4349-8854-fdc811cc62b3",
         problem_content : "다음 내용이 설명하는 디자인 패턴은?",
         problem_image_name : "ca724d98ea2af1c2db7d7d3bc2bb63c4",
         answer_id : "1580a690-ff36-439c-8eee-11d5e0ffd231",
         answer_rate : 75,
         choices : [
            {
               choice_id : "1a4d6556-6e4c-4a6d-bcd6-05ed793a7503",
               choice_content : "Visitor 패턴",
               choice_yn : "N",
               pick_rate : 0
            },
            {
               choice_id : "a490dc70-24cb-43cd-ba16-559e478fc5f4",
               choice_content : "Observer 패턴",
               choice_yn : "N",
               pick_rate : 25
            },
            {
               choice_id : "1580a690-ff36-439c-8eee-11d5e0ffd231",
               choice_content : "Factory Method 패턴",
               choice_yn : "Y",
               pick_rate : 75
            },
            {
               choice_id : "a0453b85-33d3-45a4-b161-092d1e274aaa",
               choice_content : "Bridge 패턴",
               choice_yn : "N",
               pick_rate : 0
            }
         ]
      },
      {
         problem_id : "3f7258e8-f768-47db-a81d-602d6304653a",
         problem_content : "코드화 대상 항목의 중량, 면적, 용량 등의 물리적 수치를 이용하여 만든 코드는?",
         problem_image_name : null,
         answer_id : "0ecdedaa-b969-4973-9d8b-da5ba08f7a6f",
         answer_rate : 75,
         choices : [
            {
               choice_id : "3ed15afe-ad04-4435-9e4a-244d95b59d1f",
               choice_content : "순차 코드",
               choice_yn : "N",
               pick_rate : 13
            },
            {
               choice_id : "d560e4af-e22e-43bd-a7a2-e0809257e50b",
               choice_content : "10진 코드",
               choice_yn : "N",
               pick_rate : 0
            },
            {
               choice_id : "0ecdedaa-b969-4973-9d8b-da5ba08f7a6f",
               choice_content : "표의 숫자 코드",
               choice_yn : "Y",
               pick_rate : 75
            },
            {
               choice_id : "70cb4b60-0472-49f7-b650-b6c56ccf6789",
               choice_content : "블록 코드",
               choice_yn : "N",
               pick_rate : 13
            }
         ]
      },
                           ....
   ]
}
</pre>

<pre>
HTTP/1.1 400 Bad Request
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor9">
   
   ### 문제 채점 요청
   
</a>

#### 문제에 대한 채점 요청및 이에 대한 채점결과를 반환하는 API

<br/>

<pre>
POST /restapi/users/records HTTP/1.1
Content-Type : application/json
{
   list : [
      {
         problem_id : "fd431f82-a1ae-4cd8-be31-bc08d11f9fdd",
         answer_id : "a5fb2d2c-147d-45f1-be9a-1fb38831cbaf"
      },
      {
         problem_id : "a772ad18-669b-422e-9c16-4c4f1998600f",
         answer_id : "130ddcca-db48-4148-a5a2-fef004a92c6e"
      },
      {
         problem_id : "80c58a6c-ef1d-401b-b1c1-215f78710293",
         answer_id : "8eb2fbf5-9e6a-455d-9f49-955cf4965ec5"
      },
                 ....
   ]
}

세부사항

1. 해당 API는 로그인이 필요한 기능이므로 반드시 액세스 토큰을 같이 전달해야함.

2. 액세스 토큰을 갖고 해당 API를 처음 호출했을때 HTTP 401 응답을 수신하면
   액세스 토큰이 만료되었을 수 있음.
   
   반드시 해당 액세스 토큰과 리프레쉬 토큰을 갖고 새로이 액세스 토큰과 리프레쉬 토큰을 재발급 받아야함.
   새로 발급받은 액세스 토큰으로 다시 한 번 요청을 시도해야함.
</pre>

<pre>
HTTP/1.1 201 Created
{
   flag : true,
   content : 응답 메세지,
   percentage : 정답률,
   right_score : 정답 개수,
   wrong_score : 오답 개수,
   right_problems  : [ 맞춘 문제 ID1, 맞춘 문제 ID2, 맞춘 문제 ID3, ... ],
   wrong_problems  : [ 틀린 문제 ID1, 틀린 문제 ID2, 틀린 문제 ID3, ... ]
}
</pre>

<pre>
HTTP/1.1 401 Unauthorized
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor10">
   
   ### 문제 이미지 요청
   
</a>

#### 해당 문제에 대한 이미지가 있을시, 해당 이미지를 요청하는 API

<br/>

<pre>
GET /restapi/problems/{problem_id}/images/{problem_image_name}
Accept : image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8
Accept-Encoding : gzip, deflate
Accept-Language : ko,en;q=0.9,en-US;q=0.8
{
   
}

세부사항

1. 문제에 첨부된 이미지는 .png 파일로 응답

</pre>

<pre>
HTTP/1.1 200 Ok
{

}
</pre>

<pre>
HTTP/1.1 400 Bad Request
{

}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor11">
   
   ### 문제 의견 요청
   
</a>

#### 문제에 대한 댓글을 요청하는 API

<br/>

<pre>
GET /restapi/problems/{problem_id}/opinions?section={section}&page={page} HTTP/1.1
{
   
}

세부사항

1. section과 page는 모두 1이상의 정수임

2. section당 최대 25개, page당 최대 5개의 댓글을 보유할 수 있음

</pre>

<pre>
HTTP/1.1 200 Ok
{
   flag : true,
   content : 응답 메세지,
   total : 해당 문제에 관한 의견의 총 개수( 페이징에 활용 가능 ),
   list : [
      {
         opinion_date : "2022-05-04 17:55:44",
         opinion_id : "12ffcb26-42a5-4019-b256-828c49491299",
         user_id : "jrw9215",
         user_name : "정래원",
         editable : "true",
         opinion_content : "이 문제는 다시 공부해봐야 할 것 같네요"
      },
      {
         opinion_date : "2022-05-04 17:55:37",
         opinion_id : "0904c22a-90c4-43cf-95a1-97876197ea03",
         user_id : "jrw9215",
         user_name : "정래원",
         editable : "true",
         opinion_content : "답이 조금 모호한 것 같아요"
      },
      {
         opinion_date : "2022-05-04 17:55:36",
         opinion_id : "8abcea5b-05aa-4d72-8ec6-899f7b470643",
         user_id : "jrw9215",
         user_name : "정래원",
         editable : "true",
         opinion_content : "좀 어렵긴 하지만, 못 풀 문제는 아닌 것 같은데요?"
      },
                                 ....
   ]
}
</pre>

<pre>
HTTP/1.1 400 Bad Request
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor12">
   
   ### 문제 의견 작성 요청
   
</a>

#### 문제에 대한  작성을 요청하는 API

<br/>

<pre>
POST /restapi/problems/{problem_id}/opinions HTTP/1.1
{
   opinion_content : "의견 내용"
}

세부사항

1. 한글로 200자, 영어로 600자까지 의견 작성 가능

</pre>

<pre>
HTTP/1.1 201 Created
{
   flag : true,
   content : 응답 메세지
}
</pre>

<pre>
HTTP/1.1 401 Unauthorized
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor13">
   
   ### 문제 의견 수정 요청
   
</a>

#### 문제에 대한 의견 수정을 요청하는 API

<br/>

<pre>
PUT /restapi/problems/{problem_id}/opinions/{opinion_id} HTTP/1.1
{
   opinion_content : 의견 내용
}

세부사항

1. 한글로 200자, 영어로 600자까지 의견 수정 가능

2. 본인의 의견만 수정 가능

</pre>

<pre>
HTTP/1.1 200 Ok
{
   flag : true,
   content : 응답 메세지
}
</pre>

<pre>
HTTP/1.1 401 Unauthorized
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor14">
   
   ### 문제 의견 삭제 요청
   
</a>

#### 문제에 대한 의견 삭제를 요청하는 API

<br/>

<pre>
DELETE /restapi/problems/{problem_id}/opinions/{opinion_id} HTTP/1.1
{
   
}

세부사항

1. 본인의 의견만 삭제 가능

</pre>

<pre>
HTTP/1.1 200 Ok
{
   flag : true,
   content : 응답 메세지
}
</pre>

<pre>
HTTP/1.1 401 Unauthorized
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor15">
   
   ### 게시글 목록 발급 요청
   
</a>

#### 게시글 목록 발급을 요청하는 API

<br/>

<pre>
GET /restapi/articles?search_flag={search_flag}&search_content={search_content}?section={section}?page={page} HTTP/1.1
{
   
}

세부사항

1. search_flag, search_content는 각각 검색기준, 검색내용을 나타내며
   search_flag를 전달하지 않으면 기본적으로 user_id를 기준으로 검색하고
   search_content를 전달하지 않으면 모든 내용을 검색함
   
2. section과 page는 1이상의 정수값을 필수로 전달해야하며
   각 section은 5개의 page를, 각 page는 5개의 게시글을 담을 수 있음
   
</pre>

<pre>
HTTP/1.1 200 Ok
{
   flag : true,
   content : 응답 메세지,
   articles : [
      {
         article_id : 게시글 ID,
         article_title : 게시글 제목,
         article_date : 게시글 작성 날짜,
         article_view : 게시글 조회수,
         user_id : 작성자 ID,
         user_name : 작성자 이름
      },
      {
         article_id : 게시글 ID,
         article_title : 게시글 제목,
         article_date : 게시글 작성 날짜,
         article_view : 게시글 조회수,
         user_id : 작성자 ID,
         user_name : 작성자 이름
      },
      {
         article_id : 게시글 ID,
         article_title : 게시글 제목,
         article_date : 게시글 작성 날짜,
         article_view : 게시글 조회수,
         user_id : 작성자 ID,
         user_name : 작성자 이름
      },
                  ....
   ],
   articles_total : 게시글 총 개수
}
</pre>

<pre>
HTTP/1.1 400 Bad Request
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor16">
   
   ### 게시글 작성 요청
   
</a>

#### 게시글 작성을 요청하는 API

<br/>

<pre>
POST /restapi/articles HTTP/1.1
Content-Type : multipart/form-data
{
   article_title : 게시글 제목,
   article_content : 게시글 내용,
   article_image_files : [ Multipart File, Multipart File, Multipart File ]
}   
</pre>

<pre>
HTTP/1.1 201 Created
{
   flag : true,
   content : 응답 메세지
}
</pre>

<pre>
HTTP/1.1 400 Bad Request
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>

<a id="/anchor17">
   
   ### 게시글 조회 요청
   
</a>

#### 게시글 조회를 요청하는 API

<br/>

<pre>
GET /restapi/articles/{article_id} HTTP/1.1
{

}   
</pre>

<pre>
HTTP/1.1 200 Ok
{
   flag : true,
   content : 응답 메세지,
   article_id : 게시글 ID,
   article_title : 게시글 제목,
   article_content : 게시글 내용,
   article_date : 게시글 작성 날짜,
   article_view : 게시글 조회수,
   article_images : [
      {
         article_id : 게시글 ID,
         article_image_id : 게시글 이미지 ID,
         article_image_extension : 게시글 이미지 확장자 
      },
      {
         article_id : 게시글 ID,
         article_image_id : 게시글 이미지 ID,
         article_image_extension : 게시글 이미지 확장자
      },
      {
         article_id : 게시글 ID,
         article_image_id : 게시글 이미지 ID,
         article_image_extension : 게시글 이미지 확장자 
      }
   ],
   user_id : 작성자 ID,
   user_name : 작성자 이름,
   editable : true 또는 false
}
</pre>
<pre>
HTTP/1.1 400 Bad Request
{
   flag : false,
   content : 응답 메세지
}
</pre>

<br/>

#### [API 목록으로 되돌아가기](#api_list)

<br/>

***

<br/>
