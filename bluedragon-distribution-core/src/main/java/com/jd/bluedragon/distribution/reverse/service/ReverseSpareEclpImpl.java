package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.EclpItemManager;
import com.jd.bluedragon.core.base.OBCSManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.reverse.domain.BdInboundECLPDetail;
import com.jd.bluedragon.distribution.reverse.domain.BdInboundECLPDto;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uad.api.claim.facade.claim.resp.ClaimInfoRespDTO;
import com.jd.uad.api.core.APIResultDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
        String cky2 = cky2AndStoreId[1];
        String storeId = cky2AndStoreId[2];

        // 组装 机构编码 配送中心编码 库房编码
        Waybill waybill =  waybillCommonService.findByWaybillCode(waybillCode);
        if(waybill != null){
            bdInboundECLPDto.setCky2(cky2);
            bdInboundECLPDto.setStoreId(storeId);
            bdInboundECLPDto.setOrgId(siteOrgDto.getOrgId().toString());
            bdInboundECLPDto.setBusiOrderCode(waybill.getBusiOrderCode());
        }

        //目的事业部编码(新事业部编码)


        List<ItemInfo> itemInfos =  eclpItemManager.getltemBySoNo(waybill.getBusiOrderCode());
        if(itemInfos!=null && itemInfos.size()>0){
            //商家事业部ID
            bdInboundECLPDto.setOriginDeptId(itemInfos.get(0).getDeptId().toString());
            //商品信息
            for(ItemInfo itemInfo : itemInfos){
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

        }

        //操作人  操作时间
        bdInboundECLPDto.setOperatorName(sendDetail.getCreateUser());
        bdInboundECLPDto.setOperateTime(sendDetail.getOperateTime());


        //理赔金额
        //获取老单号  通过V3 获取 V2 在通过V2 获取V1
        BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
        if(oldWaybill.getResultCode()==1 && oldWaybill.getData()!=null && StringUtils.isNotBlank(oldWaybill.getData().getWaybillCode())) {
            String oldWaybillCode = oldWaybill.getData().getWaybillCode(); //V2

            //在此获取老单号 V1
            BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill2 = waybillQueryManager.getWaybillByReturnWaybillCode(oldWaybillCode);
            if(oldWaybill2.getResultCode()==1 && oldWaybill2.getData()!=null && StringUtils.isNotBlank(oldWaybill2.getData().getWaybillCode())) {
                String oldWaybill2Code = oldWaybill2.getData().getWaybillCode(); //V2
                //理赔接口
                LocalClaimInfoRespDTO claimInfoRespDTO = obcsManager.getClaimListByClueInfo(1, oldWaybill2Code);

                //理赔金额 结算主体 结算主体名称
                bdInboundECLPDto.setCompensationMoney(claimInfoRespDTO.getPaymentRealMoney());
                bdInboundECLPDto.setSettleSubjectCode(claimInfoRespDTO.getSettleSubjectCode());
                bdInboundECLPDto.setSettleSubjectName(claimInfoRespDTO.getSettleSubjectName());


            }
        }

        bdInboundECLPDto.setSupportLack( (byte)(1));


        return null;
    }

}
