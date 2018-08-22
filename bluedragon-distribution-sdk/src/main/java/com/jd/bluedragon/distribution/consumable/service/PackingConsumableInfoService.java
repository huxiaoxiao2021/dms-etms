package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 *
 * @ClassName: PackingConsumableInfoService
 * @Description: 包装耗材信息表--Service接口
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public interface PackingConsumableInfoService extends Service<PackingConsumableInfo> {

    /*
    * 根据编号获取包装耗材信息
    * */
    PackingConsumableBaseInfo getPackingConsumableInfoByCode(String code);
}
