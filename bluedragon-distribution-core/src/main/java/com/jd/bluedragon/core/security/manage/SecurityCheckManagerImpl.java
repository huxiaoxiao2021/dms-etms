package com.jd.bluedragon.core.security.manage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.security.data.am.jsf.client.service.DataAmOrderBusinessService;
import com.jd.security.data.am.jsf.client.service.DataAmWaybillBusinessService;
import com.jd.security.data.am.jsf.client.vo.BusinessCode;
import com.jd.security.data.am.jsf.client.vo.Response;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 安全校验切片查询服务（安全校验）
 *
 * @see <a href="https://cf.jd.com/pages/viewpage.action?pageId=935562906"/>
 * @author hujiping
 * @date 2022/8/12 5:09 PM
 */
@Service("securityCheckManager")
public class SecurityCheckManagerImpl implements SecurityCheckManager {

    private static final Logger logger = LoggerFactory.getLogger(SecurityCheckManagerImpl.class);

    // 安全授权校验方式
    private static final String AM_CHECK_TYPE_PIN = "pin";
    private static final String AM_CHECK_TYPE_ERP = "erp";

    // 安全授权系统token @see <a href="http://data-am.jd.com/#/dashboard"/>
    @Value("${waybill.am.sys.token:'1ef1fa90-06e2-4aed-876b-cc26c97b8b87'}")
    private String waybillAMToken;

    @Autowired
    private DataAmWaybillBusinessService dataAmWaybillBusinessService;

    @Autowired
    private DataAmOrderBusinessService dataAmOrderBusinessService;

    @Override
    public InvokeResult<Boolean> verifyWaybillDetailPermissionByPin(String userPin, String waybillNo, String waybillAMToken) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SecurityCheckManager.verifyWaybillDetailPermissionByPin",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        InvokeResult<Boolean> result = new InvokeResult<>();
        try {
            Response response = dataAmWaybillBusinessService.verifyWaybillDetailPermission(userPin, waybillNo, waybillAMToken, AM_CHECK_TYPE_PIN);
            if(logger.isInfoEnabled()){
                logger.info("根据运单号:{},用户pin:{}查询安全切片查询服务结果:{}", waybillNo, userPin, response == null ? null : JsonHelper.toJson(response));
            }
            if(response == null || Objects.equals(response.getCode(), BusinessCode.SUCCESS.getCode())){
                return result;
            }
            result.customMessage(400, response.getMsg());
        }catch (Exception e){
            logger.error("根据运单号:{},用户pin:{}判断是否有访问权限异常!", waybillNo, userPin, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }

    @Override
    public InvokeResult<Boolean> verifyWaybillDetailPermissionByErp(String userErp, String waybillNo, String waybillAMToken) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SecurityCheckManager.verifyWaybillDetailPermissionByErp",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        InvokeResult<Boolean> result = new InvokeResult<>();
        try {
            Response response = dataAmWaybillBusinessService.verifyWaybillDetailPermission(userErp, waybillNo, waybillAMToken, AM_CHECK_TYPE_ERP);
            if(logger.isInfoEnabled()){
                logger.info("根据运单号:{},用户erp:{}查询安全切片查询服务结果:{}", waybillNo, userErp, response == null ? null : JsonHelper.toJson(response));
            }
            if(response == null || Objects.equals(response.getCode(), BusinessCode.SUCCESS.getCode())){
                return result;
            }
            result.customMessage(400, response.getMsg());
        }catch (Exception e){
            logger.error("根据运单号:{},用户ERP:{}判断是否有访问权限异常!", waybillNo, userErp, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }

    @Override
    public InvokeResult<Boolean> verifyOrderDetailPermissionByPin(String userPin, String orderNo, String waybillAMToken) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SecurityCheckManager.verifyOrderDetailPermissionByPin",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        InvokeResult<Boolean> result = new InvokeResult<>();
        try {
            Response response = dataAmOrderBusinessService.verifyOrderDetailPermission(userPin, orderNo, waybillAMToken, AM_CHECK_TYPE_PIN);
            if(logger.isInfoEnabled()){
                logger.info("根据订单号:{},用户pin:{}查询安全切片查询服务结果:{}", userPin, orderNo, response == null ? null : JsonHelper.toJson(response));
            }
            if(response == null || Objects.equals(response.getCode(), BusinessCode.SUCCESS.getCode())){
                return result;
            }
            result.customMessage(400, response.getMsg());
        }catch (Exception e){
            logger.error("根据订单号:{},用户ERP:{}判断是否有访问权限异常!", orderNo, userPin, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }

    @Override
    public InvokeResult<Boolean> verifyOrderDetailPermissionByErp(String userErp, String orderNo, String waybillAMToken) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SecurityCheckManager.verifyOrderDetailPermissionByErp",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        InvokeResult<Boolean> result = new InvokeResult<>();
        try {
            Response response = dataAmOrderBusinessService.verifyOrderDetailPermission(userErp, orderNo, waybillAMToken, AM_CHECK_TYPE_ERP);
            if(logger.isInfoEnabled()){
                logger.info("根据订单号:{},用户erp:{}查询安全切片查询服务结果:{}", orderNo, userErp, response == null ? null : JsonHelper.toJson(response));
            }
            if(response == null || Objects.equals(response.getCode(), BusinessCode.SUCCESS.getCode())){
                return result;
            }
            result.customMessage(400, response.getMsg());
        }catch (Exception e){
            logger.error("根据订单号:{},用户ERP:{}判断是否有访问权限异常!", orderNo, userErp, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }
}
