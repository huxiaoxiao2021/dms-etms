package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static PropertiesHelper propertiesHelper = null;

	private Properties properties = null;

	public static PropertiesHelper newInstance() {
		if (null == PropertiesHelper.propertiesHelper) {
			PropertiesHelper.propertiesHelper = new PropertiesHelper();
		}

		return PropertiesHelper.propertiesHelper;
	}

	private PropertiesHelper() {
		InputStream mainFileInputStream = null;
		InputStream importantInputiStream = null;
		InputStream dmsIpMappingInputiStream = null;

		try {
			mainFileInputStream = PropertiesHelper.class.getResourceAsStream("/configured/"
			        + Constants.MAIN_CONFIGNAME);

			importantInputiStream = PropertiesHelper.class.getResourceAsStream("/configured/"
			        + Constants.IMPORTANT_CONFIGNAME);

			dmsIpMappingInputiStream = PropertiesHelper.class.getResourceAsStream("/configured/"
					+ Constants.DMS_IP_MAPPING_CONFIGNAME);

			this.properties = new Properties();
			this.properties.load(mainFileInputStream);
			this.properties.load(importantInputiStream);
			this.properties.load(dmsIpMappingInputiStream);
		} catch (IOException ioe) {
			this.log.error("加载配置文件发生异常！", ioe);
		} finally {
			try {
				if (mainFileInputStream != null) {
					mainFileInputStream.close();
				}
			} catch (IOException ioe) {
				this.log.error("关闭文件流发生异常！", ioe);
			}

			try {
				if (importantInputiStream != null) {
					importantInputiStream.close();
				}
			} catch (IOException ioe) {
				this.log.error("关闭文件流发生异常！", ioe);
			}

			try {
				if (dmsIpMappingInputiStream != null) {
					dmsIpMappingInputiStream.close();
				}
			} catch (IOException ioe) {
				this.log.error("关闭文件流发生异常！", ioe);
			}
		}
	}

	/**
	 * 得到配置文件中对应元素值
	 *
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public String getValue(String key) {
		return this.properties.getProperty(key);
	}
}
