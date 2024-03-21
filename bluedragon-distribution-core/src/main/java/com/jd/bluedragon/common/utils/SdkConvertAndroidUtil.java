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
     * 将 InvokeWithMsgBoxResult.MsgBox 列表转换为 JdVerifyResponse.MsgBox 列表
     * @param msgBoxs 待转换的 InvokeWithMsgBoxResult.MsgBox 列表
     * @param selfDomFlag 是否为自有域标识
     * @return 转换后的 JdVerifyResponse.MsgBox 列表
     */
    public static List<JdVerifyResponse.MsgBox> convertMsg(List<InvokeWithMsgBoxResult.MsgBox> msgBoxs,boolean selfDomFlag){
        if(CollectionUtils.isEmpty(msgBoxs)){
            return new ArrayList<>(0);
        }
        List<JdVerifyResponse.MsgBox> result = new ArrayList<>(msgBoxs.size());
        for(InvokeWithMsgBoxResult.MsgBox currMsgBox : msgBoxs){
            JdVerifyResponse.MsgBox tempMsgBox = new JdVerifyResponse.MsgBox(convertMsgType(currMsgBox.getType()),currMsgBox.getCode(),currMsgBox.getMsg());
            tempMsgBox.setSelfDomFlag(selfDomFlag);
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
