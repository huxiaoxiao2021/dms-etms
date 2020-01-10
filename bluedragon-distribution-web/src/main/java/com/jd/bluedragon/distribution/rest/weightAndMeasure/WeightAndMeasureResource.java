package com.jd.bluedragon.distribution.rest.weightAndMeasure;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WeightMeasureRequest;
import com.jd.bluedragon.distribution.api.response.DmsOutWeightAndVolumeResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import com.jd.bluedragon.distribution.weightAndMeasure.service.DmsOutWeightAndVolumeService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.transboard.api.dto.BoardMeasureDto;
import com.jd.transboard.api.dto.BoardMeasureRequest;
import com.jd.transboard.api.dto.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WeightAndMeasureResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DmsOutWeightAndVolumeService dmsOutWeightAndVolumeService;

    @Autowired
    BoardCombinationService boardCombinationService;

    /**
     * 根据扫描的包裹号/箱号+操作站点获取重量体积信息
     *
     * @param request
     * @return
     */
    @POST
    @Path("/weightAndMeasure/getWeightAndVolume")
    public JdResult<DmsOutWeightAndVolumeResponse> getWeightAndVolume(WeightMeasureRequest request) {
        if (log.isInfoEnabled()) {
            log.info(JsonHelper.toJsonUseGson(request));
        }

        JdResult<DmsOutWeightAndVolumeResponse> result = new JdResult<DmsOutWeightAndVolumeResponse>(JdResult.CODE_SUC, JdResult.MESSAGE_SUC.getMsgCode(), JdResult.MESSAGE_SUC.getMsgFormat());

        String barCode = request.getBarCode();
        Integer dmsCode = request.getSiteCode();

        //校验barCode和DmsCode是否有效
        if (StringUtils.isBlank(barCode)) {
            result.setCode(JdResponse.CODE_PARAM_ERROR);
            result.setMessage("请输入板号/箱号/包裹号");
            return result;
        }
        if (dmsCode == null || dmsCode <= 0) {
            result.setCode(JdResponse.CODE_PARAM_ERROR);
            result.setMessage("分拣中心编码为空");
            return result;
        }

        //从dms_out_weight_volume表中查询本分拣中心是否已经对该barCode进行过称重量方录入操作
        //如果已经录入，返回给pda显示出来；否则返回空
        DmsOutWeightAndVolumeResponse data = new DmsOutWeightAndVolumeResponse();

        if (BusinessUtil.isBoxcode(request.getBarCode()) || WaybillUtil.isPackageCode(request.getBarCode())) {
            DmsOutWeightAndVolume weightAndVolume = dmsOutWeightAndVolumeService.getOneByBarCodeAndDms(barCode, dmsCode);
            if (weightAndVolume != null) {
                data.setBarCode(weightAndVolume.getBarCode());
                data.setSiteCode(weightAndVolume.getCreateSiteCode());
                data.setWeight(weightAndVolume.getWeight());
                data.setVolume(weightAndVolume.getVolume());
                data.setWeightUserCode(weightAndVolume.getWeightUserCode());
                data.setWeightUserName(weightAndVolume.getWeightUserName());
                data.setMeasureUserCode(weightAndVolume.getMeasureUserCode());
                data.setMeasureUserName(weightAndVolume.getMeasureUserName());

            }
        }else{
            //如果扫描的是板体积，调用TC接口查询是否已经测量过
            List<String> barCodeList = new ArrayList<String>();
            barCodeList.add(request.getBarCode());
            List<BoardMeasureDto> boardMeasureDtoList = null;
            try{
                boardMeasureDtoList = boardCombinationService.getBoardVolumeByBoardCode(barCodeList);
            }catch (Exception e){
                log.error("调用TC接口查询板的体积信息异常.",e);
            }
            if(boardMeasureDtoList != null && boardMeasureDtoList.size() > 0){
                BoardMeasureDto dto = boardMeasureDtoList.get(0);
                data.setBarCode(dto.getBoardCode());
                data.setWeight(dto.getWeight()/1000);
                data.setVolume(dto.getVolume());
            }
        }

        result.setData(data);
        return result;
    }

    /**
     * 保存人工测量应付体积
     *
     * @param request
     * @return
     */
    @POST
    @Path("/weightAndMeasure/dmsOutVolumeAdd")
    public JdResult dmsOutVolumeAdd(WeightMeasureRequest request) {
        if (log.isInfoEnabled()) {
            log.info(JsonHelper.toJsonUseGson(request));
        }

        JdResult result = new JdResult(JdResult.CODE_SUC, JdResult.MESSAGE_SUC.getMsgCode(), JdResult.MESSAGE_SUC.getMsgFormat());

        //参数校验
        String errStr = requestCheck(request);
        if (StringUtils.isNotBlank(errStr)) {
            log.warn("保存人工测量应付体积失败.参数错误:{}-{}",errStr, JsonHelper.toJsonUseGson(request));
            result.toFail(JdResult.CODE_FAIL, errStr);
            return result;
        }

        try {
            //如果扫描的是箱号/包裹号，存储到分拣的表dms_out_weight_volume
            if (BusinessUtil.isBoxcode(request.getBarCode()) || WaybillUtil.isPackageCode(request.getBarCode())) {
                boxOrPackageMeasureProcess(request);
            } else {
                //如果扫描的是板号，调TC的接口进行持久化
                Response response = boardMeasureProcess(request);
                if(response.getCode()!= 200){
                    result.setCode(response.getCode());
                    result.setMessage(response.getMesseage());
                }
            }
        } catch (Exception e) {
            log.error("保存人工测量应付体积失败.", e);
            result.toError(JdResult.CODE_ERROR, "服务器异常");
        }

        return result;
    }

    /**
     * 包裹和箱的应付体积处理
     * 持久化到dms_out_weight_volume表
     * @param request
     */
    private void boxOrPackageMeasureProcess(WeightMeasureRequest request) {
        //组装对象
        Date now = new Date();
        DmsOutWeightAndVolume dmsOutWeightAndVolume = new DmsOutWeightAndVolume();
        dmsOutWeightAndVolume.setBarCode(request.getBarCode());
        if (BusinessHelper.isBoxcode(request.getBarCode())) {
            dmsOutWeightAndVolume.setBarCodeType(DmsOutWeightAndVolume.BARCODE_TYPE_BOXCODE);
        } else {
            dmsOutWeightAndVolume.setBarCodeType(DmsOutWeightAndVolume.BARCODE_TYPE_PACKAGECODE);
        }

        //设置重量及称重操作人和操作时间
        if (request.getWeight() != null && request.getWeight() > 0) {
            dmsOutWeightAndVolume.setWeight(NumberHelper.doubleFormat(request.getWeight()));
            dmsOutWeightAndVolume.setWeightUserCode(request.getUserCode());
            dmsOutWeightAndVolume.setWeightUserName(request.getUserName());
            if (StringUtils.isNotBlank(request.getOperateTime())) {
                dmsOutWeightAndVolume.setWeightTime(DateHelper.parseAllFormatDateTime(request.getOperateTime()));
            } else {
                dmsOutWeightAndVolume.setWeightTime(now);
            }
        }

        //设置体积及量方操作人和操作时间
        dmsOutWeightAndVolume.setVolume(NumberHelper.doubleFormat(request.getVolume()));
        dmsOutWeightAndVolume.setMeasureUserCode(request.getUserCode());
        dmsOutWeightAndVolume.setMeasureUserName(request.getUserName());
        if (StringUtils.isNotBlank(request.getOperateTime())) {
            dmsOutWeightAndVolume.setMeasureTime(DateHelper.parseAllFormatDateTime(request.getOperateTime()));
        } else {
            dmsOutWeightAndVolume.setMeasureTime(now);
        }

        //设置操作类型和操作站点
        dmsOutWeightAndVolume.setOperateType(DmsOutWeightAndVolume.OPERATE_TYPE_STATIC);
        dmsOutWeightAndVolume.setCreateSiteCode(request.getSiteCode());

        dmsOutWeightAndVolume.setCreateTime(now);
        dmsOutWeightAndVolume.setUpdateTime(now);

        dmsOutWeightAndVolume.setIsDelete(0);

        //写入数据库
        dmsOutWeightAndVolumeService.saveOrUpdate(dmsOutWeightAndVolume);
    }

    /**
     * 板的应付体积处理
     * 调TC的接口进行持久化
     * @param request
     * @return
     */
    private Response<Void> boardMeasureProcess(WeightMeasureRequest request){
        BoardMeasureRequest boardMeasureRequest = new BoardMeasureRequest();
        boardMeasureRequest.setBoardCode(request.getBarCode());
        //pda没有输入长宽高，直接输入体积
        boardMeasureRequest.setLength(0.0);
        boardMeasureRequest.setWidth(0.0);
        boardMeasureRequest.setHeight(0.0);
        boardMeasureRequest.setVolume(request.getVolume());//单位cm³
        if(request.getWeight() != null){
            boardMeasureRequest.setWeight(request.getWeight() * 1000);//request的参数是kg,TC接收的单位是g
        }else{
            boardMeasureRequest.setWeight(0.0);
        }

        boardMeasureRequest.setOperator(request.getUserName());
        try {
            boardMeasureRequest.setMeasureTime(DateHelper.parseDateTime(request.getOperateTime()).getTime());
        }catch (Exception e) {
            boardMeasureRequest.setMeasureTime(System.currentTimeMillis());
        }

        return boardCombinationService.persistentBoardMeasureData(boardMeasureRequest);
    }


    /**
     * 参数校验
     *
     * @param weightMeasureRequest
     * @return
     */
    private String requestCheck(WeightMeasureRequest weightMeasureRequest) {
        String errStr = "";
        if (weightMeasureRequest == null) {
            errStr = "weightMeasureRequest为空.";
        } else {
            //校验分拣中心编码是否有效
            if (weightMeasureRequest.getSiteCode() == null || weightMeasureRequest.getSiteCode() <= 0) {
                errStr += "分拣中心编码为空";
            }

            //校验扫描到的箱号/包裹号是否满足正则
            String barCode = weightMeasureRequest.getBarCode();
            if (StringUtils.isBlank(barCode)) {
                errStr += "请扫描板号/箱号/包裹号";
            } else if (!BusinessUtil.isBoardCode(barCode) && !BusinessUtil.isBoxcode(barCode) && !WaybillUtil.isPackageCode(barCode)) {
                errStr += "板号/箱号/包裹号不符合规则";
            }

            if (weightMeasureRequest.getVolume() == null || weightMeasureRequest.getVolume() <= 0) {
                errStr += "请输入体积";
            }
        }

        return errStr;
    }
}
