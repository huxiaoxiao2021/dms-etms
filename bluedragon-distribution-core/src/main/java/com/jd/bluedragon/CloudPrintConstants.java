package com.jd.bluedragon;

import java.io.Serializable;

/**
 * 云打印常量类
 *
 * @author hujiping
 * @date 2023/8/3 11:26 AM
 */
public class CloudPrintConstants implements Serializable {

    // 成功
    public static final Integer PRINT_CODE_SUC = 1000;
    
    // 模版尺寸：100x50
    public static final String TEMPLATE_SIZE_1005 = "100x50";
    
    // 模版尺寸：100x100
    public static final String TEMPLATE_SIZE_1010 = "100x100";
    
    // 打印类型：换单打印
    public static final Integer PRINT_TYPE_EXCHANGE = 1;
    
    // 打印类型：包裹补打
    public static final Integer PRINT_TYPE_REPRINT = 2;

    // 输出方式配置可选的值，0(Printer，输出到打印机)，1(File，输出到文件)
    public static final Integer OUTPUT_TYPE_FILE = 1;

    // 输出到本地文件格式，PDF-1，手持贴标机专用-2，蓝牙打印指令集-3。图片-4
    public static final Integer FILE_FORMAT_PDF = 1;

    // 1-URL；2-base64字符串；3-ESC/POS；4-CPCL ；5-cpcl指令打印BMP；6-tspl；7-tspl指令打印BMP
    public static final Integer DATA_FORMAT_URL = 1;

    // 单据码类型：运单号-waybillCode；包裹号-packageCode
    public static final String BILL_CODE_TYPE_WAYBILL = "waybillCode";
    public static final String BILL_CODE_TYPE_PACK = "packageCode";
}
