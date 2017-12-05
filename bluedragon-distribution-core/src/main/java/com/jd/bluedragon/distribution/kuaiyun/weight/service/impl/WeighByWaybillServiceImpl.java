package com.jd.bluedragon.distribution.kuaiyun.weight.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightDTO;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.exception.JMQException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WeighByWaybillServiceImpl
{
    private static final Log logger = LogFactory.getLog(WeighByWaybillServiceImpl.class);
    private final String WAYBILL_REGEX = "(^[1-9]{1}[0-9]{8,29}$)|(^V[A-Z0-9]{1}[0-9]{11,28}$)|(^(W|T|F|[Q|q]){1}([A-Za-z0-9]{9,29})$)";
    private final String PACKAGE_REGEX = "^([A-Za-z0-9]{8,})(-(?=[0-9]{1,4}-)|N(?=[0-9]{1,4}S))([1-9]{1}[0-9]{0,3})(-(?=[0-9]{1,4}-)|S(?=[0-9]{1,4}H))([1-9]{1}[0-9]{0,3})([-|H][A-Za-z0-9]*)$";


    /*运单接口 用于运单校验*/
    @Autowired
    WaybillQueryApi waybillQueryApi;

    /* MQ消息生产者： topic:dms_waybill_weight*/
//    @Autowired
//    @Qualifier("weighByWaybillProducer")
    private DefaultJMQProducer weighByWaybillProducer;


    public void insertWaybillWeightEntry(WaybillWeightVO vo) throws Exception
    {
        String codeStr = vo.getCodeStr();
        /*1 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号*/
        String waybillCode = this.convertToWaybillCode(codeStr);

        Double weight = this.convertWeightUnitToRequired(vo.getWeight());
        Double volume = this.convertVolumeUnitToRequired(vo.getVolume());
        WaybillWeightDTO waybillWeightDTO = new WaybillWeightDTO(waybillCode,weight,volume,vo.getStatus());

        if (vo.getStatus().equals(10))
        {
            try
            {
                this.validWaybillProcess(waybillWeightDTO);
            } catch (Exception e)
            {
                throw e;
            }
        } else if (vo.getStatus().equals(20))
        {
            try
            {
                this.invalidWaybillProcess(waybillWeightDTO);
            } catch (Exception e)
            {
                throw e;
            }

        } else
        {
            throw new WeighByWaybillExcpetion("非法调用",WeightByWaybillExceptionTypeEnum.InvalidMethodInvokeException);
        }

    }

//  =====================================================

    /*1 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号*/
    public String convertToWaybillCode(String codeStr) throws WeighByWaybillExcpetion
    {

        String waybillCode = null;
        if (this.isValidPackageCode(codeStr))
        {
            waybillCode = BusinessHelper.getWaybillCodeByPackageBarcode(codeStr);
        } else if (this.isValidWaybillCode(codeStr))
        {
            waybillCode = codeStr;
        } else
        {
            logger.error("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则");

            throw new WeighByWaybillExcpetion("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则",
                    WeightByWaybillExceptionTypeEnum.UnknownCodeException);
        }

        if (null == waybillCode)
        {
            logger.error("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则");

            throw new WeighByWaybillExcpetion("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则",
                    WeightByWaybillExceptionTypeEnum.UnknownCodeException);
        }

        return waybillCode;
    }

//  =====================================================

    /*2 对运单进行存在校验*/
    public boolean validateWaybillCodeReality(String waybillCode) throws WeighByWaybillExcpetion
    {

        BaseEntity<Waybill> waybillBaseEntity = null;
        try
        {
            waybillBaseEntity = waybillQueryApi.getWaybillByWaybillCode(waybillCode);
        } catch (Exception e)
        {
            WeighByWaybillExcpetion excpetion = new WeighByWaybillExcpetion("调取运单系统失败，运单查询接口不可用");
            excpetion.exceptionType = WeightByWaybillExceptionTypeEnum.WaybillServiceNotAvailableException;

            throw excpetion;
        }

//        int resultCode = waybillBaseEntity.getResultCode();
        Waybill waybill = waybillBaseEntity.getData();

        if (waybill == null)
        {
            WeighByWaybillExcpetion excpetion = new WeighByWaybillExcpetion("调取运单系统成功，但未查询到运单数据");
            excpetion.exceptionType = WeightByWaybillExceptionTypeEnum.WaybillNotFindException;

            throw excpetion;
        }

        return true;
    }

    //  =====================================================
    /*3-1 校验成功 将称重量方信息写入 MQ 返回成功消息*/
    public boolean validWaybillProcess(WaybillWeightDTO dto) throws Exception
    {
        this.sendMessageToMq(dto);
        return true;
    }

    /*3-2 校验失败 修改标志位*/
    public boolean invalidWaybillProcess(WaybillWeightDTO dto) throws Exception
    {
        this.sendMessageToMq(dto);

        return true;
    }

    /*4 发送mq*/
    private void sendMessageToMq(WaybillWeightDTO vo) throws WeighByWaybillExcpetion
    {
        try
        {
            weighByWaybillProducer.send(vo.getWaybillCode(), JsonHelper.toJson(vo));
        } catch (JMQException e)
        {
            WeighByWaybillExcpetion excpetion = new WeighByWaybillExcpetion("调取运单称重量方对象转换JSON失败，请检查原因");
            excpetion.exceptionType = WeightByWaybillExceptionTypeEnum.WaybillWeightVOConvertExcetion;

            throw excpetion;
        } catch (Exception e)
        {
            WeighByWaybillExcpetion excpetion = new WeighByWaybillExcpetion("运单称重信息MQ发送失败，请检查原因");
            excpetion.exceptionType = WeightByWaybillExceptionTypeEnum.MQServiceNotAvailableException;

            throw excpetion;
        }


    }

    public Double convertWeightUnitToRequired(Double weight)
    {

        return 0.0;
    }

    /*转换单位 重量单位 传入值为kg 标准为kg 不需转换 | 体积单位 传入值为立方米 标准为立方厘米*/
    public Double convertVolumeUnitToRequired(Double cbm)
    {

        return 0.0;
    }

    private boolean isValidWaybillCode(String waybillCode)
    {
        Pattern pattern = Pattern.compile(WAYBILL_REGEX);
        Matcher matcher = pattern.matcher(waybillCode);
        boolean matches = matcher.matches();

        return matches;
    }

    private boolean isValidPackageCode(String packageCode)
    {
        Pattern pattern = Pattern.compile(PACKAGE_REGEX);
        Matcher matcher = pattern.matcher(packageCode);
        boolean matches = matcher.matches();

        return matches;
    }
}
