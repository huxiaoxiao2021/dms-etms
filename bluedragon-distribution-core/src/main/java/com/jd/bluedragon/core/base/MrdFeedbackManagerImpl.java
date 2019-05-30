package com.jd.bluedragon.core.base;

import com.jd.fastjson.JSONObject;
import com.jd.fastjson.TypeReference;
import com.jd.mrd.delivery.rpc.sdk.dto.RpcResultDto;
import com.jd.mrd.delivery.rpc.sdk.feedback.MrdFeedbackRpcService;
import com.jd.mrd.delivery.rpc.sdk.feedback.dto.UserFeedbackContent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName MrdFeedbackManagerImpl
 * @date 2019/5/23
 */
@Service("mrdFeedbackManager")
public class MrdFeedbackManagerImpl implements MrdFeedbackManager {

    private Log log = LogFactory.getLog(MrdFeedbackManagerImpl.class);

    @Autowired
    @Qualifier("mrdFeedbackRpcService")
    private MrdFeedbackRpcService mrdFeedbackRpcService;

    @Override
    public boolean saveFeedback(UserFeedbackContent userFeedbackContent) {
        if (userFeedbackContent != null) {
            RpcResultDto<Object> resultDto = mrdFeedbackRpcService.saveFeedback(userFeedbackContent);
            if (resultDto.getCode() == 0) {
                return true;
            } else {
                log.warn("提交意见反馈信息失败，失败状态:" + resultDto.getCode() + "，失败信息:" + resultDto.getMsg());
            }
        }
        return false;
    }

    @Override
    public Map<Integer, String> getFeedType(String packageName) {
        if (StringUtils.isNotEmpty(packageName)) {
            RpcResultDto<String> resultDto = mrdFeedbackRpcService.getFeedType(packageName);
            if (resultDto.getCode() == 0) {
                HashMap<Integer, String> typeMap = JSONObject.parseObject(resultDto.getData(), new TypeReference<HashMap<Integer, String>>() {
                });
                return typeMap;
            } else {
                log.warn("根据包名获取反馈类型失败，失败状态:" + resultDto.getCode() + "，失败信息:" + resultDto.getMsg());
            }
        }
        return null;
    }

    @Override
    public Map<Integer, String> getFeedTypeWithRole(String packageName, String roleIndex) {
        if (StringUtils.isNotEmpty(packageName) && StringUtils.isNotEmpty(roleIndex)) {
            RpcResultDto<String> resultDto = mrdFeedbackRpcService.getFeedTypeWithRole(packageName, roleIndex);
            if (resultDto.getCode() == 0) {
                HashMap<Integer, String> typeMap = JSONObject.parseObject(resultDto.getData(), new TypeReference<HashMap<Integer, String>>() {
                });
                return typeMap;
            } else {
                log.warn("根据包名和物流权限码获取反馈类型信息失败，失败状态:" + resultDto.getCode() + "，失败信息:" + resultDto.getMsg());
            }
        }
        return null;
    }

}
