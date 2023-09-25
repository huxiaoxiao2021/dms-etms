package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackage;
import com.jd.bluedragon.distribution.jy.enums.MixBoxTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

@Service
@Slf4j
public class JyCollectPackageServiceImpl implements JyCollectPackageService{

    @Autowired
    private SortingService sortingService;
    @Autowired
    private BoxService boxService;
    @Autowired
    private WaybillCacheService waybillCacheService;
    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;
    @Autowired
    private BaseService baseService;
    @Override
    public InvokeResult<CollectPackageResp> collectScan(CollectPackageReq request) {
        //基础校验
        collectPackageBaseCheck(request);
        //集包业务校验
        collectPackageBizCheck(request);
        //执行集包
        CollectPackageResp response =new CollectPackageResp();
        execCollectPackage(request,response);

        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,response);
    }

    private void execCollectPackage(CollectPackageReq request, CollectPackageResp response) {

    }

    private void collectPackageBizCheck(CollectPackageReq request) {
        //校验箱号：是否存在 +是否已打印+状态合法性+是否已经发货
        boxCheck(request);
        //流向校验
        flowCheck(request);
        //sorting拦截器链
        execIntecepterChain(request);
    }

    private void flowCheck(CollectPackageReq request) {
        JyBizTaskCollectPackage collectPackageTask =jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (ObjectHelper.isEmpty(collectPackageTask)){
            throw new JyBizException("集包任务不存在或者已经过期，请刷新界面！");
        }
        if (request.getForceCollectPackage()){
            request.setEndSiteId(collectPackageTask.getEndSiteId());
            return;
        }

        //查询包裹路由信息
        String router =waybillCacheService.getRouterByWaybillCode(WaybillUtil.getWaybillCode(request.getPackageCode()));
        if (!ObjectHelper.isNotNull(router)){
            log.info("未获取到运单的路由信息,startSiteId:{}",request.getCurrentOperate().getSiteCode());
            throw new JyBizException("未获取到运单的路由信息！");
        }
        if (MixBoxTypeEnum.MIX_DISABLE.getCode().equals(collectPackageTask.getMixBoxType())){
            //校验路由是否存在于允许集包的流向集合中
            List<Integer> flowList =new ArrayList<>();
            flowList.add(collectPackageTask.getEndSiteId().intValue());
            checkRouterIfExitInCollectFlowList(router,flowList,request,collectPackageTask);
        }
        else {
            //查询可集的流向集合信息
            List<Integer> flowList =queryMixBoxFlowList();//TODO
            if (CollectionUtils.isEmpty(flowList)){
                BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseService.getSiteBySiteID(collectPackageTask.getEndSiteId().intValue());
                if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())){
                    throw new JyBizException(FORCE_COLLECT_PACKAGE_WARNING,"路由节点不在允许集包的流向内，是否强制集往【"+baseStaffSiteOrgDto.getSiteName()+"】？");
                }
            }
            //校验路由信息是否 在可集包的流向集合内
            checkRouterIfExitInCollectFlowList(router,flowList,request,collectPackageTask);
        }
    }

    private List<Integer> queryMixBoxFlowList() {
        return null;
    }

    private void checkRouterIfExitInCollectFlowList(String router, List<Integer> flowList,CollectPackageReq request,JyBizTaskCollectPackage collectPackageTask) {
        List<Integer> nextNodeList =getNextNodeList(request.getCurrentOperate().getSiteCode(),router);
        if (CollectionUtils.isEmpty(nextNodeList)){
            log.info("集包扫描获取路由信息：current:{},router:{}",request.getCurrentOperate().getSiteCode(),router);
            throw new JyBizException("未获取到有效的路由节点信息！");
        }
        boolean collectEnable =false;
        for (Integer nextNode :nextNodeList){
            if (flowList.contains(nextNode)){
                request.setEndSiteId(Long.valueOf(nextNode));
                collectEnable =true;
                break;
            }
        }
        if (!collectEnable){
            BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseService.getSiteBySiteID(collectPackageTask.getEndSiteId().intValue());
            if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())){
                throw new JyBizException(FORCE_COLLECT_PACKAGE_WARNING,"路由节点不在允许集包的流向内，是否强制集往【"+baseStaffSiteOrgDto.getSiteName()+"】？");
            }
            throw new JyBizException("路由节点不在允许集包的流向内，集包失败！");
        }
    }

    private List<Integer> getNextNodeList(Integer startSiteId, String router) {
        return null;
    }


    private void execIntecepterChain(CollectPackageReq request) {
        if (request.getSkipInterceptChain()){
            return;
        }
        PdaOperateRequest pdaOperateRequest =assemblePdaOperateRequest(request);
        SortingJsfResponse response =sortingService.check(pdaOperateRequest);
        if(!Objects.equals(response.getCode(), SortingResponse.CODE_OK)){
            if(response.getCode() >= 30000 && response.getCode() <= 40000){
               throw new JyBizException(CONFIRM_COLLECT_PACKAGE_WARNING,response.getMessage());
            }
            //todo 存储拦截扫描记录
            throw new JyBizException(response.getMessage());
        }

    }

    private PdaOperateRequest assemblePdaOperateRequest(CollectPackageReq request) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setBoxCode(request.getBoxCode());
        //pdaOperateRequest.setBusinessType(checkRequest.getBusinessType());
        /*if(request.getIsGather() == null){
            pdaOperateRequest.setIsGather(0);
        }else{
            pdaOperateRequest.setIsGather(request.getIsGather());
        }*/
        //pdaOperateRequest.setOperateType(request.getOperateType());
        pdaOperateRequest.setPackageCode(request.getPackageCode());
        pdaOperateRequest.setReceiveSiteCode(request.getEndSiteId().intValue());
        pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
        pdaOperateRequest.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(),DateUtil.FORMAT_DATE_TIME));
        pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
        return pdaOperateRequest;
    }

    private void boxCheck(CollectPackageReq request) {
        Box box = boxService.findBoxByCode(request.getBoxCode());
        if (box == null) {
            throw new JyBizException("该箱号不存在或者已过期！");
        } else if (Box.STATUS_DEFALUT.intValue() == box.getStatus().intValue()) {
            throw new JyBizException("该箱号未打印！");
        }
        //判断箱子是否已发货
        if (boxService.checkBoxIsSent(request.getBoxCode(), box.getCreateSiteCode())) {
            throw new JyBizException("该箱号已经发货，禁止继续集包！");
        }
    }

    private void collectPackageBaseCheck(CollectPackageReq request) {
        if (!ObjectHelper.isNotNull(request.getBizId())){
            throw new JyBizException("参数错误：缺失任务bizId！");
        }
        if (!ObjectHelper.isNotNull(request.getBoxCode())){
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBoxCode())){
            throw new JyBizException("参数错误：不支持该类型箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getPackageCode())){
            throw new JyBizException("参数错误：缺失包裹号！");
        }
        if (!WaybillUtil.isPackageCode(request.getPackageCode())){
            throw new JyBizException("参数错误：包裹号类型错误，请扫描正确的包裹号码！");
        }
    }

    private void collectBaseCheck(CollectPackageReq request) {
    }


    @Override
    public InvokeResult<CollectPackageTaskResp> listCollectPackageTask(CollectPackageTaskReq request) {
        return null;
    }

    @Override
    public InvokeResult<TaskDetailResp> queryTaskDetail(TaskDetailReq request) {
        return null;
    }

    @Override
    public InvokeResult<SealingBoxResp> sealingBox(SealingBoxReq request) {
        return null;
    }

    @Override
    public InvokeResult<BindCollectBagResp> bindCollectBag(BindCollectBagReq request) {
        return null;
    }

    @Override
    public InvokeResult<CancelCollectPackageResp> cancelCollectPackage(CancelCollectPackageReq request) {
        return null;
    }
}
