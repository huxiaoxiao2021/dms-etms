package com.jd.bluedragon.distribution.api.request;

import java.util.List;
import com.jd.bluedragon.distribution.api.JdRequest;

public class ReverseSpareRequest extends JdRequest {

    private static final long serialVersionUID = 556555029682781842L;

    /** 箱号 */
    private String boxCode;

    /** 运单号 */
    private String waybillCode;

    /** 是否取消分拣 '0' 正常 '1' 取消 */
    private Integer isCancel;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接收站点名称 */
    private String receiveSiteName;
    
    /**
     * 备件库退货原因
     */
    private String spareReason;
    
    /**
     * 商品明细对象-备件库
     */
	private List<ReverseSpareDto> data;

    /**
     * 异常编码
     * */
    private Integer spareCode;

    /**
     * 全程跟踪显示内容
     * */
    private String trackContent;

    /**
     * 责任主体编号
     * */
    private String dutyCode;

    /**
     * 责任主体名称
     * */
    private String dutyName;

    public Integer getSpareCode() {
        return spareCode;
    }

    public void setSpareCode(Integer spareCode) {
        this.spareCode = spareCode;
    }

    public String getBoxCode() {
        return this.boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getIsCancel() {
        return this.isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return this.receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getSpareReason() {
		return spareReason;
	}

	public void setSpareReason(String spareReason) {
		this.spareReason = spareReason;
	}

	public List<ReverseSpareDto> getData() {
		return data;
	}

	public void setData(List<ReverseSpareDto> data) {
		this.data = data;
	}

    public String getTrackContent() {
        return trackContent;
    }

    public void setTrackContent(String trackContent) {
        this.trackContent = trackContent;
    }

    public String getDutyCode() {
        return dutyCode;
    }

    public void setDutyCode(String dutyCode) {
        this.dutyCode = dutyCode;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }
}
