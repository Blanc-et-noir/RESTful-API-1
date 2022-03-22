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
		$("#user_info").empty();
		$("#user_info").append("<div id='user_id'>ID : "+user_info.user_id+"</div>");
		$("#user_info").append("<div id='user_accesstoken_exp'>EXP : "+user_info.user_accesstoken_exp+"</div>");
	}).fail(function(){
		console.log("액세스 토큰에 문제가 있어 재발급 요청");
		refreshTokens().done(function(){
			console.log("액세스, 리프레쉬토큰 재발급 성공");
			getInfo().done(function(result){
				var user_info = result.user_info;
				console.log("정상적인 액세스 토큰으로 요청 성공");
				$("#user_info").empty();
				$("#user_info").append("<div id='user_id'>ID : "+user_info.user_id+"</div>");
				$("#user_info").append("<div id='user_accesstoken_exp'>EXP : "+user_info.user_accesstoken_exp+"</div>");
			})
		}).fail(function(){
			console.log("액세스, 리프레쉬토큰 재발급 실패");
		})
	})
	
	
	
	
	
	$(document).on("click","#logout_button",function(){
		$.ajax({
			"url":"/restapi/token/logout.do",
			"type":"post"
		}).done(function(){
	    	var form = $("<form action='/restapi/user/loginForm.do'></form>");
	    	$("body").append(form);
	    	form.submit();
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
				console.log(list);
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
})