package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("jyBizTaskSendVehicleDetailService")
public class JyBizTaskSendVehicleDetailServiceImpl implements JyBizTaskSendVehicleDetailService{

    private static final Logger logger = LoggerFactory.getLogger(JyBizTaskSendVehicleDetailServiceImpl.class);

    @Autowired
    JyBizTaskSendVehicleDetailDao jyBizTaskSendVehicleDetailDao;

    @Override
    public JyBizTaskSendVehicleDetailEntity findByBizId(String bizId) {
        return jyBizTaskSendVehicleDetailDao.findByBizId(bizId);
    }

    @Override
    public int updateDateilTaskByVehicleBizId(JyBizTaskSendVehicleDetailEntity detailEntity) {
        return jyBizTaskSendVehicleDetailDao.updateDateilTaskByVehicleBizId(detailEntity);
    }

    @Override
    public int saveTaskSendDetail(JyBizTaskSendVehicleDetailEntity detailEntity) {
        long detailId = jyBizTaskSendVehicleDetailDao.findByTransWorkItem(detailEntity);
        if (detailId > 0) {
            logger.warn("派车单明细已经存在. id:{}, {}", detailId, JsonHelper.toJson(detailEntity));
            return 0;
        }
        return jyBizTaskSendVehicleDetailDao.save(detailEntity);
    }

    @Override
    public List<JyBizTaskSendVehicleDetailEntity> findEffectiveSendVehicleDetail(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.findByMainVehicleBiz(entity);
    }

    @Override
    public int cancelDetail(JyBizTaskSendVehicleDetailEntity detailEntity) {
        detailEntity.setVehicleStatus(JyBizTaskSendDetailStatusEnum.CANCEL.getCode());
        detailEntity.setYn(Constants.YN_NO); // 删除取消的流向
        return jyBizTaskSendVehicleDetailDao.updateByBiz(detailEntity);
    }

    @Override
    public int countByStatus(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.countByStatus(entity);
    }

    @Override
    public List<Long> getAllSendDest(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDetailDao.getAllSendDest(entity);
    }
}
