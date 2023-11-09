package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.distribution.api.request.ReassignWaybillApprovalRecordQuery;
import com.jd.bluedragon.distribution.api.response.BaseStaffResponse;
import com.jd.bluedragon.distribution.api.response.ReassignOrder;
import com.jd.bluedragon.distribution.api.response.ReassignWaybillApprovalRecordResponse;
import com.jd.bluedragon.distribution.api.response.StationMatchResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.StationMatchRequest;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/7 20:38
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class ReassignWaybillServiceTest {

    @Autowired
    private ReassignWaybillService reassignWaybillService;

    @Test
    public void  getReassignOrderInfoTest(){
        JdResult<ReassignOrder> result = reassignWaybillService.getReassignOrderInfo("JDX000260058081", null);
        System.out.println(JSON.toJSONString(result));
    }

    @Test
    public void stationMatchByAddressTest(){
        StationMatchRequest request = new StationMatchRequest();
        request.setAddress("北京市大兴区荣华中路19号朝林广场a座21层");
        JdResult<StationMatchResponse> result = reassignWaybillService.stationMatchByAddress(request);
        System.out.println(JSON.toJSONString(result));

    }

    @Test
    public void getSiteByCodeOrNameTest(){

        JdResult<List<BaseStaffResponse>> siteByCodeOrName = reassignWaybillService.getSiteByCodeOrName("北京");

        System.out.println(JSON.toJSONString(siteByCodeOrName));

    }

    @Test
    public void getReassignWaybillRecordListByPageTest(){

        ReassignWaybillApprovalRecordQuery query = new ReassignWaybillApprovalRecordQuery();
        query.setPageNumber(1);
        query.setPageSize(3);

        String startStr ="2023-11-01 12:00:00";
        String endStr ="2023-11-11 12:00:00";
        query.setStartSubmitTime(DateHelper.parseDateTime(startStr));
        query.setEndSubmitTime(DateHelper.parseDateTime(endStr));

        JdResult<PageDto<ReassignWaybillApprovalRecordResponse>> pageDtoJdResult = reassignWaybillService.getReassignWaybillRecordListByPage(query);

        System.out.println(JSON.toJSONString(pageDtoJdResult));

    }

}
