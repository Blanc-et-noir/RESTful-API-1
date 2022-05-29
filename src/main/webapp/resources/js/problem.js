function paging(problem_id,total,section,page){
	var opinions =  $("#problem_"+problem_id).find(".opinions");
	var max_section = Math.ceil(total/25);
	var max_page = Math.ceil((total-(section-1)*25)/5)>=5?5:Math.ceil((total-(section-1)*25)/5);
	var pagebar = $("#problem_"+problem_id).find(".pagebar");
	
	$(pagebar).off();
	$(pagebar).empty();
	
	var tag;
		
	if(section>=2){
		tag = $("<a class='prev touchable'>이전</a>");
		$(pagebar).on("click",".prev",function(){
			$(opinions).attr("section",section-1);
			$(opinions).attr("page",1);
			$(opinions).parent().find("a").removeClass("viewd");
			$(opinions).parent().find(".page"+i).addClass("viewd");

			readOpinions(problem_id)
			.done(function(result){
				list = result.list;
				total = result.total;
				
				$(opinions).empty();
				renderOpinions(opinions,list);
				paging(problem_id,total,section-1, 1);
			})
			.fail(function(xhr, status, error){
				alert(xhr.responseJSON.content);
			})
		})
		$(pagebar).append(tag);
	}
		
	for(var i=1; i<=max_page;i++){
		$(pagebar).append($("<a class='page"+i+" touchable' value='"+i+"'>"+i+"</a>"));
		$(pagebar).on("click",".page"+i,function(){
			$(opinions).attr("page",$(this).attr("value"));
			
			readOpinions(problem_id)
			.done(function(result){
				list = result.list;
				total = result.total;
				
				$(opinions).empty();
				renderOpinions(opinions,list);
				paging(problem_id,total,section, page);
			})
			.fail(function(xhr, status, error){
				alert(xhr.responseJSON.content);
			})	
		})
	}
	
	if(section<max_section){
		tag = $("<a class='next touchable'>다음</a>");
		$(pagebar).on("click",".next",function(){
			$(opinions).attr("section",section+1);
			$(opinions).attr("page",1);
			$(opinions).parent().find("a").removeClass("viewd");
			$(opinions).parent().find(".page"+i).addClass("viewd");

			readOpinions(problem_id)
			.done(function(result){
				list = result.list;
				total = result.total;
				
				$(opinions).empty();
				renderOpinions(opinions,list);
				paging(problem_id,total,section+1, 1);
			})
			.fail(function(xhr, status, error){
				alert(xhr.responseJSON.content);
			})
		})
		$(pagebar).append(tag);
	}
	
	$(opinions).parent().find("a").removeClass("viewd");
	$(opinions).parent().find(".page"+$(opinions).attr("page")).addClass("viewd");
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

function setOpinionEditable(target){
	$(target).parent().parent().find(".opinion_content").prop('readonly', false);
	$(target).css({"display":"none"});
	$(target).siblings(".opinion_edit_complete_button").css({"display":"block"});
	$(target).siblings(".opinion_edit_cancel_button").css({"display":"block"});
}

function setOpinionNotEditable(target){
	$(target).parent().parent().find(".opinion_content").prop('readonly', true);
	$(target).siblings(".opinion_edit_button").css({"display":"block"});
	$(target).css({"display":"none"});
	$(target).siblings(".opinion_edit_cancel_button").css({"display":"none"});
}

function updateCancelOpinion(target){
	$(target).parent().parent().find(".opinion_content").prop('readonly', true);
	$(target).siblings(".opinion_edit_button").css({"display":"block"});
	$(target).siblings(".opinion_edit_complete_button").css({"display":"none"});
	$(target).css({"display":"none"});
}

function getProblems(){
	return $.ajax({
		"url":"/restapi/problems?category_id="+$("#category_id").val(),
		"type":"get",
		"dataType":"json"
	});
}

function sendSolution(list){
	var obj = new Object();
	obj.list = list;
	return $.ajax({
		"url":"/restapi/users/records",
		"type":"post",
		"contentType": "application/json;",
		"data":JSON.stringify(obj)
	});
}

function readOpinions(problem_id){
	var opinions =  $("#problem_"+problem_id).find(".opinions");
	var section =  $(opinions).attr("section");
	var page =  $(opinions).attr("page");
	return $.ajax({
		"url":"/restapi/problems/"+problem_id+"/opinions?section="+section+"&page="+page,
		"type":"get",
		"dataType":"json"
	});
}

function writeOpinion(target,problem_id){
	return $.ajax({
		"url":"/restapi/problems/"+problem_id+"/opinions",
		"type":"post",
		"dataType":"json",
		"data":{
			"opinion_content":$(target).parent().find(".opinion_input").val()
		}
	});
}

function deleteOpinion(target, problem_id,opinion_id){
	var opinions = $("#problem_"+problem_id).find(".opinions");
	var list, total;
	
	$.ajax({
		"url":"/restapi/problems/"+problem_id+"/opinions/"+opinion_id,
		"type":"delete",
		"dataType":"json"
	})
	.done(function(result){
		readOpinions(problem_id)
		.done(function(result){
			list = result.list;
			total = result.total;
			
			$(opinions).empty();
			$(opinions).attr("section",1);
			$(opinions).attr("page",1);
			
			renderOpinions(opinions,list);
			paging(problem_id,total,1, 1);
		})
		.fail(function(xhr, status, error){
			if(xhr.status==401){
				refreshTokens()
				.done(function(result){
					list = result.list;
					total = result.total;
					
					$(opinions).empty();
					$(opinions).attr("section",1);
					$(opinions).attr("page",1);

					renderOpinions(opinions,list);
					paging(problem_id,total,section, page);
				})
				.fail(function(xhr, status, error){
					alert(xhr.responseJSON.content);
				})
			}else{
				alert(xhr.responseJSON.content);
			}
		})
	})
	.fail(function(xhr, status, error){
		if(xhr.status==401){
			refreshTokens()
			.done(function(result){
				$.ajax({
					"url":"/restapi/problems/"+problem_id+"/opinions/"+opinion_id,
					"type":"delete",
					"dataType":"json"
				})
				.done(function(result){
					readOpinions(problem_id)
					.done(function(result){
						list = result.list;
						total = result.total;
						
						$(opinions).empty();
						$(opinions).attr("section",1);
						$(opinions).attr("page",1);
						
						renderOpinions(opinions,list);
						paging(problem_id,total,1, 1);
					})
					.fail(function(xhr, status, error){
						if(xhr.status==401){
							refreshTokens()
							.done(function(result){
								list = result.list;
								total = result.total;
								
								$(opinions).empty();
								$(opinions).attr("section",1);
								$(opinions).attr("page",1);
								
								renderOpinions(opinions,list);
								paging(problem_id,total,1, 1);
							})
							.fail(function(xhr, status, error){
								alert(xhr.responseJSON.content);
							})
						}else{
							alert(xhr.responseJSON.content);
						}
					})
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
	});
}

function updateOpinion(target, problem_id,opinion_id){
	var opinions = $("#problem_"+problem_id).find(".opinions");
	var list, total;
	var opinion_content = $(target).parent().parent().find(".opinion_content").val();
	
	$.ajax({
		"url":"/restapi/problems/"+problem_id+"/opinions/"+opinion_id,
		"type":"put",
		"dataType":"json",
		"data":{
			"opinion_content":opinion_content
		}
	})
	.done(function(result){
		readOpinions(problem_id)
		.done(function(result){
			list = result.list;
			total = result.total;
			
			$(opinions).empty();
			$(opinions).attr("section",1);
			$(opinions).attr("page",1);
			
			renderOpinions(opinions,list);
			paging(problem_id,total,1, 1);
		})
		.fail(function(xhr, status, error){
			if(xhr.status==401){
				refreshTokens()
				.done(function(result){
					list = result.list;
					total = result.total;
					
					$(opinions).empty();
					$(opinions).attr("section",1);
					$(opinions).attr("page",1);

					renderOpinions(opinions,list);
					paging(problem_id,total,section, page);
				})
				.fail(function(xhr, status, error){
					alert(xhr.responseJSON.content);
				})
			}else{
				alert(xhr.responseJSON.content);
			}
		})
	})
	.fail(function(xhr, status, error){
		if(xhr.status==401){
			refreshTokens()
			.done(function(result){
				$.ajax({
					"url":"/restapi/problems/"+problem_id+"/opinions/"+opinion_id,
					"type":"put",
					"dataType":"json",
					"data":{
						"opinion_content":opinion_content
					}
				})
				.done(function(result){
					readOpinions(problem_id)
					.done(function(result){
						list = result.list;
						total = result.total;
						
						$(opinions).empty();
						$(opinions).attr("section",1);
						$(opinions).attr("page",1);
						
						renderOpinions(opinions,list);
						paging(problem_id,total,1, 1);
					})
					.fail(function(xhr, status, error){
						if(xhr.status==401){
							refreshTokens()
							.done(function(result){
								list = result.list;
								total = result.total;
								
								$(opinions).empty();
								$(opinions).attr("section",1);
								$(opinions).attr("page",1);
								
								renderOpinions(opinions,list);
								paging(problem_id,total,1, 1);
							})
							.fail(function(xhr, status, error){
								alert(xhr.responseJSON.content);
							})
						}else{
							alert(xhr.responseJSON.content);
						}
					})
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
	});
}

function renderOpinions(opinions,list){
	if(list.length==0){
		$(opinions).append("<div class='touchable' style='color:gray; text-align:center; height:40px; line-height:40px;'>등록된 의견이 없는걸요...</div>");
		return;
	}
	for(i=0; i<list.length; i++){
		if(list[i].editable=='true'){
			$(opinions).append(
				"<div class='opinion touchable'>" +
					"<div class='touchable' style='flex-direction:row; justify-content:space-between; display:flex;'>" +
						"<div class='opinion_user touchable'>"+list[i].user_name+"(<span class='touchable' style='color:gray;'>"+" "+list[i].user_id+" "+"</span>)</div>" +
						"<div class='opinion_date touchable'>"+list[i].opinion_date+"</div>" +
					"</div>" +
					"<textarea readonly class='opinion_content touchable'>"+list[i].opinion_content+"</textarea>" +
					"<div class='opinion_edit_form touchable'>"+
						"<div class='opinion_edit_button touchable' onclick='setOpinionEditable(this,\""+list[i].problem_id+"\")'>수정</div>"+
						"<div class='opinion_edit_complete_button touchable' onclick='updateOpinion(this,\""+list[i].problem_id+"\",\""+list[i].opinion_id+"\")'>수정완료</div>"+
						"<div class='opinion_edit_cancel_button touchable' onclick='updateCancelOpinion(this,\""+list[i].problem_id+"\")'>취소</div>"+
						"<div class='opinion_delete_button touchable' onclick='deleteOpinion(this,\""+list[i].problem_id+"\",\""+list[i].opinion_id+"\")'>삭제</div>"+
					"</div>"+
				"</div>"
			);
		}else{
			$(opinions).append(
					"<div class='opinion touchable'>" +
					"<div class='touchable' style='flex-direction:row; justify-content:space-between; display:flex;'>" +
						"<div class='opinion_user touchable'>"+list[i].user_name+"(<span class='touchable' style='color:gray;'>"+" "+list[i].user_id+" "+"</span>)</div>" +
						"<div class='opinion_date touchable'>"+list[i].opinion_date+"</div>" +
					"</div>" +
					"<textarea readonly class='opinion_content touchable'>"+list[i].opinion_content+"</textarea>" +
				"</div>"
			)
		}
	}
}

function registerOpinion(target, problem_id){
	var opinion_content = $(target).parent().find(".opinion_input").val();
	if(!checkBytes(opinion_content,600)){
		alert("한글로 200자, 영어로 600자 이하만 작성 가능합니다.");
		return;
	}else if(opinion_content.length<10){
		alert("최소 10자이상 작성해야합니다.");
		return;
	}
	
	var problem = $("#problem_"+problem_id);
	var opinions = $(problem).find(".opinions");
	
	writeOpinion(target,problem_id)
	.done(function(result){
		$(target).parent().find(".opinion_input").val("");
		
		$(problem).find(".opinions").attr("section",1);
		$(problem).find(".opinions").attr("page",1);
		
		readOpinions(problem_id)
		.done(function(result){
			list = result.list;
			total = result.total;
			
			$(opinions).empty();
			
			renderOpinions(opinions,list);
			
			paging(problem_id,total,1, 1);
		})
		.fail(function(xhr, status, error){
			if(xhr.status==401){
				refreshTokens()
				.done(function(result){
					list = result.list;
					total = result.total;
					
					$(opinions).empty();
					
					renderOpinions(opinions,list);
					
					paging(problem_id,total,1, 1);
				})
				.fail(function(xhr, status, error){
					alert(xhr.responseJSON.content);
				})
			}else{
				alert(xhr.responseJSON.content);
			}
		})		
	})
	.fail(function(xhr, status, error){
		if(xhr.status==401){
			refreshTokens()
			.done(function(result){
				writeOpinion(target,problem_id)
				.done(function(result){
					$(target).parent().find(".opinion_input").val("");
					readOpinions(problem_id)
					.done(function(result){
						list = result.list;
						total = result.total;
						
						$(opinions).empty();
						
						renderOpinions(opinions,list);
						
						paging(problem_id,total,section, page);
					})
					.fail(function(xhr, status, error){
						if(xhr.status==401){
							refreshTokens()
							.done(function(result){
								list = result.list;
								total = result.total;
								
								$(opinions).empty();
								
								renderOpinions(opinions,list);
								
								paging(problem_id,total,section, page);
							})
							.fail(function(xhr, status, error){
								alert(xhr.responseJSON.content);
							})
						}else{
							alert(xhr.responseJSON.content);
						}
					})
					
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
}

function openOpinionsForm(target,problem_id){
	var problem = $(target).parent();
	$(problem).find(".problem_image").css({
		"display":"none"
	})
	$(problem).find(".choices").css({
		"display":"none"
	})
	$(problem).find(".open_opinion_button").css({
		"display":"none"
	})
	$(problem).find(".opinions_form").css({
		"display":"block"
	})
	$(problem).find(".close_opinion_button").css({
		"display":"block"
	})
	$(problem).find(".opinions").attr("section",1);
	$(problem).find(".opinions").attr("page",1);
	
	var opinions = $("#problem_"+problem_id).find(".opinions");
	var list, total;
	
	readOpinions(problem_id)
	.done(function(result){
		list = result.list;
		total = result.total;
		
		$(opinions).empty();
		
		renderOpinions(opinions,list);
		
		paging(problem_id,total,1, 1);
	})
	.fail(function(xhr, status, error){
		alert(xhr.responseJSON.content);
	})
}

function closeOpinionsForm(view){
	var problem = $(view).parent();
	$(problem).find(".problem_image").css({
		"display":"block"
	})
	$(problem).find(".choices").css({
		"display":"block"
	})
	$(problem).find(".open_opinion_button").css({
		"display":"block"
	})
	$(problem).find(".opinions_form").css({
		"display":"none"
	})
	$(problem).find(".close_opinion_button").css({
		"display":"none"
	})
}

function renderProblems(result){
	var i,j, problems = result.problems;
	$("#problems").remove();
	var problemDiv = $("<div id='problems' class='touchable'></div>");
	
	for(i=0;i<problems.length;i++){
		var choices = problems[i].choices;
		var problem = $("<div id='problem_"+problems[i].problem_id+"' class='problem touchable'></div>");

		$(problem).append("<div class='problem_content touchable'><span class='touchable' style='color:#ee3f5c; font-weight:900; font-size:12px;'>["+(i+1)+"]</span> "+problems[i].problem_content+", 정답률 : "+problems[i].answer_rate+"%</div>");
		
		if(problems[i].problem_image_name!=null){
			$(problem).append("<img class='problem_image touchable' src='/restapi/problems/"+problems[i].problem_id+"/images/"+problems[i].problem_image_name+"';>");
		}
		var choicesDiv = $("<div class='choices touchable'></div>");
		
		for(j=0;j<choices.length;j++){
			choicesDiv.append("<div class='choice touchable'><input id='choice_"+choices[j].choice_id+"' class='touchable' type='radio' name='"+problems[i].problem_id+"' value='"+choices[j].choice_id+"'><label for='choice_"+choices[j].choice_id+"' class='touchable'>("+(j+1)+") "+choices[j].choice_content+"</label></div>");
		}
		$(problem).append(choicesDiv);
		$(problem).append(
			"<div class='opinions_form touchable'>" +
				"<div class='opinions touchable' section='1' page='1'></div>" +
				"<div class='opinion_search_form touchable'>" +
					"<textarea class='opinion_input touchable' placeholder='이곳에 자신의 의견을 입력해주세요.'></textarea>" +
					"<div class='opinion_write_button touchable' onclick='registerOpinion(this,\""+problems[i].problem_id+"\")'>등록</div>" +
				"</div>" +
				"<div class='pagebar touchable'></div>" +
			"</div>"
		);

		$(problem).append("<div class='open_opinion_button touchable' onclick='openOpinionsForm(this,\""+problems[i].problem_id+"\")'>다른 사람들의 의견이 궁금하신가요?</div>");
		$(problem).append("<div class='close_opinion_button touchable' onclick='closeOpinionsForm(this)'>문제를 다시 보여주세요!</div>");
		
		$(problemDiv).append(problem);
	}
	$(problemDiv).append("<div id='score_problems_button' class='touchable'>문제를 잘 풀었는지 확인해보세요. </div>");
	$("#problem_div").append(problemDiv);
	
}

$(document).ready(function(){
    
	$.ajax({
		"url":"/restapi/problems/categories",
		"type":"get"
	}).done(function(result){
		var i, categories = result.categories;
		for(i=0;i<categories.length;i++){
			$("#category_id").append("<option value='"+categories[i].category_id+"' label='"+categories[i].category_name+"'></option>")
		}
	}).fail(function(xhr, status, error){
		alert(xhr.responseJSON.content);
	})
	
	$(document).on("click","#get_problems_button",function(e){
		getProblems()
		.done(function(result){
			renderProblems(result);
		})
		.fail(function(xhr, status, error){
			alert(xhr.responseJSON.content);
		})
    });
	
	$(document).on("click","#score_problems_button",function(e){
		var i,j, problem = $(".problem");
		var list = new Array();
		var sum = 80;
		for(i=0;i<problem.length;i++){
			var problem_id = $(".problem").eq(i).find("input[type='radio']").attr("name");
			
			if(i!=0){
				sum += ($(".problem").eq(i).css("height").replaceAll("px","")-0)+40;
			}
			
			if(!$("input[name='"+problem_id+"']").is(":checked")){
				alert("["+(i+1)+"] 문제를 체크해야합니다.");
				$("#s2s1container").animate({
					"scrollTop":sum+"px"
				},300,"linear",function(){});
				
				return false;
			}
			var obj = new Object();
			obj.problem_id = problem_id ;
			obj.answer_id = $("input[name='"+problem_id+"']:checked").val();
			list.push(obj);
		}
		
		sendSolution(list)
		.done(function(result){
			alert("[ "+(result.right_score)+" / "+(result.right_score+result.wrong_score)+" ] \n percentage : "+result.percentage);
			
			var right_problems = result.right_problems;
			var wrong_problems = result.wrong_problems;
			
			for(i=0; i<right_problems.length; i++){
				$("#problem_"+right_problems[i]).css({
					"border":"2px solid #06d6a0"
				})
				$("#problem_"+right_problems[i]+" input:checked").next("label").css({
					"color":"#06d6a0",
					"font-weight":900
				})
			}
			
			for(i=0; i<wrong_problems.length; i++){
				$("#problem_"+wrong_problems[i]).css({
					"border":"2px solid #ee3f5c"
				})
				$("#problem_"+wrong_problems[i]+" input:checked").next("label").css({
					"color":"#ee3f5c",
					"font-weight":900
				})
			}
			
			$("input[type='radio']").attr({
				"disabled":true
			})
			
			$("#score_problems_button").remove();
			
			
		})
		.fail(function(xhr, status, error){
			if(xhr.status==401){
				refreshTokens()
				.done(function(result){
					sendSolution(list)
					.done(function(result){
						alert("[ "+(result.right_score)+" / "+(result.right_score+result.wrong_score)+" ] \n percentage : "+result.percentage);
						
						var right_problems = result.right_problems;
						var wrong_problems = result.wrong_problems;
						
						for(i=0; i<right_problems.length; i++){
							$("#problem_"+right_problems[i]+" input:checked").next("label").css({
								"color":"#06d6a0",
								"font-weight":900
							})
						}
						
						for(i=0; i<wrong_problems.length; i++){
							$("#problem_"+wrong_problems[i]+" input:checked").next("label").css({
								"color":"#ee3f5c",
								"font-weight":900
							})
						}
						
						$("input[type='radio']").attr({
							"disabled":true
						})
						
						$("#score_problems_button").remove();
					})
					.fail(function(xhr, status, error){
						alert(xhr.responseJSON.content);
					})
				})
				.fail(function(xhr, status, error){
					alert(xhr.responseJSON.content);
				});
			}else{
				alert(xhr.responseJSON.content);
			}
		})
    });
}) 