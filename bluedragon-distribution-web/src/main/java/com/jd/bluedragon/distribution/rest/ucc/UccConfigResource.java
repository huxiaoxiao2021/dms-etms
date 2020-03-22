package com.jd.bluedragon.distribution.rest.ucc;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.ql.dms.print.utils.ObjectHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by xumei3 on 2017/12/15.
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class UccConfigResource {
    @Resource
    UccPropertyConfiguration uccPropertyConfiguration;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @GET
    @Path("/ucc/get/")
    public String getConfigureValue(@QueryParam("configureKey") String configureKey) {
        String filedName = configureKey;
        String returnValue = null;
        try {
            returnValue = filedName + "="+ObjectHelper.getValue(uccPropertyConfiguration, filedName);
        } catch (Exception e) {
            returnValue = "获取ucc配置失败：" + e.getMessage();
            log.error(returnValue,e);
        }
        return returnValue;
    }

    @GET
    @Path("/ucc/getAll")
    public String getAllConfigure() {
        StringBuilder returnValue = new StringBuilder();
        try {
            Field[] fields = uccPropertyConfiguration.getClass().getDeclaredFields();
            for (Field field : fields) {
                String filedName = field.getName();
                Method method = uccPropertyConfiguration.getClass().getDeclaredMethod("get" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1), null);
                Object value = method.invoke(uccPropertyConfiguration);

                if (value != null) {
                    returnValue.append(filedName + "#" + String.valueOf(value) + "#" + value.getClass().getSimpleName());
                    returnValue.append('\n');
                } else {
                    returnValue.append(filedName + "#"+String.valueOf(value));
                    returnValue.append('\n');
                }
            }
        } catch (Exception e) {
            returnValue.append("获取ucc配置失败：" + e.getMessage());
            returnValue.append('\n');
            log.error(returnValue.toString(),e);
        }
        return returnValue.toString();
    }
}