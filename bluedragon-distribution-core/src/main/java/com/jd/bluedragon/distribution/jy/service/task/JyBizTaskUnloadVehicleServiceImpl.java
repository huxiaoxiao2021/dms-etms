package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskUnloadVehicleDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
public class JyBizTaskUnloadVehicleServiceImpl implements JyBizTaskUnloadVehicleService{
    /**
     * 锁格式
     */
    private final static String JY_BIZ_TASK_UNLOAD_V_LOCK_KEY_FORMAT = "JY:B:T:UV:%s";
    /**
     * 锁失效时间 单位秒
     */
    private final static int LOCK_LOSE_TIME_OF_S = 10*60*1000;
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

    /**
     * 按状态分组返回 统计 总数
     *
     * @param condition 条件 车牌后四位 封车编码  目的场地（必填）状态集合
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findStatusCountByCondition4Status",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public List<JyBizTaskUnloadCountDto> findStatusCountByCondition4Status(JyBizTaskUnloadVehicleEntity condition,JyBizTaskUnloadStatusEnum... enums) {
        if(enums == null){
            // 如果入参状态为空 则全部状态匹配
            enums = JyBizTaskUnloadStatusEnum.values();
        }
        List<Integer> statusOfCodes = new ArrayList<>();
        for(JyBizTaskUnloadStatusEnum statusEnum : enums){
            statusOfCodes.add(statusEnum.getCode());
        }
        //获取数据
        List<JyBizTaskUnloadCountDto> jyBizTaskUnloadCountDtoList = jyBizTaskUnloadVehicleDao.findStatusCountByCondition4Status(condition, statusOfCodes);

        return jyBizTaskUnloadCountDtoList;
    }

    /**
     * 按状态和线路 分组返回 统计 总数
     *
     * @param condition 条件 车牌后四位 封车编码  目的场地（必填）状态集合
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.jy.JyBizTaskUnloadVehicleServiceImpl.findStatusCountByCondition4StatusAndLine",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public List<JyBizTaskUnloadCountDto> findStatusCountByCondition4StatusAndLine(JyBizTaskUnloadVehicleEntity condition,JyBizTaskUnloadStatusEnum... enums) {
        if(enums == null){
            // 如果入参状态为空 则全部状态匹配
            enums = JyBizTaskUnloadStatusEnum.values();
        }
        List<Integer> statusOfCodes = new ArrayList<>();
        for(JyBizTaskUnloadStatusEnum statusEnum : enums){
            statusOfCodes.add(statusEnum.getCode());
        }

        //获取数据
        List<JyBizTaskUnloadCountDto> jyBizTaskUnloadCountDtoList = jyBizTaskUnloadVehicleDao.findStatusCountByCondition4StatusAndLine(condition, statusOfCodes);

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
    public List<JyBizTaskUnloadVehicleEntity> findByConditionOfPage(JyBizTaskUnloadVehicleEntity condition, JyBizTaskUnloadOrderTypeEnum typeEnum, Integer pageNum, Integer pageSize) {
        Integer offset = 0;
        Integer limit = pageSize;
        if(pageNum > 0 ){
            offset = pageNum * pageSize;
        }
        //超过最大分页数据量 直接返回空数据
        if(offset + limit > ucc.getJyTaskPageMax()){
            return new ArrayList<>();
        }

        return jyBizTaskUnloadVehicleDao.findByConditionOfPage(condition,typeEnum,offset,limit);
    }

    /**
     * 改变状态
     *
     * @param entity 业务主键 必填 状态 必填 修改人必填 修改时间必填
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean changeStatus(JyBizTaskUnloadVehicleEntity entity) {
        if(StringUtils.isEmpty(entity.getBizId())){
            return false;
        }
        if(entity.getUpdateTime() == null){
            entity.setUpdateTime(new Date());
        }
        String bizId = entity.getBizId();
        try{
            if(locked(bizId)){
                return jyBizTaskUnloadVehicleDao.changeStatus(entity) > 0;
            }else {
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        }finally {
            unLocked(bizId);
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
    public boolean saveOrUpdateOfBaseInfo(JyBizTaskUnloadVehicleEntity entity) {
        String bizId = entity.getBizId();
        if(StringUtils.isEmpty(bizId)){
            throw new JyBizException("未传入bizId！请检查入参");
        }
        //截取车牌后4位逻辑
        if(!StringUtils.isEmpty(entity.getVehicleNumber())){
            int vl = entity.getVehicleNumber().length();
            String fvn = entity.getVehicleNumber();
            if(vl > 4){
                fvn = fvn.substring(vl - 4 , vl);
            }
            entity.setFuzzyVehicleNumber(fvn);
        }
        // 锁定数据
        boolean result;
        try{
            if(locked(bizId)){
                //获取数据判断是否存在
                Long id = jyBizTaskUnloadVehicleDao.findIdByBizId(bizId);
                if(id != null && id > 0){
                    //存在即更新
                    result = jyBizTaskUnloadVehicleDao.updateOfBaseInfoById(entity) > 0;
                }else {
                    //不存在则新增
                    result = jyBizTaskUnloadVehicleDao.insert(entity) > 0;
                }
            }else{
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        }finally {
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
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean saveOrUpdateOfOtherBusinessInfo(JyBizTaskUnloadVehicleEntity entity) {

        String bizId = entity.getBizId();
        if(StringUtils.isEmpty(bizId)){
            throw new JyBizException("未传入bizId！请检查入参");
        }
        // 锁定数据
        boolean result;
        try{
            if(locked(bizId)){
                //获取数据判断是否存在
                Long id = jyBizTaskUnloadVehicleDao.findIdByBizId(bizId);
                if(id != null && id > 0){
                    //存在即更新
                    result = jyBizTaskUnloadVehicleDao.updateOfOtherBusinessInfoById(entity) > 0;
                }else {
                    //不存在则新增
                    result = jyBizTaskUnloadVehicleDao.insert(entity) > 0;
                }
            }else{
                //未锁定成功 抛出异常
                throw new JyBizException(String.format("锁定数据数据失败，bizId:%s", bizId));
            }
        }finally {
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
}
