package com.jd.bluedragon.core.security.log.builder;

import com.jd.securitylog.entity.ReqInfo;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log
 * @ClassName: SecurityLogReqInfoBuilder
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/5 18:08
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SecurityLogReqInfoBuilder {

    private ReqInfo reqInfo = new ReqInfo();

    public SecurityLogReqInfoBuilder telephone(String telephone) {
        this.reqInfo.setTelephone(telephone);
        return this;
    }

    public SecurityLogReqInfoBuilder telephoneIndex(String telephoneIndex) {
        this.reqInfo.setTelephoneIndex(telephoneIndex);
        return this;
    }

    public SecurityLogReqInfoBuilder phone(String phone) {
        this.reqInfo.setPhone(phone);
        return this;
    }

    public SecurityLogReqInfoBuilder phoneIndex(String phoneIndex) {
        this.reqInfo.setPhoneIndex(phoneIndex);
        return this;
    }

    public SecurityLogReqInfoBuilder email(String email) {
        this.reqInfo.setEmail(email);
        return this;
    }

    public SecurityLogReqInfoBuilder timeFrom(Long timeFrom) {
        this.reqInfo.setTimeFrom(timeFrom);
        return this;
    }

    public SecurityLogReqInfoBuilder timeTo(Long timeTo) {
        this.reqInfo.setTimeTo(timeTo);
        return this;
    }

    public SecurityLogReqInfoBuilder pin(String pin) {
        this.reqInfo.setPin(pin);
        return this;
    }

    public SecurityLogReqInfoBuilder orderId(String orderId) {
        this.reqInfo.setOrderId(orderId);
        return this;
    }

    public SecurityLogReqInfoBuilder carryBillId(String carryBillId) {
        this.reqInfo.setCarryBillId(carryBillId);
        return this;
    }

    public SecurityLogReqInfoBuilder erpId(String erpId) {
        this.reqInfo.setErpId(erpId);
        return this;
    }

    public SecurityLogReqInfoBuilder venderId(Integer venderId) {
        this.reqInfo.setVenderId(venderId);
        return this;
    }

    public SecurityLogReqInfoBuilder addrProvinceId(Integer addrProvinceId) {
        this.reqInfo.setAddrProvinceId(addrProvinceId);
        return this;
    }

    public SecurityLogReqInfoBuilder addrCityId(Integer addrCityId) {
        this.reqInfo.setAddrCityId(addrCityId);
        return this;
    }

    public SecurityLogReqInfoBuilder addrCountyId(Integer addrCountyId) {
        this.reqInfo.setAddrCountyId(addrCountyId);
        return this;
    }

    public SecurityLogReqInfoBuilder storeId(Integer storeId) {
        this.reqInfo.setStoreId(storeId);
        return this;
    }

    public SecurityLogReqInfoBuilder skuId(String skuId) {
        this.reqInfo.setSkuId(skuId);
        return this;
    }

    public SecurityLogReqInfoBuilder orderCd(String orderCd) {
        this.reqInfo.setOrderCd(orderCd);
        return this;
    }

    public SecurityLogReqInfoBuilder carryBillCd(String carryBillCd) {
        this.reqInfo.setCarryBillCd(carryBillCd);
        return this;
    }

    public SecurityLogReqInfoBuilder afsOrdId(String afsOrdId) {
        this.reqInfo.setAfsOrdId(afsOrdId);
        return this;
    }

    public SecurityLogReqInfoBuilder itemCate(String itemCate) {
        this.reqInfo.setItemCate(itemCate);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public SecurityLogReqInfoBuilder stageId(Long stageId) {
        this.reqInfo.setStageId(stageId);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public SecurityLogReqInfoBuilder couponKey(Integer couponKey) {
        this.reqInfo.setCouponKey(couponKey);
        return this;
    }

    public SecurityLogReqInfoBuilder barterId(String barterId) {
        this.reqInfo.setBarterId(barterId);
        return this;
    }

    public SecurityLogReqInfoBuilder consigneeId(String consigneeId) {
        this.reqInfo.setConsigneeId(consigneeId);
        return this;
    }

    public SecurityLogReqInfoBuilder bankNo(String bankNo) {
        this.reqInfo.setBankNo(bankNo);
        return this;
    }

    public SecurityLogReqInfoBuilder realName(String realName) {
        this.reqInfo.setRealName(realName);
        return this;
    }

    public SecurityLogReqInfoBuilder fileUrl(String fileUrl) {
        this.reqInfo.setFileUrl(fileUrl);
        return this;
    }

    public SecurityLogReqInfoBuilder ruleId(String ruleId) {
        this.reqInfo.setRuleId(ruleId);
        return this;
    }

    public SecurityLogReqInfoBuilder spuId(String spuId) {
        this.reqInfo.setSpuId(spuId);
        return this;
    }

    public SecurityLogReqInfoBuilder smartDeviceId(String smartDeviceId) {
        this.reqInfo.setSmartDeviceId(smartDeviceId);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public SecurityLogReqInfoBuilder additionParams(String additionParams) {
        this.reqInfo.setAdditionParams(additionParams);
        return this;
    }

    public SecurityLogReqInfoBuilder vendorCode(String vendorCode) {
        this.reqInfo.setVendorCode(vendorCode);
        return this;
    }

    public SecurityLogReqInfoBuilder vendorName(String vendorName) {
        this.reqInfo.setVendorName(vendorName);
        return this;
    }

    public SecurityLogReqInfoBuilder signId(String signId) {
        this.reqInfo.setSignId(signId);
        return this;
    }

    public SecurityLogReqInfoBuilder companyId(String companyId) {
        this.reqInfo.setCompanyId(companyId);
        return this;
    }

    public SecurityLogReqInfoBuilder areaId(String areaId) {
        this.reqInfo.setAreaId(areaId);
        return this;
    }

    public SecurityLogReqInfoBuilder townId(String townId) {
        this.reqInfo.setTownId(townId);
        return this;
    }

    public SecurityLogReqInfoBuilder addressDetail(String addressDetail) {
        this.reqInfo.setAddressDetail(addressDetail);
        return this;
    }

    public SecurityLogReqInfoBuilder addressDetailIndex(String addressDetailIndex) {
        this.reqInfo.setAddressDetailIndex(addressDetailIndex);
        return this;
    }

    public SecurityLogReqInfoBuilder postCode(String postCode) {
        this.reqInfo.setPostCode(postCode);
        return this;
    }

    public SecurityLogReqInfoBuilder accountId(String accountId) {
        this.reqInfo.setAccountId(accountId);
        return this;
    }

    public SecurityLogReqInfoBuilder accountName(String accountName) {
        this.reqInfo.setAccountName(accountName);
        return this;
    }

    public SecurityLogReqInfoBuilder shopId(String shopId) {
        this.reqInfo.setShopId(shopId);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantId(String merchantId) {
        this.reqInfo.setMerchantId(merchantId);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantContacts(String merchantContacts) {
        this.reqInfo.setMerchantContacts(merchantContacts);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantJdmErp(String merchantJdmErp) {
        this.reqInfo.setMerchantJdmErp(merchantJdmErp);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantContactsPhone(String merchantContactsPhone) {
        this.reqInfo.setMerchantContactsPhone(merchantContactsPhone);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantContactsPhoneIndex(String merchantContactsPhoneIndex) {
        this.reqInfo.setMerchantContactsPhoneIndex(merchantContactsPhoneIndex);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantContactsEmail(String merchantContactsEmail) {
        this.reqInfo.setMerchantContactsEmail(merchantContactsEmail);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantBrn(String merchantBrn) {
        this.reqInfo.setMerchantBrn(merchantBrn);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantZgRegName(String merchantZgRegName) {
        this.reqInfo.setMerchantZgRegName(merchantZgRegName);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantCompanyName(String merchantCompanyName) {
        this.reqInfo.setMerchantCompanyName(merchantCompanyName);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantBad(String merchantBad) {
        this.reqInfo.setMerchantBad(merchantBad);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantRad(String merchantRad) {
        this.reqInfo.setMerchantRad(merchantRad);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantlegalPerson(String merchantlegalPerson) {
        this.reqInfo.setMerchantlegalPerson(merchantlegalPerson);
        return this;
    }

    public SecurityLogReqInfoBuilder merchantlegalId(String merchantlegalId) {
        this.reqInfo.setMerchantlegalId(merchantlegalId);
        return this;
    }

    public SecurityLogReqInfoBuilder shopContacts(String shopContacts) {
        this.reqInfo.setShopContacts(shopContacts);
        return this;
    }

    public SecurityLogReqInfoBuilder shopContactsPhone(String shopContactsPhone) {
        this.reqInfo.setShopContactsPhone(shopContactsPhone);
        return this;
    }

    public SecurityLogReqInfoBuilder shopContactsPhoneIndex(String shopContactsPhoneIndex) {
        this.reqInfo.setShopContactsPhoneIndex(shopContactsPhoneIndex);
        return this;
    }

    public SecurityLogReqInfoBuilder shopTelephone(String shopTelephone) {
        this.reqInfo.setShopTelephone(shopTelephone);
        return this;
    }

    public SecurityLogReqInfoBuilder shopTelephoneIndex(String shopTelephoneIndex) {
        this.reqInfo.setShopTelephoneIndex(shopTelephoneIndex);
        return this;
    }

    public SecurityLogReqInfoBuilder shoplegalPerson(String shoplegalPerson) {
        this.reqInfo.setShoplegalPerson(shoplegalPerson);
        return this;
    }

    public SecurityLogReqInfoBuilder shopegalId(String shopegalId) {
        this.reqInfo.setShopegalId(shopegalId);
        return this;
    }

    public SecurityLogReqInfoBuilder shopJdmName(String shopJdmName) {
        this.reqInfo.setShopJdmName(shopJdmName);
        return this;
    }

    public SecurityLogReqInfoBuilder shopJdmTel(String shopJdmTel) {
        this.reqInfo.setShopJdmTel(shopJdmTel);
        return this;
    }

    public SecurityLogReqInfoBuilder shopJdmTelIndex(String shopJdmTelIndex) {
        this.reqInfo.setShopJdmTelIndex(shopJdmTelIndex);
        return this;
    }

    public SecurityLogReqInfoBuilder shopJdmEmail(String shopJdmEmail) {
        this.reqInfo.setShopJdmEmail(shopJdmEmail);
        return this;
    }

    public SecurityLogReqInfoBuilder shopJdmErp(String shopJdmErp) {
        this.reqInfo.setShopJdmErp(shopJdmErp);
        return this;
    }

    public SecurityLogReqInfoBuilder shopAddressProvince(String shopAddressProvince) {
        this.reqInfo.setShopAddressProvince(shopAddressProvince);
        return this;
    }

    public SecurityLogReqInfoBuilder shopAddressCity(String shopAddressCity) {
        this.reqInfo.setShopAddressCity(shopAddressCity);
        return this;
    }

    public SecurityLogReqInfoBuilder shopAddressCountry(String shopAddressCountry) {
        this.reqInfo.setShopAddressCountry(shopAddressCountry);
        return this;
    }

    public SecurityLogReqInfoBuilder cardCode(String cardCode) {
        this.reqInfo.setCardCode(cardCode);
        return this;
    }

    public SecurityLogReqInfoBuilder customerId(String customerId) {
        this.reqInfo.setCustomerId(customerId);
        return this;
    }

    public SecurityLogReqInfoBuilder customerName(String customerName) {
        this.reqInfo.setCustomerName(customerName);
        return this;
    }

    public SecurityLogReqInfoBuilder storeCode(String storeCode) {
        this.reqInfo.setStoreCode(storeCode);
        return this;
    }

    public SecurityLogReqInfoBuilder idCardNo(String idCardNo) {
        this.reqInfo.setIdCardNo(idCardNo);
        return this;
    }

    public SecurityLogReqInfoBuilder paimaiId(Long paimaiId) {
        this.reqInfo.setPaimaiId(paimaiId);
        return this;
    }

    public SecurityLogReqInfoBuilder kmCardNo(String kmCardNo) {
        this.reqInfo.setKmCardNo(kmCardNo);
        return this;
    }

    public SecurityLogReqInfoBuilder cfCode(String cfCode) {
        this.reqInfo.setCfCode(cfCode);
        return this;
    }

    public SecurityLogReqInfoBuilder payoutApplyId(String payoutApplyId) {
        this.reqInfo.setPayoutApplyId(payoutApplyId);
        return this;
    }

    public SecurityLogReqInfoBuilder inputParam(String inputParam) {
        this.reqInfo.setInputParam(inputParam);
        return this;
    }

    public SecurityLogReqInfoBuilder extKey1(String extKey1) {
        this.reqInfo.setExtKey1(extKey1);
        return this;
    }

    public SecurityLogReqInfoBuilder extValue1(String extValue1) {
        this.reqInfo.setExtValue1(extValue1);
        return this;
    }

    public SecurityLogReqInfoBuilder extKey2(String extKey2) {
        this.reqInfo.setExtKey2(extKey2);
        return this;
    }

    public SecurityLogReqInfoBuilder extValue2(String extValue2) {
        this.reqInfo.setExtValue2(extValue2);
        return this;
    }

    public SecurityLogReqInfoBuilder extKey3(String extKey3) {
        this.reqInfo.setExtKey3(extKey3);
        return this;
    }

    public SecurityLogReqInfoBuilder extValue3(String extValue3) {
        this.reqInfo.setExtValue3(extValue3);
        return this;
    }

    public SecurityLogReqInfoBuilder extKey4(String extKey4) {
        this.reqInfo.setExtKey4(extKey4);
        return this;
    }

    public SecurityLogReqInfoBuilder extValue4(String extValue4) {
        this.reqInfo.setExtValue4(extValue4);
        return this;
    }

    public SecurityLogReqInfoBuilder extKey5(String extKey5) {
        this.reqInfo.setExtKey5(extKey5);
        return this;
    }

    public SecurityLogReqInfoBuilder extValue5(String extValue5) {
        this.reqInfo.setExtValue5(extValue5);
        return this;
    }

    public ReqInfo build() {
        return this.reqInfo;
    }


}
