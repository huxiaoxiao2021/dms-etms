package com.jd.bluedragon.distribution.popAbnormal.helper;

import java.util.List;

public class AbnormalReason implements Comparable<AbnormalReason> {

	private Integer code;
	private String name;
	private List<AbnormalReason> clilds;
	private Integer level;
	private Integer parent;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AbnormalReason> getClilds() {
		return clilds;
	}

	public void setClilds(List<AbnormalReason> clilds) {
		this.clilds = clilds;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "[" + this.code + ":" + this.name + "]";
	}

	@Override
	public int compareTo(AbnormalReason o) {
		if (o == null || this.code == null || o.code == null) {
			return 0;
		}
		return this.code.intValue() < o.code.intValue() ? -1 : (this.code.intValue() == o.code.intValue() ? 0 : 1);
	}
}
