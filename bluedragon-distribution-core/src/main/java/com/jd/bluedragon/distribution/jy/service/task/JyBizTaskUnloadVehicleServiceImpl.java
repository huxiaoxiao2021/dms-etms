package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskUnloadVehicleDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadAggsDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadStatisticsQueryTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadTaskLabelEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
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
    private final static int LOCK_LOSE_TIME_OF_S = 10 * 60 * 1000;
    /**
     * 一次自旋时间 单位毫秒
     */
    private final static int SPIN_TIME_OF_MS = 100;

    private Logger logger = LoggerFactory.getLogger(JyBizTaskUnloadVehicleServiceImpl.class);

    @Autowired
    private JyBizTaskUnloadVehicleDao jyBizTaskUnloadVehicleDao;

    @Autowired
    private UccPropertyConfiguration ucc;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private VosManager vosManager;

    @Autowired
    @Qualifier("redisJyBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;

    @Autowired
    JyUnloadAggsDao jyUnloadAggsDao;
    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

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
        if (offset + limit > ucc.getJyTaskPageMax()) {
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
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.changeStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean changeStatus(JyBizTaskUnloadVehicleEntity entity) {
        if (logger.isInfoEnabled()) {
            logger.info("changeStatus begin ,bizId:{},req:{}", entity.getBizId(), JsonHelper.toJson(entity));
        }
        if (StringUtils.isEmpty(entity.getBizId()) || entity.getVehicleStatus() == null) {
            return false;
        }
        if (entity.getUpdateTime() == null) {
            entity.setUpdateTime(new Date());
        }
        String bizId = entity.getBizId();
        Integer changeStatus = entity.getVehicleStatus();
        try {
            if (locked(bizId)) {
                JyBizTaskUnloadVehicleEntity nowStatus = jyBizTaskUnloadVehicleDao.findIdAndStatusByBizId(bizId);
                if (logger.isInfoEnabled()) {
                    logger.info("changeStatus findNow bizId:{} nowStatus:{}", entity.getBizId(), JsonHelper.toJson(nowStatus));
                }
                if (nowStatus == null) {
                    //未获得当前状态数据
                    throw new JyBizException(String.format("未获取到需要更新状态的任务数据，bizId:%s", bizId));
                }
                // 如果要更新的状态 在 更新前的状态 前置节点 则直接返回成功不做任何动作
                if (checkStatusIsBefore(JyBizTaskUnloadStatusEnum.getEnumByCode(changeStatus), JyBizTaskUnloadStatusEnum.getEnumByCode(nowStatus.getVehicleStatus()))) {
                    logger.warn("bizId:{}尝试错误更新状态，丢弃此次操作，更新前{}，更新后{}", bizId, nowStatus.getVehicleStatus(), changeStatus);
                    return true;
                }
                if (logger.isInfoEnabled()) {
                    logger.info("changeStatus change bizId:{} before:{} after:{} ", entity.getBizId(), nowStatus.getVehicleStatus(), changeStatus);
                }
                return jyBizTaskUnloadVehicleDao.changeStatus(entity) > 0;
            } else {
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        } finally {
            unLocked(bizId);
            if (logger.isInfoEnabled()) {
                logger.info("changeStatus end ,bizId:{}", entity.getBizId());
            }
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
     *
     * @param entity
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.saveOrUpdateOfBaseInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean saveOrUpdateOfBaseInfo(JyBizTaskUnloadVehicleEntity entity) {
        String bizId = entity.getBizId();
        if (StringUtils.isEmpty(bizId)) {
            throw new JyBizException("未传入bizId！请检查入参");
        }
        //截取车牌后4位逻辑
        if (!StringUtils.isEmpty(entity.getVehicleNumber())) {
            int vl = entity.getVehicleNumber().length();
            String fvn = entity.getVehicleNumber();
            if (vl > 4) {
                fvn = fvn.substring(vl - 4, vl);
            }
            entity.setFuzzyVehicleNumber(fvn);
        }
        // 初始默认数据
        if (entity.getManualCreatedFlag() == null) {
            entity.setManualCreatedFlag(0);
        }
        // 锁定数据
        boolean result;
        try {
            if (locked(bizId)) {
                //获取数据判断是否存在
                Long id = jyBizTaskUnloadVehicleDao.findIdByBizId(bizId);
                if (id != null && id > 0) {
                    //存在即更新
                    entity.setId(id);
                    result = jyBizTaskUnloadVehicleDao.updateOfBaseInfoById(entity) > 0;
                    entity.setId(null);
                } else {
                    //不存在则新增
                    result = jyBizTaskUnloadVehicleDao.insert(entity) > 0;
                }
            } else {
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        } finally {
            unLocked(bizId);
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
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.saveOrUpdateOfOtherBusinessInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean saveOrUpdateOfBusinessInfo(JyBizTaskUnloadVehicleEntity entity) {

        String bizId = entity.getBizId();
        if (StringUtils.isEmpty(bizId)) {
            throw new JyBizException("未传入bizId！请检查入参");
        }
        // 锁定数据
        boolean result;
        try {
            if (locked(bizId)) {
                //获取数据判断是否存在
                Long id = jyBizTaskUnloadVehicleDao.findIdByBizId(bizId);
                if (id != null && id > 0) {
                    //存在即更新
                    entity.setId(id);
                    result = jyBizTaskUnloadVehicleDao.updateOfBusinessInfoById(entity) > 0;
                    entity.setId(null);
                } else {
                    //不存在则新增
                    result = jyBizTaskUnloadVehicleDao.insert(entity) > 0;
                }
            } else {
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        } finally {
            unLocked(bizId);
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
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.isLocked", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean isLocked(String bizId) {
        boolean r = Boolean.FALSE;
        String lockKey = String.format(JY_BIZ_TASK_UNLOAD_V_LOCK_KEY_FORMAT, bizId);
        if (redisClientOfJy.exists(lockKey)) {
            try {
                Thread.sleep(SPIN_TIME_OF_MS);
                r = redisClientOfJy.exists(lockKey);
            } catch (InterruptedException e) {
                logger.error("JyBizTaskUnloadVehicleServiceImpl#isLocked sleep error! bizId:{}", bizId);
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
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.locked", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean locked(String bizId) {
        boolean r = Boolean.FALSE;
        String lockKey = String.format(JY_BIZ_TASK_UNLOAD_V_LOCK_KEY_FORMAT, bizId);

        if (!isLocked(bizId)) {
            if (redisClientOfJy.set(lockKey, bizId, LOCK_LOSE_TIME_OF_S, TimeUnit.SECONDS, Boolean.FALSE)) {
                r = Boolean.TRUE;
            } else {
                //自旋尝试
                try {
                    Thread.sleep(SPIN_TIME_OF_MS);
                    r = redisClientOfJy.set(lockKey, bizId, LOCK_LOSE_TIME_OF_S, TimeUnit.SECONDS, Boolean.FALSE);
                } catch (InterruptedException e) {
                    logger.error("JyBizTaskUnloadVehicleServiceImpl#locked sleep error! bizId:{}", bizId);
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
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.unLocked", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean unLocked(String bizId) {
        String lockKey = String.format(JY_BIZ_TASK_UNLOAD_V_LOCK_KEY_FORMAT, bizId);
        try {
            redisClientOfJy.del(lockKey);
        } catch (Exception e) {
            try {
                Thread.sleep(SPIN_TIME_OF_MS);
            } catch (InterruptedException interruptedException) {
                logger.error("JyBizTaskUnloadVehicleServiceImpl#unLocked sleep error! bizId:{}", bizId);
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
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.initTaskByTms", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public JyBizTaskUnloadVehicleEntity initTaskByTms(String sealCarCode) {
        CommonDto<SealCarDto> sealCarDtoCommonDto = vosManager.querySealCarInfoBySealCarCode(sealCarCode);
        if (sealCarDtoCommonDto != null && Constants.RESULT_SUCCESS == sealCarDtoCommonDto.getCode() && sealCarDtoCommonDto.getData() != null && !StringUtils.isEmpty(sealCarDtoCommonDto.getData().getSealCarCode())) {
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
            if (saveOrUpdateOfBaseInfo(initParams)) {
                return initParams;
            } else {
                logger.error("JyBizTaskUnloadVehicleService.initTaskByTms save fail! {},{}", sealCarCode, JsonHelper.toJson(initParams));
            }
        } else {
            logger.error("JyBizTaskUnloadVehicleService.initTaskByTms fail! {},{}", sealCarCode, JsonHelper.toJson(sealCarDtoCommonDto));
        }
        return null;
    }

    /**
     * 无任务模式初始数据 无任务模式创建的任务默认为待卸状态
     * 业务主键自定生成 封车编码未空字符串
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.initTaskByNoTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleEntity initTaskByNoTask(JyBizTaskUnloadDto dto) {
        String bizId = genBizId();
        if (StringUtils.isEmpty(bizId)) {
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
        //本身已带锁
        if (saveOrUpdateOfBaseInfo(initParams)) {
            return initParams;
        } else {
            logger.error("JyBizTaskUnloadVehicleService.initTaskByTms save fail! {},{}", JsonHelper.toJson(dto), JsonHelper.toJson(initParams));
        }
        return null;
    }

    /**
     * 生成BIZID逻辑
     * XCZJ2204050000001
     *
     * @return
     */
    private String genBizId() {
        String ownerKey = String.format(JY_BIZ_TASK_BIZ_ID_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }

    /**
     * 检查节点 source  是否 在 target 的前置节点
     *
     * @param source
     * @param target
     * @return
     */
    private boolean checkStatusIsBefore(JyBizTaskUnloadStatusEnum source, JyBizTaskUnloadStatusEnum target) {
        if (source == null) {
            return true;
        }
        if (target == null) {
            return false;
        }
        return source.getOrder() < target.getOrder();
    }

    @Override
    public Long countByVehicleNumberAndStatus(JyBizTaskUnloadVehicleEntity condition) {
        if (condition == null) {
            return 0L;
        }
        return jyBizTaskUnloadVehicleDao.countByVehicleNumberAndStatus(condition);
    }

    @Override
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
    public UnloadVehicleTaskDto entityConvertDto(JyBizTaskUnloadVehicleEntity entity) {
        UnloadVehicleTaskDto unloadVehicleTaskDto = BeanUtils.copy(entity, UnloadVehicleTaskDto.class);
        calculationProcessTime(unloadVehicleTaskDto);
        if (ObjectHelper.isNotNull(entity.getUnloadProgress())) {
            unloadVehicleTaskDto.setProcessPercent(entity.getUnloadProgress().intValue());
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
    public ScanStatisticsDto queryStatisticsByDiffDimension(DimensionQueryDto dto) {
        JyUnloadAggsEntity entity = null;
        if (UnloadStatisticsQueryTypeEnum.PACKAGE.getCode().equals(dto.getType())) {
            entity = jyUnloadAggsDao.queryPackageStatistics(dto);
        } else if (UnloadStatisticsQueryTypeEnum.WAYBILL.getCode().equals(dto.getType())) {
            entity = dto.getBoardCode() != null ? jyUnloadAggsDao.queryWaybillStatisticsUnderBoard(dto) : jyUnloadAggsDao.queryWaybillStatisticsUnderTask(dto);
        }
        if (ObjectHelper.isNotNull(entity)) {
            ScanStatisticsDto scanStatisticsDto = dtoConvert(entity, dto);
            if (dto.getNeedWaybillInfo()) {
                JyUnloadAggsEntity waybillStatistics = dto.getBoardCode() != null ? jyUnloadAggsDao.queryWaybillStatisticsUnderBoard(dto) : jyUnloadAggsDao.queryWaybillStatisticsUnderTask(dto);
                scanStatisticsDto.setWaybillCount(dto.getBoardCode() != null ? waybillStatistics.getActualScanWaybillCount() : waybillStatistics.getTotalSealWaybillCount());
            }
            return scanStatisticsDto;
        }
        return null;
    }

    @Override
    public UnloadVehicleTaskDto queryTaskDataByBizId(String bizId) {
        JyBizTaskUnloadVehicleEntity entity = findByBizId(bizId);
        return entityConvertDto(entity);
    }

    private ScanStatisticsDto dtoConvert(JyUnloadAggsEntity entity, DimensionQueryDto dto) {
        ScanStatisticsDto scanStatisticsDto = new ScanStatisticsDto();
        scanStatisticsDto.setProcessPercent((entity.getTotalScannedPackageCount() / entity.getTotalSealPackageCount()));
        if (UnloadStatisticsQueryTypeEnum.PACKAGE.getCode().equals(dto.getType())) {
            scanStatisticsDto.setShouldScanCount(entity.getShouldScanCount());
            scanStatisticsDto.setHaveScanCount(entity.getActualScanCount());
            scanStatisticsDto.setWaitScanCount(entity.getShouldScanCount() - entity.getActualScanCount());
            scanStatisticsDto.setInterceptShouldScanCount(entity.getInterceptShouldScanCount());
            scanStatisticsDto.setInterceptActualScanCount(entity.getInterceptActualScanCount());
            scanStatisticsDto.setExtraScanCountCurrSite(entity.getMoreScanLocalCount());
            scanStatisticsDto.setExtraScanCountOutCurrSite(entity.getMoreScanOutCount());
        } else if (UnloadStatisticsQueryTypeEnum.WAYBILL.getCode().equals(dto.getType())) {
            if (ObjectHelper.isNotNull(dto.getBoardCode())) {
                scanStatisticsDto.setHaveScanCount(entity.getActualScanWaybillCount());
            } else {
                scanStatisticsDto.setShouldScanCount(entity.getTotalSealWaybillCount());
                scanStatisticsDto.setHaveScanCount(entity.getTotalScannedWaybillCount());
                scanStatisticsDto.setWaitScanCount(entity.getTotalSealWaybillCount() - entity.getTotalScannedWaybillCount());
                scanStatisticsDto.setInterceptShouldScanCount(entity.getTotalShouldInterceptWaybillCount());
                scanStatisticsDto.setInterceptActualScanCount(entity.getTotalScannedInterceptWaybillCount());
                scanStatisticsDto.setExtraScanCountCurrSite(entity.getTotalMoreScanLocalWaybillCount());
                scanStatisticsDto.setExtraScanCountOutCurrSite(entity.getTotalMoreScanOutWaybillCount());
            }
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

    private void calculationProcessTime(UnloadVehicleTaskDto dto) {
        Date now = new Date();
        switch (dto.getVehicleStatus()) {
            case 3:
                int hourArrive = (int) DateHelper.betweenHours(dto.getActualArriveTime(), now);
                int minutesArrive = (int) DateHelper.betweenMinutes(dto.getActualArriveTime(), now);
                dto.setProcessTime("已到车 " + hourArrive + "时" + minutesArrive + "分");
                break;
            case 4:
                int hourUnload = (int) DateHelper.betweenHours(dto.getUnloadStartTime(), now);
                int minutesUnload = (int) DateHelper.betweenMinutes(dto.getUnloadStartTime(), now);
                dto.setProcessTime("卸车 " + hourUnload + "时" + minutesUnload + "分");
                break;
            case 5:
                int hourComplete = (int) DateHelper.betweenHours(dto.getUnloadStartTime(), dto.getUnloadFinishTime());
                int minutesComplete = (int) DateHelper.betweenMinutes(dto.getUnloadStartTime(), dto.getUnloadFinishTime());
                dto.setProcessTime("总计 " + hourComplete + "时" + minutesComplete + "分");
                break;
            default:
        }
    }
}
