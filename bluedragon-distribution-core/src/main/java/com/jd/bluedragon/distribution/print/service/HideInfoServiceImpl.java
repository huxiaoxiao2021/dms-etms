package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.SendPayConstants;
import com.jd.bluedragon.utils.StringHelper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("hideInfoService")
public class HideInfoServiceImpl implements HideInfoService{

    private static final Logger log = LoggerFactory.getLogger(HideInfoServiceImpl.class);

    private static final int ADDRESS_SHOW_LENGTH = 9; //地址信息需要显示的前几位，超过部分用微笑符号替代

    public void setHideInfo(String waybillSign,String sendPay,BasePrintWaybill waybill){
    	if(waybill == null){
    		return;
    	}
    	if(StringUtils.isBlank(waybillSign) && StringUtils.isBlank(sendPay)){
            return;
        }
    	boolean customerInfoHideFlag = false;
        //收件人信息隐藏，根据waybill_sign第37位判断
        if(waybillSign.length() >= 37 && '0' != waybillSign.charAt(36)){
            char customerInfoHideType = waybillSign.charAt(36);
            customerInfoHideFlag = customerInfoHide(customerInfoHideType, waybill);
        }
        //waybillSign未设置隐藏时，根据SendPay第188位进行隐藏收件人信息
        if(!customerInfoHideFlag 
        		&& sendPay.length() >= SendPayConstants.POSITION_188){
            char customerInfoHideType = sendPay.charAt(SendPayConstants.POSITION_188 - 1);
            customerInfoHide(customerInfoHideType, waybill);
        }
        //寄件人信息隐藏，根据waybill_sign第47位判断
        if(waybillSign.length() >= 47 && '0' != waybillSign.charAt(46)){
            char consignerInfoHideType = waybillSign.charAt(46);
            consignerInfoHide(consignerInfoHideType,waybill);
        }
    }

    /**
     * 收件人信息隐藏设置
     * @param hideType
     * @param waybill
     */
    private boolean customerInfoHide(char hideType, BasePrintWaybill waybill){
        switch(hideType) {
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
                hideCustomerAddress(waybill);
                break;
            case '5':
                //5、隐藏姓名 + 隐藏地址
                hideCustomerName(waybill);
                hideCustomerAddress(waybill);
                break;
            case '6':
                //6、隐藏电话 + 隐藏地址
                hideCustomerContacts(waybill);
                hideCustomerAddress(waybill);
                break;
            case '7':
                //7、隐藏全部
                hideCustomerName(waybill);
                hideCustomerContacts(waybill);
                hideCustomerAddress(waybill);
                break;
            default:
                log.info("运单{}不做隐藏处理：{}" ,waybill.getWaybillCode(),hideType);
                return false;
        }
        return true;
    }


    /**
     * 寄件人信息隐藏设置
     * @param hideType
     * @param waybill
     */
    private void consignerInfoHide(char hideType, BasePrintWaybill waybill){
        switch(hideType) {
            case '1':
                //1、隐藏姓名
                hideConsignerName(waybill);
                break;
            case '2':
                //2、隐藏电话
                hideConsignerTel(waybill);
                break;
            case '3':
                //3、隐藏姓名 + 隐藏电话
                hideConsignerName(waybill);
                hideConsignerTel(waybill);
                break;
            case '4':
                //4、隐藏地址
                hideConsignerAddress(waybill);
                break;
            case '5':
                //5、隐藏姓名 + 隐藏地址
                hideConsignerName(waybill);
                hideConsignerAddress(waybill);
                break;
            case '6':
                //6、隐藏电话 + 隐藏地址
                hideConsignerTel(waybill);
                hideConsignerAddress(waybill);
                break;
            case '7':
                //7、隐藏全部（姓名、地址、电话）
                hideConsignerName(waybill);
                hideConsignerTel(waybill);
                hideConsignerAddress(waybill);
                break;
            default:
                log.info("运单的waybillSign第37位标识非法：{}" , waybill.getWaybillCode());
        }
    }


    /**
     * 隐藏寄件人的姓名
     * 第一个字+^_^
     * @param waybill
     */
    private void hideConsignerName(BasePrintWaybill waybill){
        String consignerName = waybill.getConsigner();
        if(StringUtils.isNotBlank(consignerName)){
            waybill.setConsigner(consignerName.substring(0,1) + StringHelper.SMILE);
        }
    }

    /**
     * 隐藏寄件人的电话
     * 少于7位不隐藏
     * 前三位+^_^+后四位
     * @param waybill
     */
    private void hideConsignerTel(BasePrintWaybill waybill){
        String consignerTel = waybill.getConsignerTel();
        String consignerMobile = waybill.getConsignerMobile();
        //进行隐藏要求tel/mobile至少有7位，<7位则不隐藏
        int phoneLeastLength = StringHelper.PHONE_FIRST_NUMBER + StringHelper.PHONE_HIGHLIGHT_NUMBER;

        if(StringUtils.isNotBlank(consignerTel)){
            //去除号码中间的空白字符
            consignerTel = consignerTel.replaceAll("\\s*", "");

            if(consignerTel.length() >= phoneLeastLength ){
                waybill.setConsignerTel(consignerTel.substring(0,StringHelper.PHONE_FIRST_NUMBER) + StringHelper.SMILE +
                        consignerTel.substring(consignerTel.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER));
            }else{
                waybill.setConsignerTel(consignerTel);
            }
        }

        if(StringUtils.isNotBlank(consignerMobile)){
            consignerMobile = consignerMobile.replaceAll("\\s*", "");

            if(consignerMobile.length() >= phoneLeastLength ){
                waybill.setConsignerMobile(consignerMobile.substring(0,StringHelper.PHONE_FIRST_NUMBER) + StringHelper.SMILE +
                        consignerMobile.substring(consignerMobile.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER));
            }else{
                waybill.setConsignerMobile(consignerMobile);
            }
        }

    }

    /**
     * 隐藏寄件人的地址
     * 少于9个字则不隐藏
     * 否则，前9个字+^_^
     * @param waybill
     */
    private void hideConsignerAddress(BasePrintWaybill waybill){
        String consignerAddress = waybill.getConsignerAddress();
        if(StringUtils.isNotBlank(consignerAddress) && consignerAddress.length() >= ADDRESS_SHOW_LENGTH){
            waybill.setConsignerAddress(consignerAddress.substring(0,ADDRESS_SHOW_LENGTH) + StringHelper.SMILE);
        }
    }

    /**
     * 设置客户姓名隐藏信息
     * @param waybill 运单
     */
    private void hideCustomerName(BasePrintWaybill waybill){
        String customerName = waybill.getCustomerName();
        if(StringUtils.isNotBlank(customerName)){
            customerName = customerName.trim().substring(0, 1) + StringHelper.SMILE;
            waybill.setCustomerName(customerName);
        }
    }
    /**
     * 设置客户电话隐藏信息
     * @param waybill 运单
     */
    private static void hideCustomerContacts(BasePrintWaybill waybill){
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
                log.warn("微笑面单手机号错误，运单号：{};手机号：{}",waybill.getWaybillCode(),waybill.getCustomerContacts());
                return;
            }
        }
        StringBuilder customerContacts =new StringBuilder();
        //国内：普通城市座机、4位数区号+7位数座机电话号码=11位
        //国内：一线城市座机：3位数区号+8位数座机电话号码=11位
        //国内：手机 11位
        //电话大于等于7位，则显示为：前3位+^_^+后4位。
        if(StringUtils.isNotBlank(firstMobile)){
            if(firstMobile.length() >= StringHelper.PHONE_FIRST_NUMBER){
                customerContacts.append(firstMobile.substring(0, StringHelper.PHONE_FIRST_NUMBER) + StringHelper.SMILE + lastMobile);
                waybill.setMobileFirst(firstMobile.substring(0, StringHelper.PHONE_FIRST_NUMBER) + StringHelper.SMILE );
            }else{
                customerContacts.append(firstMobile + StringHelper.SMILE + lastMobile);
                waybill.setMobileFirst(firstMobile + StringHelper.SMILE);
            }
        }
        if(StringUtils.isNotBlank(firstTel)){
            if(customerContacts.length() > 0){
                customerContacts.append(",");
            }
            if(firstTel.length() >= StringHelper.PHONE_FIRST_NUMBER){
                customerContacts.append(firstTel.substring(0, StringHelper.PHONE_FIRST_NUMBER) + StringHelper.SMILE + lastTel);
                waybill.setTelFirst(firstTel.substring(0, StringHelper.PHONE_FIRST_NUMBER) + StringHelper.SMILE);
            }else{
                customerContacts.append(firstTel + StringHelper.SMILE + lastTel);
                waybill.setTelFirst(firstTel + StringHelper.SMILE);
            }
        }
        if(customerContacts.length() > 0){
            waybill.setCustomerContacts(customerContacts.toString());
        }
    }

    /**
     * 隐藏收件人的地址
     * 少于9个字则不隐藏
     * 否则，前9个字+^_^
     * @param waybill
     */
    private void hideCustomerAddress(BasePrintWaybill waybill){
        String customerAddress = waybill.getPrintAddress();
        if(StringUtils.isNotBlank(customerAddress) && customerAddress.length() >= ADDRESS_SHOW_LENGTH){
            waybill.setPrintAddress(customerAddress.substring(0,ADDRESS_SHOW_LENGTH) + StringHelper.SMILE);
        }
    }

    /**
     * 将手机号分段
     * @param waybill
     * @return
     */
    private static boolean setPhone(BasePrintWaybill waybill){
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
        if (StringUtils.isNotBlank(mobile) && mobile.length() >= StringHelper.PHONE_HIGHLIGHT_NUMBER) {
            firstMobile = mobile.substring(0, mobile.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER);
            waybill.setMobileFirst(firstMobile);
            waybill.setMobileLast(mobile.substring(mobile.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER));
        }
        if (StringUtils.isNotBlank(tel) && tel.length() >= StringHelper.PHONE_HIGHLIGHT_NUMBER) {
            firstTel = tel.substring(0, tel.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER);
            waybill.setTelFirst(firstTel);
            waybill.setTelLast(tel.substring(tel.length() - StringHelper.PHONE_HIGHLIGHT_NUMBER));
        }
        if(StringUtils.isBlank(firstMobile) && StringUtils.isBlank(firstTel) ){
            return false;
        }
        return true;
    }
}
