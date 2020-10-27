package com.jd.bluedragon.common.dto.material.collectionbag;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName CollectionBagOperationReq
 * @Description
 * @Author wyh
 * @Date 2020/7/8 18:41
 **/
public class CollectionBagOperationReq implements Serializable {

    private static final long serialVersionUID = -7327732875182354286L;

    private User user;

    private CurrentOperate currentOperate;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

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
