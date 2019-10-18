package com.jd.bluedragon.external.crossbow.pdd;

import com.jd.bluedragon.distribution.external.pdd.DMSExternalInPDDService;
import com.jd.bluedragon.distribution.external.pdd.domain.PDDWaybillPrintInfoDto;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDRequest;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.bluedragon.external.crossbow.pdd.manager.PDDBusinessManager;
import com.jd.bluedragon.external.crossbow.pdd.service.PDDService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * <p>
 *     拼多多运单面单信息的获取实现类 jsf接口实现类，工程内的调用请使用 PDDService
 *
 * @link com.jd.bluedragon.external.crossbow.pdd.service.PDDService 工程内的拼多多业务的接口调用请调用该类方法
 * @author wuzuxiang
 * @since 2019/10/14
 **/
@Service("dmsExternalInPDDService")
public class PDDExternalJSFServiceImpl implements DMSExternalInPDDService {

    private static final Logger logger = LoggerFactory.getLogger(PDDExternalJSFServiceImpl.class);

    @Autowired
    @Qualifier("pddWaybillQueryManager")
    private PDDBusinessManager pddWaybillQueryManager;

    @Override
    public BaseEntity<PDDWaybillPrintInfoDto> queryPDDWaybillByWaybillCode(String waybillCode) {
        try {
            /* 调用拼多多的接口 */
            PDDResponse<PDDWaybillDetailDto> response = pddWaybillQueryManager.doRestInterface(waybillCode);
            if (null == response) {
                return new BaseEntity<>(BaseEntity.CODE_SUCCESS_NO, BaseEntity.MESSAGE_SUCCESS_NO);
            }
            /* 接口调用失败，则返回具体信息 */
            if (!response.getSuccess()) {
                logger.warn("拼多多接口调用失败，返回值：{}",JsonHelper.toJson(response));
                return new BaseEntity<>(BaseEntity.CODE_SUCCESS_NO, response.getErrorMsg());
            }
            /* 对拼多多接口返回内容进行重新组装返回 */
            PDDWaybillDetailDto pddWaybillDetailDto = response.getResult();
            BaseEntity<PDDWaybillPrintInfoDto> baseEntity = new BaseEntity<>(BaseEntity.CODE_SUCCESS, BaseEntity.MESSAGE_SUCCESS);
            PDDWaybillPrintInfoDto pddWaybillPrintInfoDto = new PDDWaybillPrintInfoDto();
            pddWaybillPrintInfoDto.setWaybillCode(waybillCode);
            pddWaybillPrintInfoDto.setSenderName(pddWaybillDetailDto.getSenderName());
            pddWaybillPrintInfoDto.setSenderMobile(pddWaybillDetailDto.getSenderMobile());
            pddWaybillPrintInfoDto.setSenderPhone(pddWaybillDetailDto.getSenderPhone());
            pddWaybillPrintInfoDto.setConsigneeName(pddWaybillDetailDto.getConsigneeName());
            pddWaybillPrintInfoDto.setConsigneeMobile(pddWaybillDetailDto.getConsigneeMobile());
            pddWaybillPrintInfoDto.setConsigneePhone(pddWaybillDetailDto.getConsigneePhone());
            baseEntity.setData(pddWaybillPrintInfoDto);
            return baseEntity;
        } catch (RuntimeException e) {
            logger.error(MessageFormat.format("拼多多接口调用发生异常，请求参数：{0}", waybillCode), e);
            return new BaseEntity<>(BaseEntity.CODE_SERVICE_ERROR, BaseEntity.MESSAGE_SERVICE_ERROR);
        }
    }
}
