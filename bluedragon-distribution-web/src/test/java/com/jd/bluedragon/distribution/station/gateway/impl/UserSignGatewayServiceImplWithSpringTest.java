package com.jd.bluedragon.distribution.station.gateway.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.jsf.attBlackList.AttendanceBlackListManager;
import com.jd.bluedragon.core.jsf.attBlackList.impl.AttendanceBlackListManagerImpl;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jdl.basic.api.domain.attBlackList.AttendanceBlackList;
import com.jdl.basic.api.service.attBlackList.AttendanceBlackListJsfService;
import com.jdl.basic.common.utils.DateUtil;
import com.jdl.basic.common.utils.Result;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context.xml"})
//@ContextConfiguration( {"classpath:bak/distribution-web-context-test.xml"})
public class UserSignGatewayServiceImplWithSpringTest {

    @Autowired
    AttendanceBlackListManager attendanceBlackListManager;

    @Autowired
    AttendanceBlackListJsfService basicAttendanceBlackListJsfService;

    @Autowired
    private UserSignRecordService userSignRecordService;

    @Autowired
    UserSignGatewayServiceImpl userSignGatewayService;
    static String userCode ="bjxings";
    static String scanUserCode = "1bjxings";
    static String positionCode1 = "GW0000010001";
    static String positionCode2 = "GW0000010002";
    @Test
    public void testCheckBeforeSignIn() throws Exception{
        UserSignRequest userSignRequest = new UserSignRequest();
        userSignRequest.setPositionCode(positionCode1);
        userSignRequest.setScanUserCode(scanUserCode);
        JdCResponse<UserSignRecordData> result = userSignGatewayService.checkBeforeSignIn(userSignRequest);
        System.out.println(JsonHelper.toJson(result));
    }

    @Test
    public void testCheckAttendanceBlackList(){


        String userCode="131124199922222822";
        //查询出勤黑名单，并校验
        Result<AttendanceBlackList>  rs=attendanceBlackListManager.queryByUserCode(userCode);
        if(rs == null){
            System.out.println("empty");
        }
        if(rs.isSuccess()){
            AttendanceBlackList attendanceBlackList=rs.getData();
            int cancelFlag=attendanceBlackList.getCancelFlag();
            Date takeTime=attendanceBlackList.getTakeTime();
            Date loseTime=attendanceBlackList.getLoseTime();
            String dateStr= DateUtil.format(new Date(),DateUtil.FORMAT_DATE);
            Date currentTime=DateUtil.parse(dateStr,DateUtil.FORMAT_DATE);
            if(cancelFlag ==0 && (currentTime.compareTo(takeTime) < Constants.NUMBER_ZERO)){
                //待生效
                String defaultMsg = String.format(HintCodeConstants.ATTENDANCE_BLACK_LIST_TOBE_EFFECTIVE_MSG, userCode,DateUtil.format(takeTime,DateUtil.FORMAT_DATE));
                System.out.println(defaultMsg);
            }else if(cancelFlag ==0 && ((loseTime ==null && currentTime.compareTo(takeTime) >=0) ||  (loseTime !=null && currentTime.compareTo(takeTime) >=0 && currentTime.compareTo(loseTime) <0))){//已生效
                //已生效
                String defaultMsg = String.format(HintCodeConstants.ATTENDANCE_BLACK_LIST_TAKE_EFFECTIVE_MSG, userCode);
                System.out.println(defaultMsg);
            }
        }
    }
}
