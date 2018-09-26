package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationCondition;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationDetailInfo;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
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

    /*
    * 根据分拣中心编号获取耗材信息
    * */
    List<PackingConsumableBaseInfo> getPackingConsumableInfoByDmsId(Integer dmsId);

    /*
     * 查询分拣中心耗材明细信息
     *
     */
    PagerResult<DmsConsumableRelationDetailInfo> queryDetailInfoByPagerCondition(DmsConsumableRelationCondition dmsConsumableRelationCondition);

    /*
    * 批量启用耗材
    * */
    boolean enableByCodes(List<String> codes, DmsConsumableRelation dmsConsumableRelation);

    /*
   * 批量停用耗材
   * */
    boolean disableByCodes(List<String> codes, DmsConsumableRelation dmsConsumableRelation);

}
