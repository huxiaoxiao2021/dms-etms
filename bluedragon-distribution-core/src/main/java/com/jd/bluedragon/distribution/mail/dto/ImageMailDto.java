package com.jd.bluedragon.distribution.mail.dto;

import java.io.Serializable;

public class ImageMailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] imageData;
    private String imageCid;//"<img src=\"cid:abcd\">"
    private String mimeType;//like image/jpeg,image/bmp,image/gif


    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageCid() {
        return imageCid;
    }

    public void setImageCid(String imageCid) {
        this.imageCid = imageCid;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}