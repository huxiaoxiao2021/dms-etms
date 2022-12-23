package com.jd.bluedragon.external.crossbow.postal.manager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.distribution.waybill.domain.WaybillInfo;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesCompanyRequest;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesCompanyRequestItem;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesCompanyResponse;
import com.jdl.basic.common.utils.ObjectHelper;
/**
 * 发全程跟踪给外协-邮政平台
 * @author wuyoude
 *
 */
public class EmsTracesCompanyManager extends AbstractPostalCrossbowManager<TracesCompanyRequest, TracesCompanyResponse> {
    
	public EmsTracesCompanyManager(){
        List<String> tracesFiieldList = new ArrayList<String>();
		List<Field> fieldList = ObjectHelper.getAllFieldsList(TracesCompanyRequestItem.class);
        sortByName(fieldList);
        for(Field field : fieldList) {
        	if("serialVersionUID".equals(field.getName())) {
        		continue;
        	}
        	tracesFiieldList.add(field.getName());
        }
		listFieldNameMap.put("traces", tracesFiieldList);
	}
	/**
     * condition 为 WaybillInfo
     * @param condition 相应的条件
     * @see WaybillInfo
     * @return
     */
    @Override
    protected TracesCompanyRequest getMyRequestBody(Object condition) {
    	TracesCompanyRequest request = (TracesCompanyRequest)condition;
    	request.setBrandCode(brandCode);
        return request;
    }
}
