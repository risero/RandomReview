package com.gdgygj.randomreview.advice;

import com.gdgygj.randomreview.enums.ExceptionEnum;
import com.gdgygj.randomreview.exception.QuestionBankException;
import com.gdgygj.randomreview.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.xml.ws.Response;

/**
 * 全异常处理
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/29 16:48
 */
@ControllerAdvice
public class commonExceptionhandler {

    /**
     * 随机题库异常处理器
     */
    @ExceptionHandler(QuestionBankException.class) // 以后捕获自定义的异常就可以了
    public ResponseEntity<ExceptionResult> handlerException(QuestionBankException e){
        ExceptionEnum em = e.getExceptionEnum();
        return ResponseEntity.status(em.getCode()).body(new ExceptionResult(em));
    }
}
