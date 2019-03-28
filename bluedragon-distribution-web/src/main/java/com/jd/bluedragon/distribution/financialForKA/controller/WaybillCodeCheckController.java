package com.jd.bluedragon.distribution.financialForKA.controller;

import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.financialForKA.WaybillCodeCheckCondition;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: WaybillCodeCheckController
 * @Description: 金融客户运单号对比校验-Controller
 * @author: hujiping
 * @date: 2019/3/7 15:16
 */
@Controller
@RequestMapping("waybillCodeCheckForKA")
public class WaybillCodeCheckController extends DmsBaseController {

    /**
     * 返回主页面
     * @return
     */
    @RequestMapping("/toIndex")
    public String toIndex(){
        return "/financialForKA/waybillCodeCheck";
    }

    /**
     * 条码校验
     * @param condition
     * @return
     */
    @RequestMapping("/check")
    @ResponseBody
    public InvokeResult waybillCodeCheck(@RequestBody WaybillCodeCheckCondition condition){
        InvokeResult result = new InvokeResult();
        if(StringUtils.isEmpty(condition.getBarCodeOfOne()) || StringUtils.isEmpty(condition.getBarCodeOfTwo())){
            result.setCode(InvokeResult.RESULT_NULL_CODE);
            result.setMessage("条码为空，请重新输入!");
            return result;
        }
        if((!WaybillUtil.isWaybillCode(condition.getBarCodeOfOne()) && !WaybillUtil.isPackageCode(condition.getBarCodeOfOne())) ||
                (!WaybillUtil.isWaybillCode(condition.getBarCodeOfTwo()) && !WaybillUtil.isPackageCode(condition.getBarCodeOfTwo())) ){
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("输入的条码不符合规则!");
            return result;
        }
        String waybillCodeOfOne = WaybillUtil.getWaybillCode(condition.getBarCodeOfOne());
        String waybillCodeOfTwo = WaybillUtil.getWaybillCode(condition.getBarCodeOfTwo());
        if(!waybillCodeOfOne.equals(waybillCodeOfTwo)){
            result.setCode(600);
            result.setMessage("匹配失败，号码不一致！请检查面单是否贴错");
            return result;
        }
        result.setMessage("匹配成功，号码一致!");
        return result;
    }
}
