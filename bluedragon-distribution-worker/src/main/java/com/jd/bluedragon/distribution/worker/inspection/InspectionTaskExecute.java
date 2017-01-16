package com.jd.bluedragon.distribution.worker.inspection;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.framework.AbstractTaskExecute;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangtingwei on 2017/1/16.
 */
public class InspectionTaskExecute extends AbstractTaskExecute<InspectionTaskExecuteContext> {

    private static final Type TYPE_LIST_INSPECTIONREQUEST=new TypeToken<List<InspectionRequest>>(){}.getType();

    private static final Log logger= LogFactory.getLog(InspectionTaskExecute.class);

    @Autowired
    private InspectionService inspectionService;

    @Override
    protected InspectionTaskExecuteContext prepare(Task domain) {
        InspectionTaskExecuteContext context=new InspectionTaskExecuteContext();
        context.setPassCheck(false);
        List<Inspection> inspectionList=new ArrayList<Inspection>();
        InspectionRequest request= JsonHelper.fromJsonUseGson(domain.getBody(),InspectionRequest.class);
        if(null==request){
            if(logger.isWarnEnabled()){
                logger.warn(MessageFormat.format("验货JSON解析后对象为空{0}",domain.getBody()));
            }
            return context;
        }
        String code = request.getPackageBarOrWaybillCode();
        // 如果是包裹号获取取件单号，则存在包裹号属性中
        if (BusinessHelper.isPackageCode(code)|| BusinessHelper.isPickupCode(code)) {
            request.setPackageBarcode(code);
        } else if (BusinessHelper.isWaybillCode(code)) {// 否则为运单号
            request.setWaybillCode(code);
        } else {
            if(logger.isErrorEnabled()){
                logger.error(MessageFormat.format("验货条码不符合规则{0}",code));
            }
            return context;
        }
        String waybillCode = BusinessHelper.getWaybillCode(request.getPackageBarOrWaybillCode());
        BigWaybillDto bigWaybillDto = getWaybill(waybillCode);
        resetBusinessType(request,bigWaybillDto);/*验货businessType存在非50的数据吗，需要验证*/
        resetStoreId(request,bigWaybillDto);
        if (StringUtils.isNotBlank(request.getWaybillCode())) {
            if (null == bigWaybillDto||null==bigWaybillDto.getPackageList()||bigWaybillDto.getPackageList().size()==0) {
                if(logger.isErrorEnabled()){
                    logger.error(MessageFormat.format("验货包裹信息为空{0}",code));
                }
                return context;
            }
            List<DeliveryPackageD> packages=bigWaybillDto.getPackageList();
            if (BusinessHelper.checkIntNumRange(packages.size())) {
                for (DeliveryPackageD pack : packages) {
                    request.setPackageBarcode(pack.getPackageBarcode());
                    inspectionList.add(Inspection.toInspection(request));
                }
            }
        } else if (StringUtils.isNotEmpty(request.getPackageBarcode())) {
            if (StringUtils.isBlank(request.getWaybillCode())
                    && !BusinessHelper.isPickupCode(request.getPackageBarcode())) {
                request.setWaybillCode(BusinessHelper.getWaybillCodeByPackageBarcode(request.getPackageBarcode()));
            }
            inspectionList.add(Inspection.toInspection(request));
        }
        context.setPassCheck(true);
        context.setInspectionList( inspectionList);
        Collections.sort(context.getInspectionList());
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
        return false;
    }

    /**
     * pda不再区分用户入口， 所有订单都可以扫描， 由后台获取运单ordertype和storeid 判断以前的类型
     * 2014年12月16日16:21:55 by guoyongzhi
     * @param request
     * @param bigWaybillDto
     */
    private void resetBusinessType(InspectionRequest request,BigWaybillDto bigWaybillDto){
        if(Integer.valueOf(Constants.BUSSINESS_TYPE_NEWTRANSFER).equals(request.getBusinessType())&&bigWaybillDto != null && bigWaybillDto.getWaybill()!=null)//包裹交接类型 以前是 1130： 50库房， 51夺宝岛 52协同仓  现在是50
        {
                if (Integer.valueOf(Constants.BUSSINESS_TYPE_DBD_ORDERTYPE).equals(bigWaybillDto.getWaybill().getWaybillType())) { //夺宝岛交接 51 : 2
                    request.setBusinessType(Constants.BUSSINESS_TYPE_BDB);
                } else if (Integer.valueOf(Constants.BUSSINESS_TYPE_ZY_ORDERTYPE).equals(bigWaybillDto.getWaybill().getWaybillType())) {  //正向库房或者协同仓交接
                    if (isExists(bigWaybillDto.getWaybillState().getStoreId())) {
                        request.setBusinessType(Constants.BUSSINESS_TYPE_OEM_52);
                    } else {
                        request.setBusinessType(Constants.BUSSINESS_TYPE_TRANSFER);
                    }
                }
        }
    }

    private boolean isExists(Integer storeId){
        int value=(null== storeId)?0: storeId.intValue();
        switch (value){
            case 52:
                return true;
            case 54:
                return true;
            case 53:
                return true;
            case 55:
                return true;
            case 56:
                return true;
            case 58:
                return true;
            case 59:
                return true;
            default:
                return false;
        }

    }

    /**
     * 返仓交接 重设库房号至收货站点
     * @param request
     * @param bigWaybillDto
     */
    private void resetStoreId(InspectionRequest request,BigWaybillDto bigWaybillDto){
        if (Integer.valueOf(Constants.BUSSINESS_TYPE_FC).equals(request.getBusinessType().intValue())){
                if(null!=bigWaybillDto
                        &&null!=bigWaybillDto.getWaybill()
                        &&null!=bigWaybillDto.getWaybill().getDistributeStoreId()) {
                    request.setReceiveSiteCode(bigWaybillDto.getWaybill().getDistributeStoreId());
                }
        }
    }
}
