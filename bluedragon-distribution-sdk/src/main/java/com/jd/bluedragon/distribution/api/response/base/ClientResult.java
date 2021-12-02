package com.jd.bluedragon.distribution.api.response.base;

import java.io.Serializable;
import java.util.*;

/**
 * 客户端结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-16 20:05:16 周二
 */
public class ClientResult<T> implements Serializable {

    private static final long serialVersionUID = -5851805740711267609L;

    /**
     * 泛型数据
     */
    private T data;

    /**
     * 返回码
     */
    private int code = ResultCodeConstant.SUCCESS;

    /**
     * 消息
     */
    private String message = "";

    /**
     * 消息盒子
     */
    private List<MsgBox> msgBoxes;

    /**
     * 响应时间毫秒级时间戳
     */
    private Long responseTimeMillSeconds;

    /**
     * 响应时间格式化字符串
     */
    private String responseTimeFormative;

    public T getData() {
        return data;
    }

    public ClientResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ClientResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ClientResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getResponseTimeMillSeconds() {
        return responseTimeMillSeconds;
    }

    public ClientResult<T> setResponseTimeMillSeconds(Long responseTimeMillSeconds) {
        this.responseTimeMillSeconds = responseTimeMillSeconds;
        return this;
    }

    public String getResponseTimeFormative() {
        return responseTimeFormative;
    }

    public ClientResult<T> setResponseTimeFormative(String responseTimeFormative) {
        this.responseTimeFormative = responseTimeFormative;
        return this;
    }

    public List<MsgBox> getMsgBoxes() {
        return msgBoxes;
    }

    public ClientResult<T> setMsgBoxes(List<MsgBox> msgBoxes) {
        this.msgBoxes = msgBoxes;
        return this;
    }

    /**
     * 无参构造方法
     */
    public ClientResult() {
        super();
    }

    /**
     * 构造方法
     * @param data 数据
     * @param code 返回码
     * @param message 消息
     */
    public ClientResult(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     * @param data 数据
     */
    public ClientResult(T data) {
        this.data = data;
    }

    /**
     * 构造方法
     * @param code 返回码
     * @param message 消息
     */
    public ClientResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 设置result值
     * @param code 返回码
     * @param message 消息
     */
    public static <T> ClientResult<T> init(int code, String message, T data) {
        ClientResult<T> result = new ClientResult<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 返回成功结果，静态方法
     *
     * @param <T>  返回结果泛型
     * @return Result对象
     */
    public static <T> ClientResult<T> success() {
        ClientResult<T> result = new ClientResult<T>();
        result.setMessage("ok");
        return result;
    }

    /**
     * 返回成功结果，静态方法
     *
     * @param data 数据
     * @param <T>  返回结果泛型
     * @return Result对象
     */
    public static <T> ClientResult<T> success(T data) {
        ClientResult<T> result = new ClientResult<T>();
        result.setData(data);
        result.setMessage("ok");
        return result;
    }

    /**
     * 返回成功结果，静态方法
     *
     * @param data    数据
     * @param message 消息
     * @param <T>     返回结果泛型
     * @return Result对象
     */
    public static <T> ClientResult<T> success(T data, String message) {
        ClientResult<T> result = new ClientResult<T>();
        result.setData(data);
        result.setMessage(message);
        return result;
    }

    /**
     * 返回成功结果，静态方法
     *
     * @param message 消息
     * @param <T>     返回结果泛型
     * @return Result对象
     */
    public static <T> ClientResult<T> success(String message) {
        ClientResult<T> result = new ClientResult<T>();
        result.setMessage(message);
        return result;
    }

    /**
     * 返回失败结果，静态方法
     *
     * @param code    返回码
     * @param message 消息
     * @param <T>     返回结果泛型
     * @return Result对象
     */
    public static <T> ClientResult<T> fail(int code, String message) {
        return new ClientResult<T>(code, message);
    }

    /**
     * 返回失败结果，静态方法
     *
     * @param message 消息
     * @param <T>     返回结果泛型
     * @return Result对象
     */
    public static <T> ClientResult<T> fail(String message) {
        return new ClientResult<T>(ResultCodeConstant.FAIL, message);
    }

    /**
     * 置为成功
     */
    public ClientResult<T> toSuccess() {
        this.code = ResultCodeConstant.SUCCESS;
        this.message = "ok";
        return this;
    }

    /**
     * 置为成功
     */
    public ClientResult<T> toSuccess(String message) {
        this.code = ResultCodeConstant.SUCCESS;
        this.message = message;
        return this;
    }

    /**
     * 置为成功
     */
    public ClientResult<T> toSuccess(T data, String message) {
        this.code = ResultCodeConstant.SUCCESS;
        this.message = message;
        this.data = data;
        return this;
    }

    /**
     * 置为失败
     */
    public ClientResult<T> toFail() {
        this.code = ResultCodeConstant.FAIL;
        this.message = "fail";
        return this;
    }

    /**
     * 置为失败
     */
    public ClientResult<T> toFail(String message, Integer code) {
        this.code = code;
        this.message = message;
        return this;
    }

    /**
     * 置为失败
     */
    public ClientResult<T> toFail(Integer code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }

    /**
     * 置为失败
     */
    public ClientResult<T> toFail(String message, Integer code, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        return this;
    }

    /**
     * 置为失败
     */
    public ClientResult<T> toFail(String message) {
        this.code = ResultCodeConstant.FAIL;
        this.message = message;
        return this;
    }

    /**
     * 是否成功
     * @return boolean 成功标志
     */
    public boolean isSuccess() {
        return this.code == ResultCodeConstant.SUCCESS;
    }

    /**
     * 是否失败
     * @return boolean 失败标志
     */
    public boolean isFail() {
        return !this.isSuccess();
    }

    /**
     * 是否空数据
     * @return boolean 空数据标志，true：空，false：不为空
     */
    public boolean isEmptyData() {
        if (this.data == null) {
            return true;
        }
        if (this.data instanceof Collections) {
            return ((Collection<?>) this.data).size() == 0;
        }
        if (this.data instanceof Map) {
            return ((Map<?, ?>) this.data).size() == 0;
        }
        return false;
    }

    public enum MsgBoxTypeEnum {

        /**
         * 提示
         */
        PROMPT(0, "prompt"),

        /**
         * 警告
         */
        WARNING(1, "warning"),

        /**
         * 确认
         */
        CONFIRM(2, "confirm"),

        /**
         * 拦截
         */
        INTERCEPT(3, "intercept"),
        ;

        public static Map<Integer, String> ENUM_MAP;

        public static List<Integer> ENUM_LIST;

        private Integer type;

        private String name;

        static {
            //将所有枚举装载到map中
            ENUM_MAP = new HashMap<Integer, String>();
            ENUM_LIST = new ArrayList<Integer>();
            for (MsgBoxTypeEnum enumItem : MsgBoxTypeEnum.values()) {
                ENUM_MAP.put(enumItem.getType(), enumItem.getName());
                ENUM_LIST.add(enumItem.getType());
            }
        }

        /**
         * 通过code获取name
         *
         * @param code 编码
         * @return string
         */
        public static String getEnumNameByCode(Integer code) {
            return ENUM_MAP.get(code);
        }

        MsgBoxTypeEnum(Integer type, String name) {
            this.type = type;
            this.name = name;
        }

        public Integer getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }

    public static class MsgBox {

        /**
         * 校验结果类型 1-提示框 2-确认框 3-拦截框
         */
        private Integer type;

        /**
         * 状态码
         */
        private Integer code;

        /**
         * 提示语
         */
        private String msg;

        protected Object data;

        public MsgBox() {
        }

        public MsgBox(Integer type, Integer code, String msg) {
            this.type = type;
            this.code = code;
            this.msg = msg;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    public ClientResult<T> addBox(MsgBox box) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        msgBoxes.add(box);
        return this;
    }

    public ClientResult<T> addBox(Integer type, Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(type, code, msg);
        msgBoxes.add(box);
        return this;
    }

    public ClientResult<T> addPromptBox(Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(MsgBoxTypeEnum.PROMPT.getType(), code, msg);
        msgBoxes.add(box);
        return this;
    }

    public ClientResult<T> addWarningBox(Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(MsgBoxTypeEnum.WARNING.getType(), code, msg);
        msgBoxes.add(box);
        return this;
    }

    public ClientResult<T> addConfirmBox(Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(MsgBoxTypeEnum.CONFIRM.getType(), code, msg);
        msgBoxes.add(box);
        return this;
    }

    public ClientResult<T> addInterceptBox(Integer code, String msg) {
        if (this.getMsgBoxes() == null) {
            msgBoxes = new ArrayList<MsgBox>();
        }
        MsgBox box = new MsgBox(MsgBoxTypeEnum.INTERCEPT.getType(), code, msg);
        msgBoxes.add(box);
        return this;
    }
}
