package com.jd.bluedragon.distribution.newseal.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.newseal.domain.*;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.newseal.dao.PreSealVehicleDao;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * @ClassName: PreSealVehicleServiceImpl
 * @Description: 预封车数据表--Service接口实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Service("preSealVehicleService")
public class PreSealVehicleServiceImpl extends BaseService<PreSealVehicle> implements PreSealVehicleService {

	@Autowired
	@Qualifier("preSealVehicleDao")
	private PreSealVehicleDao preSealVehicleDao;

	@Override
	public Dao<PreSealVehicle> getDao() {
		return this.preSealVehicleDao;
	}

    @Autowired
    private SealVehiclesService sealVehiclesService;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private TaskService taskService;

    @Autowired
    @Qualifier("newSealVehicleService")
    private NewSealVehicleService newSealVehicleService;

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
	public boolean insert(PreSealVehicle preSealVehicle) {
		return preSealVehicleDao.insert(preSealVehicle);
	}

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
	public boolean cancelPreSealBeforeInsert(PreSealVehicle preSealVehicle) {
        preSealVehicleDao.preCancelByCreateAndReceive(preSealVehicle.getCreateSiteCode(), preSealVehicle.getReceiveSiteCode(),
                preSealVehicle.getCreateUserErp(), preSealVehicle.getCreateUserName());
		return insert(preSealVehicle);
	}

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean updateById(PreSealVehicle preSealVehicle) {
        return preSealVehicleDao.update(preSealVehicle);
    }

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public boolean updateStatusById(Long id, String updateUserErp, String updateUserName, SealVehicleEnum status) {
		PreSealVehicle preSealVehicle = new PreSealVehicle();

		preSealVehicle.setId(id);
		preSealVehicle.setStatus(status.getCode());
		preSealVehicle.setUpdateUserErp(updateUserErp);
		preSealVehicle.setUpdateUserName(updateUserName);
		preSealVehicle.setUpdateTime(new Date());

		return preSealVehicleDao.update(preSealVehicle);
	}

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public int updateStatusByIds(List<Long> ids, String updateUserErp, String updateUserName, SealVehicleEnum status) {
		return preSealVehicleDao.updateStatusByIds(ids, updateUserErp, updateUserName, status.getCode());
	}

	@Override
	public List<PreSealVehicle> findByCreateAndReceive(Integer createSiteCode, Integer receiveSiteCode) {
		return preSealVehicleDao.findByCreateAndReceive(createSiteCode, receiveSiteCode);
	}

    @Override
    public List<PreSealVehicle> queryBySiteCode(Integer createSiteCode) {

        PreSealVehicle query = new PreSealVehicle();
        query.setCreateSiteCode(createSiteCode);
        query.setStatus(SealVehicleEnum.PRE_SEAL.getCode());

        List<PreSealVehicle> preSealVehicleList = preSealVehicleDao.queryByCondition(query);

        return preSealVehicleList;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.PreSealVehicleServiceImpl.findTodayUsedTransports", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
	public List<String> findTodayUsedTransports(Integer createSiteCode) {
	    //获取当天零点时刻
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
        calendar.set(Calendar.MILLISECOND, 0);

		return preSealVehicleDao.findUsedTransports(createSiteCode, calendar.getTime());
	}

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    @JProfiler(jKey = "DMSWEB.PreSealVehicleServiceImpl.batchSeal", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public boolean batchSeal(List<PreSealVehicle> preList, Integer updateUserCode, String updateUserErp, String updateUserName, Date operateTime) throws Exception{
        List<PreSealVehicle> data = new ArrayList<>(preList.size());
        List<String> transportCodes = new ArrayList<>(preList.size());
	    for(PreSealVehicle pre : preList){
	        if(pre.getSendCodes() != null && pre.getSendCodes().size() > 0){
                transportCodes.add(pre.getTransportCode());
                data.add(pre);
            }
        }
        preSealVehicleDao.updateStatusByTransportCodes(transportCodes, updateUserErp, updateUserName, SealVehicleEnum.SEAL.getCode());
        List<SealVehicles> sealVehiclesList = convert2SealVehicles(data, updateUserErp, updateUserName, operateTime);
        sealVehiclesService.batchAdd(sealVehiclesList);
        try{
            addSealTask(data, updateUserCode, updateUserName,operateTime);
            addRedisCache(sealVehiclesList);
        }catch (Exception e){
            clearRedisCache(sealVehiclesList);
            throw e;
        }
        return true;
    }

    /**
     * 预封车数据转换落库数据
     * @param preList
     * @return
     */
    private List<SealVehicles> convert2SealVehicles(List<PreSealVehicle> preList, String updateUserErp, String updateUserName, Date operateTime){
        List<SealVehicles> sealVehiclesList = new ArrayList<>();
        for(PreSealVehicle pre : preList){
            List<SealVehicles> sendCodes = pre.getSendCodes();
            if(sendCodes == null || sendCodes.isEmpty()){
                continue;
            }
            for(SealVehicles sealVehicles : sendCodes){
                sealVehicles.setPreSealUuid(pre.getPreSealUuid());
                sealVehicles.setCreateSiteCode(pre.getCreateSiteCode());
                sealVehicles.setCreateSiteName(pre.getCreateSiteName());
                sealVehicles.setReceiveSiteCode(pre.getReceiveSiteCode());
                sealVehicles.setReceiveSiteName(pre.getReceiveSiteName());
                sealVehicles.setTransportCode(pre.getTransportCode());
//                sealVehicles.setVehicleNumber(pre.getVehicleNumber());
//                sealVehicles.setSealCodes(pre.getSealCodes());
                sealVehicles.setSendCarTime(pre.getSendCarTime());
                sealVehicles.setStatus(SealVehicleEnum.SEAL.getCode());
                sealVehicles.setCreateUserErp(updateUserErp);
                sealVehicles.setCreateUserName(updateUserName);
                sealVehicles.setOperateTime(operateTime);
                sealVehicles.setSealCarType(Constants.SEAL_TYPE_TRANSPORT);
                sealVehicles.setSource(Constants.SEND_DETAIL_SOUCRE_NORMAL);
            }
            sealVehiclesList.addAll(sendCodes);
        }


        return sealVehiclesList;
    }

    /**
     * 已封车批次号清除缓存
     *
     * @param sealVehiclesList
     */
    private void clearRedisCache(List<SealVehicles> sealVehiclesList) {
        if (sealVehiclesList == null || sealVehiclesList.size() == 0) {
            return;
        }
        for (SealVehicles dto : sealVehiclesList) {
            if(StringUtils.isNotEmpty(dto.getSealDataCode())){
                try {
                    redisManager.del(Constants.CACHE_KEY_PRE_SEAL_SENDCODE + dto.getSealDataCode());
                    logger.info("已封车批次号清除缓存成功:" + dto.getSealDataCode());
                } catch (Throwable e) {
                    logger.warn("已封车批次号清除缓存失败:" + dto.getSealDataCode() + ";异常：" + e.getMessage());
                }
            }
        }
    }

    /**
     * 将封车的批次号缓存到Redis里
     *
     * @param sealVehiclesList
     */
    private void addRedisCache(List<SealVehicles> sealVehiclesList) {
        if (sealVehiclesList == null || sealVehiclesList.size() == 0) {
            return;
        }
        for (SealVehicles dto : sealVehiclesList) {
            if(StringUtils.isNotEmpty(dto.getSealDataCode())) {
                try {
                    redisManager.setex(Constants.CACHE_KEY_PRE_SEAL_SENDCODE + dto.getSealDataCode(), Constants.TIME_SECONDS_ONE_WEEK, String.valueOf(dto.getOperateTime().getTime()));
                    logger.info("已封车批次号存入缓存成功:" + dto.getSealDataCode());
                } catch (Throwable e) {
                    logger.warn("已封车批次号存入缓存失败:" + dto.getSealDataCode() + ";异常：" + e.getMessage());
                }
            }
        }
    }

    /**
     * 添加封车任务
     * @param preList
     * @param operateTime
     */
    private void addSealTask(List<PreSealVehicle> preList, Integer updateUserCode, String updateUserName, Date operateTime){
        Integer siteCode = preList.get(0).getCreateSiteCode();
        Map<String, SealTaskBody> taskBodyMap = new HashMap<>();

        Map<String, SealTaskBody> taskFerrySealBodyMap = new HashMap<>();
        for(PreSealVehicle pre : preList){
            List<SealVehicles> sendCodes = pre.getSendCodes();
            if(sendCodes == null || sendCodes.isEmpty()){
                continue;
            }
            Map<String, SealTaskBody> carMap = new HashMap<>();
            for(String vehicleNumber : pre.getVehicleNumbers()){
                SealTaskBody body = new SealTaskBody();
                if (PreSealVehicleSourceEnum.FERRY_PRE_SEAL.getCode() == pre.getPreSealSource()) {
                    body.setTaskType(Task.TASK_TYPE_FERRY_SEAL_OFFLINE);
                } else {
                    body.setTaskType(Task.TASK_TYPE_SEAL_OFFLINE);
                }
                body.setSiteCode(pre.getCreateSiteCode());
                body.setReceiveSiteCode(pre.getCreateSiteCode());
                body.setSiteName(pre.getCreateSiteName());
                body.setSealBoxCode(pre.getTransportCode());
                body.setUserCode(updateUserCode);
                body.setUserName(updateUserName);
                body.setOperateTime(DateHelper.formatDate(operateTime, Constants.DATE_TIME_MS_FORMAT));
                body.setCarCode(vehicleNumber);
                carMap.put(vehicleNumber, body);
            }
            for(int i = 0; i < sendCodes.size(); i++){
                SealVehicles vo = sendCodes.get(i);
                //未封车的批次才需要提交封车任务
                if(newSealVehicleService.checkSendCodeIsSealed(vo.getSealDataCode())){
                    logger.warn("一键封车批次号已封车：" + vo.getSealDataCode());
                    continue;
                }
                SealTaskBody body = carMap.get(vo.getVehicleNumber());
                body.setShieldsCarCode(vo.getSealCodes());
                body.appendBatchCode(vo.getSealDataCode());
                body.setVolume(vo.getVolume());
                body.setWeight(vo.getWeight());
                //车和运力多对多
                String key = pre.getTransportCode() + vo.getVehicleNumber();
                //普通预封车和传摆预封车，任务集合分开发
                if (body.getTaskType().equals(Task.TASK_TYPE_FERRY_SEAL_OFFLINE)) {
                    if(! taskFerrySealBodyMap.containsKey(key)){
                        taskFerrySealBodyMap.put(key, body);
                    }
                } else {
                    if(!taskBodyMap.containsKey(key)){
                        taskBodyMap.put(key, body);
                    }
                }

            }
        }
        Task task = new Task();
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setType(Task.TASK_TYPE_OFFLINE);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_OFFLINE));
        task.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_OFFLINE));
        task.setKeyword1(siteCode.toString());
        task.setCreateSiteCode(siteCode);
        task.setReceiveSiteCode(siteCode);
        if (! taskBodyMap.isEmpty()) {
            //普通预封车任务
            task.setBody(JsonHelper.toJson(taskBodyMap.values()));
            taskService.add(task, true);
        }
        if (! taskFerrySealBodyMap.isEmpty()) {
            //传摆与封车任务
            task.setBody(JsonHelper.toJson(taskFerrySealBodyMap.values()));
            taskService.add(task, true);
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.PreSealVehicleServiceImpl.getVehicleMeasureInfoList", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public PreSealVehicle getPreSealVehicleInfo(String transportCode) {
        return preSealVehicleDao.getPreSealVehicleInfo(transportCode);
    }
    @Override
    @JProfiler(jKey = "DMSWEB.PreSealVehicleServiceImpl.getVehicleMeasureInfoList", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public List<VehicleMeasureInfo> getVehicleMeasureInfoList(String transportCode) {
        return preSealVehicleDao.getVehicleMeasureInfoList(transportCode);
    }

    @Override
    public boolean updatePreSealVehicleMeasureInfo(PreSealVehicle preSealVehicle) {
        return preSealVehicleDao.updatePreSealVehicleMeasureInfo(preSealVehicle) > 0;
    }


}
