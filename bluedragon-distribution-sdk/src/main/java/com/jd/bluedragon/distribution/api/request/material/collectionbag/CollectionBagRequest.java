package com.jd.bluedragon.distribution.api.request.material.collectionbag;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.List;

/**
 * @ClassName CollectionBagRequest
 * @Description 集包袋发空袋，收空袋请求体
 * @Author wyh
 * @Date 2020/6/30 15:40
 **/
public class CollectionBagRequest extends JdRequest {

    private static final long serialVersionUID = 1018964135504679701L;

    /**
     * 集包袋编码集合
     */
    private List<String> collectionBagCodes;

    /**
     * 目的站点编码
     */
    private Long receiveSiteCode;

    /**
     * 目的站点名称
     */
    private String receiveSiteName;

    private String userErp;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public List<String> getCollectionBagCodes() {
        return collectionBagCodes;
    }

    public void setCollectionBagCodes(List<String> collectionBagCodes) {
        this.collectionBagCodes = collectionBagCodes;
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
