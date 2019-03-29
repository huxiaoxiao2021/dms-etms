package com.jd.bluedragon.distribution.newseal.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.newseal.domain.SealTaskBody;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
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

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
	public boolean insert(PreSealVehicle preSealVehicle) {
		return preSealVehicleDao.insert(preSealVehicle);
	}

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
	public boolean cancelPreSealBeforeInsert(PreSealVehicle preSealVehicle) {
		List<PreSealVehicle> exists = findByCreateAndReceive(preSealVehicle.getCreateSiteCode(), preSealVehicle.getReceiveSiteCode());
		if(exists != null && !exists.isEmpty()){
			List<Long> ids = new ArrayList<>(exists.size());
			for (PreSealVehicle vo : exists){
				ids.add(vo.getId());
			}
			updateStatusByIds(ids, preSealVehicle.getCreateUserErp(), preSealVehicle.getCreateUserName(), SealVehicleEnum.CANCEL_PRE_SEAL);
		}
		return insert(preSealVehicle);
	}

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
    public Map<Integer, PreSealVehicle> queryBySiteCode(Integer createSiteCode) {

        PreSealVehicle query = new PreSealVehicle();
        query.setCreateSiteCode(createSiteCode);
        query.setStatus(SealVehicleEnum.PRE_SEAL.getCode());

        List<PreSealVehicle> preSealVehicleList = preSealVehicleDao.queryByCondition(query);
        Map<Integer, PreSealVehicle> preMap = null;
        if(preSealVehicleList != null && !preSealVehicleList.isEmpty()){
            preMap = new HashMap<>(preSealVehicleList.size());
            //组装车牌信息
            for(PreSealVehicle vo : preSealVehicleList){
                //同一目的地，将车牌组装到车牌list中
                if(preMap.containsKey(vo.getReceiveSiteCode())){
                    preMap.get(vo.getReceiveSiteCode()).getVehicleNumbers().add(vo.getVehicleNumber());
                }else{
                    vo.getVehicleNumbers().add(vo.getVehicleNumber());
                    preMap.put(vo.getReceiveSiteCode(), vo);
                }
            }
        }
        return preMap;
    }

    @Override
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
        addRedisCache(sealVehiclesList);
        try{
            addSealTask(data, updateUserCode, updateUserName,operateTime);
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
                sealVehicles.setVehicleNumber(pre.getVehicleNumber());
                sealVehicles.setSealCodes(pre.getSealCodes());
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
    private void addRedisCache(List<SealVehicles> sealVehiclesList) {
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
    private void clearRedisCache(List<SealVehicles> sealVehiclesList) {
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
        List<SealTaskBody> bodyList = new ArrayList<>(preList.size());
        for(PreSealVehicle pre : preList){
            List<SealVehicles> sendCodes = pre.getSendCodes();
            if(sendCodes != null && !sendCodes.isEmpty()){
                SealTaskBody body = new SealTaskBody();
                body.setTaskType(Task.TASK_TYPE_SEAL_OFFLINE);
                body.setSiteCode(pre.getCreateSiteCode());
                body.setReceiveSiteCode(pre.getCreateSiteCode());
                body.setSiteName(pre.getCreateSiteName());
                body.setSealBoxCode(pre.getTransportCode());
                body.setShieldsCarCode(pre.getSealCodes());
                body.setCarCode(pre.getVehicleNumber());

                StringBuilder sendCodeStr = new StringBuilder();
                for(int i = 0; i < sendCodes.size(); i++){
                    sendCodeStr.append(sendCodes.get(i).getSealDataCode());
                    if(i < sendCodes.size() - 1){
                        sendCodeStr.append(Constants.SEPARATOR_COMMA);
                    }
                }
                body.setBatchCode(sendCodeStr.toString());
                body.setUserCode(updateUserCode);
                body.setUserName(updateUserName);
                body.setOperateTime(DateHelper.formatDate(operateTime, Constants.DATE_TIME_MS_FORMAT));
                bodyList.add(body);
            }
        }
        Task task = new Task();
        task.setBody(JsonHelper.toJson(bodyList));
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setType(Task.TASK_TYPE_OFFLINE);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_OFFLINE));
        task.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_OFFLINE));
        task.setKeyword1(siteCode.toString());
        task.setCreateSiteCode(siteCode);
        task.setReceiveSiteCode(siteCode);
//        task.setFingerprint();
        taskService.add(task, true);
    }
}
