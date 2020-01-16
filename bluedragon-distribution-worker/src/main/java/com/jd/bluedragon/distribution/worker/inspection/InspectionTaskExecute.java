package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.framework.AbstractTaskExecute;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.exception.WayBillCodeIllegalException;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by wangtingwei on 2017/1/16.
 */
public class InspectionTaskExecute extends AbstractTaskExecute<InspectionTaskExecuteContext> {


    private static final Logger log = LoggerFactory.getLogger(InspectionTaskExecute.class);

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private CenConfirmService cenConfirmService;

    @Resource(name="storeIdSet")
    private Set<Integer> storeIdSet;

    @Override
    protected InspectionTaskExecuteContext prepare(Task domain) {
        InspectionTaskExecuteContext context=new InspectionTaskExecuteContext();
        context.setPassCheck(true);
        InspectionRequest request= JsonHelper.fromJsonUseGson(domain.getBody(),InspectionRequest.class);
        if(null==request){
            if(log.isWarnEnabled()){
                log.warn("验货JSON解析后对象为空{}",domain.getBody());
            }
            context.setPassCheck(false);
            return context;
        }
        builderSite(request,context);
        String code = request.getPackageBarOrWaybillCode();
        /** 是否按运单验货 */
        boolean isByWayBillCode = false;
        // 如果是包裹号获取取件单号，则存在包裹号属性中
        if (WaybillUtil.isPackageCode(code)|| WaybillUtil.isSurfaceCode(code)) {
            request.setPackageBarcode(code);
        } else if (WaybillUtil.isWaybillCode(code)) {// 否则为运单号
            isByWayBillCode = true;
            request.setWaybillCode(code);
        } else {
            String errorMsg = "验货条码不符合规则:" + code;
            log.warn(errorMsg);
            throw new WayBillCodeIllegalException(errorMsg);
        }

        String waybillCode = WaybillUtil.getWaybillCode(request.getPackageBarOrWaybillCode());

        BigWaybillDto bigWaybillDto = getWaybill(waybillCode, isByWayBillCode);
        if(bigWaybillDto == null){    //没有查到运单信息，可能运单号不存在或者服务暂不可用等
            context.setPassCheck(false);
            return context;
        }
        context.setBigWaybillDto(bigWaybillDto);
        resetBusinessType(request, bigWaybillDto);/*验货businessType存在非50的数据吗，需要验证*/
        resetStoreId(request, bigWaybillDto);
        builderInspectionList(request, context);
        builderCenConfirmList(context);
        return context;
    }



    /**
     * 保存数据至Inspection及Cen_confirm
     * @param inspectionTaskExecuteContext
     * @return
     */
    @Override
    protected boolean executeCoreFlow(InspectionTaskExecuteContext inspectionTaskExecuteContext) {
        for(Inspection domain : inspectionTaskExecuteContext.getInspectionList()){
            inspectionService.insertOrUpdate(domain);
        }
        for (CenConfirm confirm:inspectionTaskExecuteContext.getCenConfirmList()){
            cenConfirmService.updateOrInsert(confirm);
        }
        return true;
    }

    /**
     * pda不再区分用户入口， 所有订单都可以扫描， 由后台获取运单ordertype和storeid 判断以前的类型
     * 2014年12月16日16:21:55 by guoyongzhi
     * @param request
     * @param bigWaybillDto
     */
    private final void resetBusinessType(InspectionRequest request,BigWaybillDto bigWaybillDto){
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
    private final void resetStoreId(InspectionRequest request,BigWaybillDto bigWaybillDto){
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
    private final void builderInspectionList(InspectionRequest request,InspectionTaskExecuteContext context){
        List<Inspection> inspectionList=new ArrayList<Inspection>();
        context.setInspectionList(inspectionList);
        BigWaybillDto bigWaybillDto=context.getBigWaybillDto();
        if (StringUtils.isNotBlank(request.getWaybillCode())) {
            if (null == bigWaybillDto||null==bigWaybillDto.getPackageList()||bigWaybillDto.getPackageList().size()==0) {
                log.warn("验货包裹信息为空{}",context.getBusinessKey());
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
        if(log.isInfoEnabled()){
            log.info("验货明细为{}",JsonHelper.toJson(inspectionList));
        }
    }


    private final void builderCenConfirmList(InspectionTaskExecuteContext context){
        List<CenConfirm> cenList=new ArrayList<CenConfirm>(context.getInspectionList().size());

        for (Inspection inspection:context.getInspectionList()) {
            CenConfirm cenConfirm= cenConfirmService.createCenConfirmByInspection(inspection);
            if (Constants.BUSSINESS_TYPE_POSITIVE == cenConfirm.getType()
                    || Constants.BUSSINESS_TYPE_REVERSE == cenConfirm.getType()) {
                if (WaybillUtil.isSurfaceCode(cenConfirm.getPackageBarcode())) {
                    cenConfirm =cenConfirmService.fillPickupCode(cenConfirm);// 根据取件单序列号获取取件单号和运单号

                    cenConfirm.setOperateType(Constants.PICKUP_OPERATE_TYPE);
                } else {
                    cenConfirm = cenConfirmService.fillOperateType(cenConfirm);// 根据运单号调用运单接口判断操作类型
                }
            }else if(Constants.BUSSINESS_TYPE_SITE==cenConfirm.getType()){
                cenConfirm.setOperateType(Constants.OPERATE_TYPE_PSY);
            }else if(Constants.BUSSINESS_TYPE_InFactory==cenConfirm.getType()){
                cenConfirm.setOperateType(Constants.OPERATE_TYPE_In);
            }
            cenList.add(cenConfirm);
        }
        Collections.sort(cenList);
        context.setCenConfirmList(cenList);
    }

    private final void builderSite(InspectionRequest request,InspectionTaskExecuteContext context){
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
