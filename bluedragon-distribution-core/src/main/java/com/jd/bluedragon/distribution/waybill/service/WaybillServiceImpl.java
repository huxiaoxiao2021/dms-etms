package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.dms.BlockerQueryWSJsfManager;
import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.core.security.dataam.SecurityCheckerExecutor;
import com.jd.bluedragon.core.security.dataam.enums.SecurityDataMapFuncEnum;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ReturnsRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.request.WaybillForPreSortOnSiteRequest;
import com.jd.bluedragon.distribution.api.response.DmsWaybillInfoResponse;
import com.jd.bluedragon.distribution.api.response.OrderPackage;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.SiteTypeEnum;
import com.jd.bluedragon.distribution.print.service.ScheduleSiteSupportInterceptService;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.waybill.dao.CancelWaybillDao;
import com.jd.bluedragon.distribution.waybill.domain.*;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.service.LossServiceManager;
import com.jd.bluedragon.utils.*;
import com.jd.dms.ver.domain.JsfResponse;
import com.jd.dms.ver.domain.WaybillCancelJsfResponse;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.dto.WaybillVasDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

@Service
public class WaybillServiceImpl implements WaybillService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 记录拦截订单操作数据
     */
    public final static Integer INTERCEPT_RECORD_TYPE = -1;

    @Autowired
    private WaybillStatusService waybillStatusService;
    @Autowired
    private WaybillPackageApi waybillPackageApi;
    @Autowired
    private BoxService boxService;
    @Autowired
    WaybillQueryManager waybillQueryManager;

    @Autowired
    private SendDatailDao sendDetailDao;

    @Autowired
    AbnormalWayBillService abnormalWayBillService;
    @Autowired
    private LossServiceManager lossServiceManager;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private ReverseReceiveService reverseReceiveService;
	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
    private CancelWaybillDao cancelWaybillDao;

    @Autowired
    private TaskService taskService;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private CancelWaybillJsfManager cancelWaybillJsfManager;

    @Autowired
    private BlockerQueryWSJsfManager blockerQueryWSJsfManager;
    @Autowired
    private SiteService siteService;

    @Autowired
    private ScheduleSiteSupportInterceptService scheduleSiteSupportInterceptService;

    @Autowired
    private SecurityCheckerExecutor securityCheckerExecutor;

    /**
     * 普通运单类型（非移动仓内配）
     **/
    private static final Integer WAYBILL_TYPE_COMMON = 1;

    /**
     * 移动仓内配运单类型
     **/
    private static final Integer WAYBILL_TYPE_MOVING_WAREHOUSE_INNER = 2;

	private static final Integer CODE_WAYBILL_NOE_FOUND = 404;
	private static final String MESSAGE_WAYBILL_NOE_FOUND = "运单不存在";

	private static final String DEFAUIT_PACKAGE_WEIGHT = "0.0";
    /* 基础资料subType:6420快运中心 */
    private static final Integer SUBTYPE_6420 = 6420;
	@Override
    public BigWaybillDto getWaybill(String waybillCode) {
        String aWaybillCode = WaybillUtil.getWaybillCode(waybillCode);

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryPackList(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(aWaybillCode,
                wChoice);

        return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
    }

    @Override
    public BigWaybillDto getWaybill(String waybillCode, boolean isPackList) {
        String aWaybillCode = WaybillUtil.getWaybillCode(waybillCode);

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryPackList(isPackList);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(aWaybillCode,
                wChoice);

        return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
    }
    @Override
    public BigWaybillDto getWaybill(String waybillCode, boolean isPackList, boolean isExtend) {
        String aWaybillCode = WaybillUtil.getWaybillCode(waybillCode);

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryWaybillExtend(isExtend);
        wChoice.setQueryPackList(isPackList);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(aWaybillCode,
                wChoice);

        return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
    }
    @Override
    public BigWaybillDto getWaybillProduct(String waybillCode) {
        String aWaybillCode = WaybillUtil.getWaybillCode(waybillCode);

        WChoice wChoice = new WChoice();
        wChoice.setQueryGoodList(true);
        wChoice.setQueryWaybillC(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(aWaybillCode,
                wChoice);

        return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
    }

    @Override
    public BigWaybillDto getWaybillState(String waybillCode) {

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryWaybillC(Boolean.TRUE);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(waybillCode,
                wChoice);

        return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
    }

    @Override
    public Boolean doWaybillStatusTask(Task task) {
        try {
            // 妥投任务
            if(null != task.getType() && task.getType().equals(Task.TASK_TYPE_WAYBILL_FINISHED)) {
                waybillStatusService.sendModifyWaybillStatusFinished(task);
                // 除妥投外需要改变运单状态的任务
            } else {
                List<Task> taskList = new ArrayList<Task>();
                taskList.add(task);
                waybillStatusService.sendModifyWaybillStatusNotify(taskList);
            }
        } catch (Exception e) {
            log.error("调用运单接口[回传运单状态]或者[置妥投]失败:{}",JsonHelper.toJson(task), e);
            return false;
        }

        return true;
    }

    @Override
    public Boolean doWaybillTraceTask(Task task) {
        try {
            List<Task> taskList = new ArrayList<Task>();
            taskList.add(task);
            this.waybillStatusService.sendModifyWaybillTrackNotify(taskList);
        } catch (Exception e) {
            this.log.error("调用运单[回传全程跟踪]服务出现异常：{}",JsonHelper.toJson(task), e);
            return false;
        }
        return true;
    }

    @Override
    public WaybillPackageDTO getWaybillPackage(String packageCode) {
            return getPackageByWaybillInterface(packageCode);
    }

    private WaybillPackageDTO getPackageByWaybillInterface(String packageCode){

        if(packageCode == null || packageCode.length() == 0){
            return null;
        }

        //判断是否为包裹号，如果不是包裹号，先从箱号里边取值
        if(!WaybillUtil.isPackageCode(packageCode)){
            Box box = boxService.findBoxByCode(packageCode);
            if(box == null){
                return null;
            }
            double length = box.getLength() == null ? 0 : box.getLength();
            double width = box.getWidth() == null ? 0 : box.getWidth();
            double height = box.getHeight() == null ? 0 : box.getWidth();
            WaybillPackageDTO waybillPackageDTOTemp = new WaybillPackageDTO();
            waybillPackageDTOTemp.setPackageCode(packageCode);
            //长宽高
            waybillPackageDTOTemp.setLength(length);
            waybillPackageDTOTemp.setWidth(width);
            waybillPackageDTOTemp.setHeight(height);
            waybillPackageDTOTemp.setOriginalVolume(length*width*height);
            waybillPackageDTOTemp.setVolume(length*width*height);
            return waybillPackageDTOTemp;
        }else{
            String waybillCode = SerialRuleUtil.getWaybillCode(packageCode);
            BaseEntity<List<PackOpeFlowDto>> dtoList= waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
            if(dtoList!=null && dtoList.getResultCode()==1){
                List<PackOpeFlowDto> dto = dtoList.getData();
                if(dto!=null && !dto.isEmpty()) {
                    for(PackOpeFlowDto pack :dto){
                        if(packageCode.equals(pack.getPackageCode())){
                            WaybillPackageDTO waybillPackageDTOTemp = new WaybillPackageDTO();
                            waybillPackageDTOTemp.setWaybillCode(pack.getWaybillCode());
                            waybillPackageDTOTemp.setPackageCode(pack.getPackageCode());
                            waybillPackageDTOTemp.setWeight(pack.getpWeight());
                            //长宽高
                            waybillPackageDTOTemp.setLength(pack.getpLength());
                            waybillPackageDTOTemp.setWidth(pack.getpWidth());
                            waybillPackageDTOTemp.setHeight(pack.getpHigh());
                            double volume = null==pack.getpLength()||null==pack.getpWidth()||null==pack.getpHigh()?
                                    0.00 : pack.getpLength()*pack.getpWidth()*pack.getpHigh();
                            waybillPackageDTOTemp.setOriginalVolume(volume);
                            waybillPackageDTOTemp.setVolume(volume);
                            waybillPackageDTOTemp.setCreateUserCode(pack.getWeighUserId());
                            waybillPackageDTOTemp.setCreateTime(pack.getWeighTime());
                            return waybillPackageDTOTemp;
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public InvokeResult<Boolean> isReverseOperationAllowed(String waybillCode, Integer siteCode) throws Exception {

        InvokeResult<Boolean> invokeResult = new InvokeResult<Boolean>();
        //获取运单信息
        BigWaybillDto bigWaybillDto = this.getWaybill(waybillCode, false);
        if(bigWaybillDto != null && bigWaybillDto.getWaybillState() != null) {
            WaybillManageDomain waybillManageDomain = bigWaybillDto.getWaybillState();
            //判断运单是否妥投
            if (Constants.WAYBILL_DELIVERED_CODE.equals(waybillManageDomain.getWaybillState())) {
                //查询运单是否操作异常处理
                AbnormalWayBill abnormalWaybill = abnormalWayBillService.getAbnormalWayBillByWayBillCode(waybillCode, siteCode);
                //异常操作运单记录为空，不能进行逆向操作，需提示妥投订单逆向操作需提交异常处理记录
                if(abnormalWaybill == null) {
                    invokeResult.setData(false);
                    invokeResult.setCode(SortingResponse.CODE_29121);
                    invokeResult.setMessage(HintService.getHint(HintCodeConstants.WAYBILL_DELIVERED_WHILE_REVERSE));
                    return invokeResult;
                }
            }

        } else {
            String log = "isReverseOperationAllowed方法获取运单状态失败，waybillCode：" + waybillCode + ", siteCode：" + siteCode;
            this.log.error(log);
            throw new Exception(log);
        }

        //判断运单是否为仓储收货运单 是则提示 不强制
        // reverse_receive 中的包裹号字段存的是运单号
        if(sysConfigService.getConfigByName("reverse.receive.alert.switch") ){
            ReverseReceive reverseReceive = reverseReceiveService.findByPackageCode(waybillCode);
            if(reverseReceive!=null && reverseReceive.getCanReceive()!=null){
                if(reverseReceive.getCanReceive().equals(new Integer(1))){
                    //1代表已收货 则提示
                    invokeResult.setData(false);
                    invokeResult.setCode(SortingResponse.CODE_31121);
                    invokeResult.setMessage(SortingResponse.MESSAGE_31121);
                    return invokeResult;
                }
            }
        }

        //判断运单是否为报丢报损 是则提示 （切记报丢分拣时不需要提示 需要调用者去自行判断）

        if(sysConfigService.getConfigByName("reverse.loss.alert.switch") && WaybillUtil.isJDWaybillCode(waybillCode) && bigWaybillDto.getWaybill()!=null){
            String orderId = bigWaybillDto.getWaybill().getVendorId();
            if(StringUtils.isNotBlank(orderId)){
                int lossCount = this.lossServiceManager.getLossProductCountOrderId(orderId);
                if(lossCount>0){
                    //存在报丢
                    //存在未发货的报丢分拣不提示  因为还需要继续发货
                    SendDetail query = new SendDetail();
                    query.setCreateSiteCode(siteCode);
                    query.setWaybillCode(waybillCode);
                    int lossSortingSize = sendDetailDao.findLossSortingNoSendCount(query);
                    if(lossSortingSize == 0){
                        invokeResult.setData(false);
                        invokeResult.setCode(SortingResponse.CODE_31122);
                        invokeResult.setMessage(SortingResponse.MESSAGE_31122);
                        return invokeResult;
                    }


                }
            }
        }


        return invokeResult;
    }

    @Override
    public BigWaybillDto getWaybillProductAndState(String waybillCode) {
        String aWaybillCode = WaybillUtil.getWaybillCode(waybillCode);

        WChoice wChoice = new WChoice();
        wChoice.setQueryGoodList(true);
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillM(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(aWaybillCode, wChoice);

        return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
    }

    @Override
    public Waybill getWaybillByWayCode(String waybillCode) {
        return waybillQueryManager.getWaybillByWayCode(waybillCode);
    }

    /**
     * 根据waybillSign获取运单类型
     * waybillSign第14位==‘5’表示是移动仓内配单
     * @param waybillCode
     * @return
     */
    @Override
    public Integer getWaybillTypeByWaybillSign(String waybillCode){
        BigWaybillDto dto = getWaybillProductAndState(waybillCode);
        if(dto != null &&
                dto.getWaybill() != null &&
                StringUtils.isNotBlank(dto.getWaybill().getWaybillSign())){
            String waybillSign = dto.getWaybill().getWaybillSign();
            if(BusinessUtil.isMovingWareHouseInnerWaybill(waybillSign)){
                return WAYBILL_TYPE_MOVING_WAREHOUSE_INNER;
            }
        }
        return WAYBILL_TYPE_COMMON;
    }

    /**
     * 判断是否移动仓内配单
     * @param waybillCode
     * @return
     */
    @Override
    public boolean isMovingWareHouseInnerWaybill(String waybillCode){
        return WAYBILL_TYPE_MOVING_WAREHOUSE_INNER.equals(getWaybillTypeByWaybillSign(waybillCode));
    }

    @Override
    public DmsWaybillInfoResponse getDmsWaybillInfoAndCheck(String packageCode){
        BigWaybillDto waybillDto = this.getWaybill(packageCode);
        if (waybillDto == null || waybillDto.getWaybill() == null || waybillDto.getWaybillState() == null) {
            return new DmsWaybillInfoResponse(CODE_WAYBILL_NOE_FOUND, MESSAGE_WAYBILL_NOE_FOUND);
        }
        //-136 代表超区；具体逻辑上游（预分拣）控制
        if(isOutZoneControl(waybillDto.getWaybill())){
            return new DmsWaybillInfoResponse(JdResponse.CODE_WRONG_STATUS, JdResponse.MESSAGE_OUT_ZONE);
        }
        DmsWaybillInfoResponse response = getDmsWaybillInfoResponse(packageCode);
        return response;
    }

    /**
     * 正向运单是否是疫情超区 或者 春节禁售
     * @param waybill
     * @return true 是，false 不是
     */
    @Override
    public boolean isOutZoneControl(Waybill waybill){
        if(waybill == null){
            return false;
        }
        //-136 代表超区；具体逻辑上游（预分拣）控制
        if(uccPropertyConfiguration.isPreOutZoneSwitch()
                && BusinessUtil.isForeignForwardAndWaybillMarkForward(waybill.getWaybillSign())
                && waybill.getOldSiteId() != null && waybill.getOldSiteId() == Constants.WAYBILL_SITE_ID_OUT_ZONE){
            log.info("疫情超区或者春节禁售运单判断-拦截运单waybillCode{}",waybill.getWaybillCode());
            return true;
        }
        return false;
    }

    /**
     * 根据包裹号或者运单号获取运单相关信息
     */
    @Override
	public DmsWaybillInfoResponse getDmsWaybillInfoResponse(String packageCode) {
		Boolean isIncludePackage = WaybillUtil.isWaybillCode(packageCode);
		BigWaybillDto waybillDto = this.getWaybill(packageCode);
		if (waybillDto == null || waybillDto.getWaybill() == null || waybillDto.getWaybillState() == null) {
			return new DmsWaybillInfoResponse(CODE_WAYBILL_NOE_FOUND, MESSAGE_WAYBILL_NOE_FOUND);
		}

		Waybill waybill = waybillDto.getWaybill();
		BaseStaffSiteOrgDto receiveSite = NumberHelper.isPositiveNumber(waybill.getOldSiteId()) ? this.baseMajorManager
				.getBaseSiteBySiteId(waybill.getOldSiteId()) : null;
		BaseStaffSiteOrgDto transferSite = NumberHelper.isPositiveNumber(waybill.getTransferStationId()) ? this.baseMajorManager
				.getBaseSiteBySiteId(waybill.getTransferStationId()) : null;

		DmsWaybillInfoResponse response = new DmsWaybillInfoResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setAddress(waybill.getReceiverAddress());
		response.setReassignAddress(waybill.getNewRecAddr());
		response.setCky2(waybillDto.getWaybillState().getCky2());
		response.setMobile(waybill.getReceiverMobile());
		response.setPackageQuantity(waybill.getGoodNumber());
		response.setPayment(waybill.getPayment());
		response.setPaymentText(PaymentEnum.getNameByCode(waybill.getPayment()));
		response.setWaybillCode(waybill.getWaybillCode());
		response.setWaybillType(waybill.getWaybillType());
		response.setWaybillTypeText(WaybillTypeEnum.getNameByCode(waybill.getWaybillType()));
		response.setSendPay(waybill.getSendPay());
		response.setSiteId(waybill.getOldSiteId());
		response.setSiteName(receiveSite != null ? receiveSite.getSiteName() : null);
		response.setTransferStationId(waybill.getTransferStationId());
		response.setTransferStationName(transferSite != null ? transferSite.getSiteName() : null);

		this.appendPackages(packageCode, isIncludePackage, waybillDto, response);
        response.setMobile(StringHelper.phoneEncrypt(response.getMobile()));
		return response;
	}

	private void appendPackages(String packageCode, Boolean isIncludePackage, BigWaybillDto waybillDto,
			DmsWaybillInfoResponse response) {
        List<DeliveryPackageD> packageList = waybillDto.getPackageList();
        if(CollectionUtils.isEmpty(packageList)){
            response.setCode(CODE_WAYBILL_NOE_FOUND);
            response.setMessage(JdResponse.MESSAGE_RE_PRINT_NO_PACK_LIST);
            return;
        }
		for (DeliveryPackageD waybillPackage : packageList) {
			if (isIncludePackage || !isIncludePackage
					&& waybillPackage.getPackageBarcode().equalsIgnoreCase(packageCode)) {
				OrderPackage orderPackage = new OrderPackage();
				orderPackage.setPackageCode(waybillPackage.getPackageBarcode());
				orderPackage
						.setPackageWeight(waybillPackage.getAgainWeight() == null ? DEFAUIT_PACKAGE_WEIGHT
								: String.valueOf(waybillPackage.getAgainWeight()));
				response.addPackage(orderPackage);
			}
		}
	}

    @Override
    public InvokeResult<Boolean> thirdCheckWaybillCancel(PdaOperateRequest pdaOperateRequest) {
	    InvokeResult<Boolean> result = new InvokeResult<>();
	    result.success();

        String waybillCode = WaybillUtil.getWaybillCode(pdaOperateRequest.getPackageCode());

        // 判断运单是否存在
	    if (!this.waybillQueryManager.queryExist(waybillCode)) {
	        result.customMessage(InvokeResult.RESULT_NULL_WAYBILLCODE_CODE, InvokeResult.RESULT_NULL_WAYBILLCODE_MESSAGE);
	        return result;
        }

	    // 校验运单是否拦截
	    CancelWaybill cancelWaybill = this.getCancelWaybillByWaybillCode(waybillCode);
        if (null == cancelWaybill) {
            return result;
        }
        if (cancelWaybill.getInterceptType() != null) {
            result = this.getResponseByInterceptType(cancelWaybill.getInterceptType(), cancelWaybill.getInterceptMode());
        }

        // 发送全流程跟踪
        if (result.getCode() != InvokeResult.RESULT_SUCCESS_CODE) {
            this.pushSortingInterceptByFeatureType(pdaOperateRequest, cancelWaybill.getFeatureType());
        }

        return result;
    }


    /*
    * 是否是特安送服务的运单
    * 33位等于2，且增值服务中某个对象的vosNo=fr-a-0010
    * */
    @Override
    public boolean isSpecialRequirementTeAnSongService(String waybillCode, String waybillSign) {
        //33位不为2直接跳过
        if (! BusinessUtil.isSignChar(waybillSign, 33, '2')) {
            return false;
        }
        try {
            //获取增值服务信息
            BaseEntity<List<WaybillVasDto>> baseEntity = waybillQueryManager.getWaybillVasInfosByWaybillCode(waybillCode);
            log.info("运单getWaybillVasInfosByWaybillCode返回的结果为：{}", JsonHelper.toJson(baseEntity));
            if (baseEntity != null && baseEntity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode() && baseEntity.getData() != null) {
                List<WaybillVasDto> vasDtoList = baseEntity.getData();
                for (WaybillVasDto waybillVasDto : vasDtoList) {
                    if (waybillVasDto != null && Constants.TE_AN_SONG_SERVICE.equals(waybillVasDto.getVasNo())) {
                        return true;
                    }
                }
            } else {
                log.warn("运单{}获取增值服务信息失败！返回baseEntity: ", waybillCode, JsonHelper.toJson(baseEntity));
            }
        } catch (Exception e) {
            log.error("运单{}获取增值服务信息异常！", waybillCode, e);
        }
        return false;
    }

    /**
     * 1.从本地分拣中心获取WaybillCancel数据
     * 2.如果Waybillcancel为null,直接返回OK
     * 3.如果不为null,则从订单中间件获取Order数据
     * 4.结合WaybillCancel.featureType和Order的数据,联合判断
     * 4.1.订单取消和订单删除,这两种情况,订单中间件对应的字段是yn,且yn = 0,而featureType分别是-1和-2
     * 4.2.订单锁定,这种情况,订单中间件对应的字段是state (注意不是state2),且state > 100, 而featureType = -3
     * 4.3.退款100分订单和订单拦截,这两种情况,不用加订单中间件校验 (订单拦截是外单业务范畴)
     *
     * @param waybillCode
     * @return
     */
    @Override
    public JdCancelWaybillResponse dealCancelWaybill(String waybillCode) {
        JdCancelWaybillResponse response = new JdCancelWaybillResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        CancelWaybill cancelWaybill = this.getCancelWaybillByWaybillCode(waybillCode);
        if (cancelWaybill == null) {
            return response;
        }

        Integer featureType = cancelWaybill.getFeatureType();
        Integer interceptType = cancelWaybill.getInterceptType();

        if (interceptType != null) {
            // 走新逻辑
            InvokeResult<Boolean> invokeResult = this.getResponseByInterceptType(interceptType, cancelWaybill.getInterceptMode());
            response.setCode(invokeResult.getCode());
            response.setMessage(invokeResult.getMessage());
        } else {
            //走旧逻辑
            response = this.getResponseByFeatureType(featureType);
        }
        response.setFeatureType(featureType);
        response.setInterceptType(interceptType);
        return response;
    }

    /**
     * 理赔破损拦截专用拦截判断方法
     * 1. 仅有破损拦截时，不允许换单
     * 1. 收到可换单消息后，可以换单
     * @param waybillCode 运单号
     */
    @Override
    public JdCancelWaybillResponse dealCancelWaybillWithClaimDamaged(String waybillCode) {
        JdCancelWaybillResponse response = new JdCancelWaybillResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        CancelWaybill cancelWaybill = this.judgeCancelWaybillForClaimDamaged(waybillCode, true);
        if (cancelWaybill == null) {
            return response;
        }

        Integer featureType = cancelWaybill.getFeatureType();
        Integer interceptType = cancelWaybill.getInterceptType();

        if (interceptType != null) {
            // 走新逻辑
            InvokeResult<Boolean> invokeResult = this.getResponseByInterceptType(interceptType, cancelWaybill.getInterceptMode());
            response.setCode(invokeResult.getCode());
            response.setMessage(invokeResult.getMessage());
        } else {
            //走旧逻辑
            response = this.getResponseByFeatureType(featureType);
        }
        response.setFeatureType(featureType);
        response.setInterceptType(interceptType);
        return response;
    }

    @Override
    public JdCancelWaybillResponse dealCancelWaybill(PdaOperateRequest pdaOperate) {
        String waybillCode = WaybillUtil.getWaybillCode(pdaOperate.getPackageCode());

        JdCancelWaybillResponse jdResponse = dealCancelWaybill(waybillCode);
        if (!jdResponse.getCode().equals(JdResponse.CODE_OK)) {
            this.pushSortingInterceptByFeatureType(pdaOperate, jdResponse.getFeatureType());
        }
        return jdResponse;
    }

    @Override
    public BlockResponse checkWaybillBlock(String waybillCode, Integer featureType) {
        BlockResponse cancelResponse = new BlockResponse();
        //参数错误
        if (featureType == null) {
            cancelResponse.setMessage("入参的业务类型featureType不能为空");
            cancelResponse.setCode(BlockResponse.ERROR_PARAM);
            log.error(MessageFormat.format("按运单号{0}查询拦截,featureType为空", waybillCode));
            return cancelResponse;
        }
        //参数错误
        if (StringUtils.isBlank(waybillCode) || !WaybillUtil.isWaybillCode(waybillCode)) {
            cancelResponse.setMessage("运单号为空或格式非法");
            cancelResponse.setCode(BlockResponse.ERROR_PARAM);
            log.error(MessageFormat.format("按包裹号{0}查询拦截,waybillCode为空或格式非法", waybillCode));
            return cancelResponse;
        }
        CancelWaybill cancelWaybill = cancelWaybillDao.findWaybillCancelByCodeAndFeatureType(waybillCode, featureType);
        //无需拦截
        if (cancelWaybill == null) {
            cancelResponse.setMessage("没有拦截记录无需拦截");
            cancelResponse.setCode(BlockResponse.NO_NEED_BLOCK);
            log.info(MessageFormat.format("根据运单号：{0}未查到拦截记录", waybillCode));
            return cancelResponse;
        }
        //有拦截 锁定状态
        if (CancelWaybill.BUSINESS_TYPE_LOCK.equals(cancelWaybill.getBusinessType())) {
            //如果是包裹维度也需要拦截的业务类型
            if (CancelWaybill.FEATURE_TYPES_NEED_PACKAGE_DEAL.contains(featureType)) {
                log.info(MessageFormat.format("运单{0}拦截未完成，有包裹未处理。", waybillCode));
                List<CancelWaybill> cancelWaybills = cancelWaybillDao.findPackageCodesByFeatureTypeAndWaybillCode(
                        waybillCode, featureType, CancelWaybill.BUSINESS_TYPE_LOCK, CancelWaybill.BLOCK_PACKAGE_QUERY_NUMBER);
                List<String> packageCodes = getPackageCodes(cancelWaybills);
                Long PackageCount = cancelWaybillDao.findPackageCodeCountByFeatureTypeAndWaybillCode(waybillCode,
                        featureType, CancelWaybill.BUSINESS_TYPE_LOCK);
                cancelResponse.setBlockPackageCount(PackageCount);
                cancelResponse.setBlockPackages(packageCodes);
            }
            cancelResponse.setMessage("该运单拦截待处理");
            cancelResponse.setCode(BlockResponse.BLOCK);
            log.info(MessageFormat.format("根据运单号：{0}查询到该包裹未拦截状态", waybillCode));
            return cancelResponse;
        }
        //有拦截 解锁状态
        cancelResponse.setMessage("该运单拦截已解除");
        cancelResponse.setCode(BlockResponse.UNBLOCK);
        log.info(MessageFormat.format("根据包裹号：{0}该包裹拦截已解除", waybillCode));
        return cancelResponse;
    }

    @Override
    public BlockResponse checkPackageBlock(String packageCode, Integer featureType) {
        BlockResponse cancelResponse = new BlockResponse();
        if (featureType == null) {
            cancelResponse.setMessage("入参的业务类型featureType不能为空");
            cancelResponse.setCode(BlockResponse.ERROR_PARAM);
            log.error(MessageFormat.format("按包裹号{0}查询拦截,featureType为空", packageCode));
            return cancelResponse;
        }
        if (StringUtils.isBlank(packageCode) || !WaybillUtil.isPackageCode(packageCode)) {
            cancelResponse.setMessage("包裹号为空或格式非法");
            cancelResponse.setCode(BlockResponse.ERROR_PARAM);
            log.error(MessageFormat.format("按包裹号{0}查询拦截,packageCode为空或格式非法", packageCode));
            return cancelResponse;
        }
        //根据包裹号查询拦截记录
        CancelWaybill cancelWaybill = cancelWaybillDao.findPackageBlockedByCodeAndFeatureType(packageCode, featureType);
        if (cancelWaybill == null) {
            cancelResponse.setMessage("没有拦截记录无需拦截");
            cancelResponse.setCode(BlockResponse.NO_NEED_BLOCK);
            log.info(MessageFormat.format("根据包裹号：{0}未查到拦截记录", packageCode));
            return cancelResponse;
        }
        //锁定状态
        if (CancelWaybill.BUSINESS_TYPE_LOCK.equals(cancelWaybill.getBusinessType())) {
            cancelResponse.setMessage("该包裹拦截待处理");
            cancelResponse.setCode(BlockResponse.BLOCK);
            cancelResponse.setBlockPackageCount(1L);
            cancelResponse.setBlockPackages(Collections.singletonList(packageCode));
            log.info(MessageFormat.format("根据包裹号：{0}该包裹为拦截状态", packageCode));
            return cancelResponse;
        }
        //解锁状态
        cancelResponse.setMessage("该包裹拦截已解除");
        cancelResponse.setCode(BlockResponse.UNBLOCK);
        log.info(MessageFormat.format("根据包裹号：{0}查询拦截状态，该包裹拦截已解除", packageCode));
        return cancelResponse;
    }

    @Override
    public Integer getRouterFromMasterDb(String waybillCode, Integer createSiteCode) {
        // 根据waybillCode查库获取路由信息
        String router = waybillCacheService.getRouterByWaybillCode(waybillCode);
        if (StringUtils.isBlank(router)) {
            log.error("从数据库实时获取运单路由返回空|getRouterFromMasterDb：waybillCode={},createSiteCode={}", waybillCode, createSiteCode);
            return null;
        }
        Integer nextSiteCode = null;
        String[] routerNodes = router.split("\\|");
        for (int i = 0; i < routerNodes.length - 1; i ++) {
            int curNode = Integer.parseInt(routerNodes[i]);
            // 如果当前网点等于始发站点
            if (curNode == createSiteCode) {
                // 如果当前路由节点不是最后一个
                if (i != (routerNodes.length - 1)) {
                    // 获取下一个
                    nextSiteCode = Integer.parseInt(routerNodes[i + 1]);
                }
            }
        }
        return nextSiteCode;
    }

    @Override
    public String getRouterByWaybillCode(String waybillCode) {
        return waybillCacheService.getRouterByWaybillCode(waybillCode);
    }

    @Override
    public boolean allowFilePackFilter(Integer subType, String waybillSign) {
        if (!(Constants.BASE_SITE_DISTRIBUTION_CENTER.equals(subType)
                || Constants.SITE_SUBTYPE_SECOND.equals(subType)
                || Constants.SITE_SUBTYPE_THIRD.equals(subType))) {
            return false;
        }

        return BusinessHelper.fileTypePackage(waybillSign);
    }

    @Override
    public Integer getFinalOrFirstRouterFromDb(String waybillCode,int locationFlag) {
        // 根据waybillCode查库获取路由信息
        String router = waybillCacheService.getRouterByWaybillCode(waybillCode);
        log.info("获取路由字符串为{}",router);
        //如果没有路由信息 调用运单的路由信息 获取始发和目的转运中心
        if (StringUtils.isBlank(router)) {
            BaseEntity<Waybill> result = waybillQueryManager.getWaybillByWaybillCode(waybillCode);
            if(log.isInfoEnabled()){
                log.info("从数据库实时获取运单路由返回空|getWaybillByWaybillCode：waybillCode={},result={}", waybillCode, JsonHelper.toJson(result));
            }
            if(result.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode() && result.getData() != null){
                Waybill waybill = result.getData();
                WaybillExt waybillExt= waybill.getWaybillExt();
                if(waybillExt != null){
                    if(locationFlag == -1){
                        //将dmsid转成纯数字id
                        log.info("getFinalOrFirstRouterFromDb-router is null;return endDmsId:{}",waybillExt.getEndDmsId());
                        return waybillExt.getEndDmsId();
                    }else {
                        log.info("getFinalOrFirstRouterFromDb-router is null;return startDmsId:{}",waybillExt.getStartDmsId());
                        return waybillExt.getStartDmsId();
                    }
                }
            }
        }else{
            //如果从分拣数据库中能查询到路由信息
            String[] routerNodes = router.split("\\|");
            List<String> routerList = Arrays.asList(routerNodes);
            if(log.isInfoEnabled()){
                log.info("获取路由routerList字符串为{}", JsonHelper.toJson(routerList));
            }
            int routeSize = routerList.size();
            if(locationFlag == -1){
                //目的转运中心
                Integer lastSiteCode = 0;
                for(int i=routeSize-1; i>=0; i--){
                    //获取目的转运中心
                    String siteCode = routerList.get(i);
                    Site site = siteService.get(Integer.parseInt(siteCode));
                    if(site != null && SUBTYPE_6420.equals(site.getSubType())){
                        lastSiteCode = Integer.parseInt(siteCode);
                        break;
                    }
                }
                log.info("获取路由lastSiteCode字符串为{}",lastSiteCode);
                return lastSiteCode;
            }else if(locationFlag == 0){
                //始发转运中心code
                Integer startSiteCode = 0;
                for(String  routerStr : routerList){
                    //判断路由节点是否是始发转运中心 subtype 6420
                    Site site = siteService.get(Integer.parseInt(routerStr));
                    if(site != null && SUBTYPE_6420.equals(site.getSubType())){
                        startSiteCode = Integer.parseInt(routerStr);
                        break;
                    }
                }
                log.info("获取路由startSiteCode字符串为{}",startSiteCode);
                return startSiteCode;
            }
        }
        return null;
    }

    @Override
    public boolean isStartOrEndSite(Integer operateSiteCode, String waybillCode, int locationFlag) {
        //操作所属站点code和目的转运中心code
        Integer finalRouterCode = getFinalOrFirstRouterFromDb(waybillCode, locationFlag);
        if(operateSiteCode != null && Objects.equals(operateSiteCode,finalRouterCode)){
            log.info("isStartOrEndSite-return true;operateSiteCode={},waybillCode={}",operateSiteCode,waybillCode);
            return true;
        }else{
            log.info("isStartOrEndSite-return false;operateSiteCode={},waybillCode={}",operateSiteCode,waybillCode);
            return false;
        }
    }

    /**
     * 根据FeatureType获取拦截结果
     *
     * @param featureType
     * @return
     */
    private JdCancelWaybillResponse getResponseByFeatureType(Integer featureType) {
        Integer code = JdResponse.CODE_OK;
        String message = JdResponse.MESSAGE_OK;
        if (featureType != null) {
            if (CancelWaybill.FEATURE_TYPE_CANCELED.equals(featureType)) {
                code = SortingResponse.CODE_29302;
                message = SortingResponse.MESSAGE_29302;
            } else if (CancelWaybill.FEATURE_TYPE_DELETED.equals(featureType)) {
                code = SortingResponse.CODE_29302;
                message = SortingResponse.MESSAGE_29302;
            } else if (CancelWaybill.FEATURE_TYPE_REFUND100.equals(featureType)) {
                code = SortingResponse.CODE_29303;
                message = SortingResponse.MESSAGE_29303;
            } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT.equals(featureType)) {
                code = SortingResponse.CODE_29305;
                message = SortingResponse.MESSAGE_29305;
            } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_BUSINESS.equals(featureType)) {
                code = SortingResponse.CODE_29306;
                message = SortingResponse.MESSAGE_29306;
            } else if (CancelWaybill.FEATURE_TYPE_SICK.equals(featureType)) {
                code = SortingResponse.CODE_29307;
                message = SortingResponse.MESSAGE_29307;
            } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_LP.equals(featureType)) {
                code = SortingResponse.CODE_29308;
                message = SortingResponse.MESSAGE_29308;
            }
        }
        return new JdCancelWaybillResponse(code, message);
    }

    private List<String> getPackageCodes(List<CancelWaybill> cancelWaybills) {
        if (cancelWaybills == null || cancelWaybills.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<String> packageCodes = new ArrayList<String>(cancelWaybills.size());
        for (CancelWaybill cancelWaybill : cancelWaybills) {
            packageCodes.add(cancelWaybill.getPackageCode());
        }
        return packageCodes;
    }

    /**
     * 三方验货拦截，目前只强拦截四种类型
     * <ul>
     *     <li>29311：此单为[取消订单],请退货</li>
     *     <li>29312：此单为[拒收订单拦截],请退货</li>
     *     <li>29313：此单为[恶意订单拦截],请退货</li>
     *     <li>29316：此单为[白条强制拦截],请退货</li>
     *     <li>29317：此单为[运营退货拦截],请退货</li>
     * </ul>
     * @param interceptType
     * @param interceptMode
     * @return
     */
    private InvokeResult<Boolean> getResponseByInterceptType(Integer interceptType, Integer interceptMode) {
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (WaybillCancelInterceptTypeEnum.CANCEL.getCode() == interceptType) {
            if (interceptMode == WaybillCancelInterceptModeEnum.NOTICE.getCode()) {//目前三方验货 要求通知类型的也要拦截
                result.customMessage(SortingResponse.CODE_29311, HintService.getHint(HintCodeConstants.CANCEL_WAYBILL_INTERCEPT));
                return result;
            }
            if (interceptMode == WaybillCancelInterceptModeEnum.INTERCEPT.getCode()) {
                result.customMessage(SortingResponse.CODE_29311, HintService.getHint(HintCodeConstants.CANCEL_WAYBILL_INTERCEPT));
                return result;
            }
        }

        if (WaybillCancelInterceptTypeEnum.REFUSE.getCode() == interceptType) {
            result.customMessage(SortingResponse.CODE_29312, HintService.getHint(HintCodeConstants.REFUSE_RECEIVE_INTERCEPT));
            return result;
        }

        if (WaybillCancelInterceptTypeEnum.MALICE.getCode() == interceptType) {
            result.customMessage(SortingResponse.CODE_29313, HintService.getHint(HintCodeConstants.MALICIOUS_WAYBILL_INTERCEPT));
            return result;
        }

        // 病单提示
        if (WaybillCancelInterceptTypeEnum.STORAGE_SICK.getCode() == interceptType) {
            result.customMessage(SortingResponse.CODE_29315, SortingResponse.MESSAGE_29315);
            return result;
        }

        if (WaybillCancelInterceptTypeEnum.WHITE.getCode() == interceptType) {
            result.customMessage(SortingResponse.CODE_29316, HintService.getHint(HintCodeConstants.WHITE_BILL_FORCE_INTERCEPT));
            return result;
        }
        if (WaybillCancelInterceptTypeEnum.CANCEL_SYS_RETURN.getCode() == interceptType) {
            result.customMessage(SortingResponse.CODE_29317, HintService.getHint(HintCodeConstants.RETURN_GOODS_INTERCEPT));
            return result;
        }
        if (WaybillCancelInterceptTypeEnum.FULL_ORDER_FAIL.getCode() == interceptType) {
            result.customMessage(SortingResponse.CODE_29321, HintService.getHint(HintCodeConstants.FULL_ORDER_FAIL_INTERCEPT));
            return result;
        }
        return result;
    }

    /**
     * 获取理赔破损拦截数据
     *
     * @param cancelWaybills
     * @return
     */
    private CancelWaybill getClaimDamagedCancelWaybill(List<CancelWaybill> cancelWaybills, boolean allowClaimDamagedChange) {
        for (CancelWaybill cancelWaybill : cancelWaybills) {
            if (Objects.equals(WaybillCancelInterceptTypeEnum.CLAIM_DAMAGED.getCode(), cancelWaybill.getInterceptType())) {
                // 若有可换单数据
                if(allowClaimDamagedChange && Objects.equals(WaybillCancelInterceptModeEnum.NOTICE.getCode(), cancelWaybill.getInterceptMode())) {
                    return null;
                }
                // 如果仅有理赔拦截数据，且没有可换单数据
                if(Objects.equals(WaybillCancelInterceptModeEnum.INTERCEPT.getCode(), cancelWaybill.getInterceptMode())) {
                    return cancelWaybill;
                }
            }
        }
        return null;
    }


    private CancelWaybill getCancelWaybillByWaybillCode(String waybillCode) {
        List<CancelWaybill> cancelWaybills = this.cancelWaybillDao.getByWaybillCode(waybillCode);
        if (cancelWaybills == null || cancelWaybills.isEmpty()) {
            return null;
        }

        // 获取病单拦截
        CancelWaybill cancelWaybill = this.getSickCancelWaybill(cancelWaybills);
        if (cancelWaybill != null) {
            return cancelWaybill;
        }

        // 获取理赔拦截
        CancelWaybill claimDamagedCancelWaybill = this.getClaimDamagedCancelWaybill(cancelWaybills, false);
        if (claimDamagedCancelWaybill != null) {
            return claimDamagedCancelWaybill;
        }

        return cancelWaybills.get(0);
    }

    /**
     * 判断运单拦截
     * @param waybillCode 运单号
     * @param allowClaimDamagedChange 是否允许拦截后换单
     */
    private CancelWaybill judgeCancelWaybillForClaimDamaged(String waybillCode, boolean allowClaimDamagedChange) {
        List<CancelWaybill> cancelWaybills = this.cancelWaybillDao.getByWaybillCode(waybillCode);
        if (cancelWaybills == null || cancelWaybills.isEmpty()) {
            return null;
        }

        // 获取病单拦截
        CancelWaybill cancelWaybill = this.getSickCancelWaybill(cancelWaybills);
        if (cancelWaybill != null) {
            return cancelWaybill;
        }

        // 获取理赔拦截
        CancelWaybill claimDamagedCancelWaybill = this.getClaimDamagedCancelWaybill(cancelWaybills, allowClaimDamagedChange);
        if (claimDamagedCancelWaybill != null) {
            return claimDamagedCancelWaybill;
        }

        return cancelWaybills.get(0);
    }

    /**
     * 仅获取理赔破损拦截 包含 存在取消拦截场景
     * @param waybillCode
     * @return
     */
    public CancelWaybill checkClaimDamagedCancelWaybill(String waybillCode){
        List<CancelWaybill> cancelWaybills = this.cancelWaybillDao.getByWaybillCode(waybillCode);
        if (cancelWaybills == null || cancelWaybills.isEmpty()) {
            return null;
        }
        // 获取理赔拦截
        CancelWaybill claimDamagedCancelWaybill = this.getClaimDamagedCancelWaybill(cancelWaybills, true);
        if (claimDamagedCancelWaybill != null) {
            return claimDamagedCancelWaybill;
        }

        return null;
    }

    /**
     * 获取病单，有病单则优先返回病单 30病单 31 取消病单
     *
     * @param cancelWaybills
     * @return
     */
    private CancelWaybill getSickCancelWaybill(List<CancelWaybill> cancelWaybills) {
        for (CancelWaybill cancelWaybill : cancelWaybills) {
            if (CancelWaybill.FEATURE_TYPE_SICK.equals(cancelWaybill.getFeatureType())) {
                return cancelWaybill;
            }
            if (CancelWaybill.FEATURE_TYPE_SICK_CANCEL.equals(cancelWaybill.getFeatureType())) {
                return null;
            }
        }
        return null;
    }

    private void pushSortingInterceptByFeatureType(PdaOperateRequest pdaOperate, Integer featureType) {
        String recordMsg = this.getRecordMsg(featureType);
        this.pushSortingInterceptRecord(pdaOperate, recordMsg);
    }

    private String getRecordMsg(Integer featureType) {
        /*分拣信息链接*/
        String recordMsg = StringUtils.EMPTY;

        if (CancelWaybill.FEATURE_TYPE_LOCKED.equals(featureType)) {
            recordMsg = CancelWaybill.FEATURE_MSG_LOCKED;
        }
        else if (CancelWaybill.FEATURE_TYPE_CANCELED.equals(featureType)) {
            recordMsg = CancelWaybill.FEATURE_MSG_CANCELED;
        }
        else if (CancelWaybill.FEATURE_TYPE_DELETED.equals(featureType)) {
            recordMsg = CancelWaybill.FEATURE_MSG_CANCELED;
        }
        else if (CancelWaybill.FEATURE_TYPE_REFUND100.equals(featureType)) {
            recordMsg = CancelWaybill.FEATURE_MSG_REFUND100;
        }
        else if (CancelWaybill.FEATURE_TYPE_INTERCEPT.equals(featureType)) {
            recordMsg = CancelWaybill.FEATURE_MSG_INTERCEPT;
        }
        else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_BUSINESS.equals(featureType)) {
            recordMsg = CancelWaybill.FEATURE_MSG_INTERCEPT_BUSINESS;
        }
        else if (CancelWaybill.FEATURE_TYPE_SICK.equals(featureType)) {
            recordMsg = CancelWaybill.FEATURE_MSG_SICKCANCEL;
        }
        else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_LP.equals(featureType)) {
            recordMsg = CancelWaybill.FEATURE_MSG_INTERCEPT_LP;
        }

        return recordMsg;
    }

    private void pushSortingInterceptRecord(PdaOperateRequest request, String recordMsg) {
        ReturnsRequest sortingReturn = new ReturnsRequest();
        sortingReturn.setBusinessType(INTERCEPT_RECORD_TYPE);
        sortingReturn.setSiteCode(request.getCreateSiteCode());
        sortingReturn.setSiteName(request.getCreateSiteName());
        sortingReturn.setUserCode(request.getOperateUserCode());
        sortingReturn.setUserName(request.getOperateUserName());
        sortingReturn.setShieldsError(recordMsg);
        sortingReturn.setPackageCode(request.getPackageCode());
        sortingReturn.setOperateTime(request.getOperateTime());

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setOperateTime(request.getOperateTime());
        taskRequest.setKeyword1(String.valueOf(request.getCreateSiteCode()));
        taskRequest.setKeyword2(request.getPackageCode());
        taskRequest.setSiteCode(request.getCreateSiteCode());
        taskRequest.setReceiveSiteCode(request.getCreateSiteCode());
        taskRequest.setType(Task.TASK_TYPE_RETURNS);
        taskRequest.setBody(JsonHelper.toJson(request));

        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                + JsonHelper.toJson(sortingReturn)
                + Constants.PUNCTUATION_CLOSE_BRACKET;

        try {
            Task task = this.taskService.toTask(taskRequest, eachJson);
            this.taskService.add(task);
        }
        catch (Exception ex) {
            log.error("Failed to add task. req:{}", eachJson, ex);
        }
    }

    /**
     * 现场预分拣拦截校验
     * 校验逻辑：
     * 1.如果运单类型为重货网运单，即waybillsign 第36位=4 且 操作人ERP所属部分类型为分拣中心64类型
     * 2.只有重货网的运单现场调度站点可以选择京东帮类型的站点
     * 3.是否取消
     * 4.是否已退款
     * 5.预分拣站点校验滑道信息
     * @param waybillForPreSortOnSiteRequest
     * @return
     */
    @Override
    public InvokeResult<String> checkWaybillForPreSortOnSite(WaybillForPreSortOnSiteRequest waybillForPreSortOnSiteRequest) {
        InvokeResult<String> result = new InvokeResult<>();
        result.success();
        if (!uccPropertyConfiguration.getPreSortOnSiteSwitchOn()){
            return result;
        }
        // 信息安全校验
        com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Boolean> securityCheckResult
                = securityCheckerExecutor.verifyWaybillDetailPermission(SecurityDataMapFuncEnum.WAYBILL_PRINT, waybillForPreSortOnSiteRequest.getErp(), WaybillUtil.getWaybillCodeByPackCode(waybillForPreSortOnSiteRequest.getWaybill()));
        if(!securityCheckResult.codeSuccess()){
            result.error(securityCheckResult.getMessage());
            return result;
        }
        try{
            /*------------------------------------------------------------数据准备--------------------------------------------------------------*/
            //获取运单
            if (WaybillUtil.isPackageCode(waybillForPreSortOnSiteRequest.getWaybill())){
                //如果是包裹，提取运单号
                waybillForPreSortOnSiteRequest.setWaybill(WaybillUtil.getWaybillCodeByPackCode(waybillForPreSortOnSiteRequest.getWaybill()));
            }
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillForPreSortOnSiteRequest.getWaybill());
            if (waybill == null) {
                result.error("运单不存在。");
                log.warn("运单不存在：{}" , com.jd.bluedragon.utils.JsonHelper.toJson(waybillForPreSortOnSiteRequest));
                return result;
            }
            // 获取站点信息-预分拣站点
            BaseStaffSiteOrgDto siteOfSchedulingOnSite = baseMajorManager.getBaseSiteBySiteId(waybillForPreSortOnSiteRequest.getSiteOfSchedulingOnSite());
            if (siteOfSchedulingOnSite == null){
                result.error("预分拣站点信息不存在");
                log.warn("预分拣站点信息不存在：{}" , com.jd.bluedragon.utils.JsonHelper.toJson(waybillForPreSortOnSiteRequest));
                return result;
            }
            Site site = new Site();
            site.setType(siteOfSchedulingOnSite.getSiteType());
            site.setSubType(siteOfSchedulingOnSite.getSubType());
            // 获取登录人信息
            BaseStaffSiteOrgDto userInfo = baseMajorManager.getBaseStaffByErpNoCache(waybillForPreSortOnSiteRequest.getErp());
            if (userInfo == null){
                result.error("登陆人信息不存在");
                log.warn("登陆人信息不存在：{}" , com.jd.bluedragon.utils.JsonHelper.toJson(waybillForPreSortOnSiteRequest));
                return result;
            }
            /*------------------------------------------------------------规则校验----------------------------------------------------------------------------*/
            // 特殊品类自营逆向单不能返调度到仓
            String sendPayMap = waybill.getWaybillExt() == null ? null : waybill.getWaybillExt().getSendPayMap();
            if(BusinessUtil.isSelfReverse(waybill.getWaybillSign()) && BusinessHelper.isSpecialOrder(com.jd.bluedragon.utils.JsonHelper.json2MapByJSON(sendPayMap))
                    && BusinessUtil.isWmsSite(siteOfSchedulingOnSite.getSiteType())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, JdResponse.MESSAGE_SELF_REVERSE_SCHEDULE_ERROR);
                return result;
            }
            //规则1
            if(BusinessUtil.isSignChar(waybill.getWaybillSign(),36,'4') &&
                    SiteTypeEnum.SORTING_CENTER.getCode().equals(userInfo.getSiteType())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"此单为重货网运单，禁止分拣人员操作现场预分拣");
                return result;
            }
            //规则2
            if (!BusinessUtil.isSignChar(waybill.getWaybillSign(),36,'4') &&
                    SiteHelper.isBigElectricApplianceSite(site)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"此单非重货网运单，禁止选择京东帮网点");
                return result;
            }
            //规则3
            JsfResponse<WaybillCancelJsfResponse> cancelJsfResponseJsfResponse = cancelWaybillJsfManager.dealCancelWaybill(waybillForPreSortOnSiteRequest.getWaybill());
            if (!cancelJsfResponseJsfResponse.getCode().equals(JsfResponse.SUCCESS_CODE)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,cancelJsfResponseJsfResponse.getMessage());
                return result;
            }
            //规则4-已退款的禁止 操作现场预分拣
            JdCResponse jdCResponse = blockerQueryWSJsfManager.queryExceptionOrders(waybill.getWaybillCode());
            if(!jdCResponse.getCode().equals(JdCResponse.CODE_SUCCESS)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,jdCResponse.getMessage());
                return result;
            }

            // 规则6- 同城站点才能返调度
            InvokeResult<Boolean> invokeResult = scheduleSiteSupportInterceptService.checkSameCity(waybillForPreSortOnSiteRequest, waybill, userInfo);
            if (!invokeResult.codeSuccess()) {
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, invokeResult.getMessage());
                return result;
            }

            //自营逆向单（waybill_sign第一位=T），且为全球购订单（sendPay第8位 = 6），禁止反调度到普通库房「类型为wms」
            if(BusinessUtil.isReverseGlobalWaybill(waybill.getWaybillSign(), waybill.getSendPay())){
                if(BusinessUtil.isWmsSite(siteOfSchedulingOnSite.getSiteType())){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, JdResponse.MESSAGE_SELF_REVERSE_SCHEDULE_ERROR);
                    return result;
                }
            }

            //针对运费到付「waybillsign第25位=2」的运单，禁止反调度到三方网点「同cod限制逻辑，sitetype = 16」
            if(BusinessUtil.isDF(waybill.getWaybillSign())){
                if(Objects.equals(Constants.THIRD_SITE_TYPE, siteOfSchedulingOnSite.getSiteType())){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, JdResponse.MESSAGE_FORBIDDEN_SCHEDULE_TO_PARTNER_SITE);
                    return result;
                }
            }

            // 当前校验必须放在最后
            //规则5- 预分拣站点校验滑道信息  (因为存在确认跳过检验)
            InvokeResult<String>  crossResult =   scheduleSiteSupportInterceptService.checkCrossInfo(waybill.getWaybillSign(),waybill.getSendPay(),
                    waybill.getWaybillCode(),waybillForPreSortOnSiteRequest.getSiteOfSchedulingOnSite(),waybillForPreSortOnSiteRequest.getSortingSite());
            if(!crossResult.codeSuccess()){
                result.customMessage(crossResult.getCode(),crossResult.getMessage());
                return result;
            }


        }catch (Exception ex){
            log.error("WaybillService.checkWaybillForPreSortOnSite has error. The error is " + ex.getMessage(),ex);
            result.error("系统异常，请联系分拣小秘！");
        }
        return result;
    }

    /**
     * 百川业务开关，判断omcOrderCode是否有值，有值代表开启百川业务
     *
     * @param waybill
     * @return
     */
    @Override
    public String baiChuanEnableSwitch(Waybill waybill) {
        String omcOrderCode = null;
        if (waybill != null && waybill.getWaybillExt() != null) {
            omcOrderCode = waybill.getWaybillExt().getOmcOrderCode();
            if (StringUtils.isNotBlank(omcOrderCode)) {
                if (log.isInfoEnabled()) {
                    log.info("启用百川业务场景. waybillCode:{}, omcOrderCode:{}", waybill.getWaybillCode(), omcOrderCode);
                }
            }
        }

        return omcOrderCode;
    }

	@Override
	public boolean hasPrintIntercept(String waybillCode, String waybillSign) {
		/**
		 * 标位判断
		 */
		if(BusinessUtil.isChangeWaybillSign(waybillSign)) {
			return true;
		}
		BlockResponse response = this.checkWaybillBlock(waybillCode, CancelWaybill.FEATURE_TYPE_ORDER_MODIFY);
        if(response != null
        		&& BlockResponse.BLOCK.equals(response.getCode())) {
        	return true;
        }
		return false;
	}
}
