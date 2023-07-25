package com.jd.bluedragon.core.jsf.degrade.route;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.HystrixConstants;
import com.jd.bluedragon.configuration.ducc.HystrixRouteDuccPropertyConfiguration;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManagerImpl;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.etms.api.common.dto.CommonDto;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.etms.api.recommendroute.resp.RecommendRouteResp;
import com.jd.etms.sdk.compute.RouteComputeUtil;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.netflix.hystrix.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 使用hystrix 对路由方法queryRecommendRoute封装，使支持自动降级熔断
 * what hystrix https://github.com/Netflix/Hystrix/wiki
 */
public class CommandQueryRecommendRoute extends HystrixCommand<CommonDto<RecommendRouteResp>> {

    private static final Logger log = LoggerFactory.getLogger(CommandQueryRecommendRoute.class);
    private RouteComputeUtil routeComputeUtil;

    private String startNode;
    private String endNodeCode;
    private Date operateTime;
    private RouteProductEnum routeProduct;

    public CommandQueryRecommendRoute(String startNode, String endNodeCode, Date operateTime, RouteProductEnum routeProduct,RouteComputeUtil routeComputeUtil,HystrixRouteDuccPropertyConfiguration configuration) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(HystrixConstants.PRINT_HYSTRIX_COMMAND_GROUPKEY))//如账户服务定义一个group key，订单服务定义另一个group key。
                .andCommandKey(HystrixCommandKey.Factory.asKey("commandQueryRecommendRoute"))//具体命令方法的标识名称，常用于对该命令进行动态参数设置。
                .andCommandPropertiesDefaults(getHystrixCommandProperties(configuration))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(HystrixConstants.PRINT_HYSTRIX_THREADPOOL_KEY))
                .andThreadPoolPropertiesDefaults(getHystrixThreadPoolProperties(configuration))
        );
        this.startNode = startNode;
        this.endNodeCode = endNodeCode;
        this.operateTime = operateTime;
        this.routeProduct = routeProduct;
        this.routeComputeUtil = routeComputeUtil;
    }

    @Override
    public CommonDto<RecommendRouteResp> run() throws Exception {
        CallerInfo info = Profiler.registerInfo("dms.base.routeComputeUtil.rpc.queryRecommendRoute", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            log.info("查询远程路由接口-run-startNode:{},endNodeCode:{},predictSendTime:{},routeProduct:{}"
                    ,startNode,endNodeCode,operateTime.getTime(),routeProduct);
            return routeComputeUtil.queryRecommendRoute(startNode,endNodeCode,operateTime,routeProduct);
        }  catch (Exception e) {
            Profiler.functionError(info);
            log.error("查询远程路由接口异常,参数列表：startNode:{},endNodeCode:{},predictSendTime:{},routeProduct:{}"
                    ,startNode,endNodeCode,operateTime.getTime(),routeProduct,e);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    @Override
    protected CommonDto<RecommendRouteResp> getFallback() {
        log.warn("CommandQueryRecommendRoute-触发fallback-startNode[{}],endNodeCode[{}]operateTime[{}],routeProduct[{}]",startNode,endNodeCode,operateTime.getTime(),routeProduct);
        Profiler.businessAlarm("CommandQueryRecommendRoute.fallback", System.currentTimeMillis(), "调用推荐路由接口已熔断");
        return null;
    }

    private static HystrixCommandProperties.Setter getHystrixCommandProperties(HystrixRouteDuccPropertyConfiguration configuration){
       return HystrixCommandProperties.Setter().withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                .withExecutionIsolationThreadInterruptOnTimeout(true)
                .withExecutionTimeoutEnabled(true)
                .withExecutionTimeoutInMilliseconds(configuration.getExecutionTimeoutInMilliseconds())
                .withFallbackEnabled(configuration.isFallbackEnabled())//启用fallback
                .withFallbackIsolationSemaphoreMaxConcurrentRequests(10)//fallback的并发限制 默认10
                .withCircuitBreakerEnabled(configuration.isCircuitBreakerEnabled())//启用断路器
                .withCircuitBreakerForceOpen(configuration.isCircuitBreakerForceOpen())//此属性 iftrue强制断路器进入打开（跳闸）状态，在该状态下它将拒绝所有请求。
                .withCircuitBreakerErrorThresholdPercentage(configuration.getCircuitBreakerErrorThresholdPercentage())//此属性设置错误百分比，等于或高于该百分比时，电路应跳闸开路并启动对回退逻辑的短路请求。 线程池大小 = 服务TP99响应时长（单位秒） * 每秒请求量 + 冗余缓冲值
                .withCircuitBreakerRequestVolumeThreshold(configuration.getCircuitBreakerRequestVolumeThreshold())//此属性设置滚动窗口中将使电路跳闸的最小请求数。默认20/10s
                .withCircuitBreakerSleepWindowInMilliseconds(configuration.getCircuitBreakerSleepWindowInMilliseconds())
               .withRequestLogEnabled(true);//此属性指示是否应将HystrixCommand执行和事件记录到HystrixRequestLog。
    }

    private static HystrixThreadPoolProperties.Setter getHystrixThreadPoolProperties(HystrixRouteDuccPropertyConfiguration configuration){
        return HystrixThreadPoolProperties.Setter().withCoreSize(4).withMaximumSize(configuration.getMaximumSize())
                .withMaxQueueSize(10)//-1时队列使用SynchronousQueue，不支持动态设置
                .withQueueSizeRejectionThreshold(10)// 队列大小拒绝阈值 支持动态设置
                .withKeepAliveTimeMinutes(1)// 队列大小拒绝阈值 支持动态设置
                .withAllowMaximumSizeToDivergeFromCoreSize(true);// 该属性允许配置maximumSize生效,startNode,endNodeCode,operateTime,routeProduct);
    }
}
