package com.jd.bluedragon.distribution.kuaiyun.weight.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightDTO;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByWaybillService;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.exception.JMQException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 运单称重
 * B2B的称重量方简化功能，支持按运单/包裹号维度录入总重量（KG）和总体积（立方米）
 *
 * @author luyue  2017-12
 */
@Service
public class WeighByWaybillServiceImpl implements WeighByWaybillService
{
    private static final Log logger = LogFactory.getLog(WeighByWaybillServiceImpl.class);
    private final String WAYBILL_REGEX = "(^[1-9]{1}[0-9]{8,29}$)|(^V[A-Z0-9]{1}[0-9]{11,28}$)|(^(W|T|F|[Q|q]){1}([A-Za-z0-9]{9,29})$)";
    private final String PACKAGE_REGEX = "^([A-Za-z0-9]{8,})(-(?=[0-9]{1,4}-)|N(?=[0-9]{1,4}S))([1-9]{1}[0-9]{0,3})(-(?=[0-9]{1,4}-)|S(?=[0-9]{1,4}H))([1-9]{1}[0-9]{0,3})([-|H][A-Za-z0-9]*)$";

    private final Integer VALID_EXISTS_STATUS_CODE = 10;
    private final Integer VALID_NOT_EXISTS_STATUS_CODE = 20;

    private final String CASSANDRA_SIGN = "WaybillWeight_";


    /*运单接口 用于运单校验*/
    @Autowired
    WaybillQueryApi waybillQueryApi;

    /*用于记录操作日志*/
    @Autowired
    private GoddessService goddessService;

    /* MQ消息生产者： topic:dms_waybill_weight*/
    @Autowired
    @Qualifier("weighByWaybillProducer")
    private DefaultJMQProducer weighByWaybillProducer;


    /**
     * 运单称重信息录入入口 最终发送mq消息给运单部门
     *
     * @param vo 运单称重参数
     * @throws WeighByWaybillExcpetion 运单称重异常
     */
    public void insertWaybillWeightEntry(WaybillWeightVO vo) throws WeighByWaybillExcpetion
    {
        String codeStr = vo.getCodeStr();
        /*1 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号*/
        String waybillCode = this.convertToWaybillCode(codeStr);

        /*将重量体积单位转为 运单部门要求的单位*/
        Double weight = this.convertWeightUnitToRequired(vo.getWeight());
        Double volume = this.convertVolumeUnitToRequired(vo.getVolume());
        WaybillWeightDTO waybillWeightDTO = new WaybillWeightDTO();
        BeanUtils.copyProperties(vo, waybillWeightDTO);
        waybillWeightDTO.setWaybillCode(waybillCode);
        waybillWeightDTO.setWeight(weight);
        waybillWeightDTO.setVolume(volume);
        waybillWeightDTO.setOperateTimeMillis(System.currentTimeMillis());

        /*经与运单方讨论,现阶段定为:无论是否存在运单,都将信息推送给运单,运单在后续流程补数据。
            为防止以后流程变动 将验证存在运单与不存在的对应处理方法拆开*/
        if (vo.getStatus().equals(VALID_EXISTS_STATUS_CODE))
        {
            try
            {
                this.validWaybillProcess(waybillWeightDTO);
            } catch (WeighByWaybillExcpetion e)
            {
                throw e;
            }
        } else if (vo.getStatus().equals(VALID_NOT_EXISTS_STATUS_CODE))
        {
            try
            {
                this.invalidWaybillProcess(waybillWeightDTO);
            } catch (WeighByWaybillExcpetion e)
            {
                throw e;
            }

        } else
        {
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.InvalidMethodInvokeException);
        }

    }

    /**
     * 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号
     *
     * @param codeStr 运单号/包裹号
     * @return 运单号
     * @throws WeighByWaybillExcpetion 运单号/包裹号格式错误异常 UnknownCodeException
     */
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
            logger.warn("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则");

            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.UnknownCodeException);
        }

        if (null == waybillCode)
        {
            logger.warn("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则");

            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.UnknownCodeException);
        }

        return waybillCode;
    }

    /**
     * 对运单进行存在校验
     *
     * @param waybillCode 运单号
     * @return 是否存在运单
     * @throws WeighByWaybillExcpetion 运单称重异常 WaybillServiceNotAvailableException WaybillNotFindException
     */
    public boolean validateWaybillCodeReality(String waybillCode) throws WeighByWaybillExcpetion
    {
        BaseEntity<Waybill> waybillBaseEntity = null;

        try
        {
            waybillBaseEntity = waybillQueryApi.getWaybillByWaybillCode(waybillCode);
        } catch (Exception e)
        {
            logger.error(e);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillServiceNotAvailableException);
        }

        int resultCode = waybillBaseEntity.getResultCode();
        Waybill waybill = waybillBaseEntity.getData();

        if (waybill == null)
        {
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.WaybillNotFindException);
        }

        return true;
    }

    /**
     * 当运单经校验存在时的流程
     *
     * @param dto 待传输消息对象
     * @throws WeighByWaybillExcpetion MQServiceNotAvailableException WaybillWeightVOConvertExcetion
     */
    public void validWaybillProcess(WaybillWeightDTO dto) throws WeighByWaybillExcpetion
    {
        this.sendMessageToMq(dto);
        this.logToOperationlogCassandra(dto);
    }

    /**
     * 当运单经校验不存在时的流程
     *
     * @param dto 待传输消息对象
     * @throws WeighByWaybillExcpetion MQServiceNotAvailableException WaybillWeightVOConvertExcetion
     */
    public void invalidWaybillProcess(WaybillWeightDTO dto) throws WeighByWaybillExcpetion
    {
        this.sendMessageToMq(dto);
        this.logToOperationlogCassandra(dto);
    }

    /**
     * 发送运单称重MQ消息
     *
     * @param dto 待传输消息对象
     * @throws WeighByWaybillExcpetion WaybillWeightVOConvertExcetion MQServiceNotAvailableException
     */
    private void sendMessageToMq(WaybillWeightDTO dto) throws WeighByWaybillExcpetion
    {
        try
        {
            weighByWaybillProducer.send(dto.getWaybillCode(), JsonHelper.toJson(dto));
        } catch (Exception e)
        {
            logger.error(e);
            throw new WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum.MQServiceNotAvailableException);
        }

    }

    /**
     * 将待传输MQ的信息记录 Cassandra,便于特殊问题定则
     *
     * @param dto 操作消息对象
     */
    private void logToOperationlogCassandra(WaybillWeightDTO dto)
    {
        try
        {
            Goddess goddess = new Goddess();
            goddess.setKey(dto.getWaybillCode());
            goddess.setBody(JsonHelper.toJson(dto));
            goddess.setHead(CASSANDRA_SIGN  +  String.valueOf(dto.getStatus()));

            goddessService.save(goddess);
        } catch (Exception e)
        {
            logger.error("运单称重：cassandra操作日志记录失败");
            logger.error(e);
        }
    }

    /**
     * 传入值为kg 标准为kg 重量现阶段单位相同不需转换 后续有可能变化
     *
     * @param weight kg
     * @return kg
     */
    private Double convertWeightUnitToRequired(Double weight)
    {
        return weight;
    }

    /**
     * 体积单位 传入值为立方米 运单要求标准为立方厘米
     *
     * @param cbm 立方米
     * @return 体积 立方厘米
     */
    private Double convertVolumeUnitToRequired(Double cbm)
    {
        return cbm * 1000000.0;
    }

    /**
     * 正则校验运单号
     *
     * @param waybillCode 运单号
     * @return 是否匹配
     */
    private boolean isValidWaybillCode(String waybillCode)
    {
        Pattern pattern = Pattern.compile(WAYBILL_REGEX);
        Matcher matcher = pattern.matcher(waybillCode);

        return matcher.matches();
    }

    /**
     * 正则校验包裹号
     *
     * @param packageCode 包裹号
     * @return 是否匹配
     */
    private boolean isValidPackageCode(String packageCode)
    {
        Pattern pattern = Pattern.compile(PACKAGE_REGEX);
        Matcher matcher = pattern.matcher(packageCode);

        return matcher.matches();
    }
}
