package com.jd.common.limiter;

import com.jd.jim.cli.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName LuaScriptConfig
 * @Description
 * @Author wyh
 * @Date 2023/5/18 16:59
 **/
@Slf4j
@Configuration
@DependsOn(value = {"jimClient"})
public class LuaScriptConfig {

    @Autowired
    @Qualifier("jimClient")
    private Cluster redisCache;

    private static final String LUA_SCRIPT_PATH = "redis/rateLimit.lua";

    @Bean(name = "limitRedisScript")
    public RedisScript<Long> limitRedisScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(LUA_SCRIPT_PATH)));
        redisScript.setResultType(Long.class);

        String scriptCode = LuaScriptConstants.RATE_LIMIT_LUA.getCode();
        try {
            String luaScript = redisScript.getScriptAsString();
            String sha = redisCache.scriptLoad(luaScript);
            redisCache.set(scriptCode, sha);
            redisCache.expire(scriptCode, 1, TimeUnit.DAYS);
            log.info("上传限流Lua脚本{}，并初始化到Redis缓存. sha:{}", scriptCode, sha);
        }
        catch (Exception ex) {
            log.error("上传限流Lua脚本{}失败！", scriptCode, ex);
        }

        return redisScript;
    }
}
