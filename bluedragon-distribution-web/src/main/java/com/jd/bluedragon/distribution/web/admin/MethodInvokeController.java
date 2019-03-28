package com.jd.bluedragon.distribution.web.admin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;

/**
 * Created by dudong on 2016/6/27.
 */
@Controller
@RequestMapping("/admin/method-invoke")
public class MethodInvokeController implements ApplicationContextAware{
    private ApplicationContext context;
    private static final String ENCP_KEY = PropertiesHelper.newInstance().getValue("encpKey");

    @Authorization(Constants.DMS_WEB_DEVELOP_METHOD_INVOKE_R)
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index() {
        return "/tools/online/method_invoke";
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_METHOD_INVOKE_R)
    @RequestMapping(value = "/method/list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,List<String>> getMethodList(String beanName) {
        Map<String,List<String>> methods = new HashMap<String, List<String>>();
        Object bean = context.getBean(getClassByName(beanName.trim()));
        if(null == bean) return methods;
        Method[] declareMethods = bean.getClass().getMethods();
        for(Method method : declareMethods) {
            methods.put(method.getName() + "&" + method.getReturnType().getName(),getParamTypes(method.getParameterTypes()));
        }
        return methods;
    }

    private List<String> getParamTypes(Class[] paramClazz) {
        List<String> paramTypes = new ArrayList<String>();
        for(Class clz : paramClazz) {
            paramTypes.add(clz.getName());
        }
        return paramTypes;
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_METHOD_INVOKE_R)
    @RequestMapping(value = "/method/invoke",method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String invokeMethod(String beanName, String beanMethod, String beanPara, String encpKey) {
        if(StringHelper.isEmpty(beanName) || StringHelper.isEmpty(beanMethod)) {
            return JsonHelper.toJson("参数不符合要求");
        }
        if(!encpKey.trim().equals(ENCP_KEY)) {
            return JsonHelper.toJson("没有权限的调用");
        }
        String[] paramStr = null;
        if(beanPara.indexOf("&") < 0) {
            paramStr = new String[1];
            paramStr[0] = beanPara;
        } else {
            paramStr = beanPara.split("&");
        }
        Object bean = context.getBean(getClassByName(beanName.trim()));
        if(null == bean) return null;
        Method[] declareMethods = bean.getClass().getMethods();
        for(Method method : declareMethods) {
            if(method.getName().equals(beanMethod.trim())) {
                try {
                    Class[] paramClazz = method.getParameterTypes();
                    if(paramClazz.length == 0) {  // 无参函数
                        return JsonHelper.toJson(method.invoke(bean,new Object[0]));
                    }
                    if(paramClazz.length != paramStr.length) {
                        throw new IllegalArgumentException("方法参数不匹配");
                    }
                    Object[] params = new Object[paramClazz.length];
                    for(int i = 0; i < paramClazz.length; i++) {
                        params[i] = JsonHelper.fromJson(paramStr[i], paramClazz[i]);
                    }
                    return JsonHelper.toJson(method.invoke(bean,params));
                } catch (Exception ex) {
                    return JsonHelper.toJson(ex.getMessage());
                }
            }
        }
        return JsonHelper.toJson("无效的调用");
    }

    private Class getClassByName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
