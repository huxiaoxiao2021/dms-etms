package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.dmslocalinstanceinfo.DmsLocalInstanceInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.dmslocalserverinfo.DmsLocalServerInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.dmslocalserverinfo.entity.VipInfoJsfEntity;
import com.jd.bd.dms.automatic.sdk.modules.storagecomponent.StorageComponentJsfService;
import com.jd.bd.dms.automatic.sdk.modules.storagecomponent.entity.ComponentPositionJsfEntity;
import com.jd.bd.dms.automatic.sdk.modules.storagecomponent.request.ComponentPositionInfoJsfRequest;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分拣中心本地服务器信息管理
 * <p>
 * Created by lixin39 on 2018/7/16.
 */
@Service("dmsLocalServerManager")
public class DmsLocalServerManagerImpl implements DmsLocalServerManager {

    private final static Log logger = LogFactory.getLog(DmsLocalServerManagerImpl.class);

    @Autowired
    @Qualifier("dmsLocalServerInfoJsfService")
    private DmsLocalServerInfoJsfService dmsLocalServerInfoJsfService;

    @Autowired
    @Qualifier("storageComponentJsfService")
    private StorageComponentJsfService storageComponentJsfService;

    @Autowired
    @Qualifier("dmsLocalInstanceInfoJsfService")
    private DmsLocalInstanceInfoJsfService dmsLocalInstanceInfoJsfService;

    @JProfiler(jKey = "DMSWEB.DmsLocalServerManagerImpl.getVipListByDmsId", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    @Override
    public List<VipInfoJsfEntity> getVipListByDmsId(Integer dmsId) {
        if (dmsId == null) {
            return Collections.emptyList();
        }
        try {
            return this.getDataListByResponse(dmsLocalServerInfoJsfService.getVipListByDmsId(dmsId));
        } catch (Exception e) {
            logger.error("[JSF接口调用异常]调用自动化JSF接口getVipListByDmsId()获取分拣中心本地服务器VIP地址出现异常", e);
        }
        return null;
    }

    @JProfiler(jKey = "DMSWEB.DmsLocalServerManagerImpl.getAllVipList", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    @Override
    public List<VipInfoJsfEntity> getAllVipList() {
        try {
            return this.getDataListByResponse(dmsLocalServerInfoJsfService.getAllVipList());
        } catch (Exception e) {
            logger.error("[JSF接口调用异常]调用自动化JSF接口getAllVipList()获取分拣中心本地服务器VIP地址出现异常", e);
        }
        return null;
    }

    @JProfiler(jKey = "DMSWEB.DmsLocalServerManagerImpl.getStorageCodeByDmsId", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    @Override
    public List<String> getStorageCodeByDmsId(Integer dmsId) {
        ComponentPositionInfoJsfRequest param = new ComponentPositionInfoJsfRequest();
        List<String> result = new ArrayList<String>();
        param.setCreateSiteCode(dmsId.toString());

        try {
            BaseDmsAutoJsfResponse<List<ComponentPositionJsfEntity>> baseDmsAutoJsfResponse = storageComponentJsfService.getPositionBySite(param);
            if(BaseDmsAutoJsfResponse.SUCCESS_CODE == baseDmsAutoJsfResponse.getStatusCode()){
                List<ComponentPositionJsfEntity> lst = baseDmsAutoJsfResponse.getData();
                for(ComponentPositionJsfEntity pojo : lst){
                    result.add(pojo.getCode());
                }
            }else{
                logger.error("[JSF接口调用失败]调用自动化JSF接口getPositionBySite  "+ JsonHelper.toJson(param)+"  "+baseDmsAutoJsfResponse.getStatusMessage());
            }
        } catch (Exception e) {
            logger.error("[JSF接口调用异常]调用自动化JSF接口getPositionBySite "+ JsonHelper.toJson(param), e);
        }

        return result;
    }

    @JProfiler(jKey = "DMSWEB.DmsLocalServerManagerImpl.getVerAppUrlBySiteCode", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    @Override
    public String getVerAppUrlBySiteCode(String siteCode) {
        try {
            BaseDmsAutoJsfResponse<String> response=dmsLocalInstanceInfoJsfService.getSortSchemeUrlBySiteCode(siteCode);
            if (response != null && response.getStatusCode() == BaseDmsAutoJsfResponse.SUCCESS_CODE){
                return response.getData();
            }else {
                logger.error("[JSF接口调用失败]调用自动化JSF接口getVerAppUrlBySiteCode  "+ JsonHelper.toJson(siteCode)+",返回结果：" + JsonHelper.toJson(response));
            }

        } catch (Exception e) {
            logger.error("[JSF接口调用异常]调用自动化JSF接口getVerAppUrlBySiteCode获取分拣中心本地服务器地址出现异常", e);
        }
        return null;
    }

    @JProfiler(jKey = "DMSWEB.DmsLocalServerManagerImpl.checkStorage", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    @Override
    public boolean checkStorage(Integer dmsId, String storageCode) {
        ComponentPositionInfoJsfRequest param = new ComponentPositionInfoJsfRequest();
        param.setCreateSiteCode(dmsId.toString());
        param.setPositionCode(storageCode);

        try {
            BaseDmsAutoJsfResponse<List<ComponentPositionJsfEntity>> baseDmsAutoJsfResponse = storageComponentJsfService.getPositionBySiteAndCode(param);
            if(BaseDmsAutoJsfResponse.SUCCESS_CODE == baseDmsAutoJsfResponse.getStatusCode()){
                List<ComponentPositionJsfEntity> lst = baseDmsAutoJsfResponse.getData();
                if(lst!=null && lst.size()==1 && lst.get(0)!=null){
                    if(storageCode.equals(lst.get(0).getCode())){
                        //存在
                        return true;
                    }
                }

            }else{
                logger.error("[JSF接口调用失败]调用自动化JSF接口getPositionBySiteAndCode  "+ JsonHelper.toJson(param)+"  "+baseDmsAutoJsfResponse.getStatusMessage());
            }
        } catch (Exception e) {
            logger.error("[JSF接口调用异常]调用自动化JSF接口getPositionBySiteAndCode "+ JsonHelper.toJson(param), e);
        }

        return false;
    }

    /**
     * 在调用接口获取反馈的响应中获取数据列表
     * 返回null表示调用或响应状态异常
     *
     * @param response
     * @param <T>
     * @return
     */
    private <T> List<T> getDataListByResponse(BaseDmsAutoJsfResponse<List<T>> response) {
        if (response != null && response.getStatusCode() == BaseDmsAutoJsfResponse.SUCCESS_CODE) {
            List<T> data = response.getData();
            if (data != null && data.size() > 0) {
                return data;
            } else {
                return Collections.EMPTY_LIST;
            }
        } else {
            return null;
        }
    }

}
