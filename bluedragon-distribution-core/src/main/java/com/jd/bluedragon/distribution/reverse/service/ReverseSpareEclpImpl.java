package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.EclpItemManager;
import com.jd.bluedragon.core.base.OBCSManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.reverse.domain.BdInboundECLPDetail;
import com.jd.bluedragon.distribution.reverse.domain.BdInboundECLPDto;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrder;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrderTypeEnum;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundSourceEnum;
import com.jd.eclp.spare.ext.api.outbound.GoodsInfoItem;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.SparsModel;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ECLP退备件库 业务逻辑
 */
@Service("reverseSpareEclp")
public class ReverseSpareEclpImpl implements ReverseSpareEclp {

    private final Log logger = LogFactory.getLog(this.getClass());

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

    @Override
    public BdInboundECLPDto makeEclpMessage(String waybillCode, SendDetail sendDetail) {

        BdInboundECLPDto bdInboundECLPDto = new BdInboundECLPDto();
        List<BdInboundECLPDetail> goodsList = new ArrayList<BdInboundECLPDetail>();
        bdInboundECLPDto.setGoodsList(goodsList);
        bdInboundECLPDto.setWaybillCode(waybillCode);
        bdInboundECLPDto.setSendCode(sendDetail.getSendCode());
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendDetail.getSendCode());
        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);

        String dmdStoreId = siteOrgDto.getStoreCode();

        String[] cky2AndStoreId = dmdStoreId.split("-");

        //退备件库时启用默认值 -1 原因为ECLP只认-1 如果开关开启则使用青龙基础资料维护的CKY2
        String cky2 = "-1";
        if(useQLBaiscCky2()){
            cky2 = cky2AndStoreId[1];
        }
        String storeId = cky2AndStoreId[2];

        // 组装 机构编码 配送中心编码 库房编码
        Waybill waybill =  waybillCommonService.findByWaybillCode(waybillCode);
        String eclpBusiOrderCode;
        String oldWaybillCodeV2; //V2单号
        if(waybill != null){
            BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybillResp = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
            if(oldWaybillResp!= null && oldWaybillResp.getData()!=null  && WaybillUtil.isECLPByBusiOrderCode(oldWaybillResp.getData().getBusiOrderCode())) {
                eclpBusiOrderCode = oldWaybillResp.getData().getBusiOrderCode();
                oldWaybillCodeV2 = oldWaybillResp.getData().getWaybillCode();
            }else{
                logger.error("组装逆向退备件库运单集合时出现异常数据，原单不符合规则"+waybillCode+"|"+sendDetail.getSendCode());
                return null;
            }
            bdInboundECLPDto.setCky2(cky2);
            bdInboundECLPDto.setStoreId(storeId);
            bdInboundECLPDto.setOrgId(siteOrgDto.getOrgId().toString());
            bdInboundECLPDto.setBusiOrderCode(eclpBusiOrderCode);
        }else {
            logger.error("组装逆向退备件库运单集合时出现异常数据，运单空"+waybillCode+"|"+sendDetail.getSendCode());
            return null;
        }

        //目的事业部编码(新事业部编码)

        List<ItemInfo> itemInfos =  eclpItemManager.getltemBySoNo(eclpBusiOrderCode);
        if(itemInfos!=null && itemInfos.size()>0){
            //商家事业部ID
            bdInboundECLPDto.setOriginDeptId(itemInfos.get(0).getDeptId().toString());
            //商品信息
            for(ItemInfo itemInfo : itemInfos){
                if(StringUtils.isBlank(itemInfo.getGoodsNo())){
                    continue; //过滤商品编码不存在的数据
                }
                BdInboundECLPDetail bdInboundECLPDetail = new BdInboundECLPDetail();
                bdInboundECLPDetail.setGoodsNo(itemInfo.getGoodsNo());
                bdInboundECLPDetail.setGoodsName(itemInfo.getGoodsName());
                if(itemInfo.getDeptRealOutQty() == null){
                    //使用实际发货数量
                    bdInboundECLPDetail.setNum(itemInfo.getRealOutstoreQty());
                }else{
                    //使用事业部实际发货的数量
                    bdInboundECLPDetail.setNum(itemInfo.getDeptRealOutQty());
                }
                goodsList.add(bdInboundECLPDetail);
            }

        }else{
            logger.error("组装逆向退备件库运单集合时出现异常数据,获取商品信息为空 v2:"+oldWaybillCodeV2+"|esl:"+eclpBusiOrderCode+"|v3"+waybillCode+"|"+sendDetail.getSendCode());
            return null;
        }

        //操作人  操作时间
        bdInboundECLPDto.setOperatorName(sendDetail.getCreateUser());
        bdInboundECLPDto.setOperateTime(DateHelper.formatDate(sendDetail.getOperateTime(),Constants.DATE_TIME_FORMAT));


        //理赔金额
        //获取老单号  通过V3 获取 V2 在通过V2  获取V1
        BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybillCodeV1 = waybillQueryManager.getWaybillByReturnWaybillCode(oldWaybillCodeV2);
        if(oldWaybillCodeV1.getResultCode()==1 && oldWaybillCodeV1.getData()!=null && StringUtils.isNotBlank(oldWaybillCodeV1.getData().getWaybillCode())) {
            String oldWaybillCodeV1Code = oldWaybillCodeV1.getData().getWaybillCode(); //V1
            //理赔接口
            LocalClaimInfoRespDTO claimInfoRespDTO = obcsManager.getClaimListByClueInfo(1, oldWaybillCodeV1Code);
            if(claimInfoRespDTO != null){
                //理赔金额 结算主体 结算主体名称
                bdInboundECLPDto.setCompensationMoney(claimInfoRespDTO.getPaymentRealMoney());
                bdInboundECLPDto.setSettleSubjectCode(claimInfoRespDTO.getSettleSubjectCode());
                bdInboundECLPDto.setSettleSubjectName(claimInfoRespDTO.getSettleSubjectName());
            }else{
                logger.error("组装逆向退备件库运单集合时出现异常数据,理赔接口异常"+waybillCode+"|"+sendDetail.getSendCode());
                return null;
            }
        }else{
            logger.error("组装逆向退备件库运单集合时出现异常数据,获取V1异常"+waybillCode+"|"+sendDetail.getSendCode());
            return null;
        }

        bdInboundECLPDto.setSupportLack( (byte)(1));

        return bdInboundECLPDto;
    }

    @Override
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
                this.logger.warn("通过运单号" + waybillCode + "获取运单商品明细失败!");
            }
        }else {
            this.logger.warn("通过运单号" + waybillCode + "获取运单信息失败!");
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
                    logger.warn("组装逆向退备件库运单集合时出现异常数据,理赔接口异常:"+oldWaybillCodeV2);
                }
                if(WaybillUtil.isECLPByBusiOrderCode(eclpBusiOrderCode)) {
                    //仓配
                    inboundOrder.setSaleOrderNo(eclpBusiOrderCode);
                    inboundOrder.setCompensationMoney(compensationMoney);
                    inboundOrder.setSource(InboundSourceEnum.BD);
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
                            list.add(goodsInfoItem);
                        }
                    } else {
                        logger.warn("通过:"+eclpBusiOrderCode+"获取原事业部信息为空!");
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
                    logger.warn("组装逆向退备件库运单集合时出现异常数据,理赔接口异常:"+oldWaybillCodeV1);
                }
            }else {
                logger.warn("组装逆向退备件库运单集合时出现异常数据，原单不符合规则");
            }
        }else {
            logger.warn("组装逆向退备件库运单集合时出现异常数据，原单不符合规则");
        }
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
            logger.error("判断纯配外单是否可逆向换单（理赔完成且物权归京东）异常"+waybillCode+"|"+waybillSign,e);
        }

        return result.getData();
    }
}
