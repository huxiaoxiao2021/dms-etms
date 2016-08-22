package com.jd.bluedragon.distribution.basic;

public class RequireCellValidate extends AbstractValidate implements
		CellValidate {

	@Override
	public String validate(Object value, MetaData metaData) {
		String msg = null;
		if (value == null) {
			msg = super.getPositionMsg(metaData) + metaData.getFieldName()
					+ ":对应的属性为空";
		} else if (value instanceof String) {
			if ("".equals(((String) value).trim())) {
				msg = super.getPositionMsg(metaData) + metaData.getFieldName()
						+ ":对应的属性为空";
			}
		}
		return msg;
	}

}