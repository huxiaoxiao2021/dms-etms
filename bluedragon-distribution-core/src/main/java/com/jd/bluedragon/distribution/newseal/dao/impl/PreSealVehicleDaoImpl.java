package com.jd.bluedragon.distribution.newseal.dao.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import com.jd.bluedragon.distribution.newseal.domain.VehicleMeasureInfo;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicleCondition;
import com.jd.bluedragon.distribution.newseal.dao.PreSealVehicleDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PreSealVehicleDaoImpl
 * @Description: 预封车数据表--Dao接口实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Repository("preSealVehicleDao")
public class PreSealVehicleDaoImpl extends BaseDao<PreSealVehicle> implements PreSealVehicleDao {

    @Override
    public int updateStatusByIds(List<Long> ids, String updateUserErp, String updateUserName, Integer status) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("list", ids);
        parameter.put("updateUserErp", updateUserErp);
        parameter.put("updateUserName", updateUserName);
        parameter.put("status", status);
        parameter.put("updateTime", new Date());
        return sqlSession.update(this.nameSpace+".updateStatusByIds", parameter);
    }

    @Override
    public int updateStatusByTransportCodes(List<String> transportCodes, String updateUserErp, String updateUserName, Integer status) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("list", transportCodes);
        parameter.put("updateUserErp", updateUserErp);
        parameter.put("updateUserName", updateUserName);
        parameter.put("status", status);
        parameter.put("updateTime", new Date());
        return sqlSession.update(this.nameSpace+".updateStatusByTransportCodes", parameter);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.PreSealVehicleDaoImpl.updateStatusByTransportCodesAndVehicleNumbers", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP})
    public int updateStatusByTransportCodesAndVehicleNumbers(List<String> transportCodes, List<String> vehicleNumbers, String updateUserErp, String updateUserName, int status) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("transportCodes", transportCodes);
        parameter.put("vehicleNumbers", vehicleNumbers);
        parameter.put("updateUserErp", updateUserErp);
        parameter.put("updateUserName", updateUserName);
        parameter.put("updateTime", new Date());
        parameter.put("status", status);
        return sqlSession.update(this.nameSpace+".updateStatusByTransportCodesAndVehicleNumbers", parameter);
    }

    @Override
    public List<PreSealVehicle> findByCreateAndReceive(Integer createSiteCode, Integer receiveSiteCode) {
        PreSealVehicle query = new PreSealVehicle();
        query.setCreateSiteCode(createSiteCode);
        query.setReceiveSiteCode(receiveSiteCode);
        query.setStatus(SealVehicleEnum.PRE_SEAL.getCode());
        return sqlSession.selectList(this.nameSpace+".findByCreateAndReceive", query);
    }

    @Override
    public int preCancelByCreateAndReceive(Integer createSiteCode, Integer receiveSiteCode, String updateUserErp, String updateUserName) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("createSiteCode", createSiteCode);
        parameter.put("receiveSiteCode", receiveSiteCode);
        parameter.put("updateUserErp", updateUserErp);
        parameter.put("updateUserName", updateUserName);
        parameter.put("updateTime", new Date());
        return sqlSession.update(this.nameSpace+".preCancelByCreateAndReceive", parameter);
    }

    @Override
    public List<PreSealVehicle> queryByCondition(PreSealVehicle preSealVehicle) {
        return sqlSession.selectList(this.nameSpace+".queryByCondition", preSealVehicle);
    }

    @Override
    public List<String> findUsedTransports(Integer createSiteCode, Date startDate) {
        PreSealVehicle query = new PreSealVehicle();
        query.setCreateSiteCode(createSiteCode);
        query.setCreateTime(startDate);
        return sqlSession.selectList(this.nameSpace+".findUsedTransports", query);
    }

    @Override
    public PreSealVehicle getPreSealVehicleInfo(String transportCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("transportCode", transportCode);
        return sqlSession.selectOne(this.nameSpace+".getPreSealVehicleInfo", params);
    }

    @Override
    public List<VehicleMeasureInfo> getVehicleMeasureInfoList(String transportCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("transportCode", transportCode);
        return sqlSession.selectList(this.nameSpace+".getVehicleMeasureInfoList", params);
    }

    @Override
    public int updatePreSealVehicleMeasureInfo(PreSealVehicle preSealVehicle) {
        return sqlSession.update(this.nameSpace+".updatePreSealVehicleMeasureInfo", preSealVehicle);
    }

    /*
     * 根据运力编码获取预封车信息
     * */
    @Override
    public List<PreSealVehicle> getPreSealInfoByParams(Map<String, Object> params) {
        return sqlSession.selectList(this.nameSpace+".getPreSealInfoByParams", params);
    }

    @Override
    public int completePreSealVehicleRecord(PreSealVehicle preSealVehicle) {
        return sqlSession.update(this.nameSpace+".completePreSealVehicleRecord", preSealVehicle);

    }

	@Override
	public Integer countPreSealNumByTransportInfo(PreSealVehicleCondition condition) {
		return sqlSession.selectOne(this.nameSpace+".countPreSealNumByTransportInfo", condition);
	}

	@Override
	public Integer countPreSealNumBySendRelation(PreSealVehicleCondition condition) {
		return sqlSession.selectOne(this.nameSpace+".countPreSealNumBySendRelation", condition);
	}

	@Override
	public List<String> findOtherUuidsByCreateAndReceive(PreSealVehicleCondition condition) {
		return sqlSession.selectList(this.nameSpace+".findOtherUuidsByCreateAndReceive", condition);
	}

	@Override
	public List<PreSealVehicle> queryUnSealVehicleInfo(PreSealVehicleCondition condition) {
		return sqlSession.selectList(this.nameSpace+".queryUnSealVehicleInfo", condition);
	}
}
