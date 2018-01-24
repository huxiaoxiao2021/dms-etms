package com.jd.bluedragon.distribution.rest.transport;

/**
 * Created by xumei3 on 2017/12/29.
 */

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.transport.domain.ARCommonDictionaryType;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.distribution.transport.service.impl.BusTypeService;
import com.jd.ql.dms.common.domain.BusType;
import com.jd.ql.dms.common.domain.City;
import com.jd.ql.dms.common.domain.DictionaryInfoModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ArBaseResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ArSendRegisterService arSendRegisterService;

    @Autowired
    private BusTypeService busTypeService;


    /**
     * 获取空铁项目的城市信息和车型信息
     * @return
     */
    @GET
    @Path("/arbase/getARCommonDictionaryInfo/")
    public List<DictionaryInfoModel>  getARCommonDictionaryInfo() {
        this.logger.info("获取空铁项目城市信息和摆渡车信息列表");

        List<DictionaryInfoModel>  result = new ArrayList<DictionaryInfoModel>();

        //1-查询发货登记表获取始发城市id和始发城市名称
        List<City> startCities = arSendRegisterService.queryStartCityInfo();
        for (City city : startCities) {
            result.add(new DictionaryInfoModel(city.getCityId(), city.getCityName(), ARCommonDictionaryType.CITY.getType()));
        }

        //2-获取摆渡车型信息
        List<BusType> busTypes = busTypeService.getAllBusType();
        for (BusType busType : busTypes) {
            result.add(new DictionaryInfoModel(busType.getBusTypeId(), busType.getBusTypeName(), ARCommonDictionaryType.BUS_TYPE.getType()));
        }

        return result;
    }

}

