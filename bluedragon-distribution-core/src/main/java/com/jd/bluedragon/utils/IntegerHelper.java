package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import net.sf.json.util.JSONStringer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yangbo7 on 2016/7/7.
 */
public class IntegerHelper {

    private static Logger logger = LoggerFactory.getLogger(IntegerHelper.class);

    public static int integerToInt(Integer value){
        if(value == null){
            return 0;
        }
        return value.intValue();
    }

    /**
     @description:   判断是否在Integer范围内
     @Param:         input-输入的字符串
     @return:
     @Author:        laoqingchang1
     @Date:          2022-7-27 16:25:00
     */
    public static boolean isInIntegerRange(String input){
        if(StringUtils.isEmpty(input)){
            return false;
        }
        try{
            return Integer.parseInt(input) > Constants.NUMBER_ZERO;
        }catch (NumberFormatException e){
            logger.error("IntegerHelper.isInIntegerRange error! input:{} , eMsg:{}"
                ,input,e.getMessage(),e);
            return false;
        }
    }

}
