package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.ql.basic.domain.BaseDataDict;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description 编码转换工具
 * @author zhangwentao
 * @date 2016年8月18日 下午4:19:42
 */
@Service("carLicenseChangeUtil")
public class CarLicenseChangeUtil {
	protected final static Logger log = LoggerFactory.getLogger(CarLicenseChangeUtil.class);

	@Autowired
	@Qualifier("baseMajorManager")
	private BaseMajorManager baseMajorManager;

	/**
	 * @Title formateLicense2Chinese
	 * @description 格式化车牌号-如果有编码的将编码转换中文
	 * @param carLicense
	 * @return
	 * @throws Exception
	 * @auther zhangwentao
	 */
	public String formateLicense2Chinese(String carLicense) {
		if (StringUtils.isBlank(carLicense)) {
			return carLicense;
		}
		try {
			String firstNum = carLicense.substring(0, 1);
			String codeRule = "[\u4e00-\u9fa5]";
			// 判断首字母是否中文
			if (firstNum.matches(codeRule)) {
				return carLicense; // 是中文
			}
			return carCodeToLicense(carLicense);
		} catch (Exception e) {
			log.error("formateLicense2Chinese-error:carLicense：{}" , carLicense, e);
			return carLicense;
		}
	}

	/**
	 * 车辆编码转换为车牌号
	 * 
	 * @param carCode
	 * @return
	 * @throws Exception
	 */
	private String carCodeToLicense(String carCode) throws Exception {
		if (StringUtils.isBlank(carCode) || carCode.length() <= 3) {
			return carCode;
		}
		List<BaseDataDict> provinceShortName = baseMajorManager.getBaseDataDictList(112, 2, 112);
		if (CollectionUtils.isNotEmpty(provinceShortName)) {
			for (BaseDataDict baseDataDict : provinceShortName) {
				String typeCode = String.valueOf(baseDataDict.getTypeCode());
				if (carCode.substring(0, 3).equals(typeCode) || (carCode.startsWith("0") && carCode.substring(1, 3).equals(typeCode))) {
					return baseDataDict.getTypeName() + carCode.substring(3);
				}
			}
		}
		return carCode;
	}

}
