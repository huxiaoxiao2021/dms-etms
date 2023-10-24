package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackStatusCount;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;
import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.dao.collectpackage.JyBizTaskCollectPackageDao;
import com.jd.bluedragon.distribution.jy.dao.collectpackage.JyCollectPackageDao;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CancelCollectPackageDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.middleend.SortingServiceFactory;
import com.jd.bluedragon.distribution.middleend.sorting.service.ISortingService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Console;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        return jyBizTaskCollectPackageDao.queryTaskStatusCount(query);
    }

    @Override
    public Boolean updateById(JyBizTaskCollectPackageEntity entity) {
        return jyBizTaskCollectPackageDao.updateByPrimaryKeySelective(entity) > 0;
    }

    @Override
    public Boolean updateStatusByBizIds(JyBizTaskCollectPackageQuery query) {
        return jyBizTaskCollectPackageDao.updateStatusByBizIds(query) > 0;
    }

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

    private void execCancel(CancelCollectPackageDto dto) {
        SortingRequest req = new SortingRequest();
        req.setPackageCode(dto.getPackageCode());
        req.setUserCode(dto.getUpdateUserCode());
        req.setUserName(dto.getUpdateUserName());
        //req.setBusinessType(request.getBusinessType());
        req.setOperateTime(DateUtil.format(dto.getUpdateTime(), DateUtil.FORMAT_DATE_TIME));
        req.setSiteCode(dto.getSiteCode());
        req.setSiteName(dto.getSiteName());
        req.setBoxCode(dto.getBoxCode());

        List<Task> tasks = this.findWaitingProcessSortingTasks(req);
        if (!tasks.isEmpty()) {
            throw new JyBizException(SortingResponse.CODE_SORTING_WAITING_PROCESS, HintService.getHint(HintCodeConstants.CANCEL_SORTING_IS_PROCESSING));
        }

        String fingerPrintKey = "SORTING_CANCEL" + dto.getSiteCode() + "|" + dto.getPackageCode();
        //判断是否重复取消分拣, 5分钟内如果同操作场地、同扫描号码只允许取消一次分拣。
        boolean isSuccess = false;
        try {
            isSuccess = cacheService.setNx(fingerPrintKey, "1", 5 * 60 * 1000, TimeUnit.SECONDS);
            //说明key存在
            if (!isSuccess) {
                this.log.warn("{}正在执行取消分拣任务，5分钟内不能重复取消！", dto.getPackageCode());
                throw new JyBizException(SortingResponse.CODE_SORTING_CANCEL_PROCESS, HintService.getHint(HintCodeConstants.CANCEL_SORTING_PROCESSING));
            }
            Sorting sorting = Sorting.toSorting2(req);
            SortingResponse sortingResponse = sortingServiceFactory.getSortingService(sorting.getCreateSiteCode()).cancelSorting(sorting);
            if (!Objects.equals(sortingResponse.getCode(), SortingResponse.CODE_OK)) {
                throw new JyBizException(sortingResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("{}取消分拣服务异常", dto.getPackageCode(), e);
        } finally {
            if (isSuccess) {
                cacheService.del(fingerPrintKey);
            }
        }
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
