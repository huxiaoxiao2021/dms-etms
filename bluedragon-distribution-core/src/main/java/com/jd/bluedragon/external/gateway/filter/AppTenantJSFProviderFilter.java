package com.jd.bluedragon.external.gateway.filter;

import com.jd.jsf.gd.filter.AbstractFilter;
import com.jd.jsf.gd.msg.RequestMessage;
import com.jd.jsf.gd.msg.ResponseMessage;
import com.jd.jsf.gd.util.StringUtils;
import com.jdl.sorting.tech.jsf.filter.TenantJSFProviderFilter;
import com.jdl.sorting.tech.tenant.core.context.TenantContext;
import com.jdl.sorting.tech.tenant.core.model.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: ql-dms-distribution
 * @Package com.jd.bd.dms.automatic.marshal.filter
 * @Description: 接收分拣app传入的租户编码，专用app
 * @date Date : 2024年01月29日 14:59
 */
public class AppTenantJSFProviderFilter extends AbstractFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantJSFProviderFilter.class);

    private String tenantAppConfigKey;

    public AppTenantJSFProviderFilter(String tenantAppConfigKey){
        this.tenantAppConfigKey = tenantAppConfigKey;
    }

    @Override
    public ResponseMessage invoke(RequestMessage requestMessage) {
        Object attachment = null;
        try {
            attachment = requestMessage.getInvocationBody().getAttachment(tenantAppConfigKey);
            if (attachment != null) {
                Tenant tenant = new Tenant();
                tenant.setCode(String.valueOf(attachment));
                log.debug("调用端传递租户信息：{}", tenant);
                TenantContext.setTenant(tenant);
            }
        } catch (Exception e) {
            log.error("租户信息解析异常，参数：{}", attachment, e);
        }

        ResponseMessage result;
        try {
            result = this.getNext().invoke(requestMessage);
        } finally {
            TenantContext.removeTenant();
        }
        return result;
    }

    public void setTenantAppConfigKey(String tenantAppConfigKey) {
        this.tenantAppConfigKey = tenantAppConfigKey;
    }
}
