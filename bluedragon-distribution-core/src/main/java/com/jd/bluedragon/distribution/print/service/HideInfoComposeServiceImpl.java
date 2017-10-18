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

    private static final String  SMILE = "^_^";           //微笑符号
    private static final String  PRINTADDRESS = "******";//客户地址全隐藏，以6个*字符替代


    @Override
    public void handle(PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
        String waybillSign  = waybill.getWaybillSign();
        //完美项目取件逆向隐藏面单服务：waybillSign第37位为0，不隐藏
        if(StringUtils.isBlank(waybillSign) || waybillSign.length() < 37 || '0' == waybillSign.charAt(36)){
            return;
        }

        char flag = waybillSign.charAt(36);
        switch(flag) {
            case '1':
                //1、隐藏姓名
                hideCustomerName(waybill);
                break;
            case '2':
                //2、隐藏电话
                hideCustomerContacts(waybill);
                break;
            case '3':
                //3、隐藏姓名 + 隐藏电话
                hideCustomerName(waybill);
                hideCustomerContacts(waybill);
                break;
            case '4':
                //4、隐藏地址
                waybill.setPrintAddress(PRINTADDRESS);
                break;
            case '5':
                //5、隐藏姓名 + 隐藏地址
                hideCustomerName(waybill);
                waybill.setPrintAddress(PRINTADDRESS);
                break;
            case '6':
                //6、隐藏电话 + 隐藏地址
                hideCustomerContacts(waybill);
                waybill.setPrintAddress(PRINTADDRESS);
                break;
            case '7':
                //7、隐藏全部
                hideCustomerName(waybill);
                hideCustomerContacts(waybill);
                waybill.setPrintAddress(PRINTADDRESS);
                break;
            default:
                log.info("运单的waybillSign第37位标识非法：" + waybill.getWaybillCode());
        }
    }

    /**
     * 设置客户姓名隐藏信息
     * @param waybill 运单
     */
    private void hideCustomerName(PrintWaybill waybill){
        String customerName = waybill.getCustomerName();
        if(StringUtils.isNotBlank(customerName)){
            customerName = customerName.trim().substring(0, 1) + SMILE;
            waybill.setCustomerName(customerName);
        }else{
            waybill.setCustomerName(SMILE);
        }
    }
    /**
     * 设置客户电话隐藏信息
     * @param waybill 运单
     */
    private void hideCustomerContacts(PrintWaybill waybill){
        String customerContacts = waybill.getCustomerContacts();
        //国内：普通城市座机、4位数区号+7位数座机电话号码=11位
        //国内：一线城市座机：3位数区号+8位数座机电话号码=11位
        //国内：手机 11位
        //电话大于等于6位，则显示为：前两位+^_^+后两位。
        if(StringUtils.isNotBlank(customerContacts) && customerContacts.trim().length() > 5){
            String trim = customerContacts.trim();
            customerContacts = trim.substring(0, 2) + SMILE + trim.substring(trim.length() - 2);
            waybill.setCustomerContacts(customerContacts);
        }
    }
}
