package com.jd.bluedragon.distribution.send.domain.reverse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DsBoxTransferConverter implements Converter {
	
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
		return clazz.equals(DsBox.class);
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
	}
	
	public DsBox getDsBoxByunmarshal(HierarchicalStreamReader reader,DsBox dsBox){
		if (null != reader.getNodeName() && "batchId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setBatchId(reader.getValue());
			}
		}else if (null != reader.getNodeName()
				&& "boxId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setBoxId(reader.getValue());
			}
		}else if (null != reader.getNodeName()
				&& "sourceDCId".equalsIgnoreCase(reader.getValue())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setSourceDCId(Integer.parseInt(reader.getValue()));
			}
		}else if (null != reader.getNodeName()
				&& "driverId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setDriverId(Integer.parseInt(reader.getValue()));
			}
		}else if (null != reader.getNodeName()
				&& "driverName".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setDriverName(reader.getValue());
			}
		}else if (null != reader.getNodeName()
				&& "operatorId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setOperatorId(Integer.parseInt(reader.getValue()));
			}
		}else if (null != reader.getNodeName()
				&& "operatorName".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setOperatorName(reader.getValue());
			}
		}else if (null != reader.getNodeName()
				&& "orderSum".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setOrderSum(Integer.parseInt(reader.getValue()));
			}
		}else if (null != reader.getNodeName()
				&& "orgId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setOrgId(Integer.parseInt(reader.getValue()));
			}
		}else if (null != reader.getNodeName()
				&& "siteId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setSiteId(Integer.parseInt(reader.getValue()));
			}
		}else if (null != reader.getNodeName()
				&& "siteName".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setSiteName(reader.getValue());
			}
		}else if (null != reader.getNodeName()
				&& "carLicense".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setCarLicense(reader.getValue());
			}
		} else if (null != reader.getNodeName()
				&& "destId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setDestId(reader.getValue());
			}
		}  else if (null != reader.getNodeName()
				&& "remark".equalsIgnoreCase(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())) {
				dsBox.setRemark(reader.getValue());
		}
		return dsBox;
	}
	
	public DsOrder getDsOrderByunmarshal(HierarchicalStreamReader reader,DsOrder dsOrder){
		if (null != reader.getNodeName()
				&& "boxRefId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setBoxRefId(reader.getValue());
			}
		}else if (null != reader.getNodeName()
				&& "deliveryType".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setDeliveryType(reader.getValue());
			}
		} else if (null != reader.getNodeName()
				&& "orderId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setOrderId(reader.getValue());
			}
		} else if (null != reader.getNodeName()
				&& "orderType".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setOrderType(Integer.parseInt(reader.getValue()));
			}
		}else if (null != reader.getNodeName()
				&& "packSum".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setPackSum(Integer.parseInt(reader.getValue()));
			}
		} else if (null != reader.getNodeName()
				&& "queryOrderId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setQueryOrderId(Integer.parseInt(reader.getValue()));
			}
		} else if (null != reader.getNodeName()
				&& "sourceSiteId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setSourceSiteId(Integer.parseInt(reader.getValue()));
			}
		}else if (null != reader.getNodeName()
				&& "storeId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setStoreId(Integer.parseInt(reader.getValue()));
			}
		} else if (null != reader.getNodeName()
				&& "flag".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setFlag(Integer.parseInt(reader.getValue()));
			}
		} else if (null != reader.getNodeName()
				&& "remark".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setRemark(reader.getValue());
			}
		}else if (null != reader.getNodeName()
				&& "sourceDCId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setSourceDCId(Integer.parseInt(reader.getValue()));
			}
		} else if (null != reader.getNodeName()
				&& "destId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setDestId(reader.getValue());
			}
		} else if (null != reader.getNodeName()
				&& "queryOrderCode".equalsIgnoreCase(reader.getNodeName())
				&& StringUtils.isNotBlank(reader.getValue())) {
				dsOrder.setQueryOrderCode(reader.getValue());
		} 
		return dsOrder;
	}
	
	public DsPack getDsPackByunmarshal(HierarchicalStreamReader reader,DsPack dsPack){
		if (null != reader.getNodeName()
				&& "orderRefId".equalsIgnoreCase(reader
						.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsPack.setOrderRefId(reader.getValue());
			}
		} else if (null != reader.getNodeName()
				&& "packId".equalsIgnoreCase(reader.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsPack.setPackId(reader.getValue());
			}
		} else if (null != reader.getNodeName()
				&& "packWbulk".equalsIgnoreCase(reader
						.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsPack.setPackWbulk(new BigDecimal(reader.getValue()));
			}
		} else if (null != reader.getNodeName()
				&& "packWeight".equalsIgnoreCase(reader
						.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsPack.setPackWeight(new BigDecimal(reader.getValue()));
			}
		} else if (null != reader.getNodeName()
				&& "status".equalsIgnoreCase(reader
						.getNodeName())) {
			if (StringUtils.isNotBlank(reader.getValue())) {
				dsPack.setStatus(Integer.parseInt(reader.getValue()));
			}
		} else if (null != reader.getNodeName()
				&& "boxRefId".equalsIgnoreCase(reader
						.getNodeName())
				&& StringUtils.isNotBlank(reader.getValue())) {
				dsPack.setBoxRefId(reader.getValue());
		}
		return dsPack;
	}
	
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		DsBox dsBox = new DsBox();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			dsBox = getDsBoxByunmarshal(reader , dsBox);
			if (null != reader.getNodeName()
					&& "orderList".equalsIgnoreCase(reader.getNodeName())) {
				List<DsOrder> orderList = new ArrayList<DsOrder>();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if (null != reader.getValue()
							&& "dsOrder".equalsIgnoreCase(reader.getNodeName())) {
						DsOrder dsOrder = new DsOrder();
						while (reader.hasMoreChildren()) {
							reader.moveDown();
							dsOrder = getDsOrderByunmarshal(reader ,dsOrder);
							if (null != reader.getNodeName()
									&& "packList".equalsIgnoreCase(reader.getNodeName())) {
								List<DsPack> packList = new ArrayList<DsPack>();
								while (reader.hasMoreChildren()) {
									reader.moveDown();
									if (null != reader.getNodeName()
											&& "dsPack".equalsIgnoreCase(reader.getNodeName())) {
										DsPack dsPack = new DsPack();
										while (reader.hasMoreChildren()) {
											reader.moveDown();
											dsPack = getDsPackByunmarshal(reader,dsPack);
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
		
		return dsBox;
	}
}
