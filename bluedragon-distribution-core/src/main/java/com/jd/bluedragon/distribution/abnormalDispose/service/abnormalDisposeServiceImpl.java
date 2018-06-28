package com.jd.bluedragon.distribution.abnormalDispose.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.distribution.abnormalDispose.dao.AbnormalDisposeDao;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeCondition;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeInspection;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeMain;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeRecord;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeSend;
import com.jd.bluedragon.distribution.abnormalorder.dao.AbnormalOrderDao;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.etms.api.common.dto.PageDto;
import com.jd.etms.api.transferwavemonitor.req.TransferWaveMonitorReq;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorResp;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class abnormalDisposeServiceImpl implements AbnormalDisposeService {

    private static final Log logger = LogFactory.getLog(abnormalDisposeServiceImpl.class);
    @Autowired
    private AbnormalDisposeDao abnormalDisposeDao;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Autowired
    private AbnormalOrderDao abnormalOrderDao;

    @Override
    public PagerResult<AbnormalDisposeInspection> queryInspection(AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeInspection> pagerResult = new PagerResult<AbnormalDisposeInspection>();

        AbnormalDisposeInspection abnormalDisposeInspection = new AbnormalDisposeInspection();
        abnormalDisposeInspection.setWaybillCode("12345");
        abnormalDisposeInspection.setCreateUser("tcq");
        abnormalDisposeInspection.setEndCityName("上海");
        abnormalDisposeInspection.setQcCode("ddd");
        pagerResult.setTotal(50);
        List<AbnormalDisposeInspection> r = new ArrayList<AbnormalDisposeInspection>();
        r.add(abnormalDisposeInspection);
        pagerResult.setRows(r);

        return pagerResult;
    }

    //计算页数
    private Integer getCurrentPage(int offset, int limit) {
        if (offset == 0) {
            return 1;
        }
        if (offset > 0) {
            return offset / limit + 1;
        }
        return 1;
    }

    @Override
    public PagerResult<AbnormalDisposeMain> queryMain(AbnormalDisposeCondition abnormalDisposeCondition) {

        //封装分页参数
        PageDto<TransferWaveMonitorReq> page = new PageDto<TransferWaveMonitorReq>();
        page.setCurrentPage(getCurrentPage(abnormalDisposeCondition.getOffset(),abnormalDisposeCondition.getLimit()));
        page.setPageSize(abnormalDisposeCondition.getLimit());
        //封装查询条件
        TransferWaveMonitorReq parameter=new TransferWaveMonitorReq();
        parameter.setCityId(abnormalDisposeCondition.getCityId());
        parameter.setOrgId(abnormalDisposeCondition.getAreaId());
        parameter.setProvinceId(abnormalDisposeCondition.getProvinceId());
        parameter.setStartDate(abnormalDisposeCondition.getStartTime());
        parameter.setEndDate(abnormalDisposeCondition.getEndTime());
        parameter.setSiteCode(abnormalDisposeCondition.getDmsSiteCode());
        PageDto<TransferWaveMonitorResp> pageDto=vrsRouteTransferRelationManager.getAbnormalTotal(page,parameter);

        PagerResult<AbnormalDisposeMain> pagerResult=new PagerResult<AbnormalDisposeMain>();
        if (pageDto==null){
            pagerResult.setTotal(0);
            pagerResult.setRows(new ArrayList<AbnormalDisposeMain>());
        }else{
            pagerResult.setTotal(pageDto.getTotalRow());
            if (pageDto.getResult()!=null&&pageDto.getResult().size()>0){
                List<String> waybillcodes=Lists.newArrayList();
                for (TransferWaveMonitorResp transferWaveMonitorResp:pageDto.getResult()){
                    waybillcodes.add(transferWaveMonitorResp.getWaveBusinessId());
                }
//                abnormalOrderDao.queryByorderIds()
                List<AbnormalDisposeMain> abnormalDisposeMains= Lists.newArrayList();
                for (TransferWaveMonitorResp transferWaveMonitorResp:pageDto.getResult()){
                    AbnormalDisposeMain abnormalDisposeMain=new AbnormalDisposeMain();
                    abnormalDisposeMain.setWaveBusinessId(transferWaveMonitorResp.getWaveBusinessId());
                    abnormalDisposeMain.setAreaName(transferWaveMonitorResp.getOrgName());
                    abnormalDisposeMain.setTransferStartTime(transferWaveMonitorResp.getPlanStartTime());
                    abnormalDisposeMain.setTransferEndTime(transferWaveMonitorResp.getPlanEndTime());
                    abnormalDisposeMain.setTransferNo(transferWaveMonitorResp.getWaveCode());
                    abnormalDisposeMain.setNotSendNum(transferWaveMonitorResp.getNoSendWaybillCount());
                    abnormalDisposeMain.setNotReceiveNum(transferWaveMonitorResp.getActualArriveNoInspection());

                }
            }else{
                pagerResult.setRows(new ArrayList<AbnormalDisposeMain>());
            }
        }


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
        pagerResult.setTotal(50);
        List<AbnormalDisposeMain>  r=new ArrayList<AbnormalDisposeMain>();
        r.add(abnormalDisposeMain);
        pagerResult.setRows(r);
        return pagerResult;
    }

    @Override
    public PagerResult<AbnormalDisposeSend> querySend(AbnormalDisposeCondition abnormalDisposeCondition) {

        AbnormalDisposeSend aa = new AbnormalDisposeSend();
        aa.setAbnormalReason1("一级原因");
        aa.setAbnormalType("1");
        aa.setCreateTime(new Date());
        aa.setEndCityName("北京");
        aa.setEndSiteName("目的站点");
        aa.setInspectionDate(new Date());
        aa.setWaybillCode("12345");
        PagerResult<AbnormalDisposeSend> pagerResult = new PagerResult<AbnormalDisposeSend>();
        pagerResult.setTotal(50);
        List<AbnormalDisposeSend> r = new ArrayList<AbnormalDisposeSend>();
        r.add(aa);
        pagerResult.setRows(r);
        return pagerResult;
    }

    @Override
    public Integer saveInspection(AbnormalDisposeRecord abnormalDisposeRecord) {
        AbnormalDisposeRecord ab = abnormalDisposeDao.findInspection(abnormalDisposeRecord);
        if (ab == null)
            return abnormalDisposeDao.saveInspection(abnormalDisposeRecord);
        else
            return this.updateInspection(ab);
    }

    @Override
    public Integer updateInspection(AbnormalDisposeRecord abnormalDisposeRecord) {
        return abnormalDisposeDao.updateInspection(abnormalDisposeRecord);
    }
}
