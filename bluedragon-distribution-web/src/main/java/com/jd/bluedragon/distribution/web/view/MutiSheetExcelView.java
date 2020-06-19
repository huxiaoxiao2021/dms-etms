package com.jd.bluedragon.distribution.web.view;

import com.jd.ldop.utils.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 默认的Excel视图
 *
 * @author suihonghua
 */
public class MutiSheetExcelView extends AbstractExcelView {

    private static final Logger log = LoggerFactory.getLogger(MutiSheetExcelView.class);
    List<Object> heads;

    public MutiSheetExcelView(List<Object> heads) {
        this.heads = heads;
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        try {
            String filename = (String) map.get("filename");
            if(filename == null || filename.trim().length() == 0) {
                filename = System.currentTimeMillis() + ".csv";
            }
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));// 设定输出文件头
//            OutputStream os = response.getOutputStream();
//            BufferedOutputStream buff = new BufferedOutputStream(os);
//            response.setHeader("Content-Type", "application/ms-txt.numberformat:@");
//            String result=buildCsv(map,null);
//            buff.write(result.getBytes("GBK"));
//            buff.flush();
//            buff.close();
//            os.close();
            this.buildExcelDocument(map, workbook);
        } catch (Exception e) {
            log.error("buildExcelDocument-error:", e);
            throw e;
        }
    }

    /**
     * 构建Excel文本
     *
     * @param map
     * @param workbook
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook) throws Exception {
        String sheetname = (String) map.get("sheetname");

        if(sheetname == null || sheetname.trim().length() == 0) {
            sheetname = "Sheet";
        }

        List<List<Object>> contents = (List<List<Object>>) map.get("contents");
        if(contents == null) {
            throw new RuntimeException("DefaultExcelView Attribute[contents] in Model can't be null! ");
        }
        long beginTime = System.currentTimeMillis();
        log.info("写入excel开始，执行时间：{}", beginTime);
        Integer perSheetNum = 50000;
        ExcelWriter exporter = new ExcelWriter();
        exporter.setWorkbook(workbook);
        Integer sheetCount = contents.size() / perSheetNum + (contents.size() % perSheetNum == 0 ? 0 : 1);

        for(Integer i = 0; i < sheetCount; i++) {
            int startIndex = i * perSheetNum;
            int endIndex = (i + 1) * perSheetNum;
            if(endIndex >= contents.size()) {
                endIndex = contents.size();
            }
            List<List<Object>> subContent = contents.subList(startIndex, endIndex);
            subContent.add(0, heads);
            exporter.writeSheet(sheetname + (i + 1), subContent);
        }
        log.info("写入excel结束，用时：{}", System.currentTimeMillis() - beginTime);
    }

    /**
     * 构建Excel文本
     *
     * @param map
     * @param workbook
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected String buildCsv(Map<String, Object> map, HSSFWorkbook workbook) throws Exception {

        List<List<Object>> contents = (List<List<Object>>) map.get("contents");
        if(contents == null) {
            throw new RuntimeException("DefaultExcelView Attribute[contents] in Model can't be null! ");
        }
        long beginTime = System.currentTimeMillis();
        log.info("写入excel开始，执行时间：{}", beginTime);
        StringBuilder stringBuilder = new StringBuilder();
        if(!CollectionUtils.isEmpty(contents)) {
            for(List<Object> list : contents) {
                for(Object item : list) {
                    stringBuilder.append(item).append(",");
                }
                stringBuilder.append("\r\n");
            }
        }
        log.info("写入excel结束，用时：{}", System.currentTimeMillis() - beginTime);
        return stringBuilder.toString();

    }
}
