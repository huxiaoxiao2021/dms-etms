package com.jd.bluedragon.common.dto.search.response;

import java.io.Serializable;
import java.util.List;

/**
 * 箱信息出参
 */
public class BoxInfoResponse  implements Serializable {
    private static final long serialVersionUID = -6429215687354579653L;

    /**
     * 箱内包裹总数
     */
    private Integer totalPack;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接收站点名称 */
    private String receiveSiteName;

    /**
     * 箱号包裹信息集合
     */
    private List<PackBoxResponse> boxPackList;

    public Integer getTotalPack() {
        return totalPack;
    }

    public void setTotalPack(Integer totalPack) {
        this.totalPack = totalPack;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public List<PackBoxResponse> getBoxPackList() {
        return boxPackList;
    }

    public void setBoxPackList(List<PackBoxResponse> boxPackList) {
        this.boxPackList = boxPackList;
    }
}
