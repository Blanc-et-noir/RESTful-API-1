<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <c:set var="path" value="${pageContext.request.contextPath}"/>
    <script src="${path}/resources/js/jquery.js"></script>
    <script src="${path}/resources/js/jquery-ui.js"></script>
    <script src="${path}/resources/js/jquery-cookie.js"></script>
    <script src="${path}/resources/js/JSEncrypt.js"></script>
    <script src="${path}/resources/js/fullpage.js"></script>
    
    <script src="${path}/resources/js/swiper.js"></script>
    <script src="${path}/resources/js/main.js"></script>
    <script src="${path}/resources/js/article.js"></script>
    <script src="${path}/resources/js/problem.js"></script>
    
    <link rel="stylesheet" href="${path}/resources/css/main.css">
    <link rel="stylesheet" href="${path}/resources/css/swiper.css">
    <title>정보</title>
</head>
<body>
	<div id="form_cover"></div>
	<div id="alert_cover"></div>
	<div id="blancetnoir">Made By <span style="color:#06d6a0">B</span>lanc et <span style="color:#ee3f5c">N</span>oir</div>
    <div id="fullpage">
        <div class="section">
            <div class="slide touchable" style="background-color:#f4f4f4">
				<div id="slide_box" class="wrapper touchable">
  					<div class="swiper mySwiper touchable">
    					<div class="swiper-wrapper touchable">
      						<div class="swiper-slide touchable">
      							<img class="touchable" src="${path}/resources/image/image0.svg">
      							<p class="touchable"><span class='touchable highlight'>언</span>제, <span class='touchable highlight'>어</span>디서나</p>
      						</div>
      						<div class="swiper-slide">
      							<img class="touchable" src="${path}/resources/image/image1.svg">
      							<p class="touchable"><span class='touchable highlight'>모</span>바일로 <span class='touchable highlight'>간</span>편하게</p>
      						</div>
      						<div class="swiper-slide">
      							<img class="touchable" src="${path}/resources/image/image2.svg">
      							<p class="touchable"><span class='touchable highlight'>공</span>부하고</p>
      						</div>
      						<div class="swiper-slide">
      							<img class="touchable" src="${path}/resources/image/image3.svg">
      							<p class="touchable"><span class='touchable highlight'>평</span>가하고</p>
      						</div>
      						<div class="swiper-slide">
      							<img class="touchable" src="${path}/resources/image/image4.svg">
      							<p class="touchable"><span class='touchable highlight'>의</span>견을 <span class='touchable highlight'>공</span>유하세요</p>
      						</div>
  						</div>
					</div>
				</div>
            </div>
			<div class="slide" style="background-color:#f4f4f4">
				<div id="problem_form" class="touchable">
					<div>
						<select id="category_id" class="touchable"></select>
						<div id="get_problems_button" class='touchable'>조회하기</div>
					</div>
					<div id="problems" class='touchable'>
						
					</div>
				</div>
        	</div>
        </div>
        <div class="section">
            <div class="slide" style="background-color:#f4f4f4">
				<p>The boxes are not essential elements</p>
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
				<p>In this case, when the height of the browser is less than 2000px, then slide will be scrollable</p>
            </div>
        </div>
    </div>
</body>
</html>