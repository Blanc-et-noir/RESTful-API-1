var section=1, page=1;

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

function renderArticles(articles){
	$("#article_list").empty();
	var i;
	
	$("#article_list").append("<div class='article_header touchable'><div class='touchable'>제목</div><div class='touchable'>작성일</div><div class='touchable'>작성자</div><div class='touchable'>조회</div></div>");
	
	for(i=0; i<articles.length; i++){
		$("#article_list").append("<div class='article_row touchable'><div class='touchable'>"+articles[i].article_title+"</div><div class='touchable'>"+articles[i].article_date+"</div><div class='touchable'>"+articles[i].user_name+"(<span class='touchable' style='color:gray;'>"+" "+articles[i].user_id+" "+"</span>)</div><div class='touchable'>"+articles[i].article_view+"</div></div>");
	}
	
	$("#article_list").append("<div class='pagebar touchable'></div>")
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

$(document).ready(function(){
	getArticles(section,page);
})