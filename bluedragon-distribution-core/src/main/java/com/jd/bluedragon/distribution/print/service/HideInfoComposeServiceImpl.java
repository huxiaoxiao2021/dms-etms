package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 处理部分单子的客户信息打印隐藏
 * Created by shipeilin on 2017/9/21.
 */
public class HideInfoComposeServiceImpl implements  ComposeService {

    private static final Log log = LogFactory.getLog(HideInfoComposeServiceImpl.class);

    private static final String  customerName = "***";        //客户姓名以3个*字符替代
    private static final String  customerContacts = "******";//电话全隐藏，以6个*字符替代
    private static final String  printAddress = "******";    //客户地址全隐藏，以6个*字符替代


    @Override
    public void handle(PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
        String waybillSign  = waybill.getWaybillSign();
        //完美项目取件逆向隐藏面单服务：waybillSign第37位为0，不隐藏
        if(StringUtils.isBlank(waybillSign) || waybillSign.length() < 37 || "0".equals(waybillSign.charAt(36))){
            return;
        }

        char flag = waybillSign.charAt(36);
        switch(flag) {
            case '1':
                //1、隐藏姓名
                waybill.setCustomerName(customerName);
                break;
            case '2':
                //2、隐藏电话
                waybill.setCustomerContacts(customerContacts);
                break;
            case '3':
                //3、隐藏姓名 + 隐藏电话
                waybill.setCustomerName(customerName);
                waybill.setCustomerContacts(customerContacts);
                break;
            case '4':
                //4、隐藏地址
                waybill.setPrintAddress(printAddress);
                break;
            case '5':
                //5、隐藏姓名 + 隐藏地址
                waybill.setCustomerName(customerName);
                waybill.setPrintAddress(printAddress);
                break;
            case '6':
                //6、隐藏电话 + 隐藏地址
                waybill.setCustomerContacts(customerContacts);
                waybill.setPrintAddress(printAddress);
                break;
            case '7':
                //7、隐藏全部
                waybill.setCustomerName(customerName);
                waybill.setCustomerContacts(customerContacts);
                waybill.setPrintAddress(printAddress);
                break;
            default:
                log.info("运单的waybillSign第37位标识非法：" + waybill.getWaybillCode());
        }
    }
}
