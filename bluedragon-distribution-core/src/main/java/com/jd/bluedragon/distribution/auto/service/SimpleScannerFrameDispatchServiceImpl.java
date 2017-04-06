package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlanDetail;
import com.jd.bluedragon.distribution.areadest.service.AreaDestPlanDetailService;
import com.jd.bluedragon.distribution.areadest.service.AreaDestService;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.RouteType;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.domain.BaseEntity;
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
import java.util.HashMap;
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
    private GantryDeviceService gantryDeviceService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseService baseService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private AreaDestService areaDestService;

    @Autowired
    private AreaDestPlanDetailService areaDestPlanDetailService;

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
    public boolean dispatch(UploadData domain) throws Exception {

        GantryDeviceConfig config = gantryDeviceConfigService.findGantryDeviceConfigByOperateTime(Integer.parseInt(domain.getRegisterNo()), domain.getScannerTime());
        this.printInfoLog("获取龙门架操作方式registerNo={0},operateTime={1}|结果{2}", domain.getRegisterNo(), domain.getScannerTime(), JsonHelper.toJson(config));
        if (null == config) {
            this.printWarnLog("获取龙门架操作方式registerNo={0},operateTime={1}|结果为NULL", domain.getRegisterNo(), domain.getScannerTime());
            return true;
        }
        boolean result = false;
        domain.setBarCode(StringUtils.remove(domain.getBarCode(), BOX_SUFFIX));/*龙门加校正箱号后面-CF*/

        Byte version = getVersion(config.getMachineId());
        config.setVersion(version);
        // 判断操作类型是否为发货并且龙门架为新设备
        if (version != null && version.intValue() == 1) {
            String sendCode = getSendCode(domain, config);
            if (StringUtils.isNotEmpty(sendCode)) {
                this.printInfoLog("龙门架自动发货,跨分拣,根据包裹号获取批次号registerNo={0},operateTime={1},barCode={2}|批次号为{3}", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode(), sendCode);
                config.setSendCode(sendCode);
            } else {
                this.printWarnLog("获取龙门架操作方式registerNo={0},operateTime={1}|获取批次号结果为NULL", domain.getRegisterNo(), domain.getScannerTime());
                return true;
            }
        }

        Iterator<Map.Entry<Integer, ScannerFrameConsume>> item = scannerFrameConsumeMap.entrySet().iterator();
        while (item.hasNext()) {
            Map.Entry<Integer, ScannerFrameConsume> consume = item.next();
            if (consume.getKey().intValue() == (config.getBusinessType().intValue() & consume.getKey().intValue())) {
                this.printInfoLog("龙门架分发消息registerNo={0},operateTime={1},consume={2},barcode={3}", domain.getRegisterNo(), domain.getScannerTime(), consume.getKey(), domain.getBarCode());
                result = consume.getValue().onMessage(domain, config);
            }
        }
        return result;
    }

    /**
     * 获取设备版本
     *
     * @param machineId
     * @return
     */
    private Byte getVersion(Integer machineId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("machineId", machineId);
        List<GantryDevice> gantryDevice = gantryDeviceService.getGantry(param);
        if (gantryDevice != null && gantryDevice.size() > 0) {
            return gantryDevice.get(0).getVersion();
        }
        return null;
    }

    /**
     * 输出Warn级别日志信息
     *
     * @param pattern
     * @param arguments
     */
    private void printWarnLog(String pattern, Object... arguments) {
        if (logger.isWarnEnabled()) {
            logger.warn(MessageFormat.format(pattern, arguments));
        }
    }

    /**
     * 输出info级别日志信息
     *
     * @param pattern
     * @param arguments
     */
    private void printInfoLog(String pattern, Object... arguments) {
        if (logger.isInfoEnabled()) {
            logger.info(MessageFormat.format(pattern, arguments));
        }
    }

    /**
     * 根据路由分拣中心获取批次号
     *
     * @param domain
     * @param config
     * @return
     */
    private String getSendCode(UploadData domain, GantryDeviceConfig config) throws Exception {
        // 判断条码是箱号还是包裹号
        if (SerialRuleUtil.isMatchBoxCode(domain.getBarCode())) {
            // 箱号
            this.printInfoLog("龙门架自动发货判断货物类型registerNo={0},operateTime={1},barCode={2}|结果为箱子", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
            return getSendCodeWithBoxCode(domain, config);
        } else {
            // 包裹号
            this.printInfoLog("龙门架自动发货判断货物类型registerNo={0},operateTime={1},barCode={2}|结果为包裹", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
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
    private String getSendCodeWithBoxCode(UploadData domain, GantryDeviceConfig config) throws Exception {
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
            AreaDestPlanDetail detail = areaDestPlanDetailService.getByScannerTime(config.getMachineId(), config.getCreateSiteCode(), domain.getScannerTime());
            if (detail != null && detail.getPlanId() != null) {
                String sendCode = getSendCodeBySiteCode(detail.getPlanId(), baseStaffSiteOrgDto.getSiteCode(), domain, config);
                if (StringUtils.isNotEmpty(sendCode)) {
                    this.printInfoLog("龙门架自动发货,直发站点,根据箱号获取批次号registerNo={0},operateTime={1},barCode={2}|批次号为{3}", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode(), sendCode);
                } else {
                    this.addGantryException(domain, config, 5, sendCode);
                }
                return sendCode;
            } else {
                this.printInfoLog("龙门架自动发货,直发站点,根据箱号获取批次号registerNo={0},operateTime={1},barCode={2}|获取龙门架发货线路关系方案操作记录异常", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
                throw new Exception(MessageFormat.format("龙门架自动发货获取发货方案为null，龙门架编号为{0}，操作站点编号为{1}，龙门扫描时间为{2}", config.getMachineId(), config.getCreateSiteCode(), domain.getScannerTime()));
            }
        }
        this.addGantryException(domain, config, 3, null);
        this.printWarnLog("龙门架自动发货,根据箱号获取批次号registerNo={0},operateTime={1},boxCode={2}|箱子信息查询结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
        return null;
    }

    /**
     * 包裹获取批次号
     *
     * @param domain
     * @param config
     * @return
     */
    private String getSendCodeWithPackageCode(UploadData domain, GantryDeviceConfig config) throws Exception {
        // 获取运单号
        String waybillCode = SerialRuleUtil.getWaybillCode(domain.getBarCode());
        // 判断是否为拦截订单
        if (WaybillCancelClient.isWaybillCancel(waybillCode)) {
            this.addGantryException(domain, config, 4, null);
            this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},waybillCode={2}|拦截订单，取消发货", domain.getRegisterNo(), domain.getScannerTime(), waybillCode);
        } else {
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
            if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
                // 获取运单信息
                Waybill waybill = baseEntity.getData().getWaybill();
                // 预分拣站点
                Integer siteCode = waybill.getOldSiteId();
                if (siteCode != null && siteCode.intValue() != 0) {
                    // 判断是否为自提柜
                    if (isZiTiGui(waybill)) {
                        // 获取自提柜所属站点编号
                        siteCode = baseService.getSiteSelfDBySiteCode(siteCode);
                    }
                    return doGetSendCode(siteCode, domain, config);
                } else {
                    this.addGantryException(domain, config, 1, null);
                    this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},waybillCode={2}|预分拣站点查询结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), waybillCode);
                }
            } else {
                this.addGantryException(domain, config, 2, null);
                this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},waybillCode={2}|运单信息查询结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), waybillCode);
            }
        }
        return null;
    }

    /**
     * 根据目的站点获取批次号
     *
     * @param destSiteCode
     * @param domain
     * @param config
     * @return
     */
    private String doGetSendCode(Integer destSiteCode, UploadData domain, GantryDeviceConfig config) throws Exception {
        AreaDestPlanDetail detail = areaDestPlanDetailService.getByScannerTime(config.getMachineId(), config.getCreateSiteCode(), domain.getScannerTime());
        if (detail != null) {
            Integer planId = detail.getPlanId();
            if (planId != null && planId > 0) {
                String sendCode = getSendCodeBySiteCode(planId, destSiteCode, domain, config);
                if (StringUtils.isNotEmpty(sendCode)) {
                    // 有效批次号 直接返回
                    return sendCode;
                } else {
                    // 根据所属站点获取对应目的分拣中心
                    BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(destSiteCode);
                    Integer dmsId = baseStaffSiteOrgDto.getDmsId();
                    if (dmsId != null && dmsId > 0) {
                        sendCode = getSendCodeBySiteCode(detail.getPlanId(), baseStaffSiteOrgDto.getDmsId(), domain, config);
                        if (StringUtils.isNotEmpty(sendCode)) {
                            // 有效批次号 直接返回
                            return sendCode;
                        } else {
                            this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},destSiteCode={2}|龙门架未绑该站点", domain.getRegisterNo(), domain.getScannerTime(), destSiteCode);
                            this.addGantryException(domain, config, 5, null);
                        }
                    } else {
                        this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},destSiteCode={2}|获取站点对应分拣中心结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), destSiteCode);
                    }
                }
            }
        } else {
            this.printInfoLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},barCode={2}|获取龙门架发货线路关系方案操作记录异常，结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
            throw new Exception(MessageFormat.format("龙门架自动发货获取发货方案为null，龙门架编号为{0}，操作站点编号为{1}，龙门扫描时间为{2}", config.getMachineId(), config.getCreateSiteCode(), domain.getScannerTime()));
        }
        return null;
    }

    private String getSendCodeBySiteCode(Integer planId, Integer siteCode, UploadData domain, GantryDeviceConfig config) {
        List<AreaDest> areaDestList = areaDestService.getList(planId, config.getCreateSiteCode(), siteCode);
        // 是否存在路由
        if (areaDestList != null && areaDestList.size() > 0) {
            AreaDest areaDest = areaDestList.get(0);
            switch (RouteType.getEnum(areaDest.getRouteType())) {
                case DIRECT_SITE:
                    break;
                case DIRECT_DMS:
                    siteCode = areaDest.getTransferSiteCode();
                    break;
                case MULTIPLE_DMS:
                    Integer transferSiteCode = areaDest.getTransferSiteCode();
                    if (transferSiteCode != null && transferSiteCode > 0) {
                        siteCode = transferSiteCode;
                    }
            }
            // 根据发货站点和目的站点获取批次号
            return scannerFrameBatchSendService.getAndGenerate(domain.getScannerTime(), siteCode, config).getSendCode();
        }
        return null;
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
     * @param type   1：没有预分拣站点  2：没有运单信息 3：没有箱子信息 4：订单拦截 5：龙门架未绑该站点
     */
    private void addGantryException(UploadData domain, GantryDeviceConfig config, int type, String sendCode) {
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
            gantryException.setSendCode(sendCode);
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
    }

}
