package com.jd.bluedragon.external.crossbow;

/**
 * <p>
 *     序列化和反序列化配置对象
 *     放一些乱七八糟的序列化的
 *
 * @author wuzuxiang
 * @since 2019/12/23
 **/
public class SerializationConfig {

    /**
     * 序列化模式
     */
    private SerializationModeEnum serializationMode;

    /**
     * SOAP时需要
     */
    private String methodName;

    /**
     * SOAP时需要
     */
    private String parameterName;

    /**
     * SOAP时需要
     */
    private String nameSpaceUIR;

    public SerializationModeEnum getSerializationMode() {
        return serializationMode;
    }

    public void setSerializationMode(SerializationModeEnum serializationMode) {
        this.serializationMode = serializationMode;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getNameSpaceUIR() {
        return nameSpaceUIR;
    }

    public void setNameSpaceUIR(String nameSpaceUIR) {
        this.nameSpaceUIR = nameSpaceUIR;
    }
}
