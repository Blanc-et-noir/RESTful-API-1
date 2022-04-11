function listArticles(){
	return $.ajax({
		"url":"/restapi/articles?article_search="+$("#article_search").val()+"&search_flag="+$("#search_flag").val(),
		"type":"get"
	});
}

function addArticle(){
	return $.ajax({
		"url":"/restapi/articles",
		"type":"post",
		"data":{
			"article_title":$("#article_title").val(),
			"article_content":$("#article_content").val()
		}
	});
}

$(document).ready(function(){

	$(document).on("click","#article_search_button",function(){
		listArticles()
		.done(function(result){
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
		})
		.fail(function(xhr, status, error){
			alert(xhr.responseJSON.content);
		})
	})

	$(document).on("click","#article_write_button",function(){
		addArticle()
		.done(function(result){
			$("#article_title").val("");
			$("#article_content").val("");
			alert(result.content);
		})
		.fail(function(xhr, status, error){
			if(xhr.status == 401){
				refreshTokens()
				.done(function(result){
					addArticle()
					.done(function(result){
						$("#article_title").val("");
						$("#article_content").val("");
						alert(result.content);
					})
					.fail(function(xhr, status, error){
						alert(xhr.responseJSON.content);
					})
				})
				.fail(function(xhr, status, error){
					alert(xhr.responseJSON.content);
				})
			}else{
				alert(xhr.responseJSON.content);
			}
		})
	})
})