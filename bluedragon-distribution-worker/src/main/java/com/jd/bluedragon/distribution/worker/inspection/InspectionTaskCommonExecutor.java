package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.distribution.inspection.exception.WayBillCodeIllegalException;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.ump.UmpMonitorHandler;
import com.jd.bluedragon.utils.ump.UmpMonitorHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

/**
 * @ClassName InspectionTaskCommonExecutor
 * @Description
 * @Author wyh
 * @Date 2020/9/27 13:47
 **/
public abstract class InspectionTaskCommonExecutor extends AbstractInspectionTaskExecutor<InspectionTaskExecuteContext> {

    @Resource(name = "storeIdSet")
    private Set<Integer> storeIdSet;

    @Autowired
    WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseService baseService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private CenConfirmService cenConfirmService;

    @Override
    protected InspectionTaskExecuteContext prepare(InspectionRequest request) {
        return null;
    }

    @Override
    protected boolean executeCoreFlow(final InspectionTaskExecuteContext context) {

        String umpKey = "DMSWORKER.InspectionTaskExecute.executeCoreFlow";

        UmpMonitorHelper.doWithUmpMonitor(umpKey, Constants.UMP_APP_NAME_DMSWORKER, new UmpMonitorHandler() {
            @Override
            public void process() {
                for (Inspection domain : context.getInspectionList()) {
                    inspectionService.insertOrUpdate(domain);
                }
                for (CenConfirm confirm: context.getCenConfirmList()) {
                    cenConfirmService.updateOrInsert(confirm);
                }
            }
        });

        return true;
    }

    @Override
    protected boolean otherOperation(InspectionRequest request, InspectionTaskExecuteContext context) {

        return true;
    }

    /**
     * 获取运单信息
     * @param waybillCode 运单号
     * @return
     */
    protected BigWaybillDto getWaybill(String waybillCode, boolean queryPackList){
        if (!SerialRuleUtil.isMatchCommonWaybillCode(waybillCode)) {
            String errorMsg = "运单号正则校验不通过:" + waybillCode;
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(errorMsg);
            }
            throw new WayBillCodeIllegalException(errorMsg);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(MessageFormat.format("获取运单信息{0}", waybillCode));
        }
        BigWaybillDto result = null;
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true, false, true, queryPackList);
        if (baseEntity != null) {
            result= baseEntity.getData();
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(MessageFormat.format("获取运单信息{0}, 结果为{1}", waybillCode, JsonHelper.toJson(result)));
        }
        return result;
    }

    protected BaseStaffSiteOrgDto getSite(Integer siteCode){
        return baseService.getSiteBySiteID(siteCode);
    }

    /**
     * pda不再区分用户入口， 所有订单都可以扫描， 由后台获取运单ordertype和storeid 判断以前的类型
     * 2014年12月16日16:21:55 by guoyongzhi
     * @param request
     * @param bigWaybillDto
     */
    protected void resetBusinessType(InspectionRequest request,BigWaybillDto bigWaybillDto){
        if(Integer.valueOf(Constants.BUSSINESS_TYPE_NEWTRANSFER).equals(request.getBusinessType())&&bigWaybillDto != null && bigWaybillDto.getWaybill()!=null)//包裹交接类型 以前是 1130： 50库房， 51夺宝岛 52协同仓  现在是50
        {
            if (Integer.valueOf(Constants.BUSSINESS_TYPE_DBD_ORDERTYPE).equals(bigWaybillDto.getWaybill().getWaybillType())) { //夺宝岛交接 51 : 2
                request.setBusinessType(Constants.BUSSINESS_TYPE_BDB);
            } else if (Integer.valueOf(Constants.BUSSINESS_TYPE_ZY_ORDERTYPE).equals(bigWaybillDto.getWaybill().getWaybillType())) {  //正向库房或者协同仓交接
                if (storeIdSet.contains(bigWaybillDto.getWaybillState().getStoreId())) {
                    request.setBusinessType(Constants.BUSSINESS_TYPE_OEM_52);
                } else {
                    request.setBusinessType(Constants.BUSSINESS_TYPE_TRANSFER);
                }
            }
        }
    }



    /**
     * 返仓交接 重设库房号至收货站点
     * @param request
     * @param bigWaybillDto
     */
    protected void resetStoreId(InspectionRequest request,BigWaybillDto bigWaybillDto){
        if (Integer.valueOf(Constants.BUSSINESS_TYPE_FC).equals(request.getBusinessType().intValue())){
            if(null!=bigWaybillDto
                    &&null!=bigWaybillDto.getWaybill()
                    &&null!=bigWaybillDto.getWaybill().getDistributeStoreId()) {
                request.setReceiveSiteCode(bigWaybillDto.getWaybill().getDistributeStoreId());
            }
        }
    }
    /**
     * 构建验货列表数据
     * @param request
     * @param context
     */
    protected void builderInspectionList(InspectionRequest request, InspectionTaskExecuteContext context){
        List<Inspection> inspectionList=new ArrayList<Inspection>();
        context.setInspectionList(inspectionList);
        BigWaybillDto bigWaybillDto=context.getBigWaybillDto();
        if (StringUtils.isNotBlank(request.getWaybillCode())) {
            if (null == bigWaybillDto||null==bigWaybillDto.getPackageList()||bigWaybillDto.getPackageList().size()==0) {
                LOGGER.warn("验货包裹信息为空{}",context.getBusinessKey());
                context.setPassCheck(false);
                return;
            }
            List<DeliveryPackageD> packages=bigWaybillDto.getPackageList();
            if (BusinessHelper.checkIntNumRange(packages.size())) {
                for (DeliveryPackageD pack : packages) {
                    request.setPackageBarcode(pack.getPackageBarcode());
                    inspectionList.add(Inspection.toInspection(request,bigWaybillDto));
                }
            }
        } else if (StringUtils.isNotEmpty(request.getPackageBarcode())) {
            if (StringUtils.isBlank(request.getWaybillCode())
                    && !WaybillUtil.isSurfaceCode(request.getPackageBarcode())) {
                request.setWaybillCode(WaybillUtil.getWaybillCode(request.getPackageBarcode()));
            }
            inspectionList.add(Inspection.toInspection(request,bigWaybillDto));
        }
        Collections.sort(inspectionList);
        context.setInspectionList(inspectionList);
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("验货明细为{}", com.jd.bluedragon.utils.JsonHelper.toJson(inspectionList));
        }
    }


    protected void builderCenConfirmList(InspectionTaskExecuteContext context){
        List<CenConfirm> cenList=new ArrayList<CenConfirm>(context.getInspectionList().size());

        for (Inspection inspection:context.getInspectionList()) {
            cenList.add(cenConfirmService.commonGenCenConfirmFromInspection(inspection));
        }
        Collections.sort(cenList);
        context.setCenConfirmList(cenList);
    }

    protected void builderSite(InspectionRequest request, InspectionTaskExecuteContext context){
        BaseStaffSiteOrgDto site=this.getSite(request.getSiteCode());
        context.setCreateSite(site);
        if(null==site||null==site.getSiteCode()){
            context.setPassCheck(false);
        }
        if(request.getReceiveSiteCode()!=null&&request.getReceiveSiteCode()>0){
            BaseStaffSiteOrgDto rsite=this.getSite(request.getReceiveSiteCode());
            if(null==rsite||null==rsite.getSiteCode()){
                context.setPassCheck(false);
            }
            context.setReceiveSite(rsite);
        }
    }
}
