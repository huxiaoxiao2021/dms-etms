package com.jd.bluedragon.distribution.external.constants;

/**
 * 操作箱节点枚举值
 */
public enum OpBoxNodeEnum {
    //1000-2000 分拣使用
    PRINTBOXCODE(1001,"箱号打印"),
    SEND(1002,"分拣发货"),
    CANCELSEND(1003,"取消发货"),
    //2000-3000 终端使用
    PRESEND(2001,"预发货"),
    SEALBOX(2002,"封箱");
    private Integer nodeCode;

    private String nodeName;

    public Integer getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(Integer nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    OpBoxNodeEnum(Integer nodeCode, String nodeName) {
        this.nodeCode = nodeCode;
        this.nodeName = nodeName;
    }
}
