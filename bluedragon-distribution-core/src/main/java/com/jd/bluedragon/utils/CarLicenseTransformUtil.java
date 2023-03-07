package com.jd.bluedragon.utils;

import com.jd.bluedragon.dms.utils.DmsConstants;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarLicenseTransformUtil {

  static Logger logger = LoggerFactory.getLogger(CarLicenseTransformUtil.class);

  public static Map<String, String> numToChinese =new HashMap<>();
  static {
    numToChinese.put("021","沪");
    numToChinese.put("022","津");
    numToChinese.put("010","京");
    numToChinese.put("023","渝");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
    numToChinese.put("010","京");
  }

  /**
   * @Description 车牌号统一转成中文开头格式 如"010A12345" -> "京A12345"
   * @Param carLicense(车牌号) dictMap字典值(key=区号编码，value=区域简称)
   */
  public static String transformLicensePrefixToChinese(String carLicense, Map<String,String> dictMap) {
    if (StringUtils.isBlank(carLicense)) {
      logger.warn("transformLicensePrefixToChinese invalid format carLicense["+ carLicense +"]");
      return carLicense;
    }
    carLicense = carLicense.replaceAll(DmsConstants.WHITE_SPACE, "").toUpperCase();
    try {
      if (carLicense.matches(DmsConstants.CHINESE_PREFIX)) {
        return carLicense;
      }
      else if (carLicense.matches(DmsConstants.CODE_PREFIX)){
        return codeToChinese(carLicense, dictMap);
      }
      else {
        logger.warn("transformLicensePrefixToChinese invalid format carLicense["+ carLicense +"]");
        return carLicense;
      }
    } catch (Exception e) {
      logger.error("transformLicensePrefixToChinese-error:carLicense：" + carLicense, e);
      return carLicense;
    }
  }

  /**
   * 车辆编码转换为汉字车牌号
   *
   * @param carLicense dictMap
   * @return
   */
  private static String codeToChinese(String carLicense, Map<String,String> dictMap) {
    if(null == dictMap){
      logger.warn("codeToChinese dict is empty..");
      return carLicense;
    }
    String prefix = carLicense.substring(0, 3);
    String remainSegment = carLicense.substring(3);
    if(dictMap.containsKey(prefix)){
      return dictMap.get(prefix).concat(remainSegment);
    }else{
      logger.warn("codeToChinese has not matched dict carLicense["+ carLicense +"]");
      return carLicense;
    }
  }

}
