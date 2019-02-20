package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.EclpItemManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.dao.AbnormalUnknownWaybillDao;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybillCondition;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybillRequest;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.domain.AreaNode;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.web.LoginContext;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wuyoude
 * @ClassName: AbnormalUnknownWaybillServiceImpl
 * @Description: 三无订单申请--Service接口实现
 * @date 2018年05月08日 15:16:15
 */
@Service("abnormalUnknownWaybillService")
public class AbnormalUnknownWaybillServiceImpl extends BaseService<AbnormalUnknownWaybill> implements AbnormalUnknownWaybillService {

    @Autowired
    BaseMinorManager baseMinorManager;
    @Autowired
    @Qualifier("abnormalUnknownWaybillDao")
    private AbnormalUnknownWaybillDao abnormalUnknownWaybillDao;
    @Autowired
    private WaybillService waybillService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private EclpItemManager eclpItemManager;
    @Autowired
    @Qualifier("abnormalUnknownSendProducer")
    private DefaultJMQProducer abnormalEclpSendProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public Dao<AbnormalUnknownWaybill> getDao() {
        return this.abnormalUnknownWaybillDao;
    }

    /**
     * 查询并上报
     *
     * @return
     */
    public JdResponse<String> queryAndReport(AbnormalUnknownWaybill request, LoginUser loginUser) {
        JdResponse<String> rest = new JdResponse<String>();
        rest.setMessage("操作成功！");

        if (request.getWaybillCode() == null || StringUtils.isBlank(request.getWaybillCode())) {
            rest.toFail("运单号不能为空");
            return rest;
        }
        //传入的运单号处理
        String[] waybillcodes = StringUtils.trim(request.getWaybillCode()).split(AbnormalUnknownWaybill.SEPARATOR_SPLIT);
        List<String> waybillList = Lists.newArrayList();
        //不合法的运单号
        StringBuilder notWaybillCodes = new StringBuilder();
        //不存在的运单号集合
        StringBuilder noExistsWaybills = new StringBuilder();
        //缓存运单信息
        Map<String, BigWaybillDto> bigWaybillDtoMap = Maps.newHashMap();
        //没商家
        StringBuilder noTraderWaybills = new StringBuilder();
        //返回给前台的运单号字符串
        StringBuilder newWaybillCodes = new StringBuilder();
        //去重运单号/包裹号
        Set<String> newWaybillCodesSet = new HashSet<String>();

        //妥投的运单号字符串
        StringBuilder deliveredWaybillCodes = new StringBuilder();
        //妥投的运单号/包裹号
        Set<String> deliveredWaybillCodesSet = new HashSet<String>();
        for(String waybillCodeInput : waybillcodes){
            if(WaybillUtil.isPackageCode(waybillCodeInput)){
                waybillCodeInput = WaybillUtil.getWaybillCode(waybillCodeInput);
            }
            newWaybillCodesSet.add(waybillCodeInput);
        }
        int count = 0;
        for (String waybillCodeInput : newWaybillCodesSet) {
            count ++;
            if (StringUtils.isBlank(waybillCodeInput)) {
                continue;
            }
            if(count==newWaybillCodesSet.size()){
                newWaybillCodes.append(WaybillUtil.getWaybillCode(waybillCodeInput));
            }else{
                newWaybillCodes.append(WaybillUtil.getWaybillCode(waybillCodeInput)).append(",");
            }
            String waybillCode = waybillCodeInput.trim();
            //解析包裹号生成运单号
            if(WaybillUtil.isPackageCode(waybillCode)){
                waybillCode = WaybillUtil.getWaybillCode(waybillCode);
            }
            if (WaybillUtil.isWaybillCode(waybillCode)) {
                BigWaybillDto bigWaybillDto = waybillService.getWaybillProductAndState(waybillCode);

                if (bigWaybillDto != null && bigWaybillDto.getWaybill() != null) {
                    //暂存起来
                    bigWaybillDtoMap.put(waybillCode, bigWaybillDto);
                    //如果要上报
                    // 必须有商家
                    if (StringUtils.isEmpty(bigWaybillDto.getWaybill().getBusiName())) {
                        noTraderWaybills.append(waybillCode).append(AbnormalUnknownWaybill.SEPARATOR_APPEND);
                    } else {
                        waybillList.add(waybillCode);
                    }
                    if (AbnormalUnknownWaybill.REPORT_YES.equals(request.getIsReport())) {
                        //判断运单的妥投状态
                        if (bigWaybillDto.getWaybillState() != null && bigWaybillDto.getWaybillState() != null) {
                            WaybillManageDomain waybillManageDomain = bigWaybillDto.getWaybillState();
                            //判断运单是否妥投
                            if (Constants.WAYBILL_DELIVERED_CODE.equals(waybillManageDomain.getWaybillState())) {
                                deliveredWaybillCodesSet.add(waybillCode);
                                if (deliveredWaybillCodesSet.size() == 1) {
                                    deliveredWaybillCodes.append(waybillCode);
                                }
                                else if (deliveredWaybillCodesSet.size() < AbnormalUnknownWaybill.DELIVERED_WAYBILL_NOTICE_MAX_COUNT) {
                                    deliveredWaybillCodes.append(AbnormalUnknownWaybill.SEPARATOR_APPEND);
                                    if (deliveredWaybillCodesSet.size() % 3 == 1) {
                                        deliveredWaybillCodes.append("\n");
                                    }
                                    deliveredWaybillCodes.append(waybillCode);
                                }
                                else if (deliveredWaybillCodesSet.size() == AbnormalUnknownWaybill.DELIVERED_WAYBILL_NOTICE_MAX_COUNT) {
                                    deliveredWaybillCodes.append("等");
                                }
                            }
                        }
                    }
                } else {
                    noExistsWaybills.append(waybillCode).append(AbnormalUnknownWaybill.SEPARATOR_APPEND);
                }
            } else {
                notWaybillCodes.append(waybillCode).append(AbnormalUnknownWaybill.SEPARATOR_APPEND);
            }
        }
        if (notWaybillCodes.length() > 0) {
            rest.toFail("以下运单号/包裹号不合法:" + notWaybillCodes + "请检查！");
            logger.warn("以下运单号/包裹号不合法:" + notWaybillCodes + "请检查！");
            return rest;
        }
        if (noTraderWaybills.length() > 0) {
            rest.toFail("以下运单号/包裹号未找到商家:" + noTraderWaybills + "请检查！");
            logger.warn("以下运单号/包裹号未找到商家" + noTraderWaybills + "请检查！");
            return rest;
        }
        if (noExistsWaybills.length() > 0) {
            rest.toFail("以下运单号/包裹号不存在:" + noExistsWaybills + "请检查！");
            logger.warn("以下运单号/包裹号不存在:" + noExistsWaybills + "请检查！");
            return rest;
        }
        if (waybillList.size() == 0) {
            rest.toFail("无可用运单号/包裹号");
            return rest;
        }

        //查出哪些已经有明细了或发过请求了 下面就不查了
        List<String> hasDetailWaybillCodes = abnormalUnknownWaybillDao.queryHasDetailWaybillCode(waybillList);
        //过滤掉 已经查过的
        Set<String> noDetails = new HashSet<String>(waybillList);//前台传过来的运单
        if (hasDetailWaybillCodes != null && hasDetailWaybillCodes.size() > 0) {
            for (String hasDetailWaybillCode : hasDetailWaybillCodes) {
                noDetails.remove(hasDetailWaybillCode);//去掉查过的
            }
        }
        //确实有没明细的运单，下面开始处理
        if (noDetails.size() > 0) {
            //批量添加用
            List<AbnormalUnknownWaybill> addList = Lists.newArrayList();
            //获取用户信息
            LoginContext loginContext = LoginContext.getLoginContext();
            BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(loginUser.getUserErp());
            //站点区域查出来
            BaseStaffSiteOrgDto org = baseMajorManager.getBaseSiteBySiteId(userDto.getSiteCode());
            if (org == null) {
                rest.toFail("所在站点未找到：" + userDto.getSiteName());
                logger.warn("所在站点未找到：" + userDto.getSiteName());
                return rest;
            }
            ProvinceNode province = AreaHelper.getProvince(Integer.parseInt(Long.valueOf(org.getProvinceId()).toString()));
            if (province == null) {
                rest.toFail("站点所在省份获取失败：" + org.getProvinceId());
                logger.warn("站点所在省份获取失败：" + org.getProvinceId());
                return rest;
            }
            AreaNode areaNode = AreaHelper.getAreaByProvinceId(province.getId());
            if (areaNode == null) {
                rest.toFail("站点所在区域获取失败：" + province.getId());
                logger.warn("站点所在区域获取失败：" + province.getId());
                return rest;
            }
            for (String waybillCode : noDetails) {
                getGoodDetails(bigWaybillDtoMap, hasDetailWaybillCodes, addList, userDto, areaNode, waybillCode, request, deliveredWaybillCodesSet);
            }
            if (addList.size() > 0) {
                abnormalUnknownWaybillDao.batchInsert(addList);
            }
        }

        if (newWaybillCodesSet != null && newWaybillCodesSet.size() > 0) {
            rest.setData(newWaybillCodes.toString());
        } else {
            rest.setData(null);
        }

        //如果存在妥投订单，需要提示已妥投，然后页面再进行跳转
        if (deliveredWaybillCodes.length() > 0) {
            rest.setMessage("以下运单号已妥投，只能查询不能上报：" + deliveredWaybillCodes.toString());
        }
        return rest;
    }

    private void getGoodDetails(Map<String, BigWaybillDto> bigWaybillDtoMap, List<String> hasDetailWaybillCodes, List<AbnormalUnknownWaybill> addList, BaseStaffSiteOrgDto userDto, AreaNode areaNode, String waybillCode, AbnormalUnknownWaybill request, Set<String> deliveredWaybillCodesSet) {
        //商品明细拼接使用
        StringBuilder waybillDetail = new StringBuilder();
        BigWaybillDto bigWaybillDto = bigWaybillDtoMap.get(waybillCode);
        //创建实体类
        AbnormalUnknownWaybill abnormalUnknownWaybill = buildAbnormalUnknownWaybillFactory(userDto, waybillCode, areaNode, bigWaybillDto.getWaybill());
        //第一步 查运单
        //如果运单有商品信息
        if (bigWaybillDto != null && bigWaybillDto.getGoodsList() != null && bigWaybillDto.getGoodsList().size() > 0) {
            buildWaybillDetails(abnormalUnknownWaybill, waybillDetail, bigWaybillDto.getGoodsList());
            addList.add(abnormalUnknownWaybill);//后面将插入表中
            hasDetailWaybillCodes.add(waybillCode);//前台用
            logger.info(waybillCode + "三无托寄物核实，运单查到了");
            return;
        }
        //第二步 查eclp
        //如果运单上没有明细 就判断是不是eclp订单 如果是，调用eclp接口
        String busiOrderCode = bigWaybillDto.getWaybill().getBusiOrderCode();
        if (WaybillUtil.isECLPByBusiOrderCode(busiOrderCode)) {
            List<ItemInfo> itemInfos = eclpItemManager.getltemBySoNo(busiOrderCode);
            if (itemInfos != null && itemInfos.size() > 0) {
                queryEclpDetails(itemInfos, abnormalUnknownWaybill, waybillDetail);
                addList.add(abnormalUnknownWaybill);//后面将插入表中
                hasDetailWaybillCodes.add(waybillCode);//前台用
                logger.info(waybillCode + "三无托寄物核实，eclp查到了");
                return;
            }
            logger.info(waybillCode + "三无托寄物核实，eclp没查到");
        } else {
            logger.info(waybillCode + "不是eclp运单");
        }
        //第三步 查运单的托寄物
//        BaseEntity<BigWaybillDto> entity = waybillQueryManager.getDataByChoice(waybillCode, true, true, false, false);
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(Boolean.TRUE);
        wChoice.setQueryWaybillExtend(Boolean.TRUE);
        BaseEntity<BigWaybillDto> entity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
        if(entity != null && entity.getData() != null && entity.getData().getWaybill() != null){
            Waybill waybill = entity.getData().getWaybill();
            if(waybill.getWaybillExt() != null &&
                    waybill.getWaybillExt().getConsignWare() != null) {
                buildWaybillDetailsByConsignWare(abnormalUnknownWaybill, waybillDetail, waybill.getWaybillExt());
                addList.add(abnormalUnknownWaybill);//后面将插入表中
                hasDetailWaybillCodes.add(waybillCode);//前台用
                logger.info(waybillCode + "三无托寄物核实，运单查到了");
                return;
            }
        }
        logger.info(waybillCode + "三无托寄物核实，运单没查到");
        //第三步 发B商家请求
        //查询运单
        if (AbnormalUnknownWaybill.REPORT_YES.equals(request.getIsReport())) {
            //是否是妥投订单
            if (deliveredWaybillCodesSet.contains(waybillCode)) {
                logger.info(waybillCode + "为妥投订单，无需上报！");
            } else {
                queryDetailForB(waybillCode, abnormalUnknownWaybill, AbnormalUnknownWaybill.ORDERNUMBER_1, bigWaybillDto.getWaybill());
                addList.add(abnormalUnknownWaybill);//后面将插入表中
                hasDetailWaybillCodes.add(waybillCode);//前台用
            }
        }


    }

    /**
     * 向B商家端发请求
     *
     * @param waybillCode
     * @param abnormalUnknownWaybill1Report
     * @param times
     * @return
     */
    private boolean queryDetailForB(String waybillCode, AbnormalUnknownWaybill abnormalUnknownWaybill1Report, Integer times, Waybill waybill) {
        //发mq 给异常系统
        AbnormalUnknownWaybillRequest abnormalUnknownWaybillRequest = new AbnormalUnknownWaybillRequest();
        abnormalUnknownWaybillRequest.setWaybillCode(waybillCode);
        abnormalUnknownWaybillRequest.setReportNumber(times);
        abnormalUnknownWaybillRequest.setTraderName(waybill.getBusiName());
        abnormalUnknownWaybillRequest.setTraderId(waybill.getBusiId());
        abnormalEclpSendProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(abnormalUnknownWaybillRequest));
        logger.info("三无寄托物核实B商家申请：" + JsonHelper.toJson(abnormalUnknownWaybillRequest));
        abnormalUnknownWaybill1Report.setIsReceipt(AbnormalUnknownWaybill.ISRECEIPT_NO);
        abnormalUnknownWaybill1Report.setReceiptFrom(AbnormalUnknownWaybill.RECEIPT_FROM_B);
        abnormalUnknownWaybill1Report.setOrderNumber(times);
        return true;
    }

    /**
     * 组装运单里的商品明细
     *
     * @param abnormalUnknownWaybill
     * @param waybillDetail
     * @param goods
     */
    private void buildWaybillDetails(AbnormalUnknownWaybill abnormalUnknownWaybill, StringBuilder waybillDetail, List<Goods> goods) {
        for (int i = 0; i < goods.size(); i++) {
            //明细内容： 商品编码SKU：商品名称*数量
            waybillDetail.append(goods.get(i).getSku() + ":" + goods.get(i).getGoodName() + " * " + goods.get(i).getGoodCount());
            if (i != goods.size() - 1) {
                //除了最后一个，其他拼完加个,
                waybillDetail.append(",");
            }
        }
        //设置回复系统
        abnormalUnknownWaybill.setReceiptFrom(AbnormalUnknownWaybill.RECEIPT_FROM_WAYBILL);
        //设置已回复
        abnormalUnknownWaybill.setIsReceipt(AbnormalUnknownWaybill.ISRECEIPT_YES);
        //设置明细
        abnormalUnknownWaybill.setReceiptContent(waybillDetail.toString());
        //设计次数
        abnormalUnknownWaybill.setOrderNumber(AbnormalUnknownWaybill.ORDERNUMBER_0);
        //回复时间
        abnormalUnknownWaybill.setReceiptTime(new Date());

    }

    /**
     * 组装运单里托寄物明细
     * @param abnormalUnknownWaybill
     * @param waybillDetail
     * @param waybillExt
     */
    private void buildWaybillDetailsByConsignWare(AbnormalUnknownWaybill abnormalUnknownWaybill, StringBuilder waybillDetail, WaybillExt waybillExt) {

        //明细内容
        waybillDetail.append(waybillExt.getConsignWare() + (waybillExt.getConsignCount() == null ? "" : " * " + waybillExt.getConsignCount()));

        //设置回复系统
        abnormalUnknownWaybill.setReceiptFrom(AbnormalUnknownWaybill.RECEIPT_FROM_WAYBILL);
        //设置已回复
        abnormalUnknownWaybill.setIsReceipt(AbnormalUnknownWaybill.ISRECEIPT_YES);
        //设置明细
        abnormalUnknownWaybill.setReceiptContent(waybillDetail.toString());
        //设计次数
        abnormalUnknownWaybill.setOrderNumber(AbnormalUnknownWaybill.ORDERNUMBER_0);
        //回复时间
        abnormalUnknownWaybill.setReceiptTime(new Date());
    }

    /**
     * 组装eclp里的商品明细
     *
     * @param itemInfos
     * @param abnormalUnknownWaybill
     * @param waybillDetail
     */
    private void queryEclpDetails(List<ItemInfo> itemInfos, AbnormalUnknownWaybill abnormalUnknownWaybill, StringBuilder waybillDetail) {
        for (int i = 0; i < itemInfos.size(); i++) {
            //明细内容： 商品名称*数量 优先取deptRealOutQty，如果该字段为空取realOutstoreQty  eclp负责人宫体雷
            waybillDetail.append(itemInfos.get(i).getGoodsName() + " * " + (itemInfos.get(i).getDeptRealOutQty() == null ? itemInfos.get(i).getRealOutstoreQty() : itemInfos.get(i).getDeptRealOutQty()));
            if (i != itemInfos.size() - 1) {
                //除了最后一个，其他拼完加个,
                waybillDetail.append(",");
            }
        }
        //设置回复系统
        abnormalUnknownWaybill.setReceiptFrom(AbnormalUnknownWaybill.RECEIPT_FROM_ECLP);
        //设置已回复
        abnormalUnknownWaybill.setIsReceipt(AbnormalUnknownWaybill.ISRECEIPT_YES);
        //设置明细
        abnormalUnknownWaybill.setReceiptContent(waybillDetail.toString());
        //设计次数
        abnormalUnknownWaybill.setOrderNumber(AbnormalUnknownWaybill.ORDERNUMBER_0);
        //回复时间
        abnormalUnknownWaybill.setReceiptTime(new Date());
    }

    /**
     * 构造核实的类
     *
     * @param userDto
     * @param waybillCode
     * @return
     * @throws Exception
     */
    public AbnormalUnknownWaybill buildAbnormalUnknownWaybillFactory(BaseStaffSiteOrgDto userDto, String waybillCode, AreaNode areaNode, Waybill waybill) {
        AbnormalUnknownWaybill abnormalUnknownWaybill = new AbnormalUnknownWaybill();
        abnormalUnknownWaybill.setWaybillCode(waybillCode);
        abnormalUnknownWaybill.setCreateUser(userDto.getAccountNumber());
        abnormalUnknownWaybill.setCreateUserCode(userDto.getStaffNo());
        abnormalUnknownWaybill.setCreateUserName(userDto.getStaffName());
        abnormalUnknownWaybill.setDmsSiteCode(userDto.getSiteCode());
        abnormalUnknownWaybill.setDmsSiteName(userDto.getSiteName());
        abnormalUnknownWaybill.setAreaId(areaNode.getId());
        abnormalUnknownWaybill.setAreaName(areaNode.getName());
        abnormalUnknownWaybill.setIsDelete(0);
        //商家信息
        abnormalUnknownWaybill.setTraderId(waybill.getBusiId());
        abnormalUnknownWaybill.setTraderName(waybill.getBusiName());
        return abnormalUnknownWaybill;
    }

    /**
     * 从sysconfig表里查出来 上报次数限制
     *
     * @return
     */
    @Cache(key = "AbnormalUnknownWaybillServiceImpl.getReportTimes", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = false)
    public int getReportTimes() {
        final int defaultTimes = AbnormalUnknownWaybill.ORDERNUMBER_DEFAULT_MAX;//默认是2次
        List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(Constants.SYS_ABNORMAL_UNKNOWN_REPORT_TIMES);
        if (sysConfigs != null && !sysConfigs.isEmpty()) {
            String contents = sysConfigs.get(0).getConfigContent();
            if (StringUtils.isEmpty(contents)) {
                return defaultTimes;
            }
            try {
                return Integer.parseInt(contents);
            } catch (Exception e) {
                logger.warn("系统配置abnormal.unknown.report.times内容不合法：" + contents);
                return defaultTimes;
            }
        }
        return defaultTimes;
    }

    @Override
    public JdResponse<String> submitAgain(String waybillCode) {
        JdResponse<String> rest = new JdResponse<String>();
        if (WaybillUtil.isWaybillCode(waybillCode)) {
            AbnormalUnknownWaybill abnormalUnknownWaybill = abnormalUnknownWaybillDao.findLastReportByWaybillCode(waybillCode);
            if (abnormalUnknownWaybill == null) {
                rest.toFail(waybillCode + "该运单还未上报过");
                logger.warn(waybillCode + "该运单还未上报过");
                return rest;
            }
            if (abnormalUnknownWaybill.getOrderNumber() == AbnormalUnknownWaybill.ORDERNUMBER_0) {
                rest.toFail(waybillCode + "已由系统回复，不允许上报");
                logger.warn(waybillCode + "已由系统回复，不允许上报");
                return rest;
            }
            if (abnormalUnknownWaybill.getIsReceipt() == AbnormalUnknownWaybill.ISRECEIPT_NO) {
                rest.toFail(waybillCode + "第" + abnormalUnknownWaybill.getOrderNumber() + "次上报还未回复，不允许再次上报");
                logger.warn(waybillCode + "第" + abnormalUnknownWaybill.getOrderNumber() + "次上报还未回复，不允许再次上报");
                return rest;
            }
            int maxTime = getReportTimes();
            if (abnormalUnknownWaybill.getOrderNumber() >= maxTime) {
                rest.toFail(waybillCode + "上报次数已超过上限" + maxTime + "次，不允许再次上报");
                logger.warn(waybillCode + "上报次数已超过上限" + maxTime + "次，不允许再次上报");
                return rest;
            }
            //获取用户信息
            LoginContext loginContext = LoginContext.getLoginContext();
            BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
            //站点区域查出来
            BaseStaffSiteOrgDto org = baseMajorManager.getBaseSiteBySiteId(userDto.getSiteCode());
            if (org == null) {
                rest.toFail("所在站点未找到：" + userDto.getSiteName());
                logger.warn("所在站点未找到：" + userDto.getSiteName());
                return rest;
            }
            ProvinceNode province = AreaHelper.getProvince(Integer.parseInt(Long.valueOf(org.getProvinceId()).toString()));
            if (province == null) {
                rest.toFail("站点所在省份获取失败：" + org.getProvinceId());
                logger.warn("站点所在省份获取失败：" + org.getProvinceId());
                return rest;
            }
            AreaNode areaNode = AreaHelper.getAreaByProvinceId(province.getId());
            if (areaNode == null) {
                rest.toFail("站点所在区域获取失败：" + province.getId());
                logger.warn("站点所在区域获取失败：" + province.getId());
                return rest;
            }
            //查运单
            BigWaybillDto bigWaybillDto = waybillService.getWaybillProduct(waybillCode);
            //构建实体
            AbnormalUnknownWaybill abnormalUnknownWaybill1New = buildAbnormalUnknownWaybillFactory(userDto, waybillCode, areaNode, bigWaybillDto.getWaybill());
            //发mq
            queryDetailForB(waybillCode, abnormalUnknownWaybill1New, abnormalUnknownWaybill.getOrderNumber() + 1, bigWaybillDto.getWaybill());
            //插入
            if (abnormalUnknownWaybillDao.insert(abnormalUnknownWaybill1New)) {
                rest.setData(waybillCode);
                rest.toSucceed("提报成功");
                return rest;
            } else {
                rest.toFail("服务异常");
                logger.warn("插入上报失败" + waybillCode);
                return rest;
            }


        } else {
            rest.toFail("非法操作");
            logger.warn("非法操作二次上报" + waybillCode);
            return rest;
        }
    }

    /**
     * 查最后一次上报
     */
    public AbnormalUnknownWaybill findLastReportByWaybillCode(String waybillCode) {
        return abnormalUnknownWaybillDao.findLastReportByWaybillCode(waybillCode);
    }

    /**
     * 回写结果
     *
     * @param abnormalUnknownWaybill
     * @return
     */
    public int updateReceive(AbnormalUnknownWaybill abnormalUnknownWaybill) {
        return abnormalUnknownWaybillDao.updateReceive(abnormalUnknownWaybill);
    }

    /**
     * 整理导出数据
     *
     * @param abnormalUnknownWaybillCondition
     * @return
     */
    public List<List<Object>> getExportData(AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();

        List<Object> heads = new ArrayList<Object>();

        //添加表头
        heads.add("运单号");
        heads.add("第几次上报");
        heads.add("商家名称");
        heads.add("机构名称");
        heads.add("区域名称");
        heads.add("是否回复");
        heads.add("回复时间");
        heads.add("回复来源");
        heads.add("托寄物");
        heads.add("提报人");
        resList.add(heads);

        abnormalUnknownWaybillCondition.setLimit(-1);
        PagerResult<AbnormalUnknownWaybill> pagerResult = this.queryByPagerCondition(abnormalUnknownWaybillCondition);
        List<AbnormalUnknownWaybill> rows = pagerResult.getRows();
        if (rows != null && rows.size() > 0) {
            for (AbnormalUnknownWaybill abnormalUnknownWaybill : rows) {
                List<Object> body = Lists.newArrayList();
                body.add(abnormalUnknownWaybill.getWaybillCode());//运单号
                body.add(abnormalUnknownWaybill.getOrderNumber());//第几次上报
                body.add(abnormalUnknownWaybill.getTraderName());//商家名称
                body.add(abnormalUnknownWaybill.getDmsSiteName());//机构名称
                body.add(abnormalUnknownWaybill.getAreaName());//区域名称
                body.add(abnormalUnknownWaybill.getIsReceipt() == null ? null : abnormalUnknownWaybill.getIsReceipt() == 1 ? "是" : "否");//是否回复
                body.add(abnormalUnknownWaybill.getReceiptTime() == null ? null : DateHelper.formatDate(abnormalUnknownWaybill.getReceiptTime(), Constants.DATE_TIME_FORMAT));//回复时间
                body.add(abnormalUnknownWaybill.getReceiptFrom() == null ? null : AbnormalUnknownWaybill.RECEIPT_FROM_WAYBILL.equals(abnormalUnknownWaybill.getReceiptFrom()) ? "运单系统" : AbnormalUnknownWaybill.RECEIPT_FROM_ECLP.equals(abnormalUnknownWaybill.getReceiptFrom()) ? "ECLP系统" : "商家回复");
                body.add(abnormalUnknownWaybill.getReceiptContent());
                body.add(abnormalUnknownWaybill.getCreateUserName());
                resList.add(body);
            }
        }
        return resList;
    }

    @Override
    public PagerResult<AbnormalUnknownWaybill> queryByPagerCondition(AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition) {
        //不是通过搜索加载数据
        if (abnormalUnknownWaybillCondition.getWaybillCode() == null && abnormalUnknownWaybillCondition.getWaybillCodes() == null) {
            return this.getDao().queryByPagerCondition(abnormalUnknownWaybillCondition);
        } else {
            //查询并补充无详细的运单号
            return queryAbnormalUnknownWaybillAndReplenish(abnormalUnknownWaybillCondition);
        }
    }

    @Override
    public JdResponse<String> queryByWaybillCode(List<String> waybillCodes){

        JdResponse response = new JdResponse();
        response.setCode(JdResponse.CODE_SUCCESS);
        //非法运单号
        StringBuilder notWaybillCodes = new StringBuilder();
        int count = 0;
        for(String inputWaybillCode : waybillCodes){
            inputWaybillCode = inputWaybillCode.trim();
            count++;
            //不存在的运单号
            if(StringHelper.isNotEmpty(inputWaybillCode) && WaybillUtil.isWaybillCode(inputWaybillCode)){
                List<AbnormalUnknownWaybill> list = abnormalUnknownWaybillDao.queryByWaybillCode(inputWaybillCode);
                if(list == null || list.size() == 0){
                    response.setCode(JdResponse.CODE_FAIL);
                    response.setMessage("未检索到该运单号：" + inputWaybillCode);
                    break;
                }
            }
            if(StringHelper.isNotEmpty(inputWaybillCode) && !WaybillUtil.isWaybillCode(inputWaybillCode)){
                if(count == waybillCodes.size()){
                    notWaybillCodes.append(inputWaybillCode);
                }else {
                    notWaybillCodes.append(inputWaybillCode).append(AbnormalUnknownWaybill.SEPARATOR_APPEND);
                }
            }
        }
        if(notWaybillCodes.length() > 0){
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("以下运单号不合法：" + notWaybillCodes.toString());
            return response;
        }
        return response;
    }

    /**
     * 查询并补充无详细的运单号
     *
     * @param abnormalUnknownWaybillCondition
     * @return
     */
    private PagerResult<AbnormalUnknownWaybill> queryAbnormalUnknownWaybillAndReplenish(AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition) {
        int limit = abnormalUnknownWaybillCondition.getLimit();
        //查出这批全部
        abnormalUnknownWaybillCondition.setLimit(-1);
        PagerResult<AbnormalUnknownWaybill> pagerResult = this.getDao().queryByPagerCondition(abnormalUnknownWaybillCondition);
        if (pagerResult != null && pagerResult.getTotal() > 0) {
            if (abnormalUnknownWaybillCondition.getWaybillCode() != null) {//代表前端输入的一个运单号
                //肯定就是那一个了
                return pagerResult;

            } else {//代表输入的多个运单号
                //补上没查到的单号
                List<AbnormalUnknownWaybill> data = pagerResult.getRows();
                //前端输入的运单号
                List<String> waybillCodes = abnormalUnknownWaybillCondition.getWaybillCodes();
                //转成set
                Set<String> waybillCodesSetRequest = new HashSet(waybillCodes);
                //查询结果封装成set
                Set<String> waybillCodesSetDb = Sets.newHashSet();
                for (AbnormalUnknownWaybill abnormalUnknownWaybill : data) {
                    waybillCodesSetDb.add(abnormalUnknownWaybill.getWaybillCode());
                }
                //存在的删掉，剩下的是需要补的
                waybillCodesSetRequest.removeAll(waybillCodesSetDb);
                for (String waybillCode : waybillCodesSetRequest) {
                    AbnormalUnknownWaybill abnormalUnknownWaybill = new AbnormalUnknownWaybill();
                    abnormalUnknownWaybill.setWaybillCode(waybillCode);
                    data.add(abnormalUnknownWaybill);
                }
                pagerResult.setTotal(data.size());
                if (limit == -1) {
                    //导出的话，全部返回
                    pagerResult.setRows(data);
                } else {
                    int endIndex = (abnormalUnknownWaybillCondition.getOffset() + limit) > data.size() ? data.size() : (abnormalUnknownWaybillCondition.getOffset() + limit);
                    pagerResult.setRows(data.subList(abnormalUnknownWaybillCondition.getOffset(), endIndex));
                }
                return pagerResult;
            }
        } else {//如果没有查询结果，要补出前端输入的运单号
            pagerResult = new PagerResult<AbnormalUnknownWaybill>();
            List<AbnormalUnknownWaybill> data = Lists.newArrayList();
            if (abnormalUnknownWaybillCondition.getWaybillCode() != null) {//代表前端输入的一个运单号
                pagerResult.setTotal(1);
                AbnormalUnknownWaybill abnormalUnknownWaybill = new AbnormalUnknownWaybill();
                abnormalUnknownWaybill.setWaybillCode(abnormalUnknownWaybillCondition.getWaybillCode());
                //添加商家名称
                abnormalUnknownWaybill.setTraderName(getBusiName(abnormalUnknownWaybillCondition.getWaybillCode()));
                data.add(abnormalUnknownWaybill);
            } else {//代表输入的多个运单号
                List<String> waybillCodes = abnormalUnknownWaybillCondition.getWaybillCodes();
                for (String waybillCode : waybillCodes) {
                    AbnormalUnknownWaybill abnormalUnknownWaybill = new AbnormalUnknownWaybill();
                    abnormalUnknownWaybill.setWaybillCode(waybillCode);
                    //添加商家名称
                    abnormalUnknownWaybill.setTraderName(getBusiName(waybillCode));
                    data.add(abnormalUnknownWaybill);
                }
                pagerResult.setTotal(data.size());
            }
            pagerResult.setRows(data);
            return pagerResult;
        }
    }

    /**
     * 根据运单号获取商家名称
     * @param waybillCode
     * @return
     */
    private String getBusiName(String waybillCode) {
        String busiName = "";
        BaseEntity<BigWaybillDto> entity = waybillQueryManager.getDataByChoice(waybillCode,
                true, false, false, false);
        if(entity != null && entity.getData() != null && entity.getData().getWaybill() != null){
            busiName = entity.getData().getWaybill().getBusiName();
        }
        return busiName;
    }
}
