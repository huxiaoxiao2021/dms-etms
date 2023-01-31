package com.jd.bluedragon.distribution.storage.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.exception.StorageException;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageDDao;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageMDao;
import com.jd.bluedragon.distribution.storage.domain.*;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
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

import java.io.BufferedWriter;
import java.util.*;
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
     * 运单上架缓存前缀
     * */
    public static final String STORAGE_PUT_AWAY_LOCK = "STORAGE_PUT_AWAY_LOCK_";
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
    private com.jd.bluedragon.distribution.base.service.BaseService baseService;

	@Override
	public Dao<StoragePackageM> getDao() {
		return this.storagePackageMDao;
	}

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
	@Override
	public boolean putaway(PutawayDTO putawayDTO) {
	    // 加锁防止并发导致多条相同数据
        if(checkIsLock(putawayDTO)){
            return true;
        }
		// 获取运单数据
        BigWaybillDto bigWaybillDto = checkAndGetBigWaybillDto(putawayDTO.getBarCode());

        // 组装基础数据
        initBasicData(putawayDTO);

        // 暂存逻辑处理
        boolean isWaybillCode = WaybillUtil.isWaybillCode(putawayDTO.getBarCode());
        Integer storageSource = putawayDTO.getStorageSource();
        if(StorageSourceEnum.JP_STORAGE.getCode().equals(storageSource)){
            // 金鹏暂存
            return dealWithJPZC(putawayDTO, isWaybillCode, bigWaybillDto);
        }else if(StorageSourceEnum.KY_STORAGE.getCode().equals(storageSource)
                || StorageSourceEnum.QPC_STORAGE.getCode().equals(storageSource)){
            // 快运、企配仓暂存
            return dealWithKYZC(putawayDTO, isWaybillCode, bigWaybillDto);
        }else {
            throw new StorageException("非法暂存类型，禁止上架!");
        }

	}

    /**
     * 设置缓存防止并发导致多条数据
     * @param putawayDTO
     * @return
     */
    private boolean checkIsLock(PutawayDTO putawayDTO) {
        try {
            String lockKey = STORAGE_PUT_AWAY_LOCK + putawayDTO.getBarCode() + Constants.UNDERLINE_FILL
                    + putawayDTO.getCreateSiteCode();
            if(cacheService.setNx(lockKey,StringUtils.EMPTY,Constants.TIME_SECONDS_ONE_MINUTE, TimeUnit.SECONDS)){
                return false;
            }
            log.warn("已有相同单号【{}】操作上架",putawayDTO.getBarCode());
        }catch (Exception e){
            log.error("根据单号【{}】、站点【{}】设置上架缓存异常!",putawayDTO.getBarCode(),putawayDTO.getCreateSiteCode(),e);
        }
        return true;
    }

    /**
     * 校验并获取运单信息
     * @param barCode
     * @return
     */
    private BigWaybillDto checkAndGetBigWaybillDto(String barCode) {
        String waybillCode = WaybillUtil.getWaybillCode(barCode);
        if(StringUtils.isEmpty(waybillCode)){
            throw new StorageException("单号不能为空!");
        }
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getWaybillAndPackByWaybillCode(waybillCode);
        if(baseEntity == null || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
            throw new StorageException("无运单信息");
        }
        BigWaybillDto bigWaybillDto = baseEntity.getData();
        if(bigWaybillDto.getPackageList() == null || bigWaybillDto.getPackageList().size() == 0){
            throw new StorageException("无包裹信息");
        }
        if(!WaybillUtil.isWaybillCode(barCode)){
            boolean packageNotExist = true;
            for(DeliveryPackageD deliveryPackageD :bigWaybillDto.getPackageList()){
                if(barCode.equals(deliveryPackageD.getPackageBarcode())){
                    packageNotExist = false;
                    break;
                }
            }
            if(packageNotExist){
                throw new StorageException("包裹不存在，请检查!");
            }
        }
        return bigWaybillDto;
    }

    /**
     * 初始化基础数据
     * @param putawayDTO
     */
    private void initBasicData(PutawayDTO putawayDTO) {
        //初始化 基础数据
        BaseStaffSiteOrgDto site = baseService.queryDmsBaseSiteByCode(putawayDTO.getCreateSiteCode().toString());
        if(site == null || site.getsId() == null){
            throw new StorageException("未获取到对应站点信息");
        }
        putawayDTO.setCreateSiteName(site.getSiteName());
        putawayDTO.setCreateSiteType(site.getSiteType());
        putawayDTO.setOrgId(site.getOrgId());
        putawayDTO.setOrgName(site.getOrgName());

    }

    private boolean dealWithKYZC(PutawayDTO putawayDTO, boolean isWaybillCode, BigWaybillDto dto) {

        //强制上架
        if(putawayDTO.getForceStorage()){
            // 更新暂存主表及明细表
            updateStorageCode(putawayDTO,dto);
        }else {
            if(WaybillUtil.isPackageCode(putawayDTO.getBarCode()) && checkPackageIsPutAway(putawayDTO.getBarCode())){
                // 已上架包裹不处理
                return true;
            }
            //存储暂存主表
            saveStoragePackageM( putawayDTO, isWaybillCode, dto);

            //存储暂存明细表
            saveStoragePackageDs( putawayDTO, isWaybillCode, dto);

        }

        // 15500 暂存上架状态
        // 8410 企配仓上架状态，和金鹏订单共用一个状态码
        updateWaybillStatusOfKYZC(putawayDTO,true);

        return true;
    }

    /**
     * 校验包裹是否上架
     * @param packageCode
     * @return
     */
    private boolean checkPackageIsPutAway(String packageCode) {
        StoragePackageD storagePackageD = storagePackageDDao.findLastStoragePackageDByPackageCode(packageCode);
        if(storagePackageD != null){
            return true;
        }
        return false;
    }

    private boolean dealWithJPZC(PutawayDTO putawayDTO, boolean isWaybillCode, BigWaybillDto dto) {

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
     *          1、不支持按运单维度强制上架
     *          2、包裹维度强制上架
     * </p>
     */
    private void updateStorageCode(PutawayDTO putawayDTO,BigWaybillDto dto) {
	    String waybillCode = WaybillUtil.getWaybillCode(putawayDTO.getBarCode());
        // 更新暂存主表储位号
        StoragePackageM storagePackageM = new StoragePackageM();
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
            // 运单维度强制上架不处理（现阶段只支持包裹维度强制上架）
            storagePackageM.setStorageCode(putawayDTO.getStorageCode());
            storagePackageM.setPerformanceCode(waybillCode);
            storagePackageM.setPutawayPackageSum(Long.valueOf(dto.getPackageList().size()));
            storagePackageM.setPutAwayCompleteTime(new Date(putawayDTO.getOperateTime()));
            storagePackageMDao.updateKYStorageCode(storagePackageM);
            storagePackageDDao.updateKYStorageCodeByWaybillCode(storagePackageD);
            List<StoragePackageD> noExist = getNotExistStoragePackageD(putawayDTO,dto, waybillCode);
            if (noExist == null) return;
            storagePackageDDao.batchInsert(noExist);
        }else {
            // 明细表储位变更
            storagePackageDDao.updateKYStorageCodeByPackageCode(storagePackageD);
            // 主表储位号变更
            storagePackageM.setStorageCode(setNewStorageCode(putawayDTO));
            storagePackageMDao.updateKYStorageCode(storagePackageM);
        }
    }

    /**
     * 设置主表储位号
     * @param putawayDTO
     * @return
     */
    private String setNewStorageCode(PutawayDTO putawayDTO) {
        String waybillCode = WaybillUtil.getWaybillCode(putawayDTO.getBarCode());
        Integer createSiteCode = putawayDTO.getCreateSiteCode();
        StoragePackageD StoragePackageD = new StoragePackageD();
        StoragePackageD.setWaybillCode(waybillCode);
        StoragePackageD.setCreateSiteCode(createSiteCode.longValue());
        List<String> storagePackageDS = storagePackageDDao.findStorageCodeByWaybillCodeAndSiteCode(StoragePackageD);
        StringBuilder storageCodeM = new StringBuilder(Constants.EMPTY_FILL);
        if(CollectionUtils.isEmpty(storagePackageDS)){
            return storageCodeM.toString();
        }
        Set<String> storageList = new HashSet<>(storagePackageDS);
        int count = 0;
        for (String storageCodeD : storageList){
            count ++;
            if(count < storageList.size()){
                storageCodeM.append(storageCodeD).append(Constants.SEPARATOR_COMMA);
            }else {
                storageCodeM.append(storageCodeD);
            }
        }
        return storageCodeM.toString();
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
            if(StorageSourceEnum.KY_STORAGE.getCode().equals(putawayDTO.getStorageSource())
                    || StorageSourceEnum.QPC_STORAGE.getCode().equals(putawayDTO.getStorageSource())){
                // 设置全部上架时间
                if(perStoragePackageM.getPackageSum() - perStoragePackageM.getPutawayPackageSum() == 1){
                    storagePackageM.setPutAwayCompleteTime(new Date(putawayDTO.getOperateTime()));
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
			if(StorageSourceEnum.JP_STORAGE.getCode().equals(putawayDTO.getStorageSource())){
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
     * 快运|企配仓暂存额外属性
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
        if(StorageSourceEnum.QPC_STORAGE.getCode().equals(putawayDTO.getStorageSource())){
            storagePackageM.setSource(StorageSourceEnum.QPC_STORAGE.getCode());
        }
        // 非金鹏订单无履约单，因该字段非空则用运单号代替
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
		if(StorageSourceEnum.JP_STORAGE.getCode().equals(putawayDTO.getStorageSource())){
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
					logger.warn("StoragePackageD对象转换异常！");
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
                result.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_NEED_TEMP_STORE));
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
        String waybillCode = WaybillUtil.getWaybillCode(barCode);
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if(waybill == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"运单信息不存在!");
            return result;
        }
        //企配仓校验
        boolean qpcWaybill = BusinessUtil.isEdn(waybill.getSendPay(), waybill.getWaybillSign());

        //如果是企配仓的运单并且不是B2B的运单的时候 ，校验当前操作场地是末级场地。
        if(qpcWaybill&& BusinessUtil.isNotB2B(waybill.getSendPay())){
            if(!loginSiteIsLast(waybill,waybillCode,siteCode)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"当前场地非末级分拣中心，禁止上架!");
                return result;
            }
        }
        //暂存(非企配仓)
        if(!qpcWaybill){
            if(!loginSiteIsLast(waybill,waybillCode,siteCode)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"当前场地非末级分拣中心，禁止上架!");
                return result;
            }
        }

        try {
            storageCheckDto.setPlanDeliveryTime(DateHelper.formatDateTime(waybill.getRequireTime()));
            if(waybillCommonService.isStorageWaybill(waybillCode)
                    || qpcWaybill){
                storageCheckDto.setStorageSource(StorageSourceEnum.KY_STORAGE.getCode());
                if (qpcWaybill) {
                    storageCheckDto.setStorageSource(StorageSourceEnum.QPC_STORAGE.getCode());
                }
                // 校验单号上架状态
                checkKyOrderPutAwayStatus(barCode,result);
                if(result.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                    return result;
                }
                if(!qpcWaybill && !timeCheck(waybill,barCode,siteCode)){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"货物距离预计送达时间较近，正常发运");
                    return result;
                }
            }else if(waybillCommonService.isPerformanceWaybill(waybillCode)){
                storageCheckDto.setStorageSource(StorageSourceEnum.JP_STORAGE.getCode());
                if(StringUtils.isBlank(waybill.getParentOrderId())){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"履约单号为空");
                    return result;
                }
                //检查是否已经上架过（强制拦截）
                StoragePackageD lastStoragePackageD = checkExistStorage(barCode);
                if(lastStoragePackageD != null){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"已上架，储位号："+lastStoragePackageD.getStorageCode());
                    return result;
                }
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
     * 校验快运单号上架状态
     *  1、运单号：
     *      1）、运单全部上架，则提示拦截
     *      2）、运单下包裹部分上架，则提示按包裹上架
     *  2、包裹号：
     *      1）、包裹号已上架（可强制上架到其他储位）
     *      2）、未上架，则提示运单下其他包裹储位号
     * @param barCode
     * @param result
     */
    private void checkKyOrderPutAwayStatus(String barCode,InvokeResult<StorageCheckDto> result) {
        if(WaybillUtil.isWaybillCode(barCode)){
            StoragePackageM storagePackageM = storagePackageMDao.queryByWaybillCode(barCode);
            if(storagePackageM == null){
                return;
            }
            if(storagePackageM.getPackageSum().equals(storagePackageM.getPutawayPackageSum())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"此运单已上架!");
                return;
            }else {
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"此运单已部分上架，请按包裹上架!");
                return;
            }
        }else {
            StoragePackageD storagePackageD = storagePackageDDao.findLastStoragePackageDByPackageCode(barCode);
            if(storagePackageD != null){
                result.getData().setStorageCode(storagePackageD.getStorageCode());
                result.customMessage(JdCResponse.CODE_CONFIRM,"包裹号已上架请核实!");
                return;
            }
            storagePackageD = storagePackageDDao.findLastStoragePackageDByWaybillCode(WaybillUtil.getWaybillCode(barCode));
            if(storagePackageD != null){
                result.getData().setStorageCode(storagePackageD.getStorageCode());
            }
        }
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
                    String.valueOf(siteCode),WaybillRouteEnum.RealTimeOperateType.SEAL_CAR_NEW_PACKAGE);
            List<WaybillRouteLinkCustDetailResp> secondList = vrsRouteManager.waybillRouteLinkQueryCondition(barCode,
                    String.valueOf(waybill.getOldSiteId()),WaybillRouteEnum.RealTimeOperateType.UNSEAL_CAR_NEW_PACKAGE);
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
            // 预分拣站点所属分拣中心
            Integer oldDmsCode = oldSite.getDmsId();
            if(oldDmsCode == null){
                oldDmsCode = oldSite.getSiteCode();
            }
            return siteCode.equals(oldDmsCode);
        }catch (Exception e){
            log.error("服务异常,异常信息:【{}】",e.getMessage(),e);
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
        return 0;
    }

    /**
     * 根据条件导出
     *
     * @param condition
     * @param bufferedWriter
     * @return
     */
    @Override
    public void getExportData(StoragePackageMCondition condition, BufferedWriter bufferedWriter) {
        try {
            long start = System.currentTimeMillis();
            // 报表头
            Map<String, String> headerMap = getHeaderMap();
            //设置最大导出数量
            Integer MaxSize  =  exportConcurrencyLimitService.uccSpotCheckMaxSize();
            Integer oneQuery = exportConcurrencyLimitService.getOneQuerySizeLimit();
            //设置单次导出数量
            condition.setLimit(oneQuery);
            CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());

            int queryTotal = 0;
            int index = 1;
            while (index <= (MaxSize/oneQuery)+1){
                condition.setOffset((index-1) * oneQuery);
                index++;
                List<StoragePackageM> list = storagePackageMDao.queryExportByCondition(condition);
                if(CollectionUtils.isEmpty(list)){
                    break;
                }

                List<StoragePackageMExportDto>  dataList =  transForm(list);
                // 输出至csv文件中
                CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, dataList);
                // 限制导出数量
                queryTotal += dataList.size();
                if(queryTotal > MaxSize ){
                    break;
                }
            }
            long end = System.currentTimeMillis();
            exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(condition), ExportConcurrencyLimitEnum.STORAGE_PACKAGE_M_REPORT.getName(), end-start,queryTotal);
        }catch (Exception e){
            log.error("暂存管理 export error",e);
        }
    }

    private List<StoragePackageMExportDto> transForm(List<StoragePackageM> list) {
        List<StoragePackageMExportDto> dataList = new ArrayList<>();
        for(StoragePackageM detail : list){
            StoragePackageMExportDto body = new StoragePackageMExportDto();
            body.setSource(StorageSourceEnum.getNameByKey(detail.getSource()));
            body.setPerformanceCode(detail.getPerformanceCode());
            body.setWaybillCode(detail.getWaybillCode());
            body.setPackageSum(detail.getPackageSum());
            body.setPutawayPackageSum(detail.getPutawayPackageSum());
            body.setStorageCode(detail.getStorageCode());
            body.setStatus(detail.getStatus()==null?null:detail.getStatus()==1?"已上架":detail.getStatus()==2?"可发货":detail.getStatus()==3?"强制可发货":detail.getStatus()==4?"已发货":"未知状态");
            body.setPlanDeliveryTime(detail.getPlanDeliveryTime() == null ? null : DateHelper.formatDate(detail.getPlanDeliveryTime(), Constants.DATE_TIME_FORMAT));
            body.setPutawayTime(detail.getPutawayTime() == null ? null : DateHelper.formatDate(detail.getPutawayTime(), Constants.DATE_TIME_FORMAT));
            body.setCreateUser(detail.getCreateUser());
            body.setDownAwayTime(detail.getDownAwayTime() == null ? null : DateHelper.formatDate(detail.getDownAwayTime(), Constants.DATE_TIME_FORMAT));
            body.setCreateSiteName(detail.getCreateSiteName());
            body.setPutAwayCompleteTime(detail.getPutAwayCompleteTime() == null ? null : DateHelper.formatDate(detail.getPutAwayCompleteTime(), Constants.DATE_TIME_FORMAT));
            body.setDownAwayCompleteTime(detail.getDownAwayCompleteTime() == null ? null : DateHelper.formatDate(detail.getDownAwayCompleteTime(), Constants.DATE_TIME_FORMAT));
            dataList.add(body);
        }
        return dataList;
    }

    private Map<String, String> getHeaderMap() {
        Map<String,String> headerMap = new LinkedHashMap<>();
        //添加表头
        headerMap.put("source","来源");
        headerMap.put("performanceCode","履约单号");
        headerMap.put("waybillCode","运单号");
        headerMap.put("packageSum","系统包裹数");
        headerMap.put("putawayPackageSum","上架包裹数");
        headerMap.put("storageCode","储位号");
        headerMap.put("status","暂存状态");
        headerMap.put("planDeliveryTime","预计送达时间");
        headerMap.put("putawayTime","上架时间");
        headerMap.put("createUser","上架人erp");
        headerMap.put("downAwayTime","下架时间");
        headerMap.put("createSiteName","所属分拣中心");
        headerMap.put("putAwayCompleteTime","全部上架完成时间");
        headerMap.put("downAwayCompleteTime","全部下架完成时间");
        return headerMap;
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
     * 更新下架时间
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

    /**
     * 更新全部下架时间和状态
     *
     * @param waybillCode
     * @return
     */
    @Override
    public int updateDownAwayCompleteTimeAndStatusByWaybillCode(String waybillCode) {
        if(StringUtils.isEmpty(waybillCode)){
            return 0;
        }
        return storagePackageMDao.updateDownAwayCompleteTimeAndStatusByWaybillCode(waybillCode);
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
    public StoragePackageM getStoragePackageM(String waybillCode) {
        if(StringUtils.isEmpty(waybillCode)){
            return null;
        }
        return storagePackageMDao.queryByWaybillCode(waybillCode);
    }

}
