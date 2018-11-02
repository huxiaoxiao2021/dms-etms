package com.jd.bluedragon.distribution.sorting.domain;

import java.util.Date;

import com.jd.bluedragon.distribution.api.request.ReturnsRequest;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnServiceImple;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;

/**
 * 分拣退货
 */
public class SortingReturn {

    /** 全局唯一ID */
    private Long id;

    /** 运单号 */
    private String waybillCode;

    /** 包裹号 */
    private String packageCode;

    /** 操作人编号_ERP帐号 */
    private Integer userCode;

    /** 操作人姓名 */
    private String userName;

    /** 操作人所属站点编号 */
    private Integer siteCode;

    /** 操作人所属站点编号 */
    private String siteName;

    //    /** PDA操作时间 */
    //    private Date operateTime;

    /** 系统处理时间 */
    private Date createTime;

    /** 系统处理时间 */
    private Date updateTime;

    /** 操作状态 '0' 未处理，'1'已处理 */
    private Integer status;

    /** 是否删除 */
    private Integer yn;

    /** 分拣业务类型 '10' 正向 '20' 逆向 '30' 三方 '40' POP */
    private Integer businessType;

    /** 错误类型 */
    private String shieldsError;

    /** 错误类型 */
    private Integer shieldsType;

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return this.packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getUserCode() {
        return this.userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSiteCode() {
        return this.siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getYn() {
        return this.yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Date getCreateTime() {
        return createTime!=null?(Date)createTime.clone():null;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime!=null?(Date)createTime.clone():null;
    }

    public Date getUpdateTime() {
        return updateTime!=null?(Date)updateTime.clone():null;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
    }

    public String getShieldsError() {
        return this.shieldsError;
    }

    public void setShieldsError(String shieldsError) {
        this.shieldsError = shieldsError;
    }

    public Integer getShieldsType() {
        return this.shieldsType;
    }

    public void setShieldsType(Integer shieldsType) {
        this.shieldsType = shieldsType;
    }

    /**
     * 将ReturnsRequest转换为Returns 根据PackageCode设置包裹号和运单号 (如果是包裹号，则设置包裹号和运单号；如果是运单号，则只设置运单号)
     * 
     * @param  request
     * @return Returns
     */
    public static SortingReturn parse(ReturnsRequest request) {
        SortingReturn sortingReturn = new SortingReturn();
        sortingReturn.setBusinessType(request.getBusinessType());//分拣业务类型
        sortingReturn.setSiteCode(request.getSiteCode()); //站点编号
        sortingReturn.setSiteName(request.getSiteName());//站点名称
        sortingReturn.setUserCode(request.getUserCode());//用户编码
        sortingReturn.setUserName(request.getUserName());//用户名称
        sortingReturn.setShieldsError(request.getShieldsError()); //错误类型
        if(request.getShieldsError()!=null && request.getShieldsError().startsWith(SortingReturnServiceImple.ABNORMAL)){
        	sortingReturn.setShieldsType(SortingReturnServiceImple.ABNORMAL_INTEGER);
        }
        

        sortingReturn.setCreateTime(DateHelper.parseDateTime(request.getOperateTime()));
        sortingReturn.setUpdateTime(DateHelper.parseDateTime(request.getOperateTime()));
        
        
        //设置包裹号和运单号
        String aPackageCode = request.getPackageCode();
        if (WaybillUtil.isPackageCode(aPackageCode)) {
            sortingReturn.setPackageCode(aPackageCode);
            sortingReturn.setWaybillCode(WaybillUtil.getWaybillCode(aPackageCode));
        } else if (WaybillUtil.isWaybillCode(aPackageCode)) {
        	sortingReturn.setPackageCode(WaybillUtil.getWaybillCode(aPackageCode));
            sortingReturn.setWaybillCode(WaybillUtil.getWaybillCode(aPackageCode));
        }

        return sortingReturn;
    }
}
