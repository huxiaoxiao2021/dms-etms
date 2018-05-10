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
	/**
	 * 根据分拣中心和省市查询库位流向配置
	 * @param dmsSiteCode
	 * @param dmsProvinceCode
	 * @param dmsCityCode
	 * @return
	 */
    DmsStorageArea findByDesProvinceAndCityCode(Integer dmsSiteCode,Integer dmsProvinceCode,Integer dmsCityCode);
    /**
     * 查询分拣中心下省对应的配置，城市为空
     * @param dmsSiteCode
     * @param dmsProvinceCode
     * @return
     */
    DmsStorageArea findByDesProvinceCode(Integer dmsSiteCode,Integer dmsProvinceCode);

}
