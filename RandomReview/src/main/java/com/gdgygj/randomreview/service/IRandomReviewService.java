package com.gdgygj.randomreview.service;

import com.gdgygj.randomreview.pojo.QuestionBank;
import com.gdgygj.randomreview.pojo.Review;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 随机题库接口
 */
public interface IRandomReviewService {

    /**
     * 随机出题
     *
     * @return
     */
    QuestionBank randomQuestionBank(InputStream questionFile, Integer randomCount) throws IOException;

    /**
     * 获取所有文件中的题目
     *
     * @return
     */
    List<Review> reviewList() throws IOException;

    /**
     * 判断题目答案
     *
     * @param questionBank
     * @param questionId
     * @param result
     * @param currentQuestion
     * @return
     */
    QuestionBank questionResult(QuestionBank questionBank, Long questionId, String[] result, Long currentQuestion);

    /**
     * 解析上传的题目xls文件
     *
     * @param questionFile 题目xls文件
     * @return 返回题目总数
     */
    Integer parseQuestionFile(InputStream questionFile) throws IOException;

    /**
     * 根据id查询题目对象
     *
     * @param questionId
     * @return
     */
    Review getQuestionById(Long questionId);
}
