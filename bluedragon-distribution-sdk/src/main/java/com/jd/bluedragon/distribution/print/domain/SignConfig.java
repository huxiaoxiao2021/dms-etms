package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;
import java.util.Map;
/**
 * <p>示例：waybill_sign第10位标识为生鲜标识（配送业务类型）6-温控 7-冷藏 8-冷冻 9-深冷
 * <p>配置信息：{position：10,fieldName:freshSignText signTexts:{6-温控 7-冷藏 8-冷冻 9-深冷}}
 * @ClassName: SignConfig
 * @Description: 标识配置信息
 * @author: wuyoude
 * @date: 2018年2月28日 下午4:08:32
 *
 */
public class SignConfig implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 标识位置
	 */
	private Integer position;
	/**
	 * 标识位映射的字段名
	 */
	private String fieldName;
	/**
	 * 标识位-对应的描述
	 */
	private Map<String,String> signTexts;
	/**
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return the signTexts
	 */
	public Map<String, String> getSignTexts() {
		return signTexts;
	}
	/**
	 * @param signTexts the signTexts to set
	 */
	public void setSignTexts(Map<String, String> signTexts) {
		this.signTexts = signTexts;
	}
}
