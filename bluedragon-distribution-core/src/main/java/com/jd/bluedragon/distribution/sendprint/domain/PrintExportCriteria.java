package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/4/30 9:27 上午
 */
public class PrintExportCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作人ERP
     */
    private String userCode;

    /**
     * 始发站点
     */
    private Integer createSiteCode;

    /**
     * 多个目的地的查询条件
     */
    private List<PrintQueryCriteria> list;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public List<PrintQueryCriteria> getList() {
        return list;
    }

    public void setList(List<PrintQueryCriteria> list) {
        this.list = list;
    }
}
