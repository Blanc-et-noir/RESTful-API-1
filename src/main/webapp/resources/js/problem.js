var problemSwiper;

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
		"url":"/restapi/scores",
		"type":"post",
		"contentType": "application/json;",
		"data":JSON.stringify(obj)
	});
}

function renderProblems(result){
	var i,j, problems = result.problems;
	$("#problem_div").empty();
	var problemDiv = $("<div id='problems' class='touchable'></div>");
	
	for(i=0;i<problems.length;i++){
		var choices = problems[i].choices;
		var problem = $("<div id='problem_"+problems[i].problem_id+"' class='problem touchable'></div>");

		$(problem).append("<div class='problem_content touchable'>["+(i+1)+"] "+problems[i].problem_content+", 정답률 : "+problems[i].answer_rate+"%</div>");
		
		if(problems[i].problem_image_name!=null){
			$(problem).append("<img class='problem_image touchable' src='/restapi/problems/"+problems[i].problem_id+"/images/"+problems[i].problem_image_name+"';>");
		}
		var choicesDiv = $("<div class='choices touchable'></div>");
		
		for(j=0;j<choices.length;j++){
			choicesDiv.append("<div class='choice touchable'><input id='choice_"+choices[j].choice_id+"' class='touchable' type='radio' name='"+problems[i].problem_id+"' value='"+choices[j].choice_id+"'><label for='choice_"+choices[j].choice_id+"' class='touchable'>("+(j+1)+") "+choices[j].choice_content+"</label></div>");
		}
		$(problem).append(choicesDiv);
		$(problem).append("<div class='view'></div>");
		$(problemDiv).append(problem);
	}
	$(problemDiv).append("<div id='score_problems_button' class='touchable'>채점하기</div>");
	$("#problem_div").append(problemDiv);
}

$(document).ready(function(){
    
	$.ajax({
		"url":"/restapi/categories",
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
			if(xhr.status==401){
				refreshTokens()
				.done(function(){
					getProblems()
					.done(function(result){
						renderProblems(result);
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
    });
	
	$(document).on("click","#score_problems_button",function(e){
		var i,j, problem = $(".problem");
		var list = new Array();
		
		for(i=0;i<problem.length;i++){
			var problem_id = $(".problem").eq(i).find("input[type='radio']").attr("name");
			if(!$("input[name='"+problem_id+"']").is(":checked")){
				alert("["+(i+1)+"] 문제를 체크해야합니다.");
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