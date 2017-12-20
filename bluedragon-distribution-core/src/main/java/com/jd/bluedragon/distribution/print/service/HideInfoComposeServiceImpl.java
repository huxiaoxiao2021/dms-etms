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
    private static final int PHONE_FIRST_NUMBER = 3;//收件人联系方式前几位需要显示
    private static final int PHONE_HIGHLIGHT_NUMBER = 4;//收件人联系方式需要突出显示的位数(即手机尾数要保留的位数)

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
    private static void hideCustomerContacts(PrintWaybill waybill){
        String firstMobile = StringUtils.trimToEmpty(waybill.getMobileFirst());
        String lastMobile = StringUtils.trimToEmpty(waybill.getMobileLast());
        String firstTel = StringUtils.trimToEmpty(waybill.getTelFirst());
        String lastTel = StringUtils.trimToEmpty(waybill.getTelLast());
        if(StringUtils.isBlank(firstMobile) && StringUtils.isBlank(firstTel)){//没有设值或者手机号错填只有4位，需要进一步处理
            boolean success = setPhone(waybill);
            if(success){
                firstMobile = StringUtils.trimToEmpty(waybill.getMobileFirst());
                lastMobile = StringUtils.trimToEmpty(waybill.getMobileLast());
                firstTel = StringUtils.trimToEmpty(waybill.getTelFirst());
                lastTel = StringUtils.trimToEmpty(waybill.getTelLast());
            }else{
                log.error("微笑面单手机号错误，运单号：" + waybill.getWaybillCode() + ";手机号：" + waybill.getCustomerContacts());
                return;
            }
        }
        StringBuilder customerContacts =new StringBuilder();
        //国内：普通城市座机、4位数区号+7位数座机电话号码=11位
        //国内：一线城市座机：3位数区号+8位数座机电话号码=11位
        //国内：手机 11位
        //电话大于等于7位，则显示为：前3位+^_^+后4位。
        if(StringUtils.isNotBlank(firstMobile)){
            if(firstMobile.length() >= PHONE_FIRST_NUMBER){
                customerContacts.append(firstMobile.substring(0, PHONE_FIRST_NUMBER) + SMILE + lastMobile);
                waybill.setMobileFirst(firstMobile.substring(0, PHONE_FIRST_NUMBER) + SMILE );
            }else{
                customerContacts.append(firstMobile + SMILE + lastMobile);
                waybill.setMobileFirst(firstMobile + SMILE);
            }
        }
        if(StringUtils.isNotBlank(firstTel)){
            if(customerContacts.length() > 0){
                customerContacts.append(",");
            }
            if(firstTel.length() >= PHONE_FIRST_NUMBER){
                customerContacts.append(firstTel.substring(0, PHONE_FIRST_NUMBER) + SMILE + lastTel);
                waybill.setTelFirst(firstTel.substring(0, PHONE_FIRST_NUMBER) + SMILE);
            }else{
                customerContacts.append(firstTel + SMILE + lastTel);
                waybill.setTelFirst(firstTel + SMILE);
            }
        }
        if(customerContacts.length() > 0){
            waybill.setCustomerContacts(customerContacts.toString());
        }
    }

    /**
     * 将手机号分段
     * @param waybill
     * @return
     */
    private static boolean setPhone(PrintWaybill waybill){
        String firstMobile = null;
        String firstTel = null;
        String contacts = waybill.getCustomerContacts();
        if(StringUtils.isBlank(contacts)){
            return false;
        }
        String[] acontacts  = contacts.split(",");
        String mobile = acontacts[0];
        String tel = null;
        if(acontacts.length == 2){
            tel = acontacts[1];
        }
        if (StringUtils.isNotBlank(mobile) && mobile.length() >= PHONE_HIGHLIGHT_NUMBER) {
            firstMobile = mobile.substring(0, mobile.length() - PHONE_HIGHLIGHT_NUMBER);
            waybill.setMobileFirst(firstMobile);
            waybill.setMobileLast(mobile.substring(mobile.length() - PHONE_HIGHLIGHT_NUMBER));
        }
        if (StringUtils.isNotBlank(tel) && tel.length() >= PHONE_HIGHLIGHT_NUMBER) {
            firstTel = tel.substring(0, tel.length() - PHONE_HIGHLIGHT_NUMBER);
            waybill.setTelFirst(firstTel);
            waybill.setTelLast(tel.substring(tel.length() - PHONE_HIGHLIGHT_NUMBER));
        }
        if(StringUtils.isBlank(firstMobile) && StringUtils.isBlank(firstTel) ){
            return false;
        }
        return true;
    }
}
