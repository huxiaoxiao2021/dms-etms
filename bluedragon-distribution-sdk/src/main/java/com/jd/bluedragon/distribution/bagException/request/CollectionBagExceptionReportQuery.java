package com.jd.bluedragon.distribution.bagException.request;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 集包异常举报查询
 *
 * @author fanggang7
 * @time 2020-09-23 20:42:02 周三
 */
public class CollectionBagExceptionReportQuery extends BasePagerCondition implements Serializable {
    private static final long serialVersionUID = 8821327385424621084L;


    private Long id;

    /**
     * 区域code
     */
    private Integer orgCode;

    /**
     * 分拣中心
     */
    private Integer siteCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 上游箱号  db_column: upstream_box_code
     */
    private String upstreamBoxCode;
    /**
     * 箱号始发ID  db_column: box_start_id
     */
    private Long boxStartId;
    /**
     * 箱号目的ID  db_column: box_end_id
     */
    private Long boxEndId;

    /**
     * 举报类型
     */
    private Integer reportType;

    /**
     * 创建时间  db_column: create_time
     */
    private Date createTime;
    /**
     * 创建人ERP  db_column: create_user_erp
     */
    private String createUserErp;
    /**
     * 创建人  db_column: create_user_name
     */
    private String createUserName;
    /**
     * 举报时间开始
     */
    private String createTimeFromStr;

    private Date createTimeFrom;

    /**
     * 举报时间结束
     */
    private String createTimeToStr;

    private Date createTimeTo;

    private String currentUserErp;

    private Integer yn;

    private Integer pageSize;

    /**
     * 有序的按字段匹配值条件查询，如{id: [1,2,3], name: ["aaa", "bbb"]}，
     * 表示select * from someTable where id in (1,2,3) and name in ("aaa", "bbb")
     */
    private LinkedHashMap<String, List<Object>> columnValueMap;

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getCreateTimeFromStr() {
        return createTimeFromStr;
    }

    public void setCreateTimeFromStr(String createTimeFromStr) {
        this.createTimeFromStr = createTimeFromStr;
    }

    public Date getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(Date createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public String getCreateTimeToStr() {
        return createTimeToStr;
    }

    public void setCreateTimeToStr(String createTimeToStr) {
        this.createTimeToStr = createTimeToStr;
    }

    public Date getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(Date createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    public String getCurrentUserErp() {
        return currentUserErp;
    }

    public void setCurrentUserErp(String currentUserErp) {
        this.currentUserErp = currentUserErp;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.setLimit(pageSize);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getUpstreamBoxCode() {
        return upstreamBoxCode;
    }

    public void setUpstreamBoxCode(String upstreamBoxCode) {
        this.upstreamBoxCode = upstreamBoxCode;
    }

    public Long getBoxStartId() {
        return boxStartId;
    }

    public void setBoxStartId(Long boxStartId) {
        this.boxStartId = boxStartId;
    }

    public Long getBoxEndId() {
        return boxEndId;
    }

    public void setBoxEndId(Long boxEndId) {
        this.boxEndId = boxEndId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public LinkedHashMap<String, List<Object>> getColumnValueMap() {
        return columnValueMap;
    }

    public void setColumnValueMap(LinkedHashMap<String, List<Object>> columnValueMap) {
        this.columnValueMap = columnValueMap;
    }
}
