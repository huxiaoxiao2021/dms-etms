package com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service.impl;

import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service.MergeWaybillCodeReturnService;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;


/**
 * @ClassName: 123
 * @Description: 123
 * @author: hujiping
 * @date: 2018/9/17 20:32
 */
public class MergeWaybillCodeReturnServiceImpl implements MergeWaybillCodeReturnService{

    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public Boolean compare(ReturnSignatureMessageDTO data, ReturnSignatureMessageDTO secondData) {

        try{
            Class<? extends ReturnSignatureMessageDTO> dataClass = data.getClass();
            Class<? extends ReturnSignatureMessageDTO> secondDataClass = secondData.getClass();
            Field[] dataFields = dataClass.getDeclaredFields();
            Field[] secondDataFields = secondDataClass.getDeclaredFields();
            for(int i=0;i<dataFields.length;i++){
                for(int j=0;j<secondDataFields.length;j++){
                    //属性名相同
                    if(dataFields[i].getName().equals(secondDataFields[j].getName())){
                        //属性值不同
                        if(!compareTwo(dataFields[i].get(data),secondDataFields[j].get(secondData))){
                            return false;
                        }
                    }
                }
            }
        }catch (Exception e){
            this.logger.error("比较两对象出错",e);
            return false;
        }
        return true;
    }

    //对比两个数据是否内容相同
    public static boolean compareTwo(Object object1, Object object2) {

        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 == "" && object2 == null) {
            return true;
        }
        if (object1 == null && object2 == "") {
            return true;
         }
        if (object1 == null && object2 != null) {
            return false;
        }
        if (object1.equals(object2)) {
            return true;
        }
        return false;
    }


}
