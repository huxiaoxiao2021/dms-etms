package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.List;

import jd.oom.client.clientbean.Order;
import jd.oom.client.service.OrderService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-17 下午07:15:11 订单中间件整合服务
 */
public class OrderServiceHelper {
    
    private final static Log logger = LogFactory.getLog(OrderServiceHelper.class);
    
    public static void main(String[] args) {
        long orderId = 64322779;
        Waybill waybill = OrderServiceHelper.getWaybillByOrderId(orderId);
        System.out.println(waybill);
    }
    
    /**
     * 根据运单号获取运单信息并转换
     * 
     * @param orderId
     * @return Waybill
     * @remark 无POP相关信息
     */
    public static Waybill getWaybillByOrderId(long orderId) {
        Order order = OrderServiceHelper.getOriginalOrder(orderId, false,
                OrderServiceHelper.getFlag1());
        if (order == null) {
            OrderServiceHelper.logger.error("根据订单号、相关条件调用订单中间件获取订单信息Helper调用订单中间件 订单为空");
            return null;
        }
        Waybill waybill = new Waybill();
        waybill.setWaybillCode(String.valueOf(orderId));
        waybill.setSiteCode(order.getPartnerId());
        waybill.setSiteName(order.getIdPickSiteName());
        waybill.setPaymentType(order.getIdPaymentType());
        waybill.setSendPay(order.getSendPay());
        if (order.getWeight() != null) {
            waybill.setWeight(order.getWeight().doubleValue());
        }
        waybill.setAddress(order.getAddress());
        waybill.setOrgId(order.getIdCompanyBranch());
        waybill.setType(order.getOrderType());
        
        return waybill;
    }
    
    /**
     * 根据运单号获取历史运单信息并转换
     * 
     * @param orderId
     * @return Waybill
     * @remark 无POP相关信息
     */
    public static Waybill getHisWaybillByOrderId(long orderId) {
        jd.oom.client.orderfile.Order order = OrderServiceHelper.getHistoryOrder(orderId, false,
                OrderServiceHelper.getFlag1());
        if (order == null) {
            OrderServiceHelper.logger.error("根据订单号、相关条件调用订单中间件获取订单信息Helper调用订单中间件 订单为空");
            return null;
        }
        Waybill waybill = new Waybill();
        waybill.setWaybillCode(String.valueOf(orderId));
        waybill.setSiteCode(order.getPartnerId());
        waybill.setSiteName(order.getIdPickSiteName());
        waybill.setPaymentType(order.getIdPaymentType());
        waybill.setSendPay(order.getSendPay());
        if (order.getWeight() != null) {
            waybill.setWeight(order.getWeight().doubleValue());
        }
        waybill.setAddress(order.getAddress());
        waybill.setOrgId(order.getIdCompanyBranch());
        waybill.setType(order.getOrderType());
        
        return waybill;
    }
    
    /**
     * 根据订单号、相关条件调用订单中间件获取订单信息
     * 
     * @param orderId
     *            订单号
     * @param loadDetails
     *            是否加载订单明细
     * @param flagList
     *            相关条件
     * @return 订单
     */
    public static Order getOriginalOrder(long orderId, boolean loadDetails, List<String> flagList) {
        try {
            OrderService orderService = OrderHelper.getOrderHelper().getOrderService();
            return orderService.GetOrderById(orderId, loadDetails, flagList);
        } catch (Exception e) {
            OrderServiceHelper.logger.error("根据订单号、相关条件调用订单中间件获取订单信息Helper调用订单中间件异常：", e);
            return null;
        }
    }
    
    /**
     * 根据订单号、相关条件调用订单中间件获取历史订单信息
     * 
     * @param orderId
     *            订单号
     * @param loadDetails
     *            是否加载订单明细
     * @param flagList
     *            相关条件
     * @return 订单
     */
    public static jd.oom.client.orderfile.Order getHistoryOrder(long orderId, boolean loadDetails,
            List<String> flagList) {
        try {
            
            OrderService orderService = OrderHelper.getOrderHelper().getOrderService();
            // return orderService.GetOrderById(orderId, loadDetails, flagList);
            return orderService.getHistoryOrderById(orderId);
        } catch (Exception e) {
            OrderServiceHelper.logger.error("根据订单号、相关条件调用订单中间件获取订单信息Helper调用订单中间件异常：", e);
            return null;
        }
    }
    
    public static List<String> getFlag1() {
        List<String> flagList = new ArrayList<String>();
        flagList.add(Constants.JI_BEN_XIN_XI);
        flagList.add(Constants.ZHUANG_TAI);
        // flagList.add(Constants.GU_KE_XIN_XI);
        // flagList.add(Constants.JIN_ER);
        // flagList.add(Constants.ZHI_FU);
        flagList.add(Constants.CHU_KU);
        flagList.add(Constants.PEI_SONG_ZI_TI);
        // flagList.add(Constants.FA_PIAO);
        // flagList.add(Constants.CHAI_FEN);
        flagList.add(Constants.BEI_ZHU);
        flagList.add(Constants.QUAN_BU);
        // flagList.add(Constants.POP);
        return flagList;
    }
    
    private static class OrderHelper {
        private static OrderHelper orderHelper;
        
        private OrderService orderService;
        
        private OrderHelper() {
            if (this.orderService == null) {
                this.orderService = new OrderService();
            }
        }
        
        public static OrderHelper getOrderHelper() {
            if (OrderHelper.orderHelper == null) {
                OrderHelper.orderHelper = new OrderHelper();
            }
            return OrderHelper.orderHelper;
        }
        
        public OrderService getOrderService() {
            return this.orderService;
        }
    }
}
