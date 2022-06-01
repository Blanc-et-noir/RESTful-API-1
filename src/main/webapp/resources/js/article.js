var section=1, page=1;
var file_array, index=0;

function setAddArticleForm(){
	$("#add_article_form").css({
		"display":"flex"
	});
	$("#list_article_form").css({
		"display":"none"
	});
}

function setListArticleForm(){
	$("#add_article_form").css({
		"display":"none"
	});
	$("#list_article_form").css({
		"display":"flex"
	});
}

function getArticles(section, page){
	return $.ajax({
		"url":"/restapi/articles?search_flag="+$("#search_flag").val()+"&search_content="+$("#search_content").val()+"&section="+section+"&page="+page,
		"type":"get",
		"dataType":"json",
		"success":function(result){
			var articles = result.articles;
			var articles_total = result.articles_total;
			
			renderArticles(articles);
			
			pagingArticles(articles_total, section, page);
		}
	});
}

function getArticle(article_id){
	if($("div.article_wrapper[value='"+article_id+"']").length!=0){
		return
	}

	return $.ajax({
		"url":"/restapi/articles/"+article_id,
		"type":"get",
		"dataType":"json",
		"success":function(result){
			var article = result.article;
			renderArticle(article);
		}
	})
}

function renderArticles(articles){
	$("#article_list").empty();
	var i;
	
	$("#article_list").append("<div class='article_header touchable'><div class='touchable'>제목</div><div class='touchable'>작성일</div><div class='touchable'>작성자</div><div class='touchable'>조회</div></div>");
	
	for(i=0; i<articles.length; i++){
		$("#article_list").append("<div class='article_row touchable' onclick='getArticle("+"\""+articles[i].article_id+"\""+")' value='"+articles[i].article_id+"'><div class='touchable'>"+articles[i].article_title+"</div><div class='touchable'>"+articles[i].article_date+"</div><div class='touchable'>"+articles[i].user_name+"(<span class='touchable' style='color:gray;'>"+" "+articles[i].user_id+" "+"</span>)</div><div class='touchable'>"+articles[i].article_view+"</div></div>");
	}
	
	$("#article_list").append("<div class='pagebar touchable'></div>")
}



function renderArticle(article){
	$("div.article_row[value='"+article.article_id+"']").after("<div class='article_wrapper touchable' value='"+article.article_id+"'><textarea class='article_content touchable' readonly>"+article.article_content+"</textarea></div>");
}

function pagingArticles(total,section,page){
	var max_section = Math.ceil(total/25);
	var max_page = Math.ceil((total-(section-1)*25)/5)>=5?5:Math.ceil((total-(section-1)*25)/5);
	var pagebar = $("#article_list").find(".pagebar");
	
	$(pagebar).off();
	$(pagebar).empty();
	
	var tag;
		
	if(section>=2){
		tag = $("<a class='prev touchable'>이전</a>");
		$(pagebar).on("click",".prev",function(){
			$(pagebar).find("a").removeClass("viewd");
			$(pagebar).find(".page"+i).addClass("viewd");
			section = section - 1;
			page = 1;
			getArticles(section,1);
		})
		$(pagebar).append(tag);
	}
		
	for(var i=1; i<=max_page;i++){
		$(pagebar).append($("<a class='page"+i+" touchable' value='"+i+"'>"+i+"</a>"));
		$(pagebar).on("click",".page"+i,function(){
			getArticles(section,$(this).attr("value"));
			page = i;			
		})
	}
	
	if(section<max_section){
		tag = $("<a class='next touchable'>다음</a>");
		$(pagebar).on("click",".next",function(){
			$(pagebar).find("a").removeClass("viewd");
			$(pagebar).find(".page"+i).addClass("viewd");
			section = section + 1;
			page = 1;
			getArticles(section,1);
		})
		$(pagebar).append(tag);
	}
	$(pagebar).find("a").removeClass("viewd");
	$(pagebar).find(".page"+page).addClass("viewd");
}

function removeImages(idx){
	file_array.splice(idx,1);
	$(".article_image[value='"+idx+"']").remove();
}


function addImages(e){
	var i;
	var files = e.target.files;
	
	for(i=0; i<files.length; i++){
		var reader = new FileReader();
		reader.readAsDataURL(files[i]);
		
		reader.onload = function(event){
			$("#article_images").append("<div class='article_image touchable' value='"+index+"'><div class='article_image_delete_button touchable' onclick='removeImages("+index+")'>✖</div><img class='touchable' src='"+event.target.result+"'></div>");
			file_array.push(files[index]);
			index=index+1;
		}
	}
}

function addArticle(article_title, article_content){
	var i;
	var formData = new FormData();
	
	formData.append("article_image_count",file_array.length);
	
	for(i=0; i<file_array.length; i++){
		formData.append("article_image_"+i,file_array[i]);
	}
	
	return $.ajax({
		"url":"/restapi/articles",
		"type":"post",
		"processData":false,
		"contentType":false,
		"dataType":"json",
		"data":formData,
		"success":function(result){
			file_array = new Array();
			index = 0;
			openAlert(result.content);
		},
		"error":function(xhr, status, error){
			openAlert(xhr.responseJSON.content);
		}
	})
}

$(document).ready(function(){
	file_array = new Array();
	
	getArticles(section,page);
	
	$("#fullpage").on("change","#article_images_input",function(e){
		addImages(e);
	});
	
	$("#fullpage").on("click","#send_article_button",function(e){
		var article_title = $("#article_title").val();
		var article_content = $("#article_content").val();
		addArticle(article_title, article_content);
	});
})