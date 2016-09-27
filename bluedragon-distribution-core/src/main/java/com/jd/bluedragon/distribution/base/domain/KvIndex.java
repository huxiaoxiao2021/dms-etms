package com.jd.bluedragon.distribution.base.domain;

public class KvIndex implements Cloneable, java.io.Serializable {

	private static final long serialVersionUID = -6696652706839382961L;

	/** 全局唯一ID */
	private Long id;

	/** 关键字 */
	private String keyword;

	/** 值 */
	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}