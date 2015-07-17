package com.jd.bluedragon.distribution.send.domain.reverse;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DsBatchTransferConverter implements Converter {
	
	private List<Class<?>> xStreamAliasTypes;
	
	private DsBoxTransferConverter tDsBoxTransferConverter = new DsBoxTransferConverter();
	
	public List<Class<?>> getXStreamAliasTypes() {
		if (this.xStreamAliasTypes == null) {
			this.xStreamAliasTypes = new ArrayList<Class<?>>();
		}
		
		this.xStreamAliasTypes.add(DsBox.class);
		this.xStreamAliasTypes.add(DsOrder.class);
		this.xStreamAliasTypes.add(DsPack.class);
		
		return this.xStreamAliasTypes;
	}
	
	/*
	 * 只能转换DsBatch类，包括其子类 (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.
	 * lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
		return clazz.equals(DsBatch.class);
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
	}
	
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		DsBatch dsBatch = new DsBatch();
		
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if (null != reader.getNodeName() && "batchId".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					dsBatch.setBatchId(reader.getValue());
				}
			} else if (null != reader.getNodeName() && "rsn".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					dsBatch.setRsn(reader.getValue());
				}
			} else if (null != reader.getNodeName()
					&& "operateTime".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					dsBatch.setOperateTime(reader.getValue());
				}
			} else if (null != reader.getNodeName()
					&& "remark".equalsIgnoreCase(reader.getNodeName())) {
				if (StringUtils.isNotBlank(reader.getValue())) {
					dsBatch.setRemark(reader.getValue());
				}
			} else if (null != reader.getNodeName()
					&& "boxList".equalsIgnoreCase(reader.getNodeName())) {
				List<DsBox> boxList = new ArrayList<DsBox>();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if (null != reader.getNodeName()
							&& "dsBox".equalsIgnoreCase(reader.getNodeName())) {
						DsBox dsBox = new DsBox();
						while (reader.hasMoreChildren()) {
							reader.moveDown();
							dsBox = tDsBoxTransferConverter.getDsBoxByunmarshal(reader, dsBox);
							if (null != reader.getNodeName()
									&& "sendTime".equalsIgnoreCase(reader.getNodeName())) {
								if (StringUtils.isNotBlank(reader.getValue())) {
									dsBox.setSendTime(DateHelper.parseDate(reader.getValue(),
											"yyyy-MM-dd HH:mm:ss"));
								}
							} else if (null != reader.getNodeName()
									&& "orderList".equalsIgnoreCase(reader.getNodeName())) {
								List<DsOrder> orderList = new ArrayList<DsOrder>();
								while (reader.hasMoreChildren()) {
									reader.moveDown();
									if (null != reader.getNodeName()
											&& "dsOrder".equalsIgnoreCase(reader.getNodeName())) {
										DsOrder dsOrder = new DsOrder();
										while (reader.hasMoreChildren()) {
											reader.moveDown();
											dsOrder = tDsBoxTransferConverter.getDsOrderByunmarshal(reader ,dsOrder);
											if (null != reader.getNodeName()
													&& "packList".equalsIgnoreCase(reader
															.getNodeName())) {
												List<DsPack> packList = new ArrayList<DsPack>();
												while (reader.hasMoreChildren()) {
													reader.moveDown();
													if (null != reader.getNodeName()
															&& "dsPack".equalsIgnoreCase(reader
																	.getNodeName())) {
														DsPack dsPack = new DsPack();
														while (reader.hasMoreChildren()) {
															reader.moveDown();
															dsPack = tDsBoxTransferConverter.getDsPackByunmarshal(reader,dsPack);
															reader.moveUp();
														}
														packList.add(dsPack);
													}
													reader.moveUp();
												}
												dsOrder.setPackList(packList);
											}
											reader.moveUp();
										}
										orderList.add(dsOrder);
									}
									reader.moveUp();
								}
								dsBox.setOrderList(orderList);
							}
							reader.moveUp();
						}
						boxList.add(dsBox);
					}
					reader.moveUp();
				}
				dsBatch.setBoxList(boxList);
			}
			reader.moveUp();
		}
		
		return dsBatch;
	}
}
