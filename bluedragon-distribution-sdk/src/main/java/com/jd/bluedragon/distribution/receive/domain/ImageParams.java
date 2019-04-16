package com.jd.bluedragon.distribution.receive.domain;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @ClassName: ImageParams
 * @Description: 上传图片对象
 * @author: hujiping
 * @date: 2019/4/16 11:21
 */
public class ImageParams implements Serializable {

    private static final long serialVersionUID = 1L;

    private MultipartFile[] image;

    private Long[] operateTime;

    private String machineCode;

    private Integer siteCode;

//    private HttpServletResponse response;
//
//    private HttpServletRequest request;

//    public HttpServletResponse getResponse() {
//        return response;
//    }
//
//    public void setResponse(HttpServletResponse response) {
//        this.response = response;
//    }

//    public HttpServletRequest getRequest() {
//        return request;
//    }

//    public void setRequest(HttpServletRequest request) {
//        this.request = request;
//    }

    public MultipartFile[] getImage() {
        return image;
    }

    public void setImage(MultipartFile[] image) {
        this.image = image;
    }

    public Long[] getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long[] operateTime) {
        this.operateTime = operateTime;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }
}
