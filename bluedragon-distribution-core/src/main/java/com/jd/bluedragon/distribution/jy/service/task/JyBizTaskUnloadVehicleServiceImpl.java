package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.config.dto.ClientAutoRefreshConfig;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskUnloadVehicleDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadStatisticsQueryTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadTaskLabelEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.task.autoRefresh.enums.ClientAutoRefreshBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.enums.JyAutoCloseTaskBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadAggsService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.JyUnloadTaskSignConstants;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/1
 * @Description: 到车卸车任务服务类
 */
@Service("jyBizTaskUnloadVehicleService")
public class JyBizTaskUnloadVehicleServiceImpl implements JyBizTaskUnloadVehicleService {

    private final static String JY_BIZ_TASK_BIZ_ID_PREFIX = "XCZJ%s";
    /**
     * 锁格式
     */
    private final static String JY_BIZ_TASK_UNLOAD_V_LOCK_KEY_FORMAT = "JY:B:T:UV:%s";
    /**
     * 锁失效时间 单位秒
     */
    private final static int LOCK_LOSE_TIME_OF_S = 10*60;
    /**
     * 一次自旋时间 单位毫秒
     */
    private final static int SPIN_TIME_OF_MS = 100;

    /**
     * 卸车岗任务类型： 1 分拣 2转运
     */
    public static final Integer UNLOAD_TASK_CATEGORY_TYS = 2;
    public static final Integer UNLOAD_TASK_CATEGORY_DMS = 1;

    private Logger logger = LoggerFactory.getLogger(JyBizTaskUnloadVehicleServiceImpl.class);

    @Autowired
    private JyBizTaskUnloadVehicleDao jyBizTaskUnloadVehicleDao;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private VosManager vosManager;

    @Autowired
    @Qualifier("redisJyBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;

    @Autowired
    @Qualifier("jyBizTaskAutoCloseProducer")
    private DefaultJMQProducer jyBizTaskAutoCloseProducer;

    /**
     * 根据bizId获取数据
     *
     * @param bizId
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findByBizId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleEntity findByBizId(String bizId) {
        return jyBizTaskUnloadVehicleDao.findByBizId(bizId);
    }

    /**
     * 根据bizId获取数据 忽略YN
     *
     * @param bizId
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findByBizId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleEntity findByBizIdIgnoreYn(String bizId) {
        return jyBizTaskUnloadVehicleDao.findByBizIdIgnoreYn(bizId);
    }

    /**
     * 获取6个小时内实际解封车顺序
     *
     * @param entity
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findRealRankingByBizId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleEntity findRealRankingByBizId(JyBizTaskUnloadVehicleEntity entity){
        return jyBizTaskUnloadVehicleDao.findRealRankingByBizId(entity);
    }

    /**
     * 根据派车明细编码获取数据
     *
     * @param transWorkItemCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findByTransWorkItemCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleEntity findByTransWorkItemCode(String transWorkItemCode) {
        return jyBizTaskUnloadVehicleDao.findByTransWorkItemCode(transWorkItemCode);
    }

    /**
     * 根据bizId获取数据只返回逻辑主键
     *
     * @param bizId
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findIdByBizId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Long findIdByBizId(String bizId) {
        return jyBizTaskUnloadVehicleDao.findIdByBizId(bizId);
    }

    /**
     * 按状态分组返回 统计 总数
     *
     * @param condition 条件 车牌后四位 封车编码  目的场地（必填）状态集合
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findStatusCountByCondition4Status", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyBizTaskUnloadCountDto> findStatusCountByCondition4Status(JyBizTaskUnloadVehicleEntity condition, List<String> sealCarCodes, JyBizTaskUnloadStatusEnum... enums) {
        if (enums == null) {
            // 如果入参状态为空 则全部状态匹配
            enums = JyBizTaskUnloadStatusEnum.values();
        }
        List<Integer> statusOfCodes = new ArrayList<>();
        for (JyBizTaskUnloadStatusEnum statusEnum : enums) {
            statusOfCodes.add(statusEnum.getCode());
        }
        //获取数据
        List<JyBizTaskUnloadCountDto> jyBizTaskUnloadCountDtoList = jyBizTaskUnloadVehicleDao.findStatusCountByCondition4Status(condition, statusOfCodes, sealCarCodes);

        return jyBizTaskUnloadCountDtoList;
    }

    /**
     * 按状态和线路 分组返回 统计 总数
     *
     * @param condition 条件 车牌后四位 封车编码  目的场地（必填）状态集合
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findStatusCountByCondition4StatusAndLine", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyBizTaskUnloadCountDto> findStatusCountByCondition4StatusAndLine(JyBizTaskUnloadVehicleEntity condition, List<String> sealCarCodes, JyBizTaskUnloadStatusEnum... enums) {
        if (enums == null) {
            // 如果入参状态为空 则全部状态匹配
            enums = JyBizTaskUnloadStatusEnum.values();
        }
        List<Integer> statusOfCodes = new ArrayList<>();
        for (JyBizTaskUnloadStatusEnum statusEnum : enums) {
            statusOfCodes.add(statusEnum.getCode());
        }

        //获取数据
        List<JyBizTaskUnloadCountDto> jyBizTaskUnloadCountDtoList = jyBizTaskUnloadVehicleDao.findStatusCountByCondition4StatusAndLine(condition, statusOfCodes, sealCarCodes);

        return jyBizTaskUnloadCountDtoList;
    }

    @Override
    public Long findStatusCountByCondition4StatusAndLineOfTEAN(JyBizTaskUnloadVehicleEntity condition, List<String> sealCarCodes, JyBizTaskUnloadStatusEnum... enums) {
        Long num =0L;
        try{

            if (enums == null) {
                // 如果入参状态为空 则全部状态匹配
                enums = JyBizTaskUnloadStatusEnum.values();
            }
            List<Integer> statusOfCodes = new ArrayList<>();
            for (JyBizTaskUnloadStatusEnum statusEnum : enums) {
                statusOfCodes.add(statusEnum.getCode());
            }
            //获取数据
            logger.info("获取特安车辆任务数据入参-{}");
            num = jyBizTaskUnloadVehicleDao.findStatusCountByCondition4StatusAndLineOfTEAN(condition, statusOfCodes, sealCarCodes);
            if(num != null && num >0){
                return num;
            }
        }catch (Exception e){
            logger.error("获取特安车辆任务数据异常-{}",e.getMessage());
        }
        return num;
    }


    /**
     * 分页返回数据 集合（最大支持滚动到200条数据）
     *
     * @param condition 车牌后四位 封车编码  目的场地（必填）状态 线路
     * @param typeEnum  排序类型
     * @param pageNum   页码  不小于0
     * @param pageSize  每页数量
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findByConditionOfPage", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyBizTaskUnloadVehicleEntity> findByConditionOfPage(JyBizTaskUnloadVehicleEntity condition, JyBizTaskUnloadOrderTypeEnum typeEnum, Integer pageNum, Integer pageSize, List<String> sealCarCodes) {
        Integer offset = 0;
        Integer limit = pageSize;
        if (pageNum > 0) {
            offset = (pageNum - 1) * pageSize;
        }
        //超过最大分页数据量 直接返回空数据
        if (offset + limit > dmsConfigManager.getPropertyConfig().getJyTaskPageMax()) {
            return new ArrayList<>();
        }

        return jyBizTaskUnloadVehicleDao.findByConditionOfPage(condition, typeEnum, offset, limit, sealCarCodes);
    }

    /**
     * 改变状态
     * 如果此次更新的状态 在 当前状态前置节点则不更新直接返回成功
     * 比如 当前状态是卸车中 本次更新到待卸车 则直接返回成功
     *
     * @param entity 业务主键 必填 状态 必填 修改人必填 修改时间必填
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.changeStatus",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean changeStatus(JyBizTaskUnloadVehicleEntity entity) {
        if(logger.isInfoEnabled()){
            logger.info("changeStatus begin ,bizId:{},req:{}",entity.getBizId(),JsonHelper.toJson(entity));
        }
        if(StringUtils.isEmpty(entity.getBizId()) || entity.getVehicleStatus() == null){
            return false;
        }
        if(entity.getUpdateTime() == null){
            entity.setUpdateTime(new Date());
        }
        String bizId = entity.getBizId();
        Integer changeStatus = entity.getVehicleStatus();
        boolean needUnLock = Boolean.TRUE;
        try{
            if(locked(bizId)){
                JyBizTaskUnloadVehicleEntity nowStatus = jyBizTaskUnloadVehicleDao.findIdAndStatusByBizId(bizId);
                if(logger.isInfoEnabled()){
                    logger.info("changeStatus findNow bizId:{} nowStatus:{}",entity.getBizId(),JsonHelper.toJson(nowStatus));
                }
                if(nowStatus == null){
                    //未获得当前状态数据
                    throw new JyBizException(String.format("未获取到需要更新状态的任务数据，bizId:%s", bizId));
                }
                // 如果要更新的状态 在 更新前的状态 前置节点 则直接返回成功不做任何动作
                if(checkStatusIsBefore(JyBizTaskUnloadStatusEnum.getEnumByCode(changeStatus),
                        JyBizTaskUnloadStatusEnum.getEnumByCode(nowStatus.getVehicleStatus()))){
                    logger.warn("bizId:{}尝试错误更新状态，丢弃此次操作，更新前{}，更新后{}",bizId,nowStatus.getVehicleStatus(),changeStatus);
                    return true;
                }
                if(logger.isInfoEnabled()){
                    logger.info("changeStatus change bizId:{} before:{} after:{} ",entity.getBizId(),nowStatus.getVehicleStatus(),changeStatus);
                }
                final boolean updateFlag = jyBizTaskUnloadVehicleDao.changeStatus(entity) > 0;
                if(updateFlag) {
                    // 发送自动关闭任务消息
                    this.sendJyBizTaskAutoCloseMessage(bizId, changeStatus, entity.getUpdateTime().getTime());
                }
                return updateFlag;
            }else {
                needUnLock = Boolean.FALSE;
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        }finally {
            if(needUnLock){
                unLocked(bizId);
            }
            if(logger.isInfoEnabled()){
                logger.info("changeStatus end ,bizId:{} ,needUnLock:{}",entity.getBizId(),needUnLock);
            }
        }
    }

    /**
     * 初始化实际到达时间 同步修改排序时间
     * @return 永远返回成功
     */
    @Override
    public boolean initActualArriveTime(String bizId, Date actualArriveTime) {
        JyBizTaskUnloadVehicleEntity updateParam = new JyBizTaskUnloadVehicleEntity();
        updateParam.setActualArriveTime(actualArriveTime);
        //切记后续需要调整sortTime时间 需要比较数据库中的时间
        updateParam.setSortTime(actualArriveTime);
        updateParam.setBizId(bizId);
        if(!updateOfBusinessInfo(updateParam)){
            logger.error("initActualArriveTime fail!,{},{}",bizId,JsonHelper.toJson(updateParam));
        }
        if(logger.isInfoEnabled()){
            logger.info("initActualArriveTime end ,bizId:{} ,actualArriveTime:{}",bizId,actualArriveTime.toString());
        }
        return true;
    }

    /**
     * 发送自动关闭任务消息
     * @param bizId bizId
     * @param changeStatus 目标更新状态
     */
    private void sendJyBizTaskAutoCloseMessage(String bizId, Integer changeStatus, Long operateTime) {
        try {
            logger.info("sendJyBizTaskAutoCloseMessage {} {} {}", bizId, changeStatus, DateUtil.format(new Date(operateTime), DateUtil.FORMAT_DATE_TIME));
            if(!new ArrayList<>(Arrays.asList(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode(), JyBizTaskUnloadStatusEnum.UN_LOADING.getCode())).contains(changeStatus)){
                return;
            }
            final AutoCloseTaskMq autoCloseTaskMq = new AutoCloseTaskMq();
            autoCloseTaskMq.setBizId(bizId);
            autoCloseTaskMq.setChangeStatus(changeStatus);
            autoCloseTaskMq.setOperateTime(operateTime);
            if (Objects.equals(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode(), changeStatus)) {
                autoCloseTaskMq.setTaskBusinessType(JyAutoCloseTaskBusinessTypeEnum.WAIT_UNLOAD_NOT_FINISH.getCode());
            }
            if (Objects.equals(JyBizTaskUnloadStatusEnum.UN_LOADING.getCode(), changeStatus)) {
                autoCloseTaskMq.setTaskBusinessType(JyAutoCloseTaskBusinessTypeEnum.UNLOADING_NOT_FINISH.getCode());
            }
            jyBizTaskAutoCloseProducer.send(bizId, JsonHelper.toJson(autoCloseTaskMq));
        } catch (JMQException e) {
            logger.error("JyBizTaskUnloadVehicleServiceImpl.sendJyBizTaskAutoCloseMessage exception {}", bizId, e);
        }
    }


    /**
     * 保存或更新基础信息
     * 包含以下业务字段
     * 业务主键
     * 封车编码
     * 车牌号
     * 派车明细编码
     * 模糊查询车牌号（自动从车牌号截取）
     * 任务状态
     * 是否无任务卸车
     * 始发场地ID
     * 始发场地名称
     * 目的场地ID
     * 目的场地名称
     * 排序时间
     * 排序积分
     * 预计到达时间
     * 实际到达时间
     * 解封车时间
     * 线路类型
     * 线路类型名称
     * 任务标签
     * @param entity
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.saveOrUpdateOfBaseInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean saveOrUpdateOfBaseInfo(JyBizTaskUnloadVehicleEntity entity) {
        String bizId = entity.getBizId();
        if(StringUtils.isEmpty(bizId)){
            throw new JyBizException("未传入bizId！请检查入参");
        }
        //截取车牌后4位逻辑
        if(!StringUtils.isEmpty(entity.getVehicleNumber())){
            // 过滤车牌号中的特殊字符
            String vehicleNumber = entity.getVehicleNumber().replaceAll(Constants.SPECIAL_CHAR_REGEX, "");
            entity.setVehicleNumber(vehicleNumber);
            int vl = entity.getVehicleNumber().length();
            String fvn = entity.getVehicleNumber();
            if(vl > 4){
                fvn = fvn.substring(vl - 4 , vl);
            }
            entity.setFuzzyVehicleNumber(fvn);
        }
        if (entity.getTaskType() == null) {
            entity.setTaskType(getTaskType(entity.getEndSiteId()));
        }

        // 初始默认数据
        if(entity.getManualCreatedFlag() == null){
            entity.setManualCreatedFlag(0);
        }
        // 锁定数据
        boolean result;
        boolean needUnLock = Boolean.TRUE;
        try{
            if(locked(bizId)){
                //获取数据判断是否存在
                Long id = jyBizTaskUnloadVehicleDao.findIdByBizId(bizId);
                if(id != null && id > 0){
                    //存在即更新
                    entity.setId(id);
                    result = jyBizTaskUnloadVehicleDao.updateOfBaseInfoById(entity) > 0;
                    entity.setId(null);
                }else {
                    //不存在则新增
                    result = jyBizTaskUnloadVehicleDao.insert(entity) > 0;
                }
            }else{
                needUnLock = Boolean.FALSE;
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        }finally {
            if(needUnLock){
                unLocked(bizId);
            }
        }
        return result;
    }

    /**
     * 保存或更新其他业务信息
     * 包含以下业务字段
     * 业务主键
     * <p>
     * 排除saveOrUpdateOfBaseInfo字段
     *
     * @param entity
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.saveOrUpdateOfOtherBusinessInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean saveOrUpdateOfBusinessInfo(JyBizTaskUnloadVehicleEntity entity) {

        String bizId = entity.getBizId();
        if(StringUtils.isEmpty(bizId)){
            throw new JyBizException("未传入bizId！请检查入参");
        }
        // 锁定数据
        boolean result;
        boolean needUnLock = Boolean.TRUE;
        try{
            if(locked(bizId)){
                //获取数据判断是否存在 防止数据卸载 使用忽略YN模式
                Long id = jyBizTaskUnloadVehicleDao.findIdByBizIdWithoutYn(bizId);
                if(id != null && id > 0){
                    //存在即更新
                    entity.setId(id);
                    result = jyBizTaskUnloadVehicleDao.updateOfBusinessInfoById(entity) > 0;
                    entity.setId(null);
                }else {
                    //不存在则新增
                    result = jyBizTaskUnloadVehicleDao.insert(entity) > 0;
                }
            }else{
                needUnLock = Boolean.FALSE;
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        }finally {
            if(needUnLock){
                unLocked(bizId);
            }

        }

        return result;
    }

    /**
     * 更新其他业务信息 不存在时返回失败，调用者自己处理不存在场景
     * 包含以下业务字段
     * 业务主键
     * <p>
     * 排除saveOrUpdateOfBaseInfo字段
     *
     * @param entity
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.updateOfBusinessInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean updateOfBusinessInfo(JyBizTaskUnloadVehicleEntity entity) {

        String bizId = entity.getBizId();
        if(StringUtils.isEmpty(bizId)){
            throw new JyBizException("未传入bizId！请检查入参");
        }
        // 锁定数据
        boolean result;
        boolean needUnLock = Boolean.TRUE;
        try{
            if(locked(bizId)){
                //获取数据判断是否存在 防止数据卸载 使用忽略YN模式
                Long id = jyBizTaskUnloadVehicleDao.findIdByBizIdWithoutYn(bizId);
                if(id != null && id > 0){
                    //存在即更新
                    entity.setId(id);
                    result = jyBizTaskUnloadVehicleDao.updateOfBusinessInfoById(entity) > 0;
                    entity.setId(null);
                }else {
                    //不存在不执行 并返回失败
                    if(logger.isInfoEnabled()){
                        logger.info("updateOfBusinessInfo not exist! {}",bizId);
                    }
                    result = Boolean.FALSE;
                }
            }else{
                needUnLock = Boolean.FALSE;
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        }finally {
            if(needUnLock){
                unLocked(bizId);
            }

        }

        return result;
    }

    /**
     * 检查当前卸车任务是否被锁定
     * 支持自旋，操作锁异常抛出异常调用者自行处理
     *
     * @param bizId 业务主键 本任务等于封车编码
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.isLocked",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean isLocked(String bizId) {
        boolean r = Boolean.FALSE;
        String lockKey = String.format(JY_BIZ_TASK_UNLOAD_V_LOCK_KEY_FORMAT,bizId);
        if(redisClientOfJy.exists(lockKey)){
            try {
                Thread.sleep(SPIN_TIME_OF_MS);
                r = redisClientOfJy.exists(lockKey);
            } catch (InterruptedException e) {
                logger.error("JyBizTaskUnloadVehicleServiceImpl#isLocked sleep error! bizId:{}",bizId);
            }
        }
        return r;
    }

    /**
     * 锁定本任务
     * 支持自旋，操作锁异常抛出异常调用者自行处理
     *
     * @param bizId 业务主键 本任务等于封车编码
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.locked",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean locked(String bizId) {
        boolean r = Boolean.FALSE;
        String lockKey = String.format(JY_BIZ_TASK_UNLOAD_V_LOCK_KEY_FORMAT,bizId);

        if(!isLocked(bizId)){
            if(redisClientOfJy.set(lockKey,bizId,LOCK_LOSE_TIME_OF_S,TimeUnit.SECONDS,Boolean.FALSE)){
                r = Boolean.TRUE;
            }else{
                //自旋尝试
                try {
                    Thread.sleep(SPIN_TIME_OF_MS);
                    r = redisClientOfJy.set(lockKey,bizId,LOCK_LOSE_TIME_OF_S,TimeUnit.SECONDS,Boolean.FALSE);
                } catch (InterruptedException e) {
                    logger.error("JyBizTaskUnloadVehicleServiceImpl#locked sleep error! bizId:{}",bizId);
                }
            }
        }
        return r;
    }

    /**
     * 解除锁定本任务
     * 支持自旋，操作锁异常抛出异常调用者自行处理
     *
     * @param bizId 业务主键 本任务等于封车编码
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.unLocked",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean unLocked(String bizId) {
        String lockKey = String.format(JY_BIZ_TASK_UNLOAD_V_LOCK_KEY_FORMAT,bizId);
        try{
            redisClientOfJy.del(lockKey);
        }catch (Exception e){
            try {
                Thread.sleep(SPIN_TIME_OF_MS);
            } catch (InterruptedException interruptedException) {
                logger.error("JyBizTaskUnloadVehicleServiceImpl#unLocked sleep error! bizId:{}",bizId);
            }
            redisClientOfJy.del(lockKey);
        }
        return true;
    }

    /**
     * 初始通过运输实时接口补全数据
     *
     * @param sealCarCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.initTaskByTms",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public JyBizTaskUnloadVehicleEntity initTaskByTms(String sealCarCode) {
        CommonDto<SealCarDto> sealCarDtoCommonDto = vosManager.querySealCarInfoBySealCarCode(sealCarCode);
        if(sealCarDtoCommonDto != null && Constants.RESULT_SUCCESS == sealCarDtoCommonDto.getCode()
            && sealCarDtoCommonDto.getData() != null && !StringUtils.isEmpty(sealCarDtoCommonDto.getData().getSealCarCode())){
            SealCarDto sealCarDto = sealCarDtoCommonDto.getData();
            JyBizTaskUnloadVehicleEntity initParams = new JyBizTaskUnloadVehicleEntity();
            initParams.setBizId(sealCarCode);
            initParams.setSealCarCode(sealCarCode);
            initParams.setVehicleNumber(sealCarDto.getVehicleNumber());
            initParams.setTransWorkItemCode(sealCarDto.getTransWorkItemCode());
            initParams.setStartSiteId(Long.valueOf(sealCarDto.getStartSiteId()));
            initParams.setEndSiteId(Long.valueOf(sealCarDto.getEndSiteId()));
            initParams.setStartSiteName(sealCarDto.getStartSiteName());
            initParams.setEndSiteName(sealCarDto.getEndSiteName());
            initParams.setVehicleStatus(JyBizTaskUnloadStatusEnum.INIT.getCode());
            //本身已带锁
            if(saveOrUpdateOfBaseInfo(initParams)){
                return initParams;
            }else{
                logger.error("JyBizTaskUnloadVehicleService.initTaskByTms save fail! {},{}",sealCarCode,
                        JsonHelper.toJson(initParams));
            }
        }else {
            logger.error("JyBizTaskUnloadVehicleService.initTaskByTms fail! {},{}",sealCarCode,
                    JsonHelper.toJson(sealCarDtoCommonDto));
        }
        return null;
    }

    /**
     * 无任务模式初始数据 无任务模式创建的任务默认为待卸状态
     * 业务主键自定生成 封车编码未空字符串
     * @param dto
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.initTaskByNoTask",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleEntity initTaskByNoTask(JyBizTaskUnloadDto dto) {
        String bizId = genBizId();
        if(StringUtils.isEmpty(bizId)){
            return null;
        }
        JyBizTaskUnloadVehicleEntity initParams = new JyBizTaskUnloadVehicleEntity();
        initParams.setBizId(bizId);
        initParams.setSealCarCode(StringUtils.EMPTY);
        initParams.setVehicleNumber(dto.getVehicleNumber()); //车牌号从无任务模式中获取
        initParams.setStartSiteId(Long.valueOf(0));
        initParams.setStartSiteName("无任务模式");
        initParams.setEndSiteId(Long.valueOf(dto.getOperateSiteId()));
        initParams.setEndSiteName(dto.getOperateSiteName());
        initParams.setVehicleStatus(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode());
        initParams.setManualCreatedFlag(1);
        initParams.setRefGroupCode(dto.getGroupCode());

        initParams.setCreateUserErp(dto.getOperateUserErp());
        initParams.setCreateUserName(dto.getOperateUserName());
        initParams.setUpdateUserErp(dto.getOperateUserName());
        initParams.setUpdateUserName(dto.getOperateUserErp());
        initParams.setCreateTime(new Date());
        initParams.setUpdateTime(new Date());
        initParams.setTaskType(dto.getTaskType());
        //本身已带锁
        if(saveOrUpdateOfBaseInfo(initParams)){
            this.sendJyBizTaskAutoCloseMessage(bizId, JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode(), initParams.getUpdateTime().getTime());
            return initParams;
        }else{
            logger.error("JyBizTaskUnloadVehicleService.initTaskByTms save fail! {},{}",JsonHelper.toJson(dto),
                    JsonHelper.toJson(initParams));
        }
        return null;
    }

    /**
     * 生成BIZID逻辑
     * XCZJ2204050000001
     *
     * @return
     */
    private String genBizId(){
        String ownerKey = String.format(JY_BIZ_TASK_BIZ_ID_PREFIX, DateHelper.formatDate(new Date(),DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }

    /**
     * 检查节点 source  是否 在 target 的前置节点
     * @param source
     * @param target
     * @return
     */
    private boolean checkStatusIsBefore(JyBizTaskUnloadStatusEnum source,JyBizTaskUnloadStatusEnum target){
        if(source == null){
            return true;
        }
        if(target == null){
            return false;
        }
        return source.getOrder() < target.getOrder();
    }

    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.countByVehicleNumberAndStatus",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public Long countByVehicleNumberAndStatus(JyBizTaskUnloadVehicleEntity condition) {
        if (condition == null) {
            return 0L;
        }
        return jyBizTaskUnloadVehicleDao.countByVehicleNumberAndStatus(condition);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.listUnloadVehicleTask",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public List<UnloadVehicleTaskDto> listUnloadVehicleTask(JyBizTaskUnloadVehicleEntity entity) {
        List<JyBizTaskUnloadVehicleEntity> jyBizTaskUnloadVehicleEntityList = jyBizTaskUnloadVehicleDao.listUnloadVehicleTask(entity);
        if (ObjectHelper.isNotNull(jyBizTaskUnloadVehicleEntityList) && jyBizTaskUnloadVehicleEntityList.size() > 0) {
            List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = new ArrayList<>();
            for (JyBizTaskUnloadVehicleEntity unloadTask : jyBizTaskUnloadVehicleEntityList) {
                UnloadVehicleTaskDto unloadVehicleTaskDto = entityConvertDto(unloadTask);
                unloadVehicleTaskDtoList.add(unloadVehicleTaskDto);
            }
            return unloadVehicleTaskDtoList;
        }
        return null;
    }

    private String getJyScheduleTaskId(String bizId) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(bizId);
        req.setTaskType(JyScheduleTaskTypeEnum.UNLOAD.getCode());
        JyScheduleTaskResp scheduleTask = jyScheduleTaskManager.findScheduleTaskByBizId(req);
        return null != scheduleTask ? scheduleTask.getTaskId() : StringUtils.EMPTY;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.entityConvertDto",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public UnloadVehicleTaskDto entityConvertDto(JyBizTaskUnloadVehicleEntity entity) {
        UnloadVehicleTaskDto unloadVehicleTaskDto = BeanUtils.copy(entity, UnloadVehicleTaskDto.class);
        if (ObjectHelper.isNotNull(entity.getUnloadProgress())) {
            unloadVehicleTaskDto.setProcessPercent(entity.getUnloadProgress());
        }
        if (ObjectHelper.isNotNull(entity.getMoreCount())) {
            unloadVehicleTaskDto.setExtraScanCount(entity.getMoreCount().intValue());
        }
        if (ObjectHelper.isNotNull(entity.getComboardCount())) {
            unloadVehicleTaskDto.setComBoardCount(entity.getComboardCount());
        }
        if (ObjectHelper.isNotNull(entity.getInterceptCount())) {
            unloadVehicleTaskDto.setInterceptCount(entity.getInterceptCount());
        }
        if (ObjectHelper.isNotNull(entity.getTagsSign())) {
            unloadVehicleTaskDto.setLabelOptionList(resolveTagSign(entity.getTagsSign()));
        }
        unloadVehicleTaskDto.setTaskId(getJyScheduleTaskId(entity.getBizId()));
        return unloadVehicleTaskDto;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.queryStatisticsByDiffDimension",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public ScanStatisticsDto queryStatisticsByDiffDimension(DimensionQueryDto dto) {
        JyUnloadAggsEntity entity = null;
        if (UnloadStatisticsQueryTypeEnum.PACKAGE.getCode().equals(dto.getType())) {
            entity = jyUnloadAggsService.queryPackageStatistics(dto);
        } else if (UnloadStatisticsQueryTypeEnum.WAYBILL.getCode().equals(dto.getType())) {
            entity = jyUnloadAggsService.queryWaybillStatisticsUnderTask(dto);
        }
        if (ObjectHelper.isNotNull(entity)) {
            ScanStatisticsDto scanStatisticsDto = dtoConvert(entity, dto);
            return scanStatisticsDto;
        }
        return null;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.queryTaskDataByBizId",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public UnloadVehicleTaskDto queryTaskDataByBizId(String bizId) {
        try{
            JyBizTaskUnloadVehicleEntity entity = findByBizId(bizId);
            if(Objects.isNull(entity)) {
                if(logger.isInfoEnabled()) {
                    logger.info("JyBizTaskUnloadVehicleServiceImpl.queryTaskDataByBizId:根据Biz查卸车任务为空,bizId={}", bizId);
                }
                return null;
            }
            return entityConvertDto(entity);
        }catch (Exception ex) {
            logger.error("JyBizTaskUnloadVehicleServiceImpl.queryTaskDataByBizId:根据Biz查卸车任务服务异常,bizId={},errMsg={}", bizId, ex.getMessage(), ex);
            throw new JyBizException("查询卸车任务失败");
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.queryStatistics",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<StatisticsDto> queryStatistics(DimensionQueryDto dto) {
        JyUnloadAggsEntity packageStatistics = jyUnloadAggsService.queryPackageStatistics(dto);
        if(packageStatistics == null) {
            return new InvokeResult<>(RESULT_SUCCESS_CODE, "未查到数据");
        }
        JyUnloadAggsEntity waybillStatistics = dto.getBoardCode() != null ?
                jyUnloadAggsService.queryWaybillStatisticsUnderBoard(dto) : jyUnloadAggsService.queryWaybillStatisticsUnderTask(dto);

        StatisticsDto statisticsDto = new StatisticsDto();
        Integer processPercent = (packageStatistics.getTotalSealPackageCount() == null || packageStatistics.getTotalSealPackageCount() == 0 ) ? 0
                : (int)(packageStatistics.getTotalScannedPackageCount() * 100.0 / packageStatistics.getTotalSealPackageCount());
        statisticsDto.setProcessPercent(processPercent);
//        statisticsDto.setProcessPercent((packageStatistics.getTotalScannedPackageCount() / packageStatistics.getTotalSealPackageCount()));
        statisticsDto.setShouldScanCount(packageStatistics.getShouldScanCount());
        statisticsDto.setHaveScanCount(packageStatistics.getActualScanCount());
        if(packageStatistics.getShouldScanCount() == null || packageStatistics.getShouldScanCount() == 0 || packageStatistics.getActualScanCount() == null) {
            statisticsDto.setWaitScanCount(0);
        }else {
            statisticsDto.setWaitScanCount(packageStatistics.getShouldScanCount() - packageStatistics.getActualScanCount());
        }
        statisticsDto.setInterceptCount(packageStatistics.getInterceptActualScanCount());
        statisticsDto.setExtraScanCount(packageStatistics.getMoreScanTotalCount());
        statisticsDto.setWaybillCount(dto.getBoardCode()!=null?waybillStatistics.getActualScanWaybillCount():waybillStatistics.getTotalSealWaybillCount());
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, statisticsDto);
    }

    private void setAutoRefreshConfig(ScanStatisticsDto scanStatisticsDto){
        // 增加刷新间隔配置
        try {
            final ClientAutoRefreshConfig jyWorkAppAutoRefreshConfig = dmsConfigManager.getPropertyConfig().getJyWorkAppAutoRefreshConfigByBusinessType(ClientAutoRefreshBusinessTypeEnum.TYS_UNLOAD_PROGRESS.name());
            if (jyWorkAppAutoRefreshConfig != null) {
                final com.jd.bluedragon.distribution.jy.dto.ClientAutoRefreshConfig clientAutoRefreshConfig = new com.jd.bluedragon.distribution.jy.dto.ClientAutoRefreshConfig();
                BeanCopyUtil.copy(jyWorkAppAutoRefreshConfig, clientAutoRefreshConfig);
                scanStatisticsDto.setClientAutoRefreshConfig(clientAutoRefreshConfig);
            }
        } catch (Exception e) {
            logger.error("JyBizTaskUnloadVehicleServiceImpl.setAutoRefreshConfig error ", e);
        }
    }

    private ScanStatisticsDto dtoConvert(JyUnloadAggsEntity entity, DimensionQueryDto dto) {
        if(logger.isInfoEnabled()) {
            logger.info("JyBizTaskUnloadVehicleServiceImpl.dtoConvert 统计数据--req:entity={}", JsonUtils.toJSONString(entity));
        }
        ScanStatisticsDto scanStatisticsDto = new ScanStatisticsDto();
        this.setAutoRefreshConfig(scanStatisticsDto);
        Integer processPercent = (entity.getTotalSealPackageCount() == null || entity.getTotalSealPackageCount() == 0) ? 0 : (int)(entity.getTotalScannedPackageCount() * 100.0 / entity.getTotalSealPackageCount());
        scanStatisticsDto.setProcessPercent(processPercent);
        if (UnloadStatisticsQueryTypeEnum.PACKAGE.getCode().equals(dto.getType())) {
            scanStatisticsDto.setShouldScanCount(entity.getShouldScanCount());
            scanStatisticsDto.setHaveScanCount(entity.getActualScanCount());
            if(entity.getShouldScanCount() == null || entity.getShouldScanCount() == 0 || entity.getActualScanCount() == null) {
                scanStatisticsDto.setWaitScanCount(0);
            }else {
                scanStatisticsDto.setWaitScanCount(entity.getShouldScanCount() - entity.getActualScanCount());
            }
            scanStatisticsDto.setInterceptShouldScanCount(entity.getInterceptShouldScanCount());
            scanStatisticsDto.setInterceptActualScanCount(entity.getInterceptActualScanCount());
            scanStatisticsDto.setExtraScanCountCurrSite(entity.getMoreScanLocalCount());
            scanStatisticsDto.setExtraScanCountOutCurrSite(entity.getMoreScanOutCount());
        } else if (UnloadStatisticsQueryTypeEnum.WAYBILL.getCode().equals(dto.getType())) {
            scanStatisticsDto.setShouldScanCount(entity.getTotalSealWaybillCount());
            scanStatisticsDto.setHaveScanCount(entity.getTotalScannedWaybillCount());
            if(entity.getTotalSealWaybillCount() == null || entity.getTotalSealWaybillCount() == 0 ||  entity.getTotalScannedWaybillCount() == null) {
                scanStatisticsDto.setWaitScanCount(0);
            }else {
                scanStatisticsDto.setWaitScanCount(entity.getTotalSealWaybillCount() - entity.getTotalScannedWaybillCount());
            }
            scanStatisticsDto.setInterceptShouldScanCount(entity.getTotalShouldInterceptWaybillCount());
            scanStatisticsDto.setInterceptActualScanCount(entity.getTotalScannedInterceptWaybillCount());
            scanStatisticsDto.setExtraScanCountCurrSite(entity.getTotalMoreScanLocalWaybillCount());
            scanStatisticsDto.setExtraScanCountOutCurrSite(entity.getTotalMoreScanOutWaybillCount());
        }
        if(logger.isInfoEnabled()) {
            logger.info("JyBizTaskUnloadVehicleServiceImpl.dtoConvert 统计数据--res:scanStatisticsDto={}", JsonUtils.toJSONString(scanStatisticsDto));
        }
        return scanStatisticsDto;
    }

    private List<LabelOptionDto> resolveTagSign(String tagSign) {
        List<LabelOptionDto> tagList = new ArrayList<>();

        // 是否抽检
        if (BusinessUtil.isSignY(tagSign, JyUnloadTaskSignConstants.POSITION_1)) {
            UnloadTaskLabelEnum spotCheck = UnloadTaskLabelEnum.SPOT_CHECK;
            tagList.add(new LabelOptionDto(spotCheck.getCode(), spotCheck.getName(), spotCheck.getDisplayOrder()));
        }

        // 逐单卸
        if (BusinessUtil.isSignY(tagSign, JyUnloadTaskSignConstants.POSITION_2)) {
            UnloadTaskLabelEnum unloadSingleBill = UnloadTaskLabelEnum.UNLOAD_SINGLE_BILL;
            tagList.add(new LabelOptionDto(unloadSingleBill.getCode(), unloadSingleBill.getName(), unloadSingleBill.getDisplayOrder()));
        }

        // 半车卸
        if (BusinessUtil.isSignY(tagSign, JyUnloadTaskSignConstants.POSITION_3)) {
            UnloadTaskLabelEnum unloadHalfCar = UnloadTaskLabelEnum.UNLOAD_HALF_CAR;
            tagList.add(new LabelOptionDto(unloadHalfCar.getCode(), unloadHalfCar.getName(), unloadHalfCar.getDisplayOrder()));
        }

        return tagList;
    }


    /**
     * 根据操作场地区分卸车任务为分拣任务还是转运任务
     *
     * @param endSiteId
     * @return
     */
    private Integer getTaskType(Long endSiteId) {
        if(endSiteId == null) {
            logger.warn("JyBizTaskUnloadVehicleServiceImpl.getTaskType--卸车任务区分来源分拣还是转运，流向为空");
            return null;
        }
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(endSiteId.intValue());
        if(baseStaffSiteOrgDto == null) {
            logger.warn("JyBizTaskUnloadVehicleServiceImpl.getTaskType--卸车任务区分来源分拣还是转运，基础资料未查到场地，endSiteId={}", endSiteId);
            return null;
        }
        return Constants.B2B_SITE_TYPE == baseStaffSiteOrgDto.getSubType() ? UNLOAD_TASK_CATEGORY_TYS : UNLOAD_TASK_CATEGORY_DMS;
    }


    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.queryByFuzzyVehicleNumberAndStatus",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public List<UnloadVehicleTaskDto> queryByFuzzyVehicleNumberAndStatus(JyBizTaskUnloadVehicleEntity condition) {
        if (condition == null) {
            return new ArrayList<>();
        }
        List<JyBizTaskUnloadVehicleEntity> entityList = jyBizTaskUnloadVehicleDao.queryByFuzzyVehicleNumberAndStatus(condition);
        if (ObjectHelper.isNotNull(entityList) && entityList.size() > 0) {
            List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = new ArrayList<>();
            for (JyBizTaskUnloadVehicleEntity unloadTask : entityList) {
                UnloadVehicleTaskDto unloadVehicleTaskDto = entityConvertDto(unloadTask);
                unloadVehicleTaskDtoList.add(unloadVehicleTaskDto);
            }
            return unloadVehicleTaskDtoList;
        }
        return new ArrayList<>();
    }

    /**
     * 根据BIZ_ID 更新其他业务数据
     * @param entity
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.updateOfBusinessInfoById",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public int updateOfBusinessInfoById(JyBizTaskUnloadVehicleEntity entity) {
        return jyBizTaskUnloadVehicleDao.updateOfBusinessInfoById(entity);
    }

}
