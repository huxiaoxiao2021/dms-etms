package com.jd.bluedragon.external.crossbow.postal.manager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.bluedragon.external.crossbow.AbstractCrossbowManager;
import com.jd.lop.crossbow.util.HmacUtil;
import com.jdl.basic.common.utils.ObjectHelper;
/**
 * 发全程跟踪给外协-邮政平台
 * @author wuyoude
 *
 */
public abstract class AbstractPostalCrossbowManager<P,R> extends AbstractCrossbowManager<P,R> {
	private static final Logger log = LoggerFactory.getLogger(AbstractPostalCrossbowManager.class);

	protected String brandCode;
	
	protected String secret;
	
	protected List<String> orderedFieldNames = new ArrayList<String>();
	
	protected Map<String,List<String>> listFieldNameMap = new HashMap<String,List<String>>();
	
	public AbstractPostalCrossbowManager(){
        /* 获取具体实现类的返回值泛型 对应的R */
        Type superClass = this.getClass().getGenericSuperclass();
        Type requestType = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        
        List<Field> fieldList = ObjectHelper.getAllFieldsList((Class)requestType);
        sortByName(fieldList);
        for(Field field : fieldList) {
        	if("serialVersionUID".equals(field.getName())) {
        		continue;
        	}
        	orderedFieldNames.add(field.getName());
        }
	}
	
	protected void sortByName(List<Field> fieldList) {
        if(CollectionUtils.isNotEmpty(fieldList)) {
        	Collections.sort(fieldList, new Comparator<Field>() {
				@Override
				public int compare(Field o1, Field o2) {
					return o1.getName().compareTo(o2.getName());
				}
        	});
        }
	}
	/**
	 <p> 在消息头添加“sign”字段进行签名验证。然后对body所有参数按照字母序列化，采用key=value
	     （如果value是数组需要采用同样的方式进行拼接）并用&进行拼接，最后拼接secret。然后对字符串使用MD5加密，对MD5加密后的密文转换为Base64字符串
	     示例：请求body如下：
			{
			    "brandCode": "cs",
			    "doType": "A",
			    "waybillNo": "1234567890",
			    "productCode": "123456",
			    "productName": "12345678",
			    "pickupAttribute": "123",
			    "insuranceFlag": "N",
			    "mailbagClassCode": "123456",
			    "mailbagClassName": "1234567",
			    "isInternational": "4",
			    "transTypeCode": "1",
			    "realWeight": "123",
			    "volWeight": "123",
			    "receiverLinker": "123",
			    "receiverAddress": "123",
			    "receiverMobile": "123"
			}
			(1).	对body进行序列化得到：brandCode=cs&doType=A&insuranceFlag=N&isInternational=4&mailbagClassCode=123456&mailbagClassName=1234567&pickupAttribute=123&productCode=123456&productName=12345678&realWeight=123&receiverAddress=123&receiverLinker=123&receiverMobile=123&transTypeCode=1&volWeight=123&waybillNo=1234567890
			(2).	拼接secret得到：
			        brandCode=cs&doType=A&insuranceFlag=N&isInternational=4&mailbagClassCode=123456&mailbagClassName=1234567&pickupAttribute=123&productCode=123456&productName=12345678&realWeight=123&receiverAddress=123&receiverLinker=123&receiverMobile=123&transTypeCode=1&volWeight=123&waybillNo=1234567890ab3785cf01424e6099ba795e50b02e74
			(3).	对字符串进行MD5加密：68A1167306AD91CB98ADAF5B8294E378
			(4).	最后把MD5加密后的密文转换为Base64字符串：NjhBMTE2NzMwNkFEOTFDQjk4QURBRjVCODI5NEUzNzg=

	 */
	@Override
	public Map<String, String> getMyHeaderParams(Object condition) {
		Map<String, String> headerParams = new HashMap<String, String>();
        /* 计算签名 */
		StringBuffer sf = new StringBuffer();
		for(String fieldName : orderedFieldNames) {
			Object val = null;
			String strVal = "";
			try {
				val = ObjectHelper.getValue(condition, fieldName);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error("获取属性值{}异常",fieldName, e);
			}
			if(val == null) {
				continue;
			}
			if(listFieldNameMap.containsKey(fieldName)) {
				StringBuffer sfItem = new StringBuffer();
				List<Object> listVal = (List<Object>) val;
				List<String> fieldList0 = listFieldNameMap.get(fieldName);
				for(Object valItem : listVal) {
					for(String fieldNameItem : fieldList0) {
						Object valItem0 = null;
						try {
							valItem0 = ObjectHelper.getValue(valItem, fieldNameItem);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							log.error("获取属性值{}异常",fieldNameItem, e);
						}
						if(valItem0 == null) {
							continue;
						}
						if(sfItem.length() > 0) {
							sfItem.append("&");
						}
						sfItem.append(fieldNameItem);
						sfItem.append("=");
						sfItem.append(valItem0.toString());
					}
				}
				strVal = sfItem.toString();
			}else {
				strVal = val.toString();
			}
			if(sf.length() > 0) {
				sf.append("&");
			}
			sf.append(fieldName);
			sf.append("=");
			sf.append(strVal);
		}
		sf.append(secret);
		log.info("序列化报文："+sf.toString());
		String md5Str = HmacUtil.MD5(sf.toString()).toUpperCase();
		log.info("MD5报文：{}",md5Str);
		try {
			String base64Str = new Base64().encodeToString(md5Str.getBytes("UTF-8"));
			log.info("base64报文：{}",base64Str);
			headerParams.put("sign",base64Str);
		} catch (UnsupportedEncodingException e) {
			log.error("报文转换base64异常", e);
			throw new RuntimeException("签名计算异常"+e.getMessage());
		}
		return headerParams;
	}

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
    
}
