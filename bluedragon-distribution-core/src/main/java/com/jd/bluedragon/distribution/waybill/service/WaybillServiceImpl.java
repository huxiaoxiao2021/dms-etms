package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ReturnsRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.DmsWaybillInfoResponse;
import com.jd.bluedragon.distribution.api.response.OrderPackage;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.dao.CancelWaybillDao;
import com.jd.bluedragon.distribution.waybill.domain.*;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.service.LossServiceManager;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
                    invokeResult.setMessage(SortingResponse.MESSAGE_29121);
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
        if(isOutZoneControl(packageCode)){
            return new DmsWaybillInfoResponse(JdResponse.CODE_WRONG_STATUS, JdResponse.MESSAGE_OUT_ZONE);
        }
        DmsWaybillInfoResponse response = getDmsWaybillInfoResponse(packageCode);
        return response;
    }

    /**
     * 正向运单是否是疫情超区 或者 春节禁售
     * @param waybillCode
     * @return true 是，false 不是
     */
    @Override
    public boolean isOutZoneControl(String waybillCode){
        String aWaybillCode = WaybillUtil.getWaybillCode(waybillCode);
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(aWaybillCode);
        if(waybill == null){
            log.info("疫情超区或者春节禁售运单判断，数据为空waybillCode{}",waybillCode);
            return false;
        }
        //-136 代表超区；具体逻辑上游（预分拣）控制
        if(uccPropertyConfiguration.isPreOutZoneSwitch()
                && BusinessUtil.isForeignForwardAndWaybillMarkForward(waybill.getWaybillSign())
                && waybill.getOldSiteId() != null && waybill.getOldSiteId() == Constants.WAYBILL_SITE_ID_OUT_ZONE){
            log.info("疫情超区或者春节禁售运单判断-拦截运单waybillCode{}",waybillCode);
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
		for (DeliveryPackageD waybillPackage : waybillDto.getPackageList()) {
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

    /**
     * 三方验货拦截，目前只强拦截四种类型
     * <ul>
     *     <li>29311：此单为[取消订单],请退货</li>
     *     <li>29312：此单为[拒收订单拦截],请退货</li>
     *     <li>29313：此单为[恶意订单拦截],请退货</li>
     *     <li>29316：此单为[白条强制拦截],请退货</li>
     * </ul>
     * @param interceptType
     * @param interceptMode
     * @return
     */
    private InvokeResult<Boolean> getResponseByInterceptType(Integer interceptType, Integer interceptMode) {
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (WaybillCancelInterceptTypeEnum.CANCEL.getCode() == interceptType) {
            if (interceptMode == WaybillCancelInterceptModeEnum.NOTICE.getCode()) {//目前三方验货 要求通知类型的也要拦截
                result.customMessage(SortingResponse.CODE_29311, SortingResponse.MESSAGE_29311);
                return result;
            }
            if (interceptMode == WaybillCancelInterceptModeEnum.INTERCEPT.getCode()) {
                result.customMessage(SortingResponse.CODE_29311, SortingResponse.MESSAGE_29311);
                return result;
            }
        }

        if (WaybillCancelInterceptTypeEnum.REFUSE.getCode() == interceptType) {
            result.customMessage(SortingResponse.CODE_29312, SortingResponse.MESSAGE_29312);
            return result;
        }

        if (WaybillCancelInterceptTypeEnum.MALICE.getCode() == interceptType) {
            result.customMessage(SortingResponse.CODE_29313, SortingResponse.MESSAGE_29313);
            return result;
        }

        if (WaybillCancelInterceptTypeEnum.WHITE.getCode() == interceptType) {
            result.customMessage(SortingResponse.CODE_29316, SortingResponse.MESSAGE_29316);
            return result;
        }

        return result;
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

        return cancelWaybills.get(0);
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
}
