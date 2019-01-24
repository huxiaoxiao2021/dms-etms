package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlanDetail;
import com.jd.bluedragon.distribution.areadest.service.AreaDestPlanDetailService;
import com.jd.bluedragon.distribution.areadest.service.AreaDestService;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.*;
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
import java.util.*;

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

    @Autowired
    WaybillService waybillService;

    @Autowired
    SysConfigService sysConfigService;

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
        GantryDeviceConfig config = null;
        boolean result = false;
        if (domain.getSource() != null && domain.getSource().intValue() == 2) {
            config = new GantryDeviceConfig();
            if (!this.getSortMachineAutoSendConfig(domain, config)) {
                return true;
            }
        } else {
            config = gantryDeviceConfigService.findGantryDeviceConfigByOperateTime(Integer.valueOf(domain.getRegisterNo()), domain.getScannerTime());
            this.printInfoLog("获取龙门架操作方式registerNo={0},operateTime={1}|结果{2}", domain.getRegisterNo(), domain.getScannerTime(), JsonHelper.toJson(config));
            if (null == config) {
                this.printWarnLog("获取龙门架操作方式registerNo={0},operateTime={1}|结果为NULL", domain.getRegisterNo(), domain.getScannerTime());
                return true;
            }
            domain.setBarCode(StringUtils.remove(domain.getBarCode(), BOX_SUFFIX));/*龙门加校正箱号后面-CF*/

            Byte version = getVersion(Integer.valueOf(config.getMachineId()));
            config.setVersion(version);
            /* 判断龙门架版本 */
            if (version != null && version.intValue() == 1) {
                // 多批次发货龙门架
                if (!this.doGetSendCode(domain, config)) {
                    return true;
                }
            } else {
                // 龙门架老版本超过24小时换批次逻辑
                Calendar now = Calendar.getInstance();
                now.setTime(config.getStartTime());
                now.add(Calendar.HOUR, 25);
                Date endTime = now.getTime();
                if (endTime.before(domain.getScannerTime())) {/*如果一天后，则进行自动切换批次号*/
                    String sendCode = new StringBuilder()
                            .append(SerialRuleUtil.getCreateSiteCodeFromSendCode(config.getSendCode()))
                            .append("-")
                            .append(SerialRuleUtil.getReceiveSiteCodeFromSendCode(config.getSendCode()))
                            .append("-")
                            .append(DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS)).toString();
                    GantryDeviceConfig model = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(Integer.valueOf(config.getMachineId()));
                    model.setSendCode(sendCode);
                    model.setStartTime(new Date(domain.getScannerTime().getTime() - 1000));
                    model.setEndTime(new Date(model.getStartTime().getTime() + 1000 * 60 * 60 * 24));
                    gantryDeviceConfigService.addUseJavaTime(model);
                    config.setSendCode(sendCode);
                }
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
     * 构建分拣机自动发货任务配置信息
     *
     * @param domain
     * @return
     */
    private boolean getSortMachineAutoSendConfig(UploadData domain, GantryDeviceConfig config) {
        String barCode = domain.getBarCode();
        if (!BusinessUtil.isBoxcode(barCode)) {
            // 获取运单号
            String waybillCode = SerialRuleUtil.getWaybillCode(barCode);
            // 判断是否为拦截订单
            if (WaybillCancelClient.isWaybillCancel(waybillCode)) {
                this.addGantryException(domain, config, 23, null);
                return false;
            }
        }
        // 业务类型-发货
        config.setBusinessType(2);
        // 多批次发货版本
        config.setVersion((byte) 1);
        config.setMachineId(domain.getRegisterNo());
        config.setCreateSiteCode(domain.getDistributeId());
        BaseStaffSiteOrgDto site = siteService.getSite(domain.getDistributeId());
        if (null != site) {
            config.setCreateSiteName(site.getSiteName());
        } else {
            this.addGantryException(domain, config, 21, null);
            return false;
        }
        if (domain.getSendSiteCode() == null || domain.getSendSiteCode() <= 0) {
            this.addGantryException(domain, config, 22, null);
            return false;
        }
        config.setOperateUserId(domain.getOperatorId());
        config.setOperateUserName(domain.getOperatorName());
        if (domain.getScannerTime() == null) {
            this.addGantryException(domain, config, 24, null);
            return false;
        }

        /*
            获取sysConfig中CONFIG_NAME 为 "sortMachine.autoSend.sendCode.auto.change" 的CONFIG_CONTENT，
            其中CONFIG_CONTENT中维护的是开启分拣机自动发货的封车自动换批次的分拣中心所属的分拣机代码
         */
        String sendCode = "";
        SysConfig sysConfig = sysConfigService.findConfigContentByConfigName
                (Constants.SYS_CONFIG_SORT_MACHINE_AUTO_CHANGE_SEND_CODE);
        if (null != sysConfig && StringHelper.isNotEmpty(sysConfig.getConfigContent())
                && Arrays.asList(sysConfig.getConfigContent().split(",")).contains(domain.getRegisterNo())) {
            //开关为开启状态
            sendCode = scannerFrameBatchSendService.getOrGenerate
                    (domain.getScannerTime(), domain.getSendSiteCode(), config, domain.getPackageCode()).getSendCode();
        } else {
            //开关没有开启
            sendCode = scannerFrameBatchSendService.getAndGenerate
                    (domain.getScannerTime(), domain.getSendSiteCode(), config).getSendCode();
        }

        config.setSendCode(sendCode);
        return true;
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
    private boolean doGetSendCode(UploadData domain, GantryDeviceConfig config) throws Exception {
        // 判断条码是箱号还是包裹号
        if (BusinessUtil.isBoxcode(domain.getBarCode())) {
            // 箱号
            this.printInfoLog("龙门架自动发货判断货物类型registerNo={0},operateTime={1},barCode={2}|结果为箱子", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
            // 获取目的站点
            Integer destSiteCode = this.getSiteCodeWithBoxCode(domain, config);
            if (destSiteCode != null) {
                // 判断是否为发货
                if ((config.getBusinessType() & 2) == 2) {
                    // 获取批次号
                    return getSendCodeByBox(domain, config, destSiteCode);
                }
                return true;
            }
        } else {
            // 包裹号
            this.printInfoLog("龙门架自动发货判断货物类型registerNo={0},operateTime={1},barCode={2}|结果为包裹", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
            // 获取目的站点
            Integer destSiteCode = this.getSiteCodeWithPackageCode(domain, config);
            if (destSiteCode != null) {
                // 判断是否为发货
                if ((config.getBusinessType() & 2) == 2) {
                    // 获取批次号
                    return this.getSendCodeByPackage(domain, config, destSiteCode);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 根据箱号获取目的站点编号
     *
     * @param domain
     * @param config
     * @return
     */
    private Integer getSiteCodeWithBoxCode(UploadData domain, GantryDeviceConfig config) throws Exception {
        Box box = boxService.findBoxByCode(domain.getBarCode());
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = null;
        if (box != null) {
            baseStaffSiteOrgDto = siteService.getSite(box.getReceiveSiteCode());
        }
        if (baseStaffSiteOrgDto != null && baseStaffSiteOrgDto.getSiteCode() != null && baseStaffSiteOrgDto.getSiteCode() > 0) {
            return baseStaffSiteOrgDto.getSiteCode();
        } else {
            this.addGantryException(domain, config, 3, null);
            this.printWarnLog("龙门架自动发货,根据箱号获取批次号registerNo={0},operateTime={1},boxCode={2}|箱子信息查询结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
        }
        return null;
    }

    /**
     * 箱子(集包)根据目的站点和操作站点获取批次号
     *
     * @param domain
     * @param config
     * @param destSiteCode
     * @return
     * @throws Exception
     */
    private boolean getSendCodeByBox(UploadData domain, GantryDeviceConfig config, Integer destSiteCode) throws Exception {
        boolean isSuccess = false;
        AreaDestPlanDetail detail = areaDestPlanDetailService.getByScannerTime(Integer.valueOf(config.getMachineId()), config.getCreateSiteCode(), domain.getScannerTime());
        if (detail != null && detail.getPlanId() != null) {
            String sendCode = getSendCodeBySiteCode(detail.getPlanId(), destSiteCode, domain, config);
            if (StringUtils.isNotEmpty(sendCode)) {
                isSuccess = true;
                config.setSendCode(sendCode);
                this.printInfoLog("龙门架自动发货,直发站点,根据箱号获取批次号registerNo={0},operateTime={1},barCode={2}|批次号为{3}", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode(), sendCode);
            } else {
                this.addGantryException(domain, config, 5, sendCode);
            }
        } else {
            this.printInfoLog("龙门架自动发货,直发站点,根据箱号获取批次号registerNo={0},operateTime={1},barCode={2}|获取龙门架发货线路关系方案操作记录异常", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
            this.addGantryException(domain, config, 6, null);
        }
        return isSuccess;
    }

    /**
     * 包裹获取目的站点编号
     *
     * @param domain
     * @param config
     * @return
     */
    private Integer getSiteCodeWithPackageCode(UploadData domain, GantryDeviceConfig config) throws Exception {
        // 获取运单号
        String waybillCode = SerialRuleUtil.getWaybillCode(domain.getBarCode());
        // 判断是否为拦截订单  对于拦截订单的的处理去掉
//        if (WaybillCancelClient.isWaybillCancel(waybillCode)) {
//            this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},waybillCode={2}|拦截订单，取消发货", domain.getRegisterNo(), domain.getScannerTime(), waybillCode);
//            this.addGantryException(domain, config, 4, null);
//        } else {
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
            if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
                // 获取运单信息
                Waybill waybill = baseEntity.getData().getWaybill();
                // 预分拣站点
                Integer siteCode = waybill.getOldSiteId();
                if (siteCode != null && siteCode.intValue() != 0) {
                    // 判断运单是不是(自提柜,便民自提,合作代收)，如果是，则将siteCode设置为归属站点
                    if (isZiTiGui(waybill) || isBianMinZiTi(waybill) || isHeZuoDaiShou(waybill)) {
                        // 获取自提柜所属站点编号
                        siteCode = baseService.getSiteSelfDBySiteCode(siteCode);
                    }
                    return siteCode;
                } else {
                    this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},waybillCode={2}|预分拣站点查询结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), waybillCode);
                    this.addGantryException(domain, config, 1, null);
                }
            } else {
                this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},waybillCode={2}|运单信息查询结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), waybillCode);
                this.addGantryException(domain, config, 2, null);
            }
//        }
        return null;
    }

    /**
     * 包裹，根据目的站点和操作站点获取批次号
     *
     * @param destSiteCode
     * @param domain
     * @param config
     * @return
     */
    private boolean getSendCodeByPackage(UploadData domain, GantryDeviceConfig config, Integer destSiteCode) throws Exception {
        boolean isSuccess = false;
        AreaDestPlanDetail planDetail = areaDestPlanDetailService.getByScannerTime(Integer.valueOf(config.getMachineId()), config.getCreateSiteCode(), domain.getScannerTime());
        if (planDetail != null && planDetail.getPlanId() != null && planDetail.getPlanId() > 0) {
            String sendCode = getSendCodeBySiteCode(planDetail.getPlanId(), destSiteCode, domain, config);
            if (StringUtils.isNotEmpty(sendCode)) {
                // 有效批次号 直接返回
                isSuccess = true;
                config.setSendCode(sendCode);
                this.printInfoLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},barCode={2}|批次号为{3}", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode(), sendCode);
            } else {
                // 根据所属站点获取对应目的分拣中心
                BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(destSiteCode);
                if (baseStaffSiteOrgDto != null && baseStaffSiteOrgDto.getDmsId() != null && baseStaffSiteOrgDto.getDmsId() > 0) {
                    sendCode = getSendCodeBySiteCode(planDetail.getPlanId(), baseStaffSiteOrgDto.getDmsId(), domain, config);
                    if (StringUtils.isNotEmpty(sendCode)) {
                        // 有效批次号 直接返回
                        isSuccess = true;
                        config.setSendCode(sendCode);
                        this.printInfoLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},barCode={2}|批次号为{3}", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode(), sendCode);
                    } else {
                        this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},destSiteCode={2}|龙门架未绑该站点", domain.getRegisterNo(), domain.getScannerTime(), destSiteCode);
                        this.addGantryException(domain, config, 5, null);
                    }
                } else {
                    this.printWarnLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},destSiteCode={2}|获取站点对应分拣中心结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), destSiteCode);
                    this.addGantryException(domain, config, 1, null);
                }
            }
        } else {
            this.printInfoLog("龙门架自动发货,根据包裹号获取批次号registerNo={0},operateTime={1},barCode={2}|获取龙门架发货线路关系方案操作记录异常，结果为NULL", domain.getRegisterNo(), domain.getScannerTime(), domain.getBarCode());
            this.addGantryException(domain, config, 6, null);
        }
        return isSuccess;
    }

    /**
     * 根据方案编号、目的站点等信息获取批次号
     *
     * @param planId
     * @param siteCode
     * @param domain
     * @param config
     * @return
     */
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
                    break;
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
     * 便民自提判断 【sendpay 第22位等于6(合作自提柜 )】
     */
    private static Boolean isBianMinZiTi(Waybill waybill) {
        if (waybill == null || StringUtils.isBlank(waybill.getSendPay()) || waybill.getSendPay().length() < 64) {
            return Boolean.FALSE;
        }
        if ('6' == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 便民自提判断 【sendpay 7的订单(合作代收点)】
     */
    private static Boolean isHeZuoDaiShou(Waybill waybill) {
        if (waybill == null || StringUtils.isBlank(waybill.getSendPay()) || waybill.getSendPay().length() < 64) {
            return Boolean.FALSE;
        }
        if ('7' == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 龙门架自动发货异常信息存储
     *
     * @param domain
     * @param config
     * @param type   1：无预分拣站点  2：无运单信息 3：无箱子信息 4：订单拦截 5：龙门架未绑该站点 6：无启用方案信息
     *               21：发货始发地站点无效 22：无发货目的地站点 23：订单拦截 24：无落格时间
     */
    private void addGantryException(UploadData domain, GantryDeviceConfig config, int type, String sendCode) {
        String machineId = config.getMachineId();
        String barCode = domain.getBarCode();
        if (StringUtils.isNotEmpty(machineId) && StringUtils.isNotEmpty(barCode)) {
            GantryException gantryException = new GantryException();
            gantryException.setMachineId(String.valueOf(machineId));
            gantryException.setBarCode(barCode);
            if (!BusinessUtil.isBoxcode(barCode)) {
                gantryException.setPackageCode(barCode);
                gantryException.setWaybillCode(SerialRuleUtil.getWaybillCode(barCode));
            }
            gantryException.setCreateSiteCode(Long.valueOf(config.getCreateSiteCode()));
            gantryException.setCreateSiteName(config.getCreateSiteName());
            gantryException.setOperateTime(domain.getScannerTime());
            gantryException.setType(type);
            gantryException.setSendCode(sendCode);
            gantryException.setChuteCode(domain.getChuteCode());
            if (domain.getSource() != null && domain.getSource() == 2) {
                WaybillPackageDTO waybillPackageDTO = waybillService.getWaybillPackage(barCode);
                if (waybillPackageDTO != null) {
                    gantryException.setVolume(waybillPackageDTO.getVolume() == 0 ? waybillPackageDTO.getOriginalVolume() : waybillPackageDTO.getVolume());
                }
            } else {
                if (domain.getLength() != null && domain.getWidth() != null && domain.getHeight() != null) {
                    Float volume = domain.getLength() * domain.getWidth() * domain.getHeight();
                    gantryException.setVolume(Double.valueOf(volume));
                }
            }

            gantryExceptionService.addGantryException(gantryException);
            return;
        }
        if (logger.isWarnEnabled()) {
            logger.warn(MessageFormat.format("龙门架自动发货,存储异常信息registerNo={0},operateTime={1},barCode={2},machineId={3}|存储异常信息失败", domain.getRegisterNo(), domain.getScannerTime(), barCode, machineId));
        }
    }

}
