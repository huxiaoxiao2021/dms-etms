package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;

/**
 * 安卓功能是否可使用结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-04-11 16:11:59 周一
 */
public class FuncUsageProcessDto implements Serializable {

    private static final long serialVersionUID = 5558432835220167498L;

    private static final String URL_MSG_DEFAULT = "点击跳转";
    /**
     * 是否可以使用功能
     */
    private Integer canUse;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 提示类型
     */
    private Integer msgType;
    /**
     * 指引跳转的链接地址
     */
    private String url;
    /**
     * 指引跳转的链接地址-对应的信息（默认：点击跳转）
     */
    private String urlMsg;    

    public Integer getCanUse() {
        return canUse;
    }

    public void setCanUse(Integer canUse) {
        this.canUse = canUse;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlMsg() {
		if(url != null && url.length() > 0) {
			if(urlMsg == null || urlMsg.length() == 0) {
				return URL_MSG_DEFAULT;
			}
		}
		return urlMsg;
	}

	public void setUrlMsg(String urlMsg) {
		this.urlMsg = urlMsg;
	}
}
