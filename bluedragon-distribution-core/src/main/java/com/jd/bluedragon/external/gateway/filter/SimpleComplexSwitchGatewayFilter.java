package com.jd.bluedragon.external.gateway.filter;

import com.jd.bluedragon.core.simpleComplex.SimpleComplexSwitchContext;
import com.jd.bluedragon.core.simpleComplex.SimpleComplexSwitchExecutor;
import com.jd.jsf.gd.filter.AbstractFilter;
import com.jd.jsf.gd.msg.RequestMessage;
import com.jd.jsf.gd.msg.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.util.Map;
import java.util.Objects;

/**
 * jsf接口简繁体切换过滤链
 *
 * @author hujiping
 * @date 2023/7/27 7:55 PM
 */
@Slf4j
public class SimpleComplexSwitchGatewayFilter extends AbstractFilter implements BeanFactoryAware {

    private static final long serialVersionUID = -8946615210781876684L;

    private BeanFactory beanFactory;

    @Override
    public ResponseMessage invoke(RequestMessage request) {

        ResponseMessage response;
        try {
            // 获取jsf简繁标识
            boolean needSwitchFlag = checkNeedSwitch(request);
            // 入参繁to简
            complexToSimple(request, needSwitchFlag);
            // 执行业务逻辑
            response = getNext().invoke(request);
            // 返回值简to繁
            simpleToComplex(response, needSwitchFlag);
        }finally {
            try {
                // 清除标识
                SimpleComplexSwitchContext.clearJsfThreadInfo();
            }catch (Exception e){
                log.error("清除标识失败!");
            }
        }
        return response;
    }

    private boolean checkNeedSwitch(RequestMessage request) {
        Map<String, Object> attachments = request.getInvocationBody().getAttachments();
        return attachments != null && !attachments.isEmpty()
                && attachments.get(SimpleComplexSwitchContext.SIMPLE_COMPLEX_SWITCH_FLAG) != null
                && Objects.equals(SimpleComplexSwitchContext.COMPLEX, String.valueOf(attachments.get(SimpleComplexSwitchContext.SIMPLE_COMPLEX_SWITCH_FLAG)));
    }

    private void complexToSimple(RequestMessage request, boolean needSwitchFlag) {
        if(needSwitchFlag && request.getInvocationBody().getArgs() != null){
            for (Object arg : request.getInvocationBody().getArgs()) {
                getSimpleComplexSwitchExecutor().recursiveDeal(arg, SimpleComplexSwitchContext.SIMPLE_TYPE);
            }
        }
    }

    private void simpleToComplex(ResponseMessage response, boolean needSwitchFlag) {
        if(needSwitchFlag){
            getSimpleComplexSwitchExecutor().recursiveDeal(response.getResponse(), SimpleComplexSwitchContext.COMPLEX_TYPE);
        }
    }

    private SimpleComplexSwitchExecutor getSimpleComplexSwitchExecutor() {
        return (SimpleComplexSwitchExecutor) beanFactory.getBean("simpleComplexSwitchExecutor");
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}