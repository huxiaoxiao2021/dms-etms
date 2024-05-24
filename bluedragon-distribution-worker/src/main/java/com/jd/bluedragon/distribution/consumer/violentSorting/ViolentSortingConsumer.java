package com.jd.bluedragon.distribution.consumer.violentSorting;

import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.automatic.DeviceConfigInfoJsfManager;
import com.jd.bluedragon.core.jsf.workStation.WorkGridManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.distribution.sdk.modules.andon.enums.AndonEventSourceEnum;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceGridDto;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.HrUserManager;
import com.jd.bluedragon.core.base.MspClientProxy;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.andon.AndonEventService;
import com.jd.bluedragon.distribution.jy.dto.violentSorting.ViolentSortingDto;
import com.jd.bluedragon.distribution.station.domain.BaseUserSignRecordVo;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
import com.jdl.basic.api.utils.BusinessKeyUtils;
import com.jdl.basic.common.utils.PageDto;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jd.ql.basic.util.DateUtil.FORMAT_DATE;

/**
 * 此消息uat和线上使用不同的topic，uat仅测试使用，不做打标隔离
 **/
//@Service("violentSortingConsumer")
public class ViolentSortingConsumer extends MessageBaseConsumer implements InitializingBean {
    String TYPE_ANDON = "ANDON";

    String MONITOR_AREA = "FJJKS";

    Long UpgradeNotifyCount = 3l;//同一天同一网格，多少次后升级提醒网格长leader

    private static final Logger logger = LoggerFactory.getLogger(ViolentSortingConsumer.class);

    @Autowired
    private DeviceConfigInfoJsfManager deviceConfigInfoJsfService;
    @Autowired
    private JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;


    @Autowired
    AndonEventService andonEventService;

    @Autowired
    WorkStationGridManager workStationGridManager;

    @Autowired
    WorkGridManager workGridManager;

    @Autowired
    @Qualifier("redisClient")
    private Cluster redisClient;

    @Autowired
    MspClientProxy mspClientProxy;

    @Autowired
    private HrUserManager hrUserManager;

    @Autowired
    private UserSignRecordService userSignRecordService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.ViolentSortingConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (StringHelper.isEmpty(message.getText())) {
                logger.warn("ViolentSortingConsumer consume --> 消息为空");
                return;
            }
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("ViolentSortingConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            logger.info("ViolentSortingConsumer consume --> 消息Body为【{}】", message.getText());

            ViolentSortingDto violentSortingDto = JsonHelper.fromJson(message.getText(), ViolentSortingDto.class);

            Integer id = violentSortingDto.getId();
            Long createTime = violentSortingDto.getCreateTime();
            String gridStationOrGridBusinessKey = violentSortingDto.getGridBusinessKey();

            if (id == null) {
                logger.warn("ViolentSortingConsumer consume -->暴力分拣id为空，消息体为【{}】", message.getText());
                return;
            }
            if (createTime == null) {
                logger.warn("ViolentSortingConsumer consume -->创建时间为空，消息体为【{}】", message.getText());
                return;
            }
            if (StringUtils.isEmpty(gridStationOrGridBusinessKey)) {
                logger.warn("ViolentSortingConsumer consume -->网格业务主键为空，消息体为【{}】", message.getText());
                return;
            }
            String gridBusinessKey = null;

            // 如果是网格工序的话，给转换成网格
            if (BusinessKeyUtils.businessKeyIsWorkGrid(gridStationOrGridBusinessKey)) {
                gridBusinessKey = gridStationOrGridBusinessKey;
            } else if (BusinessKeyUtils.businessKeyIsWorkStationGrid(gridStationOrGridBusinessKey)) {
                WorkStationGridQuery workStationGridCheckQuery = new WorkStationGridQuery();
                workStationGridCheckQuery.setBusinessKey(gridStationOrGridBusinessKey);
                Result<WorkStationGrid> workStationGridResult = workStationGridManager.queryByGridKey(workStationGridCheckQuery);
                if (workStationGridResult.isSuccess() && workStationGridResult.getData() != null) {
                    gridBusinessKey = workStationGridResult.getData().getRefWorkGridKey();
                } else {
                    logger.warn("根据gridStationBusinessKey查网格失败，key：" + gridStationOrGridBusinessKey + "消息体：" + message.getText());
                    return;
                }
            }
            Result<WorkGrid> workGridResult = workGridManager.queryByWorkGridKey(gridBusinessKey);
            // 根据网格查出设备编码
            /*List<DeviceGridDto> data = deviceConfigInfoJsfService.findDeviceGridByBusinessKey(gridBusinessKey, null);

            // 过滤出是安灯的设备
            List<String> allAndonMachine = new ArrayList<>();
            for (DeviceGridDto datum : data) {
                DeviceConfigDto oneDeviceConfigByMachineCode = deviceConfigInfoJsfService.findOneDeviceConfigByMachineCode(datum.getMachineCode());
                if (oneDeviceConfigByMachineCode != null && Objects.equals(oneDeviceConfigByMachineCode.getTypeCode(), TYPE_ANDON)) {
                    allAndonMachine.add(oneDeviceConfigByMachineCode.getMachineCode());
                }
            }
            if (CollectionUtils.isEmpty(allAndonMachine)) {
                logger.warn("ViolentSortingConsumer consume -->根据GridByBusinessKey查设绑定的安灯备编码为空，消息体为【{}】", message.getText());
                return;
            }
            if (allAndonMachine.size() > 1) {
                logger.warn("ViolentSortingConsumer consume -->根据GridByBusinessKey查设绑定的安灯备编码查出多个，会取第一个，忽略其他，消息体为【{}】", message.getText());
            }
            String andonMachineCode = allAndonMachine.get(0);
            violentSortingDto.setAndonMachineCode(andonMachineCode);


            if (workGridResult.isSuccess()) {
                WorkGrid grid = workGridResult.getData();

                if (grid == null) {
                    logger.warn("根据网格businesskey查网格数据为空，消息体为【{}】", message.getText());
                    return;
                }
                violentSortingDto.setAreaHubCode(grid.getAreaHubCode());
                violentSortingDto.setAreaHubName(grid.getAreaHubName());
                violentSortingDto.setProvinceAgencyCode(grid.getProvinceAgencyCode());
                violentSortingDto.setProvinceAgencyName(grid.getProvinceAgencyName());
                violentSortingDto.setSiteCode(grid.getSiteCode());
                violentSortingDto.setSiteName(grid.getSiteName());
                violentSortingDto.setGridCode(grid.getGridCode());
                violentSortingDto.setGridName(grid.getGridName());
                violentSortingDto.setOwnerUserErp(grid.getOwnerUserErp());
            } else {
                logger.warn("根据网格businesskey查网格失败，消息体为【{}】", message.getText());
                throw new RuntimeException("根据网格businesskey查网格失败" + message.getText());// 异常mq重试
            }


            // 需要亮灯时，推送消息给灯绑定网格的网格长。
            // 若事件为该网格当日（0点到23点59分59秒）第三次或更多次事件时，消息额外推送给场地负责人「网格长的上级」。
            // 消息标题：违规操作预警，内容：XX分拣XXX网格违规操作已触发亮灯，当日累积触发X次安灯系统，请核查原因与责任人，推动改善！视频链接：xxxxxx
            Long l = notifyViolentSortingGridOwnerOrLerder(violentSortingDto);
            // 亮灯
            andonEventService.lightOn(AndonEventSourceEnum.VIOLENT_SORTING,
                    String.valueOf(violentSortingDto.getId()),
                    violentSortingDto.getSiteCode(),
                    violentSortingDto.getGridCode(), andonMachineCode, new Date(violentSortingDto.getCreateTime()), violentSortingDto);*/

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("暴力分拣消费失败" + message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    // 通知
    private Long notifyViolentSortingGridOwnerOrLerder(ViolentSortingDto d) {
        String date = DateUtil.format(new Date(d.getCreateTime()), FORMAT_DATE);
        String redisKey = "VIOLENT_ANDON_EVENT_COUNT:" + date + d.getAndonMachineCode();
        redisClient.set(redisKey, "0", 1l, TimeUnit.DAYS, false);
        Long incr = redisClient.incr(redisKey);

        Map<String, String> argsMap = new HashMap<>();
        argsMap.put(HintArgsConstants.ARG_FIRST, d.getSiteName());
        argsMap.put(HintArgsConstants.ARG_SECOND, d.getGridName());
        argsMap.put(HintArgsConstants.ARG_THIRD, d.getGridCode());
        argsMap.put(HintArgsConstants.ARG_FOURTH, incr.toString());
        String content = HintService.getHint(HintCodeConstants.VIOLENT_ANDON_JD_ME_CODE, argsMap);
        content = content.replaceAll(HintCodeConstants.VIOLENT_ANDON_JD_ME_CODE + "-", "");
//        String content = MessageFormat.format("{0}[{1}({2})]网格违规操作已触发亮灯，当日累积触发{3}次安灯系统，请核查原因与责任人，推动改善！", d.getSiteName(), d.getGridName(), d.getGridCode(), incr);
        HashSet<String> pins = new HashSet<>();
        // 网格负责人
        pins.add(d.getOwnerUserErp());
        // 监控区人员
        List<String> monitorRoomPerson = findMonitorRoomPerson(d);
        if (!CollectionUtils.isEmpty(monitorRoomPerson)) {
            pins.addAll(monitorRoomPerson);
        }
        //第三次或更多次事件时,网格负责人的上级
        if (incr >= UpgradeNotifyCount) {
            String superiorErp = hrUserManager.getSuperiorErp(d.getOwnerUserErp());
            if (StringUtils.isNotEmpty(superiorErp)) {
                pins.add(superiorErp);
            }
        }
        pins.add("chenlingfeng10");
        mspClientProxy.sendTimeline("[测试请忽略]违规操作预警", content, d.getUrl(), pins, false);
        return incr;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setUat("false");
    }

    // 昨天0点至当前，在监控室作业区下任一网格内有签到或签退过的人
    private List<String> findMonitorRoomPerson(ViolentSortingDto dto) {
        WorkStationGridQuery gridQuery = new WorkStationGridQuery();
        gridQuery.setSiteCode(dto.getSiteCode());
        gridQuery.setAreaCode(MONITOR_AREA);
        gridQuery.setPageNumber(1);
        gridQuery.setPageSize(10);
        Result<PageDto<WorkStationGrid>> result = workStationGridManager.queryPageList(gridQuery);
        if (!result.isSuccess() || CollectionUtils.isEmpty(result.getData().getResult())) {
            return null;
        }
        logger.info("findMonitorRoomPerson.grid:{}", JsonHelper.toJson(result));
        UserSignRecordQuery userSignRecordQuery = new UserSignRecordQuery();
        List<WorkStationGrid> grids = result.getData().getResult();
        userSignRecordQuery.setRefGridKeyList(grids.stream().map(g -> g.getBusinessKey()).collect(Collectors.toList()));
        userSignRecordQuery.setYesterdayStart(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -1));
        List<BaseUserSignRecordVo> baseUserSignRecordVos = userSignRecordService.queryMonitorRoomPerson(userSignRecordQuery);
        logger.info("findMonitorRoomPerson.user:{}", JsonHelper.toJson(baseUserSignRecordVos));
        return baseUserSignRecordVos.stream().map(v -> v.getUserCode()).collect(Collectors.toList());
    }
}
