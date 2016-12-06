package com.jd.bluedragon.distribution.crossbox.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.annotation.JProEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.CrossBoxRequest;
import com.jd.bluedragon.distribution.crossbox.dao.CrossBoxDao;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;

@Service("crossBoxService")
public class CrossBoxServiceImpl implements CrossBoxService {

    private static final Logger logger = Logger.getLogger(CrossBoxServiceImpl.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private CrossBoxDao crossBoxDao;

    @Override
    public int addCrossBox(CrossBox crossBox) {
        return crossBoxDao.addCrossBox(crossBox);
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
    public List<List<Object>> getExportDataByCrossBox(CrossBoxRequest crossBoxRequest) {
        List<List<Object>> resList = new ArrayList<List<Object>>();

        List<Object> heads = new ArrayList<Object>();

        heads.add("序号");
        heads.add("始发分拣中心_Id");
        heads.add("始发分拣中心");
        heads.add("中转1_Id");
        heads.add("中转1");
        heads.add("中转2_Id");
        heads.add("中转2");
        heads.add("中转3_Id");
        heads.add("中转3");
        heads.add("目的分拣中心_Id");
        heads.add("目的分拣中心");
        heads.add("完整线路");
        heads.add("维护(更新)时间");
        heads.add("维护人员");
        heads.add("生效时间");

        resList.add(heads);

        List<CrossBox> dataList = this.queryByConditionForExport(crossBoxRequest);
        if (dataList == null) {
            return resList;
        }

        for (CrossBox item : dataList) {
            List<Object> row = new ArrayList<Object>();
            row.add(item.getId() == null ? "" : item.getId());
            row.add(item.getOriginalDmsId() == null ? "" : item.getOriginalDmsId());
            row.add(item.getOriginalDmsName() == null ? "" : item.getOriginalDmsName());
            row.add(item.getTransferOneId() == null ? "" : item.getTransferOneId());
            row.add(item.getTransferOneName() == null ? "" : item.getTransferOneName());
            row.add(item.getTransferTwoId() == null ? "" : item.getTransferTwoId());
            row.add(item.getTransferTwoName() == null ? "" : item.getTransferTwoName());
            row.add(item.getTransferThreeId() == null ? "" : item.getTransferThreeId());
            row.add(item.getTransferThreeName() == null ? "" : item.getTransferThreeName());
            row.add(item.getDestinationDmsId() == null ? "" : item.getDestinationDmsId());
            row.add(item.getDestinationDmsName() == null ? "" : item.getDestinationDmsName());
            row.add(item.getFullLine() == null ? "" : item.getFullLine());
            row.add(item.getUpdateTime() == null ? "" : DateHelper.formatDateTime(item.getUpdateTime()));
            row.add(item.getUpdateOperatorName() == null ? "" : item.getUpdateOperatorName());
            row.add(item.getEffectiveDate() == null ? "" : DateHelper.formatDateTime(item.getEffectiveDate()));
            resList.add(row);
        }

        return resList;
    }

    @Override
    public int deleteById(CrossBox crossDmsBox) {
        return crossBoxDao.deleteById(crossDmsBox);
    }

    @Override
    public int updateCrossBoxByDms(CrossBox crossDmsBox) {
        return crossBoxDao.updateCrossBoxByDms(crossDmsBox);
    }

    @Override
    public CrossBox getCrossBoxById(Integer id) {
        return crossBoxDao.getCrossBoxById(id);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
            logger.error("执行updateCrossDmsBoxById失败,OriginalDmsId[" + crossDmsBox.getOriginalDmsId() + "]");
        }
        return res;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.BasicSafInterfaceManagerImpl.getCrossDmsBoxByOriAndDes", mState = {JProEnum.TP, JProEnum.FunctionError})
    public CrossBoxResult<CrossBox> getCrossDmsBoxByOriAndDes(Integer originalDmsId, Integer destinationDmsId) {
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
            result.setResultCode(result.FAIL);
            result.setData(null);
            result.setMessage("调用接口异常：" + e);
        }
        return result;
    }


    @Override
    public CrossBoxResult<String> getCrossDmsBox(Integer originalDmsId, Integer destinationDmsId) {
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
            result.setData(null);
            result.setResultCode(result.FAIL);
            result.setMessage("调用接口异常：" + e);
            logger.error("根据始发和目的分拣中心ID获取路线信息:", e);
        }
        return result;
    }
}
