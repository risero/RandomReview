package com.gdgygj.randomreview.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 题目对象
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/19 10:14
 */
@Setter
@Getter
@ToString
public class Review {

    private Long id;
    private String questionType; // 题型
    private String questionStem; // 题干
    private String result; // 答案

    private String AOption; // A选项
    private String BOption; // B选项
    private String COption; // C选项
    private String DOption; // D选项
    private String EOption; // E选项
    private String FOption; // F选项
}
