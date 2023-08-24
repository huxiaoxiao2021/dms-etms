package com.jd.bluedragon.distribution.external.service.impl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.common.domain.ServiceResultEnum;
import com.jd.bluedragon.distribution.api.request.material.batch.MaterialBatchSendRequest;
import com.jd.bluedragon.distribution.api.response.material.batch.MaterialTypeResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.external.intensive.service.MaterialBatchSendJsfService;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.service.SortingMaterialSendService;
import com.jd.bluedragon.distribution.material.util.MaterialServiceFactory;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * 循环物资发货相关jsf服务
 * @author wuyoude
 *
 */
@Service("materialBatchSendJsfService")
public class MaterialBatchSendJsfServiceImpl implements MaterialBatchSendJsfService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SortingMaterialSendService sortingMaterialSendService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private DepartureService departureService;
    
    @Autowired
    private MaterialServiceFactory materialServiceFactory;
    
	@Override
	public JdResult<Boolean> materialBatchSend(MaterialBatchSendRequest request) {
        JdResult<Boolean> response = new JdResult<>();
        response.toSuccess();

        if (null == request
                || null == request.getSendBusinessMode()
                || null == request.getSiteCode()
                || StringUtils.isBlank(request.getSendCode())
                || CollectionUtils.isEmpty(request.getSendDetails())) {
            response.toError("缺少必要参数");
            return response;
        }

        logger.debug("物资按批次号发货参数：[{}]", JsonHelper.toJson(request));

        try {
            List<DmsMaterialSend> materialSends = convertRequest2Send(request);
            response = materialServiceFactory.findMaterialOperationService(request.getSendBusinessMode()).saveMaterialSend(materialSends, true/*流水里保存多次发货的记录*/);
        }
        catch (Exception ex) {
            logger.error("MaterialBatchSendResource-materialBatchSend Failed. req:[{}]", JsonHelper.toJson(request), ex);
            response.toError("服务器异常");
        }

        return response;
	}
    private List<DmsMaterialSend> convertRequest2Send(MaterialBatchSendRequest request) {
        List<DmsMaterialSend> sends = new ArrayList<>(request.getSendDetails().size());
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(request.getSiteCode());
        for (MaterialBatchSendRequest.MaterialSendByTypeDetail sendDetail : request.getSendDetails()) {
            DmsMaterialSend send = new DmsMaterialSend();
            send.setSendType(MaterialSendTypeEnum.SEND_BY_BATCH.getCode());
            send.setSendCode(request.getSendCode());
            send.setSendNum(sendDetail.getSendNum());
            send.setMaterialCode(sendDetail.getMaterialTypeCode());
            // TODO 耗材档案的物资类型
            send.setMaterialType((byte)0);
            send.setCreateSiteCode(request.getSiteCode().longValue());
            send.setCreateSiteType(baseStaffSiteOrgDto.getSiteType());
            send.setReceiveSiteType(0);
            send.setReceiveSiteCode(0L);
            send.setCreateUserErp(request.getUserErp());
            send.setCreateUserName(request.getUserName());
            send.setUpdateUserErp(request.getUserErp());
            send.setUpdateUserName(request.getUserName());

            sends.add(send);
        }

        return sends;
    }
	@Override
	public JdResult<Boolean> cancelMaterialBatchSend(MaterialBatchSendRequest request) {
        JdResult<Boolean> response = new JdResult<>();
        response.toSuccess();

        if (null == request
                || null == request.getSendBusinessMode()
                || null == request.getSiteCode()
                || StringUtils.isBlank(request.getSendCode())) {
            response.toError("缺少必要参数");
            return response;
        }

        logger.debug("物资按批次号取消发货参数：[{}]", JsonHelper.toJson(request));

        try {
            response = sortingMaterialSendService.cancelMaterialSendBySendCode(request);
        }
        catch (Exception ex) {
        	logger.error("MaterialBatchSendResource-cancelMaterialBatchSend Failed. req:[{}]", JsonHelper.toJson(request), ex);
            response.toError("服务器异常");
        }
        return response;
	}

	@Override
	public JdResult<List<MaterialTypeResponse>> listMaterialType(MaterialBatchSendRequest request) {
        JdResult<List<MaterialTypeResponse>> response = new JdResult<>();
        response.toSuccess();
        if (null == request
                || null == request.getSendBusinessMode()
                || null == request.getSiteCode()) {
            response.toError("缺少必要参数");
            return response;
        }
        try {
            response = sortingMaterialSendService.listSortingMaterialType(request);
        }
        catch (Exception ex) {
        	logger.error("MaterialBatchSendResource-cancelMaterialBatchSend Failed. req:[{}]", JsonHelper.toJson(request), ex);
            response.toError("服务器异常!");
        }
        return response;
	}

	@Override
	public InvokeResult<Entry<Integer, String>> getSendCodeDestination(String sendCode) {
        InvokeResult<AbstractMap.Entry<Integer, String>> result = new InvokeResult<>();
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode);
        if (null == receiveSiteCode) {
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("请输入正确的批次号！");
            return result;
        }

        try {
            ServiceMessage<Boolean> data = departureService.checkSendStatusFromVOS(sendCode);
            if (ServiceResultEnum.WRONG_STATUS.equals(data.getResult())) {
                result.setData(new AbstractMap.SimpleEntry<>(2, "该发货批次号已操作封车，无法重复操作！"));
            }
            else if (ServiceResultEnum.SUCCESS.equals(data.getResult())) {
                BaseStaffSiteOrgDto site = siteService.getSite(receiveSiteCode);
                String siteName = null != site ? site.getSiteName() : "未获取到该站点名称";
                result.setData(new AbstractMap.SimpleEntry<>(1, siteName));
            }
            else {
                result.error(data.getErrorMsg());
            }
        }
        catch (Exception ex) {
            result.error("服务器异常!");
            logger.error("物资发货获取批次号异常：sendCode:[{}]", sendCode, ex);
        }

        return result;
	}
 
}
