package com.jd.bluedragon.distribution.storage.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.exception.StorageException;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageDDao;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageMDao;
import com.jd.bluedragon.distribution.storage.domain.KYStorageMessage;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StorageCheckDto;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageMCondition;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageMStatusEnum;
import com.jd.bluedragon.distribution.storage.domain.StoragePutStatusEnum;
import com.jd.bluedragon.distribution.storage.domain.StorageSourceEnum;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.api.common.enums.WaybillRouteEnum;
import com.jd.etms.api.waybillroutelink.resp.WaybillRouteLinkCustDetailResp;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.SiteSignTool;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

	/**
     * 运单是否需要暂存缓存前缀
     * */
    public static final String IS_NEED_STORAGE_LOCK_BEGIN = "IS_NEED_STORAGE_LOCK_";
    /**
     * 运单是否需要暂存提示
     * */
    public static final Integer HINT_CODE = 201;
    public static final String HINT_MESSAGE = "运单需暂存，请操作暂存上架";
    /**
     * 默认时间值
     * */
    public static final Integer DIFF_HOURS = 24;
    public static final Integer MS_TRANSFER_S = 1000;

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

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Autowired
    private VrsRouteTransferRelationManager vrsRouteManager;

    @Autowired
    @Qualifier("kyStorageProducer")
    private DefaultJMQProducer kyStorageProducer;

    @Autowired
    private SendDetailService sendDetailService;

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

		BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(needInitWaybillCode, true,true, true, false);
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

		if(putawayDTO.getStorageSource() == null
                || StorageSourceEnum.JP_STORAGE.getCode().equals(putawayDTO.getStorageSource())){
            return dealWithJPZC(putawayDTO, isWaybillCode, baseEntity.getData());
        }else if(StorageSourceEnum.KY_STORAGE.getCode().equals(putawayDTO.getStorageSource())
                || StorageSourceEnum.QPC_STORAGE.getCode().equals(putawayDTO.getStorageSource())){
            return dealWithKYZC(putawayDTO, isWaybillCode, baseEntity.getData());
        }else {
            throw new StorageException("目前只支持金鹏暂存、快运暂存订单");
        }

	}

    private boolean dealWithKYZC(PutawayDTO putawayDTO, boolean isWaybillCode, BigWaybillDto dto) {

        //强制上架
        if(putawayDTO.getForceStorage()){
            // 更新暂存主表及明细表
            updateStorageCode(putawayDTO,dto);
        }else {
            //存储暂存主表
            saveStoragePackageM( putawayDTO, isWaybillCode, dto);

            //存储暂存明细表
            saveStoragePackageDs( putawayDTO, isWaybillCode, dto);

            // 全部上架对外MQ
            sendKYStorageMQ(putawayDTO);
        }

        // 15500 暂存上架状态
        // 8410 企配仓上架状态，和金鹏订单共用一个状态码
        updateWaybillStatusOfKYZC(putawayDTO,true);

        return true;
    }

    private boolean dealWithJPZC(PutawayDTO putawayDTO, boolean isWaybillCode, BigWaybillDto dto) {
        if(StringUtils.isBlank(dto.getWaybill().getParentOrderId())){
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
        BaseStaffSiteOrgDto bDto = baseMajorManager.getBaseSiteBySiteId(dto.getWaybill().getOldSiteId());
        if(bDto != null && bDto.getDmsId() != null){
            //末级分拣中心
            destinationDmsId = bDto.getDmsId();
        }
        if( destinationDmsId==null || !destinationDmsId.equals(putawayDTO.getCreateSiteCode())){
            throw new StorageException("只允许在末级分拣中心上架");
        }

        //存储暂存主表
        saveStoragePackageM( putawayDTO, isWaybillCode, dto);

        //存储暂存明细表
        saveStoragePackageDs( putawayDTO, isWaybillCode, dto);

        //推送运单更新状态 8400 暂存上架状态
        updateWaybillStatus( putawayDTO, isWaybillCode, dto);

        //更新暂存主表发货状态
        updateStoragePackageMStatusForSendOfParentOrderId(dto.getWaybill().getParentOrderId());
	    return true;
    }


    /**
     * <p>
     *      更新暂存主表及明细表
     * </p>
     */
    private void updateStorageCode(PutawayDTO putawayDTO,BigWaybillDto dto) {
	    String waybillCode = WaybillUtil.getWaybillCode(putawayDTO.getBarCode());
        // 更新暂存主表储位号
        StoragePackageM storagePackageM = new StoragePackageM();
        storagePackageM.setStorageCode(putawayDTO.getStorageCode());
        storagePackageM.setWaybillCode(waybillCode);
        storagePackageM.setPutawayTime(new Date(putawayDTO.getOperateTime()));
        storagePackageM.setUpdateUser(putawayDTO.getOperatorErp());
        // 更新暂存明细表储位号
        StoragePackageD storagePackageD = new StoragePackageD();
        storagePackageD.setStorageCode(putawayDTO.getStorageCode());
        storagePackageD.setWaybillCode(waybillCode);
        storagePackageD.setPackageCode(putawayDTO.getBarCode());
        storagePackageD.setPutawayTime(new Date(putawayDTO.getOperateTime()));
        storagePackageD.setUpdateUser(putawayDTO.getOperatorErp());
        if(WaybillUtil.isWaybillCode(putawayDTO.getBarCode())){
            storagePackageM.setPutawayPackageSum(Long.valueOf(dto.getPackageList().size()));
            storagePackageM.setPutAwayCompleteTime(new Date(putawayDTO.getOperateTime()));
            storagePackageMDao.updateKYStorageCode(storagePackageM);
            storagePackageDDao.updateKYStorageCodeByWaybillCode(storagePackageD);
            List<StoragePackageD> noExist = getNotExistStoragePackageD(putawayDTO,dto, waybillCode);
            if (noExist == null) return;
            storagePackageDDao.batchInsert(noExist);
            // 对外MQ
            sendKYStorageMQ(putawayDTO);
        }else {
            storagePackageMDao.updateKYStorageCode(storagePackageM);
            storagePackageDDao.updateKYStorageCodeByPackageCode(storagePackageD);
        }
    }

    /**
     * <p>
     *     1、获取未录入的暂存包裹
     *     2、未录入包裹发上架全程跟踪
     * <p/>
     * */
    private List<StoragePackageD> getNotExistStoragePackageD(PutawayDTO putawayDTO,BigWaybillDto dto, String waybillCode) {
        List<DeliveryPackageD> allPackageD = dto.getPackageList();
        List<String> allPackageCode = new ArrayList<>();
        for(DeliveryPackageD packageD : allPackageD){
            allPackageCode.add(packageD.getPackageBarcode());
        }
        List<StoragePackageD> existStoragePackageD = storagePackageDDao.findByWaybill(waybillCode);
        List<String> existPackageCode = new ArrayList<>();
        for(StoragePackageD storagePackage : existStoragePackageD){
            existPackageCode.add(storagePackage.getPackageCode());
        }
        List<String> noExistPackageCode = new ArrayList<>();
        for(String packageCode : allPackageCode){
            if(!existPackageCode.contains(packageCode)){
                noExistPackageCode.add(packageCode);
            }
        }
        if(CollectionUtils.isEmpty(noExistPackageCode)){
            return null;
        }
        List<StoragePackageD> noExist = new ArrayList<>();
        for(String packageCode : noExistPackageCode){
            StoragePackageD noExistStoragePackageD = new StoragePackageD();
            noExistStoragePackageD.setPerformanceCode(waybillCode);
            noExistStoragePackageD.setStorageCode(putawayDTO.getStorageCode());
            noExistStoragePackageD.setWaybillCode(waybillCode);
            noExistStoragePackageD.setCreateSiteCode(Long.valueOf(putawayDTO.getCreateSiteCode()));
            noExistStoragePackageD.setCreateSiteName(putawayDTO.getCreateSiteName());
            noExistStoragePackageD.setCreateUser(putawayDTO.getOperatorErp());
            noExistStoragePackageD.setUpdateUser(putawayDTO.getOperatorErp());
            noExistStoragePackageD.setPutawayTime(new Date(putawayDTO.getOperateTime()));
            noExistStoragePackageD.setPackageCode(packageCode);
            noExist.add(noExistStoragePackageD);
            // 更新运单状态
            putawayDTO.setBarCode(packageCode);
            updateWaybillStatusOfKYZC(putawayDTO,true);
        }
        return noExist;
    }


    private void sendKYStorageMQ(PutawayDTO putawayDTO) {
        String waybillCode = WaybillUtil.getWaybillCode(putawayDTO.getBarCode());
        if(isAllPutAwayAll(waybillCode)) {

            boolean qpcWaybill = StorageSourceEnum.QPC_STORAGE.getCode().equals(putawayDTO.getStorageSource());
            if (!qpcWaybill) {

                sendPutAwayMQ(putawayDTO, waybillCode);
            }
        }
    }

    private void sendPutAwayMQ(PutawayDTO putawayDTO, String waybillCode) {
        KYStorageMessage kYStorageMessage = new KYStorageMessage();
        kYStorageMessage.setWaybillCode(waybillCode);
        kYStorageMessage.setOperateSiteCode(putawayDTO.getCreateSiteCode());
        kYStorageMessage.setOperateSiteName(putawayDTO.getCreateSiteName());
        kYStorageMessage.setOperateTime(new Date(putawayDTO.getOperateTime()));
        kYStorageMessage.setOperateErp(putawayDTO.getOperatorErp());
        kYStorageMessage.setStorageStatus(StoragePutStatusEnum.STORAGE_PUT_AWAY.getCode());
        this.log.info("运单暂存全部上架发送MQ【{}】,业务ID【{}】,消息体【{}】",
                kyStorageProducer.getTopic(),waybillCode,JsonHelper.toJson(kYStorageMessage));
        kyStorageProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(kYStorageMessage));
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
            //快运暂存订单
            if(waybillCommonService.isStorageWaybill(realWaybillCode) && isAllPutAwayAll(realWaybillCode)){
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

				if(putawayDTO.getStorageSource() == null
                        || StorageSourceEnum.JP_STORAGE.getCode().equals(putawayDTO.getStorageSource())){
                    List<String> storageCodeList =  Arrays.asList(perStoragePackageM.getStorageCode().split(Constants.SEPARATOR_COMMA))  ;
                    if(!storageCodeList.contains(putawayDTO.getStorageCode())){
                        //新储位号，  追加
                        storagePackageM.setStorageCode(perStoragePackageM.getStorageCode()+Constants.SEPARATOR_COMMA+putawayDTO.getStorageCode());
                    }
                }else {
				    // 快运暂存，主表直接存储最新储位号，保持主表储位只有一个
                    storagePackageM.setStorageCode(putawayDTO.getStorageCode());
                    // 设置全部上架时间
                    if(perStoragePackageM.getPackageSum() - perStoragePackageM.getPutawayPackageSum() == 1){
                        storagePackageM.setPutAwayCompleteTime(new Date(putawayDTO.getOperateTime()));
                    }
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
			if(putawayDTO.getStorageSource() == null
                    || StorageSourceEnum.JP_STORAGE.getCode().equals(putawayDTO.getStorageSource())){
                storagePackageM.setSource(StorageSourceEnum.JP_STORAGE.getCode());
                storagePackageM.setPerformanceCode(bigWaybillDto.getWaybill().getParentOrderId());
            }else {
                appendExtraAttr(storagePackageM,putawayDTO,isWaybillCode,bigWaybillDto);
            }
			storagePackageM.setPlanDeliveryTime(bigWaybillDto.getWaybill().getRequireTime());
			storagePackageM.setPackageSum(Long.valueOf(bigWaybillDto.getPackageList().size()));
			storagePackageM.setStatus(Integer.valueOf(StoragePackageMStatusEnum.PUTAWAY_1.getCode()));

			makeStoragePackageMBase(storagePackageM,putawayDTO);

			storagePackageMDao.insert(storagePackageM);

		}





	}

    /**
     * 快运暂存额外属性
     * <p>
     *      来源、全部上架时间、履约单号（以运单号代替）
     * </p>
     * @param storagePackageM
     * @param putawayDTO
     * @param isWaybillCode
     * @param bigWaybillDto
     */
    private void appendExtraAttr(StoragePackageM storagePackageM, PutawayDTO putawayDTO, boolean isWaybillCode,BigWaybillDto bigWaybillDto) {

        storagePackageM.setSource(StorageSourceEnum.KY_STORAGE.getCode());
        storagePackageM.setPerformanceCode(WaybillUtil.getWaybillCode(putawayDTO.getBarCode()));
        // 是运单或是一单一件
        if(isWaybillCode || bigWaybillDto.getPackageList().size() == 1){
            storagePackageM.setPutAwayCompleteTime(new Date(putawayDTO.getOperateTime()));
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
		if(putawayDTO.getStorageSource() == null
                || StorageSourceEnum.JP_STORAGE.getCode().equals(putawayDTO.getStorageSource())){
            storagePackageD.setPerformanceCode(bigWaybillDto.getWaybill().getParentOrderId());
        }else {
		    // 非金鹏订单无履约单号，用运单号代替
            storagePackageD.setPerformanceCode(WaybillUtil.getWaybillCode(putawayDTO.getBarCode()));
        }
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

    /**
     * 校验是否需要暂存
     *  <p>
     *      可暂存运单 且 B网快运中心 且 登录人所在机构与运单末级分拣中心一致,则提示"运单需暂存，请操作暂存上架"
     *      InvokeResult<Boolean>的data值表示，是否第一次上架校验
     *      <p/>
     *
     * @param barCode 运单/包裹
     * @param siteCode 站点
     * @return
     */
    @Override
    public InvokeResult<Boolean> checkIsNeedStorage(String barCode, Integer siteCode) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(false);
        if(siteCode == null
                || (!WaybillUtil.isPackageCode(barCode) && !WaybillUtil.isWaybillCode(barCode))){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        String waybillCode = WaybillUtil.getWaybillCode(barCode);
        try {
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(siteCode);
            if (baseSite == null) {
                result.parameterError("站点【" + siteCode + "】信息不存在...");
                return result;
            }
            // 可暂存运单 且 B网快运中心 且 登录人所在机构与运单末级分拣中心一致
            if(waybillCommonService.isStorageWaybill(waybillCode)
                    && baseSite.getSubType() != null && baseSite.getSubType() == Constants.B2B_SITE_TYPE
                    && loginSiteIsLast(null,waybillCode,siteCode)){
                result.setCode(HINT_CODE);
                result.setMessage(HINT_MESSAGE);
                String lockKey = IS_NEED_STORAGE_LOCK_BEGIN + waybillCode + "_" + siteCode;
                if(cacheService.setNx(lockKey,"",7, TimeUnit.DAYS)){
                    result.setData(true);
                }
            }
        }catch (Exception e){
            log.error("校验是否需要暂存,异常信息:【{}】",e.getMessage(),e);
            result.error(e);
        }
        return result;
    }

    /**
     * 暂存上架校验
     *
     * @param barCode 运单/包裹
     * @param siteCode 站点
     * @return
     */
    @Override
    public InvokeResult<StorageCheckDto> storageTempCheck(String barCode, Integer siteCode) {
        InvokeResult<StorageCheckDto> result = new InvokeResult<>();
        StorageCheckDto storageCheckDto = new StorageCheckDto();
        result.setData(storageCheckDto);
        try {
            String waybillCode = WaybillUtil.getWaybillCode(barCode);
            Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
            storageCheckDto.setPlanDeliveryTime(DateHelper.formatDateTime(waybill.getRequireTime()));
            boolean qpcWaybill = BusinessUtil.isEdn(waybill.getSendPay(), waybill.getWaybillSign());
            if(waybillCommonService.isStorageWaybill(waybillCode)
                    || qpcWaybill){
                storageCheckDto.setStorageSource(StorageSourceEnum.KY_STORAGE.getCode());
                if (qpcWaybill) {
                    storageCheckDto.setStorageSource(StorageSourceEnum.QPC_STORAGE.getCode());
                }
                if(!loginSiteIsLast(waybill,waybillCode,siteCode)){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"当前场地非末级B网场地，禁止上架");
                    return result;
                }
                StoragePackageD storagePackageD = checkExistStorage(barCode);
                if(storagePackageD != null){
                    storageCheckDto.setStorageCode(storagePackageD.getStorageCode());
                    if(!StringUtils.isEmpty(storagePackageD.getStorageCode())){
                        result.customMessage(JdCResponse.CODE_CONFIRM,"包裹号已上架请核实");
                        return result;
                    }
                }else {
                    StoragePackageM storagePackageM =  storagePackageMDao.queryByWaybillCode(waybillCode);
                    storageCheckDto.setStorageCode(storagePackageM==null?null:storagePackageM.getStorageCode());
                }
                if(!qpcWaybill && !timeCheck(waybill,barCode,siteCode)){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"货物距离预计送达时间较近，正常发运");
                    return result;
                }
            }else if(waybillCommonService.isPerformanceWaybill(waybillCode)){
                storageCheckDto.setStorageSource(StorageSourceEnum.JP_STORAGE.getCode());
            }else {
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"非暂存运单，无需上架");
            }
        }catch (Exception e){
            log.error("服务异常,异常信息:【{}】",e.getMessage(),e);
            result.error(e);
        }
        return result;
    }

    /**
     * 判断登录人所在机构与运单末级分拣中心一致
     *
     * @param waybillCode
     * @param siteCode
     * @return
     */
    private boolean loginSiteIsLast(Waybill waybill,String waybillCode,Integer siteCode){
        try {
            if(waybill == null){
                waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
                if (waybill == null) {
                    return false;
                }
            }
            BaseStaffSiteOrgDto oldSite = baseMajorManager.getBaseSiteBySiteId(waybill.getOldSiteId());
            if (oldSite == null) {
                return false;
            }
            return siteCode.equals(oldSite.getDmsId());
        }catch (Exception e){
            log.error("服务异常,异常信息:【{}】",e.getMessage(),e);
        }
        return false;
    }

    /**
     * 时间校验
     * <p>
     *     【预计送达时间-当前时间-送货在途时间】差值 >= 24小时 可以上架
     *     否则提示"货物距离预计送达时间较近，正常发运"
     *     预计送达时间：waybill的requireTime字段
     *     送货在途时间：路由中 预分拣站点解封车时间 - 末级分拣封车时间
     * </p>
     *
     * @param waybill 运单
     * @param barCode 运单号/包裹号
     * @param siteCode 站点
     * @return
     */
    private boolean timeCheck(Waybill waybill,String barCode, Integer siteCode){
        long vrsDiffTime = 0;
        try {
            List<WaybillRouteLinkCustDetailResp> firstList = vrsRouteManager.waybillRouteLinkQueryCondition(barCode,
                    String.valueOf(siteCode),WaybillRouteEnum.RealTimeOperateType.SEAL_CAR_NEW_PACKAGE.getValue());
            List<WaybillRouteLinkCustDetailResp> secondList = vrsRouteManager.waybillRouteLinkQueryCondition(barCode,
                    String.valueOf(waybill.getOldSiteId()),WaybillRouteEnum.RealTimeOperateType.UNSEAL_CAR_NEW_PACKAGE.getValue());
            vrsDiffTime = sortAndGetPlanOperateTime(firstList,siteCode) - sortAndGetPlanOperateTime(secondList,waybill.getOldSiteId());
        }catch (Exception e){
            log.error("获取路由在途时间异常,异常信息:【{}】",e.getMessage(),e);
        }
        long betweenHours = (waybill.getRequireTime().getTime() - new Date().getTime() - vrsDiffTime)
                /(MS_TRANSFER_S * Constants.TIME_SECONDS_ONE_HOUR);
        if(betweenHours >= DIFF_HOURS){
            return true;
        }
        return false;
    }

    /**
     * 获取网点操作时间（最近一次）
     *  按计划操作时间从大到小排序
     * @param list
     * @param siteCode
     */
    private long sortAndGetPlanOperateTime(List<WaybillRouteLinkCustDetailResp> list,Integer siteCode) {
        if(CollectionUtils.isEmpty(list)){
            return 0;
        }
        Collections.sort(list, new Comparator<WaybillRouteLinkCustDetailResp>() {
            @Override
            public int compare(WaybillRouteLinkCustDetailResp o1, WaybillRouteLinkCustDetailResp o2) {
                if(o1.getPlanOperateTime() == null || o2.getPlanOperateTime() == null){
                    return -1;
                }
                return o2.getPlanOperateTime().compareTo(o1.getPlanOperateTime());
            }
        });
//        for(WaybillRouteLinkCustDetailResp waybillRoute : list){
//            if(siteCode.equals(waybillRoute.getPlanNodeCode())){
//                return waybillRoute.getPlanOperateTime().getTime();
//            }
//        }
        return 0;
    }

    /**
     * 根据条件导出
     *
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(StoragePackageMCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();
        //添加表头
        heads.add("来源");
        heads.add("履约单号");
        heads.add("运单号");
        heads.add("系统包裹数");
        heads.add("上架包裹数");
        heads.add("储位号");
        heads.add("暂存状态");
        heads.add("预计送达时间");
        heads.add("上架时间");
        heads.add("上架人erp");
        heads.add("所属分拣中心");
        heads.add("全部上架完成时间");
        heads.add("全部下架完成时间");
        resList.add(heads);
        List<StoragePackageM> dataList = storagePackageMDao.queryExportByCondition(condition);
        if(dataList != null && dataList.size() > 0){
            //表格信息
            for(StoragePackageM detail : dataList){
                List<Object> body = Lists.newArrayList();
                body.add(StorageSourceEnum.getNameByKey(detail.getSource()));
                body.add(detail.getPerformanceCode());
                body.add(detail.getWaybillCode());
                body.add(detail.getPackageSum());
                body.add(detail.getPutawayPackageSum());
                body.add(detail.getStorageCode());
                body.add(detail.getStatus()==null?null:detail.getStatus()==1?"已上架":detail.getStatus()==2?"可发货":detail.getStatus()==3?"强制可发货":detail.getStatus()==4?"已发货":"未知状态");
                body.add(detail.getPlanDeliveryTime() == null ? null : DateHelper.formatDate(detail.getPlanDeliveryTime(), Constants.DATE_TIME_FORMAT));
                body.add(detail.getPutawayTime() == null ? null : DateHelper.formatDate(detail.getPutawayTime(), Constants.DATE_TIME_FORMAT));
                body.add(detail.getCreateUser());
                body.add(detail.getCreateSiteName());
                body.add(detail.getPutAwayCompleteTime() == null ? null : DateHelper.formatDate(detail.getPutAwayCompleteTime(), Constants.DATE_TIME_FORMAT));
                body.add(detail.getDownAwayCompleteTime() == null ? null : DateHelper.formatDate(detail.getDownAwayCompleteTime(), Constants.DATE_TIME_FORMAT));
                resList.add(body);
            }
        }
        return  resList;
    }

    /**
     * 获取分拣中心储位状态
     *
     * @param siteCode
     * @return
     */
    @Override
    public boolean getStorageStatusBySiteCode(Integer siteCode) {
        try {
            BaseSiteInfoDto baseSiteInfoDto = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
            if(baseSiteInfoDto == null || StringUtils.isEmpty(baseSiteInfoDto.getSiteSign())){
                return false;
            }
            return SiteSignTool.supportTemporaryStorage(baseSiteInfoDto.getSiteSign());
        }catch (Exception e){
            log.error("获取分拣中心储位状态异常,异常信息:【{}】",e.getMessage(),e);
        }
        return false;
    }

    /**
     * 更新分拣中心储位状态
     *
     * @param siteCode
     * @param isEnough
     * @param operateErp
     * @return
     */
    @Override
    public boolean updateStorageStatusBySiteCode(Integer siteCode, Integer isEnough,String operateErp) {
        try {
            BaseSiteInfoDto baseSiteInfoDto = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
            if(baseSiteInfoDto == null || StringUtils.isEmpty(baseSiteInfoDto.getSiteSign())){
                return false;
            }
            String newSiteSign = SiteSignTool.setSupportTemporaryStorage(baseSiteInfoDto.getSiteSign(), String.valueOf(isEnough));
            BaseStaffSiteDTO baseStaffSiteDTO = new BaseStaffSiteDTO();
            baseStaffSiteDTO.setSystemName(Constants.SYS_DMS);
            baseStaffSiteDTO.setSiteCode(baseSiteInfoDto.getSiteCode());
            baseStaffSiteDTO.setSiteName(baseSiteInfoDto.getSiteName());
            baseStaffSiteDTO.setSitePhone(baseSiteInfoDto.getTelephone());
            baseStaffSiteDTO.setAddress(baseSiteInfoDto.getAddress());
            baseStaffSiteDTO.setUpdateUser(operateErp);
            baseStaffSiteDTO.setProvinceId(baseSiteInfoDto.getProvinceId());
            baseStaffSiteDTO.setCityId(baseSiteInfoDto.getCityId());
            baseStaffSiteDTO.setCountryId(baseSiteInfoDto.getCountyId());
            baseStaffSiteDTO.setSiteSign(newSiteSign);
            return baseMajorManager.updateBaseSiteBasicProperty(baseStaffSiteDTO);
        }catch (Exception e){
            log.error("更新分拣中心储位状态异常,异常信息:【{}】",e.getMessage(),e);
        }
        return false;
    }

    /**
     * 是否全部上架
     *
     * @param waybillCode
     * @return
     */
    @Override
    public boolean isAllPutAwayAll(String waybillCode) {
        try {
            StoragePackageM storagePackageM = storagePackageMDao.queryByWaybillCode(waybillCode);
            return storagePackageM != null && storagePackageM.getPackageSum() == storagePackageM.getPutawayPackageSum();
        }catch (Exception e){
            log.error("服务异常");
        }
        return false;
    }

    /**
     * 运单下包裹是否已全部发货
     *
     * @param waybillCode
     * @param siteCode
     * @return
     */
    @Override
    public boolean packageIsAllSend(String waybillCode,Integer siteCode) {
        try {
            if(StringUtils.isEmpty(waybillCode) || siteCode == null){
                return false;
            }
            SendDetailDto sendDto = new SendDetailDto();
            sendDto.setCreateSiteCode(siteCode);
            sendDto.setWaybillCode(waybillCode);
            sendDto.setStatus(1);
            sendDto.setIsCancel(0);
            List<String> sendPackageList = sendDetailService.queryPackageByWaybillCode(sendDto);
            if(CollectionUtils.isEmpty(sendPackageList)){
                return false;
            }
            StoragePackageM storagePackageM = storagePackageMDao.queryByWaybillCode(waybillCode);
            return storagePackageM != null && storagePackageM.getPackageSum() == sendPackageList.size();
        }catch (Exception e){
            log.error("服务异常");
        }
        return false;
    }

    /**
     * 更新全部下架时间
     *
     * @param waybillCode
     * @return
     */
    @Override
    public int updateDownAwayTimeByWaybillCode(String waybillCode) {
        if(StringUtils.isEmpty(waybillCode)){
            return 0;
        }
        return storagePackageMDao.updateDownAwayTimeByWaybillCode(waybillCode);
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


    /**
     * 更新运单状态
     *
     * @param putawayDTO
     * @param isPutAway 是否上架
     */
    public void updateWaybillStatusOfKYZC(PutawayDTO putawayDTO,boolean isPutAway) {

        Task tTask = new Task();
        tTask.setKeyword1(putawayDTO.getBarCode());
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_STORAGE_KYZC));
        tTask.setCreateSiteCode(putawayDTO.getCreateSiteCode());
        tTask.setCreateTime(new Date(putawayDTO.getOperateTime()));
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setWaybillCode(WaybillUtil.getWaybillCode(putawayDTO.getBarCode()));
        status.setPackageCode(putawayDTO.getBarCode());
        status.setOperateTime(new Date(putawayDTO.getOperateTime()));
        status.setOperator(putawayDTO.getOperatorErp());

        boolean qpcWaybill = null != putawayDTO.getStorageSource() && StorageSourceEnum.QPC_STORAGE.getCode().equals(putawayDTO.getStorageSource());

        if(isPutAway){
            status.setRemark("分拣中心上架");
            status.setOperateType(WaybillStatus.WAYBILL_STATUS_PUTAWAY_STORAGE_KYZC);
        }else {
            status.setRemark("分拣中心下架");
            status.setOperateType(WaybillStatus.WAYBILL_STATUS_DOWNAWAY_STORAGE_KYZC);

            // 企配仓暂存下架
            if (qpcWaybill) {
                status.setOperateType(WaybillStatus.WAYBILL_INTERNAL_TRACK_OFF_SHELF);
            }
        }
        // 企配仓订单上架发全程跟踪，不更新运单状态
        if (isPutAway && qpcWaybill) {
            tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY));
            status.setRemark("分拣中心上架");
            status.setOperateType(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY);
        }
        status.setCreateSiteCode(putawayDTO.getCreateSiteCode());
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);
    }

	@Override
	public List<StoragePackageM> queryByWaybillCodeListAndSiteCode(List<String> waybillCodeList, Long createSiteCode) {
    	Map<String,Object> params=new HashMap<>();
    	params.put("waybillCodeList",waybillCodeList);
    	params.put("createSiteCode",createSiteCode);
    	return storagePackageMDao.queryByWaybillCodeListAndSiteCode(params);
	}

}
