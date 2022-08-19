package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jddl.executor.function.scalar.filter.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author liwenji
 * @description 隐藏敏感信息
 * @date 2022-08-18 14:39
 */
@Service("hideInfoSmileService")
public class HideInfoSmileServiceImpl implements HideInfoSmileService{

    private static final int ADDRESS_SHOW_LENGTH = 9; //地址信息需要显示的前几位，超过部分用微笑符号替代

    private static final int ADDRESS_HIDE_LENGTH = 6; //地址信息需要隐藏的后几位，用微笑符号替代
    //特殊规则隐藏规则  纯配冷链卡班、纯配城配共配、医药零担  20200212刘铎新增
    private static final int LLKB_CP_CP_YYLD_RULE = 1;
    //原隐藏规则
    private static final int NOMAL_RULE = 0;

    @Override
    public void setHideInfo(Waybill waybill) {
        String waybillSign = waybill.getWaybillSign();

        String sendPay = waybill.getSendPay();

        if(StringUtils.isBlank(waybillSign) && StringUtils.isBlank(sendPay)){
            return;
        }

        //隐藏规则
        int hideRule = NOMAL_RULE;
        if(BusinessUtil.isColdChainCPKB(waybillSign)
                ||BusinessUtil.isMedicineCP(waybillSign)
                ||BusinessUtil.isFreshCPGP(waybillSign)){
            hideRule = LLKB_CP_CP_YYLD_RULE;
        }
        if (!StringUtils.isBlank(waybill.getReceiverAddress())){
            waybill.setReceiverAddress(hideAddress(waybill.getReceiverAddress(),hideRule));
        }
        if (!StringUtils.isBlank(waybill.getReceiverTel())){
            waybill.setReceiverTel(hidePhone(waybill.getReceiverTel()));
        }
        if (!StringUtils.isBlank(waybill.getReceiverMobile())){
            waybill.setReceiverMobile(hidePhone(waybill.getReceiverMobile()));
        }
        if (!StringUtils.isBlank(waybill.getConsignerMobile())){
            waybill.setConsignerMobile(hidePhone(waybill.getConsignerMobile()));
        }
        if (!StringUtils.isBlank(waybill.getConsignerTel())){
            waybill.setConsignerTel(hidePhone(waybill.getConsignerTel()));
        }
        if (!StringUtils.isBlank(waybill.getConsignerAddress())){
            waybill.setConsignerAddress(hideAddress(waybill.getConsignerAddress(),hideRule));
        }
        hideName(waybill);

    }

    /**
     * 设置客户姓名隐藏信息
     * @param waybill 运单
     */
    private void hideName(Waybill waybill){
        String receiverName = waybill.getReceiverName();
        if(StringUtils.isNotBlank(receiverName)){
            receiverName = receiverName.trim().substring(0, 1) + StringHelper.SMILE;
            waybill.setReceiverName(receiverName);
        }
        String consigner = waybill.getConsigner();
        if(StringUtils.isNotBlank(consigner)){
            consigner = consigner.trim().substring(0, 1) + StringHelper.SMILE;
            waybill.setConsigner(consigner);
        }
    }
    private static String hidePhone(String mobile){
        StringBuilder mobileSmile =new StringBuilder();
        //国内：普通城市座机、4位数区号+7位数座机电话号码=11位
        //国内：一线城市座机：3位数区号+8位数座机电话号码=11位
        //国内：手机 11位
        //电话大于等于7位，则显示为：前3位+^_^+后4位。
        if(StringUtils.isNotBlank(mobile)){
            mobileSmile.append(mobile.substring(0,3));
            mobileSmile.append(StringHelper.SMILE);
            mobileSmile.append(mobile.substring(7));
        }
        return mobileSmile.toString();
    }
    /**
     * 计算隐藏地址规则
     * @param address
     * @param hideRule
     * @return
     */
    private String hideAddress(String address,int hideRule){

        if(hideRule == NOMAL_RULE){
            //保留前9位
            if(address.length() >= ADDRESS_SHOW_LENGTH){
                address = address.substring(0,ADDRESS_SHOW_LENGTH) + StringHelper.SMILE;
            }
        }else if(hideRule == LLKB_CP_CP_YYLD_RULE){
            //隐藏最后6个汉字，其它正常展示
            if(address.length() > ADDRESS_HIDE_LENGTH){
                address = address.substring(0,address.length()-ADDRESS_HIDE_LENGTH) + StringHelper.SMILE;
            }else{
                address = StringHelper.SMILE;
            }
        }
        return address;
    }
}
