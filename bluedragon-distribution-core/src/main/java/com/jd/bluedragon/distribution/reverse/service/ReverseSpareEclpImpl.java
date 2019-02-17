package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.EclpItemManager;
import com.jd.bluedragon.core.base.OBCSManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.reverse.domain.BdInboundECLPDetail;
import com.jd.bluedragon.distribution.reverse.domain.BdInboundECLPDto;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
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

    @Autowired
    private SysConfigService sysConfigService;

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
}
