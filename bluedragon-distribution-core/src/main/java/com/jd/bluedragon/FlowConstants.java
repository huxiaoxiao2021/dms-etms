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
     * 流程-OA数据-申请单主表数据key
     */
    public static final String FLOW_OA_JMEMAINCOLLIST = "jmeMainColList";

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
}
