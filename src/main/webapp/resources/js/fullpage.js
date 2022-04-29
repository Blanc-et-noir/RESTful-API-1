/*
******************************************************************
README
******************************************************************
Before you use this plugin, you must have appropriate HTML source code
1. Make <div> tag whose id is "fullpage", eg) <div id='fullpage'></div>
2. Make <div> tag whose class is "section" in <div> tag whose id is "fullpage"
   If you make 3 sections in your code, then you'll have 3 vertical pages.
   If you make 5 sections in your code, then you'll have 5 vertical pages.
3. Make <div> tag whose class is "slide" in <div> tag whose class is "section"
   If you make 3 slides in your code, then you'll have 3 horizontal pages.
   If you make 5 slides in your code, then you'll have 5 horizontal pages.
4. If you want to make an scrollable slide, then make <div> tag whose class is "box" in the slide which you want to make scrollable.
   Also, the width and height of the box must be equal or greater than the slide OR IT WILL NOT BE SCROLLABLE
There is example HTML code
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="../js/jquery.js"></script>
    <script src="../js/jquery-ui.js"></script>
    <script src="../js/fullpage.js"></script>
    <script src="../js/main.js"></script>
    <title>Document</title>
</head>
<body>
    <div id="fullpage">
        <div class="section">
            <div class="slide">
                <p>Any elements can be used in the slide</p>
            </div>
            <div class="slide">
                <p>All you have to do is just make some elements in the slides</p>
            </div>
            <div class="slide">
                <p>Enjoy this plugin</p>
            </div>
        </div>
        <div class="section">
            <div class="slide">
                <div class="box" style="width: 2000px; height:2000px">
                    <p>The boxes are not essential elements</p>
                </div>
            </div>
            <div class="slide">
                <div class="box" style="width: 100%; height:2000px">
                    <p>If you want to make slide scrollable then use box</p>
                </div>
            </div>
            <div class="slide">
                <div class="box" style="width: 100%; height:2000px">
                    <p>To make slide scrollable, the size of the box must be greater than slide</p>
                </div>
            </div>
        </div>
        <div class="section">
            <div class="slide">
                <div class="box" style="width: 100%; height:2000px">
                    <p>In this case, when the height of the browser is less than 2000px, then slide will be scrollable</p>
                </div>
            </div>
            <div class="slide">
                <div class="box" style="width: 100%; height:2000px">
                    <p>Otherwise, it won't</p>
                </div>
            </div>
            <div class="slide">
                <div class="box" style="width: 100%; height:2000px">
                    <p>slide must have some elements, if it doesn't, IT WILL NOT WORK PROPERLY</p>
                    <p>SO IF YOU HAVE EMPTY SLIDE WHICH HAS NO CHILDREN ELEMENTS, THEN REMOVE THE SLIDE, OR JUST PUT SOME ELEMENTS</p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
$(document).ready(function(){
    $("#fullpage").initialize({
        ******************************************************************
        Method
        ******************************************************************
        beforeSectionLoad:function(sectionIndex, slideIndex){
            //do process as you want
        },
        afterSectionLoad:function(sectionIndex, slideIndex){
            //do process as you want
        },
        beforeSlideLoad:function(sectionIndex, slideIndex){
            //do process as you want
        },
        afterSlideLoad:function(sectionIndex, slideIndex){
            //do process as you want
        },
        beforeLoad:function(sectionIndex, slideIndex){
            //do process as you want
        },
        afterLoad:function(sectionIndex, slideIndex){
            //do process as you want
        }
        ******************************************************************
        Information about Method 
        ******************************************************************
        function will be called in this order.
        1. beforeLoad()
        2. beforeSectionLoad() OR beforeSlideLoad()
        3. afterSectionLoad() OR afterSlideLoad()
        4. afterLoad()
        when you call before_____ function, then both sectionIndex and slideIndex will not be changed
        so when you do something before you make a page change, use these indexes
        when you call after_____ function, then both sectionIndex and slideIndex will be changed
        so when you do something after you make a page change, use these indexes
        ******************************************************************
        Option
        ******************************************************************
        keyboardMove : To use dragMove in computer system, set this option true
        keyboardSettings : {
            up : integer value in ASCII,
            down : integer value in ASCII,
            left : integer value in ASCII,
            right : integer value in ASCII
        }
        swipeMove : To use swipeMove in computer system, set this option true
        swipeSensitivity : set this option 0.0~1.0 float value
        dragMove : To use dragMove in computer system, set this option true
        dragSensitivity : set this option 0.0~1.0 float value
        animationDuration : set this option mili seconds
        animationEasing : set thie option string value, this option may require jquery-ui
        sectionColors : ["#hexcode","#hexcode","#hexcode"] this is array, the number of these options must be equal or more than the number of section
        In this case, you can have 3 or less sections. you can't have more than 3 sections.
    });
});
*/


$.fn.extend({
    initialize:function(settings){

        var winW = $(window).width(), winH = $(window).height(),i,j;
    
        //set all elements's margin, padding to 0
        $("*").css({
            padding:0,
            margin:0
        });

        //these are default options
        if(settings.wheelMove == undefined){settings.wheelMove=true;}
        if(settings.dragMove == undefined){settings.dragMove=true;}
        if(settings.dragSensitivity == undefined){settings.dragSensitivity = 0.15}
        if(settings.swipeMove == undefined){settings.swipeMove=true;}
        if(settings.swipeSensitivity == undefined){settings.swipeSensitivity = 0.15}
        if(settings.touchMove == undefined){settings.touchMove=true;}
        if(settings.keyboardMove == undefined){settings.keyboardMove=false}
        if(settings.keyboardSettings== undefined){
            settings.keyboardSettings = {
                up:72,
                down:80,
                left:75,
                right:77
            };
        }
        if(settings.animationDuration == undefined){settings.animationDuration=700}
        if(settings.animationEasing == undefined){settings.animationEasing = "easeInOutExpo"}
        if(settings.touchMove == true && settings.touchPanelColor == undefined){settings.touchPanelColor="#3F3D56"}
        if(settings.touchMove == true && settings.touchPanelOpacity == undefined){settings.touchPanelOpacity="0.10"}
        if(settings.animationEasing == undefined){settings.animationEasing = "easeInOutExpo"}
        if(settings.navigator == undefined){settings.navigator = true}
        if(settings.navigatorSelectedColor == undefined){settings.navigatorSelectedColor = "#EE3F5C"}
        if(settings.navigatorUnselectedColor == undefined){settings.navigatorUnselectedColor = "#3F3D56"}
        if(settings.color == undefined){settings.color = "#000000"}
        if(settings.touchPanelVerticalSize == undefined){settings.touchPanelSize = "7%"}
        if(settings.touchPanelHorizontalSize == undefined){settings.touchPanelSize = "7%"}

        //remove scrollbar
        $("body").css({
            "overflow-x": "hidden",
            "overflow-y": "hidden",
            "-ms-overflow-style": "none"
        })
    
        //remove scrollbar
        $("body::-webkit-scrollbar").css({
            display:"none"
        })
    
        //initialize fullpage
        $("#fullpage").css({
            position:"fixed",
            width:"100%",
            height:"100%"
        });

        //initialize sections and make wrapper class for slides
        for(i=0; i<$(".section").length; i++){
            var slideNum = $("#fullpage .section:nth-of-type("+(i+1)+")").find(".slide").length;
            if(settings.sectionColors == undefined || settings.sectionColors.length < $(".section").length){
                $("#fullpage .section:nth-of-type("+(i+1)+")").css({
                    width:winW*slideNum,
                    height:winH,
                    position:"fixed",
                    top:winH*i
                })
            }else{
                $("#fullpage .section:nth-of-type("+(i+1)+")").css({
                    width:winW*slideNum,
                    height:winH,
                    position:"fixed",
                    "background-color":settings.sectionColors[i],
                    top:winH*i
                })
            }
            for(j=0; j<slideNum; j++){
                $(".section").eq(i).find(".slide").eq(j).children().wrapAll("<div id='s"+i+"s"+j+"container'; style='overflow-y:scroll';></div>");
                $(".section").eq(i).find(".slide").eq(j).css({
                    width:winW,
                    height:winH,
                    position:"fixed",
                    top:winH*i,
                    left:winW*j
                })
            }
            $("#fullpage .section:nth-of-type("+(i+1)+")").children().wrapAll("<div class='wrapper' style='position:relative; width:100%;height:100%; display:flex;flex-direction:row;'></div>");
        }

        //set an initial Section, default is first section
        $("#fullpage .section:nth-of-type(1)").addClass("activeSection");
        //set an initial slide for each section
        $(".slide:first-of-type").addClass("activeSlide");
    
        function setVerticalNavigator(){
            $("#verticalNav").remove();
            var sectionNum = $(".section").length;
            var activeSectionIndex = $(".activeSection").index();
            $("#fullpage").append("<ul id='verticalNav'/>");
            for(i=0; i<sectionNum; i++){
                if(i!=activeSectionIndex){
                    $("#verticalNav").append("<a class='verticalA'></a>");
                }else{
                    $("#verticalNav").append("<a class='verticalA selected'></a>");
                }
            }
            $("#verticalNav").css({
                "right": "0%",
                "top": "50%",
                "transform": "translate(-50%, -50%)",
                "position":"fixed",
                "display":"flex",
                "flex-direction":"column",
                "text-decoration": "none",
                "list-style": "none"
            });
            $(".verticalA").css({
                "width": "5px",
                "height": "5px",
                "border-radius": "50%",
                "background-color": settings.navigatorUnselectedColor,
                "margin": "5px 5px"
            });
            $(".verticalA.selected").css({
                "width": "5px",
                "height": "5px",
                "border-radius": "50%",
                "background-color": settings.navigatorSelectedColor,
                "margin": "5px 5px"
            });
        }

        function setHorizontalNavigator(){
            $("#horizontalNav").remove();
            var slideNum = $(".activeSection .slide").length;
            var activeSlideIndex = $(".activeSection .activeSlide").index();
            $("#fullpage").append("<ul id='horizontalNav'/>");
            for(i=0; i<slideNum; i++){
                if(i!=activeSlideIndex){
                    $("#horizontalNav").append("<a class='horizontalA'></a>");
                }else{
                    $("#horizontalNav").append("<a class='horizontalA selected'></a>");
                }
            }
            $("#horizontalNav").css({
                "left": "50%",
                "bottom": "0%",
                "position":"fixed",
                "display":"flex",
                "flex-direction":"row",
                "transform": "translate(-50%,0%)",
                "text-decoration": "none",
                "list-style": "none"
            });
            $(".horizontalA").css({
                "width": "5px",
                "height": "5px",
                "border-radius": "50%",
                "background-color": settings.navigatorUnselectedColor,
                "margin": "5px 5px"
            });
            $(".horizontalA.selected").css({
                "width": "5px",
                "height": "5px",
                "border-radius": "50%",
                "background-color": settings.navigatorSelectedColor,
                "margin": "5px 5px"
            });
        }

        function setTouchPanel(){
            $(document).off("click","#upTouchPanel",moveUp);
            $(document).off("click","#downTouchPanel",moveDown);
            $(document).off("click","#leftTouchPanel",moveLeft);
            $(document).off("click","#rightTouchPanel",moveRight);
            $("#touchPanel").remove();
            var currentSection = $(".activeSection");
            var currentSlide = $(".activeSection .activeSlide");
            $("#fullpage").append("<ul id='touchPanel'/>");
            $("#touchPanel").css({
                "text-decoration": "none",
                "list-style": "none",
                "margin":"0px",
                "padding":"0px"
            })

            //UP Panel
            if(currentSection.prev(".section").length != 0 ){
                $("#touchPanel").append("<a id='upTouchPanel'></a>");
                $("#upTouchPanel").css({
                    "display":"block",
                    "position":"fixed",
                    "width":"100%",
                    "height":settings.touchPanelVerticalSize,
                    "top":"0%",
                    "opacity":settings.touchPanelOpacity
                });
                $("#upTouchPanel").hover(function(){
                    $(this).css({
                        "background-color":settings.touchPanelColor
                    })
                },function(){
                    $(this).css({
                        "background-color":"transparent"
                    })
                })
                $(document).on("click","#upTouchPanel",moveUp);
            }

            //DOWN Panel
            if(currentSection.next(".section").length != 0 ){
                $("#touchPanel").append("<a id='downTouchPanel'></a>");
                $("#downTouchPanel").css({
                    "display":"block",
                    "position":"fixed",
                    "width":"100%",
                    "height":settings.touchPanelVerticalSize,
                    "bottom":"0%",
                    "opacity":settings.touchPanelOpacity
                });
                $("#downTouchPanel").hover(function(){
                    $(this).css({
                        "background-color":settings.touchPanelColor
                    })
                },function(){
                    $(this).css({
                        "background-color":"transparent"
                    })
                })
                $(document).on("click","#downTouchPanel",moveDown);
            }

            //LEFT Panel
            if(currentSlide.prev(".slide").length != 0 ){
                $("#touchPanel").append("<a id='leftTouchPanel'></a>");
                $("#leftTouchPanel").css({
                    "display":"block",
                    "position":"fixed",
                    "width":settings.touchPanelHorizontalSize,
                    "height":"100%",
                    "top":"50%",
                    "left":"0%",
                    "transform": "translate(-0%,-50%)",
                    "opacity":settings.touchPanelOpacity
                });
                $("#leftTouchPanel").hover(function(){
                    $(this).css({
                        "background-color":settings.touchPanelColor
                    })
                },function(){
                    $(this).css({
                        "background-color":"transparent"
                    })
                })
                $(document).on("click","#leftTouchPanel",moveLeft);
            }

            //RIGHT Panel
            if(currentSlide.next(".slide").length != 0 ){
                $("#touchPanel").append("<a id='rightTouchPanel'></a>");
                $("#rightTouchPanel").css({
                    "display":"block",
                    "position":"fixed",
                    "width":settings.touchPanelHorizontalSize,
                    "height":"100%",
                    "top":"50%",
                    "left":"100%",
                    "transform": "translate(-100%,-50%)",
                    "opacity":settings.touchPanelOpacity

                });
                $("#rightTouchPanel").hover(function(){
                    $(this).css({
                        "background-color":settings.touchPanelColor
                    })
                },function(){
                    $(this).css({
                        "background-color":"transparent"
                    })
                })
                $(document).on("click","#rightTouchPanel",moveRight);
            }
        }

        setInitialCSS();
        setVerticalNavigator();
        setHorizontalNavigator();
        $(window).resize(reLayout);

        if(settings.touchMove == true){
            setTouchPanel();
        }
        
        function setInitialCSS(){
            $("*").css({
                "margin": "0px",
                "padding": "0px",
                "box-sizing": "border-box"
            })
            $("div[id$='container']").css({
                "width": "100%",
                "height": "100%",
                "position": "absolute",
                "display": "block"
            })
        }

        function reLayout(){
            winW = $(window).width();
            winH = $(window).height();

            //Section Size
            for(i=0; i<$(".section").length; i++){
                var slideNum = $("#fullpage .section:nth-of-type("+(i+1)+")").find(".slide").length;
                var activeSectionIndex = $("#fullpage .activeSection").index();
                var activeSlideIndex = $("#fullpage .section").eq(i).find(".activeSlide").index();
                $("#fullpage .section:nth-of-type("+(i+1)+")").css({
                    width:winW*slideNum,
                    height:winH,
                    top:winH*(i-activeSectionIndex),
                    left:winW*activeSlideIndex*(-1)
                })
            }

            //Slide Size
            for(i=0; i<$(".section").length; i++){
                var activeSectionIndex = $("#fullpage .activeSection").index();
                var activeSlideIndex = $("#fullpage .section").eq(i).find(".activeSlide").index();
                for(j=0; j<$(".section").eq(i).find(".slide").length; j++){
                    $(".section").eq(i).find(".slide").eq(j).css({
                        "width":winW,
                        "height":winH,
                        top:winH*(i-activeSectionIndex),
                        left:winW*(j-activeSlideIndex)
                    })
                }
            }
        }

        //Keyboard Event
        if(settings.keyboardMove == true){
            $(document).on("keydown",$("#fullpage"),function(e){
                if(!$("#fullpage").is(":animated") && !$(".slide").is(":animated")){
                    if(e.keyCode == settings.keyboardSettings.up){
                        moveUp();
                    }else if(e.keyCode == settings.keyboardSettings.down){
                        moveDown();
                    }else if(e.keyCode == settings.keyboardSettings.left){
                        moveLeft();
                    }else if(e.keyCode == settings.keyboardSettings.right){
                        moveRight();
                    }else{
    
                    }
                }
            });
        }

        //Wheel Event
        var target = document.querySelectorAll(".slide"), tartgetLength = target.length;
        if(settings.wheelMove == true){
            for(i=0; i<tartgetLength; i++){
                target[i].addEventListener("mousewheel",function(e){
                    if(!$("#fullpage").is(":animated")&&!$(".slide").is(":animated")){
                    	if($(e.target).hasClass("touchable")){
                    		return;
                    	}
                        if(e.deltaY&&e.shiftKey){
                            if(e.deltaY < 0 && $("#fullpage .activeSection .activeSlide div[id$='container']").scrollLeft() == 0){
                                moveLeft();
                            }else if((e.deltaY >=0 &&$("#fullpage .activeSection .activeSlide").find(".box").length==0)||e.deltaY >=0 && Math.floor($("#fullpage .activeSection .activeSlide div[id$='container']").scrollLeft()) + 10 > Math.floor($("#fullpage .activeSection .activeSlide .box").css("width").replace("px","") - $("#fullpage .activeSection .activeSlide div[id$='container']").css("width").replace("px",""))){
                                moveRight();
                            }
                        }else if(e.deltaY!=0){
                            if(e.deltaY < 0 && $("#fullpage .activeSection .activeSlide div[id$='container']").scrollTop() == 0){
                                moveUp();
                            }else if((e.deltaY >=0 &&$("#fullpage .activeSection .activeSlide").find(".box").length==0)||(e.deltaY >=0 && Math.floor($("#fullpage .activeSection .activeSlide div[id$='container']").scrollTop()) + 10 > Math.floor($("#fullpage .activeSection .activeSlide .box").css("height").replace("px","") - $("#fullpage .activeSection .activeSlide div[id$='container']").css("height").replace("px","")))){
                                moveDown();
                            }
                        }
                    }
                })
            }
        }

        //Swipe Event
        var clientY, clientX, swipeFlag = false;
        var deltaY, deltaX;
        if(settings.swipeMove == true){
            $(document).on("touchstart",".slide",function(e){
            	if($(e.target).hasClass("touchable")){
            		swipeFlag = false
            		return;
            	}
                clientY = e.touches[0].clientY;
                clientX = e.touches[0].clientX;
                swipeFlag = true;
            })
            $(document).on("touchend",".slide",function(e){
            	if($(e.target).hasClass("touchable")||swipeFlag==false){
            		swipeFlag = false
            		return;
            	}
                deltaX = e.changedTouches[0].clientX - clientX;
                deltaY = e.changedTouches[0].clientY - clientY;
                if(true){
                    if(abs(deltaX) > abs(deltaY)){
                        if(abs(deltaX) >= (($(".slide").css("width")).replace("px","")* settings.swipeSensitivity)){
                            if(deltaX > 0 && $("#fullpage .activeSection .activeSlide div[id$='container']").scrollLeft() == 0){
                                moveLeft();
                            }else{
                                if($("#fullpage .activeSection .activeSlide").find(".box").length==0&&deltaX <=0 ){
                                    moveRight();
                                }else if($("#fullpage .activeSection .activeSlide").find(".box").length!=0&&deltaX<=0&& Math.floor($("#fullpage .activeSection .activeSlide div[id$='container']").scrollLeft()) + 10 > Math.floor($("#fullpage .activeSection .activeSlide .box").css("width").replace("px","") - $("#fullpage .activeSection .activeSlide div[id$='container']").css("width").replace("px",""))){
                                    moveRight();
                                }
                            }
                        }
                    }else{
                        if(abs(deltaY) >= (($(".slide").css("height")).replace("px","")* settings.swipeSensitivity)){
                            if(deltaY > 0 && $("#fullpage .activeSection .activeSlide div[id$='container']").scrollTop() == 0){
                                moveUp();
                            }else{
                                if($("#fullpage .activeSection .activeSlide").find(".box").length==0&&deltaY <=0 ){
                                    moveDown();
                                }else if($("#fullpage .activeSection .activeSlide").find(".box").length!=0&&deltaY <=0&& Math.floor($("#fullpage .activeSection .activeSlide div[id$='container']").scrollTop()) + 10 > Math.floor($("#fullpage .activeSection .activeSlide .box").css("height").replace("px","") - $("#fullpage .activeSection .activeSlide div[id$='container']").css("height").replace("px",""))){
                                    moveDown();
                                }
                            }
                        }
                    }
                }
            })
        }

        //Drag Event
        var curX, curY, newX, newY, dragFlag = false;
        if(settings.dragMove == true){
            $(document).on("mousedown","#fullpage .slide",function(e){
            	if($(e.target).hasClass("touchable")){
            		dragFlag = false
            		return;
            	}
                if(e.which == 1){
                    curX = e.clientX;
                    curY = e.clientY;
                    dragFlag=true;
                }
            });
        }
        if(settings.dragMove == true){
            $(document).on("mouseup","#fullpage .slide",function(e){
            	if($(e.target).hasClass("touchable")||dragFlag==false){
            		return;
            	}
                if(e.which == 1){
                    newX = e.clientX;
                    newY = e.clientY;
                    if(abs(curX-newX) > abs(curY-newY)){
                        if(abs(curX-newX)>=(($(".slide").css("width")).replace("px","")* settings.dragSensitivity)){
                            if(curX-newX < 0 && $("#fullpage .activeSection .activeSlide div[id$='container']").scrollLeft() == 0){
                                moveLeft();
                            }else{
                                if($("#fullpage .activeSection .activeSlide").find(".box").length==0 && curX -newX >= 0){
                                    moveRight();
                                }else if($("#fullpage .activeSection .activeSlide").find(".box").length!=0 && curX -newX >= 0 && Math.floor($("#fullpage .activeSection .activeSlide div[id$='container']").scrollLeft()) + 10 > Math.floor($("#fullpage .activeSection .activeSlide .box").css("width").replace("px","") - $("#fullpage .activeSection .activeSlide div[id$='container']").css("width").replace("px",""))){
                                    moveRight();
                                }
                            }
                        }
                    }else{
                        if(abs(curY-newY)>=($(".slide").css("height").replace("px","")* settings.dragSensitivity)){
                            if(curY-newY < 0 && $("#fullpage .activeSection .activeSlide div[id$='container']").scrollTop() == 0){
                                moveUp();
                            }else{
                                if($("#fullpage .activeSection .activeSlide").find(".box").length==0 && curY -newY >= 0){
                                    moveDown();
                                }else if($("#fullpage .activeSection .activeSlide").find(".box").length!=0 && curY -newY >= 0 && Math.floor($("#fullpage .activeSection .activeSlide div[id$='container']").scrollTop()) + 10 > Math.floor($("#fullpage .activeSection .activeSlide .box").css("height").replace("px","") - $("#fullpage .activeSection .activeSlide div[id$='container']").css("height").replace("px",""))){
                                    moveDown();
                                }
                            }
                        }
                    }
                }
            });
        }

        function abs(num){
            return num>=0?num:num*(-1);
        }

        //Move Page Method
        function movePage(sectionIndex, slideIndex){
            var sectionNum = $(".section").length;
            var slideNum = $(".section").eq(sectionIndex).find(".slide").length;
            if(sectionNum<=sectionIndex || slideNum <= slideIndex || $(".slide").is(":animated") || $("#fullpage .section").is(":animated")){
                return;
            }else{
                
                var currentSection = $(".activeSection");
                if(settings.beforeLoad == undefined){
    
                }else{
                    settings.beforeLoad(sectionIndex, slideIndex);
                }
                if(settings.beforeSectionLoad == undefined){
                    
                }else{
                    settings.beforeSectionLoad(sectionIndex, slideIndex);
                }

                $("#fullpage .section").eq(sectionIndex).find(".slide").animate({
                    "left":winW*slideIndex*(-1)
                },0, settings.animationEasing, function(){
                    currentSection.removeClass("activeSection");
                    $(".section").eq(sectionIndex).addClass("activeSection");

                    var currentSlide = $(".activeSection .activeSlide");
                    currentSlide.removeClass("activeSlide");
                    
                    $(".section").eq(sectionIndex).find(".slide").eq(slideIndex).addClass("activeSlide");
                    reLayout();
                    setVerticalNavigator();
                    setHorizontalNavigator();
                    if(settings.touchMove == true){
                        setTouchPanel();
                    }
                    if(settings.afterSectionLoad == undefined){
    
                    }else{
                        settings.afterSectionLoad(sectionIndex, slideIndex);
                    }
                    if(settings.afterLoad == undefined){
    
                    }else{
                        settings.afterLoad(sectionIndex, slideIndex);
                    }
                })
            }
        }

        $(document).on("click","#move_button1",function(){
        	movePage(2,0);
        });
        $(document).on("click","#move_button2",function(){
        	movePage(3,0);
        });
        $(document).on("click","#move_button3",function(){
        	movePage(4,0);
        });
        $(document).on("click","#move_button4",function(){
        	movePage(5,0);
        });
        
        function moveUp(){
            var currentSection = $(".activeSection");
            var sectionIndex = $(".activeSection").index();
            var slideIndex = $(".activeSection .activeSlide").index();
            if(currentSection.prev(".section").length==0 || $(".slide").is(":animated")|| $(".section").is(":animated")){
                
            }else{
                
                if(settings.beforeLoad == undefined){
    
                }else{
                    settings.beforeLoad(sectionIndex, slideIndex);
                }
                if(settings.beforeSectionLoad == undefined){
                    
                }else{
                    settings.beforeSectionLoad(sectionIndex, slideIndex);
                }
                $(".section, .slide").animate({
                    top:"+="+winH
                },settings.animationDuration,settings.animationEasing,function(){                    
                    currentSection.removeClass("activeSection");
                    currentSection.prev().addClass("activeSection");
                    sectionIndex = $(".activeSection").index();
                    slideIndex = $(".activeSection .activeSlide").index();
                    reLayout();
                    setVerticalNavigator();
                    setHorizontalNavigator();
                    if(settings.touchMove == true){
                        setTouchPanel();
                    }
                    if(settings.afterSectionLoad == undefined){
    
                    }else{
                        settings.afterSectionLoad(sectionIndex, slideIndex);
                    }
                    if(settings.afterLoad == undefined){
    
                    }else{
                        settings.afterLoad(sectionIndex, slideIndex);
                    }
                });
            }
        }

        function moveDown(){
            var currentSection = $(".activeSection");
            var sectionIndex = $(".activeSection").index();
            var slideIndex = $(".activeSection .activeSlide").index();

            if(currentSection.next(".section").length==0 || $(".slide").is(":animated") || $(".section").is(":animated")){
                
            }else{
                if(settings.beforeLoad == undefined){
    
                }else{
                    settings.beforeLoad(sectionIndex, slideIndex);
                }
                if(settings.beforeSectionLoad == undefined){
                    
                }else{
                    settings.beforeSectionLoad(sectionIndex, slideIndex);
                }
                $(".section, .slide").animate({
                    top:"-="+winH
                },settings.animationDuration,settings.animationEasing,function(){
                    currentSection.removeClass("activeSection");
                    currentSection.next().addClass("activeSection");
                    sectionIndex = $(".activeSection").index();
                    slideIndex = $(".activeSection .activeSlide").index();
                    reLayout();
                    setVerticalNavigator();
                    setHorizontalNavigator();
                    if(settings.touchMove == true){
                        setTouchPanel();
                    }
                    if(settings.afterSectionLoad == undefined){

                    }else{
                        settings.afterSectionLoad(sectionIndex, slideIndex);
                    }
                    if(settings.afterLoad == undefined){
    
                    }else{
                        settings.afterLoad(sectionIndex, slideIndex);
                    }
                });
            }
        }

        function moveLeft(){
            var currentSlide = $(".activeSection .activeSlide");
            var sectionIndex = $(".activeSection").index();
            var slideIndex = $(".activeSection .activeSlide").index();
            if(currentSlide.prev(".slide").length ==0 || $("#fullpage .slide").is(":animated")){
                return;
            }else{
                if(settings.beforeLoad == undefined){
    
                }else{
                    settings.beforeLoad(sectionIndex, slideIndex);
                }
                if(settings.beforeSlideLoad == undefined){

                }else{
                    settings.beforeSlideLoad(sectionIndex, slideIndex);
                }
                $("#fullpage .activeSection .slide").animate({
                    left:"+="+winW
                },settings.animationDuration,settings.animationEasing,function(){
                    
                    currentSlide.removeClass("activeSlide");
                    currentSlide.prev().addClass("activeSlide");
                    sectionIndex = $(".activeSection").index();
                    slideIndex = $(".activeSection .activeSlide").index();
                    reLayout();
                    setVerticalNavigator();
                    setHorizontalNavigator();
                    if(settings.touchMove == true){
                        setTouchPanel();
                    }
                    if(settings.afterSlideLoad == undefined){

                    }else{
                        settings.afterSlideLoad(sectionIndex, slideIndex);
                    }
                    if(settings.afterLoad == undefined){
    
                    }else{
                        settings.afterLoad(sectionIndex, slideIndex);
                    }
                });
            }
        }

        function moveRight(){
            var currentSlide = $(".activeSection .activeSlide");
            var sectionIndex = $(".activeSection").index();
            var slideIndex = $(".activeSection .activeSlide").index();
                if(currentSlide.next(".slide").length == 0 || $("#fullpage .slide").is(":animated")){

                }else{
                    if(settings.beforeLoad == undefined){
    
                    }else{
                        settings.beforeLoad(sectionIndex, slideIndex);
                    }
                    if(settings.beforeSlideLoad == undefined){

                    }else{
                        settings.beforeSlideLoad(sectionIndex, slideIndex);
                    }
                    $("#fullpage .activeSection .slide").animate({
                        left:"-="+winW
                    },settings.animationDuration,settings.animationEasing,function(){
                        
                        currentSlide.removeClass("activeSlide");
                        currentSlide.next().addClass("activeSlide");
                        sectionIndex = $(".activeSection").index();
                        slideIndex = $(".activeSection .activeSlide").index();
                        reLayout();
                        setVerticalNavigator();
                        setHorizontalNavigator();
                        if(settings.touchMove == true){
                            setTouchPanel();
                        }
                        if(settings.afterSlideLoad == undefined){

                        }else{
                            settings.afterSlideLoad(sectionIndex, slideIndex);
                        }
                        if(settings.afterLoad == undefined){
    
                        }else{
                            settings.afterLoad(sectionIndex, slideIndex);
                        }
                    });
            }
        }
    }    
})