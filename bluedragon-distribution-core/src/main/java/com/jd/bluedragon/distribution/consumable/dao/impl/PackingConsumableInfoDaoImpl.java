package com.jd.bluedragon.distribution.consumable.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.dao.PackingConsumableInfoDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

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


}
