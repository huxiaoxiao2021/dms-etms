package com.jd.bluedragon.configuration.ducc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("duccPropertyConfig")
public class DuccPropertyConfig {
	private static final Logger log = LoggerFactory.getLogger(DuccPropertyConfig.class);
	/**
	 * 使用ducc
	 */
//	@LafUcc
	@Value("${duccPropertyConfig.useDucc:false}")
	private boolean useDucc;

	public boolean isUseDucc() {
		return useDucc;
	}

	public void setUseDucc(boolean useDucc) {
		log.info("DuccPropertyConfig.setUseDucc");
		this.useDucc = useDucc;
	}
}
