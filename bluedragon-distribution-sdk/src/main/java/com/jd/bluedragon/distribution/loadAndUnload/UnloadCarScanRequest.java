package com.jd.bluedragon.distribution.loadAndUnload;

import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;

/**
 * 卸车扫描请求对象
 *
 * @author: hujiping
 * @date: 2020/6/23 15:25
 */
public class UnloadCarScanRequest extends BoardCommonRequest {

    /**
     * 封车编码
     * */
    private String sealCarCode;

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }
}
