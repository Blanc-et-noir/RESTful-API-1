var section=1, page=1;
var file_map, index=0;

var send_article_flag = false;
var modify_article_flag = false;

var file_maps;
var indexs;
var removed_file_ids;

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
			var i;
			
			file_maps = new Array(articles.length);
			removed_file_ids = new Array(articles.length);
			
			for(i=0;i<file_maps.length; i++){
				file_maps[i] = new Map();
				removed_file_ids[i] = new Map();
			}
			
			indexs = new Array(articles.length);
			
			renderArticles(articles);
			
			pagingArticles(articles_total, section, page);
		}
	});
}

function getArticle(article_header){

	var article_wrapper = $(article_header).parent();
	var article_body = $(article_wrapper).find(".article_body");
	var article_images = $(article_wrapper).find(".article_images");
	var article_comments = $(article_wrapper).find(".article_comments");
	
	if($(article_body).find("*").length!=0){
		return;
	}

	return $.ajax({
		"url":"/restapi/articles/"+$(article_wrapper).attr("value"),
		"type":"get",
		"dataType":"json",
		"success":function(result){
			var article = result.article;
			renderArticle(article_wrapper, article);
		}
	})
}

function renderArticles(articles){
	$("#article_list").empty();
	var i;
	
	$("#article_list").append("<div id='article_header' class='touchable'><div class='touchable'>제목</div><div class='touchable'>작성일</div><div class='touchable'>작성자</div><div class='touchable'>조회</div></div>");
	
	for(i=0; i<articles.length; i++){
		var article_wrapper = $("<div class='article_wrapper touchable' value='"+articles[i].article_id+"'></div>");
		var article_header = $("<div class='article_header touchable' onclick='getArticle(this)'></div>");
		
		$(article_header).append("<input readonly class='article_title touchable' value='"+articles[i].article_title+"'><div class='touchable'>"+articles[i].article_date+"</div><div class='touchable'>"+articles[i].user_name+"(<span class='touchable' style='color:gray;'>"+" "+articles[i].user_id+" "+"</span>)</div><div class='touchable'>"+articles[i].article_view+"</div>");
		
		var article_body = $("<div class='article_body touchable'></div>");
		var article_images = $("<div class='article_images touchable'></div>");
		var article_comments = $("<div class='article_comments touchable'></div>");
		
		$(article_wrapper).append(article_header);
		$(article_wrapper).append(article_body);
		$(article_wrapper).append(article_images);
		
		(function(i){
			var article_edit_panel = $("<div class='article_edit_panel touchable'><div class='edit_article_button' onclick='editArticle(this)'>수정하기</div><label class='add_article_image_label'><input class='add_article_image_input' type='file' multiple onchange='changeImages(this,"+i+")'></label><div class='delete_article_button' onclick='deleteArticle(this)'>삭제하기</div><div class='confirm_article_button' onclick='confirmArticle(this,"+i+")'>수정완료하기</div><div class='cancel_article_button' onclick='cancelArticle(this)'>취소하기</div></div>");
			$(article_wrapper).append(article_edit_panel);
		}(i))
		
		$(article_wrapper).append(article_comments);
		
		$("#article_list").append(article_wrapper);
	}
	
	$("#article_list").append("<div class='pagebar touchable'></div>");
}



function renderArticle(article_wrapper, article){
	$(article_wrapper).find(".article_body").append("<textarea readonly class='article_content touchable'>"+article.article_content+"</textarea>");
	
	var i;
	var fidx = $(article_wrapper).index()-1;
	
	for(i=0; i<article.article_images.length; i++){
		$(article_wrapper).find(".article_images").append("<div class='article_image touchable' value='"+i+"'><div value='"+article.article_images[i].article_image_id+"' class='existing article_image_delete_button touchable' onclick='removeImageFromFilemaps(this,"+fidx+","+i+")'>✖</div><img class='touchable' src='/restapi/articles/"+article.article_id+"/images/"+article.article_images[i].article_image_id+"?article_image_extension="+article.article_images[i].article_image_extension+"'></div>");
		indexs[fidx] = indexs[fidx]+1;
	}
	
	
	$(article_wrapper).find(".article_edit_panel").css({"display":"flex"});

	$(".article_content").keydown();
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

function removeImageFromFilemap(idx){
	file_map.delete(idx);
	$("#article_images .article_image[value='"+idx+"']").remove();
}

function removeImageFromFilemaps(article_image_delete_button,fidx,idx){
	
	if($(".article_wrapper").eq(fidx).find(".confirm_article_button").css("display")=="none"){
		return;
	}
	
	if($(article_image_delete_button).hasClass("existing")){
		removed_file_ids[fidx].set(idx,$(article_image_delete_button).attr("value"));
	}else{
		file_maps[fidx].delete(idx);
	}
	
	$(".article_images").eq(fidx).find(".article_image[value='"+idx+"']").remove();
}

function removeImage(idx){
	file_map.delete(idx);
	$("#article_images .article_image[value='"+idx+"']").remove();
}

function addImages(e){
	var i;
	var myfiles = e.target.files;
	
	for(i=0; i<myfiles.length; i++){
		(function(i){
			var reader = new FileReader();
			reader.readAsDataURL(myfiles[i]);
			reader.onload = function(event){
				$("#article_images").append("<div class='article_image touchable' value='"+index+"'><div class='article_image_delete_button touchable' onclick='removeImageFromFilemap("+index+")'>✖</div><img class='touchable' src='"+event.target.result+"'></div>");
				file_map.set(index,myfiles[i]);
				index=index+1;
			}
		}(i))
	}	
}

function editArticle(edit_article_button){
	var article_edit_panel = $(edit_article_button).parent();
	var delete_article_button = $(article_edit_panel).find(".delete_article_button");
	var confirm_article_button = $(article_edit_panel).find(".confirm_article_button");
	var cancel_article_button = $(article_edit_panel).find(".cancel_article_button");
	var add_article_image_label = $(article_edit_panel).find(".add_article_image_label");
	var article_wrapper = $(article_edit_panel).parent();
	
	$(edit_article_button).css({"display":"none"});
	$(delete_article_button).css({"display":"none"});
	$(confirm_article_button).css({"display":"block"});
	$(cancel_article_button).css({"display":"block"});
	$(add_article_image_label).css({"display":"block"});
	
	$(article_wrapper).find(".article_title").prop("readonly",false);
	$(article_wrapper).find(".article_content").prop("readonly",false);
}

function cancelArticle(cancel_article_button){
	var article_edit_panel = $(cancel_article_button).parent();
	var edit_article_button = $(article_edit_panel).find(".edit_article_button");
	var confirm_article_button = $(article_edit_panel).find(".confirm_article_button");
	var delete_article_button = $(article_edit_panel).find(".delete_article_button");
	var add_article_image_label = $(article_edit_panel).find(".add_article_image_label");
	var article_wrapper = $(article_edit_panel).parent();
	
	$(edit_article_button).css({"display":"block"});
	$(delete_article_button).css({"display":"block"});
	$(confirm_article_button).css({"display":"none"});
	$(cancel_article_button).css({"display":"none"});
	$(add_article_image_label).css({"display":"none"});
	
	$(article_wrapper).find(".article_title").prop("readonly",true);
	$(article_wrapper).find(".article_content").prop("readonly",true);
}

function confirmArticle(confirm_article_button, fidx){
	if(modify_article_flag){
		return;
	}
	
	console.log(confirm_article_button);
	
	modify_article_flag = true;
	
	var formData = new FormData();
	
	
	for([key, value] of removed_file_ids[fidx]){
		formData.append("removed_file_ids",value);
		console.log(key+" : "+value);
	}
	
	console.log("==========");
	
	for([key, value] of file_maps[fidx]){
		console.log(key+" : "+value);
		formData.append("article_image_"+key,file_maps[fidx].get(key));
	}

	return $.ajax({
		"url":"/restapi/articles/"+$(confirm_article_button).parent().parent().attr("value"),
		"type":"post",
		"processData":false,
		"contentType":false,
		"dataType":"json",
		"data":formData,
		"success":function(result){
			openAlert(result.content);
			
			var article_edit_panel = $(confirm_article_button).parent();
			var delete_article_button = $(article_edit_panel).find(".delete_article_button");
			var edit_article_button = $(article_edit_panel).find(".edit_article_button");
			var cancel_article_button = $(article_edit_panel).find(".cancel_article_button");
			var add_article_image_label = $(article_edit_panel).find(".add_article_image_label");
			var article_wrapper = $(article_edit_panel).parent();
			
			$(edit_article_button).css({"display":"block"});
			$(delete_article_button).css({"display":"block"});
			$(confirm_article_button).css({"display":"none"});
			$(cancel_article_button).css({"display":"none"});
			$(add_article_image_label).css({"display":"none"});
			
			file_maps[fidx] = new Map();
			indexs[fidx] = 0;
			removed_file_ids[fidx] = new Map();
		},
		"error":function(xhr, status, error){
			openAlert(xhr.responseJSON.content);
		},
		"complete":function(){
			modify_article_flag = false;
		}
	})
}

function changeImages(add_article_image_input, fidx){
	var i;
	var myfiles = add_article_image_input.files;
	
	for(i=0; i<myfiles.length; i++){
		(function(i){
			var reader = new FileReader();
			reader.readAsDataURL(myfiles[i]);
			reader.onload = function(event){
				$(".article_images").eq(fidx).append("<div class='article_image touchable' value='"+indexes[fidx]+"'><div class='article_image_delete_button touchable' onclick='removeImageFromFilemaps(this,"+fidx+","+indexes[fidx]+")'>✖</div><img class='touchable' src='"+event.target.result+"'></div>");
				file_map.set(index,myfiles[i]);
				indexes[fidx]=index[fidx]+1;
			}
		}(i))
	}	
}

function addArticle(){
	
	send_article_flag = true;
	
	var key;
	var formData = new FormData();

	formData.append("article_title",$("#article_title").val());
	formData.append("article_content",$("#article_content").val());
	
	for([key, value] of file_map){
		formData.append("article_image_"+key,file_map.get(key));
	}
	
	return $.ajax({
		"url":"/restapi/articles",
		"type":"post",
		"processData":false,
		"contentType":false,
		"dataType":"json",
		"data":formData,
		"success":function(result){
			file_map = new Map();
			index = 0;
			$("#article_title").val("");
			$("#article_content").val("");
			$(".article_image").remove();
			openAlert(result.content);
			
		},
		"error":function(xhr, status, error){
			openAlert(xhr.responseJSON.content);
		},
		"complete":function(){
			send_article_flag = false;
		}
	})
}

function resizeArticleContent(article_content){
	$(article_content).css({
		"height" : (1)+"px"
	})
	$(article_content).css({
		"height" : (12+article_content.scrollHeight)+"px"
	})
}

$(document).ready(function(){
	file_map = new Map();
	
	getArticles(section,page);
	
	$("#fullpage").on("change","#article_images_input",function(e){
		addImages(e);
	});
	
	$("#fullpage").on("keydown","#article_content",function(e){
		resizeArticleContent(this);
	});
	
	$("#fullpage").on("keydown",".article_content",function(e){
		resizeArticleContent(this);
	});
	
	$("#fullpage").on("click","#send_article_button",function(e){
		if(send_article_flag){
			return;
		}
		
		var article_title = $("#article_title").val();
		var article_content = $("#article_content").val();
		
		addArticle();
	});
})