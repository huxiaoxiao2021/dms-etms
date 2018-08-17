package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: DmsConsumableRelationService
 * @Description: 分拣中心耗材关系表--Service接口
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public interface DmsConsumableRelationService extends Service<DmsConsumableRelation> {

    List<PackingConsumableBaseInfo> getPackingConsumableInfoByDmsId(Integer dmsId);

}
