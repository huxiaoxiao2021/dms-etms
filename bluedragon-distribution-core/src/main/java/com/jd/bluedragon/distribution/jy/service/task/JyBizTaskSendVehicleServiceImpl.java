package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDao;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizSendTaskAssociationDto;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendLineTypeCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailQueryEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("jyBizTaskSendVehicleService")
public class JyBizTaskSendVehicleServiceImpl implements JyBizTaskSendVehicleService{

    private static final Logger logger = LoggerFactory.getLogger(JyBizTaskSendVehicleServiceImpl.class);

    @Autowired
    JyBizTaskSendVehicleDao jyBizTaskSendVehicleDao;

    @Autowired
    private UccPropertyConfiguration ucc;
    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;


    @Override
    public String genMainTaskBizId() {
        String ownerKey = String.format(JyBizTaskSendVehicleEntity.BIZ_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }

    @Override
    public JyBizTaskSendVehicleEntity findByBizId(String bizId) {
        return jyBizTaskSendVehicleDao.findByBizId(bizId);
    }

    @Override
    public int saveSendVehicleTask(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.insert(entity);
    }

    @Override
    public int updateSendVehicleTask(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateByBizId(entity);
    }

    /**
     * 更新到来时间或者即将到来时间，取最小值为准更新
     * @param entity
     * @return
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateComeTimeOrNearComeTime",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateComeTimeOrNearComeTime(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateComeTimeOrNearComeTime(entity);
    }

    @Override
    public JyBizTaskSendVehicleEntity findByTransWorkAndStartSite(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.findByTransWorkAndStartSite(entity);
    }

    @Override
    public List<JyBizTaskSendVehicleEntity> findByTransWork(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.findByTransWork(entity);
    }

    @Override
    public int initTaskSendVehicle(JyBizTaskSendVehicleEntity entity) {
        JyBizTaskSendVehicleEntity sendTaskQ = new JyBizTaskSendVehicleEntity(entity.getTransWorkCode(), entity.getStartSiteId());
        if (this.findByTransWorkAndStartSite(sendTaskQ) == null) {
            return jyBizTaskSendVehicleDao.initTaskSendVehicle(entity);
        }

        return 0;
    }

    @Override
    public int initAviationTaskSendVehicle(JyBizTaskSendVehicleEntity entity) {
        if (this.findByBookingCode(entity.getBookingCode(), entity.getStartSiteId()) == null) {
            return jyBizTaskSendVehicleDao.initTaskSendVehicle(entity);
        }
        return 0;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.sumTaskByVehicleStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendCountDto> sumTaskByVehicleStatus(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        return jyBizTaskSendVehicleDao.sumTaskByVehicleStatus(entity, sendVehicleBizList);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.sumTaskByLineType",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendLineTypeCountDto> sumTaskByLineType(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        return jyBizTaskSendVehicleDao.sumTaskByLineType(entity, sendVehicleBizList);
    }

    @Override
    public List<JyBizTaskSendCountDto> sumTaskByVehicleStatusForTransfer(
        JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        return jyBizTaskSendVehicleDao.sumTaskByVehicleStatusForTransfer(entity, sendVehicleBizList);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.querySendTaskOfPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendVehicleEntity> querySendTaskOfPage(JyBizTaskSendVehicleEntity entity,
                                                                List<String> sendVehicleBizList,
                                                                JyBizTaskSendSortTypeEnum typeEnum,
                                                                Integer pageNum, Integer pageSize,
                                                                List<Integer> statuses) {
        Integer limit = pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        // 超过最大分页数据量 直接返回空数据
        if (offset + limit > ucc.getJyTaskPageMax()) {
            return new ArrayList<>();
        }

        return jyBizTaskSendVehicleDao.querySendTaskOfPage(entity, sendVehicleBizList, typeEnum, offset, limit, statuses);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.countByCondition",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Integer countByCondition(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList, List<Integer> statuses) {
        return jyBizTaskSendVehicleDao.countByCondition(entity, sendVehicleBizList, statuses);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateLastPlanDepartTimeAndLineType",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateLastPlanDepartTimeAndLineType(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateByBizId(entity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateLastSealCarTime",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateLastSealCarTime(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateByBizId(entity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateStatus(JyBizTaskSendVehicleEntity entity, Integer oldStatus) {
        return jyBizTaskSendVehicleDao.updateStatus(entity, oldStatus);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateStatusWithoutCompare",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateStatusWithoutCompare(JyBizTaskSendVehicleEntity entity, Integer oldStatus) {
        return jyBizTaskSendVehicleDao.updateStatusWithoutCompare(entity, oldStatus);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateBizTaskSendStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateBizTaskSendStatus(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateBizTaskSendStatus(entity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.findSendTaskByDestOfPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendVehicleEntity> findSendTaskByDestOfPage(JyBizTaskSendVehicleDetailEntity entity, Integer pageNum, Integer pageSize) {
        Integer limit = pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        // 超过最大分页数据量 直接返回空数据
        if (offset + limit > ucc.getJyTaskPageMax()) {
            return new ArrayList<>();
        }
        return jyBizTaskSendVehicleDao.findSendTaskByDestOfPage(entity, offset, limit);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.countSendTaskByDest",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Integer countSendTaskByDest(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDao.countSendTaskByDest(entity);
    }

    @Override
    public List<JyBizTaskSendVehicleEntity> findSendTaskByTransWorkCode(List<String> transWorkCodeList,Long startSiteId) {
        return jyBizTaskSendVehicleDao.findSendTaskByTransWorkCode(transWorkCodeList,startSiteId);
    }

	@Override
	public int countBizNumForCheckLineType(JyBizTaskSendVehicleEntity checkQuery, List<String> bizIdList,List<Integer> lineTypes) {
        return jyBizTaskSendVehicleDao.countBizNumForCheckLineType(checkQuery,bizIdList,lineTypes);
	}

    @Override
    public List<JyBizTaskSendVehicleEntity> findSendTaskByBizIds(List<String> bizIds) {
        return jyBizTaskSendVehicleDao.findSendTaskByBizIds(bizIds);
    }

    @Override
    public JyBizTaskSendVehicleEntity findByBookingCode(String bookingCode, Long startSiteId) {
        return jyBizTaskSendVehicleDao.findByBookingCode(bookingCode, startSiteId, false);
    }

    @Override
    public JyBizTaskSendVehicleEntity findByBookingCodeIgnoreYn(String bookingCode, Long startSiteId) {
        return jyBizTaskSendVehicleDao.findByBookingCode(bookingCode, startSiteId, true);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.countDetailSendTaskByCondition",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Integer countDetailSendTaskByCondition(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDao.countDetailSendTaskByCondition(entity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.pageFindDetailSendTaskByCondition",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizSendTaskAssociationDto> pageFindDetailSendTaskByCondition(JyBizTaskSendVehicleDetailQueryEntity entity, Integer pageNo, Integer pageSize) {

        Integer limit = pageSize;
        Integer offset = (pageNo - 1) * pageSize;
        // 超过最大分页数据量 直接返回空数据
        if (offset + limit > ucc.getJyTaskPageMax()) {
            return new ArrayList<>();
        }
        return jyBizTaskSendVehicleDao.pageFindDetailSendTaskByCondition(entity, offset, limit);
    }

}
