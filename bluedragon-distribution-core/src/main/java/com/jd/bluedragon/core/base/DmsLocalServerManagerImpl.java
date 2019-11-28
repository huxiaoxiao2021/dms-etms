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
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger log = LoggerFactory.getLogger(DmsLocalServerManagerImpl.class);

    @Autowired
    @Qualifier("dmsLocalServerInfoJsfService")
    private DmsLocalServerInfoJsfService dmsLocalServerInfoJsfService;

    @Autowired
    @Qualifier("storageComponentJsfService")
    private StorageComponentJsfService storageComponentJsfService;

    @Autowired
    @Qualifier("dmsLocalInstanceInfoJsfService")
    private DmsLocalInstanceInfoJsfService dmsLocalInstanceInfoJsfService;

    @Override
    public List<VipInfoJsfEntity> getVipListByDmsId(Integer dmsId) {
        if (dmsId == null) {
            return Collections.emptyList();
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsLocalServerManagerImpl.getVipListByDmsId", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            return this.getDataListByResponse(dmsLocalServerInfoJsfService.getVipListByDmsId(dmsId));
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("[JSF接口调用异常]调用自动化JSF接口getVipListByDmsId()获取分拣中心本地服务器VIP地址出现异常：{}",dmsId, e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    @Override
    public List<VipInfoJsfEntity> getAllVipList() {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsLocalServerManagerImpl.getAllVipList", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            return this.getDataListByResponse(dmsLocalServerInfoJsfService.getAllVipList());
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("[JSF接口调用异常]调用自动化JSF接口getAllVipList()获取分拣中心本地服务器VIP地址出现异常", e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    @Override
    public List<String> getStorageCodeByDmsId(Integer dmsId) {
        ComponentPositionInfoJsfRequest param = new ComponentPositionInfoJsfRequest();
        List<String> result = new ArrayList<String>();
        param.setCreateSiteCode(dmsId.toString());
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsLocalServerManagerImpl.getStorageCodeByDmsId", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            BaseDmsAutoJsfResponse<List<ComponentPositionJsfEntity>> baseDmsAutoJsfResponse = storageComponentJsfService.getPositionBySite(param);
            if(BaseDmsAutoJsfResponse.SUCCESS_CODE == baseDmsAutoJsfResponse.getStatusCode()){
                List<ComponentPositionJsfEntity> lst = baseDmsAutoJsfResponse.getData();
                for(ComponentPositionJsfEntity pojo : lst){
                    result.add(pojo.getCode());
                }
            }else{
                log.warn("[JSF接口调用失败]调用自动化JSF接口getPositionBySite  {}-{}",JsonHelper.toJson(param),baseDmsAutoJsfResponse.getStatusMessage());
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("[JSF接口调用异常]调用自动化JSF接口getPositionBySite:{} ", JsonHelper.toJson(param), e);
        }finally {
            Profiler.registerInfoEnd(info);
        }

        return result;
    }

    @Override
    public String getVerAppUrlBySiteCode(String siteCode) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsLocalServerManagerImpl.getVerAppUrlBySiteCode", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            BaseDmsAutoJsfResponse<String> response=dmsLocalInstanceInfoJsfService.getSortSchemeUrlBySiteCode(siteCode);
            if (response != null && response.getStatusCode() == BaseDmsAutoJsfResponse.SUCCESS_CODE){
                return response.getData();
            }else {
                log.warn("[JSF接口调用失败]调用自动化JSF接口getVerAppUrlBySiteCode  {},返回结果：{}" ,JsonHelper.toJson(siteCode), JsonHelper.toJson(response));
            }

        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("[JSF接口调用异常]调用自动化JSF接口getVerAppUrlBySiteCode获取分拣中心本地服务器地址出现异常:{}",siteCode, e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    @Override
    public boolean checkStorage(Integer dmsId, String storageCode) {
        ComponentPositionInfoJsfRequest param = new ComponentPositionInfoJsfRequest();
        param.setCreateSiteCode(dmsId.toString());
        param.setPositionCode(storageCode);

        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsLocalServerManagerImpl.checkStorage", Constants.UMP_APP_NAME_DMSWEB,false, true);
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
                log.warn("[JSF接口调用失败]调用自动化JSF接口getPositionBySiteAndCode {}-{}  ",JsonHelper.toJson(param),baseDmsAutoJsfResponse.getStatusMessage());
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("[JSF接口调用异常]调用自动化JSF接口getPositionBySiteAndCode :{}",JsonHelper.toJson(param), e);
        }finally {
            Profiler.registerInfoEnd(info);
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
