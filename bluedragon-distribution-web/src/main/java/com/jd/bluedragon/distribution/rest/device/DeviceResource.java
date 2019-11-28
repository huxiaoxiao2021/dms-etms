package com.jd.bluedragon.distribution.rest.device;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.device.dto.DeviceInfoDto;
import com.jd.bluedragon.distribution.device.service.DeviceInfoService;
import com.jd.bluedragon.utils.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * <p>
 *     自动化设备查询接口，通过调用自动化的jsf接口查询自动化的设备列表信息
 *
 * @author wuzuxiang
 * @since 2019/11/27
 **/
@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DeviceResource {

    @Autowired
    private DeviceInfoService deviceInfoService;

    @POST
    @Path("/device/getDeviceInfo")
    public InvokeResult<List<DeviceInfoDto>> getDeviceInfo (DeviceInfoDto deviceInfoDto) {
        InvokeResult<List<DeviceInfoDto>> result = new InvokeResult<>();
        if (null == deviceInfoDto || StringHelper.isEmpty(deviceInfoDto.getSiteCode())) {
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        List<DeviceInfoDto> deviceInfoDtos = deviceInfoService.queryDeviceConfigByTypeAndSiteCode
                (deviceInfoDto.getSiteCode(),deviceInfoDto.getDeviceTypeCode());
        if (null == deviceInfoDtos || deviceInfoDtos.size() == 0) {
            result.customMessage(InvokeResult.RESULT_NULL_CODE, InvokeResult.RESULT_NULL_MESSAGE);
        }
        result.setData(deviceInfoDtos);
        return result;
    }
}
