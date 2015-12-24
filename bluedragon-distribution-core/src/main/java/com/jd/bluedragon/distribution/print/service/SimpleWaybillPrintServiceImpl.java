package com.jd.bluedragon.distribution.print.service;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by wangtingwei on 2015/12/23.
 */
public class SimpleWaybillPrintServiceImpl implements WaybillPrintService {

    private static final Log logger= LogFactory.getLog(SimpleWaybillPrintServiceImpl.class);

    @Autowired
    private WaybillQueryApi waybillQueryApi;

    private List<ComposeService> composeServiceList;


    @Override
    public InvokeResult<PrintWaybill> getPrintWaybill(Integer dmsCode, String waybillCode, Integer targetSiteCode) {

        InvokeResult<PrintWaybill> result=new InvokeResult<PrintWaybill>();
        if(null!=result.getData()){
            for (ComposeService service:composeServiceList){
                service.handle(result.getData(),dmsCode,targetSiteCode);
            }
        }
        return result;
    }

    /**
     * 加载运单基础数据
     * @param result
     * @param dmsCode
     * @param waybillCode
     * @param targetSiteCode
     */
    private final void loadWaybillInfo(final InvokeResult<PrintWaybill> result,Integer dmsCode,String waybillCode,Integer targetSiteCode){
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryPackList(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(waybillCode, wChoice);
        if (baseEntity != null
                && baseEntity.getData() != null
                &&null!=baseEntity.getData().getWaybill()
                &&null!=baseEntity.getData().getWaybillState()) {
            if(null==result.getData()){
                result.setData(new PrintWaybill());
            }
            PrintWaybill commonWaybill=result.getData();
            com.jd.etms.waybill.domain.Waybill tmsWaybill=baseEntity.getData().getWaybill();
            WaybillManageDomain tmsWaybillManageDomain=baseEntity.getData().getWaybillState();
            commonWaybill.setWaybillCode(tmsWaybill.getWaybillCode());
            commonWaybill.setPopSupId(tmsWaybill.getConsignerId());
            commonWaybill.setPopSupName(tmsWaybill.getConsigner());
            commonWaybill.setBusiId(tmsWaybill.getBusiId());
            commonWaybill.setBusiName(tmsWaybill.getBusiName());
            commonWaybill.setQuantity(tmsWaybill.getGoodNumber());

        }
    }

    /**
     * 加载已打印记录【标签打印与发票打印】
     * @param printWaybill
     */
    private final void loadPrintedData(final PrintWaybill printWaybill){

    }

    public List<ComposeService> getComposeServiceList() {
        return composeServiceList;
    }

    public void setComposeServiceList(List<ComposeService> composeServiceList) {
        this.composeServiceList = composeServiceList;
    }
}
