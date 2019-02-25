package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.DmsInterturnManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.service.UserService;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
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
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;

/**
 * @author dudong
 * @date 2016/1/5
 */
public class DmsInternalServiceImpl implements DmsInternalService {

    private static final Log logger = LogFactory.getLog(DmsInternalServiceImpl.class);

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

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getDatadict",mState = JProEnum.TP)
    public DatadictResponse getDatadict(Integer parentID, Integer nodeLevel, Integer typeGroup) {
        if (logger.isInfoEnabled()) {
            logger.info("getDatadict param " + "parentID" + parentID + "nodeLevel" + nodeLevel + "typeGroup" + typeGroup);
        }
        try {
            return baseResource.getOrignalBackBusIds(parentID, nodeLevel, typeGroup);
        } catch (Exception e) {
            logger.error("getDatadict error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getBox",mState = JProEnum.TP)
    public BoxResponse getBox(String boxCode){
        if (logger.isInfoEnabled()) {
            logger.info("getBox param " + boxCode);
        }
        try {
            return boxResource.get(boxCode);
        } catch (Exception e) {
            logger.error("getBox error", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getSiteByCode",mState = JProEnum.TP)
    public BaseResponse getSiteByCode(String code) {
        if (logger.isInfoEnabled()) {
            logger.info("getSiteByCode param " + code);
        }
        try {
            return baseResource.getSite(code);
        } catch (Exception e) {
            logger.error("getSiteByCode error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.addTask",mState = JProEnum.TP)
    public TaskResponse addTask(TaskRequest request) {
        if (logger.isInfoEnabled()) {
            logger.info("addTask param " + JsonHelper.toJson(request));
        }
        try {
            return taskResource.add(request);
        } catch (Exception e) {
            logger.error("addTask error ", e);
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
        logger.info("isConsumableConfirmed param " + waybillCode);
        try {
            return waybillConsumableRecordService.isConfirmed(waybillCode);
        } catch (Exception e) {
            logger.error("isConsumableConfirmed error:" + waybillCode, e);
            return null;
        }

    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getBelongSiteCode",mState = JProEnum.TP)
    public BaseResponse getBelongSiteCode(Integer code) {
        if(logger.isInfoEnabled()){
            logger.info("getBelongSiteCode param " + code);
        }
        try{
            return baseResource.getselfD(code);
        }catch (Exception e){
            logger.error("getBelongSiteCode error ", e);
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
        if(logger.isInfoEnabled()){
            logger.info("getThreePartnerBelongSiteCode param " + code);
        }
        try{
            return baseResource.getThreePartnerD(code);
        }catch (Exception e){
            logger.error("getThreePartnerBelongSiteCode error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getTargetDmsCenter",mState = JProEnum.TP)
    public BaseResponse getTargetDmsCenter(Integer startDmsCode, Integer siteCode) {
        if(logger.isInfoEnabled()){
          logger.info("getTargetDmsCenter param " + "startDmsCode" + startDmsCode + "siteCode" + siteCode);
        }
        try{
            return waybillResource.getTargetDmsCenter(startDmsCode, siteCode);
        }catch (Exception e){
            logger.error("getTargetDmsCenter error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getLastScheduleSite",mState = JProEnum.TP)
    public BaseResponse getLastScheduleSite(String packageCode) {
        if(logger.isInfoEnabled()){
            logger.info("getLastScheduleSite param " + packageCode);
        }
        try{
            return reassignWaybillResource.queryLastScheduleSite(packageCode);
        }catch (Exception e){
            logger.error("getLastScheduleSite error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getReverseReceive",mState = JProEnum.TP)
    public ReverseReceiveResponse getReverseReceive(String waybillCode) {
       if(logger.isInfoEnabled()){
           logger.info("getReverseReceive param " + waybillCode);
       }
       try{
           return auditResource.getReverseReceiveByCode(waybillCode);
       } catch (Exception e){
           logger.error("getReverseReceive error ", e);
           return null;
       }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getLossOrderProducts",mState = JProEnum.TP)
    public LossProductResponse getLossOrderProducts(Long orderId) {
        if(logger.isInfoEnabled()){
            logger.info("getLossOrderProducts param " + orderId);
        }
        try{
            return lossProductResource.getLossOrderProducts(orderId.toString());
        }catch (Exception e){
            logger.error("getLossOrderProducts error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getLossOrderProductsByWaybillCode",mState = JProEnum.TP)
    public LossProductResponse getLossOrderProductsByWaybillCode(String waybillCode) {
        if(logger.isInfoEnabled()){
            logger.info("getLossOrderProducts param " + waybillCode);
        }
        try{
            return lossProductResource.getLossOrderProducts(waybillCode);
        }catch (Exception e){
            logger.error("getLossOrderProducts error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getSwitchStatus",mState = JProEnum.TP)
    public SysConfigResponse getSwitchStatus(String conName) {
        if(logger.isInfoEnabled()){
            logger.info("getSwitchStatus param " + conName);
        }
        try{
            return baseResource.getSwitchStatus(conName);
        }catch (Exception e){
            logger.error("getSwitchStatus error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.createAutoSortingBox",mState = JProEnum.TP)
    public InvokeResult<AutoSortingBoxResult> createAutoSortingBox(BoxRequest request) {
        if(logger.isInfoEnabled()){
            logger.info("createAutoSortingBox param " + JsonHelper.toJson(request));
        }
        try{
            return boxResource.create(request);
        }catch (Exception e){
            logger.error("createAutoSortingBox error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getPreseparateSiteId",mState = JProEnum.TP)
    public InvokeResult<Integer> getPreseparateSiteId(String waybillCode) {
        if(logger.isInfoEnabled()){
            logger.info("getPreseparateSiteId param " + waybillCode);
        }
        try{
            return preseparateWaybillResource.getPreseparateSiteId(waybillCode);
        }catch (Exception e){
            logger.error("getPreseparateSiteId error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.getPopPrintByWaybillCode",mState = JProEnum.TP)
    public PopPrintResponse getPopPrintByWaybillCode(String waybillCode) {
        if(logger.isInfoEnabled()){
            logger.info("getPreseparateSiteId param " + waybillCode);
        }
        try{
            return popPrintResource.findByWaybillCode(waybillCode);
        }catch (Exception e){
            logger.error("getPreseparateSiteId error ", e);
            return null;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInternalServiceImpl.login", mState = JProEnum.TP)
    public BaseResponse login(String userName, String passwd) {
        if(logger.isInfoEnabled()){
            logger.info("login param " + userName);
        }
        try{
            LoginRequest request = new LoginRequest();
            request.setErpAccount(userName);
            request.setPassword(passwd);
            return userService.jsfLogin(request);
        }catch (Exception e){
            logger.error("getPreseparateSiteId error ", e);
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
            logger.error("isReverseOperationAllowed方法异常", e);
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
            logger.error(MessageFormat.format("C网转B网校验异常，siteCode：{0},vendorId：{1},waybillSign:{2}", siteCode, vendorId, waybillSign), e);
            InvokeResult<Boolean> errorResult = new InvokeResult<Boolean>();
            errorResult.setCode(JdResponse.CODE_INTERNAL_ERROR);
            errorResult.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            return errorResult;
        }
    }
}
