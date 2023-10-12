package com.jd.bluedragon.utils;

import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.utils.converter.LocalDateConverter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BeanHelper extends BeanUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanHelper.class);

    public static void copyProperties(Object dest, Object orig) {
        //ConvertUtils.register(new StringToDateConverter(), Date.class);
        ConvertUtils.register(new LocalDateConverter(), Date.class);
        ConvertUtils.register(new LongConverter(null), Long.class);
        ConvertUtils.register(new ShortConverter(null), Short.class);
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new DoubleConverter(null), Double.class);
        ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            log.error("BeanHelper.copyProperties()在进行对象属性复制时出现异常", e);
        }
    }
    public static Long getLastOperateInspectionId(List<Inspection> updateList) {
		if(updateList == null || updateList.size() == 0) {
			return null;
		}
		//仅有一条数据
		if(updateList.size() == 1) {
			return updateList.get(0).getInspectionId();
		}
		//多条数据，取operateTime最大的数据
		Inspection lastOperateData = updateList.get(0);
		for(int i=1; i<updateList.size(); i++) {
			Inspection tmpData = updateList.get(i);
			if(lastOperateData.getOperateTime()== null ||
					(tmpData.getOperateTime() != null && tmpData.getOperateTime().after(lastOperateData.getOperateTime()))) {
				lastOperateData = tmpData;
			}
		}
		return lastOperateData.getInspectionId();
	}
	public static Long getLastOperateSortingId(List<Sorting> updateList) {
		if(updateList == null || updateList.size() == 0) {
			return null;
		}
		//仅有一条数据
		if(updateList.size() == 1) {
			return updateList.get(0).getId();
		}
		//多条数据，取operateTime最大的数据
		Sorting lastOperateData = updateList.get(0);
		for(int i=1; i<updateList.size(); i++) {
			Sorting tmpData = updateList.get(i);
			if(lastOperateData.getOperateTime()== null ||
					(tmpData.getOperateTime() != null && tmpData.getOperateTime().after(lastOperateData.getOperateTime()))) {
				lastOperateData = tmpData;
			}
		}
		return lastOperateData.getId();
	}
	public static Long getLastOperateSendDetail(List<SendDetail> updateList) {
		if(updateList == null || updateList.size() == 0) {
			return null;
		}
		//仅有一条数据
		if(updateList.size() == 1) {
			return updateList.get(0).getSendDId();
		}
		//多条数据，取operateTime最大的数据
		SendDetail lastOperateData = updateList.get(0);
		for(int i=1; i<updateList.size(); i++) {
			SendDetail tmpData = updateList.get(i);
			if(lastOperateData.getOperateTime()== null ||
					(tmpData.getOperateTime() != null && tmpData.getOperateTime().after(lastOperateData.getOperateTime()))) {
				lastOperateData = tmpData;
			}
		}
		return lastOperateData.getSendDId();
	}
}
