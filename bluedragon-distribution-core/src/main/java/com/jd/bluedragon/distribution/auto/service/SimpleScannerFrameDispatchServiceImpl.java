package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.areadest.service.AreaDestService;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wangtingwei on 2016/3/10.
 */
@Service("scannerFrameDispatchService")
public class SimpleScannerFrameDispatchServiceImpl implements ScannerFrameDispatchService {

    private static final Log logger = LogFactory.getLog(SimpleScannerFrameDispatchServiceImpl.class);

    @Autowired
    private GantryDeviceConfigService gantryDeviceConfigService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private AreaDestService areaDestService;

    @Autowired
    private ScannerFrameBatchSendService scannerFrameBatchSendService;

    @Autowired
    private GantryExceptionService gantryExceptionService;

    /**
     * 此处只能使用@Resource注解，使用@Autowired会报错
     * Key type [class java.lang.Integer] of map [java.util.Map]
     * must be assignable to [java.lang.String]
     */
    @Resource(name = "scannerFrameConsumeMap")
    private Map<Integer, ScannerFrameConsume> scannerFrameConsumeMap;

    private static final String BOX_SUFFIX = "-CL";

    @Override
    public boolean dispatch(UploadData domain) {

        GantryDeviceConfig config = gantryDeviceConfigService.findGantryDeviceConfigByOperateTime(Integer.parseInt(domain.getRegisterNo()), domain.getScannerTime());
        if (logger.isInfoEnabled()) {
            logger.info(MessageFormat.format("获取龙门架操作方式registerNo={0},operateTime={1}|结果{2}", domain.getRegisterNo(), domain.getScannerTime(), JsonHelper.toJson(config)));
        }
        if (null == config) {
            if (logger.isWarnEnabled()) {
                logger.warn(MessageFormat.format("获取龙门架操作方式registerNo={0},operateTime={1}|结果为NULL", domain.getRegisterNo(), domain.getScannerTime()));
            }
            return true;
        }
        boolean result = false;
        domain.setBarCode(StringUtils.remove(domain.getBarCode(), BOX_SUFFIX));/*龙门加校正箱号后面-CF*/
        // 判断操作类型是否为发货并且龙门架为新设备
        if (config.getIsNew() == 1) {
            String sendCode = getSendCode(domain, config);
            if (sendCode != null && !"".equals(sendCode)) {
                config.setSendCode(sendCode);
            } else {
                return result;
            }
        }

        Iterator<Map.Entry<Integer, ScannerFrameConsume>> item = scannerFrameConsumeMap.entrySet().iterator();
        while (item.hasNext()) {
            Map.Entry<Integer, ScannerFrameConsume> consume = item.next();
            if (consume.getKey().intValue() == (config.getBusinessType().intValue() & consume.getKey().intValue())) {
                if (logger.isInfoEnabled()) {
                    logger.info(MessageFormat.format("龙门架分发消息registerNo={0},operateTime={1},consume={2},barcode={3}", domain.getRegisterNo(), domain.getScannerTime(), consume.getKey(), domain.getBarCode()));
                }
                result = consume.getValue().onMessage(domain, config);
            }

        }
        return result;
    }

    /**
     * 根据路由分拣中心获取批次号
     *
     * @param domain
     * @param config
     * @return
     */
    private String getSendCode(UploadData domain, GantryDeviceConfig config) {
        // 判断条码是箱号还是包裹号
        if (SerialRuleUtil.isMatchBoxCode(domain.getBarCode())) {
            if (logger.isInfoEnabled()) {
                logger.info(MessageFormat.format("龙门架自动发货判断货物类型registerNo={0},operateTime={1},barCode={2}|结果为箱子", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode()));
            }
            return getSendCodeWithBoxCode(domain, config);
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(MessageFormat.format("龙门架自动发货判断货物类型registerNo={0},operateTime={1},barCode={2}|结果为包裹", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode()));
            }
            // 包裹号
            return getSendCodeWithPackageCode(domain, config);
        }
    }

    /**
     * 箱号获取批次号
     *
     * @param domain
     * @param config
     * @return
     */
    private String getSendCodeWithBoxCode(UploadData domain, GantryDeviceConfig config) {
        Box box = boxService.findBoxByCode(domain.getBarCode());
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = null;
        if (box != null) {
            baseStaffSiteOrgDto = siteService.getSite(box.getReceiveSiteCode());
        } else {
            if (StringUtils.isNotEmpty(domain.getBarCode()) && domain.getBarCode().length() > 16) {
                // 截取条码10到16位为预分拣站点
                String siteCode = domain.getBarCode().substring(9, 16);
                baseStaffSiteOrgDto = baseService.queryDmsBaseSiteByCode(siteCode);
            }
        }

        if (baseStaffSiteOrgDto != null) {
            String sendCode;
            // 目的地为分拣中心
            if (baseStaffSiteOrgDto.getSubType() == Constants.BASE_SITE_DISTRIBUTION_CENTER) {
                sendCode = getSendCodeSortingCenter(baseStaffSiteOrgDto.getSiteCode(), domain.getScannerTime(), config);
                if (logger.isInfoEnabled()) {
                    logger.info(MessageFormat.format("龙门架自动发货,跨分拣,根据箱号获取批次号registerNo={0},operateTime={1},barCode={2}|批次号为{3}", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode(), sendCode));
                }
                return sendCode;
            }
            // 直发站点 直接生成批次号
            ScannerFrameBatchSend batchSend = scannerFrameBatchSendService.getAndGenerate(domain.getScannerTime(), box.getReceiveSiteCode(), config);
            if (logger.isInfoEnabled()) {
                logger.info(MessageFormat.format("龙门架自动发货,直发站点,根据箱号获取批次号registerNo={0},operateTime={1},barCode={2}|批次号为{3}", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode(), batchSend.getSendCode()));
            }
            return batchSend.getSendCode();
        }
        this.addGantryException(domain, config, 3);
        if (logger.isWarnEnabled()) {
            logger.warn(MessageFormat.format("龙门架自动发货,根据箱号获取批次号registerNo={0},operateTime={1},boxCode={2}|箱子信息查询结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode()));
        }
        return null;
    }

    /**
     * 包裹获取批次号
     *
     * @param domain
     * @param config
     * @return
     */
    private String getSendCodeWithPackageCode(UploadData domain, GantryDeviceConfig config) {
        // 获取运单号
        String waybillCode = SerialRuleUtil.getWaybillCode(domain.getBarCode());
        BigWaybillDto waybillDto = waybillService.getWaybill(waybillCode);
        if (waybillDto != null && waybillDto.getWaybill() != null) {
            // 获取运单信息
            Waybill waybill = waybillDto.getWaybill();
            // 预分拣站点
            Integer siteCode = waybill.getOldSiteId();
            if (siteCode != null && siteCode != 0) {
                // 判断是否为自提柜
                if (isZiTiGui(waybill)) {
                    // 获取自提柜所属站点编号
                    siteCode = baseService.getSiteSelfDBySiteCode(siteCode);
                }
                BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(siteCode);
                // 根据所属站点获取对应目的分拣中心
                Integer destSiteCode = baseStaffSiteOrgDto.getDmsId();
                // 判断当前分拣中心与目的分拣中心是否一致
                if (destSiteCode != config.getCreateSiteCode()) {
                    String sendCode = getSendCodeSortingCenter(baseStaffSiteOrgDto.getDmsId(), domain.getScannerTime(), config);
                    if (logger.isInfoEnabled()) {
                        logger.info(MessageFormat.format("龙门架自动发货,跨分拣,根据包裹号获取批次号registerNo={0},operateTime={1},barCode={2}|批次号为{3}", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode(), sendCode));
                    }
                    return sendCode;
                } else {
                    // 直发站点 直接生成批次号
                    ScannerFrameBatchSend batchSend = scannerFrameBatchSendService.getAndGenerate(domain.getScannerTime(), waybill.getOldSiteId(), config);
                    if (logger.isInfoEnabled()) {
                        logger.info(MessageFormat.format("龙门架自动发货,直发站点,根据包裹号获取批次号registerNo={0},operateTime={1},barCode={2}|批次号为{3}", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode(), batchSend.getSendCode()));
                    }
                    return batchSend.getSendCode();
                }
            }
            this.addGantryException(domain, config, 1);
            if (logger.isWarnEnabled()) {
                logger.warn(MessageFormat.format("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},waybillCode={2}|预分拣站点查询结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), waybillCode));
            }
        } else {
            this.addGantryException(domain, config, 2);
            if (logger.isWarnEnabled()) {
                logger.warn(MessageFormat.format("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},waybillCode={2}|运单信息查询结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), waybillCode));
            }
        }
        return null;
    }

    /**
     * 目的地为分拣中心生成批次
     *
     * @param destSiteCode 目的分拣中心
     * @param operateDate
     * @param config
     * @return
     */
    private String getSendCodeSortingCenter(Integer destSiteCode, Date operateDate, GantryDeviceConfig config) {
        // 获取批次路由分拣中心
        List<AreaDest> areaDestList = areaDestService.getList(config.getCreateSiteCode(), null, destSiteCode);
        // 是否存在路由
        if (areaDestList != null && areaDestList.size() > 0) {
            AreaDest areaDest = areaDestList.get(0);
            // 根据发货站点和中转站点获取批次号
            ScannerFrameBatchSend batchSend = scannerFrameBatchSendService.getAndGenerate(operateDate, areaDest.getTransferSiteCode(), config);
            return batchSend.getSendCode();
        }
        // 根据发货站点和目的站点获取批次号
        ScannerFrameBatchSend batchSend = scannerFrameBatchSendService.getAndGenerate(operateDate, destSiteCode, config);
        return batchSend.getSendCode();
    }

    /**
     * 判断自提柜类型
     *
     * @param waybill
     * @return
     */
    private Boolean isZiTiGui(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }
        if ('5' == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 龙门架自动发货异常信息存储
     *
     * @param domain
     * @param config
     * @param type   1：没有预分拣站点  2：没有运单信息 3：没有箱子信息
     */
    private void addGantryException(UploadData domain, GantryDeviceConfig config, int type) {
        try {
            Long machineId = Long.valueOf(config.getMachineId());
            String barCode = domain.getBarCode();
            if (machineId != null || StringUtils.isNotEmpty(barCode)) {
                GantryException gantryException = new GantryException();
                gantryException.setMachineId(machineId);
                gantryException.setBarCode(domain.getBarCode());
                gantryException.setCreateSiteCode(Long.valueOf(config.getCreateSiteCode()));
                gantryException.setCreateSiteName(config.getCreateSiteName());
                gantryException.setOperateTime(domain.getScannerTime());
                gantryException.setType(type);
                if (domain.getLength() != null && domain.getWidth() != null && domain.getHeight() != null) {
                    Float volume = domain.getLength() * domain.getWidth() * domain.getHeight();
                    gantryException.setVolume(Double.valueOf(volume));
                }
                gantryExceptionService.addGantryException(gantryException);
                return;
            }
            if (logger.isWarnEnabled()) {
                logger.warn(MessageFormat.format("龙门架自动发货,存储异常信息registerNo={0},operateTime={1},barCode={2},machineId={3}|存储异常信息失败", domain.getRegisterNo(), domain.getScannerTime(), barCode, machineId));
            }
        } catch (Exception e) {
            logger.error("龙门架自动发货存储异常信息失败", e);
        }
    }

}
