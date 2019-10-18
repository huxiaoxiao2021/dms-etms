package com.jd.bluedragon.common.dto.basedict.response;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/9/24
 */
public class BaseDict implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 分类名称 */
    private String typeName;

    /** 分类编号 */
    private Integer typeCode;

    /** 备注 */
    private String memo;

    /** 与type_code进行关联 */
    private Integer parentId;

    /** 节点级别 */
    private Integer nodeLevel;

    /** 类型分类 */
    private Integer typeGroup;

    /** 排序值 */
    private Integer orderNum;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeLevel(Integer nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    public Integer getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(Integer typeGroup) {
        this.typeGroup = typeGroup;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
