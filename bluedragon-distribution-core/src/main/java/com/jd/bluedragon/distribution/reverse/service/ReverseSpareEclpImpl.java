package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.eclp.isv.domain.GoodsInfo;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrder;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrderTypeEnum;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundSourceEnum;
import com.jd.eclp.spare.ext.api.outbound.GoodsInfoItem;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.SparsModel;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ECLP退备件库 业务逻辑
 */
@Service("reverseSpareEclp")
public class ReverseSpareEclpImpl implements ReverseSpareEclp {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String THIRD_CATEGORY_NO = "9694"; //商品三级编码

    private String DEFAULT_GOODS_NAME = "其他"; //默认商品名称

    private Integer DEFAULT_GOOD_NUM = 1;

    private Integer DEFAULT_SAFE_DAYS = 0;

    @Value("${eclp.c2c.deft.no:EBU0000000000571}")
    private String C2C_DEFT_NO ;//待定



    @Autowired
    @Qualifier("baseMajorManager")
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("waybillCommonService")
    private WaybillCommonService waybillCommonService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("eclpItemManager")
    private EclpItemManager eclpItemManager;

    @Autowired
    @Qualifier("obcsManager")
    private OBCSManager obcsManager;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private SendDatailDao sendDetailDao;

    @Autowired
    private EclpOpenManager eclpOpenManager;

    @Deprecated
    public InboundOrder createInboundOrder(String waybillCode, SendDetail sendDetail) {
        InboundOrder inboundOrder = new InboundOrder();
        inboundOrder.setOrderNo(sendDetail.getSendCode());
        inboundOrder.setWaybillNo(waybillCode);
        inboundOrder.setOperatorName(sendDetail.getCreateUser());
        inboundOrder.setOperateTime(sendDetail.getOperateTime());
        inboundOrder.setSource(InboundSourceEnum.CHUN_PEI);
        inboundOrder.setOrderType(InboundOrderTypeEnum.PURCHASE);
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendDetail.getSendCode());
        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
        String dmdStoreId = siteOrgDto.getStoreCode();
        String[] cky2AndStoreId = dmdStoreId.split("-");
        //退备件库时启用默认值 -1 原因为ECLP只认-1 如果开关开启则使用青龙基础资料维护的CKY2
        String cky2 = "-1";
        if (useQLBaiscCky2()) {
            cky2 = cky2AndStoreId[1];
        }
        String storeId = cky2AndStoreId[2];
        inboundOrder.setInOrgNo(siteOrgDto.getOrgId().toString());
        inboundOrder.setInDistributionNo(cky2);
        inboundOrder.setInWarehouseNo(storeId);
        //商品明细 √
        List<GoodsInfoItem> list = new ArrayList<GoodsInfoItem>();
        inboundOrder.setGoodsInfoItemList(list);
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillExtend(true);
        wChoice.setQueryGoodList(true);
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoiceNoCache(waybillCode,wChoice);//根据新单号获取运单信息
        if (baseEntity != null && baseEntity.getData() != null ) {
            if (baseEntity.getData().getWaybill() != null
                    && baseEntity.getData().getWaybill().getWaybillExt() != null
                    && baseEntity.getData().getWaybill().getWaybillExt().getPaymentAmount() != null) {
                BigDecimal paymentAmount = baseEntity.getData().getWaybill().getWaybillExt().getPaymentAmount();
                //纯配运单理赔总金额
                inboundOrder.setCompensationMoney(paymentAmount.toString());
            }
            //纯配商品信息
            if (baseEntity.getData().getGoodsList() != null && baseEntity.getData().getGoodsList().size() > 0) {
                List<Goods> goodsList = baseEntity.getData().getGoodsList();
                //获取已退包裹号
                List<String> packageCodeList = sendDetailDao.queryPackageCode(sendDetail);
                //已退包裹的商品集合
                List<Goods> goodsListOfSend = new ArrayList<>();
                for(Goods goods : goodsList){
                    if(packageCodeList.contains(goods.getPackBarcode())){
                        goodsListOfSend.add(goods);
                    }
                }
                for (Goods goods : goodsListOfSend) {
                    List<SparsModel> spareList = goods.getSpareList();
                    if (spareList != null && spareList.size() > 0) {
                        for (SparsModel sparsModel : spareList) {
                            GoodsInfoItem goodsInfoItem = new GoodsInfoItem();
                            goodsInfoItem.setGoodsName(goods.getGoodName());
                            goodsInfoItem.setGoodsNo(sparsModel.getSku());
                            goodsInfoItem.setBatchNo(sparsModel.getSpareCode());//备件条码
                            goodsInfoItem.setNum(1);
                            goodsInfoItem.setMoney(sparsModel.getClaimAmount() == null ? null : sparsModel.getClaimAmount().toString());//一个备件条码对应一个理赔金额
                            list.add(goodsInfoItem);
                        }
                    }
                }
            } else {
                this.log.warn("通过运单号{}获取运单商品明细失败!",waybillCode);
            }
        }else {
            this.log.warn("通过运单号{}获取运单信息失败!",waybillCode);
        }
        String oldWaybillCodeV1 = null; //一次换单原单号
        String oldWaybillCodeV2 = null; //二次换单原单号
        String eclpBusiOrderCode = null;
        BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill1 = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
        if (oldWaybill1 != null && oldWaybill1.getData() != null && oldWaybill1.getData().getBusiOrderCode() != null) {
            oldWaybillCodeV1 = oldWaybill1.getData().getWaybillCode();
            eclpBusiOrderCode = oldWaybill1.getData().getBusiOrderCode();
            BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill2 = waybillQueryManager.getWaybillByReturnWaybillCode(oldWaybillCodeV1);
            if (oldWaybill2 != null && oldWaybill2.getData() != null) {
                oldWaybillCodeV2 = oldWaybill2.getData().getWaybillCode();
                LocalClaimInfoRespDTO claimInfoRespDTO = obcsManager.getClaimListByClueInfo(1, oldWaybillCodeV2);
                String compensationMoney = null;
                if (claimInfoRespDTO != null) {
                    //纯配仓配二次换单
                    compensationMoney = claimInfoRespDTO.getPaymentRealMoney()==null?null:claimInfoRespDTO.getPaymentRealMoney().toString();   //仓配运单理赔金额
                    inboundOrder.setOuId(claimInfoRespDTO.getSettleSubjectCode());  //结算主体
                    inboundOrder.setOuName(claimInfoRespDTO.getSettleSubjectName());//结算主体名称
                    String deptNo = eclpItemManager.getDeptBySettlementOuId(claimInfoRespDTO.getSettleSubjectCode());
                    inboundOrder.setTargetDeptNo(deptNo);//目的事业部编码
                } else {
                    log.warn("组装逆向退备件库运单集合时出现异常数据,理赔接口异常:{}",oldWaybillCodeV2);
                    return null;
                }
                if(WaybillUtil.isECLPByBusiOrderCode(eclpBusiOrderCode)) {
                    //仓配
                    inboundOrder.setSaleOrderNo(eclpBusiOrderCode);
                    inboundOrder.setCompensationMoney(compensationMoney);
                    inboundOrder.setSource(InboundSourceEnum.BD);
                    List<ItemInfo> itemInfos = eclpItemManager.getltemBySoNo(eclpBusiOrderCode);
                    if (itemInfos != null && itemInfos.size() > 0) {
                        list.clear(); //清理商品信息，仓配只认ECLP主数据商品，不需要运单的商品数据
                        //原事业部ID (仓配有纯配无)
                        inboundOrder.setOriginDeptId(itemInfos.get(0).getDeptId());
                        //仓配商品信息
                        for(ItemInfo itemInfo : itemInfos){
                            if(StringUtils.isBlank(itemInfo.getGoodsNo())){
                                continue; //过滤商品编码不存在的数据
                            }
                            GoodsInfoItem goodsInfoItem = new GoodsInfoItem();
                            goodsInfoItem.setGoodsNo(itemInfo.getGoodsNo());
                            goodsInfoItem.setGoodsName(itemInfo.getGoodsName());
                            if(itemInfo.getDeptRealOutQty() == null){
                                //使用实际发货数量
                                goodsInfoItem.setNum(itemInfo.getRealOutstoreQty()==null?0:itemInfo.getRealOutstoreQty());
                            }else{
                                //使用事业部实际发货的数量
                                goodsInfoItem.setNum(itemInfo.getDeptRealOutQty()==null?0:itemInfo.getDeptRealOutQty());
                            }
                            list.add(goodsInfoItem);
                        }
                    } else {
                        log.warn("通过:{}获取原事业部信息为空!",eclpBusiOrderCode);
                        return null;
                    }
                }
            }else if(oldWaybill2 == null || oldWaybill2.getData() == null) {
                //纯配一次换单
                LocalClaimInfoRespDTO claimInfoRespDTO = obcsManager.getClaimListByClueInfo(1, oldWaybillCodeV1);
                if (claimInfoRespDTO != null) {
                    inboundOrder.setOuId(claimInfoRespDTO.getSettleSubjectCode());
                    inboundOrder.setOuName(claimInfoRespDTO.getSettleSubjectName());
                    String deptNo = eclpItemManager.getDeptBySettlementOuId(claimInfoRespDTO.getSettleSubjectCode());
                    inboundOrder.setTargetDeptNo(deptNo);//目的事业部编码
                }else{
                    log.warn("组装逆向退备件库运单集合时出现异常数据,理赔接口异常:{}",oldWaybillCodeV1);
                    return null;
                }
            }else {
                log.warn("组装逆向退备件库运单集合时出现异常数据，原单不符合规则");
                return null;
            }
        }else {
            log.warn("组装逆向退备件库运单集合时出现异常数据，原单不符合规则");
            return null;
        }
        return inboundOrder;
    }

    @Override
    public InboundOrder makeInboundOrder(String waybillCode, SendDetail sendDetail) {

        try{
            boolean makeOtherSuccess;
            InboundOrder inboundOrder = new InboundOrder();
            inboundOrder = makeCommonInboundOrder(waybillCode, sendDetail, inboundOrder);
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillExtend(true);
            wChoice.setQueryGoodList(true);
            wChoice.setQueryPackList(true);
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoiceNoCache(waybillCode,wChoice);//根据新单号获取运单信息
            if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null ) {
                if(BusinessHelper.isC2c(baseEntity.getData().getWaybill().getWaybillSign())){
                    //C2C纯配  C2C纯配满足纯配的标所以优先判断
                    makeOtherSuccess = makeC2cOther(baseEntity,waybillCode, inboundOrder);
                }else if(BusinessUtil.isPurematch(baseEntity.getData().getWaybill().getWaybillSign())){
                    //纯配
                    makeOtherSuccess = makePureOther(baseEntity,waybillCode, sendDetail, inboundOrder);
                }else{
                    //仓配
                    makeOtherSuccess = makeWMSOther(waybillCode, inboundOrder);
                }
                if(!makeOtherSuccess){
                   return null;
                }
            }else {
                log.warn("ECLP退备件库组装入库单对象未获取到运单信息:{}",waybillCode);
                return null;
            }
            return inboundOrder;
        }catch (Exception e){
            log.error("组装ECLP退备件库对象异常:{}",waybillCode,e);
            return null;
        }


    }

    /**
     * C2C 退配件库 特殊字段处理
     * @param waybillCode
     * @param inboundOrder
     */
    private boolean makeC2cOther(BaseEntity<BigWaybillDto> baseEntity,String waybillCode, InboundOrder inboundOrder) {
        //获取依赖数据
        List<DeliveryPackageD> packageDList = baseEntity.getData().getPackageList();
        String goodName = DEFAULT_GOODS_NAME;
        if(baseEntity.getData().getWaybill().getWaybillExt()!=null && StringUtils.isNotBlank(baseEntity.getData().getWaybill().getWaybillExt().getConsignWare())){
            goodName = baseEntity.getData().getWaybill().getWaybillExt().getConsignWare();
        }else{
            log.error("C2C退配件库未获取到托寄物名称，使用默认名称:{}",waybillCode);
        }
        String oldWaybillCodeV1;
        BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill1 = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
        if (oldWaybill1 != null && oldWaybill1.getData() != null && oldWaybill1.getData().getBusiOrderCode() != null) {
            oldWaybillCodeV1 = oldWaybill1.getData().getWaybillCode();
        }else{
            log.warn("C2C退配件库未获取到原单信息:{}",waybillCode);
            return false;
        }

        //设定特殊值
        inboundOrder.setOldWaybillNo(oldWaybillCodeV1); //原运单号（正向第一个运单，此处需调运单接口，根据新单号查询出正向第一个单号）
        inboundOrder.setWaybillNo(waybillCode);
        inboundOrder.setSource(InboundSourceEnum.SORTING_C2C);
        inboundOrder.setTargetDeptNo(C2C_DEFT_NO);//固定值 时婧提供
        List<GoodsInfoItem> goodsInfoItems = inboundOrder.getGoodsInfoItemList();


        //创建商品主数据  每个包裹创建一次  对方不提供批量接口

        for(DeliveryPackageD packageD : packageDList){
            GoodsInfo goodsInfo = new GoodsInfo();
            goodsInfo.setDeptNo(C2C_DEFT_NO);//固定值 时婧提供
            goodsInfo.setIsvGoodsNo(packageD.getPackageBarcode());//包裹号
            goodsInfo.setBarcodes(packageD.getPackageBarcode());//包裹号
            goodsInfo.setThirdCategoryNo(THIRD_CATEGORY_NO);//ECLP提供固定值 ，对接徐德凤 一级9669 二级9691 三级9694
            goodsInfo.setGoodsName(goodName);
            goodsInfo.setSafeDays(DEFAULT_SAFE_DAYS);
            String goodCode = eclpOpenManager.transportGoodsInfo(goodsInfo);
            if(StringUtils.isBlank(goodCode)){
                log.warn("创建商品主数据失败:{}",JsonHelper.toJson(goodsInfo));
                return false;
            }
            //组装商品信息
            GoodsInfoItem goodsInfoItem = new GoodsInfoItem();
            goodsInfoItem.setGoodsNo(goodCode);
            goodsInfoItem.setGoodsName(goodName);
            goodsInfoItem.setBatchNo(packageD.getPackageBarcode());
            goodsInfoItem.setNum(DEFAULT_GOOD_NUM);
            goodsInfoItems.add(goodsInfoItem);
        }

        return true;

    }


    /**
     * 仓配退备件库 组装特殊字段
     * @param waybillCode
     * @param inboundOrder
     */
    private boolean makeWMSOther(String waybillCode, InboundOrder inboundOrder) {

        inboundOrder.setSource(InboundSourceEnum.BD);

        String oldWaybillCodeV1 = null; //二次换单原单号
        String oldWaybillCodeV2 = null; //一次换单原单号
        String eclpBusiOrderCode = null;
        List<GoodsInfoItem> goodsInfoItems = inboundOrder.getGoodsInfoItemList();
        BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill1 = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
        if (oldWaybill1 != null && oldWaybill1.getData() != null && oldWaybill1.getData().getBusiOrderCode() != null) {
            oldWaybillCodeV1 = oldWaybill1.getData().getWaybillCode();
            eclpBusiOrderCode = oldWaybill1.getData().getBusiOrderCode();
            BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill2 = waybillQueryManager.getWaybillByReturnWaybillCode(oldWaybillCodeV1);
            if (oldWaybill2 != null && oldWaybill2.getData() != null) {
                oldWaybillCodeV2 = oldWaybill2.getData().getWaybillCode();

                if(WaybillUtil.isECLPByBusiOrderCode(eclpBusiOrderCode)) {
                    //仓配
                    if(!makeLPOther( inboundOrder,  oldWaybillCodeV2, true)){
                        return false;
                    }

                    inboundOrder.setSaleOrderNo(eclpBusiOrderCode);

                    List<ItemInfo> itemInfos = eclpItemManager.getltemBySoNo(eclpBusiOrderCode);
                    if (itemInfos != null && itemInfos.size() > 0) {

                        //原事业部ID (仓配有纯配无)
                        inboundOrder.setOriginDeptId(itemInfos.get(0).getDeptId());
                        //仓配商品信息
                        for(ItemInfo itemInfo : itemInfos){
                            if(StringUtils.isBlank(itemInfo.getGoodsNo())){
                                continue; //过滤商品编码不存在的数据
                            }
                            GoodsInfoItem goodsInfoItem = new GoodsInfoItem();
                            goodsInfoItem.setGoodsNo(itemInfo.getGoodsNo());
                            goodsInfoItem.setGoodsName(itemInfo.getGoodsName());
                            if(itemInfo.getDeptRealOutQty() == null){
                                //使用实际发货数量
                                goodsInfoItem.setNum(itemInfo.getRealOutstoreQty()==null?0:itemInfo.getRealOutstoreQty());
                            }else{
                                //使用事业部实际发货的数量
                                goodsInfoItem.setNum(itemInfo.getDeptRealOutQty()==null?0:itemInfo.getDeptRealOutQty());
                            }
                            goodsInfoItems.add(goodsInfoItem);
                        }
                    } else {
                        log.warn("通过:{} 获取原事业部信息为空!",eclpBusiOrderCode);
                        return false;
                    }
                }else{
                    log.warn("仓配获取数据失败 原单的商家单号非ESL开头!{}|{}" ,oldWaybillCodeV2,oldWaybillCodeV1);
                    return false;
                }
            }else {
                log.warn("组装逆向退备件库运单集合时出现异常数据，原单不符合规则");
                return false;
            }
        }else {
            log.warn("组装逆向退备件库运单集合时出现异常数据，原单不符合规则");
            return false;
        }
        return true;
    }

    /**
     * 纯配退备件库 组装特殊字段
     * @param baseEntity
     * @param waybillCode
     * @param sendDetail
     * @param inboundOrder
     */
    private boolean makePureOther(BaseEntity<BigWaybillDto> baseEntity,String waybillCode, SendDetail sendDetail, InboundOrder inboundOrder) {
        inboundOrder.setSource(InboundSourceEnum.CHUN_PEI);
        List<GoodsInfoItem> goodsInfoItems = inboundOrder.getGoodsInfoItemList();
        if (baseEntity != null && baseEntity.getData() != null ) {
            if (baseEntity.getData().getWaybill() != null
                    && baseEntity.getData().getWaybill().getWaybillExt() != null
                    && baseEntity.getData().getWaybill().getWaybillExt().getPaymentAmount() != null) {
                BigDecimal paymentAmount = baseEntity.getData().getWaybill().getWaybillExt().getPaymentAmount();
                //纯配运单理赔总金额
                inboundOrder.setCompensationMoney(paymentAmount.toString());
            }
            //纯配商品信息
            if (baseEntity.getData().getGoodsList() != null && baseEntity.getData().getGoodsList().size() > 0) {
                List<Goods> goodsList = baseEntity.getData().getGoodsList();
                //获取已退包裹号
                List<String> packageCodeList = sendDetailDao.queryPackageCode(sendDetail);
                //已退包裹的商品集合
                List<Goods> goodsListOfSend = new ArrayList<>();
                for(Goods goods : goodsList){
                    if(packageCodeList.contains(goods.getPackBarcode())){
                        goodsListOfSend.add(goods);
                    }
                }
                for (Goods goods : goodsListOfSend) {
                    List<SparsModel> spareList = goods.getSpareList();
                    if (spareList != null && spareList.size() > 0) {
                        for (SparsModel sparsModel : spareList) {
                            GoodsInfoItem goodsInfoItem = new GoodsInfoItem();
                            goodsInfoItem.setGoodsName(goods.getGoodName());
                            goodsInfoItem.setGoodsNo(sparsModel.getSku());
                            goodsInfoItem.setBatchNo(sparsModel.getSpareCode());//备件条码
                            goodsInfoItem.setNum(1);
                            goodsInfoItem.setMoney(sparsModel.getClaimAmount() == null ? null : sparsModel.getClaimAmount().toString());//一个备件条码对应一个理赔金额
                            goodsInfoItems.add(goodsInfoItem);
                        }
                    }
                }
            } else {
                this.log.warn("通过运单号{}获取运单商品明细失败!",waybillCode);
                return false;
            }
            String oldWaybillCodeV1 = null; //一次换单原单号

            BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill1 = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
            if (oldWaybill1 != null && oldWaybill1.getData() != null && oldWaybill1.getData().getBusiOrderCode() != null) {
                oldWaybillCodeV1 = oldWaybill1.getData().getWaybillCode();
                //纯配一次换单
                if(!makeLPOther(inboundOrder, oldWaybillCodeV1,false)){
                    return false;
                }
            }


        }else {
            this.log.warn("通过运单号{}获取运单信息失败!",waybillCode);
            return false;
        }
        return true;
    }

    /**
     * 组装理赔属性
     * @param inboundOrder
     * @param waybillCode
     * @param isUseLPMoney
     */
    private boolean makeLPOther(InboundOrder inboundOrder, String waybillCode,boolean isUseLPMoney) {
        LocalClaimInfoRespDTO claimInfoRespDTO = obcsManager.getClaimListByClueInfo(1, waybillCode);
        if (claimInfoRespDTO != null) {
            if(isUseLPMoney){
                String compensationMoney = claimInfoRespDTO.getPaymentRealMoney()==null?null:claimInfoRespDTO.getPaymentRealMoney().toString();   //仓配运单理赔金额
                inboundOrder.setCompensationMoney(compensationMoney);
            }
            inboundOrder.setOuId(claimInfoRespDTO.getSettleSubjectCode());
            inboundOrder.setOuName(claimInfoRespDTO.getSettleSubjectName());
            String deptNo = eclpItemManager.getDeptBySettlementOuId(claimInfoRespDTO.getSettleSubjectCode());
            inboundOrder.setTargetDeptNo(deptNo);//目的事业部编码
        }else{
            log.warn("组装逆向退备件库运单集合时出现异常数据,理赔接口异常:{}",waybillCode);
            return false;
        }
        return true;
    }

    /**
     * 组装公共属性
     * @param waybillCode
     * @param sendDetail
     * @param inboundOrder
     * @return
     */
    private InboundOrder makeCommonInboundOrder(String waybillCode, SendDetail sendDetail, InboundOrder inboundOrder) {
        String orderNo = sendDetail.getSendCode()+","+waybillCode;
        inboundOrder.setOrderNo(orderNo);
        inboundOrder.setWaybillNo(waybillCode);
        inboundOrder.setOperatorName(sendDetail.getCreateUser());
        inboundOrder.setOperateTime(sendDetail.getOperateTime());
        inboundOrder.setOrderType(InboundOrderTypeEnum.PURCHASE);
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendDetail.getSendCode());
        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
        String dmdStoreId = siteOrgDto.getStoreCode();
        String[] cky2AndStoreId = dmdStoreId.split("-");
        //退备件库时启用默认值 -1 原因为ECLP只认-1 如果开关开启则使用青龙基础资料维护的CKY2
        String cky2 = "-1";
        if (useQLBaiscCky2()) {
            cky2 = cky2AndStoreId[1];
        }
        String storeId = cky2AndStoreId[2];
        inboundOrder.setInOrgNo(siteOrgDto.getOrgId().toString());
        inboundOrder.setInDistributionNo(cky2);
        inboundOrder.setInWarehouseNo(storeId);
        //商品明细 √
        List<GoodsInfoItem> list = new ArrayList<GoodsInfoItem>();
        inboundOrder.setGoodsInfoItemList(list);
        return inboundOrder;
    }

    private boolean useQLBaiscCky2(){
        try {
            List<SysConfig> sysConfigs = sysConfigService.getListByConfigName("reverse.eclp.cky2.switch");
            if (null == sysConfigs || sysConfigs.size() <= 0) {
                return false;
            } else {
                if(sysConfigs.get(0).getConfigContent()==null){
                    return false;
                }
                return sysConfigs.get(0).getConfigContent().equals("1");
            }
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * 判断纯配外单是否可逆向换单（理赔中不允许换单）
     * @param result
     */
    public boolean checkIsPureMatch(String waybillCode,String waybillSign,InvokeResult<Boolean> result){

        result.setData(true);
        if(StringUtils.isBlank(waybillCode)){
            return true;
        }
        try{
            //根据单号获取原单 如果未获取到则认为就是原单
            BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill1 = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
            if (oldWaybill1 != null && oldWaybill1.getData() != null ) {
                waybillCode = oldWaybill1.getData().getWaybillCode();
                waybillSign = oldWaybill1.getData().getWaybillSign();
            }

            if(StringUtils.isBlank(waybillSign)){
                //外部未传入waybillSign 自己再去调用一次
                BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true,true,true,false);
                if(baseEntity!=null && baseEntity.getData()!=null && baseEntity.getData().getWaybill() != null && StringUtils.isNotBlank(baseEntity.getData().getWaybill().getWaybillSign())){
                    waybillSign = baseEntity.getData().getWaybill().getWaybillSign();
                }else {
                    return true;
                }
            }
            //纯配外单（理赔中不允许换单）
            if(BusinessUtil.isPurematch(waybillSign)){
                LocalClaimInfoRespDTO claimInfoRespDTO =  obcsManager.getClaimListByClueInfo(1,waybillCode);
                if(claimInfoRespDTO != null && LocalClaimInfoRespDTO.LP_STATUS_DOING.equals(claimInfoRespDTO.getStatusDesc())){
                    result.setData(false);
                    result.setMessage("纯配外单未理赔完成，不能操作逆向换单!");
                }
            }

        }catch (Exception e){
            log.error("判断纯配外单是否可逆向换单（理赔完成且物权归京东）异常{}|{}",waybillCode,waybillSign,e);
        }

        return result.getData();
    }
}
