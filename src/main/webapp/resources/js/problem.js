function sendSolution(list){
	var obj = new Object();
	obj.list = list;
	return $.ajax({
		"url":"/restapi/user/scoreProblems.do",
		"type":"post",
		"contentType": "application/json; charset=utf-8",
		"data":JSON.stringify(obj),
		"success":function(result){
			alert("[ "+(result.right_score)+" / "+(result.right_score+result.wrong_score)+" ] \n percentage : "+result.percentage);
		}
	});
}

function getProblems(){
	return $.ajax({
		"url":"/restapi/user/getProblems.do",
		"type":"post",
		"data":{
			"category_id":$("#category_id").val()
		},
		"success":function(result){
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
	}).fail(function(){
		alert("카테고리 조회실패");
	})
	
	$(document).on("click","#get_problems_button",function(e){
		getProblems()
		.done()
		.fail(function(xhr, status, error){
			if(xhr.status==401){
				refreshTokens()
				.done(function(){
					getProblems()
					.done()
					.fail(function(xhr, status, error){
						alert(JSON.parse(xhr.responseText).content)
					})
				})
				.fail(function(xhr, status, error){
					alert(JSON.parse(xhr.responseText).content)
				})
			}else{
				alert(JSON.parse(xhr.responseText).content)
			}
		})
    });
	
	$(document).on("click","#score_problems_button",function(e){
		var i,j, problem = $(".problem");
		var list = new Array();
		
		for(i=0;i<problem.length;i++){
			var problem_id = $(".problem").eq(i).find("input[type='radio']").attr("name");
			if(!$("input[name='"+problem_id+"']").is(":checked")){
				return false;
			}
			var obj = new Object();
			obj.problem_id = problem_id ;
			obj.answer_id = $("input[name='"+problem_id+"']:checked").val();
			list.push(obj);
		}
		
		sendSolution(list)
		.done()
		.fail(function(xhr, status, error){
			//401 에러일때만 토큰갱신후 재시도
			if(xhr.status==401){
				refreshTokens()
				.done(function(){
					sendSolution(list)
					.done()
					.fail(function(xhr, status, error){
						alert(JSON.parse(xhr.responseText).content)
					})
				})
				.fail(function(xhr, status, error){
					alert(JSON.parse(xhr.responseText).content)
				});
			}else{
				alert(JSON.parse(xhr.responseText).content)
			}
		})
    });
}) 