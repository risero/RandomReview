/**
 * Created by dell on 2019/3/21.
 */
$(function () {
    // 固定选项的长度
    if($(".result-option").height() < 250){
        $(".result-option").height(250);
    }

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

    /* 展示答题卡 */
    $(".answer-sheet-box").on("click", function () {
        var pageBox = $(".answer-sheet-number li");
        pageBox.slideToggle(500);
    });

    /* 提交题目的正确答案 */
    $(".question-submit").on("click", function () {
        if($(".result-option li").hasClass("result-selected")){
            $results = $(".result-selected input");
            $questionId = $("#questionId").data("id");
            var commitResultArr = new Array();
            $.each($results, function (index, item) {
                // 获取需要提交的答案
                var $result = $(item).attr("name").substring(0, 1);
                commitResultArr[index] = $result;
            });

            $.ajax({
                url: "/questionResult",
                type: "POST",
                data: "questionId=" + $questionId + "&results=" + commitResultArr,
                success: function (data) {
                    if(data.status == 400){
                        location.href = "index";
                    }
                    // 重复提交答案，跳转到下一道题
                    if(data.status == 408){
                        $(".result-option li").unbind();
                        alert(data.message);
                        // 跳转到下一页
                        var nextQuestionPath = $(".next").attr("href");
                        location.href = nextQuestionPath;
                        return;
                    }
                    if(data.status == 403){
                        $(".result-option li").unbind();
                        alert(data.message);
                        location.href = data.result;
                        return;
                    }
                    var results = data.result;
                    $("#questionResult").text(results);

                    // 对错误的答案的选项框进行红色的高亮
                    if(!equlasResult(data.result, commitResultArr)){
                        var errorColor = "#F44336";
                        setResultOptionColor(".result-selected", errorColor);
                    }

                    // 把所有正确的答案转换为数组
                    var resultOptions = new Array();
                    var options = $(".option");
                    for (var k = 0; k < data.result.length; k++) {
                        resultOptions[k] = data.result[k]+"Option";
                    }

                    // 对后台正确答案的选项框进行绿色的高亮
                    var successResultArr = new Array();
                    for (var j = 0; j < options.length; j++) {
                        var option = options[j];
                        if(resultOptions.indexOf(option.getAttribute("name")) > -1){
                            successResultArr[j] = option;
                            // 设置颜色
                            setResultOptionColor($(option).parents("li"), "#1abc9c");
                        }
                    }
                    $(".result-option li").unbind();
                }
            });
        }
    });

    /**
     * 设置答案选项的颜色
     */
    function setResultOptionColor(ele, color) {
        $(ele).css("borderColor", color);
        $(ele).find($(".icons")).css("borderColor", color);
        $(ele).find("i").show();
        $(ele).find("i").css("backgroundColor", color);
        $(ele).find("pre").css("color", color);
    }

    /**
     * 判断答案是否正确
     *
     * @param resultArr
     * @param commitResultArr
     * @returns {boolean}
     */
    function equlasResult(resultArr, commitResultArr){
        var tempArr = new Array();
        var count = 0;
        if(resultArr.length != commitResultArr.length){
            return false;
        }
        for (var i = 0; i < resultArr.length; i++) {
            if(resultArr.indexOf(commitResultArr[i]) > -1 && tempArr.indexOf(commitResultArr[i]) <= -1){
                count++;
                tempArr[i] = commitResultArr[i];
            }
        }
        if(count == resultArr.length && count > 0){
            return true;
        }
        return false;
    }
});