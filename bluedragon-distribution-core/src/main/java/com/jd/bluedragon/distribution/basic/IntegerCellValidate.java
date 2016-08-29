package com.jd.bluedragon.distribution.basic;

public class IntegerCellValidate extends AbstractValidate implements
		CellValidate {

	@Override
	public String validate(Object value, MetaData metaData) {
		String msg = null;
		if (value != null) {
			String str = value.toString();
			if (!str.matches("^(-|)\\d+$")) {
				msg = super.getPositionMsg(metaData) + metaData.getFieldName()
						+ ":对应的属性不是Integer类型";
			}
		}
		return msg;
	}

}
