package com.jd.bluedragon.external.crossbow.whems.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *     武汉邮政面单信息实体
 *     实体中定义的别名，不一致的情况下，别名根据交互文档来，javaBean对象这里根据拼写正确性来
 *
 * @author wuzuxiang
 * @since 2019/12/18
 **/
@XmlRootElement(name = "OrderShip")
@XmlAccessorType(XmlAccessType.FIELD)
public class WuHanEMSWaybillEntityDto {

    /*
    订单唯一标识
     */
    @XmlElement(name = "Id")
    private String id;

    /*
    订单号
     */
    @XmlElement(name = "OrderId")
    private String orderId;

    /*
    包裹号
     */
    @XmlElement(name = "BagId")
    private String bagId;

    /*
    用户姓名
     */
    @XmlElement(name = "UserName")
    private String userName;

    /*
    用户邮编
     */
    @XmlElement(name = "PostalCode")
    private String postalCode;

    /*
    用户所在省
     */
    @XmlElement(name = "Province")
    private String province;

    /*
    用户所在市
     */
    @XmlElement(name = "City")
    private String city;

    /*
    用户所在区
     */
    @XmlElement(name = "Area")
    private String area;

    /*
    用户详细地址
     */
    @XmlElement(name = "Address")
    private String address;

    /*
    手机号码
     */
    @XmlElement(name = "CellPhoneNumber")
    private String cellphoneNumber;

    /*
    固定电话
     */
    @XmlElement(name = "TelePhoneNumber")
    private String telPhoneNumber;

    /*
    邮箱地址
     */
    @XmlElement(name = "EmailAddress")
    private String emailAddress;

    /*
    要求送货时间
    01：只有工作日配送
    02：工作日双休日节假日均可配送
    03：只有双休日节假日配送
     */
    @XmlElement(name = "DeliveryTime")
    private String deliveryTime;

    /*
    重量 以克为单位，整型
     */
    @XmlElement(name = "Weight")
    private String weight;

    /*
    体积  保留两位小数点
     */
    @XmlElement(name = "Wbulk")
    private String wBulk;

    /*
    是否代收款
    1：是
    0：否
     */
    @XmlElement(name = "Collection")
    private String collection;

    /*
    应收金额，若collection字段为1则此字段必须有值 保留两位小数
     */
    @XmlElement(name = "NeedFund")
    private String needFund;

    /*
    备注
     */
    @XmlElement(name = "Remark")
    private String remark;

    /*
    包裹数量
     */
    @XmlElement(name = "BagQuatity")
    private String bagQuantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBagId() {
        return bagId;
    }


    public void setBagId(String bagId) {
        this.bagId = bagId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getTelPhoneNumber() {
        return telPhoneNumber;
    }

    public void setTelPhoneNumber(String telPhoneNumber) {
        this.telPhoneNumber = telPhoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getwBulk() {
        return wBulk;
    }

    public void setwBulk(String wBulk) {
        this.wBulk = wBulk;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getNeedFund() {
        return needFund;
    }

    public void setNeedFund(String needFund) {
        this.needFund = needFund;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBagQuantity() {
        return bagQuantity;
    }

    public void setBagQuantity(String bagQuantity) {
        this.bagQuantity = bagQuantity;
    }
}
