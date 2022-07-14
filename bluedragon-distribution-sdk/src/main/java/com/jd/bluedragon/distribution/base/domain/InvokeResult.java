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
    public static final Integer CODE_HINT = 30002;

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

    public static final int BATCH_SIGLE_SEND_EXCEP_CODE= 312;
    public static final String BATCH_SIGLE_SEND_EXCEP_MESSAGE ="批量一车一单发货异常";

    public InvokeResult(){
        this.code=RESULT_SUCCESS_CODE;
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
