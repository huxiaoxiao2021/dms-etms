package com.jd.bluedragon.distribution.brush;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.dto.task.request.TaskPdaRequest;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.external.gateway.service.TaskGatewayService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/brush")
public class BrushController extends DmsBaseController {

    @Autowired
    private TaskGatewayService taskGatewayService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 返回主页面
     * @return
     */
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        // 设置基础信息
        return "brush/brush";
    }

    /**
     * 刷数导入
     * @return
     */
    @RequestMapping(value = "/toImportBrush", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse toImportBrush(@RequestParam("importExcelFile_brush") MultipartFile file) {
        if(log.isInfoEnabled()){
            log.info("uploadExcelFile brush begin...");
        }
        JdResponse response = fileCheck(file);
        if(!response.isSucceed()){
            return response;
        }
        try {
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(1);
            List<BrushVO> dataList
                    = dataResolver.resolverWithNum(file.getInputStream(), BrushVO.class,
                    new PropertiesMetaDataFactory("/excel/brush.properties"), 20000);
            if(CollectionUtils.isEmpty(dataList)){
                log.warn("刷数excel解析失败!");
                return response;
            }
            // 200一批分批处理
            List<List<BrushVO>> bathList = Lists.partition(dataList, 200);
            bathList.forEach(singleList -> {
                // singleList deal
                singleList.forEach(item -> {
                    // fill request
                    fillRequest(item);
                    // build task && send
                    buildTaskAndSend(item);
                });
                // sleep
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            log.error("导入异常!",e);
            response.toFail(e.getMessage());
        }
        return response;
    }

    private JdResponse fileCheck(MultipartFile file) {
        JdResponse response = new JdResponse();
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith("xls")) {
            response.toError("文件格式不对!");
        }
        return response;
    }

    private void buildTaskAndSend(BrushVO item) {
        TaskPdaRequest pdaRequest = new TaskPdaRequest();
        pdaRequest.setBoxCode(item.getBoxCode());
        pdaRequest.setKeyword1(String.valueOf(item.getSiteCode()));
        pdaRequest.setKeyword2(item.getBoxCode());
        pdaRequest.setReceiveSiteCode(item.getReceiveSiteCode());
        pdaRequest.setSiteCode(item.getSiteCode());
        pdaRequest.setType(Task.TASK_TYPE_SORTING);
        pdaRequest.setBody(JsonHelper.toJson(item));
        if(log.isInfoEnabled()){
            log.info("分拣刷数任务发送: {}", JsonHelper.toJson(item));
        }
        taskGatewayService.addTasksCommonly(pdaRequest);
    }

    private void fillRequest(BrushVO item) {
        item.setBizSource(SortingBizSourceEnum.ANDROID_SORTING.getCode());
        item.setFeatureType(0);
        item.setIsCancel(0);
        item.setIsLoss(0);
        BaseStaffSiteOrgDto operateSite = baseMajorManager.getBaseSiteBySiteId(item.getSiteCode());
        item.setSiteName(operateSite == null ? null : operateSite.getSiteName());
        BaseStaffSiteOrgDto receiveSite = baseMajorManager.getBaseSiteBySiteId(item.getReceiveSiteCode());
        item.setReceiveSiteName(receiveSite == null ? null : receiveSite.getSiteName());
        // 操作时间减去1分钟
        Date operateDate = DateHelper.add(DateHelper.parseDate(item.getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2), Calendar.SECOND , -60);
        item.setOperateTime(DateHelper.formatDate(operateDate, DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
    }
}
