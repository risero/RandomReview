package com.gdgygj.randomreview.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 题库对象
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/20 9:59
 */
@Setter
@Getter
@ToString
public class QuestionBank {

    private List<Review> reviewList; // 题目对象集合

    private Integer totalCount; // 题目总数

    private Integer currentQuestion; // 当前题目

    private String questionBankName; // 题库名

    private Integer questionRandomNumber; // 随机出题数
}
