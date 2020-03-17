package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

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

    /**
     * @Description 批量获取耗材
     * @param [codes]
     * @Author wyh
     * @Date 2020/3/16 14:28
     * @return java.util.List<com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo>
     **/
    List<PackingConsumableInfo> listPackingConsumableInfoByCodes(List<String> codes);
}
