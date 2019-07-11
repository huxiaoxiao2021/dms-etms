package com.jd.bluedragon.common.dto.board.response;


import java.io.Serializable;

/**
 * BoardCheckDto
 * 板号校验返回信息
 *
 * @author jiaowenqiang
 * @date 2019/7/8
 */
public class BoardCheckDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 接收站点编号
     */
    private Integer receiveSiteCode;

    /**
     * 接收站点名称
     */
    private String receiveSiteName;

    @Override
    public String toString() {
        return "BoardCheckDto{" +
                "receiveSiteCode=" + receiveSiteCode +
                ", receiveSiteName='" + receiveSiteName + '\'' +
                '}';
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
}
