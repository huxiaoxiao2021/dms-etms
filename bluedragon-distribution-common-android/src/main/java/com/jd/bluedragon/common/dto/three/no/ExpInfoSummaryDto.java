package com.jd.bluedragon.common.dto.three.no;


import java.io.Serializable;
import java.util.List;

public class ExpInfoSummaryDto implements Serializable {

    private static final long serialVersionUID = -953747299316185634L;
    /**
     * 发现人ERP
     */
    private String pin;

    /**
     * 异常编码
     */
    private String expCode;

    /**
     * 发生时间
     */
    private String happenTime;

    /**
     * 图片
     */
    private List<String> imageUrls;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getExpCode() {
        return expCode;
    }

    public void setExpCode(String expCode) {
        this.expCode = expCode;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(String happenTime) {
        this.happenTime = happenTime;
    }
}
