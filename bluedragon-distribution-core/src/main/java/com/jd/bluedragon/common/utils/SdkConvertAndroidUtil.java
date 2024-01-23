package com.jd.bluedragon.common.utils;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;



/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: ql-dms-distribution
 * @Package com.jd.bluedragon.common.utils
 * @Description:
 * @date Date : 2024年01月22日 20:09
 */
public class SdkConvertAndroidUtil {

    /**
     * 将InvokeWithMsgBoxResult.MsgBox列表转换为JdVerifyResponse.MsgBox列表
     * @param msgBoxs 要转换的InvokeWithMsgBoxResult.MsgBox列表
     * @return 转换后的JdVerifyResponse.MsgBox列表
     */
    public static List<JdVerifyResponse.MsgBox> convertMsg(List<InvokeWithMsgBoxResult.MsgBox> msgBoxs){
        if(CollectionUtils.isEmpty(msgBoxs)){
            return new ArrayList<>(0);
        }
        List<JdVerifyResponse.MsgBox> result = new ArrayList<>(msgBoxs.size());
        for(InvokeWithMsgBoxResult.MsgBox currMsgBox : msgBoxs){
            JdVerifyResponse.MsgBox tempMsgBox = new  JdVerifyResponse.MsgBox();
            tempMsgBox.setType(convertMsgType(currMsgBox.getType()));
            tempMsgBox.setCode(currMsgBox.getCode());
            tempMsgBox.setMsg(currMsgBox.getMsg());
            result.add(tempMsgBox);
        }
        return result;
    }

    /**
     * 将InvokeWithMsgBoxResult.MsgBoxTypeEnum类型转换为MsgBoxTypeEnum类型
     * @param type 要转换的InvokeWithMsgBoxResult.MsgBoxTypeEnum类型参数
     * @return 转换后的MsgBoxTypeEnum类型参数
     */
    private static MsgBoxTypeEnum convertMsgType(InvokeWithMsgBoxResult.MsgBoxTypeEnum type){
        MsgBoxTypeEnum result = null;
        switch (type) {
            case PROMPT:
                result = MsgBoxTypeEnum.PROMPT;
                break;
            case WARNING:
                result = MsgBoxTypeEnum.WARNING;
                break;
            case CONFIRM:
                result = MsgBoxTypeEnum.CONFIRM;
                break;
            case INTERCEPT:
                result = MsgBoxTypeEnum.INTERCEPT;
                break;
        }
        return result;
    }
}
