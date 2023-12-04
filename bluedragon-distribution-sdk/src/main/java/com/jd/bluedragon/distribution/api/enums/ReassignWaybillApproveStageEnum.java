package com.jd.bluedragon.distribution.api.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/6 20:04
 * @Description: 返调度审批阶段枚举
 */
public enum ReassignWaybillApproveStageEnum {

    UNKNOWN(0, 0,  "unKnown", "未知"),
    FIRST(1, 1,  "firstApprove", "一级审批"),
    SECOND(2, 2, "secondApprove", "二级审批"),
    THIRD(3, 3,  "thirdApprove", "三级审批");


    private final int code;

    private final int count;

    private final String nodeCode;

    private final String desc;

    ReassignWaybillApproveStageEnum(int code, int count, String nodeCode, String desc) {
        this.code = code;
        this.count = count;
        this.nodeCode = nodeCode;
        this.desc = desc;
    }

    public static ReassignWaybillApproveStageEnum convertApproveEnum(String nodeCode){
        for (ReassignWaybillApproveStageEnum temp : ReassignWaybillApproveStageEnum.values()){
            if(temp.getNodeCode().equals(nodeCode)){
                return temp;
            }
        }
        return ReassignWaybillApproveStageEnum.UNKNOWN;
    }

    public int getCode() {
        return code;
    }

    public int getCount() {
        return count;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public String getDesc() {
        return desc;
    }
}
