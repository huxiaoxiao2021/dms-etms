package com.jd.bluedragon.distribution.inventory.service.impl;

import com.jd.bluedragon.distribution.api.domain.SiteEntity;
import com.jd.bluedragon.distribution.api.request.inventory.InventoryTaskRequest;
import com.jd.bluedragon.distribution.api.response.inventory.InventoryTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.CooperateTypeEnum;
import com.jd.bluedragon.distribution.inventory.domain.InventoryScopeEnum;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTaskStatusEnum;
import com.jd.bluedragon.distribution.inventory.service.InventoryTaskService;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("inventoryTaskService")
public class InventoryTaskServiceImpl extends BaseService<InventoryTask> implements InventoryTaskService {
    private final Log logger = LogFactory.getLog(this.getClass());

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
    @Qualifier("inventoryTaskDao")
    private InventoryTaskDao inventoryTaskDao;

    @Override
    public Dao<InventoryTask> getDao() {
        return this.inventoryTaskDao;
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
        if (result != null && InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
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
            result.toWarn(createUserErp + "正在对该进行盘点，是否协助加入？");
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

        List<InventoryTask> inventoryTaskList = null;
        InventoryTaskResponse response = new InventoryTaskResponse();

        //1.如果有任务号并且协作类型是协作者，在inventory_task表里插入协助数据
        if (StringUtils.isNotBlank(request.getInventoryTaskId())) {
            //先根据任务号查出正在进行的任务
            inventoryTaskList = inventoryTaskDao.getInventoryTaskByTaskId(request.getInventoryTaskId());
            if (inventoryTaskList == null || inventoryTaskList.size() < 1) {
                //任务已经结束了
                result.toError("该盘点任务已经结束.请重新选择盘点范围和盘点卡位创建新的盘点任务.");
                return result;
            }

            List<SiteEntity> directionList = new ArrayList<>();

            if (request.getCooperateType() == CooperateTypeEnum.COOPERATOR.getCode()) {
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
            }
            response.setDirectionList(directionList);
            response.setInventoryScope(request.getInventoryScope());
            response.setInventoryTaskId(request.getInventoryTaskId());

            result.setData(response);
            return result;
        }

        //2.没有任务号先完成校验--（1）同一操作人的校验；（2）相同流向的校验
        result = getUserDoingInventoryTask(request);
        if (result != null && InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
            return result;
        }
        result = directionVerify(request);
        if (result != null && InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
            return result;
        }

        //3.没有正在进行的可以创建新的任务
        String inventoryTaskId = generateInventoryTaskId(request.getSiteCode());

        inventoryTaskList = new ArrayList<>();
        InventoryTask inventoryTaskBasic = new InventoryTask();
        inventoryTaskBasic.setInventoryTaskId(inventoryTaskId);
        inventoryTaskBasic.setCreateSiteCode(request.getSiteCode());
        inventoryTaskBasic.setCreateSiteName(request.getSiteName());
        inventoryTaskBasic.setCreateUserCode(request.getUserCode());
        inventoryTaskBasic.setCreateUserErp(request.getUserErp());
        inventoryTaskBasic.setCreateUserName(request.getUserName());
        inventoryTaskBasic.setCreateTime(DateHelper.getSeverTime(request.getOperateTime()));
        inventoryTaskBasic.setUpdateTime(inventoryTaskBasic.getCreateTime());
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
     * IT+createSiteCode+ md5（目的地列表） +时间戳
     *
     * @param createSiteCode
     * @return
     */
    private String generateInventoryTaskId(Integer createSiteCode) {
        UUID uuid = UUID.randomUUID();
        String taskId = uuid.toString().replaceAll(SPLIT_CHAR_STRING, "");
        //加上前缀和始发
        taskId = INVENTORY_TASK_ID_PREFIX + createSiteCode + SPLIT_CHAR_STRING + taskId;
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
        if (inventoryScope == 1) {
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
