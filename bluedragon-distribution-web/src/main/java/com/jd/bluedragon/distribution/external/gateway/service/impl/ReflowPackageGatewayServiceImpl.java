package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.recyclematerial.request.ReflowPackageRequest;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.WastePackageJSFRequest;
import com.jd.bluedragon.distribution.api.request.WastePackageRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.reflowPackage.doman.ReflowPackage;
import com.jd.bluedragon.distribution.reflowPackage.service.ReflowPackageService;
import com.jd.bluedragon.distribution.rest.wastePackageStorage.WastePackageResource;
import com.jd.bluedragon.external.gateway.service.ReflowPackageGatewayService;
import com.jd.bluedragon.utils.DateHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
public class ReflowPackageGatewayServiceImpl implements ReflowPackageGatewayService {

    @Autowired
    private ReflowPackageService reflowPackageService;

    @Autowired
    @Qualifier("wastePackageResource")
    private WastePackageResource wastePackageResource;

    /**
     * 包裹回流扫描提交
     * @param request
     * @return
     */
    @Override
    public JdCResponse<Boolean> reflowPackageSubmit(ReflowPackageRequest request){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);

        try{
            // 验证参数
            JdCResponse<Boolean> checkResult = this.checkParamForReflowPackageSubmit(request);
            if (checkResult.isFail()){
                result.init(JdCResponse.CODE_FAIL, checkResult.getMessage());
                return result;
            }

            ReflowPackage req=new ReflowPackage();
            req.setPackageCode(request.getPackageCode());
            req.setOperatorCode(request.getUser().getUserCode());
            req.setOperatorErp(request.getUser().getUserErp());
            req.setOperatorName(request.getUser().getUserName());
            req.setSiteCode(request.getCurrentOperate().getSiteCode());
            req.setSiteName(request.getCurrentOperate().getSiteName());
            req.setCreateTime(request.getCurrentOperate().getOperateTime());
            req.setUpdateTime(request.getCurrentOperate().getOperateTime());
            req.setIsDelete(request.getIsDelete());

            result=reflowPackageService.reflowPackageSubmit(req);
        }catch (Exception e) {
            log.error("CollectionBagExceptionReportServiceImpl.reflowPackageSubmit exception {}", e.getMessage(), e);
            result.toFail("包裹回流扫描提交异常");
        }
        return result;
    }

    /**
     * 弃件暂存
     * @param request
     * @return
     */
    @Override
    public JdCResponse<Boolean> wastepackagestorage(WastePackageJSFRequest request){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        try{
            // 验证参数
            JdCResponse<Boolean> checkResult = this.checkParamForwastepackagestorage(request);
            if (checkResult.isFail()){
                result.init(JdCResponse.CODE_FAIL, checkResult.getMessage());
                return result;
            }

            WastePackageRequest req=new WastePackageRequest();
            req.setWaybillCode(request.getWaybillCode());
            req.setStatus(request.getStatus());
            req.setUserCode(request.getUser().getUserCode());
            req.setOperatorERP(request.getUser().getUserErp());
            req.setUserName(request.getUser().getUserName());
            req.setSiteCode(request.getCurrentOperate().getSiteCode());
            req.setSiteName(request.getCurrentOperate().getSiteName());
            req.setOperateTime(DateHelper.formatDateTime(request.getCurrentOperate().getOperateTime()));

            InvokeResult<Boolean> res=wastePackageResource.wastepackagestorage(req);
            if (res.getCode()!=InvokeResult.RESULT_SUCCESS_CODE){
                result.setCode(res.getCode());
            }

            result.setMessage(result.getMessage());
        }catch (Exception e) {
            log.error("ReflowPackageGatewayServiceImpl.wastepackagestorage {}", e.getMessage(), e);
            result.toFail("弃件暂存异常");
        }
        return result;
    }

    private JdCResponse<Boolean> checkParamForReflowPackageSubmit(ReflowPackageRequest request){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        if(StringUtils.isEmpty(request.getPackageCode())){
            result.toFail("参数错误，包裹号不能为空");
            return result;
        }

        JdCResponse<Boolean> checkParamForUserResult = this.checkParamForUser(request.getUser());
        if(checkParamForUserResult.isFail()){
            return checkParamForUserResult;
        }
        JdCResponse<Boolean> checkParamForCurrentOperateResult = this.checkParamForCurrentOperate(request.getCurrentOperate());
        if(checkParamForCurrentOperateResult.isFail()){
            return checkParamForCurrentOperateResult;
        }

        return result;
    }

    private JdCResponse<Boolean> checkParamForwastepackagestorage(WastePackageJSFRequest request){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        if(StringUtils.isEmpty(request.getWaybillCode())){
            result.toFail("参数错误，运单号不能为空");
            return result;
        }

        JdCResponse<Boolean> checkParamForUserResult = this.checkParamForUser(request.getUser());
        if(checkParamForUserResult.isFail()){
            return checkParamForUserResult;
        }
        JdCResponse<Boolean> checkParamForCurrentOperateResult = this.checkParamForCurrentOperate(request.getCurrentOperate());
        if(checkParamForCurrentOperateResult.isFail()){
            return checkParamForCurrentOperateResult;
        }

        return result;
    }

    private JdCResponse<Boolean> checkParamForUser(User user){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        if(user == null){
            result.toFail("参数错误，用户信息不能为空");
            return result;
        }
        if(StringUtils.isEmpty(user.getUserErp())){
            result.toFail("参数错误，操作人ERP不能为空");
            return result;
        }
        if(StringUtils.isEmpty(user.getUserName())){
            result.toFail("参数错误，操作人姓名不能为空");
            return result;
        }
        return result;
    }

    private JdCResponse<Boolean> checkParamForCurrentOperate(CurrentOperate currentOperate){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        if(currentOperate == null){
            result.toFail("参数错误，场地信息不能为空");
            return result;
        }
        if(currentOperate.getSiteCode() < 0){
            result.toFail("参数错误，场地ID不能为空");
            return result;
        }
        if(StringUtils.isEmpty(currentOperate.getSiteName())){
            result.toFail("参数错误，场地名称不能为空");
            return result;
        }
        return result;
    }
}
