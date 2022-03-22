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
    <script src="${path}/resources/js/login.js"></script>
    <title>로그인</title>
</head>
<body>
	<form>
		<input id="user_id" name="user_id" type="text" autocomplete="off" placeholder="아이디">
		<input id="user_pw" name="user_pw" type="password" autocomplete="off" placeholder="비밀번호">
		<input id="login_button" type="button" value="로그인">
	</form>
</body>
</html>