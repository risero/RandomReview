package com.gdgygj.randomreview.dao.impl;

import com.gdgygj.randomreview.dao.IRandomReviewDAO;
import com.gdgygj.randomreview.pojo.Review;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 随机题库的数据库访问层
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/3/20 8:51
 */
@Repository
public class RandomReviewDAOImpl implements IRandomReviewDAO {

    @Getter
    private List<Review> reviewList;

    /**
     * 获取的所有题目
     *
     * @return
     * @throws IOException
     */
    public List<Review> questionList(InputStream questionFile) throws IOException {
        // 创建workboot，传入xls文件
        XSSFWorkbook workboot = new XSSFWorkbook(questionFile);

        // 存储每一行题目对象
        List<Review> reviewList = new ArrayList<Review>();

        int rowCount = 0;

        // 迭代每一个选项卡sheet
        for (int i = 0; i < workboot.getNumberOfSheets(); i++) {
            // 获取每一个sheet
            XSSFSheet sheet = workboot.getSheetAt(i);
            // 不存在sheet则跳过循环
            if(sheet == null){
                continue;
            }
            // 循环获取每一个sheet的每一行数据，从第一行到最后一行遍历
            for (int j = 0; j <= sheet.getLastRowNum(); j++) {
                rowCount = j;
                // 获取每一行数据
                XSSFRow row = sheet.getRow(j);

                // 设置数据
                setData(row, reviewList, j);
            }
        }
        // 删除第0个元素
        reviewList.remove(0);
        this.reviewList = reviewList;
        return reviewList;
    }

    /**
     * 获取的所有题目
     *
     * @return
     */
    @Override
    public List<Review> questionList() {
        return this.reviewList;
    }

    @Override
    public Integer parseQuestionFile(InputStream questionFile) throws IOException {
        return questionList(questionFile).size();
    }

    @Override
    public Review getQuestionById(Long questionId) {
        List<Review> reviewList = getReviewList();
        for (Review review : reviewList) {
            if(review.getId() == questionId){
                return review;
            }
        }
        return null;
    }

    /**
     * 设置Review对象的属性
     *
     * @param row 每一行xls的数据
     * @param reviewList 题目集合
     * @param id 题目id
     */
    private static void setData(XSSFRow row, List<Review> reviewList, int id) {
        Review r = new Review();
        if(id > 0){
            r.setId(Long.valueOf(id));
        }
        XSSFCell cell = row.getCell(0);
        String cellValue = getCellValue(cell);
        r.setQuestionType(cellValue);

        cell = row.getCell(1);
        cellValue = getCellValue(cell);
        r.setQuestionStem(cellValue);

        cell = row.getCell(2);
        cellValue = getCellValue(cell);
        r.setResult(cellValue);

        cell = row.getCell(3);
        cellValue = getCellValue(cell);
        r.setAOption(cellValue);

        cell = row.getCell(4);
        cellValue = getCellValue(cell);
        r.setBOption(cellValue);

        cell = row.getCell(5);
        cellValue = getCellValue(cell);
        r.setCOption(cellValue);

        cell = row.getCell(6);
        cellValue = getCellValue(cell);
        r.setDOption(cellValue);

        cell = row.getCell(7);
        cellValue = getCellValue(cell);
        r.setEOption(cellValue);

        cell = row.getCell(8);
        cellValue = getCellValue(cell);
        r.setFOption(cellValue);
        // 把每个题目对象封装到Review集合中
        reviewList.add(r);
    }

    /**
     * 处理每一行数据的类型都转为String类型
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        String cellValue = null;
        if (cell != null) {
            // 判断数据的类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC: // 数字
                    cellValue = String.valueOf((int)cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING: // 字符串
                    if(StringUtils.isEmpty(cellValue)){
                        cellValue = String.valueOf(cell.getStringCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN: // Boolean
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR: // 故障
                    cellValue = "非法字符";
                    break;
                /*default:
                    cellValue = null;
                    break;*/
            }
        }
        return cellValue;
    }
}
