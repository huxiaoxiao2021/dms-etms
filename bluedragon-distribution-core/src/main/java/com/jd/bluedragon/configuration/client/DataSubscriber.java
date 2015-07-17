package com.jd.bluedragon.configuration.client;

public interface DataSubscriber {

	public String getConfigure(String key);

	public String getAvailableConfigure(String key);

	public void init();


}
