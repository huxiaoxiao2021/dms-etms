package com.jd.bluedragon.distribution.api.request;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.distribution.api.JdObject;

public class WmsOrderPackagesRequest extends JdObject {
	
	List<WmsOrderPackageRequest> orderPackages = new ArrayList<WmsOrderPackageRequest>();
	
	public List<WmsOrderPackageRequest> getOrderPackages() {
		return orderPackages;
	}
	
	public void setOrderPackages(List<WmsOrderPackageRequest> orderPackages) {
		this.orderPackages = orderPackages;
	}
	
}
