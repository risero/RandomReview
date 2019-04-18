package com.gdgygj.randomreview.service.impl;

import com.gdgygj.randomreview.dao.IRandomReviewDAO;
import com.gdgygj.randomreview.dao.impl.RandomReviewDAOImpl;
import com.gdgygj.randomreview.pojo.QuestionBank;
import com.gdgygj.randomreview.pojo.Review;
import com.gdgygj.randomreview.service.IRandomReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 随机题库Service
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/20 8:48
 */
@Service
public class RandomReviewServiceImpl implements IRandomReviewService {

    @Autowired
    private IRandomReviewDAO randomReviewDAO;

    /**
     * 随机出题数量
     *
     * @param maxSize
     * @param randomNumber
     * @return
     */
    public QuestionBank randomQuestionBank(InputStream questionFile, Integer randomCount) throws IOException {
        // 1、获取所有题目
        List<Review> reviewList = randomReviewDAO.questionList(questionFile);
        if(CollectionUtils.isEmpty(reviewList)){
            throw new RuntimeException("Excel文件中没有可以解析的题目");
        }
        // 2、获取从1~300道题目存取题库对象中
        Set<Review> reviewSet = new HashSet<>();
        reviewSet = setQuestionBank(randomCount, reviewList, reviewSet);
        reviewList = new ArrayList<>(reviewSet);

        // 3、设置题库对象
        QuestionBank questionBank = new QuestionBank();
        questionBank.setReviewList(reviewList);
        questionBank.setTotalCount(reviewList.size());
        // 4、返回题库对象
        return questionBank;
    }

    @Override
    public List<Review> reviewList() throws IOException {
        // 获取所有题目
        return randomReviewDAO.questionList();
    }

    @Override
    public QuestionBank questionResult(QuestionBank questionBank, Long questionId, String[] result, Long currentQuestion) {
        return null;
    }

    @Override
    public Integer parseQuestionFile(InputStream questionFile) throws IOException {
        Integer questionCount = randomReviewDAO.parseQuestionFile(questionFile);
        return questionCount;
    }

    @Override
    public Review getQuestionById(Long questionId) {
        Review review = randomReviewDAO.getQuestionById(questionId);
        return null;
    }

    /**
     * 随机指定范围内N个不重复的数
     * 利用HashSet的特征，只能存放不同的值
     *
     * @param randomNumber 随机数个数
     * @param reviewList 题库的题目
     * @param reviewSet 随机数结果集
     */
    private static Set<Review> setQuestionBank(int randomNumber, List<Review> reviewList, Set<Review> reviewSet) {
        int i = 0;
        while (true) {
            if (i < randomNumber) {
                // 减1是因为根据索引获取，如果获取到和总数一样大的值会索引越界
                int random = new Random().nextInt(reviewList.size());
                Review review = reviewList.get(random);
                // 判断不相同的则存起来
                if (!reviewSet.contains(review)) {
                    reviewSet.add(review);
                    i++;
                }
            }
            // 获取目前题目的长度
            int max = reviewSet.size();
            // 如果存的数据长度比随机题目的题目数量大则停止
            if (max >= randomNumber) {
                return reviewSet;
            }
        }
    }
}
