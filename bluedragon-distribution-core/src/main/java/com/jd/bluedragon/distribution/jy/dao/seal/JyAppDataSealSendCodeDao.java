package com.jd.bluedragon.distribution.jy.dao.seal;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSealCode;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSealSendCode;

/**
 * @ClassName: JyAppDataSealSendCodeDao
 * @Description: 作业app-封车页面-批次号明细表--Dao接口
 * @author wuyoude
 * @date 2022年09月27日 15:38:11
 *
 */
@Repository("jyAppDataSealSendCodeDao")
public class JyAppDataSealSendCodeDao extends BaseDao<JyAppDataSealSendCode> {
    
	final static String NAMESPACE = JyAppDataSealSendCodeDao.class.getName();

	public int batchInsert(List<JyAppDataSealSendCode> sendCodes) {
		return this.getSqlSession().insert(NAMESPACE + ".batchInsert", sendCodes);
	}
	public int deleteByDetailBizId(String detailBizId) {
		return this.getSqlSession().update(NAMESPACE + ".deleteByDetailBizId", detailBizId);
	}
	public List<String> querySendCodeList(String sendVehicleDetailBizId) {
		return this.getSqlSession().selectList(NAMESPACE + ".querySendCodeList", sendVehicleDetailBizId);
	}

}
