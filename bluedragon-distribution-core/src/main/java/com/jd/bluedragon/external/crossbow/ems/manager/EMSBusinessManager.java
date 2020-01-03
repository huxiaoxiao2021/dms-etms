package com.jd.bluedragon.external.crossbow.ems.manager;

import com.jd.bluedragon.distribution.waybill.domain.WaybillInfo;
import com.jd.bluedragon.external.crossbow.AbstractCrossbowManager;
import com.jd.bluedragon.external.crossbow.ems.domain.EMSResponse;
import com.jd.bluedragon.external.crossbow.ems.domain.EMSWaybillEntityDto;
import com.jd.bluedragon.external.crossbow.ems.domain.EMSWaybillEntityListDto;
import com.jd.bluedragon.external.crossbow.ems.domain.EMSWaybillEntityRequest;

import java.util.Collections;

/**
 * <p>
 *     全国邮政对外接口调用
 *     webService接口
 *
 * @author wuzuxiang
 * @since 2019/12/27
 **/
public class EMSBusinessManager extends AbstractCrossbowManager<EMSWaybillEntityRequest, EMSResponse> {

    private String password;

    private String printKind;

    /**
     * condition 为 WaybillInfo
     * @param condition 相应的条件
     * @see WaybillInfo
     * @return
     */
    @Override
    protected EMSWaybillEntityRequest getMyRequestBody(Object condition) {
        EMSWaybillEntityRequest request = new EMSWaybillEntityRequest();
        request.setPassWord(password);
        request.setPrintKind(printKind);

        WaybillInfo waybillInfo = (WaybillInfo) condition;
        request.setSysAccount(waybillInfo.getSysAccount());

        EMSWaybillEntityDto emsDto = new EMSWaybillEntityDto();
        emsDto.setBusinessType(waybillInfo.getBusinessType());
        emsDto.setDateType("");
        emsDto.setProcdate("");
        emsDto.setScontactor(waybillInfo.getScontactor());
        emsDto.setScustMobile(waybillInfo.getScustMobile());
        emsDto.setScustTelplus("");
        emsDto.setScustPost("");
        emsDto.setScustAddr("<![CDATA[" + waybillInfo.getScustAddr() + "]]");
        emsDto.setScustComp("");
        emsDto.setTcontactor(waybillInfo.getTcontactor());
        emsDto.setTcustMobile(waybillInfo.getTcustMobile());
        emsDto.setTcustTelplus(waybillInfo.getTcustTelplus());
        emsDto.setTcustPost(waybillInfo.getTcustPost());
        emsDto.setTcustAddr("<![CDATA[" +waybillInfo.getTcustAddr() + "]]");
        emsDto.setTcustComp("");
        emsDto.setTcustProvince(waybillInfo.getTcustProvince());
        emsDto.setTcustCity(waybillInfo.getTcustCity());
        emsDto.setTcustCounty(waybillInfo.getTcustCounty());
        emsDto.setWeight(String.valueOf(waybillInfo.getWeight()));
        emsDto.setLength("");
        emsDto.setInsure("");
        emsDto.setFee(String.valueOf(waybillInfo.getFee().intValue()));
        emsDto.setFeeUppercase(waybillInfo.getFeeUppercase());
        emsDto.setCargoDesc("");
        emsDto.setCargoDesc1("");
        emsDto.setCargoDesc2("");
        emsDto.setCargoDesc3("");
        emsDto.setCargoDesc4("");
        emsDto.setCargoType("");
        emsDto.setDeliveryclaim("");
        emsDto.setRemark("");
        emsDto.setBigAccountDataId(waybillInfo.getPackageBarcode());
        emsDto.setCustomerDn(waybillInfo.getWaybillCode());
        emsDto.setSubBillCount(new Integer(0).equals(waybillInfo.getPackageNum())? "" : String.valueOf(waybillInfo.getPackageNum()));
        emsDto.setPayMode(waybillInfo.getPayMode());
        emsDto.setMainBillNo("");
        emsDto.setMainBillFlag("");
        emsDto.setMainSubPayMode("4");
        emsDto.setInsureType("");
        emsDto.setBlank1("");
        emsDto.setBlank2("");
        emsDto.setBlank3("");
        emsDto.setBlank4("");
        emsDto.setBlank5("");

        EMSWaybillEntityListDto listDto = new EMSWaybillEntityListDto();
        listDto.setPrintDatas(Collections.singletonList(emsDto));
        request.setPrintDatas(listDto);
        return request;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPrintKind(String printKind) {
        this.printKind = printKind;
    }
}
