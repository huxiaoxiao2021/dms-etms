package com.jd.bluedragon.distribution.consumer.trace;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.trace.BarcodeTraceDto;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
@Service("barcodeTraceConsumer")
public class BarcodeTraceConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TaskService taskService;
    @Autowired
    private BoxService boxService;

    @Autowired
    private SortingService sortingService;
    @Override
    @JProfiler(jKey = "DmsWork.BarcodeTraceConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER,mState = {JProEnum.TP,JProEnum.Heartbeat})
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("全流程跟踪消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        try {
            if(log.isInfoEnabled()) {
                log.info("BarcodeTraceConsumer-msg={}", message.getText());
            }
            BarcodeTraceDto barcodeTraceDto = JsonHelper.fromJson(message.getText(), BarcodeTraceDto.class);


            if(BusinessUtil.isBoxcode(barcodeTraceDto.getBarCode())) {
                boxTrace(barcodeTraceDto);
            }else {
                packageTrace(barcodeTraceDto, null);
            }
        } catch (Exception e) {
            log.error("BarcodeTraceConsumer全流程跟踪消费发生异常,内容为【{}】", message.getText(), e);
            throw new RuntimeException(e);
        }
    }

    void boxTrace(BarcodeTraceDto barcodeTraceDto){
        Box box = boxService.findBoxByCode(barcodeTraceDto.getBarCode());
        Sorting sorting = new Sorting();
        sorting.setBoxCode(barcodeTraceDto.getBarCode());
        sorting.setCreateSiteCode(box.getCreateSiteCode());
        List<Sorting> sortingList = sortingService.listSortingByBoxCode(sorting);
        if(CollectionUtils.isEmpty(sortingList)) {
            if(log.isInfoEnabled()) {
                log.info("按箱{}操作发送全流程跟踪，箱内未查到包裹信息，mqMsg={}", barcodeTraceDto.getBarCode(), JsonHelper.toJson(barcodeTraceDto));
                throw new JyBizException("按箱操作发送全流程跟踪，箱内未查到包裹信息");
            }
        }
        for (Sorting s:sortingList){
            packageTrace(barcodeTraceDto, s.getPackageCode());
        }
    }

    void packageTrace(BarcodeTraceDto barcodeTraceDto, String packageCode){
        barcodeTraceDto.setPackageCode(StringUtils.isBlank(packageCode) ? barcodeTraceDto.getBarCode() : packageCode);
        fillRemark(barcodeTraceDto);
        addTraceTask(barcodeTraceDto);
    }

    /**
     * 追加话术
     * @param barcodeTraceDto
     */
    private void fillRemark(BarcodeTraceDto barcodeTraceDto) {
        //取消组板话术
        if (barcodeTraceDto.getOperateType().equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION)) {
            barcodeTraceDto.setRemark("包裹号：" + barcodeTraceDto.getPackageCode() + "已进行组板，板号" + barcodeTraceDto.getBoardCode() + "，等待送往" + barcodeTraceDto.getReceiveSiteName());
        }
        //取消组板话术
        else if (barcodeTraceDto.getOperateType().equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL)) {
            barcodeTraceDto.setRemark("已取消组板，板号" + barcodeTraceDto.getBoardCode());
        }
    }


    void addTraceTask(BarcodeTraceDto dto){
        if(log.isInfoEnabled()) {
            log.info("BarcodeTraceConsumer-发送包裹全流程跟踪：dto={}", JsonHelper.toJson(dto));
        }
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(dto.getPackageCode());
        task.setKeyword2(String.valueOf(dto.getOperateType()));
        task.setCreateSiteCode(dto.getCreateSiteCode());
        task.setBody(JsonHelper.toJson(dto));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());

        taskService.add(task);
    }

}
