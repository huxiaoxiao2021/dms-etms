package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.GoodsPrintEsManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.goodsPrint.service.GoodsPrintService;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 发货明细消费 托寄物品名打印用
 * @date 2018年11月21日 17时:47分
 */
@Service("sendDetailGoodsPrintConsumer")
public class SendDetailGoodsPrintConsumer extends MessageBaseConsumer {
    private static final Logger log = LoggerFactory.getLogger(SendDetailGoodsPrintConsumer.class);
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    @Autowired
    private GoodsPrintEsManager goodsPrintEsManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private GoodsPrintService goodsPrintService;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("[SendDetailGoodsPrintConsumer消费]MQ-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        SendDetailMessage sendDetail = JsonHelper.fromJsonUseGson(message.getText(), SendDetailMessage.class);

        String packageBarCode = sendDetail.getPackageBarcode();
        if (!SerialRuleUtil.isWaybillOrPackageNo(packageBarCode)) {
            log.warn("[SendDetailGoodsPrintConsumer消费]无效的运单号/包裹号，packageBarCode:{},boxCode:{}" ,packageBarCode, sendDetail.getBoxCode());
            return;
        }

        String waybillCode = WaybillUtil.getWaybillCode(packageBarCode);
        BaseEntity<BigWaybillDto> baseEntity = getWaybillBaseEntity(waybillCode);
        if (baseEntity == null || baseEntity.getData().getWaybill() == null) {
            log.warn("[SendDetailGoodsPrintConsumer消费]根据运单号获取运单信息为空，packageBarCode:{},boxCode:{},waybillCode:{}"
                    ,packageBarCode,sendDetail.getBoxCode(),waybillCode);
            return;
        }
        Waybill waybill = baseEntity.getData().getWaybill();
        //将运单维度的 托寄物品名是数据 写到es
        //缓存中有 表示不久前，已操作过同运单的包裹，不需要重复写入es了
        String key1 = Constants.GOODS_PRINT_WAYBILL_STATUS_1 + Constants.SEPARATOR_HYPHEN + sendDetail.getSendCode() + Constants.SEPARATOR_HYPHEN + waybillCode + Constants.SEPARATOR_HYPHEN + (BusinessUtil.isBoxcode(sendDetail.getBoxCode()) ? sendDetail.getBoxCode() : "");
        String key0 = Constants.GOODS_PRINT_WAYBILL_STATUS_0 + Constants.SEPARATOR_HYPHEN + sendDetail.getSendCode() + Constants.SEPARATOR_HYPHEN + waybillCode;
        if (goodsPrintService.getWaybillFromEsOperator(key1)) {
            return;
        }
        //缓存中没有有2种情况 1：缓存过期 2：es就一直没存过
        //查es 是否有该运单
        GoodsPrintDto goodsPrintDto = goodsPrintEsManager.findGoodsPrintBySendCodeAndWaybillCode(sendDetail.getSendCode(), waybillCode);
        //es中查不到，就是真没有了， insert该运单
        if (goodsPrintDto == null) {
            goodsPrintDto = buildGoodsPrintDto(sendDetail, waybill);
        } else {
            //如果存在一单分布在不同的箱子里，要把箱号拼起来
            if (goodsPrintDto.getBoxCode() != null && BusinessUtil.isBoxcode(sendDetail.getBoxCode()) && !goodsPrintDto.getBoxCode().contains(sendDetail.getBoxCode())) {
                goodsPrintDto.setBoxCode(goodsPrintDto.getBoxCode() + (StringUtils.isBlank(goodsPrintDto.getBoxCode()) ? "" : Constants.SEPARATOR_COMMA) + sendDetail.getBoxCode());
            } else if (goodsPrintDto.getBoxCode() == null && BusinessUtil.isBoxcode(sendDetail.getBoxCode())) {
                goodsPrintDto.setBoxCode(sendDetail.getBoxCode());
            }
            //改为发货状态
            goodsPrintDto.setSendStatus(Constants.GOODS_PRINT_WAYBILL_STATUS_1);
        }
        //加到redis中
        if (goodsPrintEsManager.insertOrUpdate(goodsPrintDto)) {
            goodsPrintService.setWaybillFromEsOperator(key1);
            goodsPrintService.deleteWaybillFromEsOperator(key0);
        }

    }

    /**
     * 调用运单JSF接口获取运单基础数据信息
     *
     * @param waybillCode
     * @return
     */
    private BaseEntity<BigWaybillDto> getWaybillBaseEntity(String waybillCode) {
        WChoice choice = new WChoice();
        choice.setQueryWaybillC(true);
        choice.setQueryWaybillExtend(true);
        return waybillQueryManager.getDataByChoice(waybillCode, choice);
    }

    private GoodsPrintDto buildGoodsPrintDto(SendDetailMessage sendDetail, Waybill waybill) {
        GoodsPrintDto goodsPrintDto;
        goodsPrintDto = new GoodsPrintDto();
        goodsPrintDto.setSendCode(sendDetail.getSendCode());
        goodsPrintDto.setVendorId(waybill.getVendorId());
        goodsPrintDto.setWaybillCode(waybill.getWaybillCode());
        BaseStaffSiteOrgDto createSite = this.baseMajorManager
                .getBaseSiteBySiteId(sendDetail.getCreateSiteCode());
        if (createSite != null) {
            goodsPrintDto.setCreateSiteCode(createSite.getSiteCode());
            goodsPrintDto.setCreateSiteName(createSite.getSiteName());
        }
        BaseStaffSiteOrgDto receiveSite = this.baseMajorManager
                .getBaseSiteBySiteId(sendDetail.getReceiveSiteCode());
        if (receiveSite != null) {
            goodsPrintDto.setReceiveSiteCode(receiveSite.getSiteCode());
            goodsPrintDto.setReceiveSiteName(receiveSite.getSiteName());
        }
        goodsPrintDto.setOperateTime(new Date(sendDetail.getOperateTime()));
        if (BusinessUtil.isBoxcode(sendDetail.getBoxCode())) {
            goodsPrintDto.setBoxCode(sendDetail.getBoxCode());
        }
        if (waybill.getWaybillExt() != null && waybill.getWaybillExt().getConsignWare() != null) {
            goodsPrintDto.setConsignWare(waybill.getWaybillExt().getConsignWare());
        }
        goodsPrintDto.setSendStatus(Constants.GOODS_PRINT_WAYBILL_STATUS_1);
        return goodsPrintDto;
    }
}
