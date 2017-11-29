package com.jd.bluedragon.distribution.kuaiyun.weight.service.impl;

import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WeighByWaybillServiceImpl
{
    private static final Log logger= LogFactory.getLog(WeighByWaybillServiceImpl.class);

    /*运单接口 用于运单校验*/
    @Autowired
    WaybillQueryApi waybillQueryApi;

    public void entry(String codeStr)
    {
      /*1 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号*/
        String waybillCode = null;
        if(BusinessHelper.isPackageCode(codeStr))
        {
            waybillCode = BusinessHelper.getWaybillCodeByPackageBarcode(codeStr);


        }else if(BusinessHelper.isWaybillCode(codeStr))
        {
            waybillCode = codeStr;
        }else
        {
            logger.error("所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则");
        }

      /*2 对运单进行校验*/

        BaseEntity<Waybill> waybillBaseEntity = null;
        try
        {
            waybillBaseEntity = waybillQueryApi.getWaybillByWaybillCode(waybillCode);
        } catch (Exception e)
        {
            WeighByWaybillExcpetion excpetion = new WeighByWaybillExcpetion("");

        } finally
        {

        }

        int resultCode = waybillBaseEntity.getResultCode();
        Waybill waybill = waybillBaseEntity.getData();



//        judgeCodeType();



    }

    public void validWaybillProcess()
    {

    }

    public void invalidWaybillProcess()
    {

    }

//  =====================================================

    public void judgeCodeType()
    {

    }

    public void getWaybillCodeByPackageCode()
    {

    }

    public void validateWaybillCodeReality()
    {

    }
}
