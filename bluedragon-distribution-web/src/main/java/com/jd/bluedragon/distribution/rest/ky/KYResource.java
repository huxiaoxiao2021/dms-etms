package com.jd.bluedragon.distribution.rest.ky;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.ky.domain.KYPrintInfo;
import com.jd.bluedragon.distribution.ky.services.KYPrintService;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class KYResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KYResource.class);

    @Autowired
    private KYPrintService kyPrintService;

    /**
     * 根据批次号获取KY航签单信息
     * @return 返回KY打印信息
     */
    @Path("/KY/print")
    @POST
    public InvokeResult<KYPrintInfo> getKYCodeInfo (KYPrintInfo kyPrintInfo) {

        if (null == kyPrintInfo || StringHelper.isEmpty(kyPrintInfo.getSendCode())) {
            InvokeResult<KYPrintInfo> result = new InvokeResult<>();
            result.parameterError("无效的批次号");
            return result;
        }
        return kyPrintService.getKYPrintInfoBySendCode(kyPrintInfo.getSendCode());
    }
}
