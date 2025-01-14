package com.jd.bluedragon.distribution.rest.recyclematerial;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.box.constants.BoxTypeEnum;
import com.jd.bluedragon.dms.utils.RecycleBasketTypeEnum;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketAbolishRequest;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketEntity;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketPrintInfo;
import com.jd.bluedragon.distribution.recycle.material.enums.PrintTypeEnum;
import com.jd.bluedragon.distribution.recycle.material.service.RecycleMaterialService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.RestHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

import static com.jd.bluedragon.dms.utils.RecycleBasketTypeEnum.getFromCode;

/**
 * 循环物资实操装态更新
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年12月10日 11时:16分
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class RecycleMaterialResource {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String url = "/services/recycleMaterial/updateStatus";

    private final static String PREFIX_VER_URL = "DMS_BUSINESS_ADDRESS";

    @Autowired
    RecycleMaterialService recycleMaterialService;


    /**
     * PDA实操节点更新循环物资状态信息
     *
     * @param vo
     * @return
     */
    @POST
    @Path("/recycle/material/updateStatus")
    @JProfiler(jKey = "DMSWEB.RecycleMaterialResource.updateStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse<String> updateStatus(@RequestBody JSONObject vo){

        return RestHelper.jsonPostForEntity(PropertiesHelper.newInstance().getValue(PREFIX_VER_URL) + url, vo, new TypeReference<JdResponse>(){});
    }


    /**
     * 青龙打印客户端获取周转筐打印信息
     * @param recycleBasketEntity
     * @return
     */
    @POST
    @Path(value = "/recycleMaterial/getRecycleBasketPrintInfo")
    @JProfiler(jKey = "DMSWEB.RecycleMaterialResource.getRecycleBasketPrintInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse<RecycleBasketPrintInfo> getRecycleBasketPrintInfo(@RequestBody RecycleBasketEntity recycleBasketEntity){
        JdResponse<RecycleBasketPrintInfo> response = null;
        try {
            response = checkParam(recycleBasketEntity);
            if(!response.isSucceed()){
                return response;
            }
            // 作废周转筐条码
            if (Objects.equals(PrintTypeEnum.DISABLE_AKBOX.getCode(), recycleBasketEntity.getPrintType())) {
                response = recycleMaterialService.disableAkBox(recycleBasketEntity);
            }else {
                // 打印/补打
                response = recycleMaterialService.getPrintInfo(recycleBasketEntity);
            }
            if (response != null && response.getData() != null) {
                RecycleBasketTypeEnum boxTypeEnum = getFromCode(recycleBasketEntity.getTypeCode());
                if (boxTypeEnum != null) {
                    response.getData().setTypeName(boxTypeEnum.getName());
                }else {
                    response.getData().setTypeName("未知类型");
                }
            }
        }catch (Exception e){
            log.error("周转筐获取打印信息异常,请求参数:{}", JsonHelper.toJson(recycleBasketEntity),e);
            response.toError("周转筐获取打印信息异常");
        }
        return response;
    }

    /**
     * 批量作废周转筐
     * 
     * @param request
     * @return
     */
    @POST
    @Path(value = "/recycleMaterial/batchAbolishRecycleBasket")
    @JProfiler(jKey = "DMSWEB.RecycleMaterialResource.batchAbolishRecycleBasket", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse<Boolean> batchAbolishRecycleBasket(@RequestBody RecycleBasketAbolishRequest request){
        JdResponse<Boolean> jdResponse = new JdResponse<>();
        if(request == null || CollectionUtils.isEmpty(request.getRecycleBasketList()) 
                || StringUtils.isEmpty(request.getOperateUserErp()) || request.getOperateSiteCode() == null 
                || StringUtils.isEmpty(request.getBatchFlag())){
            jdResponse.toFail("参数错误");
            return jdResponse;
        }
        return recycleMaterialService.batchAbolishRecycleBasket(request);
    }
    
    private JdResponse<RecycleBasketPrintInfo> checkParam(RecycleBasketEntity recycleBasketEntity){
        JdResponse<RecycleBasketPrintInfo> response = new JdResponse<>();
        response.toSucceed(null);
        //
        if(StringUtils.isBlank(recycleBasketEntity.getUserErp()) || StringUtils.isBlank(recycleBasketEntity.getUserName())){
             response.toFail("获取用户信息失败，请重新登录");
             return response;
        }
        if(PrintTypeEnum.REPRINT.getCode() == recycleBasketEntity.getPrintType()
            ||PrintTypeEnum.DISABLE_AKBOX.getCode() == recycleBasketEntity.getPrintType()){
            if(StringUtils.isBlank(recycleBasketEntity.getRecycleBasketCode())){
                response.toFail("请输入周转筐编码");
                return response;
            }
            if(!BusinessUtil.isBoxcode(recycleBasketEntity.getRecycleBasketCode())
                    || !recycleBasketEntity.getRecycleBasketCode().startsWith(BoxTypeEnum.RECYCLE_BASKET.getCode())){
                 response.toFail("周转筐编码错误，请检查！");
                return response;
            }
        }
        
        if(recycleBasketEntity.getQuantity() > 50){
            recycleBasketEntity.setQuantity(50);
        }
        return response;
    }
}
