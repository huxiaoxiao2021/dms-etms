package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jsf.workStation.WorkGridScheduleManager;
import com.jd.bluedragon.distribution.api.request.sendcode.DmsUserScheduleRequest;
import com.jd.bluedragon.distribution.external.service.DmsUserScheduleService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.user.JyUserQueryDto;
import com.jdl.basic.api.utils.JyUserUtils;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleQueryDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

public class DmsUserScheduleServiceImpl implements DmsUserScheduleService {
    @Autowired
    private WorkGridScheduleManager workGridScheduleManager;
    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Override
    public JdResponse<Boolean> allowEntryTurnStile(DmsUserScheduleRequest request) {
        JdResponse<Boolean> response = new JdResponse<>();
        JyUserQueryDto userQueryDto = new JyUserQueryDto();
        UserGridScheduleQueryDto scheduleQueryDto = new UserGridScheduleQueryDto();
        if (StringUtils.isEmpty(request.getUserCode())) {
            response.toFail(JdResponse.CODE_FAIL, "userCode不能为空！");
            return response;
        }
        if (JyUserUtils.isIdCard(request.getUserCode())) {
            userQueryDto.setUserCode(request.getUserCode());
            scheduleQueryDto.setUserCode(request.getUserCode());
        } else {
            userQueryDto.setUserErp(request.getUserCode());
            scheduleQueryDto.setUserErp(request.getUserCode());
        }

        JyUserDto userDto = workGridScheduleManager.getUserByUserCode(userQueryDto);
        if (userDto == null) {
            response.setData(false);
            response.setMessage("找不到用户该用户！");
            return response;
        }

        scheduleQueryDto.setScheduleDate(DateHelper.formatDate(new Date()));
        UserGridScheduleDto scheduleDto = workGridScheduleManager.getUserScheduleByCondition(scheduleQueryDto);
        if (scheduleDto == null) {
            response.setData(false);
            response.setMessage("该用户无排班记录！");
            return response;
        }


        // 判断当前时间是否是在合理进入闸机时间范围
        boolean startTimeCheck = checkEntryTime(scheduleDto.getStartTime(), -dmsConfigManager.getPropertyConfig().getAllowEntryHours());
        if (!startTimeCheck) {
            response.setData(false);
            response.setMessage("不在有效进入闸机时间范围内！");
            return response;
        }
        boolean endTimeCheck = checkEntryTime(scheduleDto.getEndTime(), dmsConfigManager.getPropertyConfig().getAllowEntryHours());
        if (!endTimeCheck) {
            response.setData(false);
            response.setMessage("不在有效进入闸机时间范围内！");
            return response;
        }
        response.setData(true);
        return response;
    }

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
