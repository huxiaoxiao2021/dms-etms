package com.jd.bluedragon.utils.jsf;
 
import com.jd.jsf.gd.client.GroupRouter;
import com.jd.jsf.gd.config.ConsumerGroupConfig;
import com.jd.jsf.gd.msg.Invocation;
import com.jdl.sorting.tech.tenant.core.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2024/1/18
 * @Description:
 *
 *  JSF路由工具类 针对不同租户使用
 *
 */
public class GroupRouterTenantUtil implements GroupRouter {

    private static final Logger logger = LoggerFactory.getLogger(GroupRouterTenantUtil.class);

    public GroupRouterTenantUtil(String tenantJsfAliasConfigKey) {
        this.tenantJsfAliasConfigKey = tenantJsfAliasConfigKey;
    }

    /**
     * 获取租户配置的JSF别名配置KEY
     */
    private String tenantJsfAliasConfigKey;


    /**
     * 获取对应服务路由别名
     * @param invocation
     * @param config
     * @return
     */
    @Override
    public String router(Invocation invocation, ConsumerGroupConfig config) {

        TenantContext.getTenantCode();

        // 不使用动态添加分组模式，直接返回所需别名，不依赖JSF路由配置
        // 此处需要替换成根据租户配置表动态获取的


        return "xxx3";
    }

    public String getTenantJsfAliasConfigKey() {
        return tenantJsfAliasConfigKey;
    }
}