package com.gdgygj.randomreview.controller;

import com.gdgygj.randomreview.enums.ExceptionEnum;
import com.gdgygj.randomreview.pojo.QuestionBank;
import com.gdgygj.randomreview.pojo.Review;
import com.gdgygj.randomreview.service.IRandomReviewService;
import com.gdgygj.randomreview.vo.QuestionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

/**
 * 随机复习题库Controller
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/20 8:40
 */
@Controller
@RequestMapping("/")
public class RandomReviewController {

    @Autowired
    private IRandomReviewService randomReviewService;

    /**
     * 进入主页
     *
     * @return
     */
    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    /**
     * 解析上传的题目文件返回题库对象
     *
     * @return
     */
    @RequestMapping(value = "/questionUpload", method = {RequestMethod.POST})
    @ResponseBody
    public QuestionResult parseQuestionGetCount(MultipartFile file){
        // 判断文件是否为空
        QuestionResult result = null;
        if(file == null && file.isEmpty()){
            // 文件未上传
            result = new QuestionResult(404, "没有上传的文件");
            return result;
        }

        // 判断文件类型
        String[] allowTypes = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        if(!Arrays.asList(allowTypes).contains(file.getContentType())){
            result = new QuestionResult(400, "请上传Excel文件");
            return result;
        }

        // 获取题目总数
        Integer questionCount = 0;
        try {
            questionCount = randomReviewService.parseQuestionFile(file.getInputStream());
            if(questionCount <= 0){
                // 没有题目
                result = new QuestionResult(500, "上传的Excel文件没有可解析的题目");
            }
            // 设置题库对象
            QuestionBank questionBank = new QuestionBank();
            questionBank.setTotalCount(questionCount);
            String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf("."));
            questionBank.setQuestionBankName(fileName);
            // 返回题库对象
            result = new QuestionResult(200, "上传成功", questionBank);
            return result;
        } catch (Exception e) {
            // 返回错误信息
            result = new QuestionResult(500, "文件解析失败!");
            return result;
        }
    }

    /**
     * 根据提交的数据，随机出指定数量的题目
     *
     * @return
     */
    @RequestMapping(value = "/question", method = {RequestMethod.POST})
    public String randomQuestionBank(@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) throws IOException {
        // TODO 未做完
        System.out.println(file);
//        System.out.println(questionBank);
        // 判断文件是否为空
        if(file == null && file.isEmpty()){
            // 文件未上传
            return null;
        }

        // 判断文件类型
        String[] allowTypes = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        if(!Arrays.asList(allowTypes).contains(file.getContentType())){
            return "index";
        }

        // 传到dao层进行解析题目，返回题目总数
        Integer questionCount = 0;
        try {
            questionCount = randomReviewService.parseQuestionFile(file.getInputStream());
            if(questionCount <= 0){
                // 没有题目
                return "index";
            }
        } catch (Exception e) {
            // 返回错误信息
            System.out.println("解析失败");
            return "index";
        }
        System.out.println("题目总数：" + questionCount);

        // 设置随机的题目数量
       /* if(randomCount == null || randomCount <= 0){
            randomCount = questionCount;
        }

        // 返回存好随机题目的题库对象
        QuestionBank questionBank = randomReviewService.randomQuestionBank(file.getInputStream(), randomCount);

        session.setAttribute("questionBank", questionBank);*/
        return "index";
    }

    /**
     * 判断提交的题目答案
     *
     * @param session
     * @param questionId 题目id
     * @param result 提交的题目答案
     * @param currentQuestion 当前题目数
     * @return 返回题目的正确答案
     */
    @RequestMapping("/questionResult")
    @ResponseBody
    public QuestionBank questionResult(HttpSession session, Long questionId, String[] result,
          @RequestParam(defaultValue = "1", required = true) Long currentQuestion){
        QuestionBank questionBank = (QuestionBank)session.getAttribute("questionBank");
        questionBank = randomReviewService.questionResult(questionBank, questionId, result, currentQuestion);
        return questionBank;
    }
}
