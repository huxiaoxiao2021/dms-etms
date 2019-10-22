package com.jd.bluedragon.external.crossbow.pdd.service;

import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.bluedragon.external.crossbow.pdd.manager.PDDBusinessManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.dms.common.domain.JdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * <p>
 *     拼多多电子面单处理类接口
 *
 * @author wuzuxiang
 * @since 2019/10/15
 **/
@Service
public class PDDServiceImpl implements PDDService {

    private static final Logger logger = LoggerFactory.getLogger(PDDServiceImpl.class);

    private static final long EXPIRED_TIME = 24 * 24 * 60 * 60 * 1000;//24天

    @Autowired
    @Qualifier("pddWaybillQueryManager")
    private PDDBusinessManager pddWaybillQueryManager;

    @Override
    public PDDWaybillDetailDto queryWaybillDetailByWaybillCode(String waybillCode) {
        if (!WaybillUtil.isPDDWaybillCode(waybillCode)) {
            return null;
        }
        PDDWaybillQueryDto pddWaybillQueryDto = new PDDWaybillQueryDto();
        pddWaybillQueryDto.setWaybillCode(waybillCode);

        PDDResponse<PDDWaybillDetailDto> response = pddWaybillQueryManager.doRestInterface(pddWaybillQueryDto);
        logger.debug("获取拼多多的电子面单处理信息，参数为：{}，返回结果为：{}",waybillCode, JsonHelper.toJson(response));
        if (response == null) {
            logger.error("获取拼多多电子面信息失败，信息获取为空,{}",waybillCode);
            return null;
        }
        if (!response.getSuccess()) {
            logger.warn("获取拼多多电子面单信息失败，单号为：{}, 原因：{}",waybillCode,response.getErrorMsg());
            return null;
        }
        return response.getResult();
    }

    @Override
    @Cache(key = "dms.core.PDD.waybill.@args0", memoryEnable = false, redisEnable = true, redisExpiredTime = EXPIRED_TIME)
    public PDDResponse<PDDWaybillDetailDto> queryPDDWaybillByWaybillCode(String waybillCode) {
        PDDResponse<PDDWaybillDetailDto> response = new PDDResponse<>();
        try {
            /* 调用拼多多的接口 */
            PDDWaybillQueryDto condition = new PDDWaybillQueryDto();
            condition.setWaybillCode(waybillCode);
            response = pddWaybillQueryManager.doRestInterface(condition);
        } catch (RuntimeException e) {
            logger.error(MessageFormat.format("拼多多接口调用发生异常，请求参数：{0}", waybillCode), e);
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(String.valueOf(JdResponse.CODE_ERROR));
            response.setErrorMsg(JdResponse.MESSAGE_ERROR);
        }
        return response;
    }
}
