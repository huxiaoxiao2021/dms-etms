package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jd.bluedragon.core.jsf.workStation.WorkGridScheduleManager;
import com.jd.bluedragon.distribution.api.request.sendcode.DmsUserScheduleRequest;
import com.jd.bluedragon.distribution.external.service.DmsUserScheduleService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.user.JyUserQueryDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleQueryDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("dmsUserScheduleService")
public class DmsUserScheduleServiceImpl implements DmsUserScheduleService {
    @Autowired
    private WorkGridScheduleManager workGridScheduleManager;
    @Autowired
    private DmsConfigManager dmsConfigManager;
    @Autowired
    private JyUserManager jyUserManager;

    @Override
    public JdResponse<Boolean> allowEntryTurnStile(DmsUserScheduleRequest request) {
        JdResponse<Boolean> response = new JdResponse<>();
        UserGridScheduleQueryDto scheduleQueryDto = new UserGridScheduleQueryDto();
        if (StringUtils.isEmpty(request.getUserCode())) {
            response.toFail(JdResponse.CODE_FAIL, "userCode不能为空！");
            return response;
        }

        JyUserQueryDto userQueryDto = new JyUserQueryDto();
        userQueryDto.setUserUniqueCode(request.getUserCode());
        JyUserDto userDto = jyUserManager.getUserByErpOrIdNum(userQueryDto);
        if (userDto == null) {
            response.setData(false);
            response.setMessage(String.format("找不到%s该用户！", request.getUserCode()));
            return response;
        }

        scheduleQueryDto.setScheduleDate(DateHelper.formatDate(new Date()));
        scheduleQueryDto.setNature(userDto.getNature());
        scheduleQueryDto.setUserUniqueCode(request.getUserCode());
        List<UserGridScheduleDto> dtoList = workGridScheduleManager.getUserScheduleByCondition(scheduleQueryDto);
        if (CollectionUtils.isEmpty(dtoList)) {
            response.setData(false);
            response.toFail(String.format("%s该用户无排班记录！", request.getUserCode()));
            return response;
        }

        List<String> scheduleTimes = new ArrayList<>();
        boolean allowFlag = false;
        for (UserGridScheduleDto scheduleDto : dtoList) {
            // 判断当前时间是否是在合理进入闸机时间范围
            boolean startTimeCheck = checkEntryTime(scheduleDto.getStartTime(), -dmsConfigManager.getPropertyConfig().getAllowEntryHours());
            boolean endTimeCheck = checkEntryTime(scheduleDto.getEndTime(), dmsConfigManager.getPropertyConfig().getAllowEntryHours());
            if (startTimeCheck && endTimeCheck) {
                response.setData(true);
                return response;
            }
            scheduleTimes.add(scheduleDto.getStartTime() + Constants.SEPARATOR_HYPHEN + scheduleDto.getEndTime());
        }
        if (!allowFlag) {
            response.setData(false);
            response.toFail(String.format("%s排班时间为%s,不在前后%s小时的有效进入闸机时间范围内！", request.getUserCode(), scheduleTimes, dmsConfigManager.getPropertyConfig().getAllowEntryHours()));
            return response;
        }
        response.setData(allowFlag);
        return response;
    }

    /**
     * 判断当前时间是否在排班时间前后一个小时的偏移范围内
     * @param timeStr
     * @param allowEntryHour
     * @return
     */
    private boolean checkEntryTime(String timeStr, Integer allowEntryHour) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        String[] timeSplit = timeStr.split(Constants.SEPARATOR_COLON);
        int startHour = Integer.parseInt(timeSplit[0]);
        int startMin = Integer.parseInt(timeSplit[1]);
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMin);
        calendar.set(Calendar.SECOND, Constants.NUMBER_ZERO);
        Date allowEntryStartTime = calendar.getTime();
        if (allowEntryHour < 0 ) {
            return !now.before(DateUtils.addHours(allowEntryStartTime, allowEntryHour));
        } else if (allowEntryHour > 0) {
            return !now.after(DateUtils.addHours(allowEntryStartTime, allowEntryHour));
        }
        return true;
    }
}
