package com.jd.common.limiter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.common.web.LoginContext;
import com.jd.jim.cli.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName RateLimiterHandlerInterceptor
 * @Description
 * @Author wyh
 * @Date 2023/5/17 19:50
 **/
@Slf4j
public class RateLimiterHandlerInterceptor implements HandlerInterceptor {

    private static final String COLON = ":";

    /**
     * 限流前缀
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "jdl:rate:limit:";

    /**
     * 访问量统计
     */
    private static final String ACCESS_ANALYZE_KEY_PREFIX = "jdl:access:analyze:";

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    @Qualifier("limitRedisScript")
    private RedisScript<Long> redisScript;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisCache;

    private List<String> resolveWhiteUrlList() {
        if (StringUtils.isBlank(dmsConfigManager.getPropertyConfig().getRestRateLimiterWhiteList())) {
            return Lists.newArrayList();
        }
        List<String> whiteUrlList = Lists.newArrayList();
        try {
            whiteUrlList = JSONArray.parseArray(dmsConfigManager.getPropertyConfig().getRestRateLimiterWhiteList(), String.class);
        }
        catch (Exception e) {
            log.error("解析限流白名单URL配置异常！{}", dmsConfigManager.getPropertyConfig().getRestRateLimiterWhiteList(), e);
        }

        return whiteUrlList;
    }

    private List<String> resolveBlackIPList() {
        if (StringUtils.isBlank(dmsConfigManager.getPropertyConfig().getRestRateLimiterBlackIpList())) {
            return Lists.newArrayList();
        }
        List<String> blackIPList = Lists.newArrayList();
        try {
            blackIPList = JSONArray.parseArray(dmsConfigManager.getPropertyConfig().getRestRateLimiterBlackIpList(), String.class);
        }
        catch (Exception e) {
            log.error("解析限流IP黑名单配置异常！{}", dmsConfigManager.getPropertyConfig().getRestRateLimiterBlackIpList(), e);
        }

        return blackIPList;
    }

    private List<String> resolveBlackErpList() {
        if (StringUtils.isBlank(dmsConfigManager.getPropertyConfig().getRestRateLimiterBlackErpList())) {
            return Lists.newArrayList();
        }
        List<String> blackErpList = Lists.newArrayList();
        try {
            blackErpList = JSONArray.parseArray(dmsConfigManager.getPropertyConfig().getRestRateLimiterBlackErpList(), String.class);
        }
        catch (Exception e) {
            log.error("解析限流ERP黑名单配置异常！{}", dmsConfigManager.getPropertyConfig().getRestRateLimiterBlackErpList(), e);
        }

        return blackErpList;
    }

    private RateLimiterRule resolveRateLimiterConfig() {
        RateLimiterRule rateLimiterRule = new RateLimiterRule();
        if (StringUtils.isBlank(dmsConfigManager.getPropertyConfig().getRestRateLimiterBody())) {
            return rateLimiterRule;
        }
        try {
            rateLimiterRule = JSON.parseObject(dmsConfigManager.getPropertyConfig().getRestRateLimiterBody(), RateLimiterRule.class);
        }
        catch (Exception e) {
            log.error("解析限流URL配置异常！{}", dmsConfigManager.getPropertyConfig().getRestRateLimiterBody(), e);
        }

        return rateLimiterRule;
    }

    /**
     * 获取降级提示语
     * @param requestUri
     * @return
     */
    private String getFallbackTips(String requestUri) {
        AtomicReference<String> message = new AtomicReference<>(dmsConfigManager.getPropertyConfig().getRestRateLimiterFallbackGlobalTip());
        if (StringUtils.isNotBlank(dmsConfigManager.getPropertyConfig().getRestRateLimiterFallbackTips())) {
            List<RateLimiterTips> rateLimiterTips = JSONArray.parseArray(dmsConfigManager.getPropertyConfig().getRestRateLimiterFallbackTips(), RateLimiterTips.class);
            rateLimiterTips.stream()
                    .filter(item -> item.getUri().equals(requestUri))
                    .findFirst()
                    .ifPresent(rateLimiterTips1 -> {
                        if (StringUtils.isNotBlank(rateLimiterTips1.getTip())) {
                            message.set(rateLimiterTips1.getTip());
                        }
                    });
        }

        return message.get();
    }

    /**
     * 生成限流特征key
     * @param rateLimiter
     * @return
     */
    private String genLimitTokenKey(RateLimiter rateLimiter) {
        StringBuilder keyBuffer = new StringBuilder(RATE_LIMIT_KEY_PREFIX)
                .append(dmsConfigManager.getPropertyConfig().getRestRateLimiterTenant())
                .append(COLON)
                .append(rateLimiter.getIdentifier());
        LimitType limitType = LimitType.getFromCode(rateLimiter.getLimitType());
        switch (limitType) {
            case IP:
                // 客户端IP地址
                String clientIp = NetworkUtil.getRealIPAddr();
                keyBuffer.append(COLON)
                        .append(clientIp);
                break;
            case USER:
                // 用户ERP
                String erp = LoginContext.getLoginContext().getPin();
                keyBuffer.append(COLON)
                        .append(erp);
                break;
        }
        keyBuffer.append(COLON)
                .append(genTimeTypeKey(rateLimiter));

        logInfo("rate limit uniq key:{}", keyBuffer.toString());

        return keyBuffer.toString();
    }

    /**
     * 根据限流时长类型，设置key
     * @param rateLimiter
     * @return
     */
    private String genTimeTypeKey(RateLimiter rateLimiter) {
        Date date = new Date();
        LimitTimeUnit limitTimeUnit = LimitTimeUnit.getFromCode(rateLimiter.getTimeUnit());
        switch (limitTimeUnit) {
            case DAY:
                return DateHelper.getDate2String(date);
            case HOUR:
                return DateHelper.getDate2String(date)
                        + COLON
                        + DateHelper.getHour(date);
            case MINUTE:
                return DateHelper.getDate2String(date)
                        + COLON
                        + DateHelper.getHour(date)
                        + COLON
                        + DateHelper.getMin(date);
            case SECOND:
                return DateHelper.getDate2String(date) +
                        COLON +
                        DateHelper.getHour(date) +
                        COLON +
                        DateHelper.getMin(date) +
                        COLON +
                        DateHelper.getSecond(date);
            default:
                throw new RuntimeException("未知的限流时长类型！");
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean limited = false, unLimited = true;
        if (!dmsConfigManager.getPropertyConfig().getRestRateLimiterSwitch()) {
            logDebug("未开启限流功能!");
            return unLimited;
        }

        // 请求地址
        String requestURI = request.getRequestURI();
        List<String> whiteUrlList = resolveWhiteUrlList();
        if (whiteUrlList.contains(requestURI)) {
            logDebug("白名单URL不限流! {}", requestURI);
            return unLimited;
        }

        // 记录限流排名数据


        List<String> blackIPList = resolveBlackIPList();
        String realAddrIP = NetworkUtil.getRealIPAddr();
        if (blackIPList.contains(realAddrIP)) {
            logInfo("黑名单IP被限流! {}", realAddrIP);

            StringBuilder tipBuffer = new StringBuilder();
            tipBuffer.append("[警告]您的IP:").append(realAddrIP).append("访问太频繁，已加入黑名单限制访问！有问题请联系分拣小秘解除拦截！");
            outputResponseToFrontEnd(request, response, tipBuffer.toString());
            return limited;
        }
        List<String> blackErpList = resolveBlackErpList();
        String erp = LoginContext.getLoginContext().getPin();
        if (blackErpList.contains(erp)) {
            logInfo("黑名单ERP被限流! {}", erp);

            StringBuilder tipBuffer = new StringBuilder();
            tipBuffer.append("[警告]您的ERP:").append(erp).append("访问太频繁，已加入黑名单限制访问！有问题请联系分拣小秘解除拦截！");
            outputResponseToFrontEnd(request, response, tipBuffer.toString());
            return limited;
        }

        RateLimiterRule rateLimiterRule = resolveRateLimiterConfig();
        if (CollectionUtils.isEmpty(rateLimiterRule.getUrlLimitRule())) {
            logDebug("未配置限流URL名单，不限流!");
            return unLimited;
        }

        // 获取限流规则
        RateLimiter rateLimiter = getRateLimiter(requestURI, rateLimiterRule);

        // 限流窗口唯一标识
        String tokenKey = genLimitTokenKey(rateLimiter);

        if (shouldLimit(rateLimiter, tokenKey)) {
            logInfo("[{}]请求被限流", tokenKey);

            StringBuilder tipBuffer = assembleTipsMessage(rateLimiter);

            outputResponseToFrontEnd(request, response, tipBuffer.toString());
            return limited;
        }

        return unLimited;
    }

    private void outputResponseToFrontEnd(HttpServletRequest request, HttpServletResponse response, String tips) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        if (StringUtils.equals("XMLHttpRequest", request.getHeader("X-Requested-With"))) {
            // ajax请求，返回提示语
            response.getWriter().println("fallback" + tips);
        }
        else {
            response.getWriter().print("<html><head><meta charset='utf-8'/><script type='text/javascript'>alert(\"" +
                    tips +
                    "\");" +
                    "history.back();</script></head></html>");
            response.getWriter().close();
            response.flushBuffer();
        }
    }

    /**
     * 取限流配置规则，未配置取兜底配置
     * @param requestURI
     * @param rateLimiterRule
     * @return
     */
    private RateLimiter getRateLimiter(String requestURI, RateLimiterRule rateLimiterRule) {
        List<RateLimiter> rateLimiterList = rateLimiterRule.getUrlLimitRule();

        Optional<RateLimiter> optional = rateLimiterList.stream()
                .filter(config -> config.getIdentifier().equals(requestURI)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        // 取兜底限流规则
        RateLimiter defaultLimitRule = rateLimiterRule.getDefaultLimitRule();
        RateLimiter rateLimiter = new RateLimiter();
        rateLimiter.setIdentifier(requestURI);
        rateLimiter.setLimitType(LimitType.DEFAULT.getCode()); // 默认全局限流
        rateLimiter.setMax(defaultLimitRule.getMax());
        rateLimiter.setTimeout(defaultLimitRule.getTimeout());
        rateLimiter.setTimeUnit(defaultLimitRule.getTimeUnit());
        return rateLimiter;
    }

    /**
     * 设置限流提示语
     * @param rateLimiter
     * @return
     */
    private StringBuilder assembleTipsMessage(RateLimiter rateLimiter) {
        StringBuilder tipBuffer = new StringBuilder();
        if (rateLimiter.getMax() < 0) {
            // 限流值小于0，该功能降级，限制全局访问
            String fallbackTips = getFallbackTips(rateLimiter.getIdentifier());
            tipBuffer.append(fallbackTips);
        }
        else {
            LimitType limitType = LimitType.getFromCode(rateLimiter.getLimitType());
            if (LimitType.IP.equals(limitType)) {
                tipBuffer.append("您的IP[").append(NetworkUtil.getRealIPAddr()).append("]");
            }
            else if (LimitType.USER.equals(limitType)) {
                tipBuffer.append("您的用户[").append(LoginContext.getLoginContext().getPin()).append("]");
            }
            else {
                if (StringUtils.isNotBlank(rateLimiter.getDesc())) {
                    tipBuffer.append("功能[").append(rateLimiter.getDesc()).append("]");
                }
            }
            LimitTimeUnit limitTimeUnit = LimitTimeUnit.getFromCode(rateLimiter.getTimeUnit());
            switch (limitTimeUnit) {
                case SECOND:
                    tipBuffer.append("在一秒内");
                    break;
                case MINUTE:
                    tipBuffer.append("在一分钟内");
                    break;
                case HOUR:
                    tipBuffer.append("在一小时内");
                    break;
                case DAY:
                    tipBuffer.append("在一天内");
                    break;
            }
            tipBuffer.append("请求次数超过[").append(rateLimiter.getMax()).append("]次，访问太频繁，已被限流！");
            tipBuffer.append("请下一");
            switch (limitTimeUnit) {
                case SECOND:
                    tipBuffer.append("秒");
                    break;
                case MINUTE:
                    tipBuffer.append("分钟");
                    break;
                case HOUR:
                    tipBuffer.append("小时");
                    break;
                case DAY:
                    tipBuffer.append("天");
                    break;
            }
            tipBuffer.append("再重试！");
        }

        return tipBuffer;
    }

    /**
     * 当前访问是否被限流
     * @param rateLimiter
     * @param tokenKey
     * @return true：被限流
     */
    private boolean shouldLimit(RateLimiter rateLimiter, String tokenKey) {
        // 直接被限制访问
        if (rateLimiter.getMax() < 0) {
            return true;
        }
        // 校验lua脚本是否存在
        String sha = redisCache.get(LuaScriptConstants.RATE_LIMIT_LUA.getCode());
        if (StringUtils.isBlank(sha)) {
            log.info("lua限流脚本{}已过期，重新加载.", LuaScriptConstants.RATE_LIMIT_LUA.getCode());
            sha = redisCache.scriptLoad(redisScript.getScriptAsString());
            redisCache.set(LuaScriptConstants.RATE_LIMIT_LUA.getCode(), sha);
            redisCache.expire(LuaScriptConstants.RATE_LIMIT_LUA.getCode(), 1, TimeUnit.DAYS);
        }

        long ttl = 0L;
        LimitTimeUnit limitTimeUnit = LimitTimeUnit.getFromCode(rateLimiter.getTimeUnit());
        switch (limitTimeUnit) {
            case SECOND:
                ttl = TimeUnit.SECONDS.toMillis(rateLimiter.getTimeout());
                break;
            case MINUTE:
                ttl = TimeUnit.MINUTES.toMillis(rateLimiter.getTimeout());
                break;
            case HOUR:
                ttl = TimeUnit.HOURS.toMillis(rateLimiter.getTimeout());
                break;
            case DAY:
                ttl = TimeUnit.DAYS.toMillis(rateLimiter.getTimeout());
                break;
        }
        long now = Instant.now().toEpochMilli();
        long expired = now - ttl;

        List<String> keys = new ArrayList<>();
        List<String> args = new ArrayList<>();
        keys.add(tokenKey);
        args.add(Long.toString(now));
        args.add(Long.toString(ttl));
        args.add(Long.toString(expired));
        args.add(rateLimiter.getMax().toString());
        Object evalValue = redisCache.evalsha(sha, keys, args, false);
        if (Objects.nonNull(evalValue)) {
            int redisVal = Integer.parseInt(evalValue.toString());
            if (redisVal == 0) {
                logInfo("request is limited, key:{}, ttl:{}, max:{}", tokenKey, ttl, rateLimiter.getMax());
                return true;
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    private static void logInfo(String logMsg, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(logMsg, objects);
        }
    }

    private static void logDebug(String logMsg, Object... objects) {
        if (log.isDebugEnabled()) {
            log.debug(logMsg, objects);
        }
    }
}
