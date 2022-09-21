package com.jd.bluedragon.core.security.log.builder;

import com.jd.securitylog.entity.UniqueIdentifier;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log
 * @ClassName: SecurityLogUniqueIdentifierBuilder
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/6 14:13
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SecurityLogUniqueIdentifierBuilder {

    private UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();

    public SecurityLogUniqueIdentifierBuilder accountName(String accountName) {
        this.uniqueIdentifier.setAccountName(accountName);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder telephone(String telephone) {
        this.uniqueIdentifier.setTelephone(telephone);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder telephoneIndex(String telephoneIndex) {
        this.uniqueIdentifier.setTelephoneIndex(telephoneIndex);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder phone(String phone) {
        this.uniqueIdentifier.setPhone(phone);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder phoneIndex(String phoneIndex) {
        this.uniqueIdentifier.setPhoneIndex(phoneIndex);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder orderId(String orderId) {
        this.uniqueIdentifier.setOrderId(orderId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder carryBillId(String carryBillId) {
        this.uniqueIdentifier.setCarryBillId(carryBillId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder skuId(String skuId) {
        this.uniqueIdentifier.setSkuId(skuId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder afsOrdId(String afsOrdId) {
        this.uniqueIdentifier.setAfsOrdId(afsOrdId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder fileName(String fileName) {
        this.uniqueIdentifier.setFileName(fileName);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder fileUrl(String fileUrl) {
        this.uniqueIdentifier.setFileUrl(fileUrl);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder fileMd5(String fileMd5) {
        this.uniqueIdentifier.setFileMd5(fileMd5);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder barterId(String barterId) {
        this.uniqueIdentifier.setBarterId(barterId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder consigneeId(String consigneeId) {
        this.uniqueIdentifier.setConsigneeId(consigneeId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder csAppealId(String csAppealId) {
        this.uniqueIdentifier.setCsAppealId(csAppealId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder drawBackId(String drawBackId) {
        this.uniqueIdentifier.setDrawBackId(drawBackId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder csJmFeedbackId(String csJmFeedbackId) {
        this.uniqueIdentifier.setCsJmFeedbackId(csJmFeedbackId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder smartDeviceId(String smartDeviceId) {
        this.uniqueIdentifier.setSmartDeviceId(smartDeviceId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder vendorCode(String vendorCode) {
        this.uniqueIdentifier.setVendorCode(vendorCode);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder cooperationId(String cooperationId) {
        this.uniqueIdentifier.setCooperationId(cooperationId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder dimensionId(String dimensionId) {
        this.uniqueIdentifier.setDimensionId(dimensionId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder gmv(String gmv) {
        this.uniqueIdentifier.setGmv(gmv);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder signOrderNO(String signOrderNO) {
        this.uniqueIdentifier.setSignOrderNO(signOrderNO);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder bankNo(String bankNo) {
        this.uniqueIdentifier.setBankNo(bankNo);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder licenceNo(String licenceNo) {
        this.uniqueIdentifier.setLicenceNo(licenceNo);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder eabax(String eabax) {
        this.uniqueIdentifier.setEabax(eabax);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder templateId(String templateId) {
        this.uniqueIdentifier.setTemplateId(templateId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder providerCode(String providerCode) {
        this.uniqueIdentifier.setProviderCode(providerCode);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder branchCode(String branchCode) {
        this.uniqueIdentifier.setBranchCode(branchCode);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder docId(String docId) {
        this.uniqueIdentifier.setDocId(docId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder promoId(String promoId) {
        this.uniqueIdentifier.setPromoId(promoId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder categoryId(String categoryId) {
        this.uniqueIdentifier.setCategoryId(categoryId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder couponId(String couponId) {
        this.uniqueIdentifier.setCouponId(couponId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder companyQuaId(String companyQuaId) {
        this.uniqueIdentifier.setCompanyQuaId(companyQuaId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder industryQuaId(String industryQuaId) {
        this.uniqueIdentifier.setIndustryQuaId(industryQuaId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder brandQuaId(String brandQuaId) {
        this.uniqueIdentifier.setBrandQuaId(brandQuaId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder payBusinessId(String payBusinessId) {
        this.uniqueIdentifier.setPayBusinessId(payBusinessId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder payBusinessType(String payBusinessType) {
        this.uniqueIdentifier.setPayBusinessType(payBusinessType);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder subAccountId(String subAccountId) {
        this.uniqueIdentifier.setSubAccountId(subAccountId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder merchantId(String merchantId) {
        this.uniqueIdentifier.setMerchantId(merchantId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder shopId(String shopId) {
        this.uniqueIdentifier.setShopId(shopId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder cardCode(String cardCode) {
        this.uniqueIdentifier.setCardCode(cardCode);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder customerId(String customerId) {
        this.uniqueIdentifier.setCustomerId(customerId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder customerName(String customerName) {
        this.uniqueIdentifier.setCustomerName(customerName);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder reportId(String reportId) {
        this.uniqueIdentifier.setReportId(reportId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder doctorId(String doctorId) {
        this.uniqueIdentifier.setDoctorId(doctorId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder hospitalId(String hospitalId) {
        this.uniqueIdentifier.setHospitalId(hospitalId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder departId(String departId) {
        this.uniqueIdentifier.setDepartId(departId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder rxId(String rxId) {
        this.uniqueIdentifier.setRxId(rxId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder diagId(String diagId) {
        this.uniqueIdentifier.setDiagId(diagId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder patientId(String patientId) {
        this.uniqueIdentifier.setPatientId(patientId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder medicineId(String medicineId) {
        this.uniqueIdentifier.setMedicineId(medicineId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder purchaseId(String purchaseId) {
        this.uniqueIdentifier.setPurchaseId(purchaseId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder traceId(String traceId) {
        this.uniqueIdentifier.setTraceId(traceId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder storeCode(String storeCode) {
        this.uniqueIdentifier.setStoreCode(storeCode);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder storeContactId(String storeContactId) {
        this.uniqueIdentifier.setStoreContactId(storeContactId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder storeFinancialId(String storeFinancialId) {
        this.uniqueIdentifier.setStoreFinancialId(storeFinancialId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder storeManageId(String storeManageId) {
        this.uniqueIdentifier.setStoreManageId(storeManageId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder storeAgreementId(String storeAgreementId) {
        this.uniqueIdentifier.setStoreAgreementId(storeAgreementId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder storeAgreementJssKey(String storeAgreementJssKey) {
        this.uniqueIdentifier.setStoreAgreementJssKey(storeAgreementJssKey);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder spuId(String spuId) {
        this.uniqueIdentifier.setSpuId(spuId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder bookId(String bookId) {
        this.uniqueIdentifier.setBookId(bookId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder borrowId(String borrowId) {
        this.uniqueIdentifier.setBorrowId(borrowId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder scrappedId(String scrappedId) {
        this.uniqueIdentifier.setScrappedId(scrappedId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder paimaiId(Long paimaiId) {
        this.uniqueIdentifier.setPaimaiId(paimaiId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder auctionWareName(String auctionWareName) {
        this.uniqueIdentifier.setAuctionWareName(auctionWareName);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder diseaseDetail(String diseaseDetail) {
        this.uniqueIdentifier.setDiseaseDetail(diseaseDetail);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder kmCardNo(String kmCardNo) {
        this.uniqueIdentifier.setKmCardNo(kmCardNo);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder receiveName(String receiveName) {
        this.uniqueIdentifier.setReceiveName(receiveName);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder receiveAddress(String receiveAddress) {
        this.uniqueIdentifier.setReceiveAddress(receiveAddress);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder receiveAddressIndex(String receiveAddressIndex) {
        this.uniqueIdentifier.setReceiveAddressIndex(receiveAddressIndex);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder receivePhone(String receivePhone) {
        this.uniqueIdentifier.setReceivePhone(receivePhone);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder receivePhoneIndex(String receivePhoneIndex) {
        this.uniqueIdentifier.setReceivePhoneIndex(receivePhoneIndex);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder senderPhone(String senderPhone) {
        this.uniqueIdentifier.setSenderPhone(senderPhone);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder senderPhoneIndex(String senderPhoneIndex) {
        this.uniqueIdentifier.setSenderPhoneIndex(senderPhoneIndex);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder senderAddress(String senderAddress) {
        this.uniqueIdentifier.setSenderAddress(senderAddress);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder senderAddressIndex(String senderAddressIndex) {
        this.uniqueIdentifier.setSenderAddressIndex(senderAddressIndex);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder pickupCode(String pickupCode) {
        this.uniqueIdentifier.setPickupCode(pickupCode);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder merchantCode(String merchantCode) {
        this.uniqueIdentifier.setMerchantCode(merchantCode);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder certType(String certType) {
        this.uniqueIdentifier.setCertType(certType);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder certNum(String certNum) {
        this.uniqueIdentifier.setCertNum(certNum);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder transactionId(String transactionId) {
        this.uniqueIdentifier.setTransactionId(transactionId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder identityId(String identityId) {
        this.uniqueIdentifier.setIdentityId(identityId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder imgGroupId(String imgGroupId) {
        this.uniqueIdentifier.setImgGroupId(imgGroupId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder cfCode(String cfCode) {
        this.uniqueIdentifier.setCfCode(cfCode);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder payoutApplyId(String payoutApplyId) {
        this.uniqueIdentifier.setPayoutApplyId(payoutApplyId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder appealId(String appealId) {
        this.uniqueIdentifier.setAppealId(appealId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder disputeId(String disputeId) {
        this.uniqueIdentifier.setDisputeId(disputeId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder caseId(String caseId) {
        this.uniqueIdentifier.setCaseId(caseId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder balanceDetailId(String balanceDetailId) {
        this.uniqueIdentifier.setBalanceDetailId(balanceDetailId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder wosId(String wosId) {
        this.uniqueIdentifier.setWosId(wosId);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extKey1(String extKey1) {
        this.uniqueIdentifier.setExtKey1(extKey1);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extValue1(String extValue1) {
        this.uniqueIdentifier.setExtValue1(extValue1);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extKey2(String extKey2) {
        this.uniqueIdentifier.setExtKey2(extKey2);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extValue2(String extValue2) {
        this.uniqueIdentifier.setExtValue2(extValue2);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extKey3(String extKey3) {
        this.uniqueIdentifier.setExtKey3(extKey3);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extValue3(String extValue3) {
        this.uniqueIdentifier.setExtValue3(extValue3);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extKey4(String extKey4) {
        this.uniqueIdentifier.setExtKey4(extKey4);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extValue4(String extValue4) {
        this.uniqueIdentifier.setExtValue4(extValue4);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extKey5(String extKey5) {
        this.uniqueIdentifier.setExtKey5(extKey5);
        return this;
    }

    public SecurityLogUniqueIdentifierBuilder extValue5(String extValue5) {
        this.uniqueIdentifier.setExtValue5(extValue5);
        return this;
    }

    public UniqueIdentifier build() {
        return this.uniqueIdentifier;
    }



}
