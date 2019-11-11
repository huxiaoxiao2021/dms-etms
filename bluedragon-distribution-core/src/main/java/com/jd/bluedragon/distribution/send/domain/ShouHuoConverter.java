package com.jd.bluedragon.distribution.send.domain;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.utils.DateHelper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 格式化对象和反序列化xml 转换类，该类只能转换Tms收货对象
 */
public class ShouHuoConverter implements Converter {
    private static final Logger log = LoggerFactory.getLogger(ShouHuoConverter.class);
    
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class clazz) {
        return clazz.equals(ShouHuoInfo.class);
    }
    
    /**
     * 序列化时会走该方法 格式化Tms收货对象
     * 
     * @param value
     * @param writer
     * @param context
     */
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        // 强制转型发货对象
        ShouHuoInfo shouHuoInfo = (ShouHuoInfo) value;
        if (null != shouHuoInfo) {
            this.formatShouHuo(shouHuoInfo, writer); // 格式化收货Tms对象
        }
    }
    
    /**
     * 反序列化时会走该方法
     * 
     * @param reader
     * @param context
     * @return
     */
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 时间格式化对象
        ShouHuoInfo shouHuoInfo = new ShouHuoInfo(); // Tms收货业务对象
        
        // 判断有子节点
        while (reader.hasMoreChildren()) {
            reader.moveDown(); // 移到下一个节点
            // 判断节点名称是否是batchId（批次号）
            if (null != reader.getNodeName() && reader.getNodeName().equalsIgnoreCase("batchId")) {
                shouHuoInfo.setBatchId(reader.getValue());
                reader.moveUp();
                continue;
            }
            // 判断节点名称是否是uuId（防重码）
            if (null != reader.getNodeName() && reader.getNodeName().equalsIgnoreCase("uuId")) {
                shouHuoInfo.setUuId(reader.getValue());
                reader.moveUp();
                continue;
            }
            // 判断节点名称是否是createTime（创建时间或者提交时间）
            if (null != reader.getNodeName() && reader.getNodeName().equalsIgnoreCase("createTime")) {
                try {
                    shouHuoInfo.setCreateTime(format.parse(reader.getValue()));
                    reader.moveUp();
                    continue;
                } catch (ParseException e) {
                    log.error("ShouHuoConverter 日期转换错误！日期格式：{}" , reader.getValue(),e);
                }
            }
            // 判断节点名称是否是remark（描述）
            if (null != reader.getNodeName() && reader.getNodeName().equalsIgnoreCase("remark")) {
                shouHuoInfo.setRemark(reader.getValue());
                reader.moveUp();
                continue;
            }
            // 判断节点名称是否是callCode（调用代码）
            if (null != reader.getNodeName() && reader.getNodeName().equalsIgnoreCase("callCode")) {
                shouHuoInfo.setCallCode(reader.getValue());
                reader.moveUp();
                continue;
            }
            // 判断节点名称是否是carNo（车牌号）
            if (null != reader.getNodeName() && reader.getNodeName().equalsIgnoreCase("carNo")) {
                shouHuoInfo.setCarNo(reader.getValue());
                reader.moveUp();
                continue;
            }
            // 判断节点名称是否是boxInfoList（箱子集合）
            if (null != reader.getNodeName()
                    && reader.getNodeName().equalsIgnoreCase("boxInfoList")) {
                List<BoxInfo> boxInfoList = new ArrayList<BoxInfo>(); // 箱子集合
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    // 判断节点名称是否是boxInfo
                    if (null != reader.getNodeName()
                            && reader.getNodeName().equalsIgnoreCase("BoxInfo")) {
                        
                        BoxInfo boxInfo = new BoxInfo();
                        while (reader.hasMoreChildren()) {
                            reader.moveDown();
                            // 判断节点名称是否是boxId(箱号)
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("boxId")) {
                                boxInfo.setBoxId(reader.getValue());
                                reader.moveUp();
                                continue;
                            }
                            // 判断节点名称是否是sendType(发货类型)
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("sendType")) {
                                boxInfo.setSendType(reader.getValue());
                                reader.moveUp();
                                continue;
                            }
                            // 判断节点名称是否是sendId(发货ID)
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("sendId")) {
                                boxInfo.setSendId(reader.getValue());
                                reader.moveUp();
                                continue;
                            }
                            // 判断节点名称是否是sendName(发货名称，和发货ID匹配)
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("sendName")) {
                                boxInfo.setSendName(reader.getValue());
                            }
                            // 判断节点名称是否是carrierId(司机Id（承运人）)
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("carrierId")) {
                                boxInfo.setCarrierId(reader.getValue());
                                reader.moveUp();
                                continue;
                            }
                            // 判断节点名称是否是carrierName( 司机姓名（承运人）)
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("carrierName")) {
                                boxInfo.setCarrierName(reader.getValue());
                                reader.moveUp();
                                continue;
                            }
                            // 判断节点名称是否是operatorId(操作人ID)
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("operatorId")) {
                                boxInfo.setOperatorId(reader.getValue());
                                reader.moveUp();
                                continue;
                            }
                            // 判断节点名称是否是operatorCode(操作人ERP账号)
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("operatorCode")) {
                                boxInfo.setOperatorCode(reader.getValue());
                                reader.moveUp();
                                continue;
                            }
                            // 判断节点名称是否是operatorName(操作人姓名)
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("operatorName")) {
                                boxInfo.setOperatorName(reader.getValue());
                                reader.moveUp();
                                continue;
                            }
                            // 判断节点名称是否是orderInfoList（订单集合）
                            if (null != reader.getNodeName()
                                    && reader.getNodeName().equalsIgnoreCase("orderInfoList")) {
                                List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>(); // 订单集合
                                while (reader.hasMoreChildren()) {
                                    reader.moveDown();
                                    // 判断节点名称是否是orderInfo（订单信息）
                                    if (null != reader.getNodeName()
                                            && reader.getNodeName().equalsIgnoreCase("OrderInfo")) {
                                        OrderInfo orderInfo = new OrderInfo();
                                        while (reader.hasMoreChildren()) {
                                            reader.moveDown();
                                            // 判断节点名称是否是boxId(箱号)
                                            if (null != reader.getNodeName()
                                                    && reader.getNodeName().equalsIgnoreCase(
                                                            "orderId")) {
                                                orderInfo.setOrderId(reader.getValue());
                                                reader.moveUp();
                                                continue;
                                            }
                                            // 判断节点名称是否是boxId(箱号)
                                            if (null != reader.getNodeName()
                                                    && reader.getNodeName().equalsIgnoreCase(
                                                            "orderType")) {
                                                orderInfo.setOrderType(reader.getValue());
                                                reader.moveUp();
                                                continue;
                                            }
                                            // 判断节点名称是否是sendType(发货类型)
                                            if (null != reader.getNodeName()
                                                    && reader.getNodeName().equalsIgnoreCase(
                                                            "orderSource")) {
                                                orderInfo.setOrderSource(reader.getValue());
                                                reader.moveUp();
                                                continue;
                                            }
                                            // 判断节点名称是否是zdId(订单原始站点（预分拣之后的站点）)
                                            if (null != reader.getNodeName()
                                                    && reader.getNodeName()
                                                            .equalsIgnoreCase("zdId")) {
                                                orderInfo.setZdId(reader.getValue());
                                                reader.moveUp();
                                                continue;
                                            }
                                            // 判断节点名称是否是orderAdd(订单对应的客户地址)
                                            if (null != reader.getNodeName()
                                                    && reader.getNodeName().equalsIgnoreCase(
                                                            "orderAdd")) {
                                                orderInfo.setOrderAdd(reader.getValue());
                                                reader.moveUp();
                                                continue;
                                            }
                                            // 判断节点名称是否是sendId(发货ID)
                                            if (null != reader.getNodeName()
                                                    && reader.getNodeName().equalsIgnoreCase(
                                                            "packNum")) {
                                                orderInfo.setPackNum(Integer.valueOf(reader
                                                        .getValue()));
                                                reader.moveUp();
                                                continue;
                                            }
                                            
                                            // 判断节点名称是否是dispatchType(发货ID)
                                            if (null != reader.getNodeName()
                                                    && reader.getNodeName().equalsIgnoreCase(
                                                            "dispatchType")) {
                                                orderInfo.setDispatchType(reader.getValue());
                                                reader.moveUp();
                                                continue;
                                            }
                                            
                                            // 判断节点名称是否是packInfoList（包裹集合）
                                            if (null != reader.getNodeName()
                                                    && reader.getNodeName().equalsIgnoreCase(
                                                            "packInfoList")) {
                                                List<PackInfo> packInfoList = new ArrayList<PackInfo>(); // 包裹集合
                                                while (reader.hasMoreChildren()) {
                                                    reader.moveDown();
                                                    // 判断节点名称是否是packInfo（包裹信息）
                                                    if (null != reader.getNodeName()
                                                            && reader.getNodeName()
                                                                    .equalsIgnoreCase("PackInfo")) {
                                                        PackInfo packInfo = new PackInfo();
                                                        while (reader.hasMoreChildren()) {
                                                            reader.moveDown();
                                                            // 判断节点名称是否是packNo(包裹号)
                                                            if (null != reader.getNodeName()
                                                                    && reader.getNodeName()
                                                                            .equalsIgnoreCase(
                                                                                    "packNo")) {
                                                                packInfo.setPackNo(reader
                                                                        .getValue());
                                                                reader.moveUp();
                                                                continue;
                                                            }
                                                            // 判断节点名称是否是packWeight(包裹重量)
                                                            if (null != reader.getNodeName()
                                                                    && reader.getNodeName()
                                                                            .equalsIgnoreCase(
                                                                                    "packWeight")) {
                                                                packInfo.setPackWeight(new BigDecimal(
                                                                        reader.getValue()));
                                                                reader.moveUp();
                                                                continue;
                                                            }
                                                            // 判断节点名称是否是packVolume(包裹体积)
                                                            if (null != reader.getNodeName()
                                                                    && reader.getNodeName()
                                                                            .equalsIgnoreCase(
                                                                                    "packVolume")) {
                                                                packInfo.setPackVolume(new BigDecimal(
                                                                        reader.getValue()));
                                                                reader.moveUp();
                                                                continue;
                                                            }
                                                            reader.moveUp();
                                                        }
                                                        packInfoList.add(packInfo); // 添加包裹对象
                                                    }
                                                    reader.moveUp();
                                                }
                                                orderInfo.setPackInfoList(packInfoList); // 将包裹集合放入订单对象中
                                            }
                                            reader.moveUp();
                                        }
                                        orderInfoList.add(orderInfo); // 添加订单信息
                                    }
                                    reader.moveUp();
                                }
                                boxInfo.setOrderInfoList(orderInfoList); // 将订单集合放入到箱子对象中
                            }
                            reader.moveUp();
                        }
                        boxInfoList.add(boxInfo); // 添加箱子对象
                    }
                    reader.moveUp();
                }
                shouHuoInfo.setBoxInfoList(boxInfoList); // 将箱子集合放入到收货对象中
            }
            reader.moveUp();
        }
        return shouHuoInfo;
    }
    
    /**
     * 格式化收货对象
     * 
     * @param shouHuoInfo
     * @param writer
     */
    private void formatShouHuo(ShouHuoInfo shouHuoInfo, HierarchicalStreamWriter writer) {
        //java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (null != shouHuoInfo) {
            if (null != shouHuoInfo.getBatchId()) {
                writer.startNode("batchId"); // 批次号
                writer.setValue(shouHuoInfo.getBatchId());
                writer.endNode();
            }
            if (null != shouHuoInfo.getUuId()) {
                writer.startNode("uuId"); // 防重码
                writer.setValue(shouHuoInfo.getUuId());
                writer.endNode();
            }
            if (null != shouHuoInfo.getCreateTime()) {
                writer.startNode("createTime"); // 接收时间
                writer.setValue(DateHelper.formatDateTime(shouHuoInfo.getCreateTime()));
                writer.endNode();
            }
            if (null != shouHuoInfo.getRemark()) {
                writer.startNode("remark"); // 备注
                writer.setValue(shouHuoInfo.getRemark());
                writer.endNode();
            }
            if (null != shouHuoInfo.getCallCode()) {
                writer.startNode("callCode"); // 调用代码，用来统计调用方身份，必填
                writer.setValue(shouHuoInfo.getCallCode());
                writer.endNode();
            }
            if (null != shouHuoInfo.getCarNo()) {
                writer.startNode("carNo"); // 车牌号
                writer.setValue(shouHuoInfo.getCarNo());
                writer.endNode();
            }
            List<BoxInfo> boxInfoList = shouHuoInfo.getBoxInfoList();
            if (boxInfoList != null && boxInfoList.size() > 0) {
                writer.startNode("boxInfoList"); // 箱子集合信息
                for (int i = 0; i < boxInfoList.size(); i++) {
                    BoxInfo boxInfo = boxInfoList.get(i);
                    if (null != boxInfo) {
                        this.formatBoxInfo(boxInfo, writer); // 格式化箱子对象
                    }
                }
                writer.endNode();
            }
        }
    }
    
    /**
     * 格式化箱子对象
     * 
     * @param boxInfo
     * @param writer
     */
    private void formatBoxInfo(BoxInfo boxInfo, HierarchicalStreamWriter writer) {
        if (null != boxInfo) {
            writer.startNode("BoxInfo"); // 箱子对象
            if (null != boxInfo.getBoxId()) {
                writer.startNode("boxId"); // 箱号
                writer.setValue(boxInfo.getBoxId());
                writer.endNode();
            }
            if (null != boxInfo.getSendType()) {
                writer.startNode("sendType"); // 发货类型
                writer.setValue(boxInfo.getSendType());
                writer.endNode();
            }
            if (null != boxInfo.getSendId()) {
                writer.startNode("sendId"); // 发货ID，和类型匹配
                writer.setValue(boxInfo.getSendId());
                writer.endNode();
            }
            if (null != boxInfo.getSendName()) {
                writer.startNode("sendName"); // 发货名称，和发货ID匹配
                writer.setValue(boxInfo.getSendName());
                writer.endNode();
            }
            if (null != boxInfo.getCarrierId()) {
                writer.startNode("carrierId"); // 司机Id（承运人）
                writer.setValue(boxInfo.getCarrierId());
                writer.endNode();
            }
            if (null != boxInfo.getCarrierName()) {
                writer.startNode("carrierName"); // 司机姓名（承运人）
                writer.setValue(boxInfo.getCarrierName());
                writer.endNode();
            }
            if (null != boxInfo.getOperatorId()) {
                writer.startNode("operatorId"); // 操作人ID
                writer.setValue(boxInfo.getOperatorId());
                writer.endNode();
            }
            if (null != boxInfo.getOperatorCode()) {
                writer.startNode("operatorCode"); // 操作人ERP账号
                writer.setValue(boxInfo.getOperatorCode());
                writer.endNode();
            }
            if (null != boxInfo.getOperatorName()) {
                writer.startNode("operatorName"); // 操作人姓名
                writer.setValue(boxInfo.getOperatorName());
                writer.endNode();
            }
            
            List<OrderInfo> orderInfoList = boxInfo.getOrderInfoList();
            if (null != orderInfoList && orderInfoList.size() > 0) {
                writer.startNode("orderInfoList"); // 订单集合信息
                for (int j = 0; j < orderInfoList.size(); j++) {
                    OrderInfo orderInfo = orderInfoList.get(j);
                    if (null != orderInfo) {
                        this.formatOrderInfo(orderInfo, writer); // 格式化订单对象
                    }
                }
                writer.endNode();
            }
            writer.endNode();
        }
    }
    
    /**
     * 格式化订单对象
     * 
     * @param orderInfo
     * @param writer
     */
    private void formatOrderInfo(OrderInfo orderInfo, HierarchicalStreamWriter writer) {
        if (null != orderInfo) {
            writer.startNode("OrderInfo"); // 订单对象
            if (null != orderInfo.getOrderId()) {
                writer.startNode("orderId"); // 订单ID
                writer.setValue(orderInfo.getOrderId());
                writer.endNode();
            }
            if (null != orderInfo.getOrderType()) {
                writer.startNode("orderType"); // 订单类型
                writer.setValue(orderInfo.getOrderType());
                writer.endNode();
            }
            if (null != orderInfo.getOrderSource()) {
                writer.startNode("orderSource"); // 订单来源
                writer.setValue(orderInfo.getOrderSource());
                writer.endNode();
            }
            if (null != orderInfo.getZdId()) {
                writer.startNode("zdId");                      // 订单原始站点（预分拣之后的站点）
                writer.setValue(orderInfo.getZdId());
                writer.endNode();
            }
            if (null != orderInfo.getOrderAdd()) {
                writer.startNode("orderAdd"); // 订单对应的客户地址
                writer.setValue(orderInfo.getOrderAdd());
                writer.endNode();
            }
            if (orderInfo.getPackNum() > 0) {
                writer.startNode("packNum"); // 包裹数
                writer.setValue(String.valueOf(orderInfo.getPackNum()));
                writer.endNode();
            }
            if (null != orderInfo.getDispatchType()) {
                writer.startNode("dispatchType"); // 发货类型
                writer.setValue(String.valueOf(orderInfo.getDispatchType()));
                writer.endNode();
            }
            List<PackInfo> packInfoList = orderInfo.getPackInfoList();
            if (null != packInfoList && packInfoList.size() > 0) {
                writer.startNode("packInfoList"); // 包裹集合
                for (int k = 0; k < packInfoList.size(); k++) {
                    PackInfo packInfo = packInfoList.get(k);
                    if (null != packInfo) {
                        this.formatPackInfo(packInfo, writer); // 格式化包裹对象
                    }
                }
                writer.endNode();
            }
            writer.endNode();
        }
    }
    
    /**
     * 格式化包裹对象
     * 
     * @param packInfo
     * @param writer
     */
    private void formatPackInfo(PackInfo packInfo, HierarchicalStreamWriter writer) {
        if (null != packInfo) {
            writer.startNode("PackInfo"); // 包裹对象
            if (null != packInfo.getPackNo()) {
                writer.startNode("packNo"); // 包裹号
                writer.setValue(packInfo.getPackNo());
                writer.endNode();
            }
            if (null != packInfo.getPackWeight()) {
                writer.startNode("packWeight"); // 包裹重量
                writer.setValue(packInfo.getPackWeight().toString());
                writer.endNode();
            }
            if (null != packInfo.getPackVolume()) {
                writer.startNode("packVolume"); // 包裹体积
                writer.setValue(packInfo.getPackVolume().toString());
                writer.endNode();
            }
            writer.endNode();
        }
    }
}
