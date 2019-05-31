package com.jd.bluedragon.distribution.inventory.service.impl;

import com.jd.bluedragon.distribution.api.domain.SiteEntity;
import com.jd.bluedragon.distribution.api.request.inventory.InventoryTaskRequest;
import com.jd.bluedragon.distribution.api.response.inventory.InventoryTaskResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.bluedragon.distribution.inventory.service.InventoryTaskService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
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

    @Autowired
    @Qualifier("inventoryTaskDao")
    private InventoryTaskDao inventoryTaskDao;

    @Override
    public Dao<InventoryTask> getDao() {
        return this.inventoryTaskDao;
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

        if (request.getSiteCode() == null || request.getSiteCode() <= 0 || request.getUserCode() == null || request.getUserCode() <= 0) {
            result.toError("操作站点和操作人不能为空.");
            return result;
        }

        InventoryTaskResponse response = new InventoryTaskResponse();
        List<SiteEntity> directionList = new ArrayList<>();

        //1.根据操作人和操作站点判断是否有正在进行的任务
        List<InventoryTask> inventoryTaskList = inventoryTaskDao.getInventoryTaskBySiteAndUser(request.getSiteCode(), request.getUserCode());
        if (inventoryTaskList != null && inventoryTaskList.size() > 0) {

            //组装卡位列表
            String directionNameStr = "";
            for (InventoryTask task : inventoryTaskList) {
                SiteEntity site = new SiteEntity();
                site.setCode(task.getDirectionCode());
                site.setName(task.getDirectionName());
                directionList.add(site);

                directionNameStr += task.getCreateSiteName() + ",";
            }
            if (directionNameStr.length() > 0) {
                directionNameStr = directionNameStr.substring(0, directionNameStr.length() - 1);
            }

            response.setInventoryTaskId(inventoryTaskList.get(0).getInventoryTaskId());
            response.setDirectionList(directionList);
            response.setInventoryScope(inventoryTaskList.get(0).getInventoryScope());

            result.setData(response);
            result.toError("还有未完成的盘点任务，请先完成.盘点卡位：" + directionNameStr);
            return result;
        }

        //2.生成任务号，写入盘点任务表
        String inventoryTaskId = generateInventoryTaskId(request.getSiteCode());

        inventoryTaskList = new ArrayList<>();
        for (SiteEntity receiveSite : request.getDirectionList()) {
            InventoryTask inventoryTask = new InventoryTask();
            inventoryTask.setInventoryTaskId(inventoryTaskId);
            inventoryTask.setCreateSiteCode(request.getSiteCode());
            inventoryTask.setCreateSiteName(request.getSiteName());
            inventoryTask.setDirectionCode(receiveSite.getCode());
            inventoryTask.setDirectionName(receiveSite.getName());
            inventoryTask.setCreateUserCode(request.getUserCode());
            inventoryTask.setCreateUserErp(request.getUserErp());
            inventoryTask.setCreateUserName(request.getUserName());
            inventoryTask.setCreateTime(DateHelper.getSeverTime(request.getOperateTime()));
            inventoryTask.setUpdateTime(inventoryTask.getCreateTime());
            inventoryTask.setCooperateType(request.getCooperateType());
            inventoryTask.setInventoryScope(request.getInventoryScope());

            inventoryTaskList.add(inventoryTask);
        }

        inventoryTaskDao.addBatch(inventoryTaskList);

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

}
