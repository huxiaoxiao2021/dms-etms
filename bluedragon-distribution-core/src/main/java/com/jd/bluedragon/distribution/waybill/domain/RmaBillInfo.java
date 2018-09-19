package com.jd.bluedragon.distribution.waybill.domain;

import java.io.Serializable;

public class RmaBillInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 出库编码
     */
    private Long waybill_code;

    /**
     * 出库单号 -- getSkuSnListByOrderId - orderId
     */
    private Long outbound_order_code;

    /**
     * 商品编码  -- getSkuSnListByOrderId - skuCode
     */
    private String sku_code;

    /**
     * 备件条码  -- 运单接口 -- Goods -- sku
     */
    private String spare_code;

    /**
     * 商品名称  -- 运单接口 -- Goods -- sku
     */
    private String good_name;

    /**
     * 异常备注
     */
    private String execption_remark;

    /**
     * 包裹数 -- 运单接口 -- waybill -- goodNumber
     */
    private Long package_num;

    /**
     * 发货城市编号 ?
     */
    private Long send_city_id;

    /**
     * 发货城市名称  ?
     */
    private String send_city_name;

    /**
     * 操作站点编号
     */
    private String create_site_code;

    /**
     * 操作站点名称
     */
    private String create_site_name;

    /**
     * 省
     */
    private Long target_province_id;

    /**
     * 省
     */
    private String target_province_name;

    /**
     * 目的城市编号，一级 + 二级  ?
     */
    private Long target_city_id;

    /**
     * 目的城市，一级 + 二级  ?
     */
    private String target_city_name;

    /**
     * 发货操作人编号  -- 发货mq
     */
    private String send_operator_code;

    /**
     * 发货操作人 --发货mq
     */
    private String send_operator_name;

    /**
     * 发货操作人erp  --发货mq
     */
    private String send_operator_erp;

    /**
     * 分拣发货人电话 -- 暂时不取
     */
    private String send_operator_tel;

    /**
     * 商家名称  -- 运单接口 -- waybill
     */
    private String busi_name;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货人电话
     */
    private String receiver_mobile;

    /**
     * 收货人地址
     */
    private String receiver_address;

    /**
     * 是否打印
     */
    private Long is_print;

    public Long getWaybill_code() {
        return waybill_code;
    }

    public void setWaybill_code(Long waybill_code) {
        this.waybill_code = waybill_code;
    }

    public Long getOutbound_order_code() {
        return outbound_order_code;
    }

    public void setOutbound_order_code(Long outbound_order_code) {
        this.outbound_order_code = outbound_order_code;
    }

    public String getSku_code() {
        return sku_code;
    }

    public void setSku_code(String sku_code) {
        this.sku_code = sku_code;
    }

    public String getSpare_code() {
        return spare_code;
    }

    public void setSpare_code(String spare_code) {
        this.spare_code = spare_code;
    }

    public String getGood_name() {
        return good_name;
    }

    public void setGood_name(String good_name) {
        this.good_name = good_name;
    }

    public String getExecption_remark() {
        return execption_remark;
    }

    public void setExecption_remark(String execption_remark) {
        this.execption_remark = execption_remark;
    }

    public Long getPackage_num() {
        return package_num;
    }

    public void setPackage_num(Long package_num) {
        this.package_num = package_num;
    }

    public Long getSend_city_id() {
        return send_city_id;
    }

    public void setSend_city_id(Long send_city_id) {
        this.send_city_id = send_city_id;
    }

    public String getSend_city_name() {
        return send_city_name;
    }

    public void setSend_city_name(String send_city_name) {
        this.send_city_name = send_city_name;
    }

    public String getCreate_site_code() {
        return create_site_code;
    }

    public void setCreate_site_code(String create_site_code) {
        this.create_site_code = create_site_code;
    }

    public String getCreate_site_name() {
        return create_site_name;
    }

    public void setCreate_site_name(String create_site_name) {
        this.create_site_name = create_site_name;
    }

    public Long getTarget_province_id() {
        return target_province_id;
    }

    public void setTarget_province_id(Long target_province_id) {
        this.target_province_id = target_province_id;
    }

    public String getTarget_province_name() {
        return target_province_name;
    }

    public void setTarget_province_name(String target_province_name) {
        this.target_province_name = target_province_name;
    }

    public Long getTarget_city_id() {
        return target_city_id;
    }

    public void setTarget_city_id(Long target_city_id) {
        this.target_city_id = target_city_id;
    }

    public String getTarget_city_name() {
        return target_city_name;
    }

    public void setTarget_city_name(String target_city_name) {
        this.target_city_name = target_city_name;
    }

    public String getSend_operator_code() {
        return send_operator_code;
    }

    public void setSend_operator_code(String send_operator_code) {
        this.send_operator_code = send_operator_code;
    }

    public String getSend_operator_name() {
        return send_operator_name;
    }

    public void setSend_operator_name(String send_operator_name) {
        this.send_operator_name = send_operator_name;
    }

    public String getSend_operator_erp() {
        return send_operator_erp;
    }

    public void setSend_operator_erp(String send_operator_erp) {
        this.send_operator_erp = send_operator_erp;
    }

    public String getSend_operator_tel() {
        return send_operator_tel;
    }

    public void setSend_operator_tel(String send_operator_tel) {
        this.send_operator_tel = send_operator_tel;
    }

    public String getBusi_name() {
        return busi_name;
    }

    public void setBusi_name(String busi_name) {
        this.busi_name = busi_name;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver_mobile() {
        return receiver_mobile;
    }

    public void setReceiver_mobile(String receiver_mobile) {
        this.receiver_mobile = receiver_mobile;
    }

    public String getReceiver_address() {
        return receiver_address;
    }

    public void setReceiver_address(String receiver_address) {
        this.receiver_address = receiver_address;
    }

    public Long getIs_print() {
        return is_print;
    }

    public void setIs_print(Long is_print) {
        this.is_print = is_print;
    }
}
