package com.jd.bluedragon.distribution.sendprint.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sendprint.domain.ExpressInfo;
import com.jd.bluedragon.distribution.sendprint.service.ThirdExpressPrintService;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by wangtingwei on 2015/9/10.
 */
@Service("thirdExpressPrintService")
public class ThirdExpressPrintServiceImpl implements ThirdExpressPrintService {

    private static final Logger log = LoggerFactory.getLogger(ThirdExpressPrintServiceImpl.class);

    private static final String WAYBILL_NOT_SEND="该运单没有发货至三方站点";

    private static final String WAYBILL_NOT_FOUND="没有获取到运单信息";
    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private BaseService baseService;

    @Autowired
    private QuickProduceService quickProduceService;


    /**
     * 获取三方面单信息
     * @param packageCode 包裹号
     * @return
     */
    @Override

    public InvokeResult<ExpressInfo> getThirdExpress(String packageCode) {
        if(log.isInfoEnabled()){
            log.info("调用获取三方面单接口，包裹号为[{}]",packageCode);
        }
        InvokeResult<ExpressInfo> result=new InvokeResult<ExpressInfo>();
        SendDetail queryPara=new SendDetail();
        queryPara.setPackageBarcode(packageCode);
        queryPara.setIsCancel(Integer.valueOf(0));
        queryPara.setSendType(Constants.BUSSINESS_TYPE_THIRD_PARTY);
        List<SendDetail> sendDetails= sendDatailDao.querySendDatailsBySelective(queryPara);//FIXME:无create_site_code有跨节点风险
        if(null==sendDetails||sendDetails.size()==0){
            result.customMessage(0,WAYBILL_NOT_SEND);
            return result;
        }
        Collections.sort(sendDetails, new Comparator<SendDetail>() {
            @Override
            public int compare(SendDetail o1, SendDetail o2) {
                if(null==o1.getUpdateTime()){
                    o1.setUpdateTime(o1.getCreateTime());
                }
                if(null==o2.getUpdateTime()){
                    o2.setUpdateTime(o2.getCreateTime());
                }
                return -o1.getUpdateTime().compareTo(o2.getUpdateTime());
            }
        });
        SendDetail targetSend=sendDetails.get(0);
        ExpressInfo data=new ExpressInfo();
        data.setSiteName(baseService.getSiteNameBySiteID(targetSend.getReceiveSiteCode()));
        data.setSiteId(targetSend.getReceiveSiteCode());
        if(log.isInfoEnabled()){
            log.info("获取站点为ID[{}]名称[{}]",data.getSiteId(),data.getSiteName());
        }
        QuickProduceWabill waybill=quickProduceService.getQuickProduceWabill(SerialRuleUtil.getWaybillCode(packageCode));
        if(null==waybill||null==waybill.getWaybill()){
            result.customMessage(0,WAYBILL_NOT_FOUND);
            return result;
        }
        if(log.isInfoEnabled()){
            log.info("调用中间件及台账信息为：{}", JsonHelper.toJson(waybill));
        }
        data.setRecMoney(waybill.getWaybill().getRecMoney());
        data.setDistributeStoreId(waybill.getWaybill().getDistributeStoreId());
        data.setDistributeStoreName(waybill.getWaybill().getDistributeStoreName());
        data.setOrgId(waybill.getWaybill().getOrgId());
        data.setPayment(waybill.getWaybill().getPaymentType());
        data.setReceiverAddress(waybill.getWaybill().getAddress());
        data.setReceiverMobile(waybill.getWaybill().getReceiverMobile());
        data.setReceiverName(waybill.getWaybill().getReceiverName());
        data.setReceiverPhone(waybill.getWaybill().getReceiverTel());
        data.setWaybillCode(SerialRuleUtil.getWaybillCode(packageCode));
        data.setReceiverPostcode(waybill.getWaybill().getReceiverZipCode());
        data.setReceiverCityname(waybill.getWaybill().getCityName());
        data.setDistributeType(waybill.getWaybill().getShipmentType());
        data.setPackCode(packageCode);
        data.setPackWeight(waybill.getWaybill().getWeight());
        result.setData(data);
        return result;
    }


}
