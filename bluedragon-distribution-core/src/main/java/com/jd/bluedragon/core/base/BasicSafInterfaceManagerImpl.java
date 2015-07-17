package com.jd.bluedragon.core.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.etms.basic.domain.BaseDmsStore;
import com.jd.etms.basic.domain.BaseResult;
import com.jd.etms.basic.dto.BaseDmsStoreDto;
import com.jd.etms.basic.saf.BasicSafInterface;
import com.jd.etms.utils.cache.annotation.Cache;

@Service("basicSafInterfaceManager")
public class BasicSafInterfaceManagerImpl implements BasicSafInterfaceManager {
	
	public static final String SEPARATOR_HYPHEN = "-";

	@Autowired
	@Qualifier("basicSafInterface")
	BasicSafInterface basicSafInterface;
	
    @Override
	@Cache(key = "basicSafInterfaceManager.getTraderInfoPopCodeAll", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public BaseResult<BaseDmsStoreDto> getDmsInfoByStoreInfo(Integer cky2, Integer orgId, Integer storeId){
    	BaseDmsStore bdStore = new BaseDmsStore();
    	bdStore.setCky2(cky2);
    	bdStore.setOrgId(orgId);
    	bdStore.setStoreId(storeId);
        return basicSafInterface.getDmsInfoByStoreInfo(bdStore);
    }
}
