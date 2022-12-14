package com.jd.bluedragon.distribution.rest.abnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.config.constants.StrandReasonBusinessTagEnum;
import com.jd.bluedragon.distribution.config.model.ConfigStrandReason;
import com.jd.bluedragon.distribution.config.query.ConfigStrandReasonQuery;
import com.jd.bluedragon.distribution.config.service.ConfigStrandReasonService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ldop.utils.CollectionUtils;
import com.jd.ql.dms.common.cache.CacheKeyGenerator;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_PARAMETER_ERROR_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;

/**
 * 包裹滞留
 * @date 2020/3/10.
 * @author jinjingcheng
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class StrandResouce {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StrandService strandService;
    
    @Autowired
    private ConfigStrandReasonService configStrandReasonService;
    @Autowired
    private GroupBoardManager groupBoardManager;
	
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    /**
     * 包裹滞留上报
     * @param request
     * @return
     */
    @POST
    @Path("strand/report")
    @JProfiler(jKey = "DMS.WEB.StrandResouce.report", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> report(StrandReportRequest request){
        InvokeResult<Boolean> invokeResult = checkParam(request);
        if(RESULT_SUCCESS_CODE != invokeResult.getCode()){
            return invokeResult;
        }
        boolean lockFlag = false;
        String cacheKey = String.format(CacheKeyConstants.CACHE_KEY_FORMAT_STRAND_REPORT, request.getSiteCode(), request.getBarcode());
        try {
        	//箱、板、批次上报，加锁防止现场多次重复操作
        	if(ReportTypeEnum.BATCH_NO.getCode().equals(request.getReportType())
        			||ReportTypeEnum.BOX_CODE.getCode().equals(request.getReportType())
        			||ReportTypeEnum.BOARD_NO.getCode().equals(request.getReportType())) {
        		lockFlag = jimdbCacheService.setNx(cacheKey, Constants.FLAG_OPRATE_ON, DateHelper.FIVE_MINUTES_SECONDS);
        		if(!lockFlag) {
        			invokeResult.error("该" + ReportTypeEnum.getReportTypeName(request.getReportType()) + "已滞留上报，后台处理中请稍后");
        		}
        	}
            //判断一下
            if (ReportTypeEnum.BOARD_NO.getCode().equals(request.getReportType())){
                log.info("============按板进行滞留上报================");
                if (!BusinessUtil.isBoardCode(request.getBarcode())){
                    log.info("============按板进行滞留上报================非板号");
                    Response<Board> boardResponse= groupBoardManager.getBoardByBoxCode(request.getBarcode(),request.getSiteCode());
                    if (!JdCResponse.CODE_SUCCESS.equals(boardResponse.getCode())){
                        invokeResult.error("未根据箱号/包裹号找到匹配的板号！");
                        return invokeResult;
                    }
                    log.info("============按板进行滞留上报================非板号,找到板号{}",boardResponse.getData().getCode());
                    request.setBarcode(boardResponse.getData().getCode());
                }
            }
        	//查询滞留原因，设置同步标识，后续mq处理以提交时标识状态为准
        	Result<ConfigStrandReason> reasonInfo = configStrandReasonService.queryByReasonCode(request.getReasonCode());
        	if(reasonInfo == null
        			|| !reasonInfo.isSuccess()
        			|| reasonInfo.getData() == null) {
        		invokeResult.error("滞留上报异常,无效的原因类型！");
        		return invokeResult;
        	}
        	ConfigStrandReason reasonData = reasonInfo.getData();
        	request.setSyncFlag(reasonData.getSyncFlag());
            //发送滞留上报消息
        	InvokeResult<Boolean> sendResult = strandService.sendStrandReportJmq(request);
        	if(InvokeResult.RESULT_SUCCESS_CODE != sendResult.getCode()) {
        		invokeResult.error(sendResult.getMessage());
        		return invokeResult;
        	}
        }catch (Exception e){
            log.error("滞留上报异常,请求参数：{}", JsonHelper.toJson(request),e);
            invokeResult.error("滞留上报异常,请联系分拣小秘！");
        } finally {
			if(lockFlag) {
				this.jimdbCacheService.del(cacheKey);
			}
		}
        //按批次或板号提交 单独提示
        if(ReportTypeEnum.BATCH_NO.getCode().equals(request.getReportType())
                || ReportTypeEnum.BOARD_NO.getCode().equals(request.getReportType())){
            invokeResult.setMessage("提交成功，如需取消发货或封车，请手动操作！");
            return invokeResult;
        }
        boolean hasSend = strandService.hasSenddetail(request);
        String message = "滞留上报成功";
        if(hasSend){
            message += "，该" + ReportTypeEnum.getReportTypeName(request.getReportType()) + "发货已被取消";
        }
        invokeResult.setMessage(message);
        return invokeResult;
    }





    /**
     * 参数检查
     * @param request
     * @return
     */
    private InvokeResult<Boolean> checkParam(StrandReportRequest request){
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        invokeResult.setCode(RESULT_PARAMETER_ERROR_CODE);
        if(request == null){
            invokeResult.setMessage("异常上报请求参数null");
            log.warn("异常上报请求参数为空");
            return invokeResult;
        }
        String barcode = request.getBarcode();
        if(StringUtils.isBlank(barcode)){
            invokeResult.setMessage("异常上报请求参数错误，条码为空");
            log.warn("异常上报请求参数错误，条码为空");
            return invokeResult;
        }
        if(request.getReasonCode() == null){
            invokeResult.setMessage("异常上报原因为空，请选择");
            log.warn("异常上报原因为空");
            return invokeResult;
        }
        Integer reportType = request.getReportType();
        if(reportType == null){
            invokeResult.setMessage("上报的条码类型为空");
            log.warn("上报的条码类型为空");
            return invokeResult;
        }
        //按包裹操作，条码非包裹号
        if(ReportTypeEnum.PACKAGE_CODE.getCode().equals(reportType) && !WaybillUtil.isPackageCode(barcode)){
            invokeResult.setMessage("你选择的按包裹上报，请扫描包裹号");
            log.warn("按包裹上报，条码{}非包裹号", barcode);
            return invokeResult;
        }
        //按运单操作，条码非运单号
        if(ReportTypeEnum.WAYBILL_CODE.getCode().equals(reportType) && !WaybillUtil.isWaybillCode(barcode)){
            invokeResult.setMessage("你选择的按运单上报，请扫描运单号");
            log.warn("按包裹上报，条码{}非包裹号", barcode);
            return invokeResult;
        }
        //按箱号操作，条码非包裹号
        if(ReportTypeEnum.BOX_CODE.getCode().equals(reportType) && !BusinessUtil.isBoxcode(barcode)){
            invokeResult.setMessage("你选择的按箱上报，请扫描箱号");
            return invokeResult;
        }
        //按批次操作，条码非批次号
        if(ReportTypeEnum.BATCH_NO.getCode().equals(reportType) && !BusinessUtil.isSendCode(barcode)){
            invokeResult.setMessage("你选择的按批次上报，请扫描批次号");
            return invokeResult;
        }
        invokeResult.success();
        return invokeResult;
    }
    /**
     * 查询原因列表
     * 查询默认
     * @param
     * @return
     */
    @POST
    @Path("strand/queryReasonList")
    public InvokeResult<List<ConfigStrandReasonData>> queryReasonList(){
        Set<Integer> businessTagSet = new HashSet<>(1);
        businessTagSet.add(StrandReasonBusinessTagEnum.BUSINESS_TAG_DEFAULT.getCode());
        return this.queryBaseReasonList(businessTagSet);
    }

    /**
     * 查询原因列表
     * 默认 + 冷链
     * @return
     */
    @POST
    @Path("strand/queryAllReasonList")
    public InvokeResult<List<ConfigStrandReasonData>> queryAllReasonList(){
        Set<Integer> businessTagSet = new HashSet<>(2);
        businessTagSet.add(StrandReasonBusinessTagEnum.BUSINESS_TAG_DEFAULT.getCode());
        businessTagSet.add(StrandReasonBusinessTagEnum.BUSINESS_TAG_COLD.getCode());
        return this.queryBaseReasonList(businessTagSet);
    }

    /**
     * businessTag为空默认查全部
     * @param businessTagSet 业务标识，1：默认，2：冷链
     * @return
     */
    private InvokeResult<List<ConfigStrandReasonData>> queryBaseReasonList(Set<Integer> businessTagSet){
        InvokeResult<List<ConfigStrandReasonData>> invokeResult = new InvokeResult<>();
        try {
            ConfigStrandReasonQuery query = new ConfigStrandReasonQuery();
            query.setPageNumber(1);
            query.setLimit(100);
            if(!businessTagSet.isEmpty()) {
                query.setBusinessTagList(new ArrayList<Integer>(businessTagSet));
            }
            Result<PageDto<ConfigStrandReason>> resultInfo = configStrandReasonService.queryPageList(query);
            if(resultInfo == null
                    || !resultInfo.isSuccess()
                    || resultInfo.getData() == null) {
                invokeResult.error("获取滞留上报原因列表失败,请联系分拣小秘！");
                return invokeResult;
            }
            invokeResult.setData(toConfigStrandReasonDataList(resultInfo.getData().getResult()));
        }catch (Exception e){
            log.error("获取滞留上报原因异常!",e);
            invokeResult.error("获取滞留上报原因异常,请联系分拣小秘！");
        }
        invokeResult.success();
        return invokeResult;
    }
    /**
     * 列表转换
     * @param records
     * @return
     */
    private List<ConfigStrandReasonData> toConfigStrandReasonDataList(List<ConfigStrandReason> records) {
        List<ConfigStrandReasonData> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(records)) {
            for(ConfigStrandReason vo : records) {
                ConfigStrandReasonData tmp = new ConfigStrandReasonData();
                tmp.setReasonCode(vo.getReasonCode());
                tmp.setReasonName(vo.getReasonName());
                tmp.setSyncFlag(vo.getSyncFlag());
                tmp.setRemark(vo.getRemark());
                tmp.setOrderNum(vo.getOrderNum());
                list.add(tmp);
            }
        }
        return list;
    }
}
