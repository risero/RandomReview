package com.gdgygj.randomreview.exception;

import com.gdgygj.randomreview.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 随机题库的异常处理类
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/29 16:42
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuestionBankException extends RuntimeException{

    private ExceptionEnum exceptionEnum;
}
