<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%--<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>--%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
<link rel="stylesheet" href='<c:out value="/resources/css/login.css"/>'>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script type="text/javascript">
	$(function() {
//		$('#formContent #id').focus();
		
		// 에러가 있는 경우 메세지 처리
/*		var errMsg = $('#errMsg').val();
		if(errMsg != '') {
			alert(errMsg);
			$('#formContent').find('#id').focus();
		}*/
	});
</script>

<style>
img {
	width: 130px;
	height: 100px;
}
</style>

</head>
</head>
<body>
<header>

</header>

<div class="wrapper fadeInDown">
	<div class="logo-mobile">
	<a href="/home"><img src="../resources/images/icons/logo-01.png" alt="IMG-LOGO"></a>
	</div>
	<div id="formContent">
		<!-- Tabs Titles -->
		<h2 class="active">로그인</h2>
		<!-- Login Form -->
		<form action="/login" method="post">
			<input type="text" id="username" class="fadeIn second" name="username" placeholder="아이디"> 
			<input type="password" id="password" class="fadeIn third" name="password" placeholder="비밀번호" autocomplete="off"> 
			<input type="submit" class="fadeIn fourth" value="로그인">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" id="errMsg" value='${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}' />
		</form>
		<!-- Signup Link -->
		<div id="formFooter">
			<a class="underlineHover" href="/member/join">회원가입</a>
		</div>
	</div>
</div>
</body>
</html>