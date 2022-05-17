var formFlag = false;
var publickey;

//로그인 상태라면 로그아웃 버튼을 렌더링
function setLogin(){
	$("#loginForm_button").remove();
	$("#joinForm_button").remove();
	$("body").append("<div id='logout_button'>로그아웃</div>");
}

//로그아웃 상태라면 로그인, 회원가입 버튼을 렌더링
function setLogout(){
	$("#logout_button").remove();
	$("body").append("<div id='loginForm_button'>로그인</div>");
	$("body").append("<div id='joinForm_button'>회원가입</div>");
}

function checkBytes(text, MAX_BYTES){
	var sum = 0;
	for(var i=0;i<text.length;i++){
		if(escape(text.charAt(i)).length>4){
			sum=sum+3;
		}else{
			sum++;
		}
	}
	if(sum>MAX_BYTES){
		return false;
	}else{
		return true;
	}
}


function check(str) {
	var regExp = /^[a-z0-9_]{6,16}$/;		
	if(str.length==0||!regExp.test(str)) {
		return false; 
	} else { 
		return true; 
	} 
}

function checkEmail(str) {
	var regExp = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
	if(str.length==0||!regExp.test(str)) {
		return false; 
	} else { 
		return true; 
	} 
}

function closeForm(target){
	$(target).parent().parent().remove();
	$("#form_cover").css({
		"display":"none"
	})
	formFlag=false;
}

function refreshTokens(){
	return $.ajax({
		"url":"/restapi/tokens",
		"type":"put"
	})
}

function openForm(form){
	if(formFlag==false){
		formFlag=true;
    	$("#form_cover").css({
    		"display":"block"
    	})
    	$("body").append(form);
	}
}

function getInfo(){
	return $.ajax({
		"url":"/restapi/users",
		"type":"get"
	})
}

function getPublickey(){
	return $.ajax({
		"url":"/restapi/publickeys",
		"type":"get"
	})
}

function login(publickey){
	return $.ajax({
		"url":"/restapi/tokens",
		"type":"post",
		"data":{
			"user_id":$("#user_id").val(),
			"user_pw":encryptByRSA2048($("#user_pw").val(),publickey),
			"publickey":publickey
		}
	});
}

function getQuestions(){
	return 	$.ajax({
		"url":"/restapi/questions",
		"type":"get",
		"dataType":"json"
	});
}

function join(publickey){
	return $.ajax({
		"url":"/restapi/users",
		"type":"post",
		"dataType":"json",
		"data":{
			"user_id":$("#user_id").val(),
			"user_pw":encryptByRSA2048($("#user_pw").val(),publickey),
			"publickey":publickey,
			"user_name":$("#user_name").val(),
			"user_email":$("#user_email").val(),
			"question_id":$("#question_id").val(),
			"question_answer":encryptByRSA2048($("#question_answer").val(),publickey)
		}
	})
}

function myFullFunction() {

	var docElm = document.documentElement;
	if (docElm.requestFullscreen) {
		docElm.requestFullscreen();
	}
	else if (docElm.mozRequestFullScreen) {
		docElm.mozRequestFullScreen();
	}
	else if (docElm.webkitRequestFullScreen) {
		docElm.webkitRequestFullScreen();
	}
}

$(document).ready(function(){
	
	var fullpage = $("#fullpage").initialize({});
	
	//임시주석처리
	//$(document).on("click","#fullpage",myFullFunction);
	
	//로그인 인증 여부를 검사하여 초기에 렌더링
	getInfo().done(function(result){
		setLogin();
	}).fail(function(){
		console.log("액세스 토큰에 문제가 있어 재발급 요청");
		refreshTokens().done(function(){
			console.log("액세스, 리프레쉬토큰 재발급 성공");
			getInfo().done(function(result){
				var user_info = result.user_info;
				console.log("정상적인 액세스 토큰으로 요청 성공");
				setLogin();
			})
		}).fail(function(xhr,status,error){
			setLogout();
		})
	})
	
	//로그아웃 이벤트 처리
	$(document).on("click","#logout_button",function(){
		$.ajax({
			"url":"/restapi/tokens",
			"type":"delete"
		}).done(function(){
			setLogout();
		}).fail(function(xhr,status,error){
			alert(xhr.responseJSON.content);
		})
	})
	
	//문제가 있는 입력은 클릭시 자동으로 초기화 되게 설정
	$(document).on("click",".input",function(e){
		if($(this).hasClass("wrong")){
			$(this).removeClass("wrong");
			$(this).val("");
			$("#error_message").text("");
		}
	})
	
	//로그인 이벤트 처리
    $(document).on("click","#login_button",function(e){
    	if(!check($("#user_id").val())){
    		$("#user_id").addClass("wrong");
    		$("#error_message").text("아이디는 6 - 16자리이하의 알파벳, 숫자로 구성되어야 합니다.");
    		return;
    	}else if(!check($("#user_pw").val())){
    		$("#user_pw").addClass("wrong");
    		$("#error_message").text("비밀번호는 6 - 16자리이하의 알파벳, 숫자로 구성되어야 합니다.");
    		return;
    	}
    	getPublickey()
    	.done(function(result){
    		var publickey = result.publickey;
    		login(publickey)
    		.done(function(result){
    			setLogin();
		    	$("#loginForm").remove();
		    	$("#form_cover").css({
		    		"display":"none"
		    	})
		    	formFlag=false;
    		})
    		.fail(function(xhr, status, error){
        		$("#user_id").addClass("wrong");
        		$("#user_pw").addClass("wrong");
        		$("#error_message").text(xhr.responseJSON.content);
    		})
    	})
    	.fail(function(xhr, status, error){
    		$("#user_id").addClass("wrong");
    		$("#user_pw").addClass("wrong");
    		$("#error_message").text(xhr.responseJSON.content);
    	})
    });
	
	//회원가입 이벤트 처리
    $(document).on("click","#join_button",function(e){
    	if(!check($("#user_id").val())){
    		$("#user_id").addClass("wrong");
    		$("#error_message").text("아이디는 6 - 16자리이하의 알파벳, 숫자로 구성되어야 합니다.");
    		return;
    	}else if(!check($("#user_pw").val())){
    		$("#user_pw").addClass("wrong");
    		$("#error_message").text("비밀번호는 6 - 16자리이하의 알파벳, 숫자로 구성되어야 합니다.");
    		return;
    	}else if($("#user_pw").val()!= $("#user_pw_check").val()){
    		$("#user_pw").addClass("wrong");
    		$("#user_pw_check").addClass("wrong");
    		$("#error_message").text("비밀번호가 서로 일치하지 않습니다.");
    		return;
    	}else if(!checkBytes($("#user_name").val(),60)){
    		$("#user_name").addClass("wrong");
    		$("#error_message").text("이름은 영어로 60, 한글로 20자 이하여야 합니다.");
    		return;
    	}else if($("#user_name").val().length==0){
    		$("#user_name").addClass("wrong");
    		$("#error_message").text("이름은 공백일 수 없습니다.");
    		return;
    	}else if(!checkEmail($("#user_email").val())){
    		$("#user_email").addClass("wrong");
    		$("#error_message").text("이메일 형식이 올바르지 않습니다.");
    		return;
    	}else if($("#question_answer").length==0){
    		$("#question_answer").addClass("wrong");
    		$("#error_message").text("비밀번호 찾기 질문의 답은 반드시 입력해야 합니다.");
    		return;
    	}
    getPublickey()
    	.done(function(result){
    		var publickey = result.publickey;
    		join(publickey)
    		.done(function(result){
		    	$("#joinForm").remove();
		    	$("#form_cover").css({
		    		"display":"none"
		    	})
		    	formFlag=false;
    		})
    		.fail(function(xhr, status, error){
        		$("#error_message").text(xhr.responseJSON.content);
    		})
    	})
    	.fail(function(xhr, status, error){
    		$("#error_message").text(xhr.responseJSON.content);
    	})
    });
	
    $(document).on("click","#loginForm_button",function(e){
		$("#form_cover").css({
			"display":"block"
		})
		var loginForm = $("<form id='loginForm'></form>");
		var innerBox = $("<div id='loginFormInnerBox'></div>");
		var loginFormPanel = $("<div id='loginFormPanel'></div>");
		
		innerBox.append("<input id='user_id' class='input' name='user_id' type='text' autocomplete='off' placeholder='아이디'>");
		innerBox.append("<input id='user_pw' class='input' name='user_pw' type='password' autocomplete='off' placeholder='비밀번호'>");
		innerBox.append("<p id='error_message'></p>");
		
		loginFormPanel.append("<input id='login_button' type='button' value='로그인'>");
		loginFormPanel.append("<input id='loginFormClose_button' type='button' value='닫기' onclick='closeForm(this)'>");
		
		loginForm.append(innerBox);
		loginForm.append(loginFormPanel);
		
		openForm(loginForm);
    });
    
    //회원가입창 렌더링
    $(document).on("click","#joinForm_button",function(e){
		$("#form_cover").css({
			"display":"block"
		})
		
		var joinForm = $("<form id='joinForm'></form>");
		var innerBox = $("<div id='joinFormInnerBox'></div>");
		var joinFormPanel = $("<div id='joinFormPanel'></div>");
		
		innerBox.append("<input id='user_id' class='input' name='user_id' type='text' autocomplete='off' placeholder='아이디'>");
		innerBox.append("<input id='user_pw' class='input' name='user_pw' type='password' autocomplete='off' placeholder='비밀번호'>");
		innerBox.append("<input id='user_pw_check'class='input' name='user_pw_check' type='password' autocomplete='off' placeholder='비밀번호 확인'>");
		innerBox.append("<input id='user_name' class='input' name='user_name' type='text' autocomplete='off' placeholder='이름'>");
		innerBox.append("<input id='user_email' class='input' name='user_email' type='text' autocomplete='off' placeholder='이메일'>");
		innerBox.append("<select id='question_id' class='input' name='question_id'></select>");
		innerBox.append("<input id='question_answer' class='input' name='question_answer' type='text' autocomplete='off' placeholder='비밀번호 찾기 질문의 정답'>");
		innerBox.append("<p id='error_message'></p>");
		
		joinFormPanel.append("<input id='join_button' type='button' value='회원가입'>");
		joinFormPanel.append("<input id='joinFormClose_button' type='button' value='닫기' onclick='closeForm(this)'>");
		
		joinForm.append(innerBox);
		joinForm.append(joinFormPanel);
		
		//서버로부터 질문 목록을 발급받음
		getQuestions()
		.done(function(result){
			var i, list = result.list;
			for(i=0;i<list.length;i++){
				$("#question_id").append("<option value='"+list[i].question_id+"' label='"+list[i].question_content+"'></option>")
			}
		})
		.fail(function(xhr, status, error){
			alert(xhr.responseJSON.content);
		})
		openForm(joinForm);
    });
})