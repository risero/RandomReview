package com.gdgygj.randomreview.controller;

import com.gdgygj.randomreview.pojo.QuestionBank;
import com.gdgygj.randomreview.pojo.Review;
import com.gdgygj.randomreview.service.IRandomReviewService;
import com.gdgygj.randomreview.vo.QuestionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

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
     * 题库Session名称
     */
    private static final String QUESTION_BANK_SESSSION = "questionBank";

    private static final int SESSION_MAX_INACTIVE_INTERVAL = 1 * 60 * 60 * 24;

    private static final String QUESTION_RESULT_SESSION = "result";

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
    @RequestMapping(value = "/test", method = {RequestMethod.POST})
    public String randomQuestionBank(@RequestParam(value = "questionFile", required = false) MultipartFile file,
                                     QuestionBank questionBank, HttpSession session, Model model) throws IOException {
        // 判断文件是否为空
        if(file == null || file.isEmpty()){
            // 文件未上传
            QuestionResult result = new QuestionResult(404, "请上传Excel文件");
            model.addAttribute("result", result);
            return "redirect:/index";
        }

        // 判断文件类型
        String[] allowTypes = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        if(!Arrays.asList(allowTypes).contains(file.getContentType())){
            QuestionResult result = new QuestionResult(400, "上传的文件类型有误");
            model.addAttribute("result", result);
            return "redirect:/index";
        }

        // 判断表单的数据
        if(StringUtils.isEmpty(questionBank.getQuestionBankName())){
            questionBank.setQuestionBankName("联想技术岗位试题");
        }

        // 传到dao层进行解析题目，返回题目总数
        Integer questionCount = 0;
        try {
            questionCount = randomReviewService.parseQuestionFile(file.getInputStream());
            // 解析的Excel没有题目
            if(questionCount <= 0){
                QuestionResult result = new QuestionResult(404, "Excel文件中没有可以解析的题目");
                model.addAttribute("result", result);
                return "redirect:/index";
            }

            // 用户输入的出题数不能大于题目总数
            if(questionBank.getQuestionRandomNumber() > questionCount){
                QuestionResult result = new QuestionResult(500, "随机出题数不能大于题目总数");
                model.addAttribute("result", result);
                return "redirect:/index";
            }

            // 判断随机出题数过少
            if(questionBank.getQuestionRandomNumber() == null || questionBank.getQuestionRandomNumber() <= 0){
                QuestionResult result = new QuestionResult(500, "随机出题数过少");
                model.addAttribute("result", result);
                return "redirect:/index";
            }

            // 删除之前题库的 session
            session.removeAttribute(QUESTION_BANK_SESSSION);
            session.removeAttribute(QUESTION_RESULT_SESSION);

            // 开始出题
            QuestionBank resultQuestionBank = randomReviewService.randomQuestionBank(file.getInputStream(), questionBank.getQuestionRandomNumber());
            // 封装题库对象
            questionBank.setReviewList(resultQuestionBank.getReviewList());
            questionBank.setTotalCount(resultQuestionBank.getTotalCount());
            questionBank.setCurrentQuestion(1);
            questionBank.setOver(false);

            Review currentQuestion = questionBank.getReviewList().get(questionBank.getCurrentQuestion());
            questionBank.setCurrentReview(currentQuestion);
            session.setAttribute(QUESTION_BANK_SESSSION, questionBank);
            session.setMaxInactiveInterval(SESSION_MAX_INACTIVE_INTERVAL);

            return "redirect:/question";
        } catch (Exception e) {
            // 返回错误信息
            QuestionResult result = new QuestionResult(500, "Excel文件解析失败");
            model.addAttribute("result", result);
            return "redirect:/index";
        }
    }

    /**
     * 显示题目
     *
     * @return
     */
    @RequestMapping("/question")
    public String question(@RequestParam(defaultValue = "0") Integer currentQuestion, HttpSession session){
        QuestionBank questionBank = (QuestionBank)session.getAttribute(QUESTION_BANK_SESSSION);
        if(questionBank == null || questionBank.isOver()){
            // 题目对象为空 或者 已考完试
            return "redirect:/index";
        }

        // 判断当前题目数超过最大出题数
        Integer questionRandomNumber = questionBank.getQuestionRandomNumber();

        // 如果请求的题目数大于最大题目数，我们就跳转到result页面，提交答卷
        if(currentQuestion > questionRandomNumber){
            return "redirect:/result";
        }
        // 判断当前题目数小于最大题目数
        if(currentQuestion <= 0){
            Review review = questionBank.getReviewList().get(0);
            questionBank.setCurrentReview(review);
            questionBank.setCurrentQuestion(1);
            return "question";
        }

        // 根据题目id查询题目对象，设置题目对象
        Review review = questionBank.getReviewList().get(currentQuestion - 1); // 因为索引从0开始，所以减一
        questionBank.setCurrentQuestion(currentQuestion);
        questionBank.setCurrentReview(review);

        // 响应视图
        return "question";
    }

    /**
     * 判断提交的题目答案是否正确
     *
     * @param session
     * @param questionId 题目id
     * @param resultList 提交的题目对应的答案
     * @return 返回题目的正确答案
     */
    @RequestMapping("/questionResult")
    @ResponseBody
    public QuestionResult questionResult(HttpSession session,
                                         @RequestParam("questionId") Long questionId,
                                         @RequestParam("results") List<String> resultList){

        // 判断当前题目id是否为空, 判断答案是否为空
        QuestionResult result = null;
        Review review = null;

        // 根据question查找题目对象
        QuestionBank questionBank = (QuestionBank) session.getAttribute(QUESTION_BANK_SESSSION);
        if(questionBank == null){
            result = new QuestionResult(400, "请求已过期");
            return result;
        }

        // 已答完试卷
        if(questionBank.isOver()){
            result = new QuestionResult(403, "已提交试卷无法继续答题", "result");
            return result;
        }

        // 根据id查询题目对象
        for (Review item : questionBank.getReviewList()) {
            if(item.getId().equals(questionId)){
                review = item;
            }
        }

        // 根据id查询不到对象
        if(review == null){
            result = new QuestionResult(404, "找不到题目对应的答案");
            return result;
        }

        // 判断请求的数据
        if(questionId == null || resultList == null || resultList.isEmpty()){
            // 响应错误信息
            result = new QuestionResult(404, "请正确的提交数据");
            return result;
        }

        // 还没有回答过题目，已回答过该题目
        if(!CollectionUtils.isEmpty(questionBank.getHasResult()) && questionBank.getHasResult().contains(review)){
            result = new QuestionResult(408, "不能重复答题");
            return result;
        }

        // 提交的答案和题目的答案完全一致，答案的长度也完全一致
        // 题目的答案
        String questionResult = review.getResult();
        if(StringUtils.isEmpty(questionResult)){
            // 文件的答案有误
            result = new QuestionResult(404, "找不到题目对应的答案");
            return result;
        }
        String[] split = new String[questionResult.length()];
        for (int i = 0; i < questionResult.length(); i++) {
            split[i] = String.valueOf(questionResult.charAt(i));
        }

        List<String> questionResultList = Arrays.asList(split);
        List<String> tempResult = new ArrayList<String>();

        // 回答错误
        if(questionResultList.size() != resultList.size()){
            result = new QuestionResult(500, "回答错误", questionResultList);
            questionBank.setErrorCount(questionBank.getErrorCount() + 1);
            questionBank.getHasResult().add(review);
            return result;
        }
        int count = 0;
        for (int i = 0; i < questionResultList.size(); i++) {
            if(questionResultList.contains(resultList.get(i)) && !tempResult.contains(resultList.get(i))){
                count++;
                tempResult.add(resultList.get(i));
            }
        }
        if(count == questionResultList.size()){
            result = new QuestionResult(200, "回答正确", questionResultList);
            questionBank.setSuccessCount(questionBank.getSuccessCount() + 1);
            questionBank.getHasResult().add(review);
            return result;
        }
        result = new QuestionResult(500,"回答错误", questionResultList);
        questionBank.setErrorCount(questionBank.getErrorCount() + 1);
        questionBank.getHasResult().add(review);
        return result;
    }

    /**
     * 返回考试结果
     *
     * @param session
     * @return
     */
    @RequestMapping("/result")
    public String countQuestionBank(HttpSession session){
        QuestionBank questionBank = (QuestionBank) session.getAttribute(QUESTION_BANK_SESSSION);
        if(questionBank == null){
            return "redirect:/index";
        }

        if(questionBank.isOver()){
            return "/result";
        }

        // 返回错误的题目数 和 正确的题目数
        int successCount = questionBank.getSuccessCount();
        int errorCount = questionBank.getErrorCount();

        Map<String, Integer> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("errorCount", errorCount);
        result.put("questionTotalCount", questionBank.getQuestionRandomNumber());
        questionBank.setOver(true); // 考完后

        session.setAttribute(QUESTION_RESULT_SESSION, result);
        return "/result";
    }
}
