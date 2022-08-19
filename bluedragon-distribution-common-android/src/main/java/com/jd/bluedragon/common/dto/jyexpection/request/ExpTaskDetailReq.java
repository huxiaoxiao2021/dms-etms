package com.jd.bluedragon.common.dto.jyexpection.request;

public class ExpTaskDetailReq extends ExpBaseReq {
    // 重量
    private String weight;

    // 体积
    private String volume;

    // 同车包裹号
    private String togetherPackageCodes;

    // 批次号
    private String batchNo;

    // 内件描述
    private String innerDesc;

    // 外包装描述
    private String outerDesc;

    // 上级地
    private String from;

    // 下级地
    private String to;

    // 长宽高
    private String volumeDetail;

    // SN码
    private String sn;

    // 商品编码
    private String goodsNo;

    // 69码
    private String yardSixNine;

    // 件数
    private String goodsNum;

    // 封签号
    private String sealNumber;

    // 价值
    private String price;

    // 储位
    private String storage;

    // 业务主键id
    private String bizId;

    // 存储类型 0暂存 1提交
    private String saveType;

    // 操作人erp
    private String userErp;

    // 图片地址
    private String imageUrls;

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getTogetherPackageCodes() {
        return togetherPackageCodes;
    }

    public void setTogetherPackageCodes(String togetherPackageCodes) {
        this.togetherPackageCodes = togetherPackageCodes;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getInnerDesc() {
        return innerDesc;
    }

    public void setInnerDesc(String innerDesc) {
        this.innerDesc = innerDesc;
    }

    public String getOuterDesc() {
        return outerDesc;
    }

    public void setOuterDesc(String outerDesc) {
        this.outerDesc = outerDesc;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getVolumeDetail() {
        return volumeDetail;
    }

    public void setVolumeDetail(String volumeDetail) {
        this.volumeDetail = volumeDetail;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getYardSixNine() {
        return yardSixNine;
    }

    public void setYardSixNine(String yardSixNine) {
        this.yardSixNine = yardSixNine;
    }

    public String getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(String goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getSealNumber() {
        return sealNumber;
    }

    public void setSealNumber(String sealNumber) {
        this.sealNumber = sealNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSaveType() {
        return saveType;
    }

    public void setSaveType(String saveType) {
        this.saveType = saveType;
    }

    @Override
    public String getUserErp() {
        return userErp;
    }

    @Override
    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }
}
