package com.jd.bluedragon.distribution.consumable.dao;

import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: PackingConsumableInfoDao
 * @Description: 包装耗材信息表--Dao接口
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public interface PackingConsumableInfoDao extends Dao<PackingConsumableInfo> {

    /*
    * 根据耗材编号获取信息
    * */
    PackingConsumableBaseInfo getPackingConsumableInfoByCode(String code);

    /**
     * @Description 批量获取耗材
     * @param [codes]
     * @Author wyh
     * @Date 2020/3/16 14:28
     * @return java.util.List<com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo>
     **/
    List<PackingConsumableInfo> listPackingConsumableInfoByCodes(List<String> codes);
}
