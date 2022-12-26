package com.jd.bluedragon.external.crossbow.postal.domain;

import java.util.Map;

public class TracesOperTypeMap {
	
   private Map<String,TracesOperType> stateCodeToEmsMap;

	public Map<String, TracesOperType> getStateCodeToEmsMap() {
		return stateCodeToEmsMap;
	}
	
	public void setStateCodeToEmsMap(Map<String, TracesOperType> stateCodeToEmsMap) {
		this.stateCodeToEmsMap = stateCodeToEmsMap;
	}
   
}
