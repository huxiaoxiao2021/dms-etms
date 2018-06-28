package com.jd.bluedragon.distribution.abnormalDispose.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.distribution.abnormalDispose.dao.AbnormalDisposeDao;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeCondition;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeInspection;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeMain;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalQc;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeSend;
import com.jd.bluedragon.distribution.abnormalorder.dao.AbnormalOrderDao;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.etms.api.common.dto.PageDto;
import com.jd.etms.api.transferwavemonitor.req.TransferWaveMonitorReq;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorResp;
import com.jd.etms.api.common.dto.PageDto;
import com.jd.etms.api.transferwavemonitor.resp.TransferWaveMonitorDetailResp;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年06月18日 09时:52分
 */
@Service("abnormalDisposeService")
public class abnormalDisposeServiceImpl implements AbnormalDisposeService{

    private static final Log logger = LogFactory.getLog(abnormalDisposeServiceImpl.class);

    @Autowired
    private AbnormalDisposeDao abnormalDisposeDao;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Autowired
    private SiteService siteService;

    /**
     * 运单路由字段使用的分隔符
     */
    private static final  String WAYBILL_ROUTER_SPLITER = "\\|";
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Autowired
    private AbnormalOrderDao abnormalOrderDao;

    @Override
    public PagerResult<AbnormalDisposeInspection> queryInspection(AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeInspection> pagerResult=new PagerResult<AbnormalDisposeInspection>();
        ArrayList<AbnormalDisposeInspection> list =new ArrayList<AbnormalDisposeInspection>();

        PageDto<TransferWaveMonitorDetailResp> page = null;
        page.setPageSize(abnormalDisposeCondition.getLimit());
        page.setCurrentPage(abnormalDisposeCondition.getOffset()/abnormalDisposeCondition.getLimit() + 1);
        PageDto<TransferWaveMonitorDetailResp> noSendDetail = vrsRouteTransferRelationManager.getArrivedButNoCheckDetail(page, abnormalDisposeCondition.getWaveBusinessId());
        ArrayList<String> waybillCodeList = new ArrayList();
        for (TransferWaveMonitorDetailResp detailResp : noSendDetail.getResult()) {
            waybillCodeList.add(detailResp.getWaybillCode());
        }
        //查询路由信息并封装
        Map<String, String> routerByWaybillCodes = jsfSortingResourceService.getRouterByWaybillCodes(waybillCodeList);
        for(String waybillCode : routerByWaybillCodes.keySet()){
            String router = routerByWaybillCodes.get(waybillCode);
            String[] routers = router.split(WAYBILL_ROUTER_SPLITER);
            AbnormalDisposeInspection abnormalDisposeInspection = new AbnormalDisposeInspection();
            BaseStaffSiteOrgDto prevDto = null;
            BaseStaffSiteOrgDto endDto = null;
            if(routers != null && routers.length > 0){
                for (int i = 0; i < routers.length-1; i++){
                    if(abnormalDisposeCondition.getDmsSiteCode() == routers[i]){
                        //上级站点信息
                        prevDto = siteService.getSite(Integer.valueOf(routers[i-1]));
                        //目的站点信息
                        endDto = siteService.getSite(Integer.valueOf(routers[routers.length-1]));
                    }
                }
            }
            //运单号
            abnormalDisposeInspection.setWaybillCode(waybillCode);
            //上级站点、区域
            abnormalDisposeInspection.setPrevSiteCode(prevDto.getSiteCode().toString());
            abnormalDisposeInspection.setPrevSiteName(prevDto.getSiteName());
            abnormalDisposeInspection.setPrevAreaId(Integer.valueOf(prevDto.getAreaId()+""));
            abnormalDisposeInspection.setPrevAreaName(prevDto.getAreaName());
            //目的站点、城市
            abnormalDisposeInspection.setEndSiteCode(endDto.getSiteCode().toString());
            abnormalDisposeInspection.setEndSiteName(endDto.getSiteName());
            abnormalDisposeInspection.setEndCityId(endDto.getCityId());
            abnormalDisposeInspection.setEndCityName(endDto.getCityName());
            list.add(abnormalDisposeInspection);
        }
        //查库获得异常编码
        List<AbnormalQc> qcCodeList = abnormalDisposeDao.queryQcCodes(waybillCodeList);
        ArrayList<AbnormalDisposeInspection> list1 =new ArrayList<AbnormalDisposeInspection>();
        for(AbnormalQc  abnormalQc : qcCodeList){
            for(AbnormalDisposeInspection abnormalDisposeInspection : list){
                if(abnormalQc.getWaybillCode() == abnormalDisposeInspection.getWaybillCode()){
                    abnormalDisposeInspection.setQcCode(abnormalQc.getQcCode());
                    list1.add(abnormalDisposeInspection);
                }
                break;
            }
        }
        ArrayList<AbnormalDisposeInspection> list2 =new ArrayList<AbnormalDisposeInspection>();
        for(AbnormalDisposeInspection abnormalDisposeInspection : list){
            for(TransferWaveMonitorDetailResp transferWaveMonitorDetailResp : noSendDetail.getResult()){
                if(abnormalDisposeInspection.getWaybillCode() == transferWaveMonitorDetailResp.getWaybillCode()){
                    abnormalDisposeInspection.setSealVehicleDate(transferWaveMonitorDetailResp.getRealTime());
                    abnormalDisposeInspection.setWaveBusinessId(abnormalDisposeCondition.getWaveBusinessId());
                    list2.add(abnormalDisposeInspection);
                }
                break;
            }
        }
        pagerResult.setTotal(list2.size());
        pagerResult.setRows(list2);
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
        PagerResult<AbnormalDisposeMain> pagerResult=new PagerResult<AbnormalDisposeMain>();
        pagerResult.setTotal(50);
        List<AbnormalDisposeMain>  r=new ArrayList<AbnormalDisposeMain>();
        r.add(abnormalDisposeMain);
        pagerResult.setRows(r);
        return pagerResult;
    }

    @Override
    public PagerResult<AbnormalDisposeSend> querySend(AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeSend> pagerResult=new PagerResult<AbnormalDisposeSend>();
        ArrayList<AbnormalDisposeSend> list =new ArrayList<AbnormalDisposeSend>();

        PageDto<TransferWaveMonitorDetailResp> page = null;
        page.setPageSize(abnormalDisposeCondition.getLimit());
        page.setCurrentPage(abnormalDisposeCondition.getOffset()/abnormalDisposeCondition.getLimit() + 1);
        PageDto<TransferWaveMonitorDetailResp> noSendDetail = vrsRouteTransferRelationManager.getNoSendDetail(page, abnormalDisposeCondition.getWaveBusinessId());
        ArrayList<String> waybillCodeList = new ArrayList();
        for (TransferWaveMonitorDetailResp detailResp : noSendDetail.getResult()) {
            waybillCodeList.add(detailResp.getWaybillCode());
        }
        //查询路由信息并封装
        Map<String, String> routerByWaybillCodes = jsfSortingResourceService.getRouterByWaybillCodes(waybillCodeList);
        for(String waybillCode : routerByWaybillCodes.keySet()){
            String router = routerByWaybillCodes.get(waybillCode);
            String[] routers = router.split(WAYBILL_ROUTER_SPLITER);
            BaseStaffSiteOrgDto nextDto = null;
            BaseStaffSiteOrgDto endDto = null;
            AbnormalDisposeSend abnormalDisposeSend = new AbnormalDisposeSend();
            if(routers != null && routers.length > 0){
                for(int i = 0; i < routers.length-1; i++){
                    if(abnormalDisposeCondition.getDmsSiteCode() == routers[i]){
                        //下级站点信息
                        nextDto = siteService.getSite(Integer.valueOf(routers[i+1]));
                        //目的站点信息
                        endDto = siteService.getSite(Integer.valueOf(routers[routers.length-1]));
                    }
                }
            }
            //运单号
            abnormalDisposeSend.setWaybillCode(waybillCode);
            //上级站点、区域
            abnormalDisposeSend.setNextSiteCode(nextDto.getSiteCode().toString());
            abnormalDisposeSend.setNextSiteName(nextDto.getSiteName());
            abnormalDisposeSend.setNextAreaId(Integer.valueOf(nextDto.getAreaId()+""));
            abnormalDisposeSend.setNextAreaName(nextDto.getAreaName());
            //目的站点、城市
            abnormalDisposeSend.setEndSiteCode(endDto.getSiteCode().toString());
            abnormalDisposeSend.setEndSiteName(endDto.getSiteName());
            abnormalDisposeSend.setEndCityId(endDto.getCityId());
            abnormalDisposeSend.setEndCityName(endDto.getCityName());
            list.add(abnormalDisposeSend);
        }
        //查询inspection获得验货时间


        pagerResult.setRows(list);
        pagerResult.setTotal(list.size());
        return pagerResult;
    }

    @Override
    public Integer saveInspection(AbnormalQc abnormalQc) {
        AbnormalQc ab = abnormalDisposeDao.findInspection(abnormalQc);
        if(ab == null)
            return abnormalDisposeDao.saveInspection(abnormalQc);
        else
            return this.updateInspection(ab);
    }

    @Override
    public Integer updateInspection(AbnormalQc abnormalQc) {
        return abnormalDisposeDao.updateInspection(abnormalQc);
    }
}
