package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.EclpItemManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.dao.AbnormalUnknownWaybillDao;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybillRequest;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.domain.AreaNode;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.web.LoginContext;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
    @Qualifier("abnormalUnknownWaybillDao")
    private AbnormalUnknownWaybillDao abnormalUnknownWaybillDao;

    @Override
    public Dao<AbnormalUnknownWaybill> getDao() {
        return this.abnormalUnknownWaybillDao;
    }

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private EclpItemManager eclpItemManager;

    @Autowired
    BaseMinorManager baseMinorManager;

    @Autowired
    @Qualifier("abnormalUnknownSendProducer")
    private DefaultJMQProducer abnormalEclpSendProducer;

    /**
     * 查询并上报
     *
     * @return
     */
    public JdResponse<String> queryAndReport(AbnormalUnknownWaybill request) {
        JdResponse<String> rest = new JdResponse<String>();
        if (request.getWaybillCode() == null || StringUtils.isBlank(request.getWaybillCode())) {
            rest.toFail("运单号不能为空");
            return rest;
        }
        //传入的运单号处理
        String[] waybillcodes = StringUtils.trim(request.getWaybillCode()).split("\\n");
        List<String> waybillList = Lists.newArrayList();
        //不合法的运单号
        StringBuilder notWaybillCodes = new StringBuilder();
        //不存在的运单号集合
        StringBuilder noExistsWaybills = new StringBuilder();
        for (String waybillCodeInput : waybillcodes) {
            String waybillCode = waybillCodeInput.trim();
            if (BusinessHelper.isWaybillCode(waybillCode)) {
                if (waybillService.queryWaybillIsExist(waybillCode)) {
                    waybillList.add(waybillCode);
                } else {
                    noExistsWaybills.append(waybillCode).append(",");
                }
            } else {
                notWaybillCodes.append(waybillCode).append(",");
            }
        }
        if (notWaybillCodes.length() > 0) {
            rest.toFail("以下运单号不合法" + notWaybillCodes + "请检查！");
            return rest;
        }
        if (noExistsWaybills.length() > 0) {
            rest.toFail("以下运单号不存在" + noExistsWaybills + "请检查！");
            return rest;
        }
        if (waybillList.size() == 0) {
            rest.toFail("无可用运单号");
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
//            BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
            BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache("bjxings");
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
                getGoodDetails(hasDetailWaybillCodes, addList, userDto, areaNode, waybillCode, request);
            }
            if (addList.size() > 0) {
                abnormalUnknownWaybillDao.batchInsert(addList);
            }
        }
        if (hasDetailWaybillCodes != null && hasDetailWaybillCodes.size() > 0) {
            rest.setData(StringUtils.join(hasDetailWaybillCodes.toArray(), ","));
        }


        return rest;
    }

    private void getGoodDetails(List<String> hasDetailWaybillCodes, List<AbnormalUnknownWaybill> addList, BaseStaffSiteOrgDto userDto, AreaNode areaNode, String waybillCode, AbnormalUnknownWaybill request) {
        //商品明细拼接使用
        StringBuilder waybillDetail = new StringBuilder();
        BigWaybillDto bigWaybillDto = waybillService.getWaybillProduct(waybillCode);
        //创建实体类
        AbnormalUnknownWaybill abnormalUnknownWaybill = buildAbnormalUnknownWaybillFactory(userDto, waybillCode, areaNode, bigWaybillDto.getWaybill());
        //第一步 查运单
        //如果运单有商品信息
        if (bigWaybillDto != null && bigWaybillDto.getGoodsList() != null && bigWaybillDto.getGoodsList().size() > 0) {
            buildWaybillDetails(abnormalUnknownWaybill, waybillDetail, bigWaybillDto.getGoodsList());
            addList.add(abnormalUnknownWaybill);//后面将插入表中
            hasDetailWaybillCodes.add(waybillCode);//前台用
            return;
        }
        //第二步 查eclp
        //如果运单上没有明细 就判断是不是eclp订单 如果是，调用eclp接口
        String busiOrderCode = bigWaybillDto.getWaybill().getBusiOrderCode();
        if (BusinessHelper.isECLPByBusiOrderCode(busiOrderCode)) {
            List<ItemInfo> itemInfos = eclpItemManager.getltemBySoNo(busiOrderCode);
            if (itemInfos != null && itemInfos.size() > 0) {
                queryEclpDetails(itemInfos, abnormalUnknownWaybill, waybillDetail);
                addList.add(abnormalUnknownWaybill);//后面将插入表中
                hasDetailWaybillCodes.add(waybillCode);//前台用
                return;
            }
        }
        //第三步 发B商家请求
        //查询运单
        if (1 == request.getIsReport()) {
            queryDetailForB(waybillCode, abnormalUnknownWaybill, 1);
            addList.add(abnormalUnknownWaybill);//后面将插入表中
            hasDetailWaybillCodes.add(waybillCode);//前台用
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
    private boolean queryDetailForB(String waybillCode, AbnormalUnknownWaybill abnormalUnknownWaybill1Report, Integer times) {
        //发mq 给异常系统
        AbnormalUnknownWaybillRequest abnormalUnknownWaybillRequest = new AbnormalUnknownWaybillRequest();
        abnormalUnknownWaybillRequest.setWaybillCode(waybillCode);
        abnormalUnknownWaybillRequest.setReportNumber(times.toString());
        abnormalEclpSendProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(abnormalUnknownWaybillRequest));
        logger.debug("三无寄托物核实申请：" + JsonHelper.toJson(abnormalUnknownWaybillRequest));
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
            //明细内容： 商品名称*数量
            waybillDetail.append(goods.get(i).getGoodName() + " * " + goods.get(i).getGoodCount());
            if (i != goods.size() - 1) {
                //除了最后一个，其他拼完加个换行
                waybillDetail.append("\\n");
            }
        }
        //设置回复系统
        abnormalUnknownWaybill.setReceiptFrom(AbnormalUnknownWaybill.RECEIPT_FROM_WAYBILL);
        //设置已回复
        abnormalUnknownWaybill.setIsReceipt(AbnormalUnknownWaybill.ISRECEIPT_YES);
        //设置明细
        abnormalUnknownWaybill.setReceiptContent(waybillDetail.toString());
        //设计次数
        abnormalUnknownWaybill.setOrderNumber(0);
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
            //明细内容： 商品名称*数量
            waybillDetail.append(itemInfos.get(i).getGoodsName() + " * " + itemInfos.get(i).getRealOutstoreQty());
            if (i != itemInfos.size() - 1) {
                //除了最后一个，其他拼完加个换行
                waybillDetail.append("\\n");
            }
        }
        //设置回复系统
        abnormalUnknownWaybill.setReceiptFrom(AbnormalUnknownWaybill.RECEIPT_FROM_ECLP);
        //设置已回复
        abnormalUnknownWaybill.setIsReceipt(AbnormalUnknownWaybill.ISRECEIPT_YES);
        //设置明细
        abnormalUnknownWaybill.setReceiptContent(waybillDetail.toString());
        //设计次数
        abnormalUnknownWaybill.setOrderNumber(0);
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
        abnormalUnknownWaybill.setTraderName(waybill.getBuyerName());
        return abnormalUnknownWaybill;
    }

    /**
     * 从sysconfig表里查出来 上报次数限制
     *
     * @return
     */
    @Cache(key = "AbnormalUnknownWaybillServiceImpl.getReportTimes", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = false)
    public int getReportTimes() {
        final int defaultTimes = 2;//默认是2次
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
        if (BusinessHelper.isWaybillCode(waybillCode)) {
            AbnormalUnknownWaybill abnormalUnknownWaybill = abnormalUnknownWaybillDao.findLastReportByWaybillCode(waybillCode);
            if (abnormalUnknownWaybill == null) {
                rest.toFail(waybillCode + "该运单还未上报过");
                logger.warn(waybillCode + "该运单还未上报过");
                return rest;
            }
            if (abnormalUnknownWaybill.getOrderNumber() == 0) {
                rest.toFail(waybillCode + "已由系统回复，不允许上报");
                logger.warn(waybillCode + "已由系统回复，不允许上报");
                return rest;
            }
            if (abnormalUnknownWaybill.getIsReceipt() == 0) {
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
//            BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
            BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache("bjxings");
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
            queryDetailForB(waybillCode, abnormalUnknownWaybill1New, abnormalUnknownWaybill.getOrderNumber() + 1);
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
}
