package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 异常-报废审批阶段枚举
 *
 * @author hujiping
 * @date 2023/3/13 6:42 PM
 */
public enum JyExScrapApproveStageEnum {

    UNKNOWN(0, 0,  "unKnown", "未知"),
    FIRST(1, 1,  "firstApprove", "一级审批"),
    SECOND(2, 2, "secondApprove", "二级审批"),
    THIRD(3, 3,  "thirdApprove", "三级审批");


    private final int code;
    
    private final int count;
    
    private final String nodeCode;

    private final String desc;

    JyExScrapApproveStageEnum(int code, int count, String nodeCode, String desc) {
        this.code = code;
        this.count = count;
        this.nodeCode = nodeCode;
        this.desc = desc;
    }
    
    public static JyExScrapApproveStageEnum convertApproveEnum(String nodeCode){
        for (JyExScrapApproveStageEnum temp : JyExScrapApproveStageEnum.values()){
            if(temp.getNodeCode().equals(nodeCode)){
                return temp;
            }
        }
        return JyExScrapApproveStageEnum.UNKNOWN;
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
