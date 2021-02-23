package com.jd.bluedragon.distribution.rest.transport;

/**
 * Created by xumei3 on 2017/12/29.
 */

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.transport.domain.ARCommonDictionaryType;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.distribution.transport.service.impl.BusTypeService;
import com.jd.ql.dms.common.domain.BusType;
import com.jd.ql.dms.common.domain.City;
import com.jd.ql.dms.common.domain.DictionaryInfoModel;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ArBaseResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ArSendRegisterService arSendRegisterService;

    @Autowired
    private BusTypeService busTypeService;

    @Autowired
    private DmsBaseDictService dmsBaseDictService;

    /**
     * 获取空铁项目的城市信息和车型信息
     *
     * @return
     */
    @GET
    @Path("/arbase/getARCommonDictionaryInfo/")
    @JProfiler(jKey = "DMS.WEB.ArBaseResource.getARCommonDictionaryInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<DictionaryInfoModel> getARCommonDictionaryInfo() {
        this.log.debug("获取空铁项目城市信息和摆渡车信息列表");

        List<DictionaryInfoModel> result = new ArrayList<DictionaryInfoModel>();

        //1-查询发货登记表获取始发城市id和始发城市名称
        List<City> startCities = arSendRegisterService.queryStartCityInfo();
        for (City city : startCities) {
            result.add(new DictionaryInfoModel(city.getCityId(), city.getCityName(), ARCommonDictionaryType.CITY.getType()));
        }

        //2-获取摆渡车型信息
        List<BusType> allBusTypes = busTypeService.getNeedsBusType();
        for (BusType busType : allBusTypes) {
            result.add(new DictionaryInfoModel(busType.getBusTypeId(), busType.getBusTypeName(), ARCommonDictionaryType.BUS_TYPE.getType()));
        }
        //货物类型
        List<DmsBaseDict> dmsBaseDictList = dmsBaseDictService.queryLowerLevelListByTypeCode(Constants.BASEDICT_GOODS_TYPE_TYPECODE);
        for (DmsBaseDict dmsBaseDict : dmsBaseDictList) {
            result.add(new DictionaryInfoModel(dmsBaseDict.getTypeCode(), dmsBaseDict.getTypeName(), ARCommonDictionaryType.GOODS_TYPE.getType()));
        }

        Collections.sort(result);
        return result;
    }

}

