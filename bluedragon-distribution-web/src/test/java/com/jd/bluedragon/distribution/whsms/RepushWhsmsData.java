package com.jd.bluedragon.distribution.whsms;

import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.ui.Model;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wuzuxiang on 2017/9/24.
 */
public class RepushWhsmsData {

    /**
     * Excel 2003
     */
    private final static String XLS = "xls";
    /**
     * Excel 2007
     */
    private final static String XLSX = "xlsx";
    /**
     * 分隔符
     */
    private final static String SEPARATOR = "|";

    @Test
    public void rePush(){
        String path = "C:\\Users\\wuzuxiang\\Documents\\Tencent Files\\260232808\\FileRecv\\9.24异常汇总扫描数据.xls";

        String url = "http://dms.etms.jd.com/systemLog/list?keyword1=";
        String pushUrl = "http://dms.etms.jd.com/services/delivery/pushWhemsWaybill";

        String request = "";

        RestTemplate restTemplate1 = null;

        SimpleClientHttpRequestFactory requestFactory1 = new SimpleClientHttpRequestFactory();
        requestFactory1.setReadTimeout(3000);
        requestFactory1.setConnectTimeout(3000);
        restTemplate1 = new RestTemplate();
        restTemplate1.setRequestFactory(requestFactory1);
        restTemplate1.setErrorHandler(new DefaultResponseErrorHandler());

        try {
            List<String> listS= exportListFromExcel(new File(path),0);
            List<String> waybilllist = new ArrayList<String>();
            String waybill = "";
            for(int i=0;i<1;i++){
                int from = 1;
                int end = listS.get(i).indexOf("-") < 0 ?
                        (listS.get(i).indexOf("N") < 0 ? listS.get(i).length() : listS.get(i).indexOf("N"))
                        : listS.get(i).indexOf("-");
                waybill = listS.get(i).substring(from,end);//获取运单号

                //打印读取信息
                System.out.println(listS.get(i));
                System.out.println(waybill);

                request = url + waybill;
                //判断是否已经推送了数据
                boolean bool = Boolean.FALSE;
                ResponseEntity<Model> responseEntity = restTemplate1.getForEntity(new URI(request), Model.class);
                if (responseEntity.getStatusCode() == HttpStatus.OK ){
                    Map<String ,Object> map =  responseEntity.getBody().asMap();
                    List<SystemLog> systemLogs = (List<SystemLog>) map.get("systemlogs");
                    for (int j = 0 ; j< systemLogs.size() ; j ++ ){
                        if ("0".equals(systemLogs.get(i).getKeyword4())){
                            bool = Boolean.TRUE;
                        }
                    }
                }

                if (!bool) {
                    HttpHeaders header = new HttpHeaders();
                    header.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
                    header.add("Accept", MediaType.APPLICATION_JSON.toString());
                    HttpEntity<String> formEntity = new HttpEntity<String>(waybill, header);
                    ResponseEntity result = restTemplate1.postForEntity(url, formEntity, Object.class);
                    if (result.getStatusCode() == HttpStatus.OK) {
                        System.out.println(waybill + "重新推送成功！" );
                    }
                }



            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
    }

    /**
     * 由Excel流的Sheet导出至List
     *
     * @param is
     * @param extensionName
     * @param sheetNum
     * @return
     * @throws IOException
     */
    public static List<String> exportListFromExcel(InputStream is,
                                                   String extensionName, int sheetNum) throws IOException {

        Workbook workbook = null;

        if (extensionName.toLowerCase().equals(XLS)) {
            workbook = new HSSFWorkbook(is);
        } else if (extensionName.toLowerCase().equals(XLSX)) {
            workbook = new XSSFWorkbook(is);
        }

        return exportListFromExcel(workbook, sheetNum);
    }

    /**
     * 由Excel文件的Sheet导出至List
     *
     * @param file
     * @param sheetNum
     * @return
     */
    public static List<String> exportListFromExcel(File file, int sheetNum)
            throws IOException {
        return exportListFromExcel(new FileInputStream(file),
                FilenameUtils.getExtension(file.getName()), sheetNum);
    }

    private static List<String> exportListFromExcel(Workbook workbook,
                                                    int sheetNum) {

        Sheet sheet = workbook.getSheetAt(sheetNum);

        // 解析公式结果
        FormulaEvaluator evaluator = workbook.getCreationHelper()
                .createFormulaEvaluator();

        List<String> list = new ArrayList<String>();

        int minRowIx = sheet.getFirstRowNum();
        int maxRowIx = sheet.getLastRowNum();
        for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
            Row row = sheet.getRow(rowIx);
            StringBuilder sb = new StringBuilder();

            short minColIx = row.getFirstCellNum();
            short maxColIx = row.getLastCellNum();
            for (short colIx = minColIx; colIx <= maxColIx; colIx++) {
                Cell cell = row.getCell(new Integer(colIx));
                CellValue cellValue = evaluator.evaluate(cell);
                if (cellValue == null) {
                    continue;
                }
                // 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
                // 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
                switch (cellValue.getCellType()) {
                    case Cell.CELL_TYPE_BOOLEAN:
                        sb.append(SEPARATOR + cellValue.getBooleanValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        // 这里的日期类型会被转换为数字类型，需要判别后区分处理
                        if (DateUtil.isCellDateFormatted(cell)) {
                            sb.append(SEPARATOR + cell.getDateCellValue());
                        } else {
                            //把手机号码转换为字符串
                            DecimalFormat df = new DecimalFormat("#");
                            sb.append(SEPARATOR + df.format(cellValue.getNumberValue()));
                        }
                        break;
                    case Cell.CELL_TYPE_STRING:
                        sb.append(SEPARATOR + cellValue.getStringValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        break;
                    case Cell.CELL_TYPE_ERROR:
                        break;
                    default:
                        break;
                }
            }
            list.add(sb.toString());
        }
        return list;
    }
}
