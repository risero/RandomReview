package com.gdgygj.randomreview.dao;

import com.gdgygj.randomreview.pojo.QuestionBank;
import com.gdgygj.randomreview.pojo.Review;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 随机题库DAO
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/20 8:51
 */
public interface IRandomReviewDAO {

    /**
     * 获取所有题目
     *
     * @return
     */
    List<Review> questionList(InputStream questionFile) throws IOException;

    /**
     * 获取所有题目
     *
     * @return
     */
    List<Review> questionList();

    /**
     * 返回题目总数
     *
     * @param questionFile
     * @return
     */
    Integer parseQuestionFile(InputStream questionFile) throws IOException;
}
