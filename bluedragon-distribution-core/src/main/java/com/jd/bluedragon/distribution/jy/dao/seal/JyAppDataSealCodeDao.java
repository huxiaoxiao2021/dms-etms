package com.jd.bluedragon.distribution.jy.dao.seal;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSealCode;

/**
 * @ClassName: JyAppDataSealCodeDao
 * @Description: 作业app-封车页面-封签编码明细表--Dao接口
 * @author wuyoude
 * @date 2022年09月27日 15:38:11
 *
 */
@Repository("jyAppDataSealCodeDao")
public class JyAppDataSealCodeDao extends BaseDao<JyAppDataSealCode> {

    final static String NAMESPACE = JyAppDataSealCodeDao.class.getName();

	public int batchInsert(List<JyAppDataSealCode> sealCodes) {
		return this.getSqlSession().insert(NAMESPACE + ".batchInsert", sealCodes);
	}
	public int deleteByDetailBizId(String detailBizId) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByDetailBizId", detailBizId);
	}	
	public List<String> querySealCodeList(String sendVehicleDetailBizId) {
		return this.getSqlSession().selectList(NAMESPACE + ".querySealCodeList", sendVehicleDetailBizId);
	}
}
