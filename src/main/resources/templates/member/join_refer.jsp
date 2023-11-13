<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!doctype html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="Author" content="silverline">
<meta name="Keywords" content="">
<meta name="Description" content="">
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<title>회원가입</title>
<link rel="stylesheet" href="/resources/css/join.css" />
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
    <script>
        function execDaumPostcode() {
            daum.postcode.load(function () {
                new daum.Postcode({
                    oncomplete: function (data) {
                        var fullAddr = data.address; // 최종 주소 변수
                        var extraAddr = ''; // 조합형 주소 변수

                        // 기본 주소와 조합형 주소를 동일하게 설정
                        document.getElementById("addr").value = fullAddr;

                        // 기본 주소가 도로명 타입일 때 조합형 주소 설정
                        if (data.addressType === 'R') {
                            // 법정동명이 있을 경우 추가
                            if (data.bname !== '') {
                                extraAddr += data.bname;
                            }
                            // 건물명이 있을 경우 추가
                            if (data.buildingName !== '') {
                                extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                            }
                            // 조합형 주소를 추가
                            fullAddr += ' (' + extraAddr + ')';
                        }

                        // 주소 정보 입력 필드에 값 설정
                        document.getElementById("addr").value = fullAddr;
                    }
                }).open();
            });
        }

	function join() {
		var form = document.joinForm;

		if (!form.mem_id.value) {
			alert("아이디를 입력해주세요.");
			form.mem_id.focus();
			return;
		}
		if (form.mem_id.value.length < 4 || form.mem_id.value.length > 16) {
			alert("아이디는 4자 이상, 16자 이하로 입력해주세요.");
			form.mem_id.focus();
			return;
		}
		if (!form.pwd.value) {
			alert("비밀번호를 입력해주세요.");
			form.pwd.focus();
			return;
		}

		// 비밀번호 유효성 검사 로직 추가
		var passwordRegex = /^(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}$/;
		if (!passwordRegex.test(form.pwd.value)) {
			alert("비밀번호는 8자리 이상이어야 하며, 영문/숫자 모두 포함해야 합니다.");
			form.pwd.focus();
			return;
		}

		if (form.pwd.value !== form.pwd2.value) {
			alert("비밀번호를 확인해주세요.");
			form.pwd2.focus();
			return;
		}

		if (!form.name.value) {
			alert("이름을 입력해주세요.");
			form.name.focus();
			return;
		}
		

		form.submit();
	}

	function CheckDup() {
		var form = document.joinForm;
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");

		if (!form.mem_id.value) {
			alert("아이디를 입력해주세요.");
			form.mem_id.focus();
			return;
		}

		$.ajax({
			url:"/member/checkId.do",		// servlet 
			type: "post",
			datatype:"text",
			data: {"mem_id" : form.mem_id.value},
			beforeSend: function(xhr) { //XMLHttpRequest (XHR)은 AJAX 요청을 생성하는 JavaScript API이다. XHR의 메서드로 브라우저와 서버간의 네트워크 요청을 전송할 수 있다.
				xhr.setRequestHeader(header, token); //csrf 전송하지 않으면 아예 ajax가 되지 않는 문제가 생김.
			},
			success:function(data){
				if(data === 'success'){
					$('input[name=checkID]').val("ok");
					alert("사용 가능한 아이디입니다.")
					$('#message').text('사용할 수 있는 ID입니다.')   
					$('#message').css('color','green')
					
				}
				else {
					alert("사용 불가능한 아이디입니다.")
					$('#message').text('이미 사용 중인 아이디입니다.')
					$('#message').css('color','red')
					
				}
			},
			error:function(){
				alert("error");
			}
		})
	}
</script>
</head>
<body>
	<form name="joinForm" class="joinform" method="post" action="/member/join.do">
		<input type="hidden" name="checkID" value="no" />
		<div class="joinBox">
            <input type="button" onClick="history.go(-1);" class="closeBtn" value="X">
            <h1 class="tit">회원가입</h1>
			<div class="AlignRight MAT30">
				<span class="blet">* </span>표시는 필수입니다.
			</div>
			<table class="table_row W100P MAT10">
				<colgroup>
					<col style="">
					<col style="">
				</colgroup>
				<tr>
					<th><span class="blet">*</span> 아이디</th>
					<td>
						<input type="text" name="mem_id" size="20" maxlength="16" />
						<input type="button" value="중복 검사" onClick="CheckDup();" class="btnDup">
						<div id='message'></div>	
					</td>
				</tr>
				<tr>
					<th><span class="blet">*</span> 비밀번호</th>
					<td><input type="password" name="pwd" size="20" maxlength="16" />
						<span class="f12 fC666">※ 8~16글자의 영어, 숫자 혼용</span>
					</td>
				</tr>
				<tr>
					<th><span class="blet">*</span> 비밀번호 확인</th>
					<td><input type="password" name="pwd2" size="20" maxlength="16" /></td>
				</tr>
				<tr>
					<th><span class="blet">*</span> 이름</th>
					<td><input type="text" name="name" size="15" maxlength="6" /></td>
				</tr>
				<tr>
					<th>전화</th>
					<td><input type="text" name="pnum" size="15" maxlength="15" /></td>
				</tr>
				<tr>
					<th>이메일</th>
					<td><input type="email" name="email" size="30" maxlength="40" /></td>
				</tr>
				<tr>
					 <th>주소</th>
                <td>
                    <input type="text" id="addr" name="userAddr" placeholder="주소" size="60">
                    <input type="button" onclick="execDaumPostcode()" value="주소 찾기"><br>
                </td>
				</tr>
					<tr>
					 <th>상세주소</th>
                <td>
                    <input type="text" id="user_addr2" name="userDaddr" placeholder="상세주소" size="20">
                </td>
					</tr>
			</table>
			<div class="btnZone">
				<input type="button" id="btn" class="btn" onClick="join();" value="J O I N"/>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</div>
		</div>
	</form>
	
	<script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="/resources/js/bootstrap.js"></script>
</body>
</html>