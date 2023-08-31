package com.jd.bluedragon.distribution.workStation.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 运输月台号和联系人
 */
public class DockCodeAndPhone implements Serializable {

	private static final long serialVersionUID = 7466968922838886492L;

	private List<String> dockCodeList;

	private List<UserNameAndPhone> phoneList;

	public List<String> getDockCodeList() {
		return dockCodeList;
	}

	public void setDockCodeList(List<String> dockCodeList) {
		this.dockCodeList = dockCodeList;
	}

	public List<UserNameAndPhone> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<UserNameAndPhone> phoneList) {
		this.phoneList = phoneList;
	}
}
