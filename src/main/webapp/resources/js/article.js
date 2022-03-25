$(document).ready(function(){
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
})