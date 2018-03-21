package com.jd.bluedragon.distribution.base.dao;

import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.ql.dms.common.web.mvc.api.Dao;

/**
 *
 * @ClassName: DmsStorageAreaDao
 * @Description: 分拣中心库位--Dao接口
 * @author wuyoude
 * @date 2018年03月13日 16:25:45
 *
 */
public interface DmsStorageAreaDao extends Dao<DmsStorageArea> {

    public abstract DmsStorageArea findByProAndCity(DmsStorageArea dmsStorageArea);
}
