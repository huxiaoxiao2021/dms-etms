package com.jd.bluedragon.distribution.send.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.common.dto.send.request.DeliveryVerifyRequest;
import com.jd.bluedragon.distribution.send.service.DeliveryVerifyService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName DeliveryVerifyServiceImpl
 * @date 2019/6/13
 */
@Service("deliveryVerifyService")
public class DeliveryVerifyServiceImpl implements DeliveryVerifyService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BoxService boxService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public JdVerifyResponse packageSendVerifyForBoxCode(DeliveryVerifyRequest request) {
        JdVerifyResponse response = new JdVerifyResponse();
        response.toSuccess();
        try {
            if (!this.check(request, response)) {
                return response;
            }
            Box box = this.boxService.findBoxByCode(request.getBoxCode());
            if (box == null) {
                response.addInterceptBox(40000, "无该箱号信息，请核实后再操作");
                return response;
            }

            if (!box.getTransportType().equals(request.getLastBoxTransportType())) {
                response.addInterceptBox(40001, "发货箱号运输类型与上一箱不一致");
                return response;
            }

            Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getSendCode());
            BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
            if (siteOrgDto != null) {
                if (Constants.BASE_SITE_DISTRIBUTION_CENTER.equals(siteOrgDto.getSiteType())) {
                    // 判断是否需要走路由校验
                    if (!isNeedRouterVerify(request.getOperateSiteCode())) {
                        //
                        this.receiveSiteVerify(box.getReceiveSiteCode(), receiveSiteCode, response);
                    }
                } else {
                    this.receiveSiteVerify(box.getReceiveSiteCode(), receiveSiteCode, response);
                }
            }
        } catch (Exception e) {
            logger.error("[一车一单发货]按箱发货校验异常，请求参数：" + JsonHelper.toJson(request), e);
            response.toError();
        }
        return response;
    }

    /**
     * 检查输入的编号是否符合编码规则
     *
     * @param request
     * @param response
     * @return
     */
    private boolean check(DeliveryVerifyRequest request, JdVerifyResponse response) {
        if (!BusinessUtil.isSendCode(request.getSendCode())) {
            response.addInterceptBox(40003, "发货批次号不符合规则");
            return false;
        }

        if (!BusinessUtil.isBoxcode(request.getBoxCode())) {
            response.addInterceptBox(40004, "箱号不符合规则");
            return false;
        }
        return true;
    }


    /**
     * 需要路由校验返回true 否则返回false
     *
     * @param currentSiteCode
     * @return
     */
    private boolean isNeedRouterVerify(Integer currentSiteCode) {
        List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(Constants.SYS_CONFIG_CROUTER_OPEN_DMS_CODES);
        if (!sysConfigs.isEmpty()) {
            if (sysConfigs.get(0).getConfigContent().indexOf(Constants.SEPARATOR_COMMA) > 0) {
                String[] configSiteList = sysConfigs.get(0).getConfigContent().split(Constants.SEPARATOR_COMMA);
                for (String siteCode : configSiteList) {
                    if (currentSiteCode.toString().equals(siteCode)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 箱子目的地与批次目的地校验
     *
     * @param boxReceiveSiteCode
     * @param sendCodeReceiveSiteCode
     * @param response
     */
    private void receiveSiteVerify(Integer boxReceiveSiteCode, Integer sendCodeReceiveSiteCode, JdVerifyResponse response) {
        if (!boxReceiveSiteCode.equals(sendCodeReceiveSiteCode)) {
            response.addConfirmBox(30000, "箱号的收货站点与发货批次站点不一致，是否继续？");
        }
    }
}
