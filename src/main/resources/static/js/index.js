/**
 * Created by dell on 2019/3/21.
 */
$(function () {
    // 固定选项的长度
    if($(".result-option").height() < 250){
        $(".result-option").height(250);
    }
    // 加载答案
   /* $.ajax({
        "method": "post",
        "url": "/random",
        success: function (data) {
            var review = data.reviewList[0];
            var totalCount = data.totalCount;
            var currentQuestion = data.currentQuestion;
        }
    });*/

    // 单选题的点击操作
    $(".result-option li").on("click", function (e) {
        $(this).attr("style", "");
        var questionType = $(".question-type").text();
        if(questionType === '多选题'){
            if($(this).hasClass("result-selected")){
                // 取消效果
                $(this).removeClass("result-selected");
                $(this).find(".icons").css("borderColor", "#ddd").children("i").css("display", "none");
            } else {
                $(this).addClass("result-selected");
                $(this).find(".icons").css("borderColor", "#25bb9b").children("i").css("display", "block");
                $(this).find("pre").css("backgroundColor", "#FFF").css("color", "#333");
            }
        } else {
            $(this).addClass("result-selected").siblings("li").removeClass("result-selected");
            $(this).siblings(".question-item").find(".icons").css("borderColor", "#ddd").children("i").css("display", "none");
            $(this).find(".icons").css("borderColor", "#25bb9b").children("i").css("display", "block");
            $(this).find("pre").css("backgroundColor", "#FFF").css("color", "#333");
        }
        return false;
    });

// 鼠标停留在选项中
    $(".result-option li").on("mouseenter", function () {
        if($(this).hasClass("result-selected")){
            return;
        }
        $(this).css("backgroundColor", "#f3f3f3").css("borderColor", "#909090").find("pre").css("backgroundColor", "#f3f3f3");
        $(this).find(".icons").css("borderColor", "#1abc9c").css("backgroundColor", "#FFF");
        $(this).find("pre").css("color", "#1abc9c");
    });

// 鼠标离开选项
    $(".result-option li").on("mouseleave", function () {
        if($(this).hasClass("result-selected")){
            return;
        }
        $(this).css("backgroundColor", "#FFF").css("borderColor", "#DDD").find("pre").css("backgroundColor", "#FFF");
        $(this).find(".icons").css("borderColor", "#ddd").css("backgroundColor", "#FFF");
        $(this).find("pre").css("color", "#333");
    });

    /* 提交题目的正确答案 */
    $(".question-submit").on("click", function () {
        if($(".result-option li").hasClass("result-selected")){
            // 提交正确答案
            alert("提交正确答案");
        }
    });
});