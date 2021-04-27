package com.jd.bluedragon.distribution.inventory.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.InventoryJsfManager;
import com.jd.bluedragon.distribution.api.domain.SiteEntity;
import com.jd.bluedragon.distribution.api.request.inventory.InventoryTaskRequest;
import com.jd.bluedragon.distribution.api.response.inventory.InventoryTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.*;
import com.jd.bluedragon.distribution.inventory.service.InventoryTaskService;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.inventory.domain.InventoryDirection;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.util.*;

@Service("inventoryTaskService")
public class InventoryTaskServiceImpl extends BaseService<InventoryTask> implements InventoryTaskService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 盘点任务号前缀
     */
    private static final String INVENTORY_TASK_ID_PREFIX = "PD";

    /**
     * 分隔符-
     */
    private static final String SPLIT_CHAR_STRING = "-";

    /**
     * 继续正在进行中的盘点任务
     */
    private static final Integer WARN_TYPE_TASK_CONTINUE = 1;

    /**
     * 协助别人进行盘点
     */
    private static final Integer WARN_TYPE_TASK_COOPERATE = 2;

    @Autowired
    private InventoryJsfManager inventoryJsfManager;

    @Autowired
    @Qualifier("inventoryTaskDao")
    private InventoryTaskDao inventoryTaskDao;

    @Autowired
    private BaseMajorManager baseMajorManager;
    @Override
    public Dao<InventoryTask> getDao() {
        return this.inventoryTaskDao;
    }

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;


    @Override
    public PagerResult<InventoryTask> queryByPagerCondition(InventoryTaskCondition condition) {
        return inventoryTaskDao.queryByPagerCondition(condition);
    }

    @Override
    public void getExportData(InventoryTaskCondition condition, BufferedWriter bufferedWriter) {
        try {
            long start = System.currentTimeMillis();
            // 报表头
            Map<String, String> headerMap = getHeaderMap();
            //设置最大导出数量
            Integer MaxSize  =  exportConcurrencyLimitService.uccSpotCheckMaxSize();
            Integer oneQuery = exportConcurrencyLimitService.getOneQuerySizeLimit();
            //设置单次导出数量
            condition.setLimit(oneQuery);
            CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());
            int queryTotal = 0;
            int index = 1;
            while (index <= (MaxSize/oneQuery)+1) {
                condition.setOffset((index-1) * oneQuery);
                index++;
                List<InventoryTask> list = inventoryTaskDao.getExportResultByCondition(condition);
                if(CollectionUtils.isEmpty(list)){
                    break;
                }

                List<InventoryTaskExportDto> dataList = transForm(list);
                // 输出至csv文件中
                CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, dataList);
                // 限制导出数量
                queryTotal += dataList.size();
                if(queryTotal > MaxSize ){
                    break;
                }
            }
            long end = System.currentTimeMillis();
            exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(condition), ExportConcurrencyLimitEnum.INVENTORY_TASK_REPORT.getName(), end-start,queryTotal);
        }catch (Exception e){
            log.error("转运清场任务信息表 export error",e);
        }
    }

    private List<InventoryTaskExportDto> transForm(List<InventoryTask> list) {
        List<InventoryTaskExportDto> dataList = new ArrayList<>();
        //表格信息
        for(InventoryTask inventoryTask : list){
            InventoryTaskExportDto inventoryTaskExportDto = new InventoryTaskExportDto();
            inventoryTaskExportDto.setOrgName(inventoryTask.getOrgName());
            inventoryTaskExportDto.setCreateSiteName(inventoryTask.getCreateSiteName());
            inventoryTaskExportDto.setDirectionName(inventoryTask.getDirectionName() == null ? "" : inventoryTask.getDirectionName());
            inventoryTaskExportDto.setInventoryScopeStr(InventoryScopeEnum.getDescByCode(inventoryTask.getInventoryScope()));
            inventoryTaskExportDto.setInventoryTaskId(inventoryTask.getInventoryTaskId());
            inventoryTaskExportDto.setWaybillSum(inventoryTask.getWaybillSum());
            inventoryTaskExportDto.setPackageSum(inventoryTask.getPackageSum());
            inventoryTaskExportDto.setExceptionSum(inventoryTask.getExceptionSum());
            inventoryTaskExportDto.setCreateUserErp((inventoryTask.getCreateUserErp()));
            inventoryTaskExportDto.setHourRange((inventoryTask.getHourRange()));
            inventoryTaskExportDto.setCreateTime(DateHelper.formatDate(inventoryTask.getCreateTime(), Constants.DATE_TIME_FORMAT));
            inventoryTaskExportDto.setEndTime(DateHelper.formatDate(inventoryTask.getEndTime(), Constants.DATE_TIME_FORMAT));
            dataList.add(inventoryTaskExportDto);
        }
        return dataList;
    }

    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("orgName","区域");
        headerMap.put("createSiteName","操作场地");
        headerMap.put("directionName","盘点范围");
        headerMap.put("inventoryScopeStr","下游场地");
        headerMap.put("inventoryTaskId","任务码");
        headerMap.put("waybillSum","运单数");
        headerMap.put("packageSum","包裹数");
        headerMap.put("exceptionSum","差异数");
        headerMap.put("createUserErp","盘点erp");
        headerMap.put("hourRange","时间范围（小时）");
        headerMap.put("createTime","创建时间");
        headerMap.put("endTime","完成时间");
        return headerMap;
    }

    /**
     * 获取当前分拣中心的盘点的流向
     * @param createSiteCode
     * @return
     */
    public List<SiteEntity>  getInventoryDirectionList(Integer createSiteCode){
        List<SiteEntity> directionList = new ArrayList<>();
        BaseEntity<List<InventoryDirection>> baseEntity= inventoryJsfManager.queryInventoryDirectionList(createSiteCode);
        log.info("调用报表jsf接口获取盘点流向信息.参数：{}.返回值为:{}",createSiteCode, JSON.toJSONString(baseEntity));
        if(baseEntity!= null && baseEntity.getData() != null){
            for(InventoryDirection direction : baseEntity.getData()){
                SiteEntity siteEntity = new SiteEntity();
                siteEntity.setCode(direction.getDirectionCode());
                siteEntity.setName(direction.getDirectionName());

                directionList.add(siteEntity);
            }
        }
        return directionList;
    }

    /**
     * 查询当前操作人正在进行的任务
     * @param request
     * @return
     */
    public JdResult<InventoryTaskResponse> getUserDoingInventoryTask(InventoryTaskRequest request) {
        JdResult<InventoryTaskResponse> result = new JdResult<>();
        result.toSuccess();

        if (request == null) {
            result.toError("请求参数不能为空.");
            return result;
        }

        if (request.getUserCode() == null || request.getUserCode() <= 0 || request.getSiteCode() == null || request.getSiteCode() <= 0) {
            result.toError("操作站点和操作人不能为空.");
        }

        //查询当前操作人正在进行的任务，如果有则进行提示
        List<InventoryTask> inventoryTaskList = inventoryTaskDao.getInventoryTaskByCreateUser(request.getSiteCode(), request.getUserCode());
        if (inventoryTaskList != null && inventoryTaskList.size() > 0) {
            InventoryTaskResponse response = new InventoryTaskResponse();
            String doingDirectionStr = buildResponse(inventoryTaskList, response);
            response.setWarnType(WARN_TYPE_TASK_CONTINUE);
            result.setData(response);
            result.toWarn("存在未完成的任务，请先完成.盘点卡位[" + doingDirectionStr + "].是否继续该任务？");
            return result;
        }

        return result;
    }

    /**
     * 验证所选流向是否有正在进行的任务
     *
     * @param request
     * @return
     */
    public JdResult<InventoryTaskResponse> directionVerify(InventoryTaskRequest request) {
        JdResult<InventoryTaskResponse> result = new JdResult<>();
        result.toSuccess();

        if (request == null) {
            result.toError("请求参数不能为空.");
            return result;
        }

        //先完同一操作人的校验
        result = getUserDoingInventoryTask(request);
        if (InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
            return result;
        }

        //查询是否有相同流向/范围的正在进行的任务
        List<Integer> directionCodeList = getDirectionCodeList(request.getDirectionList());

        List<InventoryTask> inventoryTaskList = inventoryTaskDao.getInventoryTaskByDirectionOrScope(request.getSiteCode(), directionCodeList, request.getInventoryScope());

        if (inventoryTaskList != null && inventoryTaskList.size() > 0) {
            String createUserErp = inventoryTaskList.get(0).getCreateUserErp();
            InventoryTaskResponse response = new InventoryTaskResponse();
            buildResponse(inventoryTaskList,response);
            response.setWarnType(WARN_TYPE_TASK_COOPERATE);
            result.setData(response);
            result.toWarn(createUserErp + "正在对该卡位进行盘点，是否协助加入？");
            return result;
        }
        return result;
    }

    /**
     * 生成盘点任务
     * 根据操作站点和操作人判断是否有该操作人正在进行的任务，如果有则返回任务号，并提示；
     * 没有则创建新的盘点任务，并写入盘点任务表
     *
     * @param request
     * @return
     */
    public JdResult<InventoryTaskResponse> addInventoryTask(InventoryTaskRequest request) {
        JdResult<InventoryTaskResponse> result = new JdResult<>();
        result.toSuccess();

        if (request == null) {
            result.toError("请求参数不能为空.");
            return result;
        }

        if (request.getSiteCode() == null || request.getSiteCode() <= 0 || request.getUserCode() == null || request.getUserCode() <= 0) {
            result.toError("操作站点和操作人不能为空.");
            return result;
        }

//        if (request.getHourRange() == null || request.getHourRange() <= 0) {
//            result.toError("盘点时间范围必须大于0");
//            return result;
//        }

        List<InventoryTask> inventoryTaskList = null;
        InventoryTaskResponse response = new InventoryTaskResponse();

        //1.如果有任务号:(1)继续自己之前的任务（2）协助别人完成盘点
        if (StringUtils.isNotBlank(request.getInventoryTaskId())) {
            //判断任务是否已经结束
            inventoryTaskList = inventoryTaskDao.getInventoryTaskByTaskId(request.getInventoryTaskId());
            if (inventoryTaskList == null || inventoryTaskList.size() < 1) {
                //任务已经结束了
                result.toError("该盘点任务已经结束.请重新选择盘点范围和盘点卡位创建新的盘点任务.");
                return result;
            }

            //有任务号并且协作类型是协作者，在inventory_task表里插入协助数据
            if (CooperateTypeEnum.COOPERATOR.getCode().equals(request.getCooperateType())) {
                List<SiteEntity> directionList = new ArrayList<>();
                for (InventoryTask task : inventoryTaskList) {
                    task.setCreateUserCode(request.getUserCode());
                    task.setCreateUserErp(request.getUserErp());
                    task.setCreateUserName(request.getUserName());
                    task.setCooperateType(request.getCooperateType());
                    directionList.add(new SiteEntity(task.getDirectionCode(), task.getDirectionName()));

                    //如果当前人对当前任务有过协助，则更新updateTime，否则写入
                    if (!inventoryTaskDao.updateTime(task)) {
                        inventoryTaskDao.insert(task);
                    }
                }
                response.setDirectionList(directionList);
            }
            else{
                //协作类型为创建者的直接返回成功
                response.setDirectionList(request.getDirectionList());
            }

            response.setInventoryScope(request.getInventoryScope());
            response.setInventoryTaskId(request.getInventoryTaskId());

            result.setData(response);
            return result;
        }

        //2.没有任务号先完成校验--（1）同一操作人的校验；（2）相同流向的校验
        result = getUserDoingInventoryTask(request);
        if (InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
            return result;
        }
        result = directionVerify(request);
        if (InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
            return result;
        }

        //3.没有正在进行的可以创建新的任务
        String inventoryTaskId = generateInventoryTaskId(request.getSiteCode());

        inventoryTaskList = new ArrayList<>();
        InventoryTask inventoryTaskBasic = new InventoryTask();
        try {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(request.getSiteCode());
            if (baseStaffSiteOrgDto != null) {
                inventoryTaskBasic.setOrgId(baseStaffSiteOrgDto.getOrgId());
                inventoryTaskBasic.setOrgName(baseStaffSiteOrgDto.getOrgName());
            }

        } catch (Exception e) {
            log.error("获取【{}】的基础资料信息失败！",request.getSiteName(),e);
        }
        inventoryTaskBasic.setInventoryTaskId(inventoryTaskId);
        inventoryTaskBasic.setCreateSiteCode(request.getSiteCode());
        inventoryTaskBasic.setCreateSiteName(request.getSiteName());
        inventoryTaskBasic.setCreateUserCode(request.getUserCode());
        inventoryTaskBasic.setCreateUserErp(request.getUserErp());
        inventoryTaskBasic.setCreateUserName(request.getUserName());
        Date date = new Date();
        if (request.getHourRange() != null && request.getHourRange() > 0) {
            inventoryTaskBasic.setHourRange(request.getHourRange());
            inventoryTaskBasic.setHourRangeTime(DateHelper.newTimeRangeHoursAgo(date, request.getHourRange()));
        }
        inventoryTaskBasic.setCreateTime(date);
        inventoryTaskBasic.setCooperateType(request.getCooperateType());
        inventoryTaskBasic.setInventoryScope(request.getInventoryScope());
        inventoryTaskBasic.setStatus(InventoryTaskStatusEnum.DOING.getStatusCode());

        if (request.getDirectionList() == null || request.getDirectionList().size() < 1) {
            inventoryTaskList.add(inventoryTaskBasic);
        } else {
            for (SiteEntity receiveSite : request.getDirectionList()) {
                InventoryTask inventoryTask = new InventoryTask();
                BeanHelper.copyProperties(inventoryTask, inventoryTaskBasic);
                inventoryTask.setDirectionCode(receiveSite.getCode());
                inventoryTask.setDirectionName(receiveSite.getName());

                inventoryTaskList.add(inventoryTask);
            }
        }

        inventoryTaskDao.addBatch(inventoryTaskList);

        response.setDirectionList(request.getDirectionList());
        response.setInventoryScope(request.getInventoryScope());
        response.setInventoryTaskId(inventoryTaskId);

        result.setData(response);

        return result;
    }

    /**
     * 生成任务号
     * PD+createSiteCode+ md5（目的地列表） +时间戳
     *
     * @param createSiteCode
     * @return
     */
    //改成大写
    private String generateInventoryTaskId(Integer createSiteCode) {
        UUID uuid = UUID.randomUUID();
        String taskId = uuid.toString().replaceAll(SPLIT_CHAR_STRING, "");
        //加上前缀和始发
        taskId = INVENTORY_TASK_ID_PREFIX + createSiteCode + SPLIT_CHAR_STRING + taskId;
        //转换成大写
        taskId = taskId.toUpperCase();
        return taskId;
    }

    /**
     * 获取流向编码列表
     * @param directionList
     * @return
     */
    private List<Integer> getDirectionCodeList(List<SiteEntity> directionList) {
        if (directionList == null || directionList.size() < 1) {
            return null;
        }
        List<Integer> directionCodeList = new ArrayList<>();
        for (SiteEntity site : directionList) {
            directionCodeList.add(site.getCode());
        }
        return directionCodeList;
    }

    /**
     * 构建返回值
     * @param inventoryTaskList
     * @param response
     * @return
     */
    private String buildResponse(List<InventoryTask> inventoryTaskList, InventoryTaskResponse response) {
        List<SiteEntity> directionList = new ArrayList<>();
        String inventoryTaskId = inventoryTaskList.get(0).getInventoryTaskId();
        Integer inventoryScope = inventoryTaskList.get(0).getInventoryScope();

        String doingDirectionStr = "";
        if (InventoryScopeEnum.CUSTOMIZE.getCode().equals(inventoryScope)) {
            doingDirectionStr = inventoryTaskList.get(0).getDirectionName();
            directionList.add(new SiteEntity(inventoryTaskList.get(0).getDirectionCode(), inventoryTaskList.get(0).getDirectionName()));
            if (inventoryTaskList.size() > 1) {
                for (int i = 1; i < inventoryTaskList.size(); i++) {
                    Integer siteCode = inventoryTaskList.get(i).getDirectionCode();
                    String siteName = inventoryTaskList.get(i).getDirectionName();
                    directionList.add(new SiteEntity(siteCode, siteName));
                    doingDirectionStr += "," + siteName;
                }
            }
        } else {
            doingDirectionStr = InventoryScopeEnum.getDescByCode(inventoryScope);
        }

        //组织返回值
        response.setInventoryScope(inventoryScope);
        response.setInventoryTaskId(inventoryTaskId);
        response.setDirectionList(directionList);

        return doingDirectionStr;
    }
}
