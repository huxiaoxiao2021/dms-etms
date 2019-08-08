package com.jd.bluedragon.distribution.rest.mergeWaybillCodeReturn;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillCodeReturnRequest;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service.MergeWaybillCodeReturnService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ldop.center.api.reverse.dto.WaybillReturnSignatureDTO;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * @ClassName: MergeWaybillCodeReturnResource
 * @Description: 签单返回合单
 * @author: hujiping
 * @date: 2018/9/15 17:07
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class MergeWaybillCodeReturnResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private MergeWaybillCodeReturnService mergeWaybillCodeReturnService;

    /**
     * 判断运单号是否可以合单
     * @param firstWaybillCode 第一个运单号
     * @param secondWaybillCode 扫描的下一个运单号
     * @return
     */
    @GET
    @Path("/checkIsMerge/{firstWaybillCode}/{secondWaybillCode}")
    public JdResponse checkIsMerge(@PathParam("firstWaybillCode") String firstWaybillCode,
                                                      @PathParam("secondWaybillCode") String secondWaybillCode){
        JdResponse result = new JdResponse();
        if(!WaybillUtil.isWaybillCode(firstWaybillCode) ||
                !WaybillUtil.isWaybillCode(secondWaybillCode)){
            this.logger.error(InvokeResult.PARAM_ERROR);
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        try {
            result = mergeWaybillCodeReturnService.checkIsMerge(firstWaybillCode,secondWaybillCode);
        } catch (Exception e) {
            this.logger.error("根据运单号"+firstWaybillCode+"调用外单接口失败",e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }

    /**
     * 合单生成新的运单号
     * @param mergeWaybillCodeReturnRequest
     *  合单对象
     * @return
     */
    @POST
    @Path("/submitMergedWaybillCode")
    public JdResponse mergeWaybill(MergeWaybillCodeReturnRequest mergeWaybillCodeReturnRequest){

        JdResponse result = new JdResponse();
        WaybillReturnSignatureDTO dto = new WaybillReturnSignatureDTO();
        try {
            dto.setOperateTime(new Date(mergeWaybillCodeReturnRequest.getOperateTime()));
            dto.setOperateUnitId(mergeWaybillCodeReturnRequest.getOperateUnitId());
            dto.setOperateUnitType(mergeWaybillCodeReturnRequest.getOperateUnitType());
            dto.setOperateUser(mergeWaybillCodeReturnRequest.getOperatorName());
            dto.setOperateUserId(mergeWaybillCodeReturnRequest.getOperateUserId());
            dto.setWaybillCodeList(mergeWaybillCodeReturnRequest.getWaybillCodeList());
            result = mergeWaybillCodeReturnService.mergeWaybillCode(dto,mergeWaybillCodeReturnRequest);
        }catch (Exception e){
            this.logger.error("通过旧单号集合获取新单号失败"+ JSON.toJSONString(mergeWaybillCodeReturnRequest.getWaybillCodeList()),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}

