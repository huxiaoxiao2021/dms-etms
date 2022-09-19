package com.jd.bluedragon.core.security.log.enums;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log.domain
 * @ClassName: SecurityLogOpEnums
 * @Description:
 * 0：添加
 * 1：删除
 * 2：更新
 * 3：查询-无明文敏感信息显示（敏感信息脱敏显示或无敏感信息输出）
 * 4：导出-无明文敏感信息显示（敏感信息脱敏显示或无敏感信息输出）
 * 5：查询-有明文敏感信息显示（点击查看为敏感信息明文或记录列表包含敏感信息明文）
 * 6：导入
 * 7:导出-有明文敏感信息显示（导出文件中包含敏感信息明文）
 * 8：打印-有明文敏感信息显示
 * 9：打印-无明文敏感信息显示
 * 10：打印预览-有明文敏感信息显示
 * 11：打印预览-无明文敏感信息显示
 *
 * @Author： wuzuxiang
 * @CreateDate 2022/9/6 16:57
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum SecurityLogOpEnums {
    op_1(1,"删除"),
    op_2(2,"更新"),
    op_3(3,"查询-无明文敏感信息显示（敏感信息脱敏显示或无敏感信息输出）"),
    op_4(4,"导出-无明文敏感信息显示（敏感信息脱敏显示或无敏感信息输出）"),
    op_5(5,"查询-有明文敏感信息显示（点击查看为敏感信息明文或记录列表包含敏感信息明文）"),
    op_6(6,"导入"),
    op_7(7,"导出-有明文敏感信息显示（导出文件中包含敏感信息明文）"),
    op_8(8,"打印-有明文敏感信息显示"),
    op_9(9,"打印-无明文敏感信息显示"),
    op_10(10, "打印预览-有明文敏感信息显示"),
    op_11(11, "打印预览-无明文敏感信息显示");

    private Integer opCode;

    private String opName;

    public Integer getOpCode() {
        return opCode;
    }

    public void setOpCode(Integer opCode) {
        this.opCode = opCode;
    }

    SecurityLogOpEnums(Integer opCode, String opName) {
        this.opCode = opCode;
        this.opName = opName;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }
}
