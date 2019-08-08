package com.jd.bluedragon.distribution.alliance;

import java.io.Serializable;

public class AllianceBusiDeliveryDetailDto implements Serializable{

    private static final long serialVersionUID = 1L;

    /* 运单号 或者 包裹号 */
    public String opeCode;

    /*重量 单位kg*/
    private Double weight;

    /*体积 单位立方厘米 */
    private Double volume;

    /*长 单位厘米 */
    private Double length;

    /*宽 单位厘米 */
    private Double width;

    /*高 单位厘米 */
    private Double height;

    /*操作时间 毫秒级别时间戳*/
    private long operateTimeMillis;

    public String getOpeCode() {
        return opeCode;
    }

    public void setOpeCode(String opeCode) {
        this.opeCode = opeCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public long getOperateTimeMillis() {
        return operateTimeMillis;
    }

    public void setOperateTimeMillis(long operateTimeMillis) {
        this.operateTimeMillis = operateTimeMillis;
    }
}
