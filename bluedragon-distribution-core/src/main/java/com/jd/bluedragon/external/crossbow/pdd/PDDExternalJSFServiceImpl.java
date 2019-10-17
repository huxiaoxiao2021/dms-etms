package com.jd.bluedragon.external.crossbow.pdd;

import com.jd.bluedragon.distribution.external.pdd.DMSExternalInPDDService;
import com.jd.bluedragon.distribution.external.pdd.domain.PDDWaybillPrintInfoDto;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDRequest;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.bluedragon.external.crossbow.pdd.service.PDDService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     拼多多运单面单信息的获取实现类
 *
 * <doc>
 *     通过调用架构部组件crossBow,其作用是作为一个外部三方公司的代理服务，提供给内网进行使用，从而屏蔽外部复杂的协议和网络权限的不同
 * </doc>
 *
 * @author wuzuxiang
 * @since 2019/10/14
 **/
@Service("dmsExternalInPDDService")
public class PDDExternalJSFServiceImpl implements DMSExternalInPDDService {

    private static final Logger logger = LoggerFactory.getLogger(PDDExternalJSFServiceImpl.class);

    @Autowired
    private PDDService pddService;

    @Override
    public BaseEntity<PDDWaybillPrintInfoDto> queryPDDWaybillByWaybillCode(String waybillCode) {
        try {
            PDDWaybillDetailDto pddWaybillDetailDto = pddService.queryWaybillDetailByWaybillCode(waybillCode);
            if (null == pddWaybillDetailDto) {
                return new BaseEntity<>(BaseEntity.CODE_SUCCESS_NO, BaseEntity.MESSAGE_SUCCESS_NO);
            }
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
        } catch (Exception e) {
            return new BaseEntity<>(BaseEntity.CODE_SERVICE_ERROR, BaseEntity.MESSAGE_SERVICE_ERROR);
        }
    }
}
