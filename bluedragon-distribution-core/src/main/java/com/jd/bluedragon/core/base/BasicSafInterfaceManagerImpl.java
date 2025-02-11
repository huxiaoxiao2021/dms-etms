package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.CrossDmsBox;
import com.jd.ql.basic.dto.BaseDmsStoreDto;
import com.jd.ql.basic.ws.BasicMixedWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("basicSafInterfaceManager")
public class BasicSafInterfaceManagerImpl implements BasicSafInterfaceManager {

    public static final String SEPARATOR_HYPHEN = "-";

    @Autowired
    @Qualifier("basicMixedWS")
    BasicMixedWS basicMixedWS;

    @Override
    @Cache(key = "basicSafInterfaceManager.getTraderInfoPopCodeAll", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BasicSafInterfaceManagerImpl.getDmsInfoByStoreInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseResult<BaseDmsStoreDto> getDmsInfoByStoreInfo(Integer cky2, Integer orgId, Integer storeId){
        BaseDmsStore bdStore = new BaseDmsStore();
        bdStore.setCky2(cky2);
        bdStore.setOrgId(orgId);
        bdStore.setStoreId(storeId);
        return basicMixedWS.getDmsInfoByStoreInfo(bdStore);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.BasicSafInterfaceManagerImpl.getCrossDmsBoxByOriAndDes", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseResult<CrossDmsBox> getCrossDmsBoxByOriAndDes(Integer createSiteCode, Integer targetId) {
        // TODO Auto-generated method stub
        return basicMixedWS.getCrossDmsBoxByOriAndDes(createSiteCode, targetId);
    }

    @Override
    public BaseResult<String> getCrossDmsBox(Integer createSiteCode, Integer receiveSiteCode) {
        // TODO Auto-generated method stub
        return basicMixedWS.getCrossDmsBox(createSiteCode, receiveSiteCode);
    }

    /**
     * 根据库房信息获取绑定分拣中心Id
     * @param storeType 库房类型 正常大库库房类型为： wms 备件库类型为：spwms
     * @param cky2 配送中心
     * @param storeId 库房ID
     * @return
     */
    public BaseDmsStoreDto getStoreBindDms(String storeType,Integer cky2,Integer storeId){
        CallerInfo info = Profiler.registerInfo("DMS.BASE.BasicSafInterfaceManagerImpl.getStoreBindDms", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try{
            return basicMixedWS.getStoreBindDms(storeType,cky2,storeId);
        } catch (Exception e){
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    public Integer getStoreBindDmsCode(String storeType,Integer cky2,Integer storeId){
        BaseDmsStoreDto dto = getStoreBindDms(storeType,cky2,storeId);
        if(dto != null){
            return dto.getDmsId();
        }
        return null;
    }

}
