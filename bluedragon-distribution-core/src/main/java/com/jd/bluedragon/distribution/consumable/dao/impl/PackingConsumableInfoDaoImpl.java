package com.jd.bluedragon.distribution.consumable.dao.impl;

import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.dao.PackingConsumableInfoDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PackingConsumableInfoDaoImpl
 * @Description: 包装耗材信息表--Dao接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Repository("packingConsumableInfoDao")
public class PackingConsumableInfoDaoImpl extends BaseDao<PackingConsumableInfo> implements PackingConsumableInfoDao {


    @Override
    public PackingConsumableBaseInfo getPackingConsumableInfoByCode(String code) {
        return this.getSqlSession().selectOne(this.getNameSpace() + ".getPackingConsumableInfoByCode", code);
    }

    @Override
    public List<PackingConsumableInfo> listPackingConsumableInfoByCodes(List<String> codes) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("list", codes);
        return this.getSqlSession().selectList(this.getNameSpace() + ".listPackingConsumableInfoByCodes", paramMap);
    }
}
