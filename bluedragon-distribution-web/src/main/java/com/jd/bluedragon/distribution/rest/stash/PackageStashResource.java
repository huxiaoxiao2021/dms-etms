package com.jd.bluedragon.distribution.rest.stash;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.stash.domain.EMGGoodsInfoDto;
import com.jd.bluedragon.distribution.stash.domain.PackageStashRequest;
import com.jd.bluedragon.distribution.stash.service.PackageStashService;
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
import java.util.List;

/**
 * <P>
 *     收纳暂存需求
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/6/10
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class PackageStashResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageStashResource.class);

    @Autowired
    private PackageStashService packageStashService;

    /**
     * 根据一定数量请求EMG条码
     * @param request
     */
    @Path("/packageStash/genEMGCode")
    @POST
    public InvokeResult<List<String>> genEMGCode(PackageStashRequest request) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        if (null == request || null == request.getNumber()) {
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        result.setData(packageStashService.genEMGCodeByNum(request.getNumber()));
        return result;
    }


    /**
     * 根据EMG条码获取条码打印数据
     * @param request emgCode
     */
    @Path("/packageStash/getGoodsInfoByEMGCode")
    @POST
    public InvokeResult<EMGGoodsInfoDto> getGoodsInfoByEMGCode(PackageStashRequest request) {
        InvokeResult<EMGGoodsInfoDto> result = new InvokeResult<>();
        if (null == request || StringHelper.isEmpty(request.getEmgCode())) {
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }

        result.setData(packageStashService.getGoodsInfoByEMGCode(request.getEmgCode()));
        return result;
    }

}
