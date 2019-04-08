package com.gdgygj.randomreview.vo;

import com.gdgygj.randomreview.enums.ExceptionEnum;
import lombok.Data;

/**
 * 异常处理对象
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/29 16:53
 */
@Data
public class ExceptionResult {
    private int status;
    private String message;
    private Long timestamp;

    public ExceptionResult(ExceptionEnum em){
        this.status = em.getCode();
        this.message = em.getMessage();
        this.timestamp = System.currentTimeMillis(); // 获取当前时间的毫秒数
    }
}
