package com.jd.bd.dms.automatic.marshal.filter;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DmsRestFilter
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/1/3 13:40
 */
public class DmsRestFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(DmsRestFilter.class);
    private static List<String> restUrlList;
    private static final String RESTURL = "DMS_REST_URL";

    private BaseMajorManager baseMajorManager;


    /**
     * 过滤rest请求
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        //目前只拦截分拣、发货、验货三个主要流程
        int count = 0;
        for(String restUrl : restUrlList){
            if(requestURI.indexOf(restUrl) != -1){
                break;
            }
            count ++ ;
        }
        if(count == restUrlList.size()){
            filterChain.doFilter(request,response);
            return;
        }
        //获得rest请求中的登陆人erpCode
        String erpCode = request.getHeader("erpCode");
        try {
            if(StringHelper.isNotEmpty(erpCode)){
                BaseStaffSiteOrgDto basestaffDto = baseMajorManager.getBaseStaffIgnoreIsResignByErp(erpCode);
                if(basestaffDto != null && basestaffDto.getIsResign() != null && basestaffDto.getIsResign() == 1){
                    filterChain.doFilter(request,response);
                    return;
                }
                this.logger.error("该登陆用户:" + erpCode + "未在青龙基础资料中维护！");
                JdResponse jdResponse = new JdResponse(JdResponse.CODE_RESIGNATION,JdResponse.MESSAGE_RESIGNATION);
                showMessage(response,jdResponse);
            }else {
                filterChain.doFilter(request,response);
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
            this.logger.error("通过" + erpCode + "调用基础资料接口失败！");
            JdResponse jdResponse = new JdResponse(JdResponse.CODE_SERVICE_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
            showMessage(response,jdResponse);
        }
    }


    /**
     * 提示前台message
     * @param response
     * @param jdResponse
     * @throws IOException
     */
    private void showMessage(HttpServletResponse response,JdResponse jdResponse) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        String jdResponseJson = JsonHelper.toJson(jdResponse);
        OutputStream out = response.getOutputStream();
        out.write(jdResponseJson.getBytes("UTF-8"));
        out.flush();
    }

    static{
        restUrlList = new ArrayList<String>();
        String restUrl = PropertiesHelper.newInstance().getValue(RESTURL);
        String[] split = restUrl.split(",");
        for(String s : split){
            s = Constants.REST_URL + s;
            restUrlList.add(s);
        }
    }

    protected void initFilterBean() throws ServletException {
        WebApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(this.getFilterConfig().getServletContext());
        this.baseMajorManager = (BaseMajorManager)app.getBean("baseMajorManager");
        super.initFilterBean();
    }
}
