package com.jd.bluedragon.distribution.rest.express;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.express.domain.ExpressBoxDetailsResponse;
import com.jd.bluedragon.distribution.express.domain.ExpressPackageDetailsResponse;
import com.jd.bluedragon.distribution.express.enums.ExpressStatusTypeEnum;
import com.jd.bluedragon.distribution.express.service.ExpressCollectionService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 快运到齐查询接口
 * Created by zhangleqi on 2017/11/14
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ExpressCollectionResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Resource
    private ExpressCollectionService expressCollectionService;


    /**
     * 获取快运包裹明细信息
     * /express/queryPackageDetails/包裹号或者运单号/包裹扫描状态
     *
     * @param paramQueryCode  运单号或者包裹号
     * @param statusQueryCode 查询状态码
     * @return 响应对象
     */
    @GET
    @Path("/express/queryPackageDetails/{createSiteCode}/{paramQueryCode}/{statusQueryCode}")
    public ExpressPackageDetailsResponse queryPackageDetails(@PathParam("createSiteCode") Integer createSiteCode,@PathParam("paramQueryCode") String paramQueryCode, @PathParam("statusQueryCode") String statusQueryCode) {
        ExpressPackageDetailsResponse expressPackageDetailsResponse = new ExpressPackageDetailsResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        //校验参数是否正确
        checkQueryParams(expressPackageDetailsResponse, paramQueryCode, statusQueryCode);
        if (!JdResponse.CODE_OK.equals(expressPackageDetailsResponse.getCode())) {
            return expressPackageDetailsResponse;
        }

        //判断参数为包裹号还是运单号，如果是包裹号，则获取运单号
        String waybillCode = BusinessHelper.getWaybillCode(paramQueryCode);
        if (null == waybillCode) {
            expressPackageDetailsResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            expressPackageDetailsResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR + "，获取运单信息失败，包裹号为：" + paramQueryCode);
            return expressPackageDetailsResponse;
        }
        expressPackageDetailsResponse = expressCollectionService.findExpressPackageDetails(expressPackageDetailsResponse, createSiteCode, waybillCode, statusQueryCode);

        return expressPackageDetailsResponse;
    }


    /**
     * 获取快运箱明细信息
     * /express/queryPackageDetails/包裹号或者运单号/包裹扫描状态
     *
     * @param paramQueryCode  运单号或者包裹号
     * @param statusQueryCode 查询状态码
     * @return 响应对象
     */
    @GET
    @Path("/express/queryBoxDetails/{createSiteCode}/{paramQueryCode}/{statusQueryCode}")
    public ExpressBoxDetailsResponse queryBoxDetails(@PathParam("createSiteCode") Integer createSiteCode,@PathParam("paramQueryCode") String paramQueryCode, @PathParam("statusQueryCode") String statusQueryCode) {
        ExpressBoxDetailsResponse expressBoxDetailsResponse = new ExpressBoxDetailsResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        //校验参数是否正确
        checkQueryParams(expressBoxDetailsResponse, paramQueryCode, statusQueryCode);
        if (!JdResponse.CODE_OK.equals(expressBoxDetailsResponse.getCode())) {
            return expressBoxDetailsResponse;
        }

        //判断参数为包裹号还是运单号，如果是包裹号，则获取运单号
        String waybillCode = BusinessHelper.getWaybillCode(paramQueryCode);
        if (null == waybillCode) {
            expressBoxDetailsResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            expressBoxDetailsResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR + "，获取运单信息失败，包裹号为：" + paramQueryCode);
            return expressBoxDetailsResponse;
        }
        expressBoxDetailsResponse = expressCollectionService.findExpressBoxDetails(expressBoxDetailsResponse,createSiteCode, waybillCode, statusQueryCode);

        return expressBoxDetailsResponse;
    }
    /**
     * 校验查询参数是否正确
     *
     * @param jdResponse 响应对象
     * @param paramQueryCode                     运单号或者包裹号
     * @param statusQueryCode               查询状态码
     * @return 响应对象
     */
    private JdResponse checkQueryParams(JdResponse jdResponse, String paramQueryCode, String statusQueryCode) {
        //如果参数为空，则提示运单号或包裹号为空格
        if (StringHelper.isEmpty(paramQueryCode)) {
            jdResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR + ",运单号或包裹号为空");
            return jdResponse;
        }

        //判断状态码是否为空，如果为空，则提示选择正确的查询状态
        if (StringHelper.isEmpty(statusQueryCode)) {
            jdResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR + ",请选择正确的查询状态");
            return jdResponse;
        }

        //判断状态码是否为正确的值
        String[] statusCodes = statusQueryCode.split(ExpressPackageDetailsResponse.STATUS_SPLIT_CHAR);
        for (String statusCode : statusCodes) {
            if (!ExpressStatusTypeEnum.expressStatusMap.containsKey(statusCode)) {
                jdResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                jdResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR + ",请选择正确的查询状态");
                return jdResponse;
            }
        }

        return jdResponse;
    }

}