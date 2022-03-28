function getProblems(){
	return $.ajax({
		"url":"/restapi/user/getProblems.do",
		"type":"post",
		"dataType":"json",
		"data":{
			"category_id":$("#category_id").val()
		}
	});
}

function renderProblems(result){
	var i,j, problems = result.problems;
	
	$("#problems").empty();
	
	for(i=0;i<problems.length;i++){
		var choices = problems[i].choices;
		
		var problemsDiv = $("<div class='problem touchable'><div>");
		
		$(problemsDiv).append("<div class='problem_content touchable'> ["+(i+1)+"] : "+problems[i].problem_content+"</div>");
		$(problemsDiv).append("<img class='touchable' src=''>");
		
		var choicesDiv = $("<div class='touchable'><div>");
		 
		for(j=0;j<choices.length;j++){
			$(choicesDiv).append("<div class='choice touchable'><label class='touchable'><input class='touchable' type='radio' name='"+problems[i].problem_id+"' value='"+choices[j].choice_id+"'>"+choices[j].choice_content+"</label></div>");
		}
		
		$(problemsDiv).append(choicesDiv);
		$("#problems").append(problemsDiv);					
	}
	
	$("#problems").append("<div class='touchable' id='score_problems_button'>채점하기</div>");
}

function sendSolution(list){
	var obj = new Object();
	obj.list = list;
	return $.ajax({
		"url":"/restapi/user/scoreProblems.do",
		"type":"post",
		"contentType": "application/json;",
		"data":JSON.stringify(obj)
	});
}

$(document).ready(function(){
	
	$.ajax({
		"url":"/restapi/user/getCategories.do",
		"type":"post"
	}).done(function(result){
		var i, categories = result.categories;
		for(i=0;i<categories.length;i++){
			$("#category_id").append("<option value='"+categories[i].category_id+"' label='"+categories[i].category_name+"'></option>")
		}
	}).fail(function(xhr, status, error){
		alert("카테고리 조회실패");
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
						alert("목록발급 실패");
					})
				})
				.fail(function(xhr, status, error){
					alert("토큰갱신 실패");
				})
			}else{
				alert("목록발급 실패");
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
		})
		.fail(function(xhr, status, error){
			if(xhr.status==401){
				refreshTokens()
				.done(function(result){
					sendSolution(list)
					.done(function(result){
						alert("[ "+(result.right_score)+" / "+(result.right_score+result.wrong_score)+" ] \n percentage : "+result.percentage);
					})
					.fail(function(xhr, status, error){
						alert("채점 실패");
					})
				})
				.fail(function(xhr, status, error){
					alert("토큰갱신 실패");
				});
			}else{
				alert("채점 실패");
			}
		})
    });
}) 