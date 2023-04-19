package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.send.BatchTransferDto;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCodeDto;
import com.jd.bluedragon.distribution.jy.dto.send.TransferDto;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发货批次关系表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendCodeDao extends BaseDao<JySendCodeEntity> {

    private final static String NAMESPACE = JySendCodeDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendCodeEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int batchInsert(List<JySendCodeEntity> list) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", list);
    }

    /**
     * 更新
     *
     * @param
     * @return
     */
    public int updateBySendCode(TransferDto dto) {
        return this.getSqlSession().update(NAMESPACE + ".updateBySendCode", dto);
    }

    public int deleteVehicleSendRelation(TransferDto dto){
        return this.getSqlSession().update(NAMESPACE + ".deleteVehicleSendRelation", dto);
    }

    /**
     * 批量更新
     *
     * @param
     * @return
     */
    public int batchUpdateBySendCode(BatchTransferDto dto) {
        return this.getSqlSession().update(NAMESPACE + ".batchUpdateBySendCode", dto);
    }

    public List<String> querySendCodesByVehicleDetailBizId(String vehicleDetailBizId){
        return this.getSqlSession().selectList(NAMESPACE + ".querySendCodesByVehicleDetailBizId", vehicleDetailBizId);
    }

    public String findEarliestSendCode(String vehicleDetailBizId){
        return this.getSqlSession().selectOne(NAMESPACE + ".findEarliestSendCode", vehicleDetailBizId);
    }

    public List<String> querySendCodesByVehicleBizId(String vehicleBizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".querySendCodesByVehicleBizId", vehicleBizId);
    }

    public int deleteVehicleSendRelationByVehicleBizId(JySendCodeDto dto) {
        return this.getSqlSession().update(NAMESPACE + ".deleteVehicleSendRelationByVehicleBizId", dto);
    }

    public int deleteVehicleSendRelationByVehicleDetailBizId(JySendCodeEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".deleteVehicleSendRelationByVehicleDetailBizId", entity);
    }

    public List<JySendCodeEntity> querySendDetailBizIdBySendCode(List<String> sendCodeList) {
        return this.getSqlSession().selectList(NAMESPACE + ".querySendDetailBizIdBySendCode", sendCodeList);
    }

    public List<JySendCodeEntity> queryByVehicleBizId(String vehicleBizId){
        return this.getSqlSession().selectList(NAMESPACE + ".queryByVehicleBizId", vehicleBizId);
    }

    public List<JySendCodeEntity> queryByVehicleDetailBizId(String vehicleDetailBizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryByVehicleDetailBizId", vehicleDetailBizId);
    }
}
