package com.jd.bluedragon.distribution.rest.cyclebox;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.box.response.BoxCodeGroupBinDingDto;
import com.jd.bluedragon.common.dto.box.response.BoxDto;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.OrderBindMessageRequest;
import com.jd.bluedragon.distribution.api.request.WaybillCodeListRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.constants.BoxTypeEnum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.CycleBox;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.impl.FuncSwitchConfigServiceImpl;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CycleBoxResource {
    private final Logger log = LoggerFactory.getLogger(CycleBoxResource.class);

    @Autowired
    CycleBoxService cycleBoxService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private FuncSwitchConfigServiceImpl funcSwitchConfigService;

    /**
     * 快运发货获取青流箱数量
     *
     * @param request
     * @return
     */
    @POST
    @Path("/cycleBox/getCycleBoxNum")
    @BusinessLog(sourceSys = 1,bizType = 1015,operateType = 101501)
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
    @Path("/boxes/getInterceptStatus")
    @JProfiler(jKey = "DMS.WEB.BoxResource.getInterceptStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean>  getInterceptStatus(@QueryParam("siteCode") Integer siteCode){
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
     * @param groupList
     * @return
     */
    @POST
    @Path("/boxes/checkGroupBingResult")
    @JProfiler(jKey = "DMS.WEB.BoxResource.checkGroupBingResult", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<BoxCodeGroupBinDingDto>  checkGroupBingResult(List<String> groupList){
        InvokeResult<BoxCodeGroupBinDingDto> invokeResult = new InvokeResult<BoxCodeGroupBinDingDto>();
        invokeResult.success();
        try {
            BoxCodeGroupBinDingDto boxCodeGroupBinDingDto = new BoxCodeGroupBinDingDto();
            boxCodeGroupBinDingDto.setGroupTotal(groupList.size());
            List<BoxDto> noBinDingList = new ArrayList<BoxDto>(); // 没绑定的
            List<BoxDto> bingDingList = new ArrayList<BoxDto>(); // 绑定的

            for (String  boxCode: groupList){
                Box box = boxService.findBoxByCode(boxCode);
                if(box==null){
                    log.error("箱号:{}详细获取失败",boxCode);
                    continue;
                }

                // 只统计BC 箱号类型
                if(box.getType().equals(BoxTypeEnum.TYPE_BC.getCode())){
                    BoxDto boxdto = new BoxDto();
                    boxdto.setBoxCode(boxCode);
                    boxdto.setBoxType(box.getType());
                    String materialCode =  cycleBoxService.getBoxMaterialRelation(boxCode);
                    if(org.springframework.util.StringUtils.isEmpty(materialCode)){
                        noBinDingList.add(boxdto);
                    }else {
                        bingDingList.add(boxdto);
                    }
                }
            }
            boxCodeGroupBinDingDto.setNoBinDingList(noBinDingList);
            boxCodeGroupBinDingDto.setBinDingList(bingDingList);
            invokeResult.setData(boxCodeGroupBinDingDto);
        }catch (Exception e){
            log.error("获取同组箱号绑定循环集包袋状态异常",e);
            invokeResult.error("获取同组箱号绑定循环集包袋状态异常");
        }
        return invokeResult;
    }
}
