package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskUnloadVehicleDao;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.utils.DateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/7/25
 * @Description: 清理作业APP 到车 + 卸车 任务相关实现
 */
@Service("jyBizUnloadTaskCleanService")
public class JYBizUnloadTaskCleanServiceImpl implements JYBizTaskCleanService{

    private static final Logger logger = LoggerFactory.getLogger(JYBizUnloadTaskCleanServiceImpl.class);

    @Autowired
    private JyBizTaskUnloadVehicleDao jyBizTaskUnloadVehicleDao;

    /**
     * 清理数据
     *
     * 到车 + 卸车
     * 在途状态 距离预计到达时间超过60天（预计到达时间为空时去上游封车时间）
     * 待解状态 距离实际到达时间超过30天 （实际到达时间为空时去上游封车时间）
     * 待卸状态 距离解封车时间超过10天
     * 卸车中状态 距离卸车任务开始时间超过5天
     * 完成状态 距离完成时间超过30天
     * 时间都以TS为准
     *
     *
     * @return
     */
    @Override
    public boolean clean() {
        Boolean cleanSucFlag = Boolean.TRUE;
        //获取所有清理规则
        for(CleanRule cleanRule : CleanRule.values()){
            //优先获取需要清理的站点ID
            List<Integer> sites = getNeedCleanSites(cleanRule.status);
            if(CollectionUtils.isEmpty(sites)){
                continue;
            }
            //根据每个站点分别按状态清理数据
            for(Integer siteCode : sites){

                if(!cleanData(cleanRule,siteCode)){
                    //部分失败跳过，最后返回失败
                    cleanSucFlag = Boolean.FALSE;
                    continue;
                }
            }
        }
        return cleanSucFlag;
    }


    /**
     * 获取需要清理的站点数据
     * @param statusEnum
     * @return
     */
    private List<Integer> getNeedCleanSites(JyBizTaskUnloadStatusEnum statusEnum){
        List<Integer> result = new ArrayList<>();
        if(statusEnum == null){
            return result;
        }
        StopWatch sw = new StopWatch();
        sw.start();
        try{
            JyBizTaskUnloadVehicleEntity param = new JyBizTaskUnloadVehicleEntity();
            param.setVehicleStatus(statusEnum.getCode());
            result = jyBizTaskUnloadVehicleDao.needCleanSite(param);
        }catch (Exception e){
            logger.error("清理作业APP卸车+到车任务,获取需要{}清理站点信息异常！",statusEnum.getName(),e);
        }finally {
            sw.stop();
            if(logger.isInfoEnabled()){
                logger.info("清理作业APP卸车+到车任务,获取需要{}清理站点信息,耗时:{}ms",statusEnum.getName(),sw.getLastTaskTimeMillis());
            }
        }
        return result;
    }

    /**
     * 每个站点按清理规则执行清理动作
     * @param cleanRule
     * @param siteCode
     * @return
     */
    private boolean cleanData(CleanRule cleanRule,Integer siteCode){
        if(cleanRule == null || siteCode == null){
            return true;
        }
        StopWatch sw = new StopWatch();
        sw.start();
        long cleanDataSize = 0;
        try{
            JyBizTaskUnloadVehicleEntity param = new JyBizTaskUnloadVehicleEntity();
            param.setVehicleStatus(cleanRule.getStatus().getCode());
            param.setEndSiteId(Long.valueOf(siteCode));
            param.setTs(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(),-cleanRule.getMoreThanDays()));
            cleanDataSize = jyBizTaskUnloadVehicleDao.cleanByParam(param);
        }catch (Exception e){
            logger.error("清理作业APP卸车+到车任务,清理站点:{}规则:{}时异常！",
                    siteCode,cleanRule.getStatus().getName(),e);
            return false;
        }finally {
            sw.stop();
            if(logger.isInfoEnabled()){
                logger.info("清理作业APP卸车+到车任务,清理站点:{}规则:{},清理条数:{},耗时:{}ms",
                        siteCode,cleanRule.getStatus().getName(),cleanDataSize,sw.getLastTaskTimeMillis());
            }
        }
        return true;

    }

    /**
     * 清理规则
     */
    private enum CleanRule{
        INIT(JyBizTaskUnloadStatusEnum.INIT,60),
        ON_WAY(JyBizTaskUnloadStatusEnum.ON_WAY,60),
        WAIT_UN_SEAL(JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL,30),
        WAIT_UN_LOAD(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD,10),
        UN_LOADING(JyBizTaskUnloadStatusEnum.UN_LOADING,5),
        UN_LOAD_DONE(JyBizTaskUnloadStatusEnum.UN_LOAD_DONE,30),
        CANCEL(JyBizTaskUnloadStatusEnum.CANCEL,60);

        CleanRule(JyBizTaskUnloadStatusEnum status, Integer moreThanDays) {
            this.status = status;
            this.moreThanDays = moreThanDays;
        }

        /**
         * 任务状态
         */
        private JyBizTaskUnloadStatusEnum status;

        /**
         * 过期超过日期（单位：天）
         */
        private Integer moreThanDays;


        public JyBizTaskUnloadStatusEnum getStatus() {
            return status;
        }

        public Integer getMoreThanDays() {
            return moreThanDays;
        }

    }




}
