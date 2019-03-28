package com.jd.bluedragon.distribution.sortscheme.service;

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

    private List<String> stores ;

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
        if (!Integer.valueOf(625130).equals(siteCode)){
            logger.error("非北京亚一的分拣中心不能进行分拣计划同步" + siteCode);
            return true;
        }
        BaseStaffSiteOrgDto bDto = baseMajorManager.getBaseSiteBySiteId(siteCode);
        if(bDto == null){
            logger.warn("分拣计划同步失败，站点不存在：" + siteCode);
            return false;
        }
        SortSchemeResponse<SortScheme> sortScheme = sortSchemeService.findById2(request,url + sortSchemeUrl);//获取激活的方案的主表
        SortSchemeDetailRequest sortSchemeDetailRequest = new SortSchemeDetailRequest();
        sortSchemeDetailRequest.setSchemeId(request.getId().toString());
        SortSchemeDetailResponse<java.util.List<SortSchemeDetail>> sortSchemeDetail = sortSchemeDetailService.findBySchemeId2(sortSchemeDetailRequest,url + sortSchemeDetailUrl);//获取激活的方案的明细表
        SortScheme sortSchemeData = sortScheme.getData();//分拣方案主表数据
        List<SortSchemeDetail> sortSchemeDetailDatas = sortSchemeDetail.getData();//分拣方案明细表数据

        /** 发送MQ到DTC系统 **/
        List<String> jsonMQs = new ArrayList<String>();
        jsonMQs = this.sortSchemeToJson(jsonMQs,sortSchemeDetailDatas,bDto,sortSchemeData);
        if(this.logger.isDebugEnabled()){
            this.logger.info("分拣中心已激活的分拣方案推送DTC:MQ[" + jsonMQs + "]");
        }
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
        BaseStaffSiteOrgDto bDto = baseMajorManager.getBaseSiteBySiteId(Integer.valueOf(siteCode));
        if(bDto == null){
            logger.warn("分拣计划同步失败，站点不存在：" + siteCode);
            return false;
        }
        //通过siteCode得到分拣中心本地的激活的分拣方案
        SortSchemeRequest request =  new SortSchemeRequest();
        request.setSiteNo(siteCode);
        List<SortScheme> sortSchemes = sortSchemeService.queryBySiteCode(request,url + "/autosorting/sortScheme/find/siteCode").getData();
        List<SortSchemeDetail> sortSchemeDetails = new ArrayList<SortSchemeDetail>();
        if(sortSchemes == null || sortSchemes.size() <= 0){
            logger.info("没有获取到该站点的分拣计划主表信息" + siteCode);
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

            if ("625130".equals(siteCode)) {//北京亚一分拣中心有这个需求，由于是基础资料维护失败，暂时先写死 只要北京亚一进行推送
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

                String detailJsonBuffer = new String();
                Map<String,Object> detailMap = new HashMap<String,Object>();
                detailMap.put("yn",itemDetail.getYn());
                detailMap.put("schemeId",itemDetail.getSchemeId());
                detailMap.put("siteCode",itemDetail.getSiteCode());
                detailMap.put("pkgLabelName",itemDetail.getPkgLabelName());
                detailMap.put("boxSiteCode",itemDetail.getBoxSiteCode());
                detailMap.put("chuteCode1",itemDetail.getChuteCode1());
                detailMap.put("currChuteCode",itemDetail.getCurrChuteCode());
                detailMap.put("id",itemDetail.getId());
                detailJsonBuffer = JsonHelper.toJsonUseGson(detailMap);

                dmsSortSchemeRouter.setBody(detailJsonBuffer);
                dmsSortSchemeRouter.setType("SortSchemeDetail");
                String detailJson = JsonHelper.toJson(dmsSortSchemeRouter);
                mapMq.put("target","6,6,80");//北京亚一写死
                mapMq.put("messageValue",detailJson);
                mapMq.put("outboundNo",itemDetail.getSiteCode()+ "-" + String.valueOf(itemDetail.getId()));
                mapMq.put("methodName","sortSchemeDetailDownload");
                mapMq.put("outboundType","sortSchemeDetailDownload");
                mapMq.put("source","DMS");

                String mqStr = JsonHelper.toJson(mapMq);
                mapMQs.add(mqStr);
            }
        }
        return mapMQs;
    }

    public List<String> getStores() {
        return stores;
    }

    public void setStores(List<String> stores) {
        this.stores = stores;
    }
}
