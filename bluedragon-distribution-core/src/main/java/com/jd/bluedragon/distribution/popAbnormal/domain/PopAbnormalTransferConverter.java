package com.jd.bluedragon.distribution.popAbnormal.domain;

import org.apache.commons.lang.StringUtils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.DateHelper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @User：zhaohengchong
 * @E-mail: zhaohengchong@360buy.com
 * @Date：2011-12-8
 * @Time：下午01:28:24 异常订单处理转换类
 */
public class PopAbnormalTransferConverter implements Converter {
	
	public static final String ALIAS_NAME = "PopAbnormalOrderMessage";

	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {

	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		PopAbnormal popAbnormal = new PopAbnormal();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if (null != reader.getNodeName()
					&& "id".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setId(Long.valueOf(reader.getValue()));
				}
			} else if (null != reader.getNodeName()
					&& "serialNumber".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setSerialNumber(reader.getValue());
				}
			} else if (null != reader.getNodeName()
					&& ("abnormalType".equalsIgnoreCase(reader.getNodeName()) || "type"
							.equalsIgnoreCase(reader.getNodeName()))) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setAbnormalType(Integer.valueOf(reader
							.getValue()));
				}
			} else if (null != reader.getNodeName()
					&& ("waybill".equalsIgnoreCase(reader.getNodeName()) || "waybillCode".equalsIgnoreCase(reader.getNodeName()))) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setWaybillCode(reader.getValue());
				}
			} else if (null != reader.getNodeName()
					&& ("expNo".equalsIgnoreCase(reader.getNodeName()) || "orderId"
							.equalsIgnoreCase(reader.getNodeName()) || "orderCode"
							.equalsIgnoreCase(reader.getNodeName()))) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setOrderCode(reader.getValue());
				}
			} else if (null != reader.getNodeName()
					&& ("popSupNo".equalsIgnoreCase(reader.getNodeName()) || "venderId"
							.equalsIgnoreCase(reader.getNodeName()))) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setPopSupNo(reader.getValue());
				}
			} else if (null != reader.getNodeName()
					&& "popSupName".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setPopSupName(reader.getValue());
				}
			} else if (null != reader.getNodeName()
					&& "currentNum".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setCurrentNum(Integer.valueOf(reader
							.getValue()));
				}
			} else if (null != reader.getNodeName()
					&& "actualNum".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setActualNum(Integer.valueOf(reader
							.getValue()));
				}
			} else if (null != reader.getNodeName()
					&& ("confirmNum".equalsIgnoreCase(reader.getNodeName()) || "confirmNumber"
							.equalsIgnoreCase(reader.getNodeName()))) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setConfirmNum(Integer.valueOf(reader
							.getValue()));
				}
			} else if (null != reader.getNodeName()
					&& "createTime".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setCreateTime(DateHelper.parseDate(reader
							.getValue(), Constants.DATE_TIME_FORMAT));
				}
			} else if (null != reader.getNodeName()
					&& ("operatorNo".equalsIgnoreCase(reader.getNodeName()) || "operatorCode".equalsIgnoreCase(reader.getNodeName()))) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setOperatorCode(Integer.valueOf(reader.getValue()));
				}
			} else if (null != reader.getNodeName()
					&& "operatorName".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setOperatorName(reader.getValue());
				}
			} else if (null != reader.getNodeName()
					&& "confirmTime".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setConfirmTime(DateHelper.parseDate(reader
							.getValue(), Constants.DATE_TIME_FORMAT));
				}
			} else if (null != reader.getNodeName()
					&& "updateTime".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setUpdateTime(DateHelper.parseDate(reader
							.getValue(), Constants.DATE_TIME_FORMAT));
				}
			} else if (null != reader.getNodeName()
					&& "abnormalState".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setAbnormalState(Integer.valueOf(reader
							.getValue()));
				}
			} else if (null != reader.getNodeName()
					&& "memo".equalsIgnoreCase(reader.getNodeName())
					&& StringUtils.isNotBlank(reader.getValue())) {
				//if (StringUtils.isNotBlank(reader.getValue())) {
					popAbnormal.setMemo(reader.getValue());
				//}
			}
			reader.moveUp();
		}
		return popAbnormal;
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return clazz.equals(PopAbnormal.class);
	}

}
