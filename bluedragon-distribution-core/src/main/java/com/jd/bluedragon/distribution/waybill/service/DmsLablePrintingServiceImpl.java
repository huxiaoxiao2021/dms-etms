package com.jd.bluedragon.distribution.waybill.service;

/**
 * Created by yanghongqiang on 2015/11/30.
 */

import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.basic.domain.BaseDmsStore;
import com.jd.etms.waybill.domain.Waybill;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * 分拣中心包裹标签打印
 * @author eason
 *
 */
@Service("dmsLablePrintingService")
public class DmsLablePrintingServiceImpl extends AbstractLabelPrintingServiceTemplate {

    public static final Log log = LogFactory.getLog(DmsLablePrintingServiceImpl.class);

    public static final String LOG_PREFIX="包裹标签打印-分拣[DmsLablePrintingServiceImpl] ";

    /**
     * 初始化基础资料对象
     */
    @Override
    public BaseDmsStore initBaseVo(LabelPrintingRequest request) {
        BaseDmsStore baseDmsStore = new BaseDmsStore();
        baseDmsStore.setStoreId(request.getStoreCode());//库房编号
        baseDmsStore.setCky2(request.getCky2());//cky2
        baseDmsStore.setOrgId(request.getOrgCode());//机构编号
        baseDmsStore.setDmsId(request.getDmsCode());//分拣中心编号

        return baseDmsStore;
    }

    /**
     * 包裹标签在运单范围内的扩展
     */
    @Override
    public LabelPrintingResponse waybillExtensional(
            LabelPrintingRequest request, LabelPrintingResponse labelPrinting,
            Waybill waybill) {
        //现场调度时 预分拣站点为空，以现场调度的为准
        if(request.getPreSeparateCode()==null){
            labelPrinting.setPrepareSiteCode(waybill.getOldSiteId());
            labelPrinting.setPrintAddress(waybill.getReceiverAddress());
        }else{
            labelPrinting.setPrepareSiteCode(request.getPreSeparateCode());
            labelPrinting.setPrepareSiteName(request.getPreSeparateName());
            //如果调度的新地址为空，则应当显示老地址
            labelPrinting.setPrintAddress(StringHelper.isEmpty(waybill.getNewRecAddr())?waybill.getReceiverAddress():waybill.getNewRecAddr());
        }

        //自提订单---打提字，并且地址不显示
        StringBuilder specialMark = new StringBuilder(StringHelper.isEmpty(labelPrinting.getSpecialMark())?"":labelPrinting.getSpecialMark());
        log.debug(new StringBuilder(LOG_PREFIX).append("waybill---distanceType").append(waybill.getDistanceType()).append("sendpay ").append(waybill.getSendPay()));
        if(waybill.getDistributeType()!=null && waybill.getDistributeType().equals(LabelPrintingService.ARAYACAK_SIGN) && waybill.getSendPay().length()>=50){
            if(waybill.getSendPay().charAt(21)!='5'){
                labelPrinting.setPrintAddress("");
                specialMark.append(LabelPrintingService.SPECIAL_MARK_ARAYACAK_SITE);
            }
        }
        // 众包--运单 waybillSign 第 12位为 9--追打"众"字
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().charAt(11)=='9') {
            specialMark.append(LabelPrintingService.SPECIAL_MARK_CROWD_SOURCING);
        }
        labelPrinting.setSpecialMark(specialMark.toString());
        // 外单多时效打标
        if(StringHelper.isNotEmpty(waybill.getWaybillSign())) {
            if(waybill.getWaybillSign().charAt(15)=='0')
                labelPrinting.setTimeCategory("");
            if(waybill.getWaybillSign().charAt(15)=='1')
                labelPrinting.setTimeCategory("当日达");
            if(waybill.getWaybillSign().charAt(15)=='2')
                labelPrinting.setTimeCategory("次日达");
            if(waybill.getWaybillSign().charAt(15)=='3')
                labelPrinting.setTimeCategory("隔日达");
            if(waybill.getWaybillSign().charAt(15)=='4')
                labelPrinting.setTimeCategory("4日达及以上");
        }

        //加promise调用获取时效具体信息
        return labelPrinting;
    }
}
