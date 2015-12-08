package com.jd.bluedragon.distribution.waybill.domain;

import com.jd.bluedragon.distribution.api.JdResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by yanghongqiang on 2015/11/30.
 */
public class BaseResponseIncidental<T> extends JdResponse {

    /**
     *
     */
    private static final long serialVersionUID = 7201922410022373348L;

    public static final Log log = LogFactory.getLog(BaseResponseIncidental.class);

    public static final String LOG_PREFIX="对外接口返回[BaseResponseIncidental] ";

    private T data;

    private String jsonData;

    public BaseResponseIncidental() {
        super();
    }

    public BaseResponseIncidental(Integer code, String message) {
        super(code, message);
    }

    public BaseResponseIncidental(Integer code, String message, T data) {
        super(code, message);
        this.data = data;
    }

    public BaseResponseIncidental(Integer code, String message, T data,
                                  String jsonData) {
        super(code, message);
        this.data = data;
        this.jsonData = jsonData;
        log.info(new StringBuilder(LOG_PREFIX).append("---(").append(data.getClass().getName()).append("--").append(jsonData));
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public String toString() {
        return "BaseResponseIncidental [data=" + data + ", jsonData="
                + jsonData + ", getCode()=" + getCode() + ", getMessage()="
                + getMessage() + "]";
    }

}
