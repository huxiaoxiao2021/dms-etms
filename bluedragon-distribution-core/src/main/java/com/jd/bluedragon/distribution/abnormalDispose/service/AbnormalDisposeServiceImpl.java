package com.jd.bluedragon.distribution.abnormalDispose.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.distribution.abnormalDispose.dao.AbnormalQcDao;
import com.jd.bluedragon.distribution.abnormalDispose.domain.*;
import com.jd.bluedragon.distribution.abnormalorder.dao.AbnormalOrderDao;
import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;
import com.jd.bluedragon.distribution.abnormalwaybill.dao.AbnormalWayBillDao;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.domain.AreaNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.api.common.dto.PageDto;
import com.jd.etms.api.transferwavemonitor.req.TransferWaveMonitorReq;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorDetailResp;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorResp;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;

/**
 * @author tangchunqing
 * @Description: 批次清零
 * @date 2018年06月18日 09时:52分
 */
@Service("abnormalDisposeService")
public class AbnormalDisposeServiceImpl implements AbnormalDisposeService {

    private static final Logger log = LoggerFactory.getLogger(AbnormalDisposeServiceImpl.class);
    /**
     * 运单路由字段使用的分隔符
     */
    private static final String WAYBILL_ROUTER_SPLITER = "\\|";
    private static final String QC_FLAG="QC";
    //查询最大阈值，防止调用路由次数过多 影响性能
    public static Integer DEFAULT_MAX_NUM = 5000;
    @Autowired
    private AbnormalQcDao abnormalQcDao;
    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;
    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;
    @Autowired
    private SiteService siteService;
    @Autowired
    private AbnormalOrderDao abnormalOrderDao;
    @Autowired
    private AbnormalWayBillDao abnormalWayBillDao;
    @Autowired
    private InspectionDao inspectionDao;
    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 未验货明细页面加载数据
     *
     * @param abnormalDisposeCondition
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalDisposeServiceImpl.queryInspection", mState = {JProEnum.TP})
    public PagerResult<AbnormalDisposeInspection> queryInspection(AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeInspection> pagerResult = new PagerResult<AbnormalDisposeInspection>();
        BaseStaffSiteOrgDto currSite = baseMajorManager.getBaseSiteByDmsCode(abnormalDisposeCondition.getSiteCode());
        if (currSite == null) {
            log.warn("当前站点不存在：{}" , abnormalDisposeCondition.getSiteCode());
            pagerResult.setTotal(0);
            pagerResult.setRows(new ArrayList<AbnormalDisposeInspection>());
            return pagerResult;
        }
        //不进行过滤
        if (abnormalDisposeCondition.getIsDispose() == null) {
            return getInspectionOrdinary(abnormalDisposeCondition, pagerResult, currSite);
        } else {
            return getInspectionAll(abnormalDisposeCondition, pagerResult, currSite);
        }


    }

    /**
     * 查未验货明细 过滤是否已处理  这种就需要把明细全部抓过来，我们这边在实现分页
     *
     * @param abnormalDisposeCondition
     * @param pagerResult
     * @param currSite
     * @return
     */
    private PagerResult<AbnormalDisposeInspection> getInspectionAll(AbnormalDisposeCondition abnormalDisposeCondition, PagerResult<AbnormalDisposeInspection> pagerResult, BaseStaffSiteOrgDto currSite) {

        ArrayList<AbnormalDisposeInspection> resultData = new ArrayList<AbnormalDisposeInspection>();
        int totalPage = 1;//路由中总页数
        int currPage = 0;//翻页控制
        int hasGetIndex = 0;//已经得到数据索引，前端有可能查后几页的数据，前面非本页的数据，遍历到之后跳过，不new对象
        //如果路由系统已经最后一页了，就不要再拉数据了
        while (currPage < totalPage) {
            currPage++;//自动翻页查询
            if (currPage>100){
                break;//防止路由系统有BUG，导致我们循环
            }
            //调用路由接口查明细
            PageDto<TransferWaveMonitorDetailResp> page = new PageDto<TransferWaveMonitorDetailResp>();
            page.setPageSize(100);
            page.setCurrentPage(currPage);
            PageDto<TransferWaveMonitorDetailResp> noInspectionDetail = vrsRouteTransferRelationManager.getArrivedButNoCheckDetail(page, abnormalDisposeCondition.getWaveBusinessId());
            //如果没有明细，下面就不走了
            if (noInspectionDetail == null) {
                break;
            } else {
                //获取总页数
                totalPage = noInspectionDetail.getTotalPage();
                if (noInspectionDetail.getTotalRow() > DEFAULT_MAX_NUM) {
                    break;//查过阈值，就不拉了，数据量太大，有风险
                }
            }
            if (noInspectionDetail.getResult() == null || noInspectionDetail.getResult().size() == 0) {
                break;
            }

            //整理所有的运单号，后面批量查询 路由 异常等
            List<String> waybillCodeList = new ArrayList();
            for (TransferWaveMonitorDetailResp detailResp : noInspectionDetail.getResult()) {
                waybillCodeList.add(detailResp.getWaybillCode());
            }
            //查询路由信息（查ver）
            Map<String, String> routerMap = null;

            //查询已处理的明细
            Map<String, AbnormalQc> abnormalQcMap = queryAbnormalQcMap(abnormalDisposeCondition, waybillCodeList);
            for (TransferWaveMonitorDetailResp transferWaveMonitorDetailResp : noInspectionDetail.getResult()) {
                //想查未处理的,提报过异常的就过滤掉
                if (abnormalDisposeCondition.getIsDispose() != null && abnormalDisposeCondition.getIsDispose().equals(AbnormalDisposeCondition.IS_DISPOSE_NO) && abnormalQcMap.get(transferWaveMonitorDetailResp.getWaybillCode()) != null) {
                    continue;
                }
                //想查已处理的,没提报过异常的就过滤掉
                if (abnormalDisposeCondition.getIsDispose() != null && abnormalDisposeCondition.getIsDispose().equals(AbnormalDisposeCondition.IS_DISPOSE_YES) && abnormalQcMap.get(transferWaveMonitorDetailResp.getWaybillCode()) == null) {
                    continue;
                }
                hasGetIndex++;
                //说明还没有遍历到 查询页所需要的数据，只记录索引
                if (abnormalDisposeCondition.getOffset() >= hasGetIndex) {
                    continue;
                }
                //说明抓取的数据已经够了，改跳出了 resultData.size()理论上不会出现>limit的情况
                if ((abnormalDisposeCondition.getOffset() + abnormalDisposeCondition.getLimit()) < hasGetIndex && resultData.size() >= abnormalDisposeCondition.getLimit()) {
                    continue;
                }
                if (routerMap == null) {//放在这里，避免当前页都要过滤掉时，没必要的调用ver
                    routerMap = jsfSortingResourceService.getRouterByWaybillCodes(waybillCodeList);
                }
                resultData.add(convertAbnormalDisposeInspection(abnormalDisposeCondition, currSite, routerMap, abnormalQcMap, transferWaveMonitorDetailResp));
            }
        }
        pagerResult.setTotal(hasGetIndex);
        pagerResult.setRows(resultData);
        return pagerResult;

    }

    /**
     * 查询qc表处理的明细
     *
     * @param abnormalDisposeCondition
     * @param waybillCodeList
     * @return
     */
    private Map<String, AbnormalQc> queryAbnormalQcMap(AbnormalDisposeCondition abnormalDisposeCondition, List<String> waybillCodeList) {
        List<AbnormalQc> abnormalQcs = abnormalQcDao.queryQcByWaveIdAndWaybillCodes(abnormalDisposeCondition.getWaveBusinessId(), waybillCodeList);
        Map<String, AbnormalQc> abnormalQcMap = Maps.newHashMap();
        if (abnormalQcs != null && abnormalQcs.size() > 0) {
            for (AbnormalQc abnormalQc : abnormalQcs) {
                abnormalQcMap.put(abnormalQc.getWaybillCode(), abnormalQc);
            }
        }
        return abnormalQcMap;
    }

    private AbnormalDisposeInspection convertAbnormalDisposeInspection(AbnormalDisposeCondition abnormalDisposeCondition, BaseStaffSiteOrgDto currSite, Map<String, String> routerMap, Map<String, AbnormalQc> abnormalQcMap, TransferWaveMonitorDetailResp transferWaveMonitorDetailResp) {
        AbnormalDisposeInspection abnormalDisposeInspection = new AbnormalDisposeInspection();
        BaseStaffSiteOrgDto prevDto = getPrevSite(routerMap.get(transferWaveMonitorDetailResp.getWaybillCode()), currSite.getSiteCode());
        if (prevDto != null) {
            //上级站点、区域
            abnormalDisposeInspection.setPrevSiteCode(prevDto.getDmsSiteCode());
            abnormalDisposeInspection.setPrevSiteName(prevDto.getSiteName());
            AreaNode areaNode = AreaHelper.getAreaByProvinceId(prevDto.getProvinceId());
            if (areaNode != null) {
                abnormalDisposeInspection.setPrevAreaId(areaNode.getId());
                abnormalDisposeInspection.setPrevAreaName(areaNode.getName());
            }
        }
        abnormalDisposeInspection.setSealSiteCode(currSite.getDmsSiteCode());
        abnormalDisposeInspection.setSealSiteName(currSite.getSiteName());
        //目的站点、城市
        abnormalDisposeInspection.setEndSiteCode(transferWaveMonitorDetailResp.getEndNodeCode());
        abnormalDisposeInspection.setEndSiteName(transferWaveMonitorDetailResp.getEndNodeName());
        abnormalDisposeInspection.setEndCityName(transferWaveMonitorDetailResp.getEndCityName());
        //运单号
        abnormalDisposeInspection.setWaybillCode(transferWaveMonitorDetailResp.getWaybillCode());
        AbnormalQc abnormalQc = abnormalQcMap.get(transferWaveMonitorDetailResp.getWaybillCode());
        //质控编码
        if (abnormalQc != null) {
            abnormalDisposeInspection.setQcCode(abnormalQc.getQcCode());
            abnormalDisposeInspection.setCreateUser(abnormalQc.getCreateUser());
            abnormalDisposeInspection.setCreateTime(abnormalQc.getCreateTime());
        }
        abnormalDisposeInspection.setWaveBusinessId(abnormalDisposeCondition.getWaveBusinessId());
        abnormalDisposeInspection.setSealVehicleDate(transferWaveMonitorDetailResp.getRealTime());
        return abnormalDisposeInspection;
    }

    /**
     * 查未验货明细 不必过滤是否已处理
     *
     * @param abnormalDisposeCondition
     * @param pagerResult
     * @param currSite
     * @return
     */
    private PagerResult<AbnormalDisposeInspection> getInspectionOrdinary(AbnormalDisposeCondition abnormalDisposeCondition, PagerResult<AbnormalDisposeInspection> pagerResult, BaseStaffSiteOrgDto currSite) {
        //调用路由接口查明细
        PageDto<TransferWaveMonitorDetailResp> page = new PageDto<TransferWaveMonitorDetailResp>();
        page.setPageSize(abnormalDisposeCondition.getLimit());
        page.setCurrentPage(getCurrentPage(abnormalDisposeCondition.getOffset(), abnormalDisposeCondition.getLimit()));
        PageDto<TransferWaveMonitorDetailResp> noSendDetail = vrsRouteTransferRelationManager.getArrivedButNoCheckDetail(page, abnormalDisposeCondition.getWaveBusinessId());
        //如果没有明细，下面就不走了
        if (noSendDetail == null || noSendDetail.getResult() == null || noSendDetail.getResult().size() == 0) {
            pagerResult.setTotal(0);
            pagerResult.setRows(new ArrayList<AbnormalDisposeInspection>());
            return pagerResult;
        }
        //整理所有的运单号，后面批量查询 路由 异常等
        List<String> waybillCodeList = new ArrayList();
        for (TransferWaveMonitorDetailResp detailResp : noSendDetail.getResult()) {
            waybillCodeList.add(detailResp.getWaybillCode());
        }
        //查询路由信息（查ver）
        Map<String, String> routerMap = jsfSortingResourceService.getRouterByWaybillCodes(waybillCodeList);

        //查询已处理的明细
        Map<String, AbnormalQc> abnormalQcMap = queryAbnormalQcMap(abnormalDisposeCondition, waybillCodeList);
        List<AbnormalDisposeInspection> resultData = new ArrayList<AbnormalDisposeInspection>();
        for (TransferWaveMonitorDetailResp transferWaveMonitorDetailResp : noSendDetail.getResult()) {
            AbnormalDisposeInspection abnormalDisposeInspection = convertAbnormalDisposeInspection(abnormalDisposeCondition, currSite, routerMap, abnormalQcMap, transferWaveMonitorDetailResp);
            resultData.add(abnormalDisposeInspection);
        }

        pagerResult.setTotal(noSendDetail.getTotalRow());
        pagerResult.setRows(resultData);
        return pagerResult;
    }

    /**
     * 根据路由 获取上游站点
     */
    private BaseStaffSiteOrgDto getPrevSite(String router, Integer currSiteCode) {
        if (router == null) {
            return null;
        }
        String[] routers = router.split(WAYBILL_ROUTER_SPLITER);
        if (routers != null && routers.length > 0) {
            for (int i = 0; i < routers.length; i++) {
                if (currSiteCode.toString().equals(routers[i])) {
                    if (i == 0) {
                        return null;
                    }
                    //上级站点信息
                    return siteService.getSite(Integer.valueOf(routers[i - 1]));
                }
            }
        }
        return null;
    }

    /**
     * 根据路由 获取下游站点
     */
    private BaseStaffSiteOrgDto getNextSite(String router, Integer currSiteCode) {
        if (router == null) {
            return null;
        }
        String[] routers = router.split(WAYBILL_ROUTER_SPLITER);
        if (routers != null && routers.length > 0) {
            for (int i = 0; i < routers.length; i++) {
                if (currSiteCode.toString().equals(routers[i])) {
                    if (i == routers.length - 1) {
                        return null;
                    }
                    //上级站点信息
                    return siteService.getSite(Integer.valueOf(routers[i + 1]));
                }
            }
        }
        return null;
    }

    /**
     * 计算页数
     */
    private Integer getCurrentPage(int offset, int limit) {
        if (offset == 0) {
            return 1;
        }
        if (offset > 0) {
            return offset / limit + 1;
        }
        return 1;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalDisposeServiceImpl.queryMain", mState = {JProEnum.TP})
    public PagerResult<AbnormalDisposeMain> queryMain(AbnormalDisposeCondition abnormalDisposeCondition,LoginUser loginUser) {
        BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(loginUser.getUserErp());
        if (userDto.getSiteType() == Constants.BASE_SITE_DISTRIBUTION_CENTER) {
            abnormalDisposeCondition.setSiteCode(userDto.getDmsSiteCode());//分拣中心的人只能查本分拣中心的 防止前台不合法请求
        }
        //封装分页参数
        PageDto<TransferWaveMonitorReq> page = new PageDto<TransferWaveMonitorReq>();
        page.setCurrentPage(getCurrentPage(abnormalDisposeCondition.getOffset(), abnormalDisposeCondition.getLimit()));
        page.setPageSize(abnormalDisposeCondition.getLimit());
        //封装查询条件
        TransferWaveMonitorReq parameter = new TransferWaveMonitorReq();
        parameter.setCityId(abnormalDisposeCondition.getCityId());
        parameter.setOrgId(abnormalDisposeCondition.getAreaId());
        parameter.setProvinceId(abnormalDisposeCondition.getProvinceId());
        parameter.setStartDate(abnormalDisposeCondition.getStartTime());
        parameter.setEndDate(abnormalDisposeCondition.getEndTime());
        parameter.setSiteCode(abnormalDisposeCondition.getSiteCode());
        PageDto<TransferWaveMonitorResp> pageDto = vrsRouteTransferRelationManager.getAbnormalTotal(page, parameter);

        PagerResult<AbnormalDisposeMain> pagerResult = new PagerResult<AbnormalDisposeMain>();
        if (pageDto == null || pageDto.getResult() == null || pageDto.getResult().size() == 0) {
            pagerResult.setTotal(0);
            pagerResult.setRows(new ArrayList<AbnormalDisposeMain>());
            return pagerResult;
        }
        //把班次拼到一起，批量查询用
        List<String> waveBusinessIds = Lists.newArrayList();
        for (TransferWaveMonitorResp transferWaveMonitorResp : pageDto.getResult()) {
            waveBusinessIds.add(transferWaveMonitorResp.getWaveBusinessId());
        }
        //统计未发货的处理情况
        Map<String, Integer> noSendTotalMap = queryNoSendTotalMap(waveBusinessIds);
        //统计未收货的处理情况
        Map<String, Integer> noInspectionTotalMap = queryNoInspectionMap(waveBusinessIds);

        List<AbnormalDisposeMain> abnormalDisposeMains = Lists.newArrayList();
        for (TransferWaveMonitorResp transferWaveMonitorResp : pageDto.getResult()) {
            convertAbnormalDisposeMain(noSendTotalMap, noInspectionTotalMap, abnormalDisposeMains, transferWaveMonitorResp);
        }
        pagerResult.setTotal(pageDto.getTotalRow());
        pagerResult.setRows(abnormalDisposeMains);
        return pagerResult;
    }

    /**
     * 封装对象
     *
     * @param noSendTotalMap
     * @param noInspectionTotalMap
     * @param abnormalDisposeMains
     * @param transferWaveMonitorResp
     */
    private void convertAbnormalDisposeMain(Map<String, Integer> noSendTotalMap, Map<String, Integer> noInspectionTotalMap, List<AbnormalDisposeMain> abnormalDisposeMains, TransferWaveMonitorResp transferWaveMonitorResp) {
        AbnormalDisposeMain abnormalDisposeMain = new AbnormalDisposeMain();
        abnormalDisposeMain.setWaveBusinessId(transferWaveMonitorResp.getWaveBusinessId());
        AreaNode areaNode = AreaHelper.getArea(transferWaveMonitorResp.getOrgId());
        if (areaNode != null) {
            abnormalDisposeMain.setAreaName(areaNode.getName());
        }
        abnormalDisposeMain.setSiteName(transferWaveMonitorResp.getSiteName());
        abnormalDisposeMain.setSiteCode(transferWaveMonitorResp.getSiteCode());
        abnormalDisposeMain.setTransferStartTime(transferWaveMonitorResp.getPlanStartTime());
        abnormalDisposeMain.setTransferEndTime(transferWaveMonitorResp.getPlanEndTime());
        abnormalDisposeMain.setTransferNo(transferWaveMonitorResp.getWaveCode());
        abnormalDisposeMain.setNotSendNum(transferWaveMonitorResp.getNoSendWaybillCount());
        abnormalDisposeMain.setNotSendDisposeNum(noSendTotalMap.get(transferWaveMonitorResp.getWaveBusinessId()));
        abnormalDisposeMain.setNotSendProgress(countProgress(noSendTotalMap.get(transferWaveMonitorResp.getWaveBusinessId()), transferWaveMonitorResp.getNoSendWaybillCount()));
        abnormalDisposeMain.setNotReceiveNum(transferWaveMonitorResp.getMayNoArrive());
        abnormalDisposeMain.setNotReceiveDisposeNum(noInspectionTotalMap.get(transferWaveMonitorResp.getWaveBusinessId()));
        abnormalDisposeMain.setNotReceiveProgress(countProgress(noInspectionTotalMap.get(transferWaveMonitorResp.getWaveBusinessId()), transferWaveMonitorResp.getMayNoArrive()));
        abnormalDisposeMain.setDateTime(transferWaveMonitorResp.getDateTime());
        abnormalDisposeMain.setTotalProgress(countProgress(getInteger(abnormalDisposeMain.getNotReceiveDisposeNum()) + getInteger(abnormalDisposeMain.getNotSendDisposeNum()), transferWaveMonitorResp.getNoSendWaybillCount() + transferWaveMonitorResp.getMayNoArrive()));
        abnormalDisposeMains.add(abnormalDisposeMain);
    }

    /**
     * 统计未收货的处理情况
     *
     * @param waveBusinessIds
     * @return
     */
    private Map<String, Integer> queryNoInspectionMap(List<String> waveBusinessIds) {
        Map<String, Integer> noInspectionTotalMap = Maps.newHashMap();
        List<AbnormalQc> qcTotal = abnormalQcDao.getByWaveIds(waveBusinessIds);
        if (qcTotal != null && qcTotal.size() > 0) {
            for (AbnormalQc abnormalQc : qcTotal) {
                noInspectionTotalMap.put(abnormalQc.getWaveBusinessId(), Integer.parseInt(abnormalQc.getQcCode()));
            }
        }
        return noInspectionTotalMap;
    }

    /**
     * 统计未发货的处理情况
     *
     * @param waveBusinessIds
     * @return
     */
    private Map<String, Integer> queryNoSendTotalMap(List<String> waveBusinessIds) {
        Map<String, Set<String>> noSendTotalMap = Maps.newHashMap();
        List<AbnormalOrder> orderTotal = abnormalOrderDao.queryByWaveIds(waveBusinessIds);
        List<AbnormalWayBill> waybillTotal = abnormalWayBillDao.queryByWaveIds(waveBusinessIds);
        if (orderTotal != null && orderTotal.size() > 0) {
            for (AbnormalOrder abnormalOrder : orderTotal) {
                if (noSendTotalMap.containsKey(abnormalOrder.getWaveBusinessId())) {
                    noSendTotalMap.get(abnormalOrder.getWaveBusinessId()).add(abnormalOrder.getOrderId());
                } else {
                    noSendTotalMap.put(abnormalOrder.getWaveBusinessId(), new HashSet<String>());
                    noSendTotalMap.get(abnormalOrder.getWaveBusinessId()).add(abnormalOrder.getOrderId());
                }
            }
        }
        if (waybillTotal != null && waybillTotal.size() > 0) {
            for (AbnormalWayBill abnormalWayBill : waybillTotal) {
                if (noSendTotalMap.containsKey(abnormalWayBill.getWaveBusinessId())) {
                    noSendTotalMap.get(abnormalWayBill.getWaveBusinessId()).add(abnormalWayBill.getWaybillCode());
                } else {
                    noSendTotalMap.put(abnormalWayBill.getWaveBusinessId(), new HashSet<String>());
                    noSendTotalMap.get(abnormalWayBill.getWaveBusinessId()).add(abnormalWayBill.getWaybillCode());
                }

            }
        }
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (String key : noSendTotalMap.keySet()) {
            result.put(key, noSendTotalMap.get(key).size());
        }
        return result;
    }

    private Integer getInteger(Integer value) {
        if (value == null) {
            return 0;
        } else {
            return value;
        }
    }

    /**
     * 计算百分比
     *
     * @param detail
     * @param total
     * @return
     */
    private String countProgress(Integer detail, Integer total) {
        if (detail == null || total == null || total == 0) {
            return "0";
        }
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) detail / (float) total * 100);
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalDisposeServiceImpl.querySend", mState = {JProEnum.TP})
    public PagerResult<AbnormalDisposeSend> querySend(AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeSend> pagerResult = new PagerResult<AbnormalDisposeSend>();
        BaseStaffSiteOrgDto currSite = baseMajorManager.getBaseSiteByDmsCode(abnormalDisposeCondition.getSiteCode());
        if (currSite == null) {
            log.warn("当前站点不存在：{}" , abnormalDisposeCondition.getSiteCode());
            pagerResult.setTotal(0);
            pagerResult.setRows(new ArrayList<AbnormalDisposeSend>());
            return pagerResult;
        }
        //不进行过滤
        if (abnormalDisposeCondition.getIsDispose() == null) {
            return getSendOrdinary(abnormalDisposeCondition, pagerResult, currSite);
        } else {
            return getSendAll(abnormalDisposeCondition, pagerResult, currSite);
        }
    }

    /**
     * 查未发货明细 不必过滤是否已处理
     *
     * @param abnormalDisposeCondition
     * @param pagerResult
     * @param currSite
     * @return
     */
    private PagerResult<AbnormalDisposeSend> getSendOrdinary(AbnormalDisposeCondition abnormalDisposeCondition, PagerResult<AbnormalDisposeSend> pagerResult, BaseStaffSiteOrgDto currSite) {
        //调用路由接口查明细
        PageDto<TransferWaveMonitorDetailResp> page = new PageDto<TransferWaveMonitorDetailResp>();
        page.setPageSize(abnormalDisposeCondition.getLimit());
        page.setCurrentPage(getCurrentPage(abnormalDisposeCondition.getOffset(), abnormalDisposeCondition.getLimit()));
        PageDto<TransferWaveMonitorDetailResp> noSendDetail = vrsRouteTransferRelationManager.getNoSendDetail(page, abnormalDisposeCondition.getWaveBusinessId());
        //整理所有的运单号，后面批量查询 路由 异常等
        ArrayList<String> waybillCodeList = new ArrayList();
        for (TransferWaveMonitorDetailResp detailResp : noSendDetail.getResult()) {
            waybillCodeList.add(detailResp.getWaybillCode());
        }
        //查询路由信息（查ver）
        Map<String, String> routerMap = jsfSortingResourceService.getRouterByWaybillCodes(waybillCodeList);

        //查询已处理的明细 外呼
        List<AbnormalOrder> abnormalOrders = abnormalOrderDao.queryByWaveIdAndWaybillCodes(abnormalDisposeCondition.getWaveBusinessId(), waybillCodeList);
        Map<String, AbnormalOrder> abnormalOrdersMap = convertAbnormalOrdersMap(abnormalOrders);
        //查询已处理的明细 异常
        List<AbnormalWayBill> abnormalWayBills = abnormalWayBillDao.queryByWaveIdAndWaybillCodes(abnormalDisposeCondition.getWaveBusinessId(), waybillCodeList);
        Map<String, AbnormalWayBill> abnormalWayBillsMap = convertAbnormalWaybillsMap(abnormalWayBills);
        //查验货时间
        List<Inspection> inspectionList = inspectionDao.findOperateTimeByWaybillCodes(currSite.getSiteCode(), waybillCodeList);
        Map<String, Inspection> inspectionWayBillsMap = convertInspectionWayBillsMap(inspectionList);
        ArrayList<AbnormalDisposeSend> resultData = new ArrayList<AbnormalDisposeSend>();
        for (TransferWaveMonitorDetailResp transferWaveMonitorDetailResp : noSendDetail.getResult()) {
            resultData.add(convertAbnormalDisposeSend(currSite, routerMap, abnormalOrdersMap, abnormalWayBillsMap, inspectionWayBillsMap, transferWaveMonitorDetailResp));
        }

        pagerResult.setTotal(noSendDetail.getTotalRow());
        pagerResult.setRows(resultData);
        return pagerResult;
    }

    /**
     * list 转 map
     *
     * @param abnormalOrders
     * @return
     */
    private Map<String, AbnormalOrder> convertAbnormalOrdersMap(List<AbnormalOrder> abnormalOrders) {
        Map<String, AbnormalOrder> abnormalOrdersMap = Maps.newHashMap();
        if (abnormalOrders != null && abnormalOrders.size() > 0) {
            for (AbnormalOrder abnormalOrder : abnormalOrders) {
                abnormalOrdersMap.put(abnormalOrder.getOrderId(), abnormalOrder);
            }
        }
        return abnormalOrdersMap;
    }

    /**
     * 封装对象
     *
     * @param currSite
     * @param routerMap
     * @param abnormalOrdersMap
     * @param abnormalWayBillsMap
     * @param inspectionWayBillsMap
     * @param transferWaveMonitorDetailResp
     * @return
     */
    private AbnormalDisposeSend convertAbnormalDisposeSend(BaseStaffSiteOrgDto currSite, Map<String, String> routerMap, Map<String, AbnormalOrder> abnormalOrdersMap, Map<String, AbnormalWayBill> abnormalWayBillsMap, Map<String, Inspection> inspectionWayBillsMap, TransferWaveMonitorDetailResp transferWaveMonitorDetailResp) {
        AbnormalDisposeSend abnormalDisposeSend = new AbnormalDisposeSend();
        //运单号
        abnormalDisposeSend.setWaybillCode(transferWaveMonitorDetailResp.getWaybillCode());
        BaseStaffSiteOrgDto nextDto = getNextSite(routerMap.get(transferWaveMonitorDetailResp.getWaybillCode()), currSite.getSiteCode());
        if (nextDto != null) {
            //下游站点、区域
            abnormalDisposeSend.setNextSiteCode(nextDto.getDmsSiteCode());
            abnormalDisposeSend.setNextSiteName(nextDto.getSiteName());
            AreaNode areaNode = AreaHelper.getAreaByProvinceId(nextDto.getProvinceId());
            if (areaNode != null) {
                abnormalDisposeSend.setNextAreaId(areaNode.getId());
                abnormalDisposeSend.setNextAreaName(areaNode.getName());
            }
        }
        abnormalDisposeSend.setInspectionSiteName(currSite.getSiteName());
        abnormalDisposeSend.setInspectionSiteCode(currSite.getDmsSiteCode());
        //目的站点、城市
        abnormalDisposeSend.setEndSiteCode(transferWaveMonitorDetailResp.getEndNodeCode());
        abnormalDisposeSend.setEndSiteName(transferWaveMonitorDetailResp.getEndNodeName());
        abnormalDisposeSend.setEndCityName(transferWaveMonitorDetailResp.getEndCityName());
        if (abnormalOrdersMap.get(abnormalDisposeSend.getWaybillCode()) != null) {//是外呼类型
            abnormalDisposeSend.setIsDispose("1");
            abnormalDisposeSend.setAbnormalType("1");
            abnormalDisposeSend.setAbnormalReason1(abnormalOrdersMap.get(abnormalDisposeSend.getWaybillCode()).getAbnormalReason1());
            abnormalDisposeSend.setCreateUser(abnormalOrdersMap.get(abnormalDisposeSend.getWaybillCode()).getCreateUser());
            abnormalDisposeSend.setCreateTime(abnormalOrdersMap.get(abnormalDisposeSend.getWaybillCode()).getCreateTime());
        } else if (abnormalWayBillsMap.get(abnormalDisposeSend.getWaybillCode()) != null) {//是异常类型
            abnormalDisposeSend.setIsDispose("1");
            abnormalDisposeSend.setAbnormalType("0");
            abnormalDisposeSend.setAbnormalReason1(abnormalWayBillsMap.get(abnormalDisposeSend.getWaybillCode()).getQcName());
            abnormalDisposeSend.setCreateUser(abnormalWayBillsMap.get(abnormalDisposeSend.getWaybillCode()).getCreateUser());
            abnormalDisposeSend.setCreateTime(abnormalWayBillsMap.get(abnormalDisposeSend.getWaybillCode()).getCreateTime());
        }
        //验货时间
        Inspection inspection = inspectionWayBillsMap.get(abnormalDisposeSend.getWaybillCode());
        if (inspection != null) {
            abnormalDisposeSend.setInspectionDate(inspection.getOperateTime());
        }
        return abnormalDisposeSend;
    }

    /**
     * 查未发货明细 过滤是否已处理  这种就需要把明细全部抓过来，我们这边在实现分页
     *
     * @param abnormalDisposeCondition
     * @param pagerResult
     * @param currSite
     * @return
     */
    private PagerResult<AbnormalDisposeSend> getSendAll(AbnormalDisposeCondition abnormalDisposeCondition, PagerResult<AbnormalDisposeSend> pagerResult, BaseStaffSiteOrgDto currSite) {

        ArrayList<AbnormalDisposeSend> resultData = new ArrayList<AbnormalDisposeSend>();
        int totalPage = 1;//路由中总页数
        int currPage = 0;//翻页控制
        int hasGetIndex = 0;//已经得到数据索引，前端有可能查后几页的数据，前面非本页的数据，遍历到之后跳过，不new对象
        //如果路由系统已经最后一页了，就不要再拉数据了
        while (currPage < totalPage) {
            currPage++;//自动翻页查询
            if (currPage>100){
                break;//防止路由系统有BUG，导致我们循环
            }
            //调用路由接口查明细
            PageDto<TransferWaveMonitorDetailResp> page = new PageDto<TransferWaveMonitorDetailResp>();
            page.setPageSize(100);
            page.setCurrentPage(currPage);
            PageDto<TransferWaveMonitorDetailResp> noSendDetail = vrsRouteTransferRelationManager.getNoSendDetail(page, abnormalDisposeCondition.getWaveBusinessId());
            //如果没有明细，下面就不走了
            if (noSendDetail == null) {
                break;
            } else {
                //获取总页数
                totalPage = noSendDetail.getTotalPage();
                if (noSendDetail.getTotalRow() > DEFAULT_MAX_NUM) {
                    break;//查过阈值，就不拉了，数据量太大，有风险
                }
            }
            if (noSendDetail.getResult() == null || noSendDetail.getResult().size() == 0) {
                break;
            }

            //整理所有的运单号，后面批量查询 路由 异常等
            ArrayList<String> waybillCodeList = new ArrayList();
            for (TransferWaveMonitorDetailResp detailResp : noSendDetail.getResult()) {
                waybillCodeList.add(detailResp.getWaybillCode());
            }
            //查询路由信息（查ver）
            Map<String, String> routerMap = null;

            //查询已处理的明细 外呼
            List<AbnormalOrder> abnormalOrders = abnormalOrderDao.queryByWaveIdAndWaybillCodes(abnormalDisposeCondition.getWaveBusinessId(), waybillCodeList);
            Map<String, AbnormalOrder> abnormalOrdersMap = convertAbnormalOrdersMap(abnormalOrders);
            //查询已处理的明细 异常
            List<AbnormalWayBill> abnormalWayBills = abnormalWayBillDao.queryByWaveIdAndWaybillCodes(abnormalDisposeCondition.getWaveBusinessId(), waybillCodeList);
            Map<String, AbnormalWayBill> abnormalWayBillsMap = convertAbnormalWaybillsMap(abnormalWayBills);
            //查验货时间
            List<Inspection> inspectionList = inspectionDao.findOperateTimeByWaybillCodes(currSite.getSiteCode(), waybillCodeList);
            Map<String, Inspection> inspectionWayBillsMap = convertInspectionWayBillsMap(inspectionList);
            for (TransferWaveMonitorDetailResp transferWaveMonitorDetailResp : noSendDetail.getResult()) {
                //想查未处理的,提报过异常的就过滤掉
                if (abnormalDisposeCondition.getIsDispose() != null && abnormalDisposeCondition.getIsDispose().equals(AbnormalDisposeCondition.IS_DISPOSE_NO) && (abnormalOrdersMap.get(transferWaveMonitorDetailResp.getWaybillCode()) != null || abnormalWayBillsMap.get(transferWaveMonitorDetailResp.getWaybillCode()) != null)) {
                    continue;
                }
                //想查已处理的,没提报过异常的就过滤掉
                if (abnormalDisposeCondition.getIsDispose() != null && abnormalDisposeCondition.getIsDispose().equals(AbnormalDisposeCondition.IS_DISPOSE_YES) && (abnormalOrdersMap.get(transferWaveMonitorDetailResp.getWaybillCode()) == null && abnormalWayBillsMap.get(transferWaveMonitorDetailResp.getWaybillCode()) == null)) {
                    continue;
                }
                hasGetIndex++;
                //说明还没有遍历到 查询页所需要的数据，只记录索引
                if (abnormalDisposeCondition.getOffset() >= hasGetIndex) {
                    continue;
                }
                //说明抓取的数据已经够了，改跳出了 resultData.size()理论上不会出现>limit的情况
                if ((abnormalDisposeCondition.getOffset() + abnormalDisposeCondition.getLimit()) < hasGetIndex && resultData.size() >= abnormalDisposeCondition.getLimit()) {
                    continue;
                }
                if (routerMap == null) {//放在这里，避免当前页都要过滤掉时，没必要的调用ver
                    routerMap = jsfSortingResourceService.getRouterByWaybillCodes(waybillCodeList);
                }
                resultData.add(convertAbnormalDisposeSend(currSite, routerMap, abnormalOrdersMap, abnormalWayBillsMap, inspectionWayBillsMap, transferWaveMonitorDetailResp));
            }
        }
        pagerResult.setTotal(hasGetIndex);
        pagerResult.setRows(resultData);
        return pagerResult;

    }

    /**
     * list 转 map
     *
     * @param inspectionList
     * @return
     */
    private Map<String, Inspection> convertInspectionWayBillsMap(List<Inspection> inspectionList) {
        Map<String, Inspection> inspectionWayBillsMap = Maps.newHashMap();
        if (inspectionList != null && inspectionList.size() > 0) {
            for (Inspection inspection : inspectionList) {
                inspectionWayBillsMap.put(inspection.getWaybillCode(), inspection);
            }
        }
        return inspectionWayBillsMap;
    }

    /**
     * 整理导出数据-未验货
     *
     * @param abnormalDisposeCondition
     * @return
     */
    public List<List<Object>> getExportDataInspection(AbnormalDisposeCondition abnormalDisposeCondition) {

        List<List<Object>> resList = new ArrayList<List<Object>>();
        BaseStaffSiteOrgDto currSite = baseMajorManager.getBaseSiteByDmsCode(abnormalDisposeCondition.getSiteCode());
        if (currSite == null) {
            return resList;
        }
        List<Object> heads = new ArrayList<Object>();

        //添加表头
        heads.add("解封车时间");
        heads.add("运单");
        heads.add("运单");
        heads.add("上级区域");
        heads.add("上级站点");
        heads.add("目的城市");
        heads.add("目的站点");
        heads.add("是否提报异常");
        heads.add("异常编码");
        heads.add("异常提交人");
        heads.add("异常提交时间");
        resList.add(heads);

        List<AbnormalDisposeInspection> rows = Lists.newArrayList();
        int totalPage = 1;//路由中总页数
        int currPage = 0;//翻页控制
        //如果路由系统已经最后一页了，就不要再拉数据了
        while (currPage < totalPage) {
            currPage++;//自动翻页查询
            if (currPage>100){
                break;//防止路由系统有BUG，导致我们循环
            }
            //调用路由接口查明细
            PageDto<TransferWaveMonitorDetailResp> page = new PageDto<TransferWaveMonitorDetailResp>();
            page.setPageSize(100);
            page.setCurrentPage(currPage);
            PageDto<TransferWaveMonitorDetailResp> noInspectionDetail = vrsRouteTransferRelationManager.getArrivedButNoCheckDetail(page, abnormalDisposeCondition.getWaveBusinessId());
            //如果没有明细，下面就不走了
            if (noInspectionDetail == null) {
                break;
            } else {
                //获取总页数
                totalPage = noInspectionDetail.getTotalPage();
                if (noInspectionDetail.getTotalRow() > DEFAULT_MAX_NUM) {
                    break;//查过阈值，就不拉了，数据量太大，有风险
                }
            }
            if (noInspectionDetail.getResult() == null || noInspectionDetail.getResult().size() == 0) {
                break;
            }

            //整理所有的运单号，后面批量查询 路由 异常等
            ArrayList<String> waybillCodeList = new ArrayList();
            for (TransferWaveMonitorDetailResp detailResp : noInspectionDetail.getResult()) {
                waybillCodeList.add(detailResp.getWaybillCode());
            }
            //查询路由信息（查ver）
            Map<String, String> routerMap = null;

            //查询已处理的明细
            Map<String, AbnormalQc> abnormalQcMap = queryAbnormalQcMap(abnormalDisposeCondition, waybillCodeList);
            for (TransferWaveMonitorDetailResp transferWaveMonitorDetailResp : noInspectionDetail.getResult()) {
                //想查未处理的,提报过异常的就过滤掉
                if (abnormalDisposeCondition.getIsDispose() != null && abnormalDisposeCondition.getIsDispose().equals(AbnormalDisposeCondition.IS_DISPOSE_NO) && abnormalQcMap.get(transferWaveMonitorDetailResp.getWaybillCode()) != null) {
                    continue;
                }
                //想查已处理的,没提报过异常的就过滤掉
                if (abnormalDisposeCondition.getIsDispose() != null && abnormalDisposeCondition.getIsDispose().equals(AbnormalDisposeCondition.IS_DISPOSE_YES) && abnormalQcMap.get(transferWaveMonitorDetailResp.getWaybillCode()) == null) {
                    continue;
                }
                if (routerMap == null) {//放在这里，避免当前页都要过滤掉时，没必要的调用ver
                    routerMap = jsfSortingResourceService.getRouterByWaybillCodes(waybillCodeList);
                }
                rows.add(convertAbnormalDisposeInspection(abnormalDisposeCondition, currSite, routerMap, abnormalQcMap, transferWaveMonitorDetailResp));
            }
        }
        if (rows != null && rows.size() > 0) {
            for (AbnormalDisposeInspection abnormalDisposeSend : rows) {
                List<Object> body = Lists.newArrayList();
                body.add(DateHelper.formatDate(abnormalDisposeSend.getSealVehicleDate(), Constants.DATE_TIME_FORMAT));//解封车时间
                body.add(abnormalDisposeSend.getWaybillCode());//运单号
                body.add(abnormalDisposeSend.getPrevAreaName());
                body.add(abnormalDisposeSend.getPrevSiteName());
                body.add(abnormalDisposeSend.getEndCityName());
                body.add(abnormalDisposeSend.getEndSiteName());
                body.add(AbnormalDisposeCondition.IS_DISPOSE_YES.equals(abnormalDisposeSend.getIsDispose()) ? "是" : "否");
                body.add(abnormalDisposeSend.getQcCode());
                body.add(abnormalDisposeSend.getCreateUser());
                body.add(DateHelper.formatDate(abnormalDisposeSend.getCreateTime(), Constants.DATE_TIME_FORMAT));
                resList.add(body);
            }
        }
        return resList;
    }

    /**
     * 整理导出数据-未发货
     *
     * @param abnormalDisposeCondition
     * @return
     */
    public List<List<Object>> getExportDataSend(AbnormalDisposeCondition abnormalDisposeCondition) {

        List<List<Object>> resList = new ArrayList<List<Object>>();
        BaseStaffSiteOrgDto currSite = baseMajorManager.getBaseSiteByDmsCode(abnormalDisposeCondition.getSiteCode());
        if (currSite == null) {
            return resList;
        }
        List<Object> heads = new ArrayList<Object>();

        //添加表头
        heads.add("验货时间");
        heads.add("验货分拣中心");
        heads.add("运单");
        heads.add("下级区域");
        heads.add("下级站点");
        heads.add("目的城市");
        heads.add("目的站点");
        heads.add("是否提报异常");
        heads.add("异常类型");
        heads.add("一级原因");
        heads.add("异常提交人");
        heads.add("异常提交时间");
        resList.add(heads);

        List<AbnormalDisposeSend> rows = Lists.newArrayList();
        int totalPage = 1;//路由中总页数
        int currPage = 0;//翻页控制
        //如果路由系统已经最后一页了，就不要再拉数据了
        while (currPage < totalPage) {
            currPage++;//自动翻页查询
            if (currPage>100){
                break;//防止路由系统有BUG，导致我们循环
            }
            //调用路由接口查明细
            PageDto<TransferWaveMonitorDetailResp> page = new PageDto<TransferWaveMonitorDetailResp>();
            page.setPageSize(100);
            page.setCurrentPage(currPage);
            PageDto<TransferWaveMonitorDetailResp> noSendDetail = vrsRouteTransferRelationManager.getNoSendDetail(page, abnormalDisposeCondition.getWaveBusinessId());
            //如果没有明细，下面就不走了
            if (noSendDetail == null) {
                break;
            } else {
                //获取总页数
                totalPage = noSendDetail.getTotalPage();
                if (noSendDetail.getTotalRow() > DEFAULT_MAX_NUM) {
                    break;//查过阈值，就不拉了，数据量太大，有风险
                }
            }
            if (noSendDetail.getResult() == null || noSendDetail.getResult().size() == 0) {
                break;
            }

            //整理所有的运单号，后面批量查询 路由 异常等
            ArrayList<String> waybillCodeList = new ArrayList();
            for (TransferWaveMonitorDetailResp detailResp : noSendDetail.getResult()) {
                waybillCodeList.add(detailResp.getWaybillCode());
            }
            //查询路由信息（查ver）
            Map<String, String> routerMap = null;

            //查询已处理的明细 外呼
            List<AbnormalOrder> abnormalOrders = abnormalOrderDao.queryByWaveIdAndWaybillCodes(abnormalDisposeCondition.getWaveBusinessId(), waybillCodeList);
            Map<String, AbnormalOrder> abnormalOrdersMap = convertAbnormalOrdersMap(abnormalOrders);
            //查询已处理的明细 异常
            List<AbnormalWayBill> abnormalWayBills = abnormalWayBillDao.queryByWaveIdAndWaybillCodes(abnormalDisposeCondition.getWaveBusinessId(), waybillCodeList);
            Map<String, AbnormalWayBill> abnormalWayBillsMap = convertAbnormalWaybillsMap(abnormalWayBills);
            //查验货时间
            List<Inspection> inspectionList = inspectionDao.findOperateTimeByWaybillCodes(currSite.getSiteCode(), waybillCodeList);
            Map<String, Inspection> inspectionWayBillsMap = convertInspectionWayBillsMap(inspectionList);

            for (TransferWaveMonitorDetailResp transferWaveMonitorDetailResp : noSendDetail.getResult()) {
                //想查未处理的,提报过异常的就过滤掉
                if (abnormalDisposeCondition.getIsDispose() != null && abnormalDisposeCondition.getIsDispose().equals(AbnormalDisposeCondition.IS_DISPOSE_NO) && (abnormalOrdersMap.get(transferWaveMonitorDetailResp.getWaybillCode()) != null || abnormalWayBillsMap.get(transferWaveMonitorDetailResp.getWaybillCode()) != null)) {
                    continue;
                }
                //想查已处理的,没提报过异常的就过滤掉
                if (abnormalDisposeCondition.getIsDispose() != null && abnormalDisposeCondition.getIsDispose().equals(AbnormalDisposeCondition.IS_DISPOSE_YES) && (abnormalOrdersMap.get(transferWaveMonitorDetailResp.getWaybillCode()) == null && abnormalWayBillsMap.get(transferWaveMonitorDetailResp.getWaybillCode()) == null)) {
                    continue;
                }
                if (routerMap == null) {//放在这里，避免当前页都要过滤掉时，没必要的调用ver
                    routerMap = jsfSortingResourceService.getRouterByWaybillCodes(waybillCodeList);
                }
                rows.add(convertAbnormalDisposeSend(currSite, routerMap, abnormalOrdersMap, abnormalWayBillsMap, inspectionWayBillsMap, transferWaveMonitorDetailResp));
            }
        }
        if (rows != null && rows.size() > 0) {
            for (AbnormalDisposeSend abnormalDisposeSend : rows) {
                List<Object> body = Lists.newArrayList();
                body.add(DateHelper.formatDate(abnormalDisposeSend.getInspectionDate(), Constants.DATE_TIME_FORMAT));//验货时间
                body.add(abnormalDisposeSend.getInspectionSiteName());//验货分拣中心
                body.add(abnormalDisposeSend.getWaybillCode());//运单号
                body.add(abnormalDisposeSend.getNextAreaName());
                body.add(abnormalDisposeSend.getNextSiteName());
                body.add(abnormalDisposeSend.getEndCityName());
                body.add(abnormalDisposeSend.getEndSiteName());
                body.add(AbnormalDisposeCondition.IS_DISPOSE_YES.equals(abnormalDisposeSend.getIsDispose()) ? "是" : "否");
                body.add(abnormalDisposeSend.getAbnormalType() == null ? "" : AbnormalDisposeSend.ABNORMAL_TYPE_1.equals(abnormalDisposeSend.getAbnormalType()) ? "外呼" : "异常");
                body.add(abnormalDisposeSend.getAbnormalReason1());
                body.add(abnormalDisposeSend.getCreateUser());
                body.add(DateHelper.formatDate(abnormalDisposeSend.getCreateTime(), Constants.DATE_TIME_FORMAT));
                resList.add(body);
            }
        }
        return resList;
    }

    /**
     * list 转 map
     *
     * @param abnormalWayBills
     * @return
     */
    private Map<String, AbnormalWayBill> convertAbnormalWaybillsMap(List<AbnormalWayBill> abnormalWayBills) {
        Map<String, AbnormalWayBill> abnormalWayBillsMap = Maps.newHashMap();
        if (abnormalWayBills != null && abnormalWayBills.size() > 0) {
            for (AbnormalWayBill abnormalWayBill : abnormalWayBills) {
                abnormalWayBillsMap.put(abnormalWayBill.getWaybillCode(), abnormalWayBill);
            }
        }
        return abnormalWayBillsMap;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalDisposeServiceImpl.saveAbnormalQc", mState = {JProEnum.TP})
    public JdResponse<String> saveAbnormalQc(AbnormalDisposeInspection abnormalDisposeInspection,LoginUser loginUser) {
        Date date = new Date();
        if (!abnormalDisposeInspection.getQcCode().startsWith(QC_FLAG)){
            return new JdResponse<String>(JdResponse.CODE_FAIL, "格式不正确，应为QC开头的编码，请重新提交");
        }
        //获取操作人信息封装数据
        BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(loginUser.getUserErp());
        AbnormalQc abnormalQc = new AbnormalQc();
        abnormalQc.setWaveBusinessId(abnormalDisposeInspection.getWaveBusinessId());
        abnormalQc.setWaybillCode(abnormalDisposeInspection.getWaybillCode());
        abnormalQc.setCreateUserCode(userDto.getStaffNo());
        abnormalQc.setCreateUserErp(userDto.getAccountNumber());
        abnormalQc.setCreateUser(userDto.getStaffName());
        abnormalQc.setCreateSiteCode(userDto.getSiteCode());
        abnormalQc.setCreateSiteName(userDto.getSiteName());
        abnormalQc.setQcCode(abnormalDisposeInspection.getQcCode());
        abnormalQc.setOperateTime(date);
        abnormalQc.setCreateTime(date);
        abnormalQc.setUpdateTime(date);
        try {
            Integer sum = abnormalQcDao.updateAbnormalQc(abnormalQc);
            if (sum == 0) {
                abnormalQcDao.insertAbnormalQc(abnormalQc);
            }
        } catch (Exception e) {
            log.error("质控编码保存失败,保存参数:{}" , JsonHelper.toJson(abnormalDisposeInspection),e);
            return new JdResponse<String>(JdResponse.CODE_FAIL, JdResponse.MESSAGE_FAIL);
        }
        return new JdResponse<String>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
    }
}
