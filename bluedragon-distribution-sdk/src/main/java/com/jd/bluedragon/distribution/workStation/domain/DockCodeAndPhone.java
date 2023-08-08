package com.jd.bluedragon.distribution.workStation.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 运输月台号和联系人
 */
public class DockCodeAndPhone implements Serializable {

	private static final long serialVersionUID = 7466968922838886492L;

	private List<String> dockCodeList;

	private List<Map<String,String>> phoneList;

	public List<String> getDockCodeList() {
		return dockCodeList;
	}

	public void setDockCodeList(List<String> dockCodeList) {
		this.dockCodeList = dockCodeList;
	}

	public List<Map<String, String>> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<Map<String, String>> phoneList) {
		this.phoneList = phoneList;
	}
}
