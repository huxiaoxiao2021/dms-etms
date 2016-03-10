package com.jd.bluedragon.distribution.etms;

import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author dudong
 * @date 2015/9/22
 */
public class WaybillQueryWSImpl {
    public BaseEntity<List<PackageState>> getPkStateByWCode(String s) {
        PackageState bean = new PackageState();
        bean.setPackageStateId(-933995597970134862l);
        bean.setPackageBarcode("sE02iidUcI");
        bean.setWaybillCode("b4TrtTtutf");
        bean.setOperatorUserId(2727);
        bean.setOperatorUser("sd2JIIIJIG");
        bean.setOperatorSite("WknnnpBDmn");
        bean.setOperatorSiteId(7985);
        bean.setState("Mm7i7NNe4O");
        bean.setRemark("yJzJwXIMY4");
        bean.setCreateTime(new Date());
        bean.setYn(3380);
        List<PackageState> pickupGoodses = new ArrayList<PackageState>();
        pickupGoodses.add(bean);
        return new BaseEntity<List<PackageState>>(pickupGoodses);
    }

    
    public BaseEntity<List<PackageState>> getPkStateByWCodeAndState(String s, String s1) {
        PackageState bean = new PackageState();
        bean.setPackageStateId(159758524940095188l);
        bean.setPackageBarcode("AhWgQnFxxi");
        bean.setWaybillCode("guUdbcco8W");
        bean.setOperatorUserId(2940);
        bean.setOperatorUser("CJbXoqKvkf");
        bean.setOperatorSite("2EXEeEG7x5");
        bean.setOperatorSiteId(3086);
        bean.setState("rzHITIYUas");
        bean.setRemark("pPsSqQbqqH");
        bean.setCreateTime(new Date());
        bean.setYn(5234);
        List<PackageState> pickupGoodses = new ArrayList<PackageState>();
        pickupGoodses.add(bean);
        return new BaseEntity<List<PackageState>>(pickupGoodses);
    }

    
    public BaseEntity<List<PackageState>> getPkStateByWCodes(List<String> list) {
        PackageState bean = new PackageState();
        bean.setPackageStateId(159758524940095188l);
        bean.setPackageBarcode("AhWgQnFxxi");
        bean.setWaybillCode("guUdbcco8W");
        bean.setOperatorUserId(2940);
        bean.setOperatorUser("CJbXoqKvkf");
        bean.setOperatorSite("2EXEeEG7x5");
        bean.setOperatorSiteId(3086);
        bean.setState("rzHITIYUas");
        bean.setRemark("pPsSqQbqqH");
        bean.setCreateTime(new Date());
        bean.setYn(5234);
        List<PackageState> pickupGoodses = new ArrayList<PackageState>();
        pickupGoodses.add(bean);
        return new BaseEntity<List<PackageState>>(pickupGoodses);
    }

    
    public BaseEntity<List<PackageState>> getPkStateByPCode(String s) {
        PackageState bean = new PackageState();
        bean.setPackageStateId(159758524940095188l);
        bean.setPackageBarcode("AhWgQnFxxi");
        bean.setWaybillCode("guUdbcco8W");
        bean.setOperatorUserId(2940);
        bean.setOperatorUser("CJbXoqKvkf");
        bean.setOperatorSite("2EXEeEG7x5");
        bean.setOperatorSiteId(3086);
        bean.setState("rzHITIYUas");
        bean.setRemark("pPsSqQbqqH");
        bean.setCreateTime(new Date());
        bean.setYn(5234);
        List<PackageState> pickupGoodses = new ArrayList<PackageState>();
        pickupGoodses.add(bean);
        return new BaseEntity<List<PackageState>>(pickupGoodses);
    }

    
    public BaseEntity<List<PackageState>> getPkStateByPCodes(List<String> list) {
        PackageState bean = new PackageState();
        bean.setPackageStateId(159758524940095188l);
        bean.setPackageBarcode("AhWgQnFxxi");
        bean.setWaybillCode("guUdbcco8W");
        bean.setOperatorUserId(2940);
        bean.setOperatorUser("CJbXoqKvkf");
        bean.setOperatorSite("2EXEeEG7x5");
        bean.setOperatorSiteId(3086);
        bean.setState("rzHITIYUas");
        bean.setRemark("pPsSqQbqqH");
        bean.setCreateTime(new Date());
        bean.setYn(5234);
        List<PackageState> pickupGoodses = new ArrayList<PackageState>();
        pickupGoodses.add(bean);
        return new BaseEntity<List<PackageState>>(pickupGoodses);
    }

    
    public BaseEntity<WaybillAndWaybillState> getBWDtoByWCode(String s) {
        return null;
    }

    
    public BaseEntity<BigWaybillDto> getDataByChoice(String s, WChoice wChoice) {
        BigWaybillDto dto = new BigWaybillDto();
        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode("Rl8HlUVUW9");
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");

        WaybillManageDomain WaybillManageDomain = new WaybillManageDomain();
        WaybillManageDomain.setmId(-522477037880728779l);
        WaybillManageDomain.setWaybillCode("dyMNMwzywv");
        WaybillManageDomain.setActualCollection("xmFx9KM5O3");
        WaybillManageDomain.setCollectionType(3212);
        WaybillManageDomain.setWaybillState(3743);
        WaybillManageDomain.setSignState(2788);
        WaybillManageDomain.setSignHasReturn(9573);
        WaybillManageDomain.setWaybillType(3916);
        WaybillManageDomain.setCreateUser("jrKFHQz2Lf");
        WaybillManageDomain.setCreateUserId(9622);
        WaybillManageDomain.setCreateTime(new Date());
        WaybillManageDomain.setCreateSite(2514);
        WaybillManageDomain.setLaterUserId(2071);
        WaybillManageDomain.setLaterUser("z0qVC9zBQ8");
        WaybillManageDomain.setCancelReason("TwywO4O9V6");
        WaybillManageDomain.setPrint(2166);
        WaybillManageDomain.setYn(1102);
        WaybillManageDomain.setUpdateTime(new Date());
        WaybillManageDomain.setWaybillFlag(5880);
        WaybillManageDomain.setSiteId(2037);
        WaybillManageDomain.setSiteName("FYX5vMq3tG");
        WaybillManageDomain.setSiteType(3884);
        WaybillManageDomain.setStoreId(9636);
        WaybillManageDomain.setCky2(6587);
        WaybillManageDomain.setSourceCky2(8201);
        WaybillManageDomain.setWaybillOrgId(5690);
        WaybillManageDomain.setSortSiteId(3625);
        WaybillManageDomain.setReverseSiteId(9670);
        WaybillManageDomain.setOperateTime(new Date());

        DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
        DeliveryPackageD.setPackageId(5523262416506687326l);
        DeliveryPackageD.setPackageBarcode("Rzz0xXxXhN");
        DeliveryPackageD.setWaybillCode("mtRiYtVHdg");
        DeliveryPackageD.setVendorBarcode("oNOIrDoRTk");
        DeliveryPackageD.setGoodWeight(0.9778192902399022);
        DeliveryPackageD.setGoodVolume("OqRaAaAh1P");
        DeliveryPackageD.setRemark("UIHI3tDVnT");
        DeliveryPackageD.setCreateTime(new Date());
        DeliveryPackageD.setUpdateTime(new Date());
        DeliveryPackageD.setYn(3377);
        DeliveryPackageD.setServiceLevel("kFeF98S42L");
        DeliveryPackageD.setPackDate("7tECCmpACj");
        DeliveryPackageD.setPackWkNo("ubV8bbc5Gi");
        DeliveryPackageD.setAgainWeight(0.4104036068500395);

        Goods Goods = new Goods();
        Goods.setGoodId(7967733099567232650l);
        Goods.setPackBarcode("qyvOsLuMdJ");
        Goods.setWaybillCode("QU9rqbGrzv");
        Goods.setGoodName("rPoQBYRTO9");
        Goods.setSku("O2scdoojz0");
        Goods.setGoodCount(3824);
        Goods.setGoodPrice("1DvPgPgMdB");
        Goods.setGoodWeight(0.2128046587481609);
        Goods.setGoodVolume(0.85000610468906);
        Goods.setRemark("M5t03tMHGI");
        Goods.setCreateTime(new Date());
        Goods.setUpdateTime(new Date());
        Goods.setYn(7770);

        PickupTask PickupTask = new PickupTask();
        PickupTask.setPickupTaskId(8814451549516242698l);
        PickupTask.setCustomerId("xOMNvOswqs");
        PickupTask.setCustomerName("gDFx75lmN8");
        PickupTask.setCustomerAddress("PgYwhtoaAA");
        PickupTask.setTelphone("zJyyavaGta");
        PickupTask.setGweight(0.41769475634659725);
        PickupTask.setInvoiceId("RveWI4uwvM");
        PickupTask.setGvolume(0.27537104136323365);
        PickupTask.setSiteId(3824);
        PickupTask.setSiteName("imA1AbsBpQ");
        PickupTask.setNewWaybillCode("b73kNCVwOA");
        PickupTask.setPickupCode("hsssIKuSF6");
        PickupTask.setProductCode("KsFoDCnCww");
        PickupTask.setProductName("XHIfF32s2g");
        PickupTask.setAttaches("cPd6i3LyHI");
        PickupTask.setAmount(625);
        PickupTask.setFetchTime(new Date());
        PickupTask.setReceiptYn(94);
        PickupTask.setPreAllocationYn(5261);
        PickupTask.setEndCourier(7382);
        PickupTask.setEndCourierName("H7ANCKFFKb");
        PickupTask.setEndTime(new Date());
        PickupTask.setEndReason(1979);
        PickupTask.setStatus(7153);
        PickupTask.setRemark("As5Um6ltsu");
        PickupTask.setCreateTime(new Date());
        PickupTask.setUpdateTime(new Date());
        PickupTask.setIsInvoice(6947);
        PickupTask.setIsInvoiceCopy(4646);
        PickupTask.setIsPacking(3191);
        PickupTask.setIsTestReport(8104);
        PickupTask.setYn(7759);
        PickupTask.setPickupType(4300);
        PickupTask.setSurfaceCode("BnNPm6iN74");
        PickupTask.setOrgId(2131);
        PickupTask.setOrgName("NdJVFVWV9J");
        PickupTask.setCky2(8523);
        PickupTask.setStoreId(5546);
        PickupTask.setProvinceId(3751);
        PickupTask.setProvinceName("2TVcVQGYaA");
        PickupTask.setCityId(9113);
        PickupTask.setCityName("Pw1rzllRBU");
        PickupTask.setCountyId(3519);
        PickupTask.setCountyName("5YYI4SE6Q3");
        PickupTask.setTownId(2378);
        PickupTask.setTownName("VRCRCLWJWn");
        PickupTask.setOldWaybillCode("6ydDaRDDPT");
        PickupTask.setSendpay("a9aTTTcN7O");
        PickupTask.setIs3pl(7338);
        PickupTask.setServiceCode("ntM43OJ5GX");
        PickupTask.setPickupSource(516);
        PickupTask.setRecZipcode("PFiAzkzAXx");
        PickupTask.setNewPickupType(9800);
        PickupTask.setOpenFrontFlag(1527);
        PickupTask.setCourierId(1765);
        PickupTask.setAppointType(6694);
        PickupTask.setAppointBeginTime(new Date());
        PickupTask.setAppointEndTime(new Date());
        PickupTask.setAfterSaleType(7595);
        PickupTask.setMerchantId("5jg35g10jU");
        PickupTask.setMerchantTel("tOAnRQBRh4");
        PickupTask.setMerchantAddress("9xKLKL18aa");
        PickupTask.setGetInvoice(8255);
        PickupTask.setRoadCode("S32ei3zaWn");
        PickupTask.setSourceCode(192);

        ServiceBillPay ServiceBillPay = new ServiceBillPay();
        ServiceBillPay.setAmount(0.1553989014711712);
        ServiceBillPay.setBillServicetypeId("NrtsGXTiIc");
        ServiceBillPay.setCreateTime(new Date());
        ServiceBillPay.setCurrentStatus("GLI1LXIPjJ");
        ServiceBillPay.setDeliveryId("0qItdesu4G");
        ServiceBillPay.setId(-8470832482410114458l);
        ServiceBillPay.setMemo("1CdwWGF6pG");
        ServiceBillPay.setPayTime(new Date());
        ServiceBillPay.setVendorId(9253);
        ServiceBillPay.setYn(601);

        List<DeliveryPackageD> deliveryPackageDs = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
        deliveryPackageDs.add(DeliveryPackageD);

        List<Goods> goodsLis = new ArrayList<Goods>();
        goodsLis.add(Goods);

        dto.setGoodsList(goodsLis);
        dto.setPackageList(deliveryPackageDs);
        dto.setPickupTask(PickupTask);
        dto.setWaybill(bean);
        dto.setServiceBillPay(ServiceBillPay);
        dto.setWaybillState(WaybillManageDomain);
        return new BaseEntity<BigWaybillDto>(dto);
    }

    
    public BaseEntity<List<BigWaybillDto>> getDatasByChoice(List<String> list, WChoice wChoice) {
        BigWaybillDto dto = new BigWaybillDto();
        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode("Rl8HlUVUW9");
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");

        WaybillManageDomain WaybillManageDomain = new WaybillManageDomain();
        WaybillManageDomain.setmId(-522477037880728779l);
        WaybillManageDomain.setWaybillCode("dyMNMwzywv");
        WaybillManageDomain.setActualCollection("xmFx9KM5O3");
        WaybillManageDomain.setCollectionType(3212);
        WaybillManageDomain.setWaybillState(3743);
        WaybillManageDomain.setSignState(2788);
        WaybillManageDomain.setSignHasReturn(9573);
        WaybillManageDomain.setWaybillType(3916);
        WaybillManageDomain.setCreateUser("jrKFHQz2Lf");
        WaybillManageDomain.setCreateUserId(9622);
        WaybillManageDomain.setCreateTime(new Date());
        WaybillManageDomain.setCreateSite(2514);
        WaybillManageDomain.setLaterUserId(2071);
        WaybillManageDomain.setLaterUser("z0qVC9zBQ8");
        WaybillManageDomain.setCancelReason("TwywO4O9V6");
        WaybillManageDomain.setPrint(2166);
        WaybillManageDomain.setYn(1102);
        WaybillManageDomain.setUpdateTime(new Date());
        WaybillManageDomain.setWaybillFlag(5880);
        WaybillManageDomain.setSiteId(2037);
        WaybillManageDomain.setSiteName("FYX5vMq3tG");
        WaybillManageDomain.setSiteType(3884);
        WaybillManageDomain.setStoreId(9636);
        WaybillManageDomain.setCky2(6587);
        WaybillManageDomain.setSourceCky2(8201);
        WaybillManageDomain.setWaybillOrgId(5690);
        WaybillManageDomain.setSortSiteId(3625);
        WaybillManageDomain.setReverseSiteId(9670);
        WaybillManageDomain.setOperateTime(new Date());

        DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
        DeliveryPackageD.setPackageId(5523262416506687326l);
        DeliveryPackageD.setPackageBarcode("Rzz0xXxXhN");
        DeliveryPackageD.setWaybillCode("mtRiYtVHdg");
        DeliveryPackageD.setVendorBarcode("oNOIrDoRTk");
        DeliveryPackageD.setGoodWeight(0.9778192902399022);
        DeliveryPackageD.setGoodVolume("OqRaAaAh1P");
        DeliveryPackageD.setRemark("UIHI3tDVnT");
        DeliveryPackageD.setCreateTime(new Date());
        DeliveryPackageD.setUpdateTime(new Date());
        DeliveryPackageD.setYn(3377);
        DeliveryPackageD.setServiceLevel("kFeF98S42L");
        DeliveryPackageD.setPackDate("7tECCmpACj");
        DeliveryPackageD.setPackWkNo("ubV8bbc5Gi");
        DeliveryPackageD.setAgainWeight(0.4104036068500395);

        Goods Goods = new Goods();
        Goods.setGoodId(7967733099567232650l);
        Goods.setPackBarcode("qyvOsLuMdJ");
        Goods.setWaybillCode("QU9rqbGrzv");
        Goods.setGoodName("rPoQBYRTO9");
        Goods.setSku("O2scdoojz0");
        Goods.setGoodCount(3824);
        Goods.setGoodPrice("1DvPgPgMdB");
        Goods.setGoodWeight(0.2128046587481609);
        Goods.setGoodVolume(0.85000610468906);
        Goods.setRemark("M5t03tMHGI");
        Goods.setCreateTime(new Date());
        Goods.setUpdateTime(new Date());
        Goods.setYn(7770);

        PickupTask PickupTask = new PickupTask();
        PickupTask.setPickupTaskId(8814451549516242698l);
        PickupTask.setCustomerId("xOMNvOswqs");
        PickupTask.setCustomerName("gDFx75lmN8");
        PickupTask.setCustomerAddress("PgYwhtoaAA");
        PickupTask.setTelphone("zJyyavaGta");
        PickupTask.setGweight(0.41769475634659725);
        PickupTask.setInvoiceId("RveWI4uwvM");
        PickupTask.setGvolume(0.27537104136323365);
        PickupTask.setSiteId(3824);
        PickupTask.setSiteName("imA1AbsBpQ");
        PickupTask.setNewWaybillCode("b73kNCVwOA");
        PickupTask.setPickupCode("hsssIKuSF6");
        PickupTask.setProductCode("KsFoDCnCww");
        PickupTask.setProductName("XHIfF32s2g");
        PickupTask.setAttaches("cPd6i3LyHI");
        PickupTask.setAmount(625);
        PickupTask.setFetchTime(new Date());
        PickupTask.setReceiptYn(94);
        PickupTask.setPreAllocationYn(5261);
        PickupTask.setEndCourier(7382);
        PickupTask.setEndCourierName("H7ANCKFFKb");
        PickupTask.setEndTime(new Date());
        PickupTask.setEndReason(1979);
        PickupTask.setStatus(7153);
        PickupTask.setRemark("As5Um6ltsu");
        PickupTask.setCreateTime(new Date());
        PickupTask.setUpdateTime(new Date());
        PickupTask.setIsInvoice(6947);
        PickupTask.setIsInvoiceCopy(4646);
        PickupTask.setIsPacking(3191);
        PickupTask.setIsTestReport(8104);
        PickupTask.setYn(7759);
        PickupTask.setPickupType(4300);
        PickupTask.setSurfaceCode("BnNPm6iN74");
        PickupTask.setOrgId(2131);
        PickupTask.setOrgName("NdJVFVWV9J");
        PickupTask.setCky2(8523);
        PickupTask.setStoreId(5546);
        PickupTask.setProvinceId(3751);
        PickupTask.setProvinceName("2TVcVQGYaA");
        PickupTask.setCityId(9113);
        PickupTask.setCityName("Pw1rzllRBU");
        PickupTask.setCountyId(3519);
        PickupTask.setCountyName("5YYI4SE6Q3");
        PickupTask.setTownId(2378);
        PickupTask.setTownName("VRCRCLWJWn");
        PickupTask.setOldWaybillCode("6ydDaRDDPT");
        PickupTask.setSendpay("a9aTTTcN7O");
        PickupTask.setIs3pl(7338);
        PickupTask.setServiceCode("ntM43OJ5GX");
        PickupTask.setPickupSource(516);
        PickupTask.setRecZipcode("PFiAzkzAXx");
        PickupTask.setNewPickupType(9800);
        PickupTask.setOpenFrontFlag(1527);
        PickupTask.setCourierId(1765);
        PickupTask.setAppointType(6694);
        PickupTask.setAppointBeginTime(new Date());
        PickupTask.setAppointEndTime(new Date());
        PickupTask.setAfterSaleType(7595);
        PickupTask.setMerchantId("5jg35g10jU");
        PickupTask.setMerchantTel("tOAnRQBRh4");
        PickupTask.setMerchantAddress("9xKLKL18aa");
        PickupTask.setGetInvoice(8255);
        PickupTask.setRoadCode("S32ei3zaWn");
        PickupTask.setSourceCode(192);

        ServiceBillPay ServiceBillPay = new ServiceBillPay();
        ServiceBillPay.setAmount(0.1553989014711712);
        ServiceBillPay.setBillServicetypeId("NrtsGXTiIc");
        ServiceBillPay.setCreateTime(new Date());
        ServiceBillPay.setCurrentStatus("GLI1LXIPjJ");
        ServiceBillPay.setDeliveryId("0qItdesu4G");
        ServiceBillPay.setId(-8470832482410114458l);
        ServiceBillPay.setMemo("1CdwWGF6pG");
        ServiceBillPay.setPayTime(new Date());
        ServiceBillPay.setVendorId(9253);
        ServiceBillPay.setYn(601);

        List<DeliveryPackageD> deliveryPackageDs = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
        deliveryPackageDs.add(DeliveryPackageD);

        List<Goods> goodsLis = new ArrayList<Goods>();
        goodsLis.add(Goods);

        dto.setGoodsList(goodsLis);
        dto.setPackageList(deliveryPackageDs);
        dto.setPickupTask(PickupTask);
        dto.setWaybill(bean);
        dto.setServiceBillPay(ServiceBillPay);
        dto.setWaybillState(WaybillManageDomain);
        List<BigWaybillDto> bigWaybillDtos = new ArrayList<BigWaybillDto>();
        bigWaybillDtos.add(dto);
        return new BaseEntity<List<BigWaybillDto>>(bigWaybillDtos);
    }

    
    public BaseEntity<BigWaybillDto> getPsyshDataByWCode(String s) {
        BigWaybillDto dto = new BigWaybillDto();
        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode("Rl8HlUVUW9");
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");

        WaybillManageDomain WaybillManageDomain = new WaybillManageDomain();
        WaybillManageDomain.setmId(-522477037880728779l);
        WaybillManageDomain.setWaybillCode("dyMNMwzywv");
        WaybillManageDomain.setActualCollection("xmFx9KM5O3");
        WaybillManageDomain.setCollectionType(3212);
        WaybillManageDomain.setWaybillState(3743);
        WaybillManageDomain.setSignState(2788);
        WaybillManageDomain.setSignHasReturn(9573);
        WaybillManageDomain.setWaybillType(3916);
        WaybillManageDomain.setCreateUser("jrKFHQz2Lf");
        WaybillManageDomain.setCreateUserId(9622);
        WaybillManageDomain.setCreateTime(new Date());
        WaybillManageDomain.setCreateSite(2514);
        WaybillManageDomain.setLaterUserId(2071);
        WaybillManageDomain.setLaterUser("z0qVC9zBQ8");
        WaybillManageDomain.setCancelReason("TwywO4O9V6");
        WaybillManageDomain.setPrint(2166);
        WaybillManageDomain.setYn(1102);
        WaybillManageDomain.setUpdateTime(new Date());
        WaybillManageDomain.setWaybillFlag(5880);
        WaybillManageDomain.setSiteId(2037);
        WaybillManageDomain.setSiteName("FYX5vMq3tG");
        WaybillManageDomain.setSiteType(3884);
        WaybillManageDomain.setStoreId(9636);
        WaybillManageDomain.setCky2(6587);
        WaybillManageDomain.setSourceCky2(8201);
        WaybillManageDomain.setWaybillOrgId(5690);
        WaybillManageDomain.setSortSiteId(3625);
        WaybillManageDomain.setReverseSiteId(9670);
        WaybillManageDomain.setOperateTime(new Date());

        DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
        DeliveryPackageD.setPackageId(5523262416506687326l);
        DeliveryPackageD.setPackageBarcode("Rzz0xXxXhN");
        DeliveryPackageD.setWaybillCode("mtRiYtVHdg");
        DeliveryPackageD.setVendorBarcode("oNOIrDoRTk");
        DeliveryPackageD.setGoodWeight(0.9778192902399022);
        DeliveryPackageD.setGoodVolume("OqRaAaAh1P");
        DeliveryPackageD.setRemark("UIHI3tDVnT");
        DeliveryPackageD.setCreateTime(new Date());
        DeliveryPackageD.setUpdateTime(new Date());
        DeliveryPackageD.setYn(3377);
        DeliveryPackageD.setServiceLevel("kFeF98S42L");
        DeliveryPackageD.setPackDate("7tECCmpACj");
        DeliveryPackageD.setPackWkNo("ubV8bbc5Gi");
        DeliveryPackageD.setAgainWeight(0.4104036068500395);

        Goods Goods = new Goods();
        Goods.setGoodId(7967733099567232650l);
        Goods.setPackBarcode("qyvOsLuMdJ");
        Goods.setWaybillCode("QU9rqbGrzv");
        Goods.setGoodName("rPoQBYRTO9");
        Goods.setSku("O2scdoojz0");
        Goods.setGoodCount(3824);
        Goods.setGoodPrice("1DvPgPgMdB");
        Goods.setGoodWeight(0.2128046587481609);
        Goods.setGoodVolume(0.85000610468906);
        Goods.setRemark("M5t03tMHGI");
        Goods.setCreateTime(new Date());
        Goods.setUpdateTime(new Date());
        Goods.setYn(7770);

        PickupTask PickupTask = new PickupTask();
        PickupTask.setPickupTaskId(8814451549516242698l);
        PickupTask.setCustomerId("xOMNvOswqs");
        PickupTask.setCustomerName("gDFx75lmN8");
        PickupTask.setCustomerAddress("PgYwhtoaAA");
        PickupTask.setTelphone("zJyyavaGta");
        PickupTask.setGweight(0.41769475634659725);
        PickupTask.setInvoiceId("RveWI4uwvM");
        PickupTask.setGvolume(0.27537104136323365);
        PickupTask.setSiteId(3824);
        PickupTask.setSiteName("imA1AbsBpQ");
        PickupTask.setNewWaybillCode("b73kNCVwOA");
        PickupTask.setPickupCode("hsssIKuSF6");
        PickupTask.setProductCode("KsFoDCnCww");
        PickupTask.setProductName("XHIfF32s2g");
        PickupTask.setAttaches("cPd6i3LyHI");
        PickupTask.setAmount(625);
        PickupTask.setFetchTime(new Date());
        PickupTask.setReceiptYn(94);
        PickupTask.setPreAllocationYn(5261);
        PickupTask.setEndCourier(7382);
        PickupTask.setEndCourierName("H7ANCKFFKb");
        PickupTask.setEndTime(new Date());
        PickupTask.setEndReason(1979);
        PickupTask.setStatus(7153);
        PickupTask.setRemark("As5Um6ltsu");
        PickupTask.setCreateTime(new Date());
        PickupTask.setUpdateTime(new Date());
        PickupTask.setIsInvoice(6947);
        PickupTask.setIsInvoiceCopy(4646);
        PickupTask.setIsPacking(3191);
        PickupTask.setIsTestReport(8104);
        PickupTask.setYn(7759);
        PickupTask.setPickupType(4300);
        PickupTask.setSurfaceCode("BnNPm6iN74");
        PickupTask.setOrgId(2131);
        PickupTask.setOrgName("NdJVFVWV9J");
        PickupTask.setCky2(8523);
        PickupTask.setStoreId(5546);
        PickupTask.setProvinceId(3751);
        PickupTask.setProvinceName("2TVcVQGYaA");
        PickupTask.setCityId(9113);
        PickupTask.setCityName("Pw1rzllRBU");
        PickupTask.setCountyId(3519);
        PickupTask.setCountyName("5YYI4SE6Q3");
        PickupTask.setTownId(2378);
        PickupTask.setTownName("VRCRCLWJWn");
        PickupTask.setOldWaybillCode("6ydDaRDDPT");
        PickupTask.setSendpay("a9aTTTcN7O");
        PickupTask.setIs3pl(7338);
        PickupTask.setServiceCode("ntM43OJ5GX");
        PickupTask.setPickupSource(516);
        PickupTask.setRecZipcode("PFiAzkzAXx");
        PickupTask.setNewPickupType(9800);
        PickupTask.setOpenFrontFlag(1527);
        PickupTask.setCourierId(1765);
        PickupTask.setAppointType(6694);
        PickupTask.setAppointBeginTime(new Date());
        PickupTask.setAppointEndTime(new Date());
        PickupTask.setAfterSaleType(7595);
        PickupTask.setMerchantId("5jg35g10jU");
        PickupTask.setMerchantTel("tOAnRQBRh4");
        PickupTask.setMerchantAddress("9xKLKL18aa");
        PickupTask.setGetInvoice(8255);
        PickupTask.setRoadCode("S32ei3zaWn");
        PickupTask.setSourceCode(192);

        ServiceBillPay ServiceBillPay = new ServiceBillPay();
        ServiceBillPay.setAmount(0.1553989014711712);
        ServiceBillPay.setBillServicetypeId("NrtsGXTiIc");
        ServiceBillPay.setCreateTime(new Date());
        ServiceBillPay.setCurrentStatus("GLI1LXIPjJ");
        ServiceBillPay.setDeliveryId("0qItdesu4G");
        ServiceBillPay.setId(-8470832482410114458l);
        ServiceBillPay.setMemo("1CdwWGF6pG");
        ServiceBillPay.setPayTime(new Date());
        ServiceBillPay.setVendorId(9253);
        ServiceBillPay.setYn(601);

        List<DeliveryPackageD> deliveryPackageDs = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
        deliveryPackageDs.add(DeliveryPackageD);

        List<Goods> goodsLis = new ArrayList<Goods>();
        goodsLis.add(Goods);

        dto.setGoodsList(goodsLis);
        dto.setPackageList(deliveryPackageDs);
        dto.setPickupTask(PickupTask);
        dto.setWaybill(bean);
        dto.setServiceBillPay(ServiceBillPay);
        dto.setWaybillState(WaybillManageDomain);
        return new BaseEntity<BigWaybillDto>(dto);
    }

    
    public BaseEntity<List<BigWaybillDto>> getPsyshDatasByWCodes(List<String> list) {
        BigWaybillDto dto = new BigWaybillDto();
        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode("Rl8HlUVUW9");
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");

        WaybillManageDomain WaybillManageDomain = new WaybillManageDomain();
        WaybillManageDomain.setmId(-522477037880728779l);
        WaybillManageDomain.setWaybillCode("dyMNMwzywv");
        WaybillManageDomain.setActualCollection("xmFx9KM5O3");
        WaybillManageDomain.setCollectionType(3212);
        WaybillManageDomain.setWaybillState(3743);
        WaybillManageDomain.setSignState(2788);
        WaybillManageDomain.setSignHasReturn(9573);
        WaybillManageDomain.setWaybillType(3916);
        WaybillManageDomain.setCreateUser("jrKFHQz2Lf");
        WaybillManageDomain.setCreateUserId(9622);
        WaybillManageDomain.setCreateTime(new Date());
        WaybillManageDomain.setCreateSite(2514);
        WaybillManageDomain.setLaterUserId(2071);
        WaybillManageDomain.setLaterUser("z0qVC9zBQ8");
        WaybillManageDomain.setCancelReason("TwywO4O9V6");
        WaybillManageDomain.setPrint(2166);
        WaybillManageDomain.setYn(1102);
        WaybillManageDomain.setUpdateTime(new Date());
        WaybillManageDomain.setWaybillFlag(5880);
        WaybillManageDomain.setSiteId(2037);
        WaybillManageDomain.setSiteName("FYX5vMq3tG");
        WaybillManageDomain.setSiteType(3884);
        WaybillManageDomain.setStoreId(9636);
        WaybillManageDomain.setCky2(6587);
        WaybillManageDomain.setSourceCky2(8201);
        WaybillManageDomain.setWaybillOrgId(5690);
        WaybillManageDomain.setSortSiteId(3625);
        WaybillManageDomain.setReverseSiteId(9670);
        WaybillManageDomain.setOperateTime(new Date());

        DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
        DeliveryPackageD.setPackageId(5523262416506687326l);
        DeliveryPackageD.setPackageBarcode("Rzz0xXxXhN");
        DeliveryPackageD.setWaybillCode("mtRiYtVHdg");
        DeliveryPackageD.setVendorBarcode("oNOIrDoRTk");
        DeliveryPackageD.setGoodWeight(0.9778192902399022);
        DeliveryPackageD.setGoodVolume("OqRaAaAh1P");
        DeliveryPackageD.setRemark("UIHI3tDVnT");
        DeliveryPackageD.setCreateTime(new Date());
        DeliveryPackageD.setUpdateTime(new Date());
        DeliveryPackageD.setYn(3377);
        DeliveryPackageD.setServiceLevel("kFeF98S42L");
        DeliveryPackageD.setPackDate("7tECCmpACj");
        DeliveryPackageD.setPackWkNo("ubV8bbc5Gi");
        DeliveryPackageD.setAgainWeight(0.4104036068500395);

        Goods Goods = new Goods();
        Goods.setGoodId(7967733099567232650l);
        Goods.setPackBarcode("qyvOsLuMdJ");
        Goods.setWaybillCode("QU9rqbGrzv");
        Goods.setGoodName("rPoQBYRTO9");
        Goods.setSku("O2scdoojz0");
        Goods.setGoodCount(3824);
        Goods.setGoodPrice("1DvPgPgMdB");
        Goods.setGoodWeight(0.2128046587481609);
        Goods.setGoodVolume(0.85000610468906);
        Goods.setRemark("M5t03tMHGI");
        Goods.setCreateTime(new Date());
        Goods.setUpdateTime(new Date());
        Goods.setYn(7770);

        PickupTask PickupTask = new PickupTask();
        PickupTask.setPickupTaskId(8814451549516242698l);
        PickupTask.setCustomerId("xOMNvOswqs");
        PickupTask.setCustomerName("gDFx75lmN8");
        PickupTask.setCustomerAddress("PgYwhtoaAA");
        PickupTask.setTelphone("zJyyavaGta");
        PickupTask.setGweight(0.41769475634659725);
        PickupTask.setInvoiceId("RveWI4uwvM");
        PickupTask.setGvolume(0.27537104136323365);
        PickupTask.setSiteId(3824);
        PickupTask.setSiteName("imA1AbsBpQ");
        PickupTask.setNewWaybillCode("b73kNCVwOA");
        PickupTask.setPickupCode("hsssIKuSF6");
        PickupTask.setProductCode("KsFoDCnCww");
        PickupTask.setProductName("XHIfF32s2g");
        PickupTask.setAttaches("cPd6i3LyHI");
        PickupTask.setAmount(625);
        PickupTask.setFetchTime(new Date());
        PickupTask.setReceiptYn(94);
        PickupTask.setPreAllocationYn(5261);
        PickupTask.setEndCourier(7382);
        PickupTask.setEndCourierName("H7ANCKFFKb");
        PickupTask.setEndTime(new Date());
        PickupTask.setEndReason(1979);
        PickupTask.setStatus(7153);
        PickupTask.setRemark("As5Um6ltsu");
        PickupTask.setCreateTime(new Date());
        PickupTask.setUpdateTime(new Date());
        PickupTask.setIsInvoice(6947);
        PickupTask.setIsInvoiceCopy(4646);
        PickupTask.setIsPacking(3191);
        PickupTask.setIsTestReport(8104);
        PickupTask.setYn(7759);
        PickupTask.setPickupType(4300);
        PickupTask.setSurfaceCode("BnNPm6iN74");
        PickupTask.setOrgId(2131);
        PickupTask.setOrgName("NdJVFVWV9J");
        PickupTask.setCky2(8523);
        PickupTask.setStoreId(5546);
        PickupTask.setProvinceId(3751);
        PickupTask.setProvinceName("2TVcVQGYaA");
        PickupTask.setCityId(9113);
        PickupTask.setCityName("Pw1rzllRBU");
        PickupTask.setCountyId(3519);
        PickupTask.setCountyName("5YYI4SE6Q3");
        PickupTask.setTownId(2378);
        PickupTask.setTownName("VRCRCLWJWn");
        PickupTask.setOldWaybillCode("6ydDaRDDPT");
        PickupTask.setSendpay("a9aTTTcN7O");
        PickupTask.setIs3pl(7338);
        PickupTask.setServiceCode("ntM43OJ5GX");
        PickupTask.setPickupSource(516);
        PickupTask.setRecZipcode("PFiAzkzAXx");
        PickupTask.setNewPickupType(9800);
        PickupTask.setOpenFrontFlag(1527);
        PickupTask.setCourierId(1765);
        PickupTask.setAppointType(6694);
        PickupTask.setAppointBeginTime(new Date());
        PickupTask.setAppointEndTime(new Date());
        PickupTask.setAfterSaleType(7595);
        PickupTask.setMerchantId("5jg35g10jU");
        PickupTask.setMerchantTel("tOAnRQBRh4");
        PickupTask.setMerchantAddress("9xKLKL18aa");
        PickupTask.setGetInvoice(8255);
        PickupTask.setRoadCode("S32ei3zaWn");
        PickupTask.setSourceCode(192);

        ServiceBillPay ServiceBillPay = new ServiceBillPay();
        ServiceBillPay.setAmount(0.1553989014711712);
        ServiceBillPay.setBillServicetypeId("NrtsGXTiIc");
        ServiceBillPay.setCreateTime(new Date());
        ServiceBillPay.setCurrentStatus("GLI1LXIPjJ");
        ServiceBillPay.setDeliveryId("0qItdesu4G");
        ServiceBillPay.setId(-8470832482410114458l);
        ServiceBillPay.setMemo("1CdwWGF6pG");
        ServiceBillPay.setPayTime(new Date());
        ServiceBillPay.setVendorId(9253);
        ServiceBillPay.setYn(601);

        List<DeliveryPackageD> deliveryPackageDs = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
        deliveryPackageDs.add(DeliveryPackageD);

        List<Goods> goodsLis = new ArrayList<Goods>();
        goodsLis.add(Goods);

        dto.setGoodsList(goodsLis);
        dto.setPackageList(deliveryPackageDs);
        dto.setPickupTask(PickupTask);
        dto.setWaybill(bean);
        dto.setServiceBillPay(ServiceBillPay);
        dto.setWaybillState(WaybillManageDomain);
        List<BigWaybillDto> dtos = new ArrayList<BigWaybillDto>();
        dtos.add(dto);
        return new BaseEntity<List<BigWaybillDto>>(dtos);
    }

    
    public BaseEntity<List<Goods>> getGoodsDataByWCode(String s) {
        return null;
    }

    
    public BaseEntity<List<WaybillStateDto>> getWaybillStates() {
        return null;
    }

    
    public BaseEntity<List<WaybillManageDomain>> getOwnWaybill(OwnQueryDto ownQueryDto) {
        return null;
    }

    
    public BaseEntity<List<PackageState>> getAllOperations(String s) {
        return null;
    }

    
    public BaseEntity<Page<DistanceSearchDto>> getDistanceList(DistanceSearchDto distanceSearchDto) {
        return null;
    }

    
    public BaseEntity<Waybill> getWaybillByWaybillCode(String s) {
        return null;
    }

    
    public BaseEntity<List<DeliveryPackageD>> getPackListByWaybillCode(String s) {
        return null;
    }

    
    public BaseEntity<BigWaybillDto> getWaybillAndPackByWaybillCode(String s) {
        BigWaybillDto dto = new BigWaybillDto();
        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode(s);
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");

        WaybillManageDomain WaybillManageDomain = new WaybillManageDomain();
        WaybillManageDomain.setmId(-522477037880728779l);
        WaybillManageDomain.setWaybillCode(s);
        WaybillManageDomain.setActualCollection("xmFx9KM5O3");
        WaybillManageDomain.setCollectionType(3212);
        WaybillManageDomain.setWaybillState(3743);
        WaybillManageDomain.setSignState(2788);
        WaybillManageDomain.setSignHasReturn(9573);
        WaybillManageDomain.setWaybillType(3916);
        WaybillManageDomain.setCreateUser("jrKFHQz2Lf");
        WaybillManageDomain.setCreateUserId(9622);
        WaybillManageDomain.setCreateTime(new Date());
        WaybillManageDomain.setCreateSite(2514);
        WaybillManageDomain.setLaterUserId(2071);
        WaybillManageDomain.setLaterUser("z0qVC9zBQ8");
        WaybillManageDomain.setCancelReason("TwywO4O9V6");
        WaybillManageDomain.setPrint(2166);
        WaybillManageDomain.setYn(1102);
        WaybillManageDomain.setUpdateTime(new Date());
        WaybillManageDomain.setWaybillFlag(5880);
        WaybillManageDomain.setSiteId(2037);
        WaybillManageDomain.setSiteName("FYX5vMq3tG");
        WaybillManageDomain.setSiteType(3884);
        WaybillManageDomain.setStoreId(9636);
        WaybillManageDomain.setCky2(6587);
        WaybillManageDomain.setSourceCky2(8201);
        WaybillManageDomain.setWaybillOrgId(5690);
        WaybillManageDomain.setSortSiteId(3625);
        WaybillManageDomain.setReverseSiteId(9670);
        WaybillManageDomain.setOperateTime(new Date());

        DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
        DeliveryPackageD.setPackageId(5523262416506687326l);
        DeliveryPackageD.setPackageBarcode(s+"-1-1-");
        DeliveryPackageD.setWaybillCode(s);
        DeliveryPackageD.setVendorBarcode("oNOIrDoRTk");
        DeliveryPackageD.setGoodWeight(0.9778192902399022);
        DeliveryPackageD.setGoodVolume("OqRaAaAh1P");
        DeliveryPackageD.setRemark("UIHI3tDVnT");
        DeliveryPackageD.setCreateTime(new Date());
        DeliveryPackageD.setUpdateTime(new Date());
        DeliveryPackageD.setYn(3377);
        DeliveryPackageD.setServiceLevel("kFeF98S42L");
        DeliveryPackageD.setPackDate("7tECCmpACj");
        DeliveryPackageD.setPackWkNo("ubV8bbc5Gi");
        DeliveryPackageD.setAgainWeight(0.4104036068500395);

        Goods Goods = new Goods();
        Goods.setGoodId(7967733099567232650l);
        Goods.setPackBarcode("qyvOsLuMdJ");
        Goods.setWaybillCode("QU9rqbGrzv");
        Goods.setGoodName("rPoQBYRTO9");
        Goods.setSku("O2scdoojz0");
        Goods.setGoodCount(3824);
        Goods.setGoodPrice("1DvPgPgMdB");
        Goods.setGoodWeight(0.2128046587481609);
        Goods.setGoodVolume(0.85000610468906);
        Goods.setRemark("M5t03tMHGI");
        Goods.setCreateTime(new Date());
        Goods.setUpdateTime(new Date());
        Goods.setYn(7770);

        PickupTask PickupTask = new PickupTask();
        PickupTask.setPickupTaskId(8814451549516242698l);
        PickupTask.setCustomerId("xOMNvOswqs");
        PickupTask.setCustomerName("gDFx75lmN8");
        PickupTask.setCustomerAddress("PgYwhtoaAA");
        PickupTask.setTelphone("zJyyavaGta");
        PickupTask.setGweight(0.41769475634659725);
        PickupTask.setInvoiceId("RveWI4uwvM");
        PickupTask.setGvolume(0.27537104136323365);
        PickupTask.setSiteId(3824);
        PickupTask.setSiteName("imA1AbsBpQ");
        PickupTask.setNewWaybillCode("b73kNCVwOA");
        PickupTask.setPickupCode("hsssIKuSF6");
        PickupTask.setProductCode("KsFoDCnCww");
        PickupTask.setProductName("XHIfF32s2g");
        PickupTask.setAttaches("cPd6i3LyHI");
        PickupTask.setAmount(625);
        PickupTask.setFetchTime(new Date());
        PickupTask.setReceiptYn(94);
        PickupTask.setPreAllocationYn(5261);
        PickupTask.setEndCourier(7382);
        PickupTask.setEndCourierName("H7ANCKFFKb");
        PickupTask.setEndTime(new Date());
        PickupTask.setEndReason(1979);
        PickupTask.setStatus(7153);
        PickupTask.setRemark("As5Um6ltsu");
        PickupTask.setCreateTime(new Date());
        PickupTask.setUpdateTime(new Date());
        PickupTask.setIsInvoice(6947);
        PickupTask.setIsInvoiceCopy(4646);
        PickupTask.setIsPacking(3191);
        PickupTask.setIsTestReport(8104);
        PickupTask.setYn(7759);
        PickupTask.setPickupType(4300);
        PickupTask.setSurfaceCode("BnNPm6iN74");
        PickupTask.setOrgId(2131);
        PickupTask.setOrgName("NdJVFVWV9J");
        PickupTask.setCky2(8523);
        PickupTask.setStoreId(5546);
        PickupTask.setProvinceId(3751);
        PickupTask.setProvinceName("2TVcVQGYaA");
        PickupTask.setCityId(9113);
        PickupTask.setCityName("Pw1rzllRBU");
        PickupTask.setCountyId(3519);
        PickupTask.setCountyName("5YYI4SE6Q3");
        PickupTask.setTownId(2378);
        PickupTask.setTownName("VRCRCLWJWn");
        PickupTask.setOldWaybillCode("6ydDaRDDPT");
        PickupTask.setSendpay("a9aTTTcN7O");
        PickupTask.setIs3pl(7338);
        PickupTask.setServiceCode("ntM43OJ5GX");
        PickupTask.setPickupSource(516);
        PickupTask.setRecZipcode("PFiAzkzAXx");
        PickupTask.setNewPickupType(9800);
        PickupTask.setOpenFrontFlag(1527);
        PickupTask.setCourierId(1765);
        PickupTask.setAppointType(6694);
        PickupTask.setAppointBeginTime(new Date());
        PickupTask.setAppointEndTime(new Date());
        PickupTask.setAfterSaleType(7595);
        PickupTask.setMerchantId("5jg35g10jU");
        PickupTask.setMerchantTel("tOAnRQBRh4");
        PickupTask.setMerchantAddress("9xKLKL18aa");
        PickupTask.setGetInvoice(8255);
        PickupTask.setRoadCode("S32ei3zaWn");
        PickupTask.setSourceCode(192);

        ServiceBillPay ServiceBillPay = new ServiceBillPay();
        ServiceBillPay.setAmount(0.1553989014711712);
        ServiceBillPay.setBillServicetypeId("NrtsGXTiIc");
        ServiceBillPay.setCreateTime(new Date());
        ServiceBillPay.setCurrentStatus("GLI1LXIPjJ");
        ServiceBillPay.setDeliveryId("0qItdesu4G");
        ServiceBillPay.setId(-8470832482410114458l);
        ServiceBillPay.setMemo("1CdwWGF6pG");
        ServiceBillPay.setPayTime(new Date());
        ServiceBillPay.setVendorId(9253);
        ServiceBillPay.setYn(601);

        List<DeliveryPackageD> deliveryPackageDs = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
        deliveryPackageDs.add(DeliveryPackageD);

        List<Goods> goodsLis = new ArrayList<Goods>();
        goodsLis.add(Goods);

        dto.setGoodsList(goodsLis);
        dto.setPackageList(deliveryPackageDs);
        dto.setPickupTask(PickupTask);
        dto.setWaybill(bean);
        dto.setServiceBillPay(ServiceBillPay);
        dto.setWaybillState(WaybillManageDomain);
        return new BaseEntity<BigWaybillDto>(dto);
    }

    
    public BaseEntity<Waybill> getWaybillByPackCode(String s) {
        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode("Rl8HlUVUW9");
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");
        return new BaseEntity<Waybill>( bean);
    }

    
    public BaseEntity<Boolean> hasWaybill(String s) {
        return new BaseEntity<Boolean>(Boolean.FALSE);
    }

    
    public BaseEntity<Integer> getWaybillStateByWaybillCode(String s) {
        return new BaseEntity<Integer>(1111);
    }

    
    public BaseEntity<List<String>> getLuxuryWaybillCodeList(List<String> list) {
        List<String> strings = new ArrayList<String>();
        strings.add(generateStr());
        strings.add(generateStr());
        strings.add(generateStr());
        return new BaseEntity<List<String>>(strings);
    }

    
    public BaseEntity<List<WaybillAndWaybillState>> queryUnScanWaybill(int i, int i1) {

        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode("Rl8HlUVUW9");
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");

        WaybillManageDomain WaybillManageDomain = new WaybillManageDomain();
        WaybillManageDomain.setmId(-522477037880728779l);
        WaybillManageDomain.setWaybillCode("dyMNMwzywv");
        WaybillManageDomain.setActualCollection("xmFx9KM5O3");
        WaybillManageDomain.setCollectionType(3212);
        WaybillManageDomain.setWaybillState(3743);
        WaybillManageDomain.setSignState(2788);
        WaybillManageDomain.setSignHasReturn(9573);
        WaybillManageDomain.setWaybillType(3916);
        WaybillManageDomain.setCreateUser("jrKFHQz2Lf");
        WaybillManageDomain.setCreateUserId(9622);
        WaybillManageDomain.setCreateTime(new Date());
        WaybillManageDomain.setCreateSite(2514);
        WaybillManageDomain.setLaterUserId(2071);
        WaybillManageDomain.setLaterUser("z0qVC9zBQ8");
        WaybillManageDomain.setCancelReason("TwywO4O9V6");
        WaybillManageDomain.setPrint(2166);
        WaybillManageDomain.setYn(1102);
        WaybillManageDomain.setUpdateTime(new Date());
        WaybillManageDomain.setWaybillFlag(5880);
        WaybillManageDomain.setSiteId(2037);
        WaybillManageDomain.setSiteName("FYX5vMq3tG");
        WaybillManageDomain.setSiteType(3884);
        WaybillManageDomain.setStoreId(9636);
        WaybillManageDomain.setCky2(6587);
        WaybillManageDomain.setSourceCky2(8201);
        WaybillManageDomain.setWaybillOrgId(5690);
        WaybillManageDomain.setSortSiteId(3625);
        WaybillManageDomain.setReverseSiteId(9670);
        WaybillManageDomain.setOperateTime(new Date());

        DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
        DeliveryPackageD.setPackageId(5523262416506687326l);
        DeliveryPackageD.setPackageBarcode("Rzz0xXxXhN");
        DeliveryPackageD.setWaybillCode("mtRiYtVHdg");
        DeliveryPackageD.setVendorBarcode("oNOIrDoRTk");
        DeliveryPackageD.setGoodWeight(0.9778192902399022);
        DeliveryPackageD.setGoodVolume("OqRaAaAh1P");
        DeliveryPackageD.setRemark("UIHI3tDVnT");
        DeliveryPackageD.setCreateTime(new Date());
        DeliveryPackageD.setUpdateTime(new Date());
        DeliveryPackageD.setYn(3377);
        DeliveryPackageD.setServiceLevel("kFeF98S42L");
        DeliveryPackageD.setPackDate("7tECCmpACj");
        DeliveryPackageD.setPackWkNo("ubV8bbc5Gi");
        DeliveryPackageD.setAgainWeight(0.4104036068500395);
        WaybillAndWaybillState waybillAndWaybillState = new WaybillAndWaybillState();
        waybillAndWaybillState.setQuerySize(20);
        waybillAndWaybillState.setWaybill(bean);
        waybillAndWaybillState.setWaybillState(WaybillManageDomain);
        List<DeliveryPackageD> deliveryPackageDs  = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
        deliveryPackageDs.add(DeliveryPackageD);
        List<WaybillAndWaybillState> waybillAndWaybillStates = new ArrayList<WaybillAndWaybillState>();
        waybillAndWaybillStates.add(waybillAndWaybillState);
        return new BaseEntity<List<WaybillAndWaybillState>>(waybillAndWaybillStates);

    }

    
    public BaseEntity<Map<String, List<DeliveryPackageD>>> batchGetPackListByCodeList(List<String> list) {
        return null;
    }

    
    public BaseEntity<List<String>> batchCheckWaybillRevoke(List<String> list) {
        List<String> strings = new ArrayList<String>();
        strings.add(generateStr());
        strings.add(generateStr());
        strings.add(generateStr());
        return new BaseEntity<List<String>>(strings);
    }

    
    public BaseEntity<Map<String, Boolean>> batchCheckWaybillReachSucc(List<String> list) {
        return null;
    }

    
    public BaseEntity<List<String>> batchHasWaybill(List<String> list) {
       List<String> strings = new ArrayList<String>();
        strings.add(generateStr());
        strings.add(generateStr());
        strings.add(generateStr());
        return new BaseEntity<List<String>>(strings);
    }

    
    public BaseEntity<WaybillAndWaybillState> getWaybillAndStateByWaybillCode(String s) {
        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode("Rl8HlUVUW9");
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");

        WaybillManageDomain WaybillManageDomain = new WaybillManageDomain();
        WaybillManageDomain.setmId(-522477037880728779l);
        WaybillManageDomain.setWaybillCode("dyMNMwzywv");
        WaybillManageDomain.setActualCollection("xmFx9KM5O3");
        WaybillManageDomain.setCollectionType(3212);
        WaybillManageDomain.setWaybillState(3743);
        WaybillManageDomain.setSignState(2788);
        WaybillManageDomain.setSignHasReturn(9573);
        WaybillManageDomain.setWaybillType(3916);
        WaybillManageDomain.setCreateUser("jrKFHQz2Lf");
        WaybillManageDomain.setCreateUserId(9622);
        WaybillManageDomain.setCreateTime(new Date());
        WaybillManageDomain.setCreateSite(2514);
        WaybillManageDomain.setLaterUserId(2071);
        WaybillManageDomain.setLaterUser("z0qVC9zBQ8");
        WaybillManageDomain.setCancelReason("TwywO4O9V6");
        WaybillManageDomain.setPrint(2166);
        WaybillManageDomain.setYn(1102);
        WaybillManageDomain.setUpdateTime(new Date());
        WaybillManageDomain.setWaybillFlag(5880);
        WaybillManageDomain.setSiteId(2037);
        WaybillManageDomain.setSiteName("FYX5vMq3tG");
        WaybillManageDomain.setSiteType(3884);
        WaybillManageDomain.setStoreId(9636);
        WaybillManageDomain.setCky2(6587);
        WaybillManageDomain.setSourceCky2(8201);
        WaybillManageDomain.setWaybillOrgId(5690);
        WaybillManageDomain.setSortSiteId(3625);
        WaybillManageDomain.setReverseSiteId(9670);
        WaybillManageDomain.setOperateTime(new Date());

        DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
        DeliveryPackageD.setPackageId(5523262416506687326l);
        DeliveryPackageD.setPackageBarcode("Rzz0xXxXhN");
        DeliveryPackageD.setWaybillCode("mtRiYtVHdg");
        DeliveryPackageD.setVendorBarcode("oNOIrDoRTk");
        DeliveryPackageD.setGoodWeight(0.9778192902399022);
        DeliveryPackageD.setGoodVolume("OqRaAaAh1P");
        DeliveryPackageD.setRemark("UIHI3tDVnT");
        DeliveryPackageD.setCreateTime(new Date());
        DeliveryPackageD.setUpdateTime(new Date());
        DeliveryPackageD.setYn(3377);
        DeliveryPackageD.setServiceLevel("kFeF98S42L");
        DeliveryPackageD.setPackDate("7tECCmpACj");
        DeliveryPackageD.setPackWkNo("ubV8bbc5Gi");
        DeliveryPackageD.setAgainWeight(0.4104036068500395);
        WaybillAndWaybillState waybillAndWaybillState = new WaybillAndWaybillState();
        waybillAndWaybillState.setQuerySize(20);
        waybillAndWaybillState.setWaybill(bean);
        waybillAndWaybillState.setWaybillState(WaybillManageDomain);
        List<DeliveryPackageD> deliveryPackageDs  = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
        deliveryPackageDs.add(DeliveryPackageD);
        return new BaseEntity<WaybillAndWaybillState>(waybillAndWaybillState);
    }

    
    public BaseEntity<List<DeliveryPackageD>> queryPackageListForParcodes(List<String> list) {

        DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
        DeliveryPackageD.setPackageId(-4499818809640005798l);
        DeliveryPackageD.setPackageBarcode("S1TDWnfhFJ");
        DeliveryPackageD.setWaybillCode("AfFHKML0fh");
        DeliveryPackageD.setVendorBarcode("DnpAnKmYCV");
        DeliveryPackageD.setGoodWeight(0.9037447023169889);
        DeliveryPackageD.setGoodVolume("ALvvoppqfF");
        DeliveryPackageD.setRemark("KMeXduuwWB");
        DeliveryPackageD.setCreateTime(new Date());
        DeliveryPackageD.setUpdateTime(new Date());
        DeliveryPackageD.setYn(4550);
        DeliveryPackageD.setServiceLevel("ejw8gq2254");
        DeliveryPackageD.setPackDate("oYXVvVVVww");
        DeliveryPackageD.setPackWkNo("cuUpfKmHcC");
        DeliveryPackageD.setAgainWeight(0.2140504959137871);
        List<DeliveryPackageD> deliveryPackageDs = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
        deliveryPackageDs.add(DeliveryPackageD);

        return new BaseEntity<List<com.jd.etms.waybill.domain.DeliveryPackageD>>(deliveryPackageDs);
    }

    
    public BaseEntity<List<Waybill>> queryWaybillForWaybillCodes(List<String> list) {
        Waybill Waybill = new Waybill();
        Waybill.setCid(8689672243159801151l);
        Waybill.setEid(-4414393790763727247l);
        Waybill.setWaybillCode("cTiNxhxabl");
        Waybill.setVendorId("FhY2sWVGFp");
        Waybill.setArriveAreaId(164);
        Waybill.setArriveArea("2sG0reTEy8");
        Waybill.setProductType(2660);
        Waybill.setSpecialRequire("hSTFYprDnn");
        Waybill.setConsigner("MoDBRTk5wi");
        Waybill.setConsignerId(8508);
        Waybill.setConsignerMobile("4hFHrdI9wp");
        Waybill.setConsignerTel("Ae4u1rJUEt");
        Waybill.setConsignerAddress("zjjldB2rRT");
        Waybill.setConsignerZipCode("a6wPgHJaWn");
        Waybill.setReceiverName("mMFqWndEFs");
        Waybill.setReceiverMobile("ogfgwxa0qW");
        Waybill.setReceiverTel("82Q2sigfh9");
        Waybill.setReceiverAddress("IWcccuUpRh");
        Waybill.setReceiverZipCode("9sbF7tdXde");
        Waybill.setGoodNumber(1793);
        Waybill.setVolumeFormula("KC8HABBlGs");
        Waybill.setGoodVolume(0.1906742807255053);
        Waybill.setGoodWeight(0.5484799875624711);
        Waybill.setGoodName("xwmmmn99TT");
        Waybill.setChargedWeight(0.30613748994005097);
        Waybill.setDeclaredValue(0.9225181089914284);
        Waybill.setPackageId(-4639867473555687867l);
        Waybill.setRecDateType(7270);
        Waybill.setReturnSignBill(1638);
        Waybill.setRequireTime(new Date());
        Waybill.setTelCon(1560);
        Waybill.setImportantHint("dIEhij3e1s");
        Waybill.setFreight("zwzbbrKbdT");
        Waybill.setServiceCharge("4tHuHGJYWW");
        Waybill.setPackingCharges("j00j1rt3Ak");
        Waybill.setCodMoney("iwLHQFQSzD");
        Waybill.setPrice("2MN7xPMd1K");
        Waybill.setRecMoney("6wh4YFHHVU");
        Waybill.setReceivedMoney("zmMxXXcety");
        Waybill.setRelWaybillCode("hutuuuG4uN");
        Waybill.setUpdateTime(new Date());
        Waybill.setYn(6658);
        Waybill.setReceiverId("AUeRBdBDvv");
        Waybill.setWaybillType(2443);
        Waybill.setSendPay("H3C45qfuqG");
        Waybill.setSiteId(5018);
        Waybill.setSiteName("ByYwWYV21Y");
        Waybill.setPayment(3879);
        Waybill.setMemberId("3ttJupA1yY");
        Waybill.setProvinceId(739);
        Waybill.setProvinceName("dEgiAktpcp");
        Waybill.setCityId(3024);
        Waybill.setCityName("hu1ddtedAk");
        Waybill.setCountryId(7744);
        Waybill.setCountryName("j0jUgKbqEX");
        Waybill.setDiscount("NHwPrWUWWQ");
        Waybill.setCashier("MJtfo9iki4");
        Waybill.setCreateTime(new Date());
        Waybill.setDistanceType(1092);
        Waybill.setBalance(0.6580759185734455);
        Waybill.setTownId(7330);
        Waybill.setTownName("Vuubdo0421");
        Waybill.setPriceProtectFlag(7802);
        Waybill.setPriceProtectMoney(0.7385909303773289);
        Waybill.setReserveState(5364);
        Waybill.setReserveDate(new Date());
        Waybill.setOldSiteId(6542);
        Waybill.setBusiId(4105);
        Waybill.setBusiName("pH21LdYpWw");
        Waybill.setSendCityId(1232);
        Waybill.setAgainWeight(0.4649160119928638);
        Waybill.setTransferStationId(8874);
        Waybill.setTransferStationName("Xzgh1eXkL5");
        Waybill.setSlideCode("Kq0rsfW7R8");
        Waybill.setOrderSubmitTime(new Date());
        Waybill.setDistributeStoreId(2031);
        Waybill.setDistributeStoreName("6KGII9GqBT");
        Waybill.setNewRecAddr("YQDyYhgcsh");
        Waybill.setTrolleyCode("whxKbKr1rD");
        Waybill.setFirstTime(new Date());
        Waybill.setTaxValue(3907);
        Waybill.setBuyerName("5xyy1N6I2C");
        Waybill.setBuyerMobile("3IDPA2Nexq");
        Waybill.setSelfOther(7937);
        Waybill.setParentOrderId("l2s23MYqKJ");
        Waybill.setBusiOrderCode("keEXoYACLJ");
        Waybill.setSourceCode("TzUwU6HeVv");
        Waybill.setWaybillSign("E13JyRKJHJ");
        Waybill.setShopCode("uoGIFFWGqq");
        Waybill.setDistributeType(4589);
        Waybill.setReceiveTransFee(0.32571387339477054);
        Waybill.setPreOperator("oIJJJLlQhP");
        Waybill.setSortType(1772);
        Waybill.setRoadCode("3XWUGNJIqs");
        List<Waybill> waybills = new ArrayList<com.jd.etms.waybill.domain.Waybill>();
        waybills.add(Waybill);
        return new BaseEntity<List<com.jd.etms.waybill.domain.Waybill>>(waybills);
    }

    
    public BaseEntity<Boolean> hasCashier(String s) {
        return new BaseEntity<Boolean>(Boolean.TRUE);
    }

    
    public BaseEntity<List<Integer>> countOffForSiteAndState(Integer integer, List<Integer> list) {
        List<Integer> integers = new ArrayList<Integer>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        return new BaseEntity<List<Integer>>(integers);
    }

    
    public BaseEntity<List<Waybill>> getWaybillListBySiteAndState(Integer integer, Integer integer1) {
        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode("Rl8HlUVUW9");
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");
        List<Waybill> waybills = new ArrayList<Waybill>();
        waybills.add(bean);
        return new BaseEntity<List<Waybill>>(waybills);
    }

    
    public BaseEntity<List<DistanceUpdateDto>> getWaybillListByWaybillCodeList(List<String> list) {
        DistanceUpdateDto DistanceUpdateDto = new DistanceUpdateDto();
        DistanceUpdateDto.setWaybillCode("PlfNONoUWP");
        DistanceUpdateDto.setDistanceType(9551);
        DistanceUpdateDto.setLaterUserId("IRiLnpcFpa");
        DistanceUpdateDto.setReceiverAddress("GG7C3zgiin");
        List<DistanceUpdateDto> distanceUpdateDtos = new ArrayList<com.jd.etms.waybill.dto.DistanceUpdateDto>();
        distanceUpdateDtos.add(DistanceUpdateDto);
        return new BaseEntity<List<com.jd.etms.waybill.dto.DistanceUpdateDto>>(distanceUpdateDtos);
    }

    
    public BaseEntity<WaybillAndWaybillState> getWaybillMCByPack(String s) {
        Waybill bean = new Waybill();
        bean.setCid(7750123492322811220l);
        bean.setEid(4433785667048011952l);
        bean.setWaybillCode("Rl8HlUVUW9");
        bean.setVendorId("Oq7qonETVT");
        bean.setArriveAreaId(9976);
        bean.setArriveArea("AjicBzAOjM");
        bean.setProductType(7094);
        bean.setSpecialRequire("Q7xLvI6xkK");
        bean.setConsigner("wW01jkJdWJ");
        bean.setConsignerId(951);
        bean.setConsignerMobile("Uyz0nK99Ss");
        bean.setConsignerTel("I7POEMHGtl");
        bean.setConsignerAddress("2b7bcn9ugG");
        bean.setConsignerZipCode("i0JJLWYAOf");
        bean.setReceiverName("4f1uwUxehH");
        bean.setReceiverMobile("nOPR7y5O12");
        bean.setReceiverTel("lYXfYVfYXY");
        bean.setReceiverAddress("FaRDyAqQaA");
        bean.setReceiverZipCode("wOKsIHJaVr");
        bean.setGoodNumber(5011);
        bean.setVolumeFormula("vTSrnoPuUV");
        bean.setGoodVolume(0.03085393793224933);
        bean.setGoodWeight(0.3738244087106709);
        bean.setGoodName("SAlBBAyO6x");
        bean.setChargedWeight(0.7335616459070428);
        bean.setDeclaredValue(0.07639654811895491);
        bean.setPackageId(8659840469232612042l);
        bean.setRecDateType(3181);
        bean.setReturnSignBill(8087);
        bean.setRequireTime(new Date());
        bean.setTelCon(3523);
        bean.setImportantHint("MnjKj8rRoI");
        bean.setFreight("t6RnVyJJtG");
        bean.setServiceCharge("2UeEd8aufe");
        bean.setPackingCharges("LsupFXHSHR");
        bean.setCodMoney("o0LtdiutvV");
        bean.setPrice("QTTb4rRn46");
        bean.setRecMoney("tedtLUDWnR");
        bean.setReceivedMoney("e01gxxhhKb");
        bean.setRelWaybillCode("4sEzQQ3rRj");
        bean.setUpdateTime(new Date());
        bean.setYn(986);
        bean.setReceiverId("OkPgivxxJY");
        bean.setWaybillType(691);
        bean.setSendPay("tGdscFqpMd");
        bean.setSiteId(9266);
        bean.setSiteName("UjjMdu5sop");
        bean.setPayment(5330);
        bean.setMemberId("M5ooxzuMt0");
        bean.setProvinceId(37);
        bean.setProvinceName("w5RiiyAAMw");
        bean.setCityId(7614);
        bean.setCityName("pnN3Nl6i3j");
        bean.setCountryId(2107);
        bean.setCountryName("zYcdcdrSIH");
        bean.setDiscount("QPkK8nz0tt");
        bean.setCashier("W744gg5sLc");
        bean.setCreateTime(new Date());
        bean.setDistanceType(8342);
        bean.setBalance(0.053304312014438304);
        bean.setTownId(2149);
        bean.setTownName("91dwbuBB2l");
        bean.setPriceProtectFlag(4944);
        bean.setPriceProtectMoney(0.8572860902772002);
        bean.setReserveState(5655);
        bean.setReserveDate(new Date());
        bean.setOldSiteId(6750);
        bean.setBusiId(6963);
        bean.setBusiName("vVFYpVErzC");
        bean.setSendCityId(2937);
        bean.setAgainWeight(0.05754636937413948);
        bean.setTransferStationId(4962);
        bean.setTransferStationName("yqn5B2N60o");
        bean.setSlideCode("WrXoR0qVCC");
        bean.setOrderSubmitTime(new Date());
        bean.setDistributeStoreId(1743);
        bean.setDistributeStoreName("G6wyYv687j");
        bean.setNewRecAddr("mPgP76wySD");
        bean.setTrolleyCode("vcSFqpSFHt");
        bean.setFirstTime(new Date());
        bean.setTaxValue(8273);
        bean.setBuyerName("9yiuurBC45");
        bean.setBuyerMobile("qGsln6B6p9");
        bean.setSelfOther(32);
        bean.setParentOrderId("UMMBecCVcc");
        bean.setBusiOrderCode("v9Wr1AMLJ3");
        bean.setSourceCode("1jijyzKLmF");
        bean.setWaybillSign("aaaSj9skmm");
        bean.setShopCode("uooogGGaW7");
        bean.setDistributeType(1421);
        bean.setReceiveTransFee(0.818915948337743);
        bean.setPreOperator("vRlq225E55");
        bean.setSortType(1790);
        bean.setRoadCode("uYAconp0wW");

        WaybillManageDomain WaybillManageDomain = new WaybillManageDomain();
        WaybillManageDomain.setmId(-522477037880728779l);
        WaybillManageDomain.setWaybillCode("dyMNMwzywv");
        WaybillManageDomain.setActualCollection("xmFx9KM5O3");
        WaybillManageDomain.setCollectionType(3212);
        WaybillManageDomain.setWaybillState(3743);
        WaybillManageDomain.setSignState(2788);
        WaybillManageDomain.setSignHasReturn(9573);
        WaybillManageDomain.setWaybillType(3916);
        WaybillManageDomain.setCreateUser("jrKFHQz2Lf");
        WaybillManageDomain.setCreateUserId(9622);
        WaybillManageDomain.setCreateTime(new Date());
        WaybillManageDomain.setCreateSite(2514);
        WaybillManageDomain.setLaterUserId(2071);
        WaybillManageDomain.setLaterUser("z0qVC9zBQ8");
        WaybillManageDomain.setCancelReason("TwywO4O9V6");
        WaybillManageDomain.setPrint(2166);
        WaybillManageDomain.setYn(1102);
        WaybillManageDomain.setUpdateTime(new Date());
        WaybillManageDomain.setWaybillFlag(5880);
        WaybillManageDomain.setSiteId(2037);
        WaybillManageDomain.setSiteName("FYX5vMq3tG");
        WaybillManageDomain.setSiteType(3884);
        WaybillManageDomain.setStoreId(9636);
        WaybillManageDomain.setCky2(6587);
        WaybillManageDomain.setSourceCky2(8201);
        WaybillManageDomain.setWaybillOrgId(5690);
        WaybillManageDomain.setSortSiteId(3625);
        WaybillManageDomain.setReverseSiteId(9670);
        WaybillManageDomain.setOperateTime(new Date());

        DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
        DeliveryPackageD.setPackageId(5523262416506687326l);
        DeliveryPackageD.setPackageBarcode("Rzz0xXxXhN");
        DeliveryPackageD.setWaybillCode("mtRiYtVHdg");
        DeliveryPackageD.setVendorBarcode("oNOIrDoRTk");
        DeliveryPackageD.setGoodWeight(0.9778192902399022);
        DeliveryPackageD.setGoodVolume("OqRaAaAh1P");
        DeliveryPackageD.setRemark("UIHI3tDVnT");
        DeliveryPackageD.setCreateTime(new Date());
        DeliveryPackageD.setUpdateTime(new Date());
        DeliveryPackageD.setYn(3377);
        DeliveryPackageD.setServiceLevel("kFeF98S42L");
        DeliveryPackageD.setPackDate("7tECCmpACj");
        DeliveryPackageD.setPackWkNo("ubV8bbc5Gi");
        DeliveryPackageD.setAgainWeight(0.4104036068500395);

        List<DeliveryPackageD> deliveryPackageDs = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
        deliveryPackageDs.add(DeliveryPackageD);
        WaybillAndWaybillState waybillAndWaybillState = new WaybillAndWaybillState();
        waybillAndWaybillState.setPackageList(deliveryPackageDs);
        waybillAndWaybillState.setQuerySize(20);
        waybillAndWaybillState.setWaybill(bean);
        waybillAndWaybillState.setWaybillState(WaybillManageDomain);
        return new BaseEntity<WaybillAndWaybillState>(waybillAndWaybillState);
    }

    
    public BaseEntity<List<String>> isWaybillsRevoke(List<String> list, List<String> list1) {
        List<String> strings = new ArrayList<String>();
        strings.add(generateStr());
        strings.add(generateStr());
        strings.add(generateStr());
        return new BaseEntity<List<String>>(strings);
    }

    
    public BaseEntity<List<WaybillSiteInfoDto>> getWaybillSiteInfoByWaybillCodeList(List<String> list) {
        WaybillSiteInfoDto WaybillSiteInfoDto = new WaybillSiteInfoDto();
        WaybillSiteInfoDto.setWaybillCode("XeECVyVm3t");
        WaybillSiteInfoDto.setSiteId(9014);
        WaybillSiteInfoDto.setSiteName("eLtTVQ9k9d");
        WaybillSiteInfoDto.setSiteType(4497);

        List<WaybillSiteInfoDto> dtos = new ArrayList<com.jd.etms.waybill.dto.WaybillSiteInfoDto>();
        dtos.add(WaybillSiteInfoDto);
        return new BaseEntity<List<com.jd.etms.waybill.dto.WaybillSiteInfoDto>>(dtos);
    }

    
    public BaseEntity<List<SkuSn>> getSkuSnListByOrderId(String s) {
        SkuSn SkuSn = new SkuSn();
        SkuSn.setSkuSnId(-5852748748104981908l);
        SkuSn.setOrderId("st9865j93M");
        SkuSn.setSkuCode("snp8E5Cdge");
        SkuSn.setSnCode("DbdW99aRmM");
        SkuSn.setSnCount(9891);
        SkuSn.setSendTime(new Date());
        SkuSn.setCreateTime(new Date());
        SkuSn.setUpdateTime(new Date());
        SkuSn.setRemark("TJlQ5OQNL4");
        SkuSn.setYn(2257);

        List<SkuSn> skuSns = new ArrayList<SkuSn>();
        skuSns.add(SkuSn);
        return new BaseEntity<List<com.jd.etms.waybill.domain.SkuSn>>(skuSns);

    }

    
    public BaseEntity<List<PackOpeFlowDto>> getPackOpeByWaybillCode(String s) {
        List<PackOpeFlowDto> dtos = new ArrayList<PackOpeFlowDto>();
        PackOpeFlowDto PackOpeFlowDto = new PackOpeFlowDto();
        PackOpeFlowDto.setWaybillCode("nWTSNXpWWS");
        PackOpeFlowDto.setPackageCode("D4WgGXj33l");
        PackOpeFlowDto.setOpeType(5505);
        PackOpeFlowDto.setOpeDesc("MlezsLcLpe");
        PackOpeFlowDto.setpWeight(0.48712998919599737);
        PackOpeFlowDto.setpLength(0.9514903239815686);
        PackOpeFlowDto.setpWidth(0.1321473338940128);
        PackOpeFlowDto.setpHigh(0.2726754829316552);
        PackOpeFlowDto.setWeighUserId("UzbqGGGqLx");
        PackOpeFlowDto.setWeighUserName("tnn7aQaR5q");
        PackOpeFlowDto.setWeighTime(new Date());
        PackOpeFlowDto.setMeasureUserId("9GPRi4z8J5");
        PackOpeFlowDto.setMeasureUserName("77QynnkmPE");
        PackOpeFlowDto.setMeasureTime(new Date());
        dtos.add(PackOpeFlowDto);
        return new BaseEntity<List<com.jd.etms.waybill.dto.PackOpeFlowDto>>(dtos);
    }

    
    public BaseEntity<List<OrderRelationDto>> getOrderRelationList(String s) {
        OrderRelationDto orderRelationDto = new OrderRelationDto();
        orderRelationDto.setMainOrderId("FxKfueHssV");
        orderRelationDto.setGiftOrderId("gGATA7STTU");
        List<OrderRelationDto> dtos = new ArrayList<OrderRelationDto>();
        return new BaseEntity<List<OrderRelationDto>>(dtos);
    }

    
    public BaseEntity<List<OrderRelationDto>> getOfcOrderRelationList(String s) {
        OrderRelationDto orderRelationDto = new OrderRelationDto();
        orderRelationDto.setMainOrderId("FxKfueHssV");
        orderRelationDto.setGiftOrderId("gGATA7STTU");
        List<OrderRelationDto> dtos = new ArrayList<OrderRelationDto>();
        return new BaseEntity<List<OrderRelationDto>>(dtos);
    }

    
    public BaseEntity<Page<WaybillDto>> getWaybillByParam(WaybillParamDto waybillParamDto, Page<WaybillDto> page) {
        WaybillDto WaybillDto = new WaybillDto();
        WaybillDto.setWaybillId(-6452640736596045535l);
        WaybillDto.setWaybillCode("uhMOO60rYy");
        WaybillDto.setVendorId("MyjPgLJLKW");
        WaybillDto.setArriveAreaId(9669);
        WaybillDto.setArriveArea("WvRiRiBLwV");
        WaybillDto.setProductType(7391);
        WaybillDto.setSpecialRequire("JJachjuvKM");
        WaybillDto.setConsigner("OLcYFYpTSI");
        WaybillDto.setConsignerId(8460);
        WaybillDto.setConsignerMobile("3YqYTfOfo1");
        WaybillDto.setConsignerTel("pLNWWWGHUG");
        WaybillDto.setConsignerAddress("UvVfhtvVvc");
        WaybillDto.setConsignerZipCode("8bO8TbH8I9");
        WaybillDto.setReceiverName("g9LJJLFpnW");
        WaybillDto.setReceiverMobile("ybVJYX035v");
        WaybillDto.setReceiverTel("wi46CkSkPO");
        WaybillDto.setReceiverAddress("MG7888aqQ6");
        WaybillDto.setReceiverZipCode("KYpYiIgGrg");
        WaybillDto.setGoodNumber(3451);
        WaybillDto.setVolumeFormula("jIGow8rbal");
        WaybillDto.setGoodVolume(0.6696939198074628);
        WaybillDto.setGoodWeight(0.238427967834681);
        WaybillDto.setGoodName("GL0MJY0fh3");
        WaybillDto.setChargedWeight(0.7803777667269093);
        WaybillDto.setDeclaredValue(0.32617187136540626);
        WaybillDto.setPackageId(7347255902226484628l);
        WaybillDto.setRecDateType(5819);
        WaybillDto.setReturnSignBill(1821);
        WaybillDto.setRequireTime(new Date());
        WaybillDto.setTelCon(4563);
        WaybillDto.setImportantHint("lEfRS9zNBl");
        WaybillDto.setFreight("O3mj3mjlLs");
        WaybillDto.setServiceCharge("Xy3tRKYYbz");
        WaybillDto.setPackingCharges("WPC5yYj333");
        WaybillDto.setCodMoney("kWf9XHoO0i");
        WaybillDto.setPrice("tQLlLiY1Yh");
        WaybillDto.setRecMoney("jDX9XdprmK");
        WaybillDto.setReceivedMoney("NrbjHJJDWn");
        WaybillDto.setRelWaybillCode("iyyYi69K33");
        WaybillDto.setUpdateTime(new Date());
        WaybillDto.setYn(7926);
        WaybillDto.setReceiverId("zGRSvTDP9z");
        WaybillDto.setWaybillType(9579);
        WaybillDto.setSendPay("voO8yfb8bM");
        WaybillDto.setSiteId(571);
        WaybillDto.setSiteName("utMhjJK3WJ");
        WaybillDto.setSiteType(341);
        WaybillDto.setFlag(7950);
        WaybillDto.setPayment(4229);
        WaybillDto.setMemberId("gGBnk5prRH");
        WaybillDto.setCky2(5444);
        WaybillDto.setProvinceId(3553);
        WaybillDto.setProvinceName("G45OL122UM");
        WaybillDto.setCityId(5714);
        WaybillDto.setCityName("hxxtu1VXFa");
        WaybillDto.setCountryId(4597);
        WaybillDto.setCountryName("XmAEpP8zSE");
        WaybillDto.setDiscount("PA5Q45w6wP");
        WaybillDto.setCreateTime(new Date());
        WaybillDto.setCashier("KjC3RCYJId");
        WaybillDto.setActualCollection("Wkjkjkvh1x");
        WaybillDto.setCollectionType(2730);
        WaybillDto.setWaybillState(3744);
        WaybillDto.setSignState(5777);
        WaybillDto.setSignHasReturn(9592);
        WaybillDto.setCreateUser("KCNPCzmG7v");
        WaybillDto.setCreateUserId(5638);
        WaybillDto.setCreateSite(3994);
        WaybillDto.setLaterUserId(7266);
        WaybillDto.setLaterUser("m3tFaAzRNx");
        WaybillDto.setCancelReason("e6wMLFNB2A");
        WaybillDto.setPrint(8017);
        WaybillDto.setWaybillFlag(152);
        WaybillDto.setWaybillSign("qAmQa570iA");
        WaybillDto.setOperateTime(new Date());
        WaybillDto.setStoreId(1594);
        WaybillDto.setDistributeType(5114);
        WaybillDto.setAgainWeight(0.30030858639803093);
        WaybillDto.setQuerySize(3842);

        Page<WaybillDto> page1 = new Page<WaybillDto>();

        List<WaybillDto> dtos = new ArrayList<com.jd.etms.waybill.dto.WaybillDto>();
        dtos.add(WaybillDto);

        page1.setCurPage(1);
        page1.setNextPage(3);
        page1.setPageSize(20);
        page1.setPrePage(1);
        page1.setResult(dtos);
        page1.setTotalRow(60);
        page1.setTotalPage(3);
        return new BaseEntity<Page<com.jd.etms.waybill.dto.WaybillDto>>(page1);
    }
    
    public List<DeliveryPackageD> getPackageBarcodeByWaybillCode(String s) {
    	 DeliveryPackageD DeliveryPackageD = new DeliveryPackageD();
         DeliveryPackageD.setPackageId(5523262416506687326l);
         DeliveryPackageD.setPackageBarcode(s+"-1-1-");
         DeliveryPackageD.setWaybillCode(s);
         DeliveryPackageD.setVendorBarcode("oNOIrDoRTk");
         DeliveryPackageD.setGoodWeight(0.9778192902399022);
         DeliveryPackageD.setGoodVolume("OqRaAaAh1P");
         DeliveryPackageD.setRemark("UIHI3tDVnT");
         DeliveryPackageD.setCreateTime(new Date());
         DeliveryPackageD.setUpdateTime(new Date());
         DeliveryPackageD.setYn(3377);
         DeliveryPackageD.setServiceLevel("kFeF98S42L");
         DeliveryPackageD.setPackDate("7tECCmpACj");
         DeliveryPackageD.setPackWkNo("ubV8bbc5Gi");
         DeliveryPackageD.setAgainWeight(0.4104036068500395);

         List<DeliveryPackageD> deliveryPackageDs = new ArrayList<com.jd.etms.waybill.domain.DeliveryPackageD>();
         deliveryPackageDs.add(DeliveryPackageD);
         return deliveryPackageDs;
    }

    public static void main(String[] args) {
        Class clazz = new DeliveryPackageD().getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().getName().endsWith("String")) {
                System.out.println(clazz.getSimpleName() + ".set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + "(\"" + generateStr() + "\");");
            } else if (field.getType().getName().endsWith("int") || field.getType().getName().endsWith("Integer")) {
                System.out.println(clazz.getSimpleName() + ".set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + "(" + new Random().nextInt(9999) + ");");
            } else if (field.getType().getName().endsWith("Double") || field.getType().getName().endsWith("double")) {
                System.out.println(clazz.getSimpleName() + ".set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + "(" + new Random().nextDouble() + ");");
            } else if (field.getType().getName().endsWith("Float") || field.getType().getName().endsWith("float")) {
                System.out.println(clazz.getSimpleName() + ".set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + "(" + new Random().nextFloat() + ");");
            } else if (field.getType().getName().endsWith("Date")) {
                System.out.println(clazz.getSimpleName() + ".set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + "(new Date());");
            } else if (field.getType().getName().endsWith("Long") || field.getType().getName().endsWith("long")) {
                System.out.println(clazz.getSimpleName() + ".set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + "(" + new Random().nextLong() + "l);");
            }
        }
    }

    private static String generateStr() {
        String seed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(seed.charAt(new Random().nextInt(seed.length() - 1)));
        }
        return sb.toString();
    }
}