package com.jd.bluedragon.distribution.exception;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.SendCodeExceptionRequest;
import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import com.jd.bluedragon.distribution.api.response.SendCodeExceptionResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.exception.service.SendCodeExceptionHandlerService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/19
 */
@Controller
@RequestMapping("/exception")
public class SendCodeExceptionHandlerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendCodeExceptionHandlerController.class);

    @Autowired
    private SendCodeExceptionHandlerService sendCodeExceptionHandlerService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 打开批次异常单处理主页
     * @return
     */
    @Authorization
    @RequestMapping("/sendCodeHandler/index")
    public String sendCodeHandler() {
        return "exception/sendCodeExceptionHandlerIndex";
    }

    /**
     * 打开批次异常单处理明细页
     * @return
     */
    @Authorization
    @RequestMapping("/sendCodeHandler/detailPager")
    public String sendCodeHandlerDetailPager() {
        return "exception/sendCodeExceptionDetail";
    }

    /**
     * 通过单号（包裹号、运单号、批次号）获取上游发给自己的批次号
     * <exception>
     *     1.单号不属于包裹号、运单号、批次号
     *     2.批次不是上游发给自己的批次
     * </exception>
     * <attention>
     *     批次号分为分拣的批次和来自站点的批次两种
     * </attention>
     * @return
     */
    @Authorization
    @RequestMapping(value = "/sendCodeHandler/querySendCodeByBarCode", method = RequestMethod.POST)
    public InvokeResult<List<String>> querySendCodeByBarCode(SendCodeExceptionRequest request) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        result.success();

        ErpUserClient.ErpUser user = ErpUserClient.getCurrUser();
        if (user == null) {
            result.error("用户未登录");
            return result;
        }

        BaseStaffSiteOrgDto staffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(user.getUserCode());
        if (null == staffSiteOrgDto) {
            result.error("用户为维护基础资料信息");
            return result;
        }

        result.setData(sendCodeExceptionHandlerService
                .querySendCodesByBarCode(staffSiteOrgDto.getSiteCode(),request.getBarCode()));
        return result;
    }


    /**
     * 根据批次号查询批次的汇总信息
     * @param request
     * @return
     */
    @Authorization
    @RequestMapping(value = "/sendCodeHandler/summaryPackageNumBySendCodes", method = RequestMethod.POST)
    public InvokeResult<SendCodeExceptionResponse> summaryPackageNumBySendCodes(SendCodeExceptionRequest request) {
        InvokeResult<SendCodeExceptionResponse> result = new InvokeResult<>();
        result.success();

        result.setData(sendCodeExceptionHandlerService.summaryPackageBySendCodes(request));

        return result;
    }

    /**
     * 根据类型和批次号查询明细
     * @param request
     * @return
     */
    @Authorization
    @RequestMapping(value = "/sendCodeHandler/querySendCodeDetails", method = RequestMethod.POST)
    public PagerResult<List<SendBoxDetailResponse>> querySendCodeDetails(Pager<SendCodeExceptionRequest> request) {
        PagerResult<List<SendBoxDetailResponse>> result = new PagerResult<>();

//        result.setRows(sendCodeExceptionHandlerService.querySendCodeDetailByCondition(request.getData()).getSendCodeDetail());
        return result;
    }

}
