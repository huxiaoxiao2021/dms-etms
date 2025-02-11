package com.jd.bluedragon.distribution.crossbox.service;

import IceInternal.Ex;
import com.google.common.collect.Sets;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CrossBoxRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.crossbox.dao.CrossBoxDao;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxExportDto;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxMsg;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.*;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.jddl.executor.function.scalar.filter.In;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.util.*;

@Service("crossBoxService")
public class CrossBoxServiceImpl implements CrossBoxService {

    private static final Logger log = LoggerFactory.getLogger(CrossBoxServiceImpl.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private CrossBoxDao crossBoxDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BoxService boxService;

    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;
    @Autowired
    private BaseMajorManager baseSiteManager;
    @Autowired
    @Qualifier("crossBoxSendProducer")
    private DefaultJMQProducer crossBoxSendProducer;
    @Override
    public int addCrossBox(CrossBox crossBox) {
        getFullLine(crossBox);
        return crossBoxDao.addCrossBox(crossBox);
    }

    private String getDmsNameForLine(Integer dmsCode) {
        StringBuffer sf = null;
        if (dmsCode != null) {
            sf = new StringBuffer();
            BaseStaffSiteOrgDto result = baseSiteManager.getBaseSiteBySiteId(dmsCode);
            if (result != null) {
                if (StringUtils.isNotBlank(result.getDmsShortName())) {
                    sf.append(result.getDmsShortName());
                } else {
                    sf.append(result.getSiteName());
                }
                sf.append("--");
            }
            return sf.toString();
        } else {
            return null;
        }
    }
    @Override
    public String getFullLine(CrossBox crossDmsBox) {
        StringBuffer fullLineBuffer = new StringBuffer();
        String originalDms = null;
        String destinationDms = null;
        String transfer1 = null;
        String transfer2 = null;
        String transfer3 = null;
        if (crossDmsBox.getOriginalDmsId() != null) {
            originalDms = getDmsNameForLine(crossDmsBox.getOriginalDmsId());
        }
        if (crossDmsBox.getDestinationDmsId() != null) {
            destinationDms = getDmsNameForLine(crossDmsBox.getDestinationDmsId());
        }
        if (crossDmsBox.getTransferOneId() != null) {
            transfer1 = getDmsNameForLine(crossDmsBox.getTransferOneId());
        }
        if (crossDmsBox.getTransferTwoId() != null) {
            transfer2 = getDmsNameForLine(crossDmsBox.getTransferTwoId());
        }
        if (crossDmsBox.getTransferThreeId() != null) {
            transfer3 = getDmsNameForLine(crossDmsBox.getTransferThreeId());
        }
        if (StringUtils.isNotBlank(originalDms)) {
            fullLineBuffer.append(originalDms);
        }
        if (StringUtils.isNotBlank(transfer1)) {
            fullLineBuffer.append(transfer1);
        }
        if (StringUtils.isNotBlank(transfer2)) {
            fullLineBuffer.append(transfer2);
        }
        if (StringUtils.isNotBlank(transfer3)) {
            fullLineBuffer.append(transfer3);
        }
        if (StringUtils.isNotBlank(destinationDms)) {
            fullLineBuffer.append(destinationDms);
        }
        String temp = fullLineBuffer.toString();
        String fullline = temp.substring(0, temp.length() - 2);
        crossDmsBox.setFullLine(fullline);
        return fullline;
    }

    @Override
    public String checkLineExist(CrossBox crossBox) {
        return crossBoxDao.checkLineExist(crossBox);
    }

    @Override
    public List<CrossBox> queryByCondition(CrossBoxRequest crossBoxRequest, Pager<List<CrossBox>> page) {
        // 拼接时间查询条件
        String updateOperatorName = crossBoxRequest.getUpdateOperatorName();
        if (updateOperatorName != null && !updateOperatorName.equals("")) {
            crossBoxRequest.setUpdateOperatorName("%" + updateOperatorName + "%");
        }

        String startDate = crossBoxRequest.getStartDate();
        String endDate = crossBoxRequest.getEndDate();
        if (StringUtils.isNotEmpty(startDate)) {
            crossBoxRequest.setStartDate(startDate + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(endDate)) {
            crossBoxRequest.setEndDate(endDate + " 23:59:59");
        }

        Map<String, Object> params = ObjectMapHelper.makeObject2Map(crossBoxRequest);
        int count = crossBoxDao.countByCondition(params);
        if (page == null) {
            page = new Pager<List<CrossBox>>();
        }
        List<CrossBox> list = null;
        if (count > 0) {
            page.setTotalSize(count);
            page.init();
            crossBoxRequest.setStartIndex(page.getStartIndex());
            crossBoxRequest.setEndIndex(page.getEndIndex());

            params = ObjectMapHelper.makeObject2Map(crossBoxRequest);
            params.put("pageSize",page.getPageSize());
            list = crossBoxDao.queryByCondition(params);
        }
        return list;
    }

    public List<CrossBox> queryByConditionForExport(CrossBoxRequest crossBoxRequest) {
        String updateOperatorName = crossBoxRequest.getUpdateOperatorName();
        if (updateOperatorName != null && !updateOperatorName.equals("")) {
            crossBoxRequest.setUpdateOperatorName("%" + updateOperatorName + "%");
        }
        // 拼接时间查询条件
        String startDate = crossBoxRequest.getStartDate();
        String endDate = crossBoxRequest.getEndDate();
        if (StringUtils.isNotEmpty(startDate)) {
            crossBoxRequest.setStartDate(startDate + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(endDate)) {
            crossBoxRequest.setEndDate(endDate + " 23:59:59");
        }

        List<CrossBox> list = null;
        Map<String, Object> params = ObjectMapHelper.makeObject2Map(crossBoxRequest);
        list = crossBoxDao.queryByCondition(params);
        return list;
    }

    @Override
    public int deleteById(CrossBox crossDmsBox) {
        return crossBoxDao.deleteById(crossDmsBox);
    }

    @Override
    public int updateCrossBoxByDms(CrossBox crossDmsBox) {
        log.info(JsonHelper.toJson(crossDmsBox));
        getFullLine(crossDmsBox);

        Integer num = crossBoxDao.updateCrossBoxByDms(crossDmsBox);
        try {
            CrossBoxMsg crossBoxMsg = new CrossBoxMsg();
            crossBoxMsg.setSiteCode(crossDmsBox.getDestinationDmsId());
            crossBoxMsg.setTs(System.currentTimeMillis());
            crossBoxSendProducer.send(crossDmsBox.getDestinationDmsId().toString(),JsonHelper.toJson(crossBoxMsg));
        } catch (Exception e) {
            log.error("更新箱号缓存出错:", e);
        }
        return num;
    }

    private int updateCrossBoxById(CrossBox crossDmsBox) {
        getFullLine(crossDmsBox);
        log.info(JsonHelper.toJson(crossDmsBox));
        return crossBoxDao.updateCrossBoxById(crossDmsBox);
    }

    @Override
    public CrossBox getCrossBoxById(Integer id) {
        return crossBoxDao.getCrossBoxById(id);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String handleCrossBoxData(List<CrossBox> dataList, String userName) {
        String result = null;

        for (CrossBox cdBox : dataList) {
            // 查找是否存在从此始发分拣中心到此目的分拣中心的线路
            CrossBox resCdBox = crossBoxDao.selectActiveCrossBoxByDmsId(cdBox);
            // 如果不存在 增加记录
            if (null == resCdBox) {
                // 判断如果不是立即生效的
                if (cdBox.getYn() != 1 && cdBox.getEffectiveDate().after(new Date())) {
                    taskService.add(this.toTask(cdBox));
                    cdBox.setYn(2);// 未启动的任务
                } else {
                    cdBox.setYn(1);
                }
                cdBox.setCreateTime(new Date());
                cdBox.setUpdateTime(new Date());
                crossBoxDao.addCrossBox(cdBox);
            } else {
                // 验证是否立即生效 如果是立即执行 更新原记录
                if (cdBox.getEffectiveDate().before(new Date())) {
                    if (resCdBox.getEffectiveDate() == null) {
                        cdBox.setYn(1);
                        cdBox.setUpdateTime(new Date());
                        crossBoxDao.updateCrossBoxByDms(cdBox);
                    } else if (cdBox.getEffectiveDate().after(resCdBox.getEffectiveDate())) {
                        cdBox.setYn(1);
                        cdBox.setUpdateTime(new Date());
                        crossBoxDao.updateCrossBoxByDms(cdBox);
                    } else {
                        cdBox.setYn(2);
                        cdBox.setCreateTime(new Date());
                        cdBox.setUpdateTime(new Date());
                        crossBoxDao.addCrossBox(cdBox);
                    }
                } else {
                    if (cdBox.getYn() == 1) {
                        cdBox.setYn(1);
                        cdBox.setEffectiveDate(new Date());
                        cdBox.setUpdateTime(new Date());
                        crossBoxDao.updateCrossBoxByDms(cdBox);
                    } else {
                        cdBox.setYn(2);
                        cdBox.setCreateTime(new Date());
                        cdBox.setUpdateTime(new Date());
                        crossBoxDao.addCrossBox(cdBox);
                        taskService.add(this.toTask(cdBox));
                    }
                }
            }
        }
        return result;
    }

    private Task toTask(CrossBox crossDmsBox) {
        Task task = new Task();

        task.setCreateSiteCode(crossDmsBox.getOriginalDmsId());//用createSitcode存储始发分拣中心的Id
        task.setReceiveSiteCode(crossDmsBox.getDestinationDmsId());//用receiveSitcode存储目的分拣中心的Id
        task.setTableName(Task.TABLE_NAME_CROSSBOX);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));

        task.setBody(JsonHelper.toJson(crossDmsBox));
        task.setType(Task.TASK_TYPE_CROSS_BOX);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int activeCrossBox(Task task) {
        int res = 1;
        CrossBox crossDmsBox = JsonHelper.fromJson(task.getBody(), CrossBox.class);
        if (crossDmsBox.getEffectiveDate().after(new Date())) {
            // 没到生效日期，退出
            res = 2;
            return res;
        }
        // 判断当前业务表cross_box中是否已经存在该路线
        CrossBox queryBox = crossBoxDao.selectActiveCrossBoxByDmsId(crossDmsBox);

        // 如果已经存在
        if (queryBox != null) {
            queryBox.setYn(2);
            queryBox.setUpdateTime(new Date());
            queryBox.setExpiryDate(new Date());
            queryBox.setRemark("失效");
            crossBoxDao.updateYnCrossBoxById(queryBox);
        }

        CrossBox updateBox = crossBoxDao.selectInactiveCrossBoxByDmsId(crossDmsBox);
        updateBox.setUpdateTime(new Date());
        updateBox.setYn(1);
        int resUpdate = crossBoxDao.updateYnCrossBoxById(updateBox);
        if (resUpdate <= 0) {
            res = 3;
            log.warn("执行updateCrossDmsBoxById失败,OriginalDmsId[{}]",crossDmsBox.getOriginalDmsId());
        }
        return res;
    }

    @Override
    public CrossBoxResult<CrossBox> getCrossDmsBoxByOriAndDes(Integer originalDmsId, Integer destinationDmsId) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.BasicSafInterfaceManagerImpl.getCrossDmsBoxByOriAndDes", Constants.UMP_APP_NAME_DMSWEB,false, true);
        CrossBoxResult<CrossBox> result = new CrossBoxResult<CrossBox>();
        try {
            if (null == originalDmsId || null == destinationDmsId) {
                result.setResultCode(result.FAIL);
                result.setData(null);
                result.setMessage("传入参数不合法，请检查必填项值是否设置");
            } else {
                CrossBox crossBox = new CrossBox();
                crossBox.setOriginalDmsId(originalDmsId);
                crossBox.setDestinationDmsId(destinationDmsId);
                CrossBox resCrossDmsBox = crossBoxDao.selectCrossBoxByDmsId(crossBox);
                result.setData(resCrossDmsBox);
                result.setResultCode(result.SUCCESS);
                if (resCrossDmsBox != null) {
                   result.setMessage("已获取到数据并返回");
                } else {
                   result.setMessage("调用成功但在青龙系统中未查询到数据");
                }
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            result.setResultCode(result.FAIL);
            result.setData(null);
            result.setMessage("调用接口异常：" + e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    @Override
    public void export(CrossBoxRequest crossBoxRequest, BufferedWriter bufferedWriter) {
        try {
            long start = System.currentTimeMillis();
            // 写入表头
            Map<String, String> headerMap = getHeaderMap();
            CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());

            // 设置总导出数据
            Integer maxSize = exportConcurrencyLimitService.uccSpotCheckMaxSize();
            Integer oneQuery = exportConcurrencyLimitService.getOneQuerySizeLimit();

            // 设置单次查询数据库条数限制
            crossBoxRequest.setPageSize(oneQuery);
            int queryTotal = 0;
            int index = 1;
            while (index <= (maxSize/oneQuery)+1 ){
                crossBoxRequest.setStartIndex((index-1) * oneQuery);
                index++;
                List<CrossBox> dataList = this.queryByConditionForExport(crossBoxRequest);
                if (CollectionUtils.isEmpty(dataList)) {
                    break;
                }

               List<CrossBoxExportDto> exportDtoList   = transForm(dataList);
                // 输出至csv文件中
                CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, exportDtoList);
                // 限制导出数量
                queryTotal += exportDtoList.size();
                if(queryTotal > maxSize){
                    break;
                }
            }
            long end = System.currentTimeMillis();
            exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(crossBoxRequest), ExportConcurrencyLimitEnum.CROSS_BOX_REPORT.getName(),end-start,queryTotal);
        }catch (Exception e){
            log.error("跨箱号中转维护导出结果异常",e);
        }
    }

    @Override
    public void updateSiteName(Integer siteCode, String siteName) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.CrossBoxServiceImpl.updateSiteName", Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            int currentPage = 1;
            int pageSize = 1000;
            int count = crossBoxDao.countCrossBoxBySiteCode(siteCode);
            if (count>0){
                Pager page = new Pager<>(currentPage,pageSize);
                page.setTotalSize(count);
                int totalNo = page.getTotalNo();
                HashSet<Integer> clearBoxSites = Sets.newHashSet();
                log.info("分页修改数据，总页数："+page.getTotalNo());
                while(page.getPageNo() <= totalNo){
                    log.info("分页修改数据，页码："+page.getPageNo()+" ,参数["+page.getStartIndex()+","+page.getPageSize()+"]");
                    updateSiteNameByPage(page,siteCode, siteName,clearBoxSites);
                    page = new Pager<>(page.getPageNo()+1,pageSize);
                }
                for (Integer code:clearBoxSites){
                    CrossBoxMsg crossBoxMsg = new CrossBoxMsg();
                    crossBoxMsg.setSiteCode(code);
                    crossBoxMsg.setTs(System.currentTimeMillis());
                    crossBoxSendProducer.send(siteCode.toString(),JsonHelper.toJson(crossBoxMsg));
                }
            }
        }catch (Exception ex){
            log.error("执行updateSiteName失败,siteCode:"+siteCode+",siteName:"+siteName);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    private void updateSiteNameByPage(Pager page,Integer siteCode, String siteName,HashSet<Integer> clearBoxSites) {
        //更新站点名称
        List<CrossBox> queryList = crossBoxDao.selectCrossBoxBySiteCode(page.getStartIndex(),page.getPageSize(),siteCode);
        for (CrossBox crossBox : queryList) {
            if (crossBox.getOriginalDmsId() != null && crossBox.getOriginalDmsId().intValue() == siteCode.intValue()) {
                crossBox.setOriginalDmsName(siteName);
            }

            if (crossBox.getDestinationDmsId() != null && crossBox.getDestinationDmsId().intValue() == siteCode.intValue()) {
                crossBox.setDestinationDmsName(siteName);
            }

            if (crossBox.getTransferOneId() != null && crossBox.getTransferOneId().intValue() == siteCode.intValue()) {
                crossBox.setTransferOneName(siteName);
            }

            if (crossBox.getTransferTwoId() != null && crossBox.getTransferTwoId().intValue() == siteCode.intValue()) {
                crossBox.setTransferTwoName(siteName);
            }

            if (crossBox.getTransferThreeId() != null && crossBox.getTransferThreeId().intValue() == siteCode.intValue()) {
                crossBox.setTransferThreeName(siteName);
            }
            updateCrossBoxById(crossBox);
            clearBoxSites.add(crossBox.getDestinationDmsId());
        }
    }

    private List<CrossBoxExportDto> transForm(List<CrossBox> dataList) {
        List<CrossBoxExportDto> list = new ArrayList<CrossBoxExportDto>();
        for (CrossBox item : dataList) {
            CrossBoxExportDto body = new CrossBoxExportDto();
            body.setId(item.getId());
            body.setOriginalDmsId(item.getOriginalDmsId());
            body.setOriginalDmsName(item.getOriginalDmsName());
            body.setTransferOneId(item.getTransferOneId());
            body.setTransferOneName(item.getTransferOneName());
            body.setTransferTwoId(item.getTransferTwoId());
            body.setTransferTwoName(item.getTransferTwoName());
            body.setTransferThreeId(item.getTransferThreeId());
            body.setTransferThreeName(item.getTransferThreeName());
            body.setDestinationDmsId(item.getDestinationDmsId());
            body.setDestinationDmsName(item.getDestinationDmsName());
            body.setFullLine(item.getFullLine());
            body.setUpdateTime(item.getUpdateTime() == null ? "" : DateHelper.formatDateTime(item.getUpdateTime()));
            body.setUpdateOperatorName(item.getUpdateOperatorName());
            body.setEffectiveDate(item.getEffectiveDate() == null ? "" : DateHelper.formatDateTime(item.getEffectiveDate()));
            list.add(body);
        }
        return list;
    }

    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("id","序号");
        headerMap.put("originalDmsId","始发分拣中心_Id");
        headerMap.put("originalDmsName","始发分拣中心");
        headerMap.put("transferOneId","中转1_Id");
        headerMap.put("transferOneName","中转1");
        headerMap.put("transferTwoId","中转2_Id");
        headerMap.put("transferTwoName","中转2");
        headerMap.put("transferThreeId","中转3_Id");
        headerMap.put("transferThreeName","中转3");
        headerMap.put("destinationDmsId","目的分拣中心_Id");
        headerMap.put("destinationDmsName","目的分拣中心");
        headerMap.put("fullLine","完整线路");
        headerMap.put("updateTime","维护(更新)时间");
        headerMap.put("updateOperatorName","维护人员");
        headerMap.put("effectiveDate","生效时间");
        return  headerMap;
    }

    /**
     * 查询分拣系统中的路由信息
     * @param originalDmsId
     * @param destinationDmsId
     * @return
     */
    @Override
    public CrossBoxResult<String> getCrossDmsBox(Integer originalDmsId, Integer destinationDmsId) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.CrossBoxServiceImpl.getCrossDmsBox", Constants.UMP_APP_NAME_DMSWEB,false, true);
        CrossBoxResult<String> result = new CrossBoxResult<String>();
        try {
            if (originalDmsId == null || destinationDmsId == null) {
                result.setData(null);
                result.setResultCode(result.FAIL);
                result.setMessage("调用失败,参数不能为空");
                return result;
            }
            CrossBox crossBox = new CrossBox();
            crossBox.setOriginalDmsId(originalDmsId);
            crossBox.setDestinationDmsId(destinationDmsId);

            String fullLine = crossBoxDao.getFullLineByDmsId(crossBox);
            result.setResultCode(result.SUCCESS);
            if (StringUtils.isNotBlank(fullLine)) {
                result.setData(fullLine);
               result.setMessage("调用成功,数据已成功获取并返回");
            } else {
                result.setData(null);
               result.setMessage("调用成功，但未查询到数据，请判断参数是否正确");
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            result.setData(null);
            result.setResultCode(result.FAIL);
            result.setMessage("调用接口异常：" + e);
            log.error("根据始发和目的分拣中心ID获取路线信息:", e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 查询路由信息
     * 先从路由系统查，没有再从分拣系统查
     * @param originalDmsId
     * @param destinationDmsId
     * @param predictSendTime
     * @param transportType
     * @return String[0] 路由站点的名称 String[1] 路由站点的ID
     */
    @Override
    public CrossBoxResult<String[]> getBoxRouter(Integer originalDmsId, Integer destinationDmsId, Date predictSendTime, Integer transportType) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.CrossBoxServiceImpl.getBoxRouter", Constants.UMP_APP_NAME_DMSWEB,false, true);
        CrossBoxResult<String[]> result = new CrossBoxResult<String[]>(CrossBoxResult.SUCCESS,null,"成功");
        try {
            if (originalDmsId == null || destinationDmsId == null) {
                result.setData(null);
                result.setResultCode(result.FAIL);
                result.setMessage("调用失败,参数不能为空");
                return result;
            }
            //如果有预发时间
            if (predictSendTime!=null && transportType!=null){
                if (getRemoteRouter(result,originalDmsId,destinationDmsId,predictSendTime,transportType)){
                    log.info("查询CrossRouter-originalDmsId[{}]destinationDmsId[{}]predictSendTime[{}]transportType[{}]result[{}]",originalDmsId,destinationDmsId,DateHelper.formatDate(predictSendTime),transportType,JsonHelper.toJson(result));
                    return result;
                }
            }
            //路由系统没返回路由信息  查本地
            if(transportType!=null && (Box.BOX_TRANSPORT_TYPE_HIGHWAY.equals(transportType)|| Box.BOX_TRANSPORT_TYPE_CITY.equals(transportType))){
                getCrossRouter(originalDmsId, destinationDmsId, result);
                log.info("查询CrossRouter-originalDmsId[{}]destinationDmsId[{}]result[{}]",originalDmsId,destinationDmsId,JsonHelper.toJson(result));
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            result.setData(null);
            result.setResultCode(result.FAIL);
            result.setMessage("调用接口异常：" + e);
            log.error("根据始发和目的分拣中心ID获取路线信息:", e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 获取路由站点信息  从分拣系统
     * @param originalDmsId
     * @param destinationDmsId
     * @param result
     * @return
     */
    private boolean getCrossRouter(Integer originalDmsId, Integer destinationDmsId, CrossBoxResult<String[]> result) {
        CrossBoxResult<CrossBox> crossBoxCrossBoxResult= getCrossDmsBoxByOriAndDes(originalDmsId,destinationDmsId);
        if (crossBoxCrossBoxResult!=null && crossBoxCrossBoxResult.getData()!=null&& StringHelper.isNotEmpty(crossBoxCrossBoxResult.getData().getFullLine())){
            StringBuffer siteIdSb=new StringBuffer();
            siteIdSb.append(originalDmsId);
            if (crossBoxCrossBoxResult.getData().getTransferOneId()!=null){
                siteIdSb.append("--").append(crossBoxCrossBoxResult.getData().getTransferOneId());
            }
            if (crossBoxCrossBoxResult.getData().getTransferTwoId()!=null){
                siteIdSb.append("--").append(crossBoxCrossBoxResult.getData().getTransferTwoId());
            }
            if (crossBoxCrossBoxResult.getData().getTransferThreeId()!=null){
                siteIdSb.append("--").append(crossBoxCrossBoxResult.getData().getTransferThreeId());
            }
            siteIdSb.append("--").append(destinationDmsId);

            String[] routerArr=new String[2];
            routerArr[0]= crossBoxCrossBoxResult.getData().getFullLine();
            routerArr[1]=siteIdSb.toString();
            result.setData(routerArr);
            return true;
        }
        return false;
    }

    /**
     * 转换为接口用的参数
     * @param transportType
     * @return
     */
    private RouteProductEnum getRouteProduct(Integer transportType) {
        if (transportType == null) return null;
        switch (transportType) {
            case 1:
                return RouteProductEnum.T2;
            case 2:
            case 3:
                return RouteProductEnum.T1;
            case 4:
                return RouteProductEnum.T4;
            default:
                return null;
        }
    }

    /**
     * 若网点名称中包含“分拣中心”则删除， 例如 北京马驹桥分拣中心 显示为  北京马驹桥
     * @return
     */
    private String converSiteName(String siteName){
        if (StringHelper.isEmpty(siteName)){
            return "";
        }
        if (siteName.indexOf("分拣中心")!=-1){
            return siteName.replace("分拣中心","");
        }else if (siteName.indexOf("分拨中心")!=-1){
            return siteName.replace("分拨中心","");
        }else if (siteName.indexOf("中转场")!=-1){
            return siteName.replace("中转场","");
        }
        return siteName;
    }
    /**
     * 获取路由站点信息  从路由系统
     * @param result
     * @param originalDmsId
     * @param destinationDmsId
     * @param predictSendTime
     * @param transportType
     * @return 返回false 后面会走老逻辑，查cross_box
     */
    private boolean getRemoteRouter(CrossBoxResult<String[]> result, Integer originalDmsId, Integer destinationDmsId, Date predictSendTime, Integer transportType) {
        RouteProductEnum routeProduct = getRouteProduct(transportType);
        if (routeProduct == null) {
            return false;
        }
        BaseStaffSiteOrgDto originalDms=baseMajorManager.getBaseSiteBySiteId(originalDmsId);
        if (originalDms==null){
            return false;
        }
        BaseStaffSiteOrgDto destinationDms=baseMajorManager.getBaseSiteBySiteId(destinationDmsId);
        if (destinationDms==null){
            return false;
        }
        String router=vrsRouteTransferRelationManager.queryRecommendRoute(originalDms.getDmsSiteCode(),destinationDms.getDmsSiteCode(),predictSendTime,routeProduct);

        if (StringUtils.isEmpty(router)){
            return false;
        }
        //拼接路由站点的名称
        StringBuffer fullLineName=new StringBuffer();
        StringBuffer fullLineId=new StringBuffer();
        String[] siteArr=router.split("\\|");
        //有路由节点的话，加上发出和接收节点，数量一定会>2个
        if (siteArr.length<2){
            return false;
        }
        for (int i =0;i<siteArr.length;i++){
            //获取站点信息
            BaseStaffSiteOrgDto baseStaffSiteOrgDto= baseMajorManager.getBaseSiteByDmsCode(siteArr[i]);
            if (baseStaffSiteOrgDto==null){
                result.setData(null);
                result.setResultCode(result.FAIL);
                result.setMessage("远程接口返回异常：站点【"+siteArr[i]+"】不存在" );
                log.warn("路由系统返回的站点路由发现未存在站点：{},参数列表：originalDms.getDmsSiteCode():{},destinationDms.getDmsSiteCode():{},predictSendTime:{},routeProduct:{}"
                        ,siteArr[i],originalDms.getDmsSiteCode(),destinationDms.getDmsSiteCode(),predictSendTime.getTime(),routeProduct);
                return false;
            }
            if (i!=0){
                //第一个不需要连接符
                fullLineName.append("--");
                fullLineId.append("--");
            }
            fullLineName.append(converSiteName(baseStaffSiteOrgDto.getSiteName()));
            fullLineId.append(baseStaffSiteOrgDto.getSiteCode());
        }
        String[] routerArr=new String[2];
        routerArr[0]= fullLineName.toString();
        routerArr[1]=fullLineId.toString();
        result.setData(routerArr);
        return true;

    }
    /**
     * jsf提供的服务，根据箱号查询箱子信息 路由信息
     * @param boxCode
     * @return
     */
    @Override
    public BoxResponse getCrossDmsBoxByBoxCode(String boxCode) {
        Box box = this.boxService.findBoxByCode(boxCode);
        if (box == null) {
            return new BoxResponse(BoxResponse.CODE_BOX_NOT_FOUND, BoxResponse.MESSAGE_BOX_NOT_FOUND);
        }
        BoxResponse response = new BoxResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        response.setSiteType(box.getSiteType());
        response.setBoxCode(box.getCode());
        response.setCreateSiteCode(box.getCreateSiteCode());
        response.setCreateSiteName(box.getCreateSiteName());
        response.setReceiveSiteCode(box.getReceiveSiteCode());
        response.setReceiveSiteName(box.getReceiveSiteName());
        response.setType(box.getType());
        response.setTransportType(box.getTransportType());
        response.setPredictSendTime(box.getPredictSendTime());
        if (StringHelper.isNotEmpty(box.getRouterName())){
            response.setRouterInfo(box.getRouterName().split("\\-\\-"));
        }
        if (StringHelper.isNotEmpty(box.getRouter())){
            response.setRouterFullId(box.getRouter().split("\\-\\-"));
        }
        return response;
    }
}
