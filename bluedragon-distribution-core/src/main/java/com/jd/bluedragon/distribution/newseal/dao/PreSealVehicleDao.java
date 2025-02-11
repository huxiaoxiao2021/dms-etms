package com.jd.bluedragon.distribution.newseal.dao;

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicleCondition;
import com.jd.bluedragon.distribution.newseal.domain.VehicleMeasureInfo;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PreSealVehicleDao
 * @Description: 预封车数据表--Dao接口
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
public interface PreSealVehicleDao extends Dao<PreSealVehicle> {

    /**
     * 根据ids批量更新预封车数据状态
     *
     * @param ids
     * @param updateUserErp
     * @param updateUserName
     * @param status
     * @return
     */
    int updateStatusByIds(List<Long> ids, String updateUserErp, String updateUserName, Integer status);
    /**
     * 根据运力编码批量更新预封车数据状态
     *
     * @param transportCodes
     * @param updateUserErp
     * @param updateUserName
     * @param status
     * @return
     */
    int updateStatusByTransportCodes(List<String> transportCodes, String updateUserErp, String updateUserName, Integer status);

    /**
     * 根据运力编码和车牌批量更新预封车数据状态
     *
     * @param transportCodes
     * @param updateUserErp
     * @param updateUserName
     * @param status
     * @return
     */
    int updateStatusByTransportCodesAndVehicleNumbers(List<String> transportCodes, List<String> vehicleNumbers, String updateUserErp, String updateUserName, int status);

    /**
     * 根据始发目的取消预封车
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    int preCancelByCreateAndReceive(Integer createSiteCode, Integer receiveSiteCode, String updateUserErp, String updateUserName);

    /**
     * 根据始发目的查询预封车数据
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    List<PreSealVehicle> findByCreateAndReceive(Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 根据条件查询数据信息
     * @param preSealVehicle
     * @return
     */
    List<PreSealVehicle> queryByCondition(PreSealVehicle preSealVehicle);

    /**
     * 根据起始时间查询场地已使用预封车的运力编码
     * @param createSiteCode
     * @param startDate
     * @return
     */
    List<String> findUsedTransports(Integer createSiteCode, Date startDate);

    /**
     * 根据运力编码查询预封车信息
     * @param transportCode
     * @return
     */
    PreSealVehicle getPreSealVehicleInfo(String transportCode);

    /**
     * 根据运力编码查询预封车体积重量信息
     * @param transportCode
     * @return
     */
    List<VehicleMeasureInfo> getVehicleMeasureInfoList(String transportCode);

    /**
     * 更新预封车体积重量信息
     * @param preSealVehicle
     * @return
     */
    int updatePreSealVehicleMeasureInfo(PreSealVehicle preSealVehicle);

    /*
     * 根据运力编码获取预封车信息
     * */
    List<PreSealVehicle> getPreSealInfoByParams(Map<String, Object> params);

    /*
     * 完成预封车任务
     *
     * */
    int completePreSealVehicleRecord(PreSealVehicle preSealVehicle);
    /**
     * 根据运力编码及车牌查询预封车数据
     * @param condition
     * @return
     */
	Integer countPreSealNumByTransportInfo(PreSealVehicleCondition condition);
    /**
     * 根据始发和目的查询预封车数据
     * @param condition
     * @return
     */
	Integer countPreSealNumBySendRelation(PreSealVehicleCondition condition);
	/**
	 * 
	 * @param condition
	 * @return
	 */
	List<String> findOtherUuidsByCreateAndReceive(PreSealVehicleCondition condition);
	/**
	 * 一键封车-查询预封车数据
	 * @param condition
	 * @return
	 */
	List<PreSealVehicle> queryUnSealVehicleInfo(PreSealVehicleCondition condition);
}
