function disableGuideline(){
	$.cookie("disabled_guideline","disable",{expires: 365});
};

$(document).ready(function(){
	var tutorial_step = 0;
	console.log($.cookie("disabled_guideline"));
	if($.cookie("disabled_guideline")=="disable"){
		return;
	}else{
		$("#fullpage").append("<div id='tutorial_cover'></div>");
		$("#tutorial_cover").append("<div id='tutorial_description' style='top: 50%; left:50%; transform:translate(-50%,-50%);'>서비스 사용방법을 확인하시려면<br>화면을 클릭해주세요.<br>"+tutorial_step+"/"+4+"</div>");
		$(document).on("click","#tutorial_cover",function(e){
			tutorial_step++;
			if(tutorial_step>4){
				$("#tutorial_cover").remove();
				return;
			}else{
				$("#tutorial_cover").empty();
				if(tutorial_step==1){
					$("#tutorial_cover").append("<div id='menu_bar_highlight'></div>");
					$("#tutorial_cover").append("<div id='tutorial_description' style='bottom: 50px; left:50%; transform:translate(-50%);'>하단 메뉴에서 원하는 기능을<br>빠르게 사용하실 수 있습니다.<br>"+tutorial_step+"/"+4+"</div>");
				}else if(tutorial_step==2){
					$("#tutorial_cover").append("<div id='hnav_highlight'></div>");
					$("#tutorial_cover").append("<div id='vnav_highlight'></div>");
					$("#tutorial_cover").append("<div id='tutorial_description' style='top: 50%; left:50%; transform:translate(-50%,-50%);'>화면전환 가능여부를 나타냅니다.<br>스와이핑 할 수 있는 화면의 위치는 <span style='color:#3f3d56; font-weight:900;'>검정색</span>으로,<br>현재 화면의 위치는 <span style='color:#ee3f5c; font-weight:900;'>빨강색</span>으로 표기됩니다.<br>"+tutorial_step+"/"+4+"</div>");
				}else if(tutorial_step==3){
					$("#tutorial_cover").append("<div id='tutorial_description' style='top: 50%; left:50%; transform:translate(-50%,-50%);'>PC에서는 마우스 휠 또는 <span style='color:#06d6a0; font-weight:900;'>Drag</span> & <span style='color:#ee3f5c; font-weight:900;'>Drop</span>을,<br>모바일에서는 <span style='color:#3f3d56; font-weight:900;'>스와이핑</span> 동작으로<br>간단하게 화면을 전환하세요.<br>"+tutorial_step+"/"+4+"</div>");
				}else{
					$("#tutorial_cover").append("<div id='tutorial_description' style='top: 50%; left:50%; transform:translate(-50%,-50%);'>사용방법을 모두 확인하셨습니다.<br>이제 마음껏 서비스를 즐기세요 :)<br>"+tutorial_step+"/"+4+"</div>");
					$("#tutorial_cover").append("<div id='tutorial_disable_button' onclick='disableGuideline()';>가이드라인을<br>다시 보고 싶지 않아요</div>")
				}
			}
		});
	}
});