package com.jd.bluedragon.distribution.abnormalDispose.service;

import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeCondition;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeInspection;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeMain;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeSend;
import com.jd.bluedragon.distribution.abnormalDispose.service.AbnormalDisposeService;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年06月18日 09时:52分
 */
@Service("abnormalDisposeService")
public class abnormalDisposeServiceImpl implements AbnormalDisposeService{
    @Override
    public PagerResult<AbnormalDisposeInspection> queryInspection(AbnormalDisposeCondition abnormalDisposeCondition) {
        AbnormalDisposeInspection abnormalDisposeInspection=new AbnormalDisposeInspection();
        abnormalDisposeInspection.setWaybillCode("kkkk");
        abnormalDisposeInspection.setCreateUser("tcq");
        abnormalDisposeInspection.setEndCityName("上海");
        abnormalDisposeInspection.setQcCode("ddd");
        PagerResult<AbnormalDisposeInspection> pagerResult=new PagerResult<AbnormalDisposeInspection>();
        pagerResult.setTotal(50);
        List<AbnormalDisposeInspection>  r=new ArrayList<AbnormalDisposeInspection>();
        r.add(abnormalDisposeInspection);
        pagerResult.setRows(r);
        return pagerResult;
    }

    @Override
    public PagerResult<AbnormalDisposeMain> queryMain(AbnormalDisposeCondition abnormalDisposeCondition) {

        AbnormalDisposeMain abnormalDisposeMain=new AbnormalDisposeMain();
        abnormalDisposeMain.setAreaId(1);
        abnormalDisposeMain.setAreaName("西北");
        abnormalDisposeMain.setDateTime(new Date());
        abnormalDisposeMain.setNotReceiveDisposeNum(10);
        abnormalDisposeMain.setNotReceiveNum(20);
        abnormalDisposeMain.setNotReceiveProcess(50);
        abnormalDisposeMain.setNotSendNum(15);
        abnormalDisposeMain.setNotSendDisposeNum(8);
        abnormalDisposeMain.setNotSendProcess(40);
        abnormalDisposeMain.setSiteCode(333);
        abnormalDisposeMain.setSiteName("分拣中心");
        abnormalDisposeMain.setTransferNo("ssssss");
        abnormalDisposeMain.setTransferStartTime(new Date());
        abnormalDisposeMain.setTransferEndTime(new Date());
        abnormalDisposeMain.setTotalProcess(30);
        PagerResult<AbnormalDisposeMain> pagerResult=new PagerResult<AbnormalDisposeMain>();
        pagerResult.setTotal(50);
        List<AbnormalDisposeMain>  r=new ArrayList<AbnormalDisposeMain>();
        r.add(abnormalDisposeMain);
        pagerResult.setRows(r);
        return pagerResult;
    }

    @Override
    public PagerResult<AbnormalDisposeSend> querySend(AbnormalDisposeCondition abnormalDisposeCondition) {

        AbnormalDisposeSend aa=new AbnormalDisposeSend();
       aa.setAbnormalReason1("一级原因");
       aa.setAbnormalType("1");
       aa.setCreateTime(new Date());
       aa.setEndCityName("北京");
       aa.setEndSiteName("目的站点");
       aa.setInspectionDate(new Date());
        PagerResult<AbnormalDisposeSend> pagerResult=new PagerResult<AbnormalDisposeSend>();
        pagerResult.setTotal(50);
        List<AbnormalDisposeSend>  r=new ArrayList<AbnormalDisposeSend>();
        r.add(aa);
        pagerResult.setRows(r);
        return pagerResult;
    }
}
