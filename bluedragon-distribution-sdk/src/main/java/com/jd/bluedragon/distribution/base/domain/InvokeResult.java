package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;

/**
 * Created by wangtingwei on 2014/9/4.
 */
public class InvokeResult<T> implements Serializable {

    public static final int RESULT_NULL_CODE=0;
    public static final String  RESULT_NULL_MESSAGE="结果为空！";

    public static final int RESULT_SUCCESS_CODE=200;
    public static final String RESULT_SUCCESS_MESSAGE="OK";

    public static final int SERVER_ERROR_CODE=500;
    public static final String SERVER_ERROR_MESSAGE="服务器执行异常";

    public static final int RESULT_PARAMETER_ERROR_CODE=400;
    /**
     * 第三方接口异常
     */
    public static final int RESULT_THIRD_ERROR_CODE=401;
    public static final String PARAM_ERROR = "参数错误";

    public static final int RESULT_MULTI_ERROR=600;
    public static final String MULTI_ERROR = "数据已存在";

    public static final Integer RESULT_INTERCEPT_CODE = 300;
    public static final String RESULT_INTERCEPT_MESSAGE = "运单号:{0}，根据重量体积信息已经转至C网进行后续操作，请操作【包裹补打】更换面单，否则无法操作建箱及发货";

    public static final int RESULT_BOX_SENT_CODE=301;
    public static final String RESULT_BOX_SENT_MESSAGE = "该箱号已发货，不能再绑定集包袋";

    public static final int RESULT_PACKAGE_ALREADY_BIND=302;





    public static final int RESULT_NULL_WAYBILLCODE_CODE=201;
    public static final String RESULT_NULL_WAYBILLCODE_MESSAGE = "无运单数据";

    public static final int RESULT_BOX_EMPTY_CODE=303;
    public static final String RESULT_BOX_EMPTY_MESSAGE = "该箱号无关联的包裹号，请拆包称重";

    public static final int RESULT_NO_BOX_CODE=304;
    public static final String RESULT_NO_BOX_MESSAGE = "箱号:{0}，箱号不合法";

    public static final Integer CODE_CONFIRM = 30001;
    //普通拦截
    public static final Integer CODE_HINT = 30002;
    //特殊展示拦截
    public static final Integer CODE_SPECIAL_INTERCEPT = 30003;

    public static final int RESULT_BC_BOX_NO_BINDING_CODE= 305;
    public static final String RESULT_BC_BOX_NO_BINDING_MESSAGE ="该箱号未绑定循环集包袋";

    public static final int RESULT_BC_BOX_GROUP_NO_BINDING_CODE= 306;
    public static final String RESULT_BC_BOX_GROUP_NO_BINDING_MESSAGE = "同组箱号中存在未绑定循环集包袋";

    public static final int RESULT_NO_GROUP_CODE = 307;
    public static final String RESULT_NO_GROUP_MESSAGE = "查询同组箱号异常";


    public static final int RESULT_EXPORT_CODE = 308;
    public static final String RESULT_EXPORT_MESSAGE = "导出执行异常";

    public static final int RESULT_EXPORT_LIMIT_CODE = 309;
    public static final String RESULT_EXPORT_LIMIT_MESSAGE="导出调用繁忙,请稍后重试";

    public static  final int RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_CODE = 310;
    public static final  String RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_MESSAGE= "校验导出并发接口异常";

    public static final int RESULT_RFID_BIND_BOX_SENT_CODE=311;
    public static final String RESULT_RFID_BIND_BOX_SENT_MESSAGE="该循环集包袋已绑定箱号已发货，不能更换";

    public static final int RESULT_NODATA_GETCARTYPE_CODE=312;
    public static final String RESULT_NODATA_GETCARTYPE_MESSAGE="查询运输车型列表数据异常！";

    public static final int RESULT_NO_FOUND_DATA_CODE=313;
    public static final String RESULT_NO_FOUND_DATA_MESSAGE="未查询到相关流向任务数据！";

    public static final int RESULT_EXE_GETCARINFO_BYCARNO_CODE=314;
    public static final String RESULT_EXE_GETCARINFO_BYCARNO_MESSAGE="根据车牌号查询运输任务详情异常！";

    public static final int RESULT_NO_FOUND_BY_TRANS_WOEK_ITEM_CODE=315;
    public static final String RESULT_NO_FOUND_BY_TRANS_WOEK_ITEM_MESSAGE="根据派车明细单号未查询到相关运力信息！";

    public static final int RESULT_NO_FOUND_BY_BOARD_CODE=316;
    public static final String RESULT_NO_FOUND_BY_BOARD_MESSAGE="未查询到相关的组板信息！";

    public static final int BOX_NO_FOUND_BY_PACKAGE_CODE=317;
    public static final String BOX_NO_FOUND_BY_PACKAGE_MESSAGE="未根据包裹号查询到相关的箱号信息！";

    public static final int NO_SEND_DATA_UNDER_TASK_CODE=318;
    public static final String NO_SEND_DATA_UNDER_TASK_MESSAGE="该任务下未找到相应的发货数据！";

    public static final int NO_SEND_DATA_UNDER_BOARD_CODE=319;
    public static final String NO_SEND_DATA_UNDER_BOARD_MESSAGE="未找到相应的按板发货数据！";

    public static final int NO_SEND_CODE_DATA_UNDER_BIZTASK_CODE=320;
    public static final String NO_SEND_CODE_DATA_UNDER_BIZTASK_MESSAGE="未找到相应的发货数据，请前去扫描发货";

    public static final int COMMIT_SEAL_CAR_EXCEPTION_CODE=321;
    public static final String COMMIT_SEAL_CAR_EXCEPTION_MESSAGE="提交封车异常";
    public static final String COMMIT_SEAL_CAR_NO_SEAL_CODES_MESSAGE="请扫描或输入封签号，整车类型封签号为必填项";

    public static final int DETAIL_TASK_NO_FOUND_BY_MAIN_TASK_CODE=322;
    public static final String DETAIL_TASK_NO_FOUND_BY_MAIN_TASK_MESSAGE="数据异常，该自建任务下未找到有效的流向任务数据！";

    public static final int DETAIL_TASK_NO_FOUND_BY_SITE_ID_CODE=323;
    public static final String DETAIL_TASK_NO_FOUND_BY_SITE_ID_MESSAGE="未找到匹配的流向任务数据！";

    public static final int BATCH_SIGLE_SEND_EXCEP_CODE= 325;
    public static final String BATCH_SIGLE_SEND_EXCEP_MESSAGE ="批量一车一单发货异常";

    public static final int NOT_SUPPORT_MAIN_LINE_TASK_CODE= 328;
    public static final String NOT_SUPPORT_MAIN_LINE_TASK_MESSAGE ="干支批次禁止使用传摆发货！";

    public static final int NO_RE_DETELE_TASK_CODE= 329;
    public static final String NO_RE_DETELE_TASK_MESSAGE ="请勿重复删除同一个任务！";

    public static final int NO_RE_BIND_TASK_CODE= 330;
    public static final String NO_RE_BIND_TASK_MESSAGE ="该自建任务已绑定过运输任务，请勿重复绑定！";

    public static final int NO_SCAN_AFTER_BIND_TASK_CODE= 331;
    public static final String NO_SCAN_AFTER_BIND_TASK_MESSAGE ="该自建任务已绑定任务，无法继续扫描，请选择绑定的任务进入操作！";

    public static final int FORBID_BIND_FOR_SEALED_DETAIL_CODE= 332;
    public static final String FORBID_BIND_FOR_SEALED_DETAIL_MESSAGE ="该自建任务发货批次已封车，不允许绑定！";

    public static final int FORBID_BIND_TO_SEALED_DETAIL_CODE= 333;
    public static final String FORBID_BIND_TO_SEALED_DETAIL_MESSAGE ="该运输任务发货批次已封车，不允许绑定！";

    public static final int FORBID_TRANS_FRROM_SEALED_DETAIL_CODE= 334;
    public static final String FORBID_TRANS_FRROM_SEALED_DETAIL_MESSAGE ="迁出任务已封车，不允许迁移！";

    public static final int FORBID_TRANS_TO_SEALED_DETAIL_CODE= 335;
    public static final String FORBID_TRANS_TO_SEALED_DETAIL_MESSAGE ="迁入任务已封车，不允许迁移！";

    public static final int FORBID_SENDCODE_OF_OTHER_DETAIL_CODE= 337;
    public static final String FORBID_SENDCODE_OF_OTHER_DETAIL_MESSAGE ="此批次号不属于当前任务流向，禁止录入！";

    public static final int FORBID_TRANS_FOR_EMPTY_BATCH_CODE= 336;
    public static final String FORBID_TRANS_FOR_EMPTY_BATCH_MESSAGE ="空批次禁止迁出！";

    public static final int TASK_NO_FOUND_BY_STATUS_CODE=325;
    public static final String TASK_NO_FOUND_BY_STATUS_MESSAGE="未找到相应状态下的任务数据！";

    public static final int TASK_NO_FOUND_BY_PARAMS_CODE=326;
    public static final String TASK_NO_FOUND_BY_PARAMS_MESSAGE="未找到相应的卸车任务数据！";

    public static final int NOT_SUPPORT_TYPE_QUERY_CODE=327;
    public static final String NOT_SUPPORT_TYPE_QUERY_MESSAGE="暂不支持该类型的统计数据查询！";

    public static final int UNLOAD_SCAN_EXCEPTION_CODE=328;
    public static final String UNLOAD_SCAN_EXCEPTION_MESSAGE="卸车扫描异常！";

    public static final int EASY_FROZEN_TIPS_CODE= 340;
    public static final String EASY_FROZEN_TIPS_MESSAGE ="此运单为易冻品!";

    public static final int EASY_FROZEN_TIPS_STORAGE_CODE= 341;
    public static final String EASY_FROZEN_TIPS_STORAGE_MESSAGE ="此运单为易冻品，请放至保温储存区等待发货!";


    public static final int LUXURY_SECURITY_TIPS_CODE= 342;

    public static final String LUXURY_SECURITY_TIPS_MESSAGE ="此运单为特保单，请对包裹进行拍照!";

    public static final int DP_SPECIAL_CODE = 32003;
    public static final String DP_SPECIAL_HINT_MESSAGE= "您扫描的{0}订单是转德邦订单，请单独码放并放置德邦货区，谢谢。";

    public static final int GET_TRANSPORT_RESOURCE_CODE= 329;
    public static final String GET_TRANSPORT_RESOURCE_MESSAGE ="查询运力信息结果为空！";

    public static final int NO_FOUND_INCOMPELETE_DATA_CODE= 330;
    public static final String NO_FOUND_INCOMPELETE_DATA_MESSAGE ="未找到符合要求的数据！";

    public static final int SENDCODE_TRANSPOST_NOT_UNIFIED_CODE = 331;
    public static final String SENDCODE_TRANSPOST_NOT_UNIFIED_MESSAGE ="批次号与运力编码目的地不一致，请核准后重新操作!";

    public static final int UPDATE_CTT_GROUP_LIST_CODE = 3039;
    public static final String UPDATE_CTT_GROUP_LIST_MESSAGE= "更新流向异常！";

    public static final int NO_SEND_FLOW_CODE = 3040;
    public static final String NO_SEND_FLOW_MESSAGE= "未获取到流向信息!";

    public static final int BOARD_INFO_CODE = 3041;
    public static final String BOARD_INFO_MESSAGE= "获取板的详细信息异常！";

    public static final int SEND_FLOE_CTT_CODE = 3042;
    public static final String SEND_FLOE_CTT_MESSAGE= "获取滑道笼车信息异常！";

    public static final int SEND_FLOE_CTT_GROUP_CODE = 3043;
    public static final String SEND_FLOE_CTT_GROUP_MESSAGE= "未获取到相应的混扫任务信息！";

    public static final int PACKAGE_OR_BOX_UNDER_BOARD_CODE = 3044;
    public static final String PACKAGE_OR_BOX_UNDER_BOARD_MESSAGE = "获取组板下的包裹号|运单号|箱号异常！";

    public static final int CANCEL_COM_BOARD_CODE = 3045;
    public static final String CANCEL_COM_BOARD_MESSAGE = "取消组板失败！";

    public static final int FINISH_BOARD_AGAIN_CODE = 3046;
    public static final String FINISH_BOARD_AGAIN_MESSAGE = "该板已完结，请刷新页面！";

    public static final int FINISH_BOARD_CODE = 3047;
    public static final String FINISH_BOARD_MESSAGE = "完结组板失败！";

    public static final int HAVE_IN_HAND_BOARD_CODE = 3049;
    public static final String HAVE_IN_HAND_BOARD_MESSAGE = "当前存在扫描中的板，请勿移除！";

    public static final int NOT_BELONG_THIS_HUNSAO_TASK_CODE = 3050;
    public static final String NOT_BELONG_THIS_HUNSAO_TASK_MESSAGE = "非本混扫任务流向货物！";

    public static final int NOT_BELONG_THIS_SENDFLOW_CODE = 3051;
    public static final String NOT_BELONG_THIS_SENDFLOW_MESSAGE = "非本流向货物！";

    public static final int NOT_CONSISTENT_WHIT_LAST_SENDFLOW_CODE = 3052;
    public static final String NOT_CONSISTENT_WHIT_LAST_SENDFLOW_MESSAGE = "与上单流向不一致！";

    public static final int BOARD_HAS_BEEN_FULL_CODE = 3053;
    public static final String BOARD_HAS_BEEN_FULL_MESSAGE = "已达上限，需要换新板！";

    public static final int HAVE_SEND_FLOW_UNDER_GROUP_CODE = 3054;
    public static final String HAVE_SEND_FLOW_UNDER_GROUP_MESSAGE = "当前混扫任务以包含该流向，请勿重复添加！";

    public static final int SEND_FLOW_UNDER_GROUP_CODE = 3055;
    public static final String SEND_FLOW_UNDER_GROUP_MESSAGE = "获取当前混扫任务流向失败！";

    public static final int CHECK_BARCODE_CODE = 3056;
    public static final String CHECK_BARCODE_MESSAGE = "请输入正确的箱号|包裹号|滑道笼车号";


    public static final int NOT_FIND_BOARD_INFO_CODE = 3057;
    public static final String NOT_FIND_BOARD_INFO_MESSAGE= "未找到对应的板信息！";

    public static final int  CREATE_GROUP_CTT_DATA_CODE = 3058;
    public static final String CREATE_GROUP_CTT_DATA_MESSAGE = "保存本场地常用的笼车集合失败！";

    public static final int NOT_FIND_CTT_CODE = 3059;
    public static final String NOT_FIND_CTT_MESSAGE= "未获取到滑道笼车信息！";

    public static final int BOARD_HAVE_SEAL_CAR_CODE = 3060;
    public static final String BOARD_HAVE_SEAL_CAR_MESSAGE= "该批次已经封车，不能操作取消组板！";

    public static final int QUERY_EXCEPTION_REPORT_CODE = 343;
    public static final String QUERY_EXCEPTION_REPORT_MESSAGE = "查询异常提报数据失败！";
    
    public static final int REVOKE_EXCEPTION_REPORT_CODE = 344;
    public static final String REVOKE_EXCEPTION_REPORT_MESSAGE = "撤销封签异常提报失败！";

    public InvokeResult(){
        this.code=RESULT_SUCCESS_CODE;
    }

    public InvokeResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public InvokeResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 状态码
     */
    private int code;

    /**
     * 状态描述
     */
    private String message;

    /**
     * 执行结果
     */
    private T data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void success(){
        this.code=RESULT_SUCCESS_CODE;
        this.message=RESULT_SUCCESS_MESSAGE;
    }

    /**
     * 发生异常
     * @param ex
     */
    public void error(Throwable ex){
        this.code=SERVER_ERROR_CODE;
        this.message= SERVER_ERROR_MESSAGE;
    }

    /**
     * 发生异常
     */
    public void error() {
        this.code = SERVER_ERROR_CODE;
        this.message= SERVER_ERROR_MESSAGE;
    }

    /**
     * 发生异常
     * @param message
     */
    public void error(String message){
        this.code=SERVER_ERROR_CODE;
        this.message= message;
    }

    /**
     * 参数错误
     * @param message
     */
    public void parameterError(String message){
        this.code=RESULT_PARAMETER_ERROR_CODE;
        this.message=message;
    }

    /**
     * 参数错误
     */
    public void parameterError() {
        this.code = RESULT_PARAMETER_ERROR_CODE;
        this.message = PARAM_ERROR;
    }

    /**
     * 设置用户自定义消息
     * @param code      消息代号
     * @param message   消息内容
     */
    public void customMessage(int code,String message){
        this.code=code;
        this.message=message;
    }

    /**
     * 设置确认自定义消息
     * @param message   消息内容
     */
    public void confirmMessage(String message){
        this.code = CODE_CONFIRM;
        this.message = message;
    }

    /**
     * 设置确认自定义消息
     * @param message   消息内容
     */
    public void hintMessage(String message){
        this.code = CODE_HINT;
        this.message = message;
    }

    /**
     * 成功
     * @return
     */
    public boolean codeSuccess() {
        return this.code == RESULT_SUCCESS_CODE;
    }
}
