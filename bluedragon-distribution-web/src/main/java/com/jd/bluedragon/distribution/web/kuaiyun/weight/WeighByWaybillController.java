package com.jd.bluedragon.distribution.web.kuaiyun.weight;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByWaybillService;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.impl.WeighByWaybillServiceImpl;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 运单称重
 * B2B的称重量方简化功能，支持按运单/包裹号维度录入总重量（KG）和总体积（立方米）
 *
 * @author luyue  2017-12
 */
@Controller
@RequestMapping("/b2b/express/weight")
public class WeighByWaybillController
{
    private static final Log logger = LogFactory.getLog(WeighByWaybillController.class);

    private final Double MAX_WEIGHT = 999999.99;
    private final Double MAX_VOLUME = 999.99;

    /*10：表示经调取运单接口WaybillQueryApi，已查到该运单，可直接入库*/
    /*20：表示经调取运单接口WaybillQueryApi，未查到该运单，需经处理*/
    private final Integer VALID_EXISTS_STATUS_CODE = 10;
    private final Integer VALID_NOT_EXISTS_STATUS_CODE = 20;

    @Autowired
    WeighByWaybillService service;

    @Autowired
    BaseMajorManager baseMajorManager;

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/index")
    public String getIndexPage()
    {
        return "/b2bExpress/weight/weighByWaybill";
    }

    /**
     * 录入运单称重量方数据
     *
     * @param vo WaybillWeightVO
     * @return InvokeResult<Boolean> 插入结果
     */
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/insertWaybillWeight")
    @ResponseBody
    public InvokeResult<Boolean> insertWaybillWeight(WaybillWeightVO vo)
    {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();

        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setData(true);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);

        /*参数校验*/
        boolean isValid = this.validateParam(vo);
        if (!isValid)
        {
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            result.setData(false);
            return result;
        }

        /*插入记录*/
        try
        {
            try
            {
                ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
                if (erpUser != null)
                {
                    vo.setOperatorId(erpUser.getUserId());
                    vo.setOperatorName(erpUser.getUserName());

                    BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
                    if (dto != null)
                    {
                        vo.setOperatorSiteCode(dto.getSiteCode());
                        vo.setOperatorSiteName(dto.getSiteName());
                    }
                }
            } catch (Exception e)
            {
                logger.error("运单称重：获取操作用户Erp账号失败");
            }

            service.insertWaybillWeightEntry(vo);
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion)
        {
            WeightByWaybillExceptionTypeEnum exceptionType = weighByWaybillExcpetion.exceptionType;
            if (exceptionType.shouldBeThrowToTop)
            {
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(exceptionType.toString());
                result.setData(false);
                throw weighByWaybillExcpetion;
            } else
            {
                if (weighByWaybillExcpetion.exceptionType.equals(WeightByWaybillExceptionTypeEnum.MQServiceNotAvailableException))
                {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    result.setMessage("toTask");
                }else
                {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    result.setMessage(exceptionType.toString());
                    result.setData(false);
                }
            }
        } finally
        {
            return result;
        }

    }

    /**
     * 验证运单存在性
     *
     * @param codeStr 运单号/运单下包裹号
     * @return 能否从运单系统查到对应运单
     * @throws WeighByWaybillExcpetion
     */
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/verifyWaybillReality")
    @ResponseBody
    public InvokeResult<Boolean> verifyWaybillReality(@RequestParam(value = "codeStr") String codeStr)
    {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setData(true);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);

        try
        {
            /*1 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号*/
            String waybillCode = service.convertToWaybillCode(codeStr);
            /*2 对运单进行存在校验*/
            boolean isExist = service.validateWaybillCodeReality(waybillCode);

            result.setData(isExist);
            if (isExist)
            {
                result.setMessage("存在该运单相关信息，可以录入！");
            }
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion)
        {
            result.setData(false);

            WeightByWaybillExceptionTypeEnum exceptionType = weighByWaybillExcpetion.exceptionType;
            if (exceptionType.shouldBeThrowToTop)
            {
                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillServiceNotAvailableException))
                {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    result.setData(false);
                    result.setMessage(exceptionType.exceptionMessage);
                    logger.error("运单称重：" + exceptionType.exceptionMessage);
                    throw weighByWaybillExcpetion;
                }
            } else
            {
                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.UnknownCodeException))
                {
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setMessage(exceptionType.exceptionMessage);
                }

                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillNotFindException))
                {
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage(exceptionType.exceptionMessage);
                }

            }
        } finally
        {
            return result;
        }

    }

    /**
     * 将包裹号或运单号统一转为运单号
     *
     * @param codeStr 包裹号或/运单号
     * @return InvokeResult<String> 运单号
     */
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/convertCodeToWaybillCode")
    @ResponseBody
    public InvokeResult<String> convertCodeToWaybillCode(@RequestParam(value = "codeStr") String codeStr)
    {

        InvokeResult<String> result = new InvokeResult<String>();

        try
        {
            String waybillCode = service.convertToWaybillCode(codeStr);
            result.setData(waybillCode);
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion)
        {
            if (weighByWaybillExcpetion.exceptionType.equals(WeightByWaybillExceptionTypeEnum.UnknownCodeException))
            {
                result.setData(null);
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setMessage("运单号/包裹号错误，不符合运单号/包裹号格式!");
            }
        } finally
        {
            return result;
        }
    }

    /**
     * 校验传入称重量方参数
     *
     * @param vo 传入重量体积参数对象
     * @return boolean 校验结果
     */
    private boolean validateParam(WaybillWeightVO vo)
    {
        Integer status = vo.getStatus();
        Double weight = vo.getWeight();
        Double volume = vo.getVolume();

        if (!(status.equals(VALID_EXISTS_STATUS_CODE) || status.equals(VALID_NOT_EXISTS_STATUS_CODE)))
        {
            return false;
        }

        if (weight.compareTo(this.MAX_WEIGHT) != -1)
        {
            return false;
        }

        if (volume.compareTo(this.MAX_VOLUME) != -1)
        {
            return false;
        }

        return true;
    }


}
