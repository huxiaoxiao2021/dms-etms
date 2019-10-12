package com.jd.bluedragon.distribution.storage.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.exception.StorageException;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageDDao;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageMDao;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageMCondition;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageMStatusEnum;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: StoragePackageMServiceImpl
 * @Description: 储位包裹主表--Service接口实现
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
@Service("storagePackageMService")
public class StoragePackageMServiceImpl extends BaseService<StoragePackageM> implements StoragePackageMService {

	private final Log logger = LogFactory.getLog(StoragePackageMServiceImpl.class);

	@Autowired
	@Qualifier("storagePackageMDao")
	private StoragePackageMDao storagePackageMDao;

	@Autowired
	@Qualifier("storagePackageDDao")
	private StoragePackageDDao storagePackageDDao;

	@Autowired
	@Qualifier("waybillCommonService")
	private WaybillCommonService waybillCommonService;

	@Autowired
	@Qualifier("waybillQueryManager")
	private WaybillQueryManager waybillQueryManager;

	@Autowired
	@Qualifier("taskService")
	private TaskService taskService;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Override
	public Dao<StoragePackageM> getDao() {
		return this.storagePackageMDao;
	}

	/**
	 * 强制发货
	 *
	 * 初始化 履约单下所有运单上架数据
	 *
	 * 统一修改履约单下运单的暂存状态
	 *
	 * @param performanceCodes
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean forceSend(List<String> performanceCodes,PutawayDTO putawayDTO) {
		//获取履约单下所有运单 当前不论是否已上架的全部更新成强制发货

		Map<String,List<String>> needInitWaybillMap = new HashMap<String, List<String>>();

		for(String performanceCode : performanceCodes){
			//获取 未上架的运单
			List<StoragePackageM> existStoragePackageMs = storagePackageMDao.queryByPerformanceCode(performanceCode);

			List<String> existWaybills = new ArrayList<String>();
			for(StoragePackageM storagePackageM : existStoragePackageMs){
				existWaybills.add(storagePackageM.getWaybillCode());
			}

			List<String> allWaybills =  waybillQueryManager.getOrderParentChildList(performanceCode);

			allWaybills.removeAll(existWaybills);

			needInitWaybillMap.put(performanceCode,allWaybills);
		}

		//插入未上架的运单数据
		for(String performanceCode: needInitWaybillMap.keySet()){
			for(String needInitWaybillCode : needInitWaybillMap.get(performanceCode)){
				//补充上架记录剩余字段
				putawayDTO.setBarCode(needInitWaybillCode);
				//组装储位号
				putawayDTO.setStorageCode(getExistStorageCode(needInitWaybillCode));

				//插入
				saveStoragePackageMOfForceSend(needInitWaybillCode,performanceCode,putawayDTO);

			}

		}

		//更新强制发货状态
		if(storagePackageMDao.updateForceSendByPerformanceCodes(performanceCodes)>0){
			return true;
		}
		return false;

	}

	/**
	 * 强制发货时保存上架主表记录
	 * @param needInitWaybillCode
	 * @param performanceCode
	 * @param putawayDTO
	 */
	private void saveStoragePackageMOfForceSend(String needInitWaybillCode,String performanceCode,PutawayDTO putawayDTO){

		StoragePackageM storagePackageM = new StoragePackageM();

		storagePackageM.setWaybillCode(needInitWaybillCode);
		//初始成0
		storagePackageM.setPutawayPackageSum(0L);

		BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(needInitWaybillCode, true,true, true, true);
		BigWaybillDto bigWaybillDto = baseEntity.getData();

		storagePackageM.setPerformanceCode(performanceCode);
		storagePackageM.setPlanDeliveryTime(bigWaybillDto.getWaybill().getRequireTime());
		storagePackageM.setPackageSum(Long.valueOf(bigWaybillDto.getWaybill().getGoodNumber()));
		storagePackageM.setStatus(Integer.valueOf(StoragePackageMStatusEnum.FORCE_SEND_3.getCode()));

		makeStoragePackageMBase(storagePackageM,putawayDTO);

		storagePackageMDao.insert(storagePackageM);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagerResult<StoragePackageM> queryByPagerCondition(PagerCondition pagerCondition) {
		StoragePackageMCondition storagePackageMCondition = (StoragePackageMCondition)pagerCondition;
		//将运单号转换成对应的履约单号
		if(StringUtils.isNotBlank(storagePackageMCondition.getWaybillCode())){
			//如果是包裹号需要转换成运单号
			if(WaybillUtil.isPackageCode(storagePackageMCondition.getWaybillCode())){
				String waybillCode = WaybillUtil.getWaybillCode(storagePackageMCondition.getWaybillCode());
				if(StringUtils.isNotBlank(waybillCode)){
					storagePackageMCondition.setWaybillCode(waybillCode);
				}
			}
			//获取运单信息
			String performanceCode = waybillCommonService.getPerformanceCode(storagePackageMCondition.getWaybillCode());
			if(StringUtils.isNotBlank(performanceCode)){
				storagePackageMCondition.setPerformanceCode(performanceCode);
				storagePackageMCondition.setWaybillCode(null);
			}
		}
		return this.getDao().queryByPagerCondition(pagerCondition);
	}

	/**
	 * 上架逻辑处理
	 * @param putawayDTO
	 * @return true 成功
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean putaway(PutawayDTO putawayDTO) {
		//获取运单数据
		boolean isWaybillCode = WaybillUtil.isWaybillCode(putawayDTO.getBarCode());
		String waybillCode =  "";
		if(isWaybillCode){
			waybillCode = putawayDTO.getBarCode();
		}else{
			waybillCode = WaybillUtil.getWaybillCode(putawayDTO.getBarCode());
		}
		BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true,true, true, true);

		if(baseEntity == null || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
			throw new StorageException("无运单信息");
		}

		if(baseEntity.getData().getPackageList() == null || baseEntity.getData().getPackageList().size() == 0){
			throw new StorageException("无包裹信息");
		}
		if(!isWaybillCode){
			boolean packageNotExist = true;
			for(DeliveryPackageD deliveryPackageD :baseEntity.getData().getPackageList()){
				if(putawayDTO.getBarCode().equals(deliveryPackageD.getPackageBarcode())){
					packageNotExist = false;
					break;
				}
			}
			if(packageNotExist){
				throw new StorageException("包裹不存在，请检查!");
			}
		}

		if(!BusinessUtil.isPerformanceOrder(baseEntity.getData().getWaybill().getWaybillSign())){
			throw new StorageException("非加履中心订单");
		}

		if(StringUtils.isBlank(baseEntity.getData().getWaybill().getParentOrderId())){
			throw new StorageException("履约单号为空");
		}

		//检查是否已经上架过
		StoragePackageD lastStoragePackageD = checkExistStorage(putawayDTO.getBarCode());
		if(lastStoragePackageD != null){
			throw new StorageException("已上架，储位号："+lastStoragePackageD.getStorageCode());
		}

		//检查是否在其他分拣中心上架过
		/*StoragePackageM otherStoragePackageM = checkExistStorageOfOtherSite(putawayDTO,baseEntity.getData().getWaybill().getParentOrderId());
		if(otherStoragePackageM != null){
			throw new StorageException("履约单已在【"+otherStoragePackageM.getCreateSiteName()+"】上架");
		}*/

		//末级分拣中心
		Integer destinationDmsId = null;
		BaseStaffSiteOrgDto bDto = baseMajorManager.getBaseSiteBySiteId(baseEntity.getData().getWaybill().getOldSiteId());
		if(bDto != null && bDto.getDmsId() != null){
			//末级分拣中心
			destinationDmsId = bDto.getDmsId();
		}
		if( destinationDmsId==null || !destinationDmsId.equals(putawayDTO.getCreateSiteCode())){
			throw new StorageException("只允许在末级分拣中心上架");
		}

		//存储暂存主表
		saveStoragePackageM( putawayDTO, isWaybillCode, baseEntity.getData());

		//存储暂存明细表
		saveStoragePackageDs( putawayDTO, isWaybillCode, baseEntity.getData());

		//推送运单更新状态 8400 暂存上架状态
		updateWaybillStatus( putawayDTO, isWaybillCode, baseEntity.getData());

		//更新暂存主表发货状态
		updateStoragePackageMStatusForSendOfParentOrderId(baseEntity.getData().getWaybill().getParentOrderId());

		return true;
	}

	/**
	 * 获取最近一次暂存上架 储位号
	 * @param waybillCode
	 * @return
	 */
	@Override
	public String getExistStorageCode(String waybillCode) {
		// 获取 同一个履约单号下的最近一次上架记录
		String PerformanceCode = waybillCommonService.getPerformanceCode(waybillCode);
		if(StringUtils.isNotBlank(PerformanceCode)){
			StoragePackageD storagePackageD = storagePackageDDao.findLastStoragePackageDByPerformanceCode(PerformanceCode);
			if(storagePackageD != null && StringUtils.isNotBlank(storagePackageD.getStorageCode())){
				return storagePackageD.getStorageCode();
			}
		}
		return "";
	}

	/**
	 * 剔除运单
	 *
	 * 逻辑删除该运单上架数据
	 * 如果该履约单下其他运单已全部上架，则更新其他运单的状态
	 *
	 * @param waybillCode 运单编号
	 * @param performanceCode 履约单号
	 */
	@Override
	public void removeWaybill(String waybillCode,String performanceCode) {

		List<StoragePackageM> storagePackageMs  = storagePackageMDao.queryByPerformanceCode(performanceCode);

		if(storagePackageMs != null && storagePackageMs.size()>0){

			//逻辑删除 需要剔除的运单

			for(StoragePackageM storagePackageM :storagePackageMs){
				if(storagePackageM.getWaybillCode().equals(waybillCode)){
					storagePackageMDao.deleteById(storagePackageM.getId());
					storagePackageMs.remove(storagePackageM);
					break;
				}
			}

			boolean canMakeStatus = true;

			//重新获取履约单下的所有运单
			List<String> childWaybillCodes = waybillQueryManager.getOrderParentChildList(performanceCode);

			if(storagePackageMs.size() == childWaybillCodes.size()){
				for(StoragePackageM storagePackageM :storagePackageMs){
					// 履约单下运单关系 和 暂存的对应不上， 或者 运单的所有包裹未全部上架 的时候结束不需要去修改其他运单的暂存状态
					if(!childWaybillCodes.contains(storagePackageM.getWaybillCode()) || !storagePackageM.getPackageSum().equals(storagePackageM.getPutawayPackageSum())){
                        canMakeStatus = false;
					}
				}
			}else{
                //履约单下数量都对应不上肯定不需要更新状态
                canMakeStatus = false;
            }

            if(canMakeStatus){
                //修改履约单下运单的暂存状态 变更为可发货
				storagePackageMDao.updateStoragePackageMStatusForCanSendOfPerformanceCode(performanceCode);

			}


		}

	}

	@Override
	public boolean checkWaybillCanSend(String waybillCode,String waybillSign) {

		if(BusinessUtil.isPerformanceOrder(waybillSign)){
			StoragePackageM storagePackageM =  storagePackageMDao.queryByWaybillCode(waybillCode);
            if(storagePackageM != null && (StoragePackageMStatusEnum.CAN_SEND_2.getCode().equals(storagePackageM.getStatus().toString())
			|| StoragePackageMStatusEnum.SEND_4.getCode().equals(storagePackageM.getStatus().toString()))){
				return true;
			}
			return false;
		}else{
			return true;
		}


	}


	/**
	 * 检查是否上架过
	 * 通过暂存明细表中的数据判断
	 * @param barCode
	 * @return  储位号
	 */
	public StoragePackageD checkExistStorage(String barCode){
		StoragePackageD lastStoragePackageD = null;
		if(WaybillUtil.isWaybillCode(barCode)){
			lastStoragePackageD = storagePackageDDao.findLastStoragePackageDByWaybillCode(barCode);
		}else{
			lastStoragePackageD = storagePackageDDao.findLastStoragePackageDByPackageCode(barCode);
		}

		if(lastStoragePackageD != null ){
			return lastStoragePackageD;
		}
		return null;
	}

	@Override
	public void makeWaybillSend(String waybillCode) {
		if(StringUtils.isNotBlank(waybillCode)){
			storagePackageMDao.updateStoragePackageMStatusForBeSendOfPWaybill(waybillCode);
		}
	}

	@Override
	public void updateStatusOnSend(String waybillCode, String packageCode) {
		String realWaybillCode = "";
		if(StringUtils.isBlank(waybillCode)){

			if(WaybillUtil.isPackageCode(packageCode)){
				realWaybillCode = SerialRuleUtil.getWaybillCode(packageCode);
			}
		}else{
			realWaybillCode  = waybillCode;
		}

		if(StringUtils.isNotBlank(realWaybillCode)){
			//查询是否为金鹏订单
			if(waybillCommonService.isPerformanceWaybill(realWaybillCode)){

				storagePackageMDao.updateStoragePackageMStatusForBeSendOfPWaybill(realWaybillCode);

			}

		}

	}

    /**
     * 取消上架
     * 当前先本系统处理，外单和加履中心无法配合。
     * @param ids
     * @return
     */
    @Override
    public Boolean cancelPutaway(List<Long> ids) {

        //逻辑删除明细表
        for(Long id : ids){
            StoragePackageM storagePackageM = getDao().findById(id);
            if(storagePackageM!=null && StringUtils.isNotBlank(storagePackageM.getWaybillCode())){
                storagePackageDDao.cancelPutaway(storagePackageM.getWaybillCode());
            }
        }
        //逻辑删除主表
        deleteByIds(ids);

        return Boolean.TRUE;
    }

    /**
	 * 检查改履约单是否在其他分拣中心上架
	 * 通过暂存明细表中的数据判断
	 * @param putawayDTO
	 * @return  储位号
	 */
	private StoragePackageM checkExistStorageOfOtherSite(PutawayDTO putawayDTO,String performanceCode){

		//根据履约单号查询 上架记录

		List<StoragePackageM> storagePackageMs =  storagePackageMDao.queryByPerformanceCode(performanceCode);
		for(StoragePackageM storagePackageM : storagePackageMs){
			//上架分拣中心 与 当前操作不符 直接返回
			if(!storagePackageM.getCreateSiteCode().equals(Long.valueOf(putawayDTO.getCreateSiteCode()))){
				return storagePackageM;
			}
		}

		return null;
	}

	/**
	 *
	 * 保存暂存主表数据
	 *
	 *  如果存在则增加上架数
	 *
	 *  如果不存则新增
	 *
	 * @param putawayDTO
	 * @param isWaybillCode
	 * @param bigWaybillDto
	 * @return
	 */
	private void saveStoragePackageM(PutawayDTO putawayDTO,boolean isWaybillCode,BigWaybillDto bigWaybillDto){
		//运单号
		String waybillCode = bigWaybillDto.getWaybill().getWaybillCode();
		//先去根据运单号获取暂存主表数据
		StoragePackageM  perStoragePackageM  =  storagePackageMDao.queryByWaybillCode(waybillCode);

		if(perStoragePackageM != null && perStoragePackageM.getId() != null){
			//此运单上架过
			//更新上架数 累加1 此种情况只有按包裹上架的时候才会存在

			//当此次上架的储位号与之前的为同一个储位
			StoragePackageM storagePackageM = new StoragePackageM();

			if(StringUtils.isNotBlank(perStoragePackageM.getStorageCode())){
				storagePackageM.setStorageCode(perStoragePackageM.getStorageCode());

				List<String> storageCodeList =  Arrays.asList(perStoragePackageM.getStorageCode().split(Constants.SEPARATOR_COMMA))  ;
				if(!storageCodeList.contains(putawayDTO.getStorageCode())){
					//新储位号，  追加
					storagePackageM.setStorageCode(perStoragePackageM.getStorageCode()+Constants.SEPARATOR_COMMA+putawayDTO.getStorageCode());
				}

			}
			storagePackageM.setPerformanceCode(perStoragePackageM.getPerformanceCode());
			storagePackageM.setWaybillCode(perStoragePackageM.getWaybillCode());
			storagePackageM.setUpdateUser(putawayDTO.getOperatorErp());
			storagePackageM.setPutawayTime(new Date(putawayDTO.getOperateTime()));
			storagePackageMDao.updatePutawayPackageSum(storagePackageM);

		}else{
			//此运单没有上架过

			StoragePackageM storagePackageM = new StoragePackageM();

			storagePackageM.setWaybillCode(waybillCode);
			//如果为包裹号 初始成1
			if(isWaybillCode){
				storagePackageM.setPutawayPackageSum(Long.valueOf(bigWaybillDto.getPackageList().size()));
			}else{
				storagePackageM.setPutawayPackageSum(1L);
			}

			storagePackageM.setPerformanceCode(bigWaybillDto.getWaybill().getParentOrderId());
			storagePackageM.setPlanDeliveryTime(bigWaybillDto.getWaybill().getRequireTime());
			storagePackageM.setPackageSum(Long.valueOf(bigWaybillDto.getPackageList().size()));
			storagePackageM.setStatus(Integer.valueOf(StoragePackageMStatusEnum.PUTAWAY_1.getCode()));

			makeStoragePackageMBase(storagePackageM,putawayDTO);

			storagePackageMDao.insert(storagePackageM);

		}





	}

	private void makeStoragePackageMBase(StoragePackageM storagePackageM,PutawayDTO putawayDTO){

		storagePackageM.setStorageCode(putawayDTO.getStorageCode());
		storagePackageM.setCreateSiteCode(Long.valueOf(putawayDTO.getCreateSiteCode()));
		storagePackageM.setCreateSiteName(putawayDTO.getCreateSiteName());
		storagePackageM.setPutawayTime(new Date(putawayDTO.getOperateTime()));
		storagePackageM.setCreateUser(putawayDTO.getOperatorErp());
		storagePackageM.setUpdateUser(putawayDTO.getOperatorErp());

	}

	private void saveStoragePackageDs(PutawayDTO putawayDTO,boolean isWaybillCode,BigWaybillDto bigWaybillDto){

		List<StoragePackageD> StoragePackageDs = new ArrayList<StoragePackageD>();


		StoragePackageD storagePackageD = new StoragePackageD();
		storagePackageD.setWaybillCode(bigWaybillDto.getWaybill().getWaybillCode());
		storagePackageD.setPerformanceCode(bigWaybillDto.getWaybill().getParentOrderId());
		storagePackageD.setStorageCode(putawayDTO.getStorageCode());
		storagePackageD.setCreateSiteCode(Long.valueOf(putawayDTO.getCreateSiteCode()));
		storagePackageD.setCreateSiteName(putawayDTO.getCreateSiteName());
		storagePackageD.setPutawayTime(new Date(putawayDTO.getOperateTime()));
		storagePackageD.setCreateUser(putawayDTO.getOperatorErp());
		storagePackageD.setUpdateUser(putawayDTO.getOperatorErp());

		//如果扫描的是运单。则遍历运单下的所有包裹
		if(isWaybillCode){
			List<DeliveryPackageD> deliveryPackageDs = bigWaybillDto.getPackageList();
			for(DeliveryPackageD packages : deliveryPackageDs){
				StoragePackageD newStoragePackageD;
				try {
					newStoragePackageD = (StoragePackageD) storagePackageD.clone();
					newStoragePackageD.setPackageCode(packages.getPackageBarcode());
					StoragePackageDs.add(newStoragePackageD);
				} catch (CloneNotSupportedException e) {
					//不会报错。。
				}
				if(StoragePackageDs.size()==10){
					storagePackageDDao.batchInsert(StoragePackageDs);
					StoragePackageDs.clear();
				}

			}
			if(StoragePackageDs.size()>0){
				storagePackageDDao.batchInsert(StoragePackageDs);
			}

		}else{
			//包裹
			storagePackageD.setPackageCode(putawayDTO.getBarCode());

			storagePackageDDao.insert(storagePackageD);
		}


	}

	/**
	 * 更新暂存主表发货状态
	 * @param parentOrderId 履约单号
	 */
	public void updateStoragePackageMStatusForSendOfParentOrderId(String parentOrderId) {
		//获取履约单下所有运单
		List<String> childWaybillCodes = waybillQueryManager.getOrderParentChildList(parentOrderId);

		//所有运单下的包裹是否已经全部上架 是 - 更新暂存状态

		List<StoragePackageM> storagePackageMs =storagePackageMDao.queryByPerformanceCode(parentOrderId);

		if(storagePackageMs.size() > 0 && storagePackageMs.size() == childWaybillCodes.size()){
			//上架记录里的运单和 运单接口返回的运单数对应上 并且暂存上架包裹数也对应上 才会去更新暂存状态
			for(StoragePackageM storagePackageM : storagePackageMs){
				if(!storagePackageM.getPackageSum().equals(storagePackageM.getPutawayPackageSum())){
					return;
				}
			}

			//更新暂存状态 按履约单维度更新
			storagePackageMDao.updateStoragePackageMStatusForCanSendOfPerformanceCode(parentOrderId);
		}

	}

	/**
	 * 更新履约单下所有运单发货状态
	 * @param waybillCode
	 */
	public void updateStoragePackageMStatusForSend(String waybillCode){

		BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true,true, true, true);
		if(baseEntity == null || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
			throw new StorageException("无运单信息");
		}

		if(baseEntity.getData().getPackageList() == null || baseEntity.getData().getPackageList().size() == 0){
			throw new StorageException("无包裹信息");
		}

		if(!BusinessUtil.isPerformanceOrder(baseEntity.getData().getWaybill().getWaybillSign())){
			throw new StorageException("非加履中心订单");
		}

		updateStoragePackageMStatusForSendOfParentOrderId(baseEntity.getData().getWaybill().getParentOrderId());
	}


	private void updateWaybillStatus(PutawayDTO putawayDTO,boolean isWaybillCode,BigWaybillDto bigWaybillDto){


		Task tTask = new Task();
		tTask.setBoxCode(putawayDTO.getBarCode());

		tTask.setCreateSiteCode(putawayDTO.getCreateSiteCode());
		tTask.setKeyword2(putawayDTO.getBarCode());
		tTask.setReceiveSiteCode(putawayDTO.getCreateSiteCode());
		tTask.setType(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY);
		tTask.setTableName(Task.TABLE_NAME_WAYBILL);
		tTask.setSequenceName(Task.TABLE_NAME_WAYBILL_SEQ);
		tTask.setOwnSign(BusinessHelper.getOwnSign());
		tTask.setKeyword1(bigWaybillDto.getWaybill().getWaybillCode());//回传运单状态
		tTask.setFingerprint(Md5Helper.encode(putawayDTO.getCreateSiteCode() + "_"
				+ putawayDTO.getBarCode() + "-" + putawayDTO.getStorageCode() + "-" + putawayDTO.getOperateTime() ));


		WaybillStatus tWaybillStatus = new WaybillStatus();
		tWaybillStatus.setOperatorId(putawayDTO.getOperatorId());
		tWaybillStatus.setOperator(putawayDTO.getOperatorName());
		tWaybillStatus.setOperateTime(new Date(putawayDTO.getOperateTime()));
		tWaybillStatus.setOrgId(putawayDTO.getOrgId());
		tWaybillStatus.setOrgName(putawayDTO.getOrgName());
		tWaybillStatus.setCreateSiteCode(putawayDTO.getCreateSiteCode());
		tWaybillStatus.setCreateSiteName(putawayDTO.getCreateSiteName());
		tWaybillStatus.setCreateSiteType(putawayDTO.getCreateSiteType());
		tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY);
		tWaybillStatus.setWaybillCode(bigWaybillDto.getWaybill().getWaybillCode());
		// 运单自行区分 是包裹号还是运单号来更新状态
		tWaybillStatus.setPackageCode(putawayDTO.getBarCode());

		tTask.setBody(JsonHelper.toJson(tWaybillStatus));

		taskService.add(tTask);
	}

}
