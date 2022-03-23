var formFlag = false;

function closeForm(target){
	$(target).parent().remove();
	$("#form_cover").css({
		"display":"none"
	})
	formFlag=false;
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

$(document).ready(function(){
	$("#fullpage").initialize({});
	
	function errorMessage(xhr, status, error){
		JSON.parse(xhr.responseText).content
	}
	
	function getInfo(){
		return $.ajax({
			"url":"/restapi/user/info.do",
			"type":"post",
			"success":function(result){
				
			}
		})
	}
	
	function refreshTokens(){
		return $.ajax({
			"url":"/restapi/token/refreshTokens.do",
			"type":"post"
		})
	}
	
	
	getInfo().done(function(result){
		var user_info = result.user_info;
		console.log("정상적인 액세스 토큰으로 요청 성공");
		$("#loginForm_button").remove();
		$("#joinForm_button").remove();
		$("body").append("<div id='logout_button'>로그아웃</div>");
	}).fail(function(){
		console.log("액세스 토큰에 문제가 있어 재발급 요청");
		refreshTokens().done(function(){
			console.log("액세스, 리프레쉬토큰 재발급 성공");
			getInfo().done(function(result){
				var user_info = result.user_info;
				console.log("정상적인 액세스 토큰으로 요청 성공");
				$("#loginForm_button").remove();
				$("#joinForm_button").remove();
				$("body").append("<div id='logout_button'>로그아웃</div>");
			})
		}).fail(function(){
			console.log("액세스, 리프레쉬토큰 재발급 실패");
			$("#logout_button").remove();
			$("body").append("<div id='loginForm_button'>로그인</div>");
			$("body").append("<div id='joinForm_button'>회원가입</div>");
		})
	})
	
	
	
	
	
	$(document).on("click","#logout_button",function(){
		$.ajax({
			"url":"/restapi/token/logout.do",
			"type":"post"
		}).done(function(){
			$("#logout_button").remove();
			$("body").append("<div id='loginForm_button'>로그인</div>");
			$("body").append("<div id='joinForm_button'>회원가입</div>");
		}).fail(function(){
			alert("로그아웃에 실패했습니다.");
		})
	})
	
	function addArticle(){
		return $.ajax({
			"url":"/restapi/article/addArticle.do",
			"type":"post",
			"data":{
				"article_title":$("#article_title").val(),
				"article_content":$("#article_content").val()
			},
			"success":function(){
				$("#article_title").val("");
				$("#article_content").val("");
			}
					
		});
	}
	
	$(document).on("click","#article_write_button",function(){
		addArticle()
		.done(function(result){
			alert(result.content);
		})
		.fail(function(){
			refreshTokens()
			.done(function(){
				addArticle()
				.done(function(result){
					alert(result.content);
				})
				.fail(errorMessage)
			})
			.fail(function(xhr, status, error){
				console.log("액세스, 리프레쉬토큰 재발급 실패");
			})
		})
	})
	
	function listArticles(){
		return $.ajax({
			"url":"/restapi/article/listArticles.do",
			"type":"post",
			"data":{
				"article_search":$("#article_search").val(),
				"search_flag":$("#search_flag").val()
			},
			"success":function(result){
				var i, list = result.list;
				$("#article_list").empty();
				
				for(i=0; i<list.length; i++){
					var article = $("<div class='article touchable'></div>");
					article.append("<div class='user_id touchable'>"+list[i].user_id+"</div>");
					article.append("<div class='article_title touchable'>"+list[i].article_title+"</div>");
					var date = list[i].article_date.split(" ");
					article.append("<div class='article_date touchable'>"+date[0]+"<br>"+date[1]+"</div>");
					$("#article_list").append(article);
				}
			}
		});
	}
	
	$(document).on("click","#article_search_button",function(){
		listArticles()
		.done(function(){
			
		})
		.fail(function(){
			
		})
	})
	
	
	
	
	
	
	
    $(document).on("click","#login_button",function(e){
    	var publickey;
    	$.ajax({
    		"url":"/restapi/user/getPublickey.do",
    		"type":"POST",
    		"success":function(result, textStatus, request){
				publickey = result.publickey;
		    	$.ajax({
		    		"url":"/restapi/user/login.do",
		    		"data":{
		    			"user_id":$("#user_id").val(),
		    			"user_pw":encryptByRSA2048($("#user_pw").val(),publickey),
		    			"publickey":publickey
		    		},
		    		"type":"POST",
		    		"success":function(result, textStatus, request){
		    			if(result.flag=="true"){
		    				alert("로그인성공");
		    				$("#loginForm_button").remove();
		    				$("#joinForm_button").remove();
		    				$("body").append("<div id='logout_button'>로그아웃</div>");
		    				
		    				$("#loginForm").remove();
		    				$("#form_cover").css({
		    					"display":"none"
		    				})
		    				formFlag=false;
		    				
		    			}else{
		    				alert("로그인 실패");
		    				$("#logout_button").remove();
		    				$("body").append("<div id='loginForm_button'>로그인</div>");
		    				$("body").append("<div id='joinForm_button'>회원가입</div>");
		    			}
		    		},
		    		"error": function(xhr, status, error) {
		    			alert(JSON.parse(xhr.responseText).content);
		    			$("#logout_button").remove();
		    			$("body").append("<div id='loginForm_button'>로그인</div>");
		    			$("body").append("<div id='joinForm_button'>회원가입</div>");
		      		}
		    	});
    		},
    		"error": function(xhr, status, error) {
    			alert(JSON.parse(xhr.responseText).content);
    			$("#logout_button").remove();
    			$("body").append("<div id='loginForm_button'>로그인</div>");
    			$("body").append("<div id='joinForm_button'>회원가입</div>");
      		}
    	})
    });
	
    $(document).on("click","#loginForm_button",function(e){
		$("#form_cover").css({
			"display":"block"
		})
		//
		var loginForm = $("<form id='loginForm'></form>");
		loginForm.append("<input id='user_id' name='user_id' type='text' autocomplete='off' placeholder='아이디'>");
		loginForm.append("<input id='user_pw' name='user_pw' type='password' autocomplete='off' placeholder='비밀번호'>");
		loginForm.append("<input id='login_button' type='button' value='로그인'>");
		loginForm.append("<input id='loginFormClose_button' type='button' value='닫기' onclick='closeForm(this)'>");
		openForm(loginForm);
    });
    
    $(document).on("click","#joinForm_button",function(e){
		$("#form_cover").css({
			"display":"block"
		})
		//
		var joinForm = $("<form id='joinForm'></form>");
		joinForm.append("<input id='user_id' name='user_id' type='text' autocomplete='off' placeholder='아이디'>");
		joinForm.append("<input id='user_pw' name='user_pw' type='password' autocomplete='off' placeholder='비밀번호'>");
		joinForm.append("<input id='user_pw_check' name='user_pw_check' type='password' autocomplete='off' placeholder='비밀번호 확인'>");
		joinForm.append("<input id='user_name' name='user_name' type='text' autocomplete='off' placeholder='이름'>");
		joinForm.append("<input id='user_email' name='user_email' type='text' autocomplete='off' placeholder='이메일'>");
		joinForm.append("<select id='question_id' name='question_id'></select>");
		joinForm.append("<input id='question_answer' name='question_answer' type='text' autocomplete='off' placeholder='비밀번호 찾기 질문의 정답'>");
		joinForm.append("<input id='join_button' type='button' value='회원가입'>");
		joinForm.append("<input id='joinFormClose_button' type='button' value='닫기' onclick='closeForm(this)'>");
		
			
		$.ajax({
			"url":"/restapi/user/getQuestions.do",
			"type":"post",
			"success":function(result){
				var i, list = result.list;
				for(i=0;i<list.length;i++){
					$("#question_id").append("<option value='"+list[i].question_id+"' label='"+list[i].question_content+"'></option>")
				}
			}
						
		});
		openForm(joinForm);
    });
})