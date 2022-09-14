package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleInfo;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleStatusEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("jyBizTaskSendVehicleDetailService")
public class JyBizTaskSendVehicleDetailServiceImpl implements JyBizTaskSendVehicleDetailService{

    private static final Logger logger = LoggerFactory.getLogger(JyBizTaskSendVehicleDetailServiceImpl.class);

    @Autowired
    JyBizTaskSendVehicleDetailDao jyBizTaskSendVehicleDetailDao;
    @Autowired
    JyBizTaskSendVehicleDao jyBizTaskSendVehicleDao;

    @Override
    public JyBizTaskSendVehicleDetailEntity findByBizId(String bizId) {
        return jyBizTaskSendVehicleDetailDao.findByBizId(bizId);
    }

    @Override
    public int updateDateilTaskByVehicleBizId(JyBizTaskSendVehicleDetailEntity detailEntity) {
        return jyBizTaskSendVehicleDetailDao.updateDateilTaskByVehicleBizId(detailEntity);
    }

    @Override
    public int updateByBiz(JyBizTaskSendVehicleDetailEntity detailEntity) {
        return jyBizTaskSendVehicleDetailDao.updateByBiz(detailEntity);
    }

    @Override
    public int saveTaskSendDetail(JyBizTaskSendVehicleDetailEntity detailEntity) {
        Long detailId = jyBizTaskSendVehicleDetailDao.findByCondition(detailEntity);
        if (NumberHelper.gt0(detailId)) {
            logger.warn("派车单明细已经存在. id:{}, {}", detailId, JsonHelper.toJson(detailEntity));
            return 0;
        }
        return jyBizTaskSendVehicleDetailDao.save(detailEntity);
    }

    @Override
    public List<JyBizTaskSendVehicleDetailEntity> findEffectiveSendVehicleDetail(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.findByMainVehicleBiz(entity, null);
    }

    @Override
    public List<JyBizTaskSendVehicleDetailEntity> findNoSealCarSendVehicleDetail(JyBizTaskSendVehicleDetailEntity entity) {
        List<Integer> status =new ArrayList<>();
        status.add(JyBizTaskSendStatusEnum.TO_SEND.getCode());
        status.add(JyBizTaskSendStatusEnum.SENDING.getCode());
        status.add(JyBizTaskSendStatusEnum.TO_SEAL.getCode());
        return jyBizTaskSendVehicleDetailDao.findByMainVehicleBiz(entity, status);
    }

    @Override
    public List<JyBizTaskSendVehicleDetailEntity> findBySiteAndStatus(JyBizTaskSendVehicleDetailEntity entity, List<Integer> statuses) {
        return jyBizTaskSendVehicleDetailDao.findByMainVehicleBiz(entity, statuses);
    }

    @Override
    public int cancelDetail(JyBizTaskSendVehicleDetailEntity detailEntity) {
        detailEntity.setVehicleStatus(JyBizTaskSendDetailStatusEnum.CANCEL.getCode());
        detailEntity.setYn(Constants.YN_NO); // 删除取消的流向
        return jyBizTaskSendVehicleDetailDao.updateByBiz(detailEntity);
    }

    @Override
    public int cancelDetailTaskAndMainTask(JyBizTaskSendVehicleDetailEntity detailEntity) {
        JyBizTaskSendVehicleDetailEntity detailTask =new JyBizTaskSendVehicleDetailEntity();
        detailTask.setBizId(detailEntity.getBizId());
        detailTask.setUpdateTime(new Date());
        detailTask.setYn(Constants.YN_NO);
        int r =jyBizTaskSendVehicleDetailDao.updateByBiz(detailTask);
        if (r>0){
            Integer noCancelCount =countNoCancelSendDetail(detailEntity);
            if (noCancelCount==null || noCancelCount<=0){
                JyBizTaskSendVehicleEntity mainTask =new JyBizTaskSendVehicleEntity();
                mainTask.setBizId(detailEntity.getSendVehicleBizId());
                mainTask.setVehicleStatus(JyBizTaskSendStatusEnum.CANCEL.getCode());
                mainTask.setYn(Constants.YN_NO);
                mainTask.setUpdateTime(new Date());
                r =r+jyBizTaskSendVehicleDao.updateByBizId(mainTask);
            }
        }
        return r;
    }

    @Override
    public int updateStatus(JyBizTaskSendVehicleDetailEntity detailEntity, Integer oldStatus) {
        return jyBizTaskSendVehicleDetailDao.updateStatus(detailEntity, oldStatus);
    }

    @Override
    public Integer countByCondition(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.countByCondition(entity);
    }

    @Override
    public List<Long> getAllSendDest(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.getAllSendDest(entity);
    }

    @Override
    public JyBizTaskSendVehicleDetailEntity findSendDetail(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.findSendDetail(entity);
    }

    @Override
    public List<JyBizTaskSendCountDto> sumByVehicleStatus(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.sumByVehicleStatus(entity);
    }

    @Override
    public int updateBizTaskSendDetailStatus(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.updateBizTaskSendDetailStatus(entity);
    }

    @Override
    public Integer countNoCancelSendDetail(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.countNoCancelSendDetail(entity);
    }

	@Override
	public List<String> findSendVehicleBizListBySendFlow(JyBizTaskSendVehicleDetailEntity entity) {
		return jyBizTaskSendVehicleDetailDao.findSendVehicleBizListBySendFlow(entity);
	}
}
