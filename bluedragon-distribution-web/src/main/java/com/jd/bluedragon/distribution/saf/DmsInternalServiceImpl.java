package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.core.base.DmsInterturnManager;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.AutoSortingBoxResult;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.DatadictResponse;
import com.jd.bluedragon.distribution.api.response.LossProductResponse;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.distribution.api.response.ReverseReceiveResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.SysConfigResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.base.service.UserService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.internal.service.DmsInternalService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.audit.AuditResource;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.bluedragon.distribution.rest.orders.ReassignWaybillResource;
import com.jd.bluedragon.distribution.rest.pop.PopPrintResource;
import com.jd.bluedragon.distribution.rest.product.LossProductResource;
import com.jd.bluedragon.distribution.rest.task.TaskResource;
import com.jd.bluedragon.distribution.rest.waybill.PreseparateWaybillResource;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

/**
 * @author dudong
 * @date 2016/1/5
 */
public class DmsInternalServiceImpl implements DmsInternalService {

    private static final Logger log = LoggerFactory.getLogger(DmsInternalServiceImpl.class);

    @Autowired
    private BoxResource boxResource;

    @Autowired
    private BaseResource baseResource;

    @Autowired
    private TaskResource taskResource;

    @Autowired
    private WaybillResource waybillResource;

    @Autowired
    private ReassignWaybillResource reassignWaybillResource;

    @Autowired
    private AuditResource auditResource;

    @Autowired
    private LossProductResource lossProductResource;

    @Autowired
    private PreseparateWaybillResource preseparateWaybillResource;

    @Autowired
    private PopPrintResource popPrintResource;

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private BoxService boxService;
    
	@Autowired
	private UserService userService;

    @Autowired
    private DmsInterturnManager dmsInterturnManager;

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Autowired
    private AbnormalWayBillService abnormalWayBillService;
    @Autowired
    SortingService sortingService;

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    /**
     * jsf监控key前缀
     */
    private static final String UMP_KEY_PREFIX = UmpConstants.UMP_KEY_JSF_SERVER + "DmsInternalService.";
    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getDatadict",mState = JProEnum.TP)
    public DatadictResponse getDatadict(Integer parentID, Integer nodeLevel, Integer typeGroup) {
        try {
            return baseResource.getOrignalBackBusIds(parentID, nodeLevel, typeGroup);
        } catch (Exception e) {
            log.error("getDatadict error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getBox",mState = JProEnum.TP)
    public BoxResponse getBox(String boxCode){
        try {
            return boxResource.get(boxCode);
        } catch (Exception e) {
            log.error("getBox error", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getSiteByCode",mState = JProEnum.TP)
    public BaseResponse getSiteByCode(String code) {
        try {
            return baseResource.getSite(code);
        } catch (Exception e) {
            log.error("getSiteByCode error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.addTask",mState = JProEnum.TP)
    public TaskResponse addTask(TaskRequest request) {
        try {
            if(request != null && Objects.equals(Task.TASK_TYPE_INSPECTION,request.getType())){
                log.warn("DmsInternalServiceImpl验货任务keyword2[{}]siteCode[{}]request[{}]", request.getKeyword2(),request.getSiteCode(), JsonHelper.toJson(request));
            }
            return taskResource.add(request);
        } catch (Exception e) {
            log.error("addTask error ", e);
            return null;
        }
    }

    /**
     * 查询运单是否已经确认耗材
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.isConsumableConfirmed",mState = JProEnum.TP,jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Boolean isConsumableConfirmed(String waybillCode) {
        try {
            return waybillConsumableRecordService.isConfirmed(waybillCode);
        } catch (Exception e) {
            log.error("isConsumableConfirmed error:{}", waybillCode, e);
            return null;
        }

    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getBelongSiteCode",mState = JProEnum.TP)
    public BaseResponse getBelongSiteCode(Integer code) {
        try{
            return baseResource.getselfD(code);
        }catch (Exception e){
            log.error("getBelongSiteCode error ", e);
            return null;
        }
    }

    /**
     *  根据三方-合作站点获取三方-合作站点所属自营站点
     *  @param code 三方-合作站点ID
     *  @return 三方-合作站点所属自营站点信息
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getThreePartnerBelongSiteCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = JProEnum.TP)
    public BaseResponse getThreePartnerBelongSiteCode(Integer code) {
        try{
            return baseResource.getThreePartnerD(code);
        }catch (Exception e){
            log.error("getThreePartnerBelongSiteCode error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getTargetDmsCenter",mState = JProEnum.TP)
    public BaseResponse getTargetDmsCenter(Integer startDmsCode, Integer siteCode) {
        try{
            return waybillResource.getTargetDmsCenter(startDmsCode, siteCode);
        }catch (Exception e){
            log.error("getTargetDmsCenter error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getLastScheduleSite",mState = JProEnum.TP)
    public BaseResponse getLastScheduleSite(String packageCode) {
        try{
            return reassignWaybillResource.queryLastScheduleSite(packageCode);
        }catch (Exception e){
            log.error("getLastScheduleSite error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getReverseReceive",mState = JProEnum.TP)
    public ReverseReceiveResponse getReverseReceive(String waybillCode) {
       try{
           return auditResource.getReverseReceiveByCode(waybillCode);
       } catch (Exception e){
           log.error("getReverseReceive error ", e);
           return null;
       }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getLossOrderProducts",mState = JProEnum.TP)
    public LossProductResponse getLossOrderProducts(Long orderId) {
        try{
            return lossProductResource.getLossOrderProducts(orderId.toString());
        }catch (Exception e){
            log.error("getLossOrderProducts error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getLossOrderProductsByWaybillCode",mState = JProEnum.TP)
    public LossProductResponse getLossOrderProductsByWaybillCode(String waybillCode) {
        try{
            return lossProductResource.getLossOrderProducts(waybillCode);
        }catch (Exception e){
            log.error("getLossOrderProducts error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getSwitchStatus",mState = JProEnum.TP)
    public SysConfigResponse getSwitchStatus(String conName) {
        try{
            return baseResource.getSwitchStatus(conName);
        }catch (Exception e){
            log.error("getSwitchStatus error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.createAutoSortingBox",mState = JProEnum.TP)
    public InvokeResult<AutoSortingBoxResult> createAutoSortingBox(BoxRequest request) {
        try{
            return boxResource.create(request);
        }catch (Exception e){
            log.error("createAutoSortingBox error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getPreseparateSiteId",mState = JProEnum.TP)
    public InvokeResult<Integer> getPreseparateSiteId(String waybillCode) {
        try{
            return preseparateWaybillResource.getPreseparateSiteId(waybillCode);
        }catch (Exception e){
            log.error("getPreseparateSiteId error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getPopPrintByWaybillCode",mState = JProEnum.TP)
    public PopPrintResponse getPopPrintByWaybillCode(String waybillCode) {
        try{
            return popPrintResource.findByWaybillCode(waybillCode);
        }catch (Exception e){
            log.error("getPreseparateSiteId error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.login", mState = JProEnum.TP)
    public BaseResponse login(String userName, String passwd) {
        try{
            LoginRequest request = new LoginRequest();
            request.setErpAccount(userName);
            request.setPassword(passwd);
            return userService.oldJsfLogin(request);
        }catch (Exception e){
            log.error("getPreseparateSiteId error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.isReverseOperationAllowed", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> isReverseOperationAllowed(String waybillCode, Integer siteCode) {
        //返回值初始化
        InvokeResult<Boolean> invokeResult = new InvokeResult<Boolean>();
        invokeResult.success();
        invokeResult.setData(true);
        try {
            //获取判断是否可以逆向操作的结果
            com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> result = waybillService.isReverseOperationAllowed(waybillCode, siteCode);
            if(result != null && com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
                invokeResult.setData(false);
                invokeResult.setCode(result.getCode());
                invokeResult.setMessage(result.getMessage());
            }
        } catch (Exception e) {
            invokeResult.setData(false);
            invokeResult.setCode(SortingResponse.CODE_29122);
            invokeResult.setMessage(SortingResponse.MESSAGE_29122);
            log.error("isReverseOperationAllowed方法异常", e);
        }
        return invokeResult;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.isBoxSent", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Boolean isBoxSent(String boxCode, Integer siteCode) {
        return boxService.checkBoxIsSent(boxCode, siteCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.dispatchToExpress", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> dispatchToExpress(Integer siteCode, Integer vendorId, String waybillSign) {
        try{
            return dmsInterturnManager.dispatchToExpress(siteCode, vendorId, waybillSign);
        }catch (Exception e){
            log.error("C网转B网校验异常，siteCode：{},vendorId：{},waybillSign:{}", siteCode, vendorId, waybillSign, e);
            InvokeResult<Boolean> errorResult = new InvokeResult<Boolean>();
            errorResult.setCode(JdResponse.CODE_INTERNAL_ERROR);
            errorResult.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            return errorResult;
        }
    }

    /**
     * 加盟商运单是否已交接
     * <p>
     *
     * @param waybillCode 运单号
     * @return
     */
    @Override
    public BaseEntity<Boolean> allianceBusiDelivered(String waybillCode) {
        BaseEntity<Boolean> result = new BaseEntity<Boolean>(BaseEntity.CODE_SUCCESS,BaseEntity.MESSAGE_SUCCESS);
        try{
            result.setData(allianceBusiDeliveryDetailService.checkExist(waybillCode));
        }catch (Exception e){
            log.error("加盟商交接交接检查异常,入参:{}",  waybillCode,e);
            result.setData(false);
            result.setCode(BaseEntity.CODE_SERVICE_ERROR);
            result.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
        }
        return result;

    }

    @Override
    public InvokeResult<Boolean> isTreatedAbnormal(String waybillCode, Integer siteCode) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.success();
        result.setData(Boolean.FALSE);
        try {
            AbnormalWayBill abnormalWayBill = abnormalWayBillService.getAbnormalWayBillByWayBillCode(waybillCode, siteCode);
            if(abnormalWayBill != null){
                result.setData(Boolean.TRUE);
            }
        }catch (Exception e){
            log.error("查询站点：{}运单号：{}的异常记录失败!" ,siteCode ,waybillCode,e);
            result.setCode(BaseEntity.CODE_SERVICE_ERROR);
            result.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }
    @JProfiler(jKey = UMP_KEY_PREFIX + "getSortingNumberInBox", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	@Override
	public JdResult<Integer> getSortingNumberInBox(String boxCode,Integer createSiteCode) {
		JdResult<Integer> result = new JdResult<Integer>();
		result.setData(0);
		result.toSuccess();
		if(BusinessUtil.isBoxcode(boxCode)){
			Integer boxCreateSiteCode = createSiteCode;
			/**
			 * 箱号始发分拣为空，查询箱号获取始发分拣
			 */
			if(boxCreateSiteCode == null){
				Box box = boxService.findBoxByCode(boxCode);
				if(box == null){
					result.toFail("未查询到箱号信息！");
					return result;
				}else{
					boxCreateSiteCode = box.getCreateSiteCode();
				}
			}
			result.setData(sortingService.findBoxPack(boxCreateSiteCode, boxCode));
			return result;
		}else{
			result.toFail("传入的箱号编码无效！");
		}
		return result;
	}

    /**
     * 查询符合条件的功能开关配置
     * @param dto
     * @return
     */
    @JProfiler(jKey = UMP_KEY_PREFIX + "getFuncSwitchConfigs", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public InvokeResult<List<FuncSwitchConfigDto>> getFuncSwitchConfigs(FuncSwitchConfigDto dto) {
        InvokeResult<List<FuncSwitchConfigDto>> result = new InvokeResult<List<FuncSwitchConfigDto>>();
        if(dto == null || dto.getMenuCode() == null
                || !FuncSwitchConfigEnum.codeMap.containsKey(dto.getMenuCode())){
            result.parameterError("参数错误!");
            return result;
        }
        result.setData(funcSwitchConfigService.getFuncSwitchConfigs(dto));
        return result;
    }
}
