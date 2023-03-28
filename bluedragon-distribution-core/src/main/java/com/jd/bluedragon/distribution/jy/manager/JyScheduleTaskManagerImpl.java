package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.api.task.IJyScheduleTaskApi;
import com.jdl.jy.schedule.base.ServiceResult;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/6
 * @Description:
 */
@Service("jyScheduleTaskManager")
public class JyScheduleTaskManagerImpl implements JyScheduleTaskManager {

    private Logger logger = LoggerFactory.getLogger(JyScheduleTaskManagerImpl.class);

    @Autowired
    private IJyScheduleTaskApi jyScheduleTaskApi;

    /**
     * 创建调度任务
     *
     * @param req
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JyScheduleTaskManagerImpl.createScheduleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyScheduleTaskResp createScheduleTask(JyScheduleTaskReq req) {
        ServiceResult<JyScheduleTaskResp> apiResult = null;
        try{
            apiResult = jyScheduleTaskApi.createScheduleTask(req);
            if(apiResult.getSuccess()){
                return apiResult.getData();
            }
        }catch (Exception e){
            logger.error("JyScheduleTaskManagerImpl.createScheduleTask error req:{}",  JsonHelper.toJson(req),e);
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JyScheduleTaskManagerImpl.createScheduleTask req:{} resp:{}", JsonHelper.toJson(req),JsonHelper.toJson(apiResult));
            }
        }
        return null;
    }

    /**
     * 关闭调度任务
     *
     * @param req
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JyScheduleTaskManagerImpl.closeScheduleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyScheduleTaskResp closeScheduleTask(JyScheduleTaskReq req) {
        ServiceResult<JyScheduleTaskResp> apiResult = null;
        try{
            apiResult = jyScheduleTaskApi.closeScheduleTask(req);
            if(apiResult.getSuccess()){
                return apiResult.getData();
            }
        }catch (Exception e){
            logger.error("JyScheduleTaskManagerImpl.closeScheduleTask error req:{}",  JsonHelper.toJson(req),e);
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JyScheduleTaskManagerImpl.closeScheduleTask req:{} resp:{}", JsonHelper.toJson(req),JsonHelper.toJson(apiResult));
            }
        }
        return null;
    }

    /**
     * 分配调度任务
     *
     * @param req
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JyScheduleTaskManagerImpl.distributeAndStartScheduleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyScheduleTaskResp distributeAndStartScheduleTask(JyScheduleTaskReq req) {
        ServiceResult<JyScheduleTaskResp> apiResult = null;
        try{
            apiResult = jyScheduleTaskApi.distributeAndStartScheduleTask(req);
            if(apiResult.getSuccess()){
                return apiResult.getData();
            }
        }catch (Exception e){
            logger.error("JyScheduleTaskManagerImpl.distributeAndStartScheduleTask error req:{}",  JsonHelper.toJson(req),e);
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JyScheduleTaskManagerImpl.distributeAndStartScheduleTask req:{} resp:{}", JsonHelper.toJson(req),JsonHelper.toJson(apiResult));
            }
        }
        return null;
    }

    /**
     * 通过业务主键和任务类型获取调度任务
     *
     * @param req
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JyScheduleTaskManagerImpl.findScheduleTaskByBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyScheduleTaskResp findScheduleTaskByBizId(JyScheduleTaskReq req) {
        ServiceResult<JyScheduleTaskResp> apiResult = null;
        try{
            apiResult = jyScheduleTaskApi.findScheduleTaskByBizId(req);
            if(apiResult.getSuccess()){
                return apiResult.getData();
            }
        }catch (Exception e){
            logger.error("JyScheduleTaskManagerImpl.findScheduleTaskByBizId error req:{}",  JsonHelper.toJson(req),e);
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JyScheduleTaskManagerImpl.findScheduleTaskByBizId req:{} resp:{}", JsonHelper.toJson(req),JsonHelper.toJson(apiResult));
            }
        }
        return null;
    }

    /**
     * 根据分配目标获取当前已开始的任务信息
     *
     * @param req
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JyScheduleTaskManagerImpl.findStartedScheduleTasksByDistribute", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyScheduleTaskResp> findStartedScheduleTasksByDistribute(JyScheduleTaskReq req) {
        ServiceResult<List<JyScheduleTaskResp>> apiResult = null;
        try{
            apiResult = jyScheduleTaskApi.findStartedScheduleTasksByDistribute(req);
            if(apiResult.getSuccess()){
                return apiResult.getData();
            }
        }catch (Exception e){
            logger.error("JyScheduleTaskManagerImpl.findStartedScheduleTasksByDistribute error req:{}",  JsonHelper.toJson(req),e);
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JyScheduleTaskManagerImpl.findStartedScheduleTasksByDistribute req:{} resp:{}", JsonHelper.toJson(req),JsonHelper.toJson(apiResult));
            }
        }
        return null;
    }

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JyScheduleTaskManagerImpl.findStartedScheduleTasksForAddMember", mState = {JProEnum.TP, JProEnum.FunctionError})	
	public List<JyScheduleTaskResp> findStartedScheduleTasksForAddMember(JyScheduleTaskReq req) {
        ServiceResult<List<JyScheduleTaskResp>> apiResult = null;
        try{
            apiResult = jyScheduleTaskApi.findStartedScheduleTasksForAddMember(req);
            if(apiResult.getSuccess()){
                return apiResult.getData();
            }
        }catch (Exception e){
            logger.error("JyScheduleTaskManagerImpl.findStartedScheduleTasksForAddMember error req:{}",  JsonHelper.toJson(req),e);
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JyScheduleTaskManagerImpl.findStartedScheduleTasksForAddMember req:{} resp:{}", JsonHelper.toJson(req),JsonHelper.toJson(apiResult));
            }
        }
        return null;
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JyScheduleTaskManagerImpl.findStartedScheduleTasksForAddMemberFlow", mState = {JProEnum.TP, JProEnum.FunctionError})	
	public List<JyScheduleTaskResp> findStartedScheduleTasksForAddMemberFlow(JyScheduleTaskReq taskQuery) {
        ServiceResult<List<JyScheduleTaskResp>> apiResult = null;
        try{
            apiResult = jyScheduleTaskApi.findStartedScheduleTasksForAddMemberFlow(taskQuery);
            if(apiResult.getSuccess()){
                return apiResult.getData();
            }
        }catch (Exception e){
            logger.error("JyScheduleTaskManagerImpl.findStartedScheduleTasksForAddMember error req:{}",  JsonHelper.toJson(taskQuery),e);
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JyScheduleTaskManagerImpl.findStartedScheduleTasksForAddMember req:{} resp:{}", JsonHelper.toJson(taskQuery),JsonHelper.toJson(apiResult));
            }
        }
        return new ArrayList<JyScheduleTaskResp>();
	}
}
