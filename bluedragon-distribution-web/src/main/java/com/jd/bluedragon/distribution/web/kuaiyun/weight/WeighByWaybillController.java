package com.jd.bluedragon.distribution.web.kuaiyun.weight;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.impl.WeighByWaybillServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/b2b/express/weight")
public class WeighByWaybillController
{
    private final Double MAX_WEIGHT = 999999.99;
    private final Double MAX_VOLUME = 999.99;

    @Autowired
    WeighByWaybillServiceImpl service;

    @RequestMapping("/index")
    public String getIndexPage()
    {

        return "/b2bExpress/weight/weighByWaybill";
    }

    /**
     * 录入运单称重量方数据
     * @param vo
     * @return
     */
    @RequestMapping("/insertWaybillWeight")
    @ResponseBody
    public InvokeResult<Boolean> insertWaybillWeight(WaybillWeightVO vo)
    {
        System.out.println("========================================================");
        System.out.println(vo);
        System.out.println("========================================================");

        InvokeResult<Boolean> result = new InvokeResult<Boolean>();


        /*参数校验*/
        boolean isValid = this.validateParam(vo);
        if(!isValid)
        {
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            result.setData(false);
            return result;
        }

        /*插入记录*/
        try
        {
            service.insertWaybillWeightEntry(vo);
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion)
        {
            weighByWaybillExcpetion.printStackTrace();
        } catch (Exception e)
        {

        }
        finally
        {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setData(false);
            result.setMessage("");
        }


        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setData(true);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);

        return new InvokeResult<Boolean>();
    }

    /**
     * 验证运单存在性
     * @param codeStr
     * @return
     * @throws WeighByWaybillExcpetion
     */
    @RequestMapping("/verifyWaybillReality")
    @ResponseBody
    public InvokeResult<Boolean> verifyWaybillReality(@RequestParam(value = "codeStr") String codeStr) throws WeighByWaybillExcpetion
    {
        System.out.println(codeStr);

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
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(weighByWaybillExcpetion.exceptionMessage);
                throw weighByWaybillExcpetion;
            } else
            {
                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.UnknownCodeException))
                {
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setMessage(weighByWaybillExcpetion.exceptionMessage);
                }

                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillNotFindException))
                {
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage(weighByWaybillExcpetion.exceptionMessage);
                }
            }
        }
        finally
        {
            return result;
        }

    }

    /**
     * 将包裹号或运单号统一转为运单号
     * @param codeStr
     * @return
     */
    @RequestMapping("/convertCodeToWaybillCode")
    @ResponseBody
    public InvokeResult<String> convertCodeToWaybillCode(@RequestParam(value = "codeStr") String codeStr){

        InvokeResult<String> result = new InvokeResult<String>();

        String waybillCode = null;
        try
        {
            waybillCode = service.convertToWaybillCode(codeStr);
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion)
        {
            if (weighByWaybillExcpetion.exceptionType.equals(WeightByWaybillExceptionTypeEnum.UnknownCodeException))
            {
                result.setData(null);
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setMessage("运单号/包裹号错误，不符合运单号/包裹号格式!");
            }
        }

        result.setData(waybillCode);

        return result;
    }

    /**
     * 校验传入称重量方参数
     * @param vo 传入重量体积参数对象
     * @return boolean 校验结果
     */
    private boolean validateParam(WaybillWeightVO vo)
    {
        Integer status = vo.getStatus();
        Double weight = vo.getWeight();
        Double volume = vo.getVolume();

        if(  !(status.equals(10) || status.equals(20))  )
        {
            return false;
        }

        if(weight > this.MAX_WEIGHT)
        {
            return false;
        }

        if(volume > this.MAX_VOLUME)
        {
            return false;
        }

        return true;
    }


}
