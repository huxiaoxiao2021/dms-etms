package com.jd.bluedragon.distribution.siteRetake.domain;

import org.apache.poi.hssf.record.formula.functions.T;
import com.jd.common.orm.page.Page;
import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 驻厂再取 查询类
 * @date 2018年08月02日 16时:29分
 */
public class SiteRetakeCondition extends Page
{
    public static final Integer TIME_TYPE_ASSIGNTIME=0;
    public static final Integer TIME_TYPE_ORDERTIME=1;
    //任务时间
    private Date assignTime;
    //下单时间
    private Date waybillCreateTime;
   //商家编码
    private Integer VendorId;
    //站点id
    private Integer siteCode;
    //运单号
   private String waybillCode;

    public Date getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(Date assignTime) {
        this.assignTime = assignTime;
    }

    public Date getWaybillCreateTime() {
        return waybillCreateTime;
    }

    public void setWaybillCreateTime(Date waybillCreateTime) {
        this.waybillCreateTime = waybillCreateTime;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getVendorId() {
        return VendorId;
    }

    public void setVendorId(Integer vendorId) {
        VendorId = vendorId;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

}
