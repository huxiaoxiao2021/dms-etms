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

    private static List<String> stores = Lists.newArrayList("6,6,51", "6,6,80", "6,010,002","6,1,2");

    private static final String sortSchemeUrl = "/autosorting/sortScheme/find/id";
    private static final String sortSchemeDetailUrl = "/autosorting/sortSchemeDetail/list/schemeId";

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
        sortSchemeDetailRequest.setSchemeId(request.getId().toString());
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
        jsonMQs = this.sortSchemeToJson(jsonMQs,sortSchemeDetailDatas,bDto,sortSchemeData);
        this.logger.info("分拣中心已激活的分拣方案推送DTC:MQ[" + jsonMQs + "]");
        String businessId = siteCode + "_" + sortSchemeData.getMachineCode() + "_" + sortSchemeData.getName();//分拣中心+分拣机代码+方案名称，例如：1086_PX-SHYK-JD_1月12日生产方案

        if(jsonMQs.size()>0){
            try{
                for(String jsonMq : jsonMQs){
                    sortSchemeSendDtcMQ.send(businessId,jsonMq);
                }
                bool = true;
            }catch (Exception e) {
                this.logger.error(
                        "分拣中心已激活的分拣方案通过MQ推送DTC失败[" + jsonMQs + "]:" + e.getMessage(), e);
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
            bDto = this.baseMajorManager.getBaseSiteBySiteId(Integer.valueOf(siteCode));
        }catch (Exception e){
            e.printStackTrace();
        }

        //通过siteCode得到分拣中心本地的激活的分拣方案
        SortSchemeRequest request =  new SortSchemeRequest();
        request.setSiteNo(siteCode);
        List<SortScheme> sortSchemes = sortSchemeService.queryBySiteCode(request,url + "/autosorting/sortScheme/find/siteCode").getData();
        List<SortSchemeDetail> sortSchemeDetails = new ArrayList<SortSchemeDetail>();
        if(sortSchemes == null || sortSchemes.size() <= 0){
            return bool;
        }
        for(int i = 0;i<sortSchemes.size();i++ ){
            SortSchemeDetailRequest request1 = new SortSchemeDetailRequest();
            request1.setSchemeId(sortSchemes.get(i).getId().toString());
            sortSchemeDetails.addAll(sortSchemeDetailService.queryBySiteCode(request1,url + "/autosorting/sortSchemeDetail/list/schemeId").getData());

        }
        List<String> jsonMqs = new ArrayList<String>();
        jsonMqs = this.sortSchemeToJson(jsonMqs,sortSchemeDetails,bDto,sortSchemes.toArray(new SortScheme[sortSchemes.size()]));
        for(int i = 0;i<jsonMqs.size();i++ ){
            Map mapMq = new HashMap();
            mapMq = JsonHelper.json2Map(jsonMqs.get(i));
            String target = mapMq.get("target").toString();

            if (stores.contains(target)) {
                String messageValue = mapMq.get("messageValue").toString();
                String outboundNo = mapMq.get("outboundNo").toString();
                String methodName = mapMq.get("methodName").toString();
                String outboundType = mapMq.get("outboundType").toString();
                String source = mapMq.get("source").toString();
                Result result = this.dtcDataReceiverManager.downStreamHandle(target, outboundType, messageValue, source, outboundNo);
                this.logger.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultCode()=" + result.getResultCode());
                this.logger.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultMessage()=" + result.getResultMessage());
                this.logger.info("[分拣中心分拣方案推送DTC]:接口访问成功，result.getResultValue()=" + result.getResultValue());
                if (result.getResultCode()!= 1) {
                    this.logger.error("[分拣中心分拣方案推送DTC]消息失败，消息体为" + messageValue);
                }else{
                    bool = Boolean.TRUE;
                }
            }
        }
        return bool;
    }

    /**
     * 分拣计划转化为Json格式
     * @param mapMQs
     * @param sortSchemes
     * @param sortSchemeDetails
     * @return
     */
    private List<String> sortSchemeToJson(List<String> mapMQs, List<SortSchemeDetail> sortSchemeDetails, BaseStaffSiteOrgDto bDto,SortScheme... sortSchemes ){
        Integer orgId = bDto.getOrgId();
        String dmsStoreId = bDto.getDmsSiteCode();//七位站点编码
        if(this.logger.isDebugEnabled()){
            this.logger.debug(dmsStoreId);
        }
        String cky2;
        String storeId;
//        if(bDto.getSiteType() == 64){
//            cky2 = dmsStoreId.substring(0,3);
//            storeId = dmsStoreId.substring(4,7);
//        }else{
            String[] cky2AndStoreId = dmsStoreId.split(String.valueOf(dmsStoreId.charAt(3)));//自己都觉得有点多余
            cky2 = cky2AndStoreId[0].replace("0","");
            storeId = cky2AndStoreId[1].replace("0","");
//        }
//        取消分拣方案主表的同步
//        if(sortSchemes != null && sortSchemes.length > 0){
//            for (int i = 0;i<sortSchemes.length;i++){
//            DmsSortSchemeRouter dmsSortSchemeRouter = new DmsSortSchemeRouter();
//            Map<String,String> mapMq = new HashMap<String, String>();
//            logger.info("分拣方案主表的数据消息体转化");
//
//            StringBuffer jsonBuffer = new StringBuffer();
//            jsonBuffer.append("{\"machineCode\":").append(sortSchemes[i].getMachineCode())
//                    .append(",\"siteNo\":").append(sortSchemes[i].getSiteNo())
//                    .append(",\"yn\":").append(sortSchemes[i].getYn())
//                    .append(",\"name\":").append(sortSchemes[i].getName())
//                    .append(",\"id\":\"").append(sortSchemes[i].getId());
//            dmsSortSchemeRouter.setBody(jsonBuffer.toString());
//            dmsSortSchemeRouter.setType("SortScheme");
//            String json = JsonHelper.toJson(dmsSortSchemeRouter);
//            mapMq.put("target",orgId + "," + cky2 + "," + storeId);
//            mapMq.put("messageValue",json);
//            mapMq.put("outbountnNo",bDto.getDmsSiteCode());
//            mapMq.put("outbountType","SortSchemeBackDl");
//            mapMq.put("source","DMS");
//
//            String mqStr = JsonHelper.toJson(mapMq);
//            mapMQs.add(mqStr);//消息池
//            }
//        }

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
                mapMq.put("outboundNo",itemDetail.getId().toString());
                mapMq.put("methodName","sortSchemeDetailDownload");
                mapMq.put("outboundType","sortSchemeDetailDownload");
                mapMq.put("source","DMS");

                String mqStr = JsonHelper.toJson(mapMq);
                mapMQs.add(mqStr);
            }
        }
        return mapMQs;
    }
}
