package com.jd.bluedragon.distribution.external.gateway.blockcar;

import com.jd.bluedragon.common.dto.blockcar.request.CancelPreBlockCarRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.newseal.domain.CancelPreSealVehicleRequest;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.bluedragon.external.gateway.blockcar.CancelPreBlockCarGatewayExternalService;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 取消封车
 * @author zhangzhongkai8
 * @date 2020/11/18  20:24
 */
public class CancelPreBlockCarGatewayExternalServiceImpl implements CancelPreBlockCarGatewayExternalService {

    @Autowired
    private PreSealVehicleService preSealVehicleService;

    /**
     * 取消预封车
     * @param request
     * @return
     */
    @Override
    public JdResponse<Boolean> cancelPreBlockCar(CancelPreBlockCarRequest request) {
        JdResponse<Boolean> result = new JdResponse<Boolean>();
        result.toSucceed("取消预封车成功！");
        //参数校验
        String checkParam = this.checkParam(request);
        if (StringUtils.isNotEmpty(checkParam)){
            result.toFail(checkParam);
            return result;
        }
        //取消封车逻辑
        CancelPreSealVehicleRequest param = this.prepareParam(request);
        JdResult<Boolean> serviceResult = preSealVehicleService.cancelPreSeal(param);
        if (!JdResult.CODE_SUC.equals(serviceResult.getCode())){
            result.toFail(serviceResult.getMessage());
        }
        return result;
    }

    /**
     * 参数校验
     * @param request
     * @return
     */
    private String checkParam(CancelPreBlockCarRequest request){
        StringBuilder result = new StringBuilder();
        if (StringUtils.isEmpty(request.getCarNum())){
            result.append("请输入车牌号！");
            return result.toString();
        }
        if (null == request.getUser()){
            result.append("没有登录人信息！");
            return result.toString();
        }
        if (null == request.getCurrentOperate()
                || StringUtils.isEmpty(request.getCurrentOperate().getSiteName())
                || ObjectUtils.NULL.equals(request.getCurrentOperate().getSiteCode())){
            result.append("没有站点信息！");
            return result.toString();
        }
        return result.toString();
    }

    /**
     * 准备参数
     * @param request
     * @return
     */
    private CancelPreSealVehicleRequest prepareParam(CancelPreBlockCarRequest request){
        CancelPreSealVehicleRequest param = new CancelPreSealVehicleRequest();
        param.setOperateUserErp(request.getUser().getUserErp());
        param.setOperateUserName(request.getUser().getUserName());
        param.setSiteCode(request.getCurrentOperate().getSiteCode());
        param.setSiteName(request.getCurrentOperate().getSiteName());
        param.setVehicleNumber(request.getCarNum());
        return param;
    }
}
