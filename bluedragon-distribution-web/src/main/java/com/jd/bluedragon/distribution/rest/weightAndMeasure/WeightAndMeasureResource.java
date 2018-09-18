package com.jd.bluedragon.distribution.rest.weightAndMeasure;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WeightMeasureRequest;
import com.jd.bluedragon.distribution.api.response.DmsOutWeightAndVolumeResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import com.jd.bluedragon.distribution.weightAndMeasure.service.DmsOutWeightAndVolumeService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WeightAndMeasureResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    DmsOutWeightAndVolumeService dmsOutWeightAndVolumeService;

    /**
     * 根据扫描的包裹号/箱号+操作站点获取重量体积信息
     *
     * @param request
     * @return
     */
    @POST
    @Path("/weightAndMeasure/getWeightAndVolume")
    public DmsOutWeightAndVolumeResponse getWeightAndVolume(WeightMeasureRequest request) {
        if (logger.isInfoEnabled()) {
            logger.info(JsonHelper.toJsonUseGson(request));
        }

        DmsOutWeightAndVolumeResponse response = new DmsOutWeightAndVolumeResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        String barCode = request.getBarCode();
        Integer dmsCode = request.getSiteCode();

        //校验barCode和DmsCode是否有效
        if (StringUtils.isBlank(barCode)) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage("请输入包裹号/箱号");
            return response;
        }
        if (dmsCode == null || dmsCode <= 0) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage("分拣中心编码为空");
            return response;
        }

        //从dms_out_weight_volume表中查询本分拣中心是否已经对该barCode进行过称重量方录入操作
        //如果已经录入，返回给pda显示出来；否则返回空
        DmsOutWeightAndVolume weightAndVolume = dmsOutWeightAndVolumeService.getOneByBarCodeAndDms(barCode, dmsCode);
        if (weightAndVolume != null) {
            response.setBarCode(weightAndVolume.getBarCode());
            response.setSiteCode(weightAndVolume.getCreateSiteCode());
            response.setWeight(weightAndVolume.getWeight());
            response.setVolume(weightAndVolume.getVolume());
            response.setWeightUserCode(weightAndVolume.getWeightUserCode());
            response.setWeightUserName(weightAndVolume.getWeightUserName());
            response.setMeasureUserCode(weightAndVolume.getMeasureUserCode());
            response.setMeasureUserName(weightAndVolume.getMeasureUserName());
        }
        return response;
    }

    /**
     * 保存人工测量应付体积
     *
     * @param request
     * @return
     */
    @POST
    @Path("/weightAndMeasure/dmsOutVolumeAdd")
    public DmsOutWeightAndVolumeResponse dmsOutVolumeAdd(WeightMeasureRequest request) {
        if (logger.isInfoEnabled()) {
            logger.info(JsonHelper.toJsonUseGson(request));
        }

        DmsOutWeightAndVolumeResponse response = new DmsOutWeightAndVolumeResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        //参数校验
        String errStr = requestCheck(request);
        if (StringUtils.isNotBlank(errStr)) {
            logger.error("保存人工测量应付体积失败.参数错误:" + errStr + JsonHelper.toJsonUseGson(request));
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(errStr);
            return response;
        }

        //组装对象
        try {
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
        } catch (Exception e) {
            logger.error("保存人工测量应付体积失败.", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage("服务器异常.");
        }

        return response;
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
                errStr += "请扫描箱号/包裹号";
            } else if (!BusinessHelper.isBoxcode(barCode) && !BusinessHelper.isPackageCode(barCode)) {
                errStr += "箱号/包裹号不符合规则";
            }

            if (weightMeasureRequest.getVolume() == null || weightMeasureRequest.getVolume() <= 0) {
                errStr += "请输入体积";
            }
        }

        return errStr;
    }
}
