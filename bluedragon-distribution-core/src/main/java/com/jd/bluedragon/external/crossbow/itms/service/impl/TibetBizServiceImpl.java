package com.jd.bluedragon.external.crossbow.itms.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.external.crossbow.itms.domain.*;
import com.jd.bluedragon.external.crossbow.itms.manager.ItmsManager;
import com.jd.bluedragon.external.crossbow.itms.service.TibetBizService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TibetBizServiceImpl
 * @Description
 * @Author wyh
 * @Date 2021/6/4 16:06
 **/
@Service("tibetService")
public class TibetBizServiceImpl implements TibetBizService {

    private static final Logger log = LoggerFactory.getLogger(TibetBizServiceImpl.class);

    @Value("${itms.partnerNo}")
    private String partnerNo;

    @Autowired
    private UccPropertyConfiguration uccSwitch;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("itmsDownSendDataManager")
    private ItmsManager downSendDataManager;

    @Autowired
    @Qualifier("itmsDownCancelSendDataManager")
    private ItmsManager downCancelSendDataManager;

    @Autowired
    @Qualifier("itmsSendCheckSendCodeManager")
    private ItmsManager sendCheckSendCodeManager;

    @Autowired
    @Qualifier("itmsCancelSendCheckSendCodeManager")
    private ItmsManager cancelSendCheckSendCodeManager;


    /**
     * 西藏模式开关，始发场地和目的场地归属的省份配置了开关
     *
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    @Override
    public boolean tibetModeSwitch(Integer createSiteCode, Integer receiveSiteCode) {

        if (StringUtils.isBlank(uccSwitch.getItmsBizEnableSwitch())) {
            return false;
        }

        List<Integer> enableProvIds = null;
        try {
            String[] ids = uccSwitch.getItmsBizEnableSwitch().split(Constants.SEPARATOR_COMMA);
            if (ids.length > 0) {
                enableProvIds = new ArrayList<>();
                for (String id : ids) {
                    enableProvIds.add(Integer.parseInt(id));
                }
            }
        }
        catch (Exception ex) {
            log.error("获取ITMS业务开关失败.", ex);
        }

        if (CollectionUtils.isEmpty(enableProvIds)) {
            return false;
        }

        if (null == createSiteCode || null == receiveSiteCode) {
            return false;
        }
        BaseStaffSiteOrgDto fromSite = this.baseMajorManager.getBaseSiteBySiteId(createSiteCode);
        if (fromSite == null) {
            return false;
        }
        BaseStaffSiteOrgDto toSite = this.baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
        if (toSite == null) {
            return false;
        }

        if (null == fromSite.getProvinceId() || null == toSite.getProvinceId()) {
            return false;
        }

        return enableProvIds.contains(fromSite.getProvinceId()) && enableProvIds.contains(toSite.getProvinceId());
    }

    /**
     * 推送发货数据
     * @param sendDetailDto
     * @return
     */
    @Override
    public ItmsResponse downSendDataToItms(ItmsSendDetailDto sendDetailDto) {
        ItmsResponse response = new ItmsResponse();
        response.setCode(ItmsResponse.CODE_SUCCESS);
        try {
            sendDetailDto.setPartnerNo(partnerNo);
            response = downSendDataManager.doRestInterface(sendDetailDto);
        }
        catch (Exception ex) {
            log.error("给ITMS系统推送发货数据失败. req:{}", JsonHelper.toJson(sendDetailDto), ex);
            response.setCode(String.valueOf(JdResponse.CODE_ERROR));
            response.setMessage(ex.getMessage());
        }

        return response;
    }

    /**
     * 推送取消发货数据
     * @param cancelSendDto
     * @return
     */
    @Override
    public ItmsResponse downSendCancelDataToItms(ItmsCancelSendDto cancelSendDto) {
        ItmsResponse response = new ItmsResponse();
        response.setCode(ItmsResponse.CODE_SUCCESS);
        try {
            cancelSendDto.setPartnerNo(partnerNo);
            response = downCancelSendDataManager.doRestInterface(cancelSendDto);
        }
        catch (Exception ex) {
            log.error("给ITMS系统推送取消发货数据失败. req:{}", JsonHelper.toJson(cancelSendDto), ex);
            response.setCode(String.valueOf(JdResponse.CODE_ERROR));
            response.setMessage(ex.getMessage());
        }
        return response;
    }

    /**
     * 发货校验批次
     * @param checkSendCodeDto
     * @return
     */
    @Override
    public ItmsResponse sendCheckSendCode(ItmsSendCheckSendCodeDto checkSendCodeDto) {
        ItmsResponse response = new ItmsResponse();
        response.setCode(ItmsResponse.CODE_SUCCESS);
        try {
            checkSendCodeDto.setPartnerNo(partnerNo);
            response = sendCheckSendCodeManager.doRestInterface(checkSendCodeDto);
        }
        catch (Exception ex) {
            log.error("校验批次状态失败. req:{}", JsonHelper.toJson(checkSendCodeDto), ex);
            response.setCode(String.valueOf(JdResponse.CODE_ERROR));
            response.setMessage(ex.getMessage());
        }
        return response;
    }

    /**
     * 取消发货校验批次
     * @param checkSendCodeDto
     * @return
     */
    @Override
    public ItmsResponse cancelSendCheckSendCode(ItmsCancelSendCheckSendCodeDto checkSendCodeDto) {
        ItmsResponse response = new ItmsResponse();
        response.setCode(ItmsResponse.CODE_SUCCESS);
        try {
            checkSendCodeDto.setPartnerNo(partnerNo);
            response = cancelSendCheckSendCodeManager.doRestInterface(checkSendCodeDto);
        }
        catch (Exception ex) {
            log.error("校验批次取消状态失败. req:{}", JsonHelper.toJson(checkSendCodeDto), ex);
            response.setCode(String.valueOf(JdResponse.CODE_ERROR));
            response.setMessage(ex.getMessage());
        }
        return response;
    }
}
