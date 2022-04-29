<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <!-- 모바일용웹 -->
	<meta name="viewport" content="user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1, width=device-width, height=device-height" />
	
	<!-- 안드로이드 홈화면추가시 상단 주소창 제거 -->
	<meta name="mobile-web-app-capable" content="yes">
	
	<!-- ios홈화면추가시 상단 주소창 제거 -->
	<!--
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">
	-->
    <c:set var="path" value="${pageContext.request.contextPath}"/>
    <script src="${path}/resources/js/jquery.js"></script>
    <script src="${path}/resources/js/jquery-ui.js"></script>
    <script src="${path}/resources/js/jquery-cookie.js"></script>
    <script src="${path}/resources/js/JSEncrypt.js"></script>
    <script src="${path}/resources/js/fullpage.js"></script>
        
    <script src="${path}/resources/js/screenfull.js"></script>
    <script src="${path}/resources/js/main.js"></script>
    <script src="${path}/resources/js/article.js"></script>
    <script src="${path}/resources/js/problem.js"></script>
    
    <link rel="manifest" href="${path}/resources/json/manifest.json" />
    <link rel="stylesheet" href="${path}/resources/css/main.css">
    <link rel="stylesheet" href="${path}/resources/css/swiper.css">
    <title>정보</title>
</head>
<body>
	<div id="form_cover"></div>
	<div id="alert_cover"></div>
	<div id="blancetnoir"><span>Made By </span><span style="color:#06d6a0">B</span><span>lanc et </span><span style="color:#ee3f5c">N</span><span>oir</span></div>
    <div id="fullpage">
        <div class="section">
            <div class="slide touchable" style="background-color:#f4f4f4">
            	<p></p>
            </div>
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
				<div id="card_board" class="touchable">
					<div id="move_button1" class="card touchable" style="top:20%; left:20%; transform:translate(-20%,-20%)">
						<img class="touchable" src="${path}/resources/image/mobile.svg">
						<p class="touchable">평가시험</p>
					</div>
					<div id="move_button2" class="card touchable" style="top:20%; left:80%; transform:translate(-80%,-20%)">
						<img class="touchable" src="${path}/resources/image/people.svg">
						<p class="touchable">커뮤니티</p>
					</div>
					<div id="move_button3" class="card touchable" style="top:80%; left:20%; transform:translate(-20%,-80%)">
						<img class="touchable" src="${path}/resources/image/person.svg">
						<p class="touchable">내 정보</p>
					</div>
					<div id="move_button4" class="card touchable" style="top:80%; left:80%; transform:translate(-80%,-80%)">
						<img class="touchable" src="${path}/resources/image/project.svg">
						<p class="touchable">고객지원</p>
					</div>
					<p style="position:absolute; top:110%; left:50%; transform:translate(-50%,-110%); text-align:center;">단 한 번의 클릭으로<br>원하는 서비스를 제공받아보세요.</p>
				</div>
            </div>
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
            	<div class="intro_box">
            		<div class="card touchable" style="top:50%; left:5%; transform:translate(-5%,-50%)">
					<img class="touchable" src="${path}/resources/image/mobile.svg">
					<p class="touchable">평가시험</p>
					</div>
					<p class="description">자신의 성적을 평가해보세요.<br>총 800여개의 문제가 제공됩니다.</p>
            	</div>
            </div>
			<div class="slide" style="background-color:#f4f4f4;">
				<div id="problem_form" class="touchable">
					<select id="category_id" class="touchable"></select>
					<div id="get_problems_button" class='touchable'>조회하기</div>
				</div>
				<div id="problem_div"></div>
        	</div>            
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
            	<div class="intro_box">
            		<div class="card touchable" style="top:50%; left:5%; transform:translate(-5%,-50%)">
					<img class="touchable" src="${path}/resources/image/people.svg">
					<p class="touchable">커뮤니티</p>
					</div>
					<p class="description">많은 사람들과 소통하세요.<br>그리고 더 많은 지식을 공유하세요.</p>
            	</div>
            </div>
            <div class="slide" style="background-color:#f4f4f4">
            	<form id="article_form" class="touchable">
                    <input id="article_title" name="article_title" class="touchable" type="text" required style="padding:10px" placeholder="게시글 제목">
                    <textarea id="article_content" name="article_content" class="touchable" required style="padding:10px" placeholder="게시글 내용"></textarea>
                    <input id="article_write_button" class="touchable" type="button" value="글 쓰기">
            	</form>
            </div>
            <div class="slide" style="background-color:#f4f4f4">
				<div id="article_list_form" class="touchable">
					<div id="article_list_panel" class="touchable">
						<select id="search_flag" class="touchable">
							<option selected value="user_id" label="ID">
							<option value="article_title" label="제목">
							<option value="article_content" label="내용">
						</select>
						<input id="article_search" name="article_search" class="touchable">
						<input id="article_search_button" class="touchable" type="button" value="검색">
					</div>
					<div id="article_list_header" class="touchable">
						<div id="id" class="touchable">ID</div>
						<div id="title" class="touchable">제목</div>
						<div id="date" class="touchable">날짜</div>
					</div>
					<div id="article_list">
						
					</div>
				</div>
            </div>
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
            	<div class="intro_box">
            		<div class="card touchable" style="top:50%; left:5%; transform:translate(-5%,-50%)">
					<img class="touchable" src="${path}/resources/image/person.svg">
					<p class="touchable">내 정보</p>
					</div>
					<p class="description">회원님의 정보가 궁금하신가요?<br>여기에 다양한 정보가 있습니다.</p>
            	</div>
            </div>
			<div class="slide" style="background-color:#f4f4f4">
				<p>내용</p>
        	</div>          
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
            	<div class="intro_box">
            		<div class="card touchable" style="top:50%; left:5%; transform:translate(-5%,-50%)">
					<img class="touchable" src="${path}/resources/image/project.svg">
					<p class="touchable">고객지원</p>
					</div>
					<p class="description">도움이 필요하신가요?<br>신속하게 문제를 해결해드립니다.</p>
            	</div>
            </div>
			<div class="slide" style="background-color:#f4f4f4">
				<p>내용</p>
        	</div>     
        </div>
    </div>
</body>
</html>