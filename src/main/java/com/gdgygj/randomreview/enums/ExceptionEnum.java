package com.gdgygj.randomreview.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 异常处理枚举类
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/29 16:51
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {

    QUESTIONBANK_TOO_MUCH(500, "随机出题的数量不能超过题目数量!"),

    ;

    private int code;
    private String message;
}
