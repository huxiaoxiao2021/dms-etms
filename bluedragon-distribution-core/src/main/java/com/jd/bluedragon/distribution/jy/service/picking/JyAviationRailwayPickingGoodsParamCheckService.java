package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingGoodsReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/12/11 10:36
 * @Description
 */

@Service
public class JyAviationRailwayPickingGoodsParamCheckService {


    private boolean checkUser(User user, InvokeResult<String> invokeResult) {
        if(Objects.isNull(user) || StringUtils.isBlank(user.getUserErp())) {
            invokeResult.parameterError("操作人erp为空");
            return false;
        }
        return true;
    }
    private boolean checkCurrentOperate(CurrentOperate currentOperate, InvokeResult<String> invokeResult) {
        if(Objects.isNull(currentOperate) || Objects.isNull(currentOperate.getSiteCode())) {
            invokeResult.parameterError("操作场地编码为空");
            return false;
        }
        if(Objects.isNull(currentOperate.getOperateTime())) {
            invokeResult.parameterError("操作时间为空");
            return false;
        }
        return true;
    }


    /**
     * 提货扫描必填参数校验
     * @param param
     * @return
     */
    public boolean pickingGoodScanParamCheck(PickingGoodsReq param, InvokeResult<String> invokeResult) {
        if(!checkUser(param.getUser(), invokeResult)) {
            return false;
        }
        if(!checkCurrentOperate(param.getCurrentOperate(), invokeResult)) {
            return false;
        }
        if(StringUtils.isBlank(param.getBarCode())) {
            invokeResult.setMessage("扫描单据为空");
            return false;
        }
        if(!WaybillUtil.isPackageCodeExcludeDJ(param.getBarCode()) && !BusinessUtil.isBoxcode(param.getBarCode())) {
            invokeResult.setMessage("请扫描包裹号或箱号");
            return false;
        }
        if(!PickingGoodTaskTypeEnum.legalCheck(param.getTaskType())) {
            invokeResult.setMessage("任务类型不合法");
            return false;
        }
        //发货参数校验
        if(Boolean.TRUE.equals(param.getSendGoodFlag())) {
            if(StringUtils.isBlank(param.getNextSiteName()) || Objects.isNull(param.getNextSiteId())) {
                invokeResult.setMessage("发货流向场地信息不能为空");
                return false;
            }
            if(Boolean.TRUE.equals(param.getSendNextSiteSwitch())) {
                if(StringUtils.isBlank(param.getBeforeSwitchNextSiteName()) || Objects.isNull(param.getBeforeSwitchNextSiteId())) {
                    invokeResult.setMessage("切换流向之前的发货流向场地信息不能为空");
                    return false;
                }
            }
        }
        return true;
    }


}
