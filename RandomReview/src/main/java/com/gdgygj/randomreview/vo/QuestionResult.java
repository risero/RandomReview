package com.gdgygj.randomreview.vo;

import lombok.*;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/4/8 10:26
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionResult {
	
    private Integer status;
    private String message;
    private Object result;

    public QuestionResult(Integer status, String message){
        this.status = status;
        this.message = message;
    }
}
