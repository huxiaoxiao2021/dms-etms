package com.jd.bluedragon.distribution.rest.waybill;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.interceptconfig.domain.InterceptConfigInfo;
import com.jd.bluedragon.distribution.interceptconfig.service.InterceptConfigService;
import com.jd.bluedragon.utils.*;
import com.jd.dms.ver.domain.WaybillInterceptTips;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class WaybillInterceptTipsResource {

    public static final Logger log = LoggerFactory.getLogger(WaybillInterceptTipsResource.class);

    @Autowired
	private VerCommonJsfApiManager verCommonJsfApiManager;

    @Autowired
	private AutomaticSortingExceptionJsfManager automaticSortingExceptionJsfManager;

    @Autowired
	private InterceptConfigService interceptConfigService;
    /**
     * 根据条码获取拦截信息接口
     *
     * @param pdaOperateRequest
     * @return
     */
    @POST
    @Path("/waybill/intercept/tips")
    public JdResult<String> getWaybillAndPack(PdaOperateRequest pdaOperateRequest) {
        JdResult<String> response = new JdResult<>();
        response.toSuccess();
        response.setData("此单没有查询到分拣机拦截相关信息！");
    	// 判断传入参数
        if (pdaOperateRequest == null || StringHelper.isEmpty(pdaOperateRequest.getPackageCode()) || pdaOperateRequest.getCreateSiteCode() == null) {
			response.toFail("分拣机一键排障获取拦截信息失败，请检查参数！");
            return response;
        }
        String interceptCode = null;
        String interceptMessage = null;
        // 调用服务
        try {
            List<WaybillInterceptTips> list = verCommonJsfApiManager.getSortMachineInterceptTips(pdaOperateRequest.getPackageCode());

            if (list != null && ! list.isEmpty()) {
				interceptCode = list.get(0).getCode();
				interceptMessage = list.get(0).getMessage();
            } else {
				List<com.jd.bd.dms.automatic.sdk.modules.sorting.entity.WaybillInterceptTips> tipsList = automaticSortingExceptionJsfManager.getSortingExceptionTips(pdaOperateRequest.getPackageCode(), pdaOperateRequest.getCreateSiteCode());
				if (tipsList != null && ! tipsList.isEmpty()) {
					interceptCode = tipsList.get(0).getCode();
					interceptMessage = tipsList.get(0).getMessage();
				}
			}

			InterceptConfigInfo interceptConfigInfo = interceptConfigService.getInterceptConfigInfoByCode(interceptCode);

			if (interceptConfigInfo != null) {
				response.setData((interceptMessage != null ? interceptMessage : interceptConfigInfo.getInterceptMessage()) + ";" + interceptConfigInfo.getGuidanceNotes());
			}

        } catch (Exception e) {
            // 调用服务异常
            log.error("分拣机一键排障获取拦截信息异常！参数：{)", JsonHelper.toJson(pdaOperateRequest), e);
        }
        return response;
    }

}
