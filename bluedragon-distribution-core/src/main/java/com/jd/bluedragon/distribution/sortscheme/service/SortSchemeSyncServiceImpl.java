package com.jd.bluedragon.distribution.sortscheme.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.domain.DmsSortSchemeRouter;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.DtcDataReceiverManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.staig.receiver.rpc.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuzuxiang on 2017/1/11.
 */
@Service("sortSchemeSyncService")
public class SortSchemeSyncServiceImpl implements SortSchemeSyncService{

    private final Log logger = LogFactory.getLog(this.getClass());

    private static List<String> stores = Lists.newArrayList("6,6,51", "6,6,53", "10,10,51");

    private static final String sortSchemeUrl = "/sortScheme/find/id";
    private static final String sortSchemeDetailUrl = "/sortSchemeDetail/list/schemeId";

    @Autowired
    SortSchemeService sortSchemeService;

    @Autowired
    SortSchemeDetailService sortSchemeDetailService;

    @Autowired
    private DtcDataReceiverManager dtcDataReceiverManager;

    @Qualifier("sortSchemeSendDtcMQ")
    @Autowired
    private DefaultJMQProducer sortSchemeSendDtcMQ;

    @Autowired
    BaseMajorManager baseMajorManager;

    @Override
    public Boolean sendDtc(SortSchemeRequest request,String url,Integer siteCode) {
        boolean bool = false;
        SortSchemeResponse<SortScheme> sortScheme = sortSchemeService.findById2(request,url + sortSchemeUrl);//获取激活的方案的主表
        SortSchemeDetailRequest sortSchemeDetailRequest = new SortSchemeDetailRequest();
        sortSchemeDetailRequest.setId(request.getId());
        SortSchemeDetailResponse<java.util.List<SortSchemeDetail>> sortSchemeDetail = sortSchemeDetailService.findBySchemeId2(sortSchemeDetailRequest,url + sortSchemeDetailUrl);//获取激活的方案的明细表
        SortScheme sortSchemeData = sortScheme.getData();//分拣方案主表数据
        List<SortSchemeDetail> sortSchemeDetailDatas = sortSchemeDetail.getData();//分拣方案明细表数据

        BaseStaffSiteOrgDto bDto = null;
        try {
            bDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        } catch (Exception e) {
            logger.error("获取分拣中心信息失败，ID为" + siteCode);
            e.printStackTrace();
        }

        /** 发送MQ到DTC系统 **/
        List<String> jsonMQs = new ArrayList<String>();
        jsonMQs = this.sortSchemeToJson(jsonMQs,sortSchemeData,sortSchemeDetailDatas,bDto);
        this.logger.info("分拣中心已激活的分拣方案推送DTC:MQ[" + jsonMQs + "]");
        String businessId = siteCode + "_" + sortSchemeData.getMachineCode();//分拣中心加分拣机代码，例如：1086_PX-SHYK-JD

        if(jsonMQs.size()>0){
            try{
                for(String jsonMq : jsonMQs){
                    sortSchemeSendDtcMQ.send(businessId,jsonMq);
                }
                bool = true;
            }catch (Exception e) {
                this.logger.error(
                        "分拣中心已激活的分拣方案推送DTC失败[" + jsonMQs + "]:" + e.getMessage(), e);
                bool = false;
            }
        }
        return bool;
    }

    @Override
    public boolean sync(String url,String siteCode) {
        boolean bool = false;
        BaseStaffSiteOrgDto bDto = null;
        try{
            bDto = baseMajorManager.getBaseSiteByDmsCode(siteCode);
        }catch (Exception e){
            e.printStackTrace();
        }

        //通过siteCode得到分拣中心本地的激活的分拣方案
        SortSchemeRequest request =  new SortSchemeRequest();
        request.setSiteNo(siteCode);
        SortScheme sortScheme = sortSchemeService.queryBySiteCode(request,url + "/sortScheme/find/siteCode").getData();

        SortSchemeDetailRequest request1 = new SortSchemeDetailRequest();
        request1.setSchemeId(sortScheme.getId().toString());
        List<SortSchemeDetail> sortSchemeDetails = sortSchemeDetailService.queryBySiteCode(request1,url + "/sortSchemeDetail/list/schemeId").getData();
        List<String> jsonMqs = new ArrayList<String>();
        jsonMqs = this.sortSchemeToJson(jsonMqs,sortScheme,sortSchemeDetails,bDto);
        for(int i = 0;i<jsonMqs.size();i++ ){
            Map mapMq = new HashMap();
            mapMq = JsonHelper.json2Map(jsonMqs.get(i));
            String target = mapMq.get("target").toString();

            if (stores.contains(target)) {
                String messageValue = mapMq.get("messageValue").toString();
                String outboundNo = mapMq.get("outboundNo").toString();
                String outboundType = mapMq.get("outboundType").toString();
                String source = mapMq.get("source").toString();
                Result result = this.dtcDataReceiverManager.downStreamHandle(target, outboundType, messageValue, source, outboundNo);
                this.logger.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultCode()=" + result.getResultCode());
                this.logger.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultMessage()=" + result.getResultMessage());
                this.logger.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultValue()=" + result.getResultValue());
                if (result.getResultCode()== 1) {
                    this.logger.error("[分拣中心分拣方案推送DTC]消息失败，消息体为" + messageValue);
                }
            }
        }
        return bool;
    }

    /**
     * 分拣计划转化为Json格式
     * @param mapMQs
     * @param sortScheme
     * @param sortSchemeDetails
     * @return
     */
    private List<String> sortSchemeToJson(List<String> mapMQs, SortScheme sortScheme, List<SortSchemeDetail> sortSchemeDetails, BaseStaffSiteOrgDto bDto){
        Integer orgId = bDto.getOrgId();
        String dmsStoreId = bDto.getDmsSiteCode();
        String[] cky2AndStoreId = dmsStoreId.split("-");
        String cky2 = cky2AndStoreId[0];
        String storeId = cky2AndStoreId[1];
        if(sortScheme != null){
            DmsSortSchemeRouter dmsSortSchemeRouter = new DmsSortSchemeRouter();
            Map<String,String> mapMq = new HashMap<String, String>();
            logger.info("分拣方案主表的数据消息体转化");

            StringBuffer jsonBuffer = new StringBuffer();
            jsonBuffer.append("{\"machineCode\":").append(sortScheme.getMachineCode())
                    .append(",\"siteNo\":").append(sortScheme.getSiteNo())
                    .append(",\"yn\":").append(sortScheme.getYn())
                    .append(",\"name\":").append(sortScheme.getName())
                    .append(",\"id\":\"").append(sortScheme.getId());
            dmsSortSchemeRouter.setBody(jsonBuffer.toString());
            dmsSortSchemeRouter.setType("SortScheme");
            String json = JsonHelper.toJson(dmsSortSchemeRouter);
            mapMq.put("target",orgId + "," + cky2 + "," + storeId);
            mapMq.put("messageValue",json);
            mapMq.put("outbountnNo",bDto.getDmsSiteCode());
            mapMq.put("outbountType","SortSchemeBackDl");
            mapMq.put("source","DMS");

            String mqStr = JsonHelper.toJson(mapMq);
            mapMQs.add(mqStr);//消息池
        }

        if(sortSchemeDetails.size() > 0){
            logger.info("分拣方案明细的数据消息体转化：条数"+sortSchemeDetails.size());
            for(SortSchemeDetail itemDetail : sortSchemeDetails){
                Map<String,String> mapMq = new HashMap<String, String>();
                DmsSortSchemeRouter dmsSortSchemeRouter = new DmsSortSchemeRouter();
                StringBuffer detailJsonBuffer = new StringBuffer();
                detailJsonBuffer.append("{\"yn\":").append(itemDetail.getYn())
                        .append(",\"schemeId\":").append(itemDetail.getSchemeId())
                        .append(",\"siteCode\":").append(itemDetail.getSiteCode())
                        .append(",\"pkgLabelName\":").append(itemDetail.getPkgLabelName())
                        .append(",\"boxSiteCode\":\"").append(itemDetail.getBoxSiteCode())
                        .append(",\"chuteCode1\":").append(itemDetail.getChuteCode1())
                        .append(",\"currChuteCode\":\"").append(itemDetail.getCurrChuteCode())
                        .append(",\"id\":\"").append(itemDetail.getId());
                dmsSortSchemeRouter.setBody(detailJsonBuffer.toString());
                dmsSortSchemeRouter.setType("SortSchemeDetail");
                String detailJson = JsonHelper.toJson(dmsSortSchemeRouter);
                mapMq.put("target",orgId + "," + cky2 + "," + storeId);
                mapMq.put("messageValue",detailJson);
                mapMq.put("outboundNo",bDto.getDmsSiteCode());
                mapMq.put("outboundType","SortSchemeBackDl");
                mapMq.put("source","DMS");

                String mqStr = JsonHelper.toJson(mapMq);
                mapMQs.add(mqStr);
            }
        }
        return mapMQs;
    }
}
