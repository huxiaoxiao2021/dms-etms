package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.etms.vts.dto.CommonDto;
import com.jd.etms.vts.dto.DictDto;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.ql.dms.common.domain.BusType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xumei3 on 2017/12/28.
 */
@Service("busTypeService")
public class BusTypeServiceImpl implements BusTypeService {
    private final Log logger = LogFactory.getLog(this.getClass());

    public static final String BUSDICTIONARY_PARENTCODE = "1019";
    public static final String BUSDICTIONARY_DICTGROUP = "1019";
    public static final Integer BUSDICTIONARY_DICTLEVEL = 2;

    @Autowired
    private VtsQueryWS vtsQueryWS;

    public List<BusType> getAllBusType(){
        List<BusType> busTypes = new ArrayList<BusType>();
        try {
            //调用运输接口获取所有的车型信息，参数：parentCode =1019,dictLevel =2, dictGroup =1019
            CommonDto<List<DictDto>> commonDtoList = vtsQueryWS.getDictList(BUSDICTIONARY_PARENTCODE,
                    BUSDICTIONARY_DICTLEVEL, BUSDICTIONARY_DICTGROUP);
            if(commonDtoList == null || commonDtoList.getCode() != 1){
                logger.error("调用运输接口获取车型失败");
            }
            if(commonDtoList.getData() == null || commonDtoList.getData().size() < 1){
                logger.error("调用运输接口获取的车型信息为空!");
                return busTypes;
            }
            List<DictDto> dictDtoList =  commonDtoList.getData();
            for (DictDto dto : dictDtoList) {
                busTypes.add(new BusType(Integer.parseInt(dto.getDictCode()),dto.getDictName()));
            }
        }catch(Exception e){
            logger.error("BusTypeServiceImpl.getAllBusType()调用运输接口获取车型失败,信息为："+ e.getMessage());
        }

        return busTypes;
    }
}
