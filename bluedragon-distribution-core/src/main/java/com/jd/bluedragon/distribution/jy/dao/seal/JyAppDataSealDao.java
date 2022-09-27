package com.jd.bluedragon.distribution.jy.dao.seal;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSeal;

/**
 * @ClassName: JyAppDataSealDao
 * @Description: 作业app-封车主页面数据表--Dao接口
 * @author wuyoude
 * @date 2022年09月27日 15:38:11
 *
 */
@Repository("jyAppDataSealDao")
public class JyAppDataSealDao extends BaseDao<JyAppDataSeal> {

    final static String NAMESPACE = JyAppDataSealDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyAppDataSeal entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
	public int updateById(JyAppDataSeal sealData) {
        return this.getSqlSession().update(NAMESPACE + ".updateById", sealData);
	}
	public JyAppDataSeal queryByDetailBizId(String sendVehicleDetailBizId) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryByDetailBizId", sendVehicleDetailBizId);
	}
}
