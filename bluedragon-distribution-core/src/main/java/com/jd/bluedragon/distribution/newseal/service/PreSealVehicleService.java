package com.jd.bluedragon.distribution.newseal.service;

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import com.jd.bluedragon.distribution.newseal.domain.VehicleMeasureInfo;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PreSealVehicleService
 * @Description: 预封车数据表--Service接口
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
public interface PreSealVehicleService extends Service<PreSealVehicle> {

    //新建标识
    int NEW_FLAG = 1;

    //取消标识
    int CANCEL_FLAG = 2;

    /**
     * 新增一条预封车数据
     * @param preSealVehicle
     * @return
     */
    boolean insert(PreSealVehicle preSealVehicle);

    /**
     * 新增一条预封车数据,并取消之前的预封车（取消条件：始发，目的，状态预封车）
     * @param preSealVehicle
     * @return
     */
    boolean cancelPreSealBeforeInsert(PreSealVehicle preSealVehicle);

    /**
     * 根据ID更新预封车数据
     * @param preSealVehicle
     * @return
     */
    boolean updateById(PreSealVehicle preSealVehicle);

    /**
     * 根据Id更新单条预封车数据状态
     * @param id
     * @param updateUserErp
     * @param updateUserName
     * @param status
     * @return
     */
    boolean updateStatusById(Long id, String updateUserErp, String updateUserName, SealVehicleEnum status);

    /**
     * 根据Ids批量更新预封车数据状态
     * @param ids
     * @param updateUserErp
     * @param updateUserName
     * @param status
     * @return
     */
    int updateStatusByIds(List<Long> ids, String updateUserErp, String updateUserName, SealVehicleEnum status);

    /**
     * 根据始发目的查询预封车状态的数据
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    List<PreSealVehicle> findByCreateAndReceive(Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 查询场地在startDate以内的预封车数据
     *
     * @param createSiteCode
     * @return
     */
    List<PreSealVehicle> queryBySiteCode(Integer createSiteCode);

    /**
     * 查询场地和车牌号查询预封车数据
     *
     */
    List<PreSealVehicle> queryBySiteCodeAndVehicleNumber(Integer createSiteCode, String vehicleNumber);
    /**
     * 根据始发查询当天已使用预封车的运力编码
     * @param createSiteCode
     * @return
     */
    List<String> findTodayUsedTransports(Integer createSiteCode);

    /**
     * 执行批量封车
     * @param preList
     * @param updateUserErp
     * @param updateUserName
     * @param operateTime
     * @return
     * @throws Exception
     */
    boolean batchSeal(List<PreSealVehicle> preList, Integer updateUserCode, String updateUserErp, String updateUserName, Date operateTime ) throws Exception;

    /**
     * 根据运力编码获取预封车信息
     * @param transportCode
     * @return
     */
    PreSealVehicle getPreSealVehicleInfo(String transportCode);

    /**
     * 根据运力编码获取预封车体积重量信息
     * @param transportCode
     * @return
     */
    List<VehicleMeasureInfo> getVehicleMeasureInfoList(String transportCode);

    /**
     * 更新预封车体积重量信息
     * @param preSealVehicle
     * @return
     */
    boolean updatePreSealVehicleMeasureInfo(PreSealVehicle preSealVehicle);

    /**
     * 通知运输创建或取消传摆预封车任务
     *
     * */
    void notifyVosPreSealJob(PreSealVehicle preSealVehicle, int flag);


    /*
     * 根据运力编码获取预封车信息
     * */
    List<PreSealVehicle> getPreSealInfoByParams(String transportCode);

    /*
     * 根据运力编码和车牌信息获取预封车信息
     * */
    List<PreSealVehicle> getPreSealInfoByParams(String transportCode, String vehicleNumber);

    /**
     * 更新预封车记录为完成状态
     * @param preSealVehicle
     * @return
     */
    boolean completePreSealVehicleRecord(PreSealVehicle preSealVehicle);

}
