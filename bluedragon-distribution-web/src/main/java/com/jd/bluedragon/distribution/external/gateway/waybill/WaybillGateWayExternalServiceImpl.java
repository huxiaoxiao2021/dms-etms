package com.jd.bluedragon.distribution.external.gateway.waybill;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest;
import com.jd.bluedragon.external.gateway.waybill.WaybillGateWayExternalService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 运单相关 发布物流网关
 * @author : xumigen
 * @date : 2020/1/2
 */
public class WaybillGateWayExternalServiceImpl implements WaybillGateWayExternalService {
    private final Logger logger = LoggerFactory.getLogger(WaybillGateWayExternalServiceImpl.class);

    @Autowired
    private ThirdBoxDetailService thirdBoxDetailService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    private static final Integer OPERATION_SORTING = 1;//集包
    private static final Integer OPERATION_CANCEL = 2;//取消集包
    private static final Integer BOX_MAX_PACKAGE = 20000;//经济网集包上限
    private static final Integer SITE_TYPE= 10000;//经济网网点类型

    @Override
    public GateWayBaseResponse<Void> syncWaybillCodeAndBoxCode(WaybillSyncRequest request,String pin) {
        logger.info("同步运单与箱号信息waybillCode[{}]boxCode[{}]",request.getWaybillCode(),request.getBoxCode());
        GateWayBaseResponse<Void> response = null;
        //参数校验
        response = coreParamCheck(request);
        if(!GateWayBaseResponse.CODE_SUCCESS.equals(response.getResultCode())){
            return response;
        }
        //获取基础信息
        BaseStaffSiteOrgDto startSite = null;
        Box box = null;
        try{
            //获取始发地
            startSite = baseMajorManager.getBaseSiteByDmsCode(request.getStartSiteCode());
            if(startSite == null || startSite.getSiteCode() == null){
                response.toConfirm(GateWayBaseResponse.MESSAGE_START_SITE_CONFIRM);
                return response;
            }
            //校验始发为经济网站点
            if(!SITE_TYPE.equals(startSite.getSiteType())){
                response.toConfirm(GateWayBaseResponse.MESSAGE_START_SITE_TYPE_CONFIRM);
                return response;
            }
            //查询箱号
            box = boxService.findBoxByCode(request.getBoxCode());
            if(box == null || !request.getBoxCode().equals(box.getCode())){
                response.toConfirm(GateWayBaseResponse.MESSAGE_BOX_CONFIRM);
                return response;
            }
        }catch (Exception e){
            logger.error("经济网查询始发地或箱号异常：{}-{}", request.getStartSiteCode(), request.getBoxCode(), e);
            response.toFail(GateWayBaseResponse.MESSAGE_FAIL);
            return response;
        }
        //业务操作
        if(OPERATION_SORTING.equals(request.getOperationType())){
            return sorting(request, startSite, box);
        }else if(OPERATION_CANCEL.equals(request.getOperationType())){
            return cancelSorting(request, startSite.getSiteCode(), box);
        }else{
            response.toError(GateWayBaseResponse.MESSAGE_OPERATION_TYPE_ERROR);
            return response;
        }
    }

    /**
     * 核心参数校验
     * @param request 请求体
     * @return 结果
     */
    private GateWayBaseResponse<Void> coreParamCheck(WaybillSyncRequest request){
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();
        if(!Constants.TENANT_CODE_ECONOMIC.equals(request.getTenantCode())){
            response.toError(GateWayBaseResponse.MESSAGE_ERROR);
            return response;
        }
        if(StringUtils.isBlank(request.getStartSiteCode())){
            response.toError(GateWayBaseResponse.MESSAGE_START_SITE_ERROR);
            return response;
        }
        if(StringUtils.isBlank(request.getBoxCode())){
            response.toError(GateWayBaseResponse.MESSAGE_BOX_ERROR);
            return response;
        }
        if(StringUtils.isBlank(request.getPackageCode())){
            response.toError(GateWayBaseResponse.MESSAGE_PACKAGECODE_ERROR);
            return response;
        }
        return response;
    }


    private GateWayBaseResponse<Void> sorting(WaybillSyncRequest request, BaseStaffSiteOrgDto startSite, Box box){
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();
        try {
            //集包校验
            sortingCheck(startSite, box);
            if(!GateWayBaseResponse.CODE_SUCCESS.equals(response.getResultCode())){
                return response;
            }
            //获取目的地
            BaseStaffSiteOrgDto endSite = baseMajorManager.getBaseSiteByDmsCode(request.getEndSiteCode());
            if(endSite == null || endSite.getSiteCode() == null){
                response.toConfirm(GateWayBaseResponse.MESSAGE_END_SITE_CONFIRM);
                return response;
            }
            //集包
            ThirdBoxDetail detail = convertRequest(request, startSite.getSiteCode(), endSite.getSiteCode());
            boolean result = thirdBoxDetailService.sorting(detail);
            if(!result){
                response.toFail(GateWayBaseResponse.MESSAGE_FAIL);
            }
        }catch (Exception e){
            logger.error("经济网集包异常：{}", JsonHelper.toJson(request), e);
            response.toFail(GateWayBaseResponse.MESSAGE_FAIL);
        }
        return response;
    }

    /**
     * 经济网集包校验
     * @param site 始发站点
     * @param box 箱子
     * @return 结果
     */
    private GateWayBaseResponse<Void> sortingCheck(BaseStaffSiteOrgDto site, Box box){
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();

        //校验箱子是否已发货
        if(Box.BOX_STATUS_SEND.equals(box.getStatus())){
            response.toFail(GateWayBaseResponse.MESSAGE_BOX_SEND_FAIL);
            return response;
        }
        //校验箱子始发地是否和集包始发地一致
        if(site.getSiteCode() != box.getCreateSiteCode()){
            response.toError(GateWayBaseResponse.MESSAGE_BOX_SITE_ERROR);
            return response;
        }
        //校验箱子集包数量是否达到上限
        if(box.getPackageNum() > BOX_MAX_PACKAGE){
            response.toFail(GateWayBaseResponse.MESSAGE_BOX_MAX_FAIL);
            return response;
        }

        return response;
    }

    /**
     * 转换请求体为实体对象
     * @param request 请求体
     * @param startSiteId 始发站点ID
     * @param endSiteId 目的站点ID
     * @return 实体
     */
    private ThirdBoxDetail convertRequest(WaybillSyncRequest request, Integer startSiteId, Integer endSiteId){
        ThirdBoxDetail detail = new ThirdBoxDetail();
        detail.setTenantCode(request.getTenantCode());
        detail.setStartSiteId(startSiteId);
        detail.setStartSiteCode(request.getStartSiteCode());
        detail.setEndSiteId(endSiteId);
        detail.setEndSiteCode(request.getEndSiteCode());
        detail.setOperatorId(request.getOperatorId());
        detail.setOperatorName(request.getOperatorName());
        detail.setOperatorUnitName(request.getOperatorUnitName());
        detail.setOperatorTime(request.getOperatorTime());
        detail.setBoxCode(request.getBoxCode());
        detail.setWaybillCode(request.getWaybillCode());
        detail.setPackageCode(request.getPackageCode());

        return detail;
    }

    /**
     * 取消集包
     * @param request 请求体
     * @param startSiteId 始发站点ID
     * @param box 箱子
     * @return 返回值
     */
    private GateWayBaseResponse<Void> cancelSorting(WaybillSyncRequest request, Integer startSiteId, Box box){
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();
        try {
            //取消集包校验
            response = cancelSortingCheck(box);
            if(!GateWayBaseResponse.CODE_SUCCESS.equals(response.getResultCode())){
                return response;
            }
            //取消集包
            boolean result = thirdBoxDetailService.cancel(request.getTenantCode(),
                    startSiteId, request.getBoxCode(), request.getPackageCode());
            if(!result){
                response.toConfirm(GateWayBaseResponse.MESSAGE_BOX_PACKAGE_CONFIRM);
            }
        }catch (Exception e){
            logger.error("经济网取消集包异常：{}", JsonHelper.toJson(request), e);
            response.toFail(GateWayBaseResponse.MESSAGE_FAIL);
        }

        return response;
    }

    /**
     * 取消集包校验：箱子是否已发货
     * @param box 箱子
     * @return 返回值
     */
    private GateWayBaseResponse<Void> cancelSortingCheck(Box box){
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();
        //校验箱号状态
        if(Box.BOX_STATUS_SEND.equals(box.getStatus())){
            response.toFail(GateWayBaseResponse.MESSAGE_BOX_SEND_FAIL);
            return response;
        }
        return response;
    }
}
