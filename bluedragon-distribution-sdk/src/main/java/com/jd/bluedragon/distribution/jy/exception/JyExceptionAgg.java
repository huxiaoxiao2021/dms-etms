package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;

/**
 * 异常agg
 *
 * @author hujiping
 * @date 2023/3/13 4:13 PM
 */
public class JyExceptionAgg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场地ID
     */
    private Integer siteCode;
    
    /**
     * 网格编码
     */
    private String gridCode;

    /**
     * 分配到网格的异常报废任务数量
     */
    private Integer quantity;

    /**
     * 异常提报人ERP
     */
    private String createUserErp;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }
}
