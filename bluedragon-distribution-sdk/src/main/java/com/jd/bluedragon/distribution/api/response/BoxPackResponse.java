package com.jd.bluedragon.distribution.api.response;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-6-21 上午09:24:35
 *
 * 类说明
 */
public class BoxPackResponse extends JdResponse {
	private static final long serialVersionUID = -6429215687354579653L;
	
	public static final Integer CODE_OK_NODATA = 202;
    public static final String MESSAGE_OK_NODATA = "无数据";
	
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
    @SuppressWarnings("unchecked")
	private List boxPackList;

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

	@SuppressWarnings("unchecked")
	public List getBoxPackList() {
		return boxPackList;
	}

	@SuppressWarnings("unchecked")
	public void setBoxPackList(List boxPackList) {
		this.boxPackList = boxPackList;
	}
}
