package com.jd.bluedragon.distribution.rest.cyclebox;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.box.response.BoxCodeGroupBinDingDto;
import com.jd.bluedragon.common.dto.box.response.BoxDto;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.OrderBindMessageRequest;
import com.jd.bluedragon.distribution.api.request.WaybillCodeListRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.box.BCGroupBinDingDto;
import com.jd.bluedragon.distribution.api.response.box.GroupBoxDto;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.CycleBox;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.impl.FuncSwitchConfigServiceImpl;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CycleBoxResource {
    private final Logger log = LoggerFactory.getLogger(CycleBoxResource.class);

    @Autowired
    CycleBoxService cycleBoxService;

    @Autowired
    private FuncSwitchConfigServiceImpl funcSwitchConfigService;

    @Autowired
    private BoxResource boxResource;

    /**
     * 快运发货获取青流箱数量
     *
     * @param request
     * @return
     */
    @POST
    @Path("/cycleBox/getCycleBoxNum")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101501)
    @JProfiler(jKey = "DMS.WEB.CycleBoxResource.getCycleBoxNum", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CycleBox> getCycleBoxNum(List<DeliveryRequest> request) {
        if(log.isInfoEnabled()){
            this.log.info("快运发货获取青流箱数量:{}", JsonHelper.toJson(request));
        }
        InvokeResult<CycleBox> result = new InvokeResult<CycleBox>();
        try {
            result.setData(cycleBoxService.getCycleBoxNum(request));
        } catch (Exception e) {
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }

    @POST
    @Path("/cycleBox/pushClearBoxStatus")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101502)
    @JProfiler(jKey = "DMS.WEB.CycleBoxResource.pushClearBoxStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult pushClearBoxStatus(WaybillCodeListRequest request) {
        InvokeResult result = new InvokeResult();
        try {
            //参数校验
            if (request == null || request.getWaybillCodeList() == null || request.getSiteCode() == null) {
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage(InvokeResult.PARAM_ERROR);
                return result;
            }

            //生成任务
            cycleBoxService.addCycleBoxStatusTask(request);
        } catch (Exception e) {
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 通过流水号获取青流箱明细
     *
     * @param batchCode
     * @return
     */
    @GET
    @Path("/cycleBox/getCycleBoxByBatchCode/{batchCode}")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101503)
    @JProfiler(jKey = "DMS.WEB.CycleBoxResource.getCycleBoxByBatchCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CycleBox> getCycleBoxByBatchCode(@PathParam("batchCode") String batchCode) {
        this.log.info("通过流水号获取青流箱明细，流水号:{}", batchCode);
        InvokeResult<CycleBox> result = new InvokeResult<CycleBox>();

        if (StringUtils.isBlank(batchCode)) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        try {
            result.setData(cycleBoxService.getCycleBoxByBatchCode(batchCode));
        } catch (Exception e) {
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 根据箱号获取箱号绑定的集包袋
     * @param boxCode
     * @return
     */
    @GET
    @Path("/cycleBox/getBoxMaterialRelation/{boxCode}")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101504)
    @JProfiler(jKey = "DMS.WEB.CycleBoxResource.getBoxMaterialRelation", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<String> getBoxMaterialRelation(@PathParam("boxCode") String boxCode) {
        InvokeResult<String> result = new InvokeResult<String>();
        result.success();

        if (StringUtils.isBlank(boxCode)) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        try {
            result.setData(cycleBoxService.getBoxMaterialRelation(boxCode));
        } catch (Exception e) {
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 绑定、删除集包袋
     * @param request
     * @return
     */
    @POST
    @Path("/cycleBox/boxMaterialRelationAlter")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101505)
    @JProfiler(jKey = "DMS.WEB.CycleBoxResource.boxMaterialRelationAlter", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult boxMaterialRelationAlter(BoxMaterialRelationRequest request) {
        InvokeResult result = new InvokeResult();
        result.success();

        try {
            //参数校验
            if (request == null || StringUtils.isBlank(request.getBoxCode()) || StringUtils.isBlank(request.getMaterialCode()) || request.getBindFlag() <1 || request.getSiteCode()==null || request.getSiteCode()<0) {
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage(InvokeResult.PARAM_ERROR);
                return result;
            }

            //执行数据库操作
            result = cycleBoxService.boxMaterialRelationAlter(request);
        } catch (Exception e) {
            log.error("绑定循环集包袋系统异常请求参数:{}", JsonHelper.toJson(request), e);
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 外单靑流箱绑定发MQ
     * @param request
     * @return
     */
    @POST
    @Path("/cycleBox/cycleBoxBindToWD")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101506)
    @JProfiler(jKey = "DMS.WEB.CycleBoxResource.cycleBoxBindToWD", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult cycleBoxBindToWD(OrderBindMessageRequest request) {
        InvokeResult result = new InvokeResult();
        result.success();

        try {
            //参数校验
            if (request == null || StringUtils.isBlank(request.getWaybillNo()) || StringUtils.isBlank(request.getOperatorErp()) || null == request.getSealNos() || request.getSealNos().size()<=0 || request.getSiteCode()==null || request.getSiteCode()<0) {
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage(InvokeResult.PARAM_ERROR);
                return result;
            }

            result = cycleBoxService.cycleBoxBindToWD(request);
        } catch (Exception e) {
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 获取BC箱号类型强制绑定循环集包袋拦截状态
     * @param siteCode
     * @return ture 拦截 false 不拦截
     */
    @GET
    @Path("/cycleBox/getInterceptStatus/{siteCode}")
    @JProfiler(jKey = "DMS.WEB.CycleBoxResource.getInterceptStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean>  getInterceptStatus(@PathParam("siteCode") Integer siteCode){
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        Boolean flag = Boolean.TRUE;
        try{
            //默认拦截
            flag = funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(),siteCode);
        }catch (Exception e){
            log.error("获取站点{}循环集包袋绑定拦截开关异常",siteCode,e);
            result.error("获取站点拦截开关异常");
        }
        result.setData(flag);
        return  result;
    }

    /**
     * 获取BC 同组箱号 绑定和未绑定循环集包袋集合
     * @param request
     * @return
     */
    @POST
    @Path("/cycleBox/checkGroupBingResult")
    @JProfiler(jKey = "DMS.WEB.CycleBoxResource.checkGroupBingResult", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<BoxCodeGroupBinDingDto>  checkGroupBingResult(BoxMaterialRelationRequest request){
        InvokeResult<BoxCodeGroupBinDingDto> invokeResult = new InvokeResult<BoxCodeGroupBinDingDto>();
        invokeResult.success();
        try {
            //参数校验
            if (request == null || StringUtils.isBlank(request.getBoxCode())  || null == request.getSiteCode()) {
                invokeResult.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                invokeResult.setMessage(InvokeResult.PARAM_ERROR);
                return invokeResult;
            }

            // 1.校验单个箱号绑定情况
            invokeResult = cycleBoxService.checkBingResult(request);
            if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                return invokeResult;
            }

            // 2.开启同组扫描的
            if(request.getGroupSearch()){
                // 获取一组箱号有效的去判断
                InvokeResult<List<String>> result = boxResource.getAllGroupBoxes(request.getBoxCode());

                if(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE && CollectionUtils.isNotEmpty(result.getData()) ){

                    List<String> boxLists = result.getData();
                    List<GroupBoxDto> groupBoxDtoList = new ArrayList<>();
                    for(String item : boxLists){
                        BoxResponse boxResponse = boxResource.validation(item,2);
                        if(Objects.equals(boxResponse.getCode(),BoxResponse.CODE_OK)){
                            groupBoxDtoList.add(packageGroupBoxDto(boxResponse));
                        }
                    }
                    //当单个箱号未绑定不需要拦截时创建对象分组数据对象
                    if(invokeResult.getData() == null){
                        invokeResult.setData(new BoxCodeGroupBinDingDto());
                    }

                    invokeResult.getData().setGroupList(getBoxList(groupBoxDtoList));
                    invokeResult.getData().setGroupTotal(groupBoxDtoList.size());

                    //当绑定开关开启校验一组箱号绑定情况
                    if(CollectionUtils.isNotEmpty(groupBoxDtoList) && getBCBoxFilterFlag(request.getSiteCode())){
                        request.setGroupList(groupBoxDtoList);
                        InvokeResult<BCGroupBinDingDto> groupBingResult  = cycleBoxService.checkGroupBingResult(request);
                        if(groupBingResult.getCode()!= InvokeResult.RESULT_SUCCESS_CODE){
                            invokeResult.customMessage(groupBingResult.getCode(),groupBingResult.getMessage());
                            return invokeResult;
                        }

                        if(groupBingResult.getData()!=null){
                            invokeResult.getData().setBinDingList(getBoxList(groupBingResult.getData().getBinDingList()));
                            invokeResult.getData().setNoBinDingList(getBoxList(groupBingResult.getData().getNoBingDingList()));
                        }
                    }

                }else {
                    invokeResult.customMessage(InvokeResult.RESULT_NO_GROUP_CODE,InvokeResult.RESULT_NO_GROUP_MESSAGE);
                }
            }
        }catch (Exception e){
            log.error("获取同组箱号绑定循环集包袋状态异常",e);
            invokeResult.error("获取同组箱号绑定循环集包袋状态异常");
        }
        return invokeResult;
    }

    //封装后台查询用的对象
    private GroupBoxDto packageGroupBoxDto(BoxResponse boxResponse) {
        GroupBoxDto groupBoxDto = new GroupBoxDto();
        groupBoxDto.setBoxType(boxResponse.getType());
        groupBoxDto.setBoxCode(boxResponse.getBoxCode());
        groupBoxDto.setCreateSiteCode(boxResponse.getCreateSiteCode());
        groupBoxDto.setCreateSiteName(boxResponse.getCreateSiteName());
        groupBoxDto.setReceiveSiteCode(boxResponse.getReceiveSiteCode());
        groupBoxDto.setReceiveSiteName(boxResponse.getReceiveSiteName());
        groupBoxDto.setSiteType(boxResponse.getSiteType());
        groupBoxDto.setTransportType(boxResponse.getTransportType());
        return groupBoxDto;
    }

    private boolean getBCBoxFilterFlag(Integer siteCode){
       return funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(),siteCode);
    }

    private List<BoxDto> getBoxList(List<GroupBoxDto> boxDtoList) {
        List<BoxDto> list = new ArrayList<>();
        for(GroupBoxDto boxDto :boxDtoList){
            list.add(packageBoxDto(boxDto));
        }
        return list;
    }

    // 给安卓返回使用的对象
    private BoxDto packageBoxDto(GroupBoxDto groupBoxDto){
        BoxDto boxDto = new BoxDto();
        boxDto.setBoxCode(groupBoxDto.getBoxCode());
        boxDto.setBoxType(groupBoxDto.getBoxType());
        boxDto.setCreateSiteCode(groupBoxDto.getCreateSiteCode());
        boxDto.setCreateSiteName(groupBoxDto.getCreateSiteName());
        boxDto.setReceiveSiteCode(groupBoxDto.getReceiveSiteCode());
        boxDto.setReceiveSiteName(groupBoxDto.getReceiveSiteName());
        boxDto.setSiteType(groupBoxDto.getSiteType());
        boxDto.setTransportType(groupBoxDto.getTransportType());
        return boxDto;
    }

}
