package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.etms.vts.dto.CommonDto;
import com.jd.etms.vts.dto.DictDto;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.ql.dms.common.domain.BusType;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xumei3 on 2017/12/28.
 */
@Service("busTypeService")
public class BusTypeServiceImpl implements BusTypeService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String BUSDICTIONARY_PARENTCODE = "1019";
    public static final String BUSDICTIONARY_DICTGROUP = "1019";
    public static final Integer BUSDICTIONARY_DICTLEVEL = 2;

    @Autowired
    private VtsQueryWS vtsQueryWS;

    /**
     * 所需要的摆渡车系车型id
     */
    private static List<Integer> usefulBusTypeIds;

    static {
        usefulBusTypeIds = new ArrayList<Integer>();

        //读取配置文件的信息
        String usefulBusTypeStr = PropertiesHelper.newInstance()
                .getValue("usefulBusTypes");

        //转换成整形
        String [] busTypeConfig = usefulBusTypeStr.split(",");
        for(String busType : busTypeConfig){
            usefulBusTypeIds.add(Integer.parseInt(busType));
        }
    }

    public List<BusType> getAllBusType(){
        List<BusType> busTypes = new ArrayList<BusType>();
        try {
            //调用运输接口获取所有的车型信息，参数：parentCode =1019,dictLevel =2, dictGroup =1019
            CommonDto<List<DictDto>> commonDtoList = vtsQueryWS.getDictList(BUSDICTIONARY_PARENTCODE,
                    BUSDICTIONARY_DICTLEVEL, BUSDICTIONARY_DICTGROUP);
            if(commonDtoList == null || commonDtoList.getCode() != 1){
                log.warn("调用运输接口获取车型失败:{}" , JsonHelper.toJson(commonDtoList));
                return busTypes;
            }
            if(commonDtoList.getData() == null || commonDtoList.getData().size() < 1){
                log.warn("调用运输接口获取的车型信息为空：{}" , JsonHelper.toJson(commonDtoList));
                return busTypes;
            }
            List<DictDto> dictDtoList =  commonDtoList.getData();
            for (DictDto dto : dictDtoList) {
                busTypes.add(new BusType(Integer.parseInt(dto.getDictCode()),dto.getDictName()));
            }
        }catch(Exception e){
            log.error("BusTypeServiceImpl.getAllBusType()调用运输接口获取车型失败,信息为：", e);
        }

        return busTypes;
    }

    /**
     * 根据配置文件获取需要的车型，并进行过滤
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BusTypeServiceImpl.getNeedsBusType", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<BusType> getNeedsBusType(){
        List<BusType> needsBusTypes = new ArrayList<BusType>();
        //获取所有摆渡车车型信息
        List<BusType> allBusTypes = getAllBusType();

        //便利所有摆渡车车型进行过滤
        for(BusType busType : allBusTypes){
            if(usefulBusTypeIds.contains(busType.getBusTypeId())){
                needsBusTypes.add(busType);
            }
        }
        return needsBusTypes;
    }
}
