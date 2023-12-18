package com.jd.bluedragon;

/**
 * 流程常量类
 *
 * @author hujiping
 * @date 2021/4/21 5:53 下午
 */
public class FlowConstants {

    /**
     * 流程使用版本号
     */
    public static final Integer FLOW_VERSION = 1;

    /**
     * 流程编码-打印交接清单流程编码
     */
    public static final String FLOW_CODE_PRINT_HANDOVER = "print_handover_list_export_apply";
    
    /**
     * 流程编码-签到数据修改审批
     */
    public static final String FLOW_CODE_SIGN_MODIFY = "sign_modify_approve";

    /**
     * 流程编码-生鲜报废流程编码
     */
    public static final String FLOW_CODE_FRESH_SCRAP = "fresh_scrap_approve";

    /**
     * 流程编码-返调度流程编码
     */
    public static final String FLOW_CODE_REASSIGN_WAYBILL = "reassign_waybill_approve";

    /**
     * 流程业务数据唯一key
     */
    public static final String FLOW_BUSINESS_NO_KEY = "businessNoKey";
    /**
     * 流程业务数据查询条件-key
     */
    public static final String FLOW_BUSINESS_QUERY_CONDITION = "queryCondition";

    /**
     * 流程-OA数据-申请单主题key
     */
    public static final String FLOW_OA_JMEREQNAME = "jmeReqName";
    /**
     * 流程-OA数据-申请单备注key
     */
    public static final String FLOW_OA_JMEREQCOMMENTS = "jmeReqComments";

    /**
     * 申请单主题-发货交接清单
     */
    public static final String FLOW_FLOW_WORK_THEME_PRINT_HANDOVER = "发货交接清单敏感数据导出申请单";
    /**
     * 申请单备注-发货交接清单
     */
    public static final String FLOW_FLOW_WORK_REMARK_PRINT_HANDOVER = "三方目的地的发货交接清单敏感字段导出申请";

    /**
     * 申请单主题-生鲜报废申请
     */
    public static final String FLOW_FLOW_WORK_THEME_FRESH_SCRAP = "生鲜报废申请单";
    /**
     * 申请单备注-生鲜报废申请
     */
    public static final String FLOW_FLOW_WORK_REMARK_FRESH_SCRAP = "生鲜报废申请";


    /**
     * 申请单主题-返调度申请
     */
    public static final String FLOW_FLOW_WORK_THEME_REASSIGN_WAYBILL = "返调度申请单";
    /**
     * 申请单备注-返调度申请
     */
    public static final String FLOW_FLOW_WORK_REMARK_REASSIGN_WAYBILL = "返调度申请";


    /**
     * 流程-OA数据-申请单主表数据key
     */
    public static final String FLOW_OA_JMEMAINCOLLIST = "jmeMainColList";
    /**
     * 流程-OA数据-申请单附件key
     */
    public static final String FLOW_OA_ANNEX = "jmeFiles";

    /**
     * 流程-实例上下文KEY：oa
     */
    public static final String FLOW_DATA_MAP_KEY_OA = "oa";
    /**
     * 流程-实例上下文KEY：bussinessData
     */
    public static final String FLOW_DATA_MAP_KEY_BUSINESS_DATA = "bussinessData";
    /**
     * 流程-实例上下文KEY：flowControl
     */
    public static final String FLOW_DATA_MAP_KEY_FLOW_CONTROL = "flowControl";

    // 生鲜报废流程控制key：审批次数
    public static final String FLOW_DATA_MAP_SCRAP_COUNT = "approveCount";
    // 生鲜报废流程控制key：审批一级触发人ERP
    public static final String FLOW_DATA_MAP_FIRST_TRIGGER_ERP = "firstTriggerErp";
    // 生鲜报废流程控制key：审批二级触发人ERP
    public static final String FLOW_DATA_MAP_SECOND_TRIGGER_ERP = "secondTriggerErp";
    // 生鲜报废流程控制key：审批三级触发人ERP
    public static final String FLOW_DATA_MAP_THIRD_TRIGGER_ERP = "thirdTriggerErp";


    // 返调度流程控制key：审批次数
    public static final String FLOW_DATA_MAP_REASSIGN_WAYBILL_APPROVE_COUNT = "reassignWaybillApproveCount";
    // 返调度流程控制key：审批一级触发人ERP
    public static final String FLOW_DATA_MAP_FIRST_REASSIGN_WAYBILL_APPROVE_TRIGGER_ERP = "reassignWaybillApproveFirstTriggerErp";
    // 返调度流程控制key：审批二级触发人ERP
    public static final String FLOW_DATA_MAP_SECOND_REASSIGN_WAYBILL_APPROVE_TRIGGER_ERP = "reassignWaybillApproveSecondTriggerErp";
}
