package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.List;

/**
 * @ClassName CollectionBagOperationReq
 * @Description
 * @Author wyh
 * @Date 2020/7/8 18:41
 **/
public class CollectionBagOperationReqVO extends JdRequest {

    private static final long serialVersionUID = -7327732875182354286L;

    /**
     * 保温箱号集合
     */
    private List<String> boxCodes;

    /**
     * 目的站点编码
     */
    private Long receiveSiteCode;

    /**
     * 目的站点名称
     */
    private String receiveSiteName;

    public List<String> getBoxCodes() {
        return boxCodes;
    }

    public void setBoxCodes(List<String> boxCodes) {
        this.boxCodes = boxCodes;
    }

    public Long getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Long receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }
}
