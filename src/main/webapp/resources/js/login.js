$(document).ready(function(){
    $(document).on("click","#login_button",function(e){
    	var publickey;
    	$.ajax({
    		"url":"/restapi/user/getPublicKey.do",
    		"type":"POST",
    		"success":function(result, textStatus, request){
				publickey = result.publickey;
		    	$.ajax({
		    		"url":"/restapi/user/login.do",
		    		"data":{
		    			"user_id":$("#user_id").val(),
		    			"user_pw":encryptByRSA2048($("#user_pw").val(),publickey),
		    			"publickey":publickey
		    		},
		    		"type":"POST",
		    		"success":function(result, textStatus, request){
		    			if(result.flag=="true"){
		    				alert("로그인성공");
		        			var form = $("<form action='/restapi/user/infoForm.do'></form>");
		        			$("body").append(form);
		        			form.submit();
		    			}else{
		    				alert("로그인 실패");
		    			}
		    		},
		    		"error": function(xhr, status, error) {
		    			alert(JSON.parse(xhr.responseText).content);
		      		}
		    	});
    		},
    		"error": function(xhr, status, error) {
    			alert(JSON.parse(xhr.responseText).content);
      		}
    	})
    });
})