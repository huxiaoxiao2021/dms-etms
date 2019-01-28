package com.jd.bluedragon.core.base;

import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.CrossDmsBox;
import com.jd.ql.basic.dto.BaseDmsStoreDto;

public interface BasicSafInterfaceManager {

    /**
     * @param cky2
     * @param orgId  机构号
     * @param storeId 仓储号
     * @return
     */
    public abstract BaseResult<BaseDmsStoreDto> getDmsInfoByStoreInfo(Integer cky2, Integer orgId,
                                                                      Integer storeId);

    public BaseResult<CrossDmsBox> getCrossDmsBoxByOriAndDes(Integer createSiteCode, Integer targetId);

    BaseResult<String> getCrossDmsBox(Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 根据库房信息获取绑定分拣中心Id
     * @param storeType 库房类型 正常大库库房类型为： wms 备件库类型为：spwms
     * @param cky2 配送中心
     * @param storeId 库房ID
     * @return
     */
    Integer getStoreBindDms(String storeType,Integer cky2,Integer storeId);

}