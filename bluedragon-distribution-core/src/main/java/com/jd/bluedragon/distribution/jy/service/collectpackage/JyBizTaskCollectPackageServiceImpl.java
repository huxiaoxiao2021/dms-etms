package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackStatusCount;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.boxlimit.BoxLimitConfigManager;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageFlowEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;
import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.dao.collectpackage.JyBizTaskCollectPackageDao;
import com.jd.bluedragon.distribution.jy.dao.collectpackage.JyCollectPackageDao;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CancelCollectPackageDto;
import com.jd.bluedragon.distribution.jy.enums.MixBoxTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.middleend.SortingServiceFactory;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.cache.CacheService;
import com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf;
import com.jdl.basic.api.enums.FlowDirectionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf.COLLECT_CLAIM_MIX;
import static com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf.COLLECT_CLAIM_SPECIFY_MIX;

@Service
@Slf4j
public class JyBizTaskCollectPackageServiceImpl implements JyBizTaskCollectPackageService {
    @Autowired
    JyBizTaskCollectPackageDao jyBizTaskCollectPackageDao;
    @Autowired
    JyCollectPackageDao jyCollectPackageDao;
    @Autowired
    SortingService sortingService;
    @Autowired
    private TaskService taskService;
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;
    @Autowired
    private SortingServiceFactory sortingServiceFactory;

    @Autowired
    private JyBizTaskCollectPackageFlowService jyBizTaskCollectPackageFlowService;

    @Autowired
    private BoxLimitConfigManager boxLimitConfigManager;

    @Override
    public JyBizTaskCollectPackageEntity findByBizId(String bizId) {
        return jyBizTaskCollectPackageDao.findByBizId(bizId);
    }

    @Override
    public JyBizTaskCollectPackageEntity findByBoxCode(String boxCode) {
        return jyBizTaskCollectPackageDao.findByBoxCode(boxCode);
    }

    @Override
    public Boolean save(JyBizTaskCollectPackageEntity record) {
        return jyBizTaskCollectPackageDao.insertSelective(record) > 0;
    }

    @Override
    public List<JyBizTaskCollectPackageEntity> pageQueryTask(JyBizTaskCollectPackageQuery query) {
        return jyBizTaskCollectPackageDao.pageQueryTask(query);
    }

    @Override
    public List<CollectPackStatusCount> queryTaskStatusCount(JyBizTaskCollectPackageQuery query) {
        List<CollectPackStatusCount> statusCounts = jyBizTaskCollectPackageDao.queryTaskStatusCount(query);
        if (statusCounts == null) {
            statusCounts = new ArrayList<>();
        }
        // 将所有的任务状态都补充全
        if (query.getTaskStatusList().size() > statusCounts.size()) {
            List<Integer> taskStatus = new ArrayList<>();

            for (CollectPackStatusCount statusCount : statusCounts) {
                taskStatus.add(statusCount.getTaskStatus());
            }
            for (Integer status : query.getTaskStatusList()) {
                if (!taskStatus.contains(status)) {
                    CollectPackStatusCount collectPackStatusCount = new CollectPackStatusCount();
                    collectPackStatusCount.setTaskStatus(status);
                    collectPackStatusCount.setTotal(0L);
                    statusCounts.add(collectPackStatusCount);
                }
            }
        }
        return statusCounts;
    }

    @Override
    public Boolean updateById(JyBizTaskCollectPackageEntity entity) {
        return jyBizTaskCollectPackageDao.updateByPrimaryKeySelective(entity) > 0;
    }

    @Override
    public Boolean updateStatusByIds(JyBizTaskCollectPackageQuery query) {
        return jyBizTaskCollectPackageDao.updateStatusByIds(query) > 0;
    }

    @Override
    public List<JyBizTaskCollectPackageEntity> findByBizIds(List<String> bizIds) {
        return jyBizTaskCollectPackageDao.findByBizIds(bizIds);
    }

    /**
     * 取消集包方法，用于取消集包操作。
     *
     * @param dto 取消集包的数据传输对象
     * @return 返回取消集包操作的结果，true表示成功取消集包，false表示集包已经被取消或不存在
     */
    @Override
    public boolean cancelJyCollectPackage(CancelCollectPackageDto dto) {
        //先判断一下是否已经被取消-幂等
        if (!checkPackageNoExitOrCanceled(dto)) {
            //执行取消
            execCancel(dto);
            //删除jyCollectpackage扫描记录
            deleteJyCollectPackageRecord(dto);
        }
        return true;
    }

    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean createTaskAndFlowInfo(JyBizTaskCollectPackageEntity newTask, JyBizTaskCollectPackageEntity oldTask) {

        if (oldTask != null) {
            // 逻辑删除原任务
            deleteOldTask(oldTask);
        }
        // 保存箱号任务
        log.info("新增或更新集包任务信息：{}", JsonHelper.toJson(newTask));
        this.save(newTask);

        // 如果支持混装，保存当前流向集合
        if (MixBoxTypeEnum.MIX_ENABLE.getCode().equals(newTask.getMixBoxType())) {
            jyBizTaskCollectPackageFlowService.batchInsert(getMixBoxFlowList(newTask));
        } else {
            // 不支持混装的直接保存包裹目的地
            jyBizTaskCollectPackageFlowService.batchInsert(getBoxFlow(newTask));
        }
        return true;
    }

    private void deleteOldTask(JyBizTaskCollectPackageEntity oldTask) {
        oldTask.setYn(false);
        this.updateById(oldTask);
        List<JyBizTaskCollectPackageFlowEntity> flowList = jyBizTaskCollectPackageFlowService.queryListByBizIds(Collections.singletonList(oldTask.getBizId()));
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(flowList)) {
            List<Long> ids = flowList.stream().map(JyBizTaskCollectPackageFlowEntity::getId).collect(Collectors.toList());
            JyBizTaskCollectPackageQuery query = new JyBizTaskCollectPackageQuery();
            query.setIds(ids);
            query.setUpdateUserErp("system");
            jyBizTaskCollectPackageFlowService.deleteByIds(query);
        }
    }

    private List<JyBizTaskCollectPackageFlowEntity> getBoxFlow(JyBizTaskCollectPackageEntity task) {
        JyBizTaskCollectPackageFlowEntity entity = new JyBizTaskCollectPackageFlowEntity();
        entity.setBoxCode(task.getBoxCode());
        entity.setCreateTime(task.getCreateTime());
        entity.setCreateUserErp(task.getCreateUserErp());
        entity.setStartSiteId(task.getStartSiteId());
        entity.setStartSiteName(task.getStartSiteName());
        entity.setCreateUserName(task.getCreateUserName());
        entity.setCollectPackageBizId(task.getBizId());
        entity.setEndSiteId(task.getEndSiteId());
        entity.setEndSiteName(task.getEndSiteName());
        entity.setUpdateTime(task.getUpdateTime());
        entity.setUpdateUserErp(task.getUpdateUserErp());
        entity.setUpdateUserName(task.getUpdateUserName());
        entity.setYn(Boolean.TRUE);
        return Collections.singletonList(entity);
    }

    /**
     * 查询混装的流向集合（查询混装的集包的流向集合）
     *
     * @return
     */
    private List<JyBizTaskCollectPackageFlowEntity> getMixBoxFlowList(JyBizTaskCollectPackageEntity task) {
        CollectBoxFlowDirectionConf con = assembleCollectBoxFlowDirectionConf(task);
        List<CollectBoxFlowDirectionConf> collectBoxFlowDirectionConfList = boxLimitConfigManager.listCollectBoxFlowDirection(con, Arrays.asList(COLLECT_CLAIM_MIX, COLLECT_CLAIM_SPECIFY_MIX));
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(collectBoxFlowDirectionConfList)) {
            log.info("包裹号: {}未查询到对应目的地的可混装的流向集合！ request：{}", task.getBoxCode(), JsonHelper.toJson(con));
            throw new JyBizException("未查询到对应目的地的可混装的流向集合!");
        }
        return collectBoxFlowDirectionConfList.stream().map(item -> {
            JyBizTaskCollectPackageFlowEntity entity = new JyBizTaskCollectPackageFlowEntity();
            entity.setBoxCode(task.getBoxCode());
            entity.setCreateTime(task.getCreateTime());
            entity.setCreateUserErp(task.getCreateUserErp());
            entity.setStartSiteId(task.getStartSiteId());
            entity.setStartSiteName(task.getStartSiteName());
            entity.setCreateUserName(task.getCreateUserName());
            entity.setCollectPackageBizId(task.getBizId());
            entity.setEndSiteId(item.getEndSiteId().longValue());
            entity.setEndSiteName(item.getEndSiteName());
            entity.setUpdateTime(task.getUpdateTime());
            entity.setUpdateUserErp(task.getUpdateUserErp());
            entity.setUpdateUserName(task.getUpdateUserName());
            entity.setYn(Boolean.TRUE);
            return entity;
        }).collect(Collectors.toList());
    }

    private CollectBoxFlowDirectionConf assembleCollectBoxFlowDirectionConf(JyBizTaskCollectPackageEntity task) {
        CollectBoxFlowDirectionConf conf = new CollectBoxFlowDirectionConf();
        conf.setStartSiteId(task.getStartSiteId().intValue());
        conf.setBoxReceiveId(task.getEndSiteId().intValue());
        conf.setFlowType(FlowDirectionTypeEnum.OUT_SITE.getCode());
        return conf;
    }

    private void deleteJyCollectPackageRecord(CancelCollectPackageDto dto) {
        JyCollectPackageEntity query = new JyCollectPackageEntity();
        query.setBizId(dto.getBizId());
        query.setBoxCode(dto.getBoxCode());
        query.setPackageCode(dto.getPackageCode());
        query.setStartSiteId(Long.valueOf(dto.getSiteCode()));
        JyCollectPackageEntity entity = jyCollectPackageDao.queryJyCollectPackageRecord(query);
        if (ObjectHelper.isNotEmpty(entity)) {
            JyCollectPackageEntity updateDto = new JyCollectPackageEntity();
            updateDto.setId(entity.getId());
            updateDto.setYn(Boolean.FALSE);
            updateDto.setUpdateTime(new Date());
            updateDto.setUpdateUserErp(dto.getUpdateUserErp());
            updateDto.setUpdateUserName(dto.getUpdateUserName());
            jyCollectPackageDao.updateByPrimaryKeySelective(updateDto);
        }
    }

    /**
     * 执行取消分拣任务
     *
     * @param dto 取消分拣任务的传输对象
     */
    private void execCancel(CancelCollectPackageDto dto) {
        //校验是否存在并发处理中的集包任务
        SortingRequest sortingRequest = checkIfExitsProcessingSortingTask(dto);

        String fingerPrintKey = "SORTING_CANCEL" + dto.getSiteCode() + "|" + dto.getPackageCode();
        boolean isSetNxSuccess = false;
        try {
            //互斥锁防止重复取消， 5分钟内如果同操作场地、同扫描号码只允许取消一次分拣。
            isSetNxSuccess = cacheService.setNx(fingerPrintKey, "1", 5 * 60 * 1000, TimeUnit.SECONDS);
            if (!isSetNxSuccess) {
                this.log.warn("{}正在执行取消分拣任务，5分钟内不能重复取消！", dto.getPackageCode());
                throw new JyBizException(SortingResponse.CODE_SORTING_CANCEL_PROCESS, HintService.getHint(HintCodeConstants.CANCEL_SORTING_PROCESSING));
            }

            Sorting sorting = Sorting.toSorting2(sortingRequest);
            SortingResponse sortingResponse = sortingServiceFactory.getSortingService(sorting.getCreateSiteCode()).cancelSorting(sorting);
            if (!Objects.equals(sortingResponse.getCode(), SortingResponse.CODE_OK)) {
                throw new JyBizException(sortingResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("jy取消集包服务异常{}", dto.getPackageCode(), e);
            throw new JyBizException(dto.getPackageCode()+"取消集包异常！");
        } finally {
            if (isSetNxSuccess) {
                cacheService.del(fingerPrintKey);
            }
        }
    }

    private SortingRequest checkIfExitsProcessingSortingTask(CancelCollectPackageDto dto) {
        SortingRequest sortingRequest = buildSortingRequest(dto);
        List<Task> tasks = findWaitingProcessSortingTasks(sortingRequest);
        if (CollectionUtils.isNotEmpty(tasks)) {
            throw new JyBizException(SortingResponse.CODE_SORTING_WAITING_PROCESS, HintService.getHint(HintCodeConstants.CANCEL_SORTING_IS_PROCESSING));
        }
        return sortingRequest;
    }

    private static SortingRequest buildSortingRequest(CancelCollectPackageDto dto) {
        SortingRequest sortingRequest = new SortingRequest();
        sortingRequest.setPackageCode(dto.getPackageCode());
        sortingRequest.setUserCode(dto.getUpdateUserCode());
        sortingRequest.setUserName(dto.getUpdateUserName());
        sortingRequest.setBusinessType(10);
        sortingRequest.setOperateTime(DateUtil.format(dto.getUpdateTime(), DateUtil.FORMAT_DATE_TIME));
        sortingRequest.setSiteCode(dto.getSiteCode());
        sortingRequest.setSiteName(dto.getSiteName());
        return sortingRequest;
    }

    private List<Task> findWaitingProcessSortingTasks(SortingRequest request) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_SORTING);
        task.setCreateSiteCode(request.getSiteCode());
        task.setBoxCode(request.getBoxCode());
        List<Integer> statusesList = new ArrayList<>(2);
        statusesList.add(Task.TASK_STATUS_UNHANDLED);
        statusesList.add(Task.TASK_STATUS_PROCESSING);
        task.setStatusesList(statusesList);
        return this.taskService.findTasks(task);
    }

    private boolean checkPackageNoExitOrCanceled(CancelCollectPackageDto cancelCollectPackageDto) {
        List<Sorting> sortings = sortingService.findByPackageCode(cancelCollectPackageDto.getSiteCode(), cancelCollectPackageDto.getPackageCode());
        if (!CollectionUtils.isEmpty(sortings)) {
            for (Sorting sorting : sortings) {
                if (sorting.getBoxCode().equals(cancelCollectPackageDto.getBoxCode()) && Constants.NUMBER_ZERO.equals(sorting.getIsCancel())) {
                    return false;
                }
            }
        }
        return true;
    }
}
