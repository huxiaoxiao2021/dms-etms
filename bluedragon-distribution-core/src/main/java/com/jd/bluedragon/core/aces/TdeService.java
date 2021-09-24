package com.jd.bluedragon.core.aces;

import com.jd.security.tde.InvalidKeyPermission;
import com.jd.security.tde.InvalidTokenException;
import com.jd.security.tde.MalformedException;
import com.jd.security.tdeclient.CorruptKeyException;
import com.jd.security.tdeclient.NoValidKeyException;
import com.jd.security.tdeclient.ServiceErrorException;
import com.jd.security.tdeclient.TDEClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.utils
 * @ClassName: TdeHelper
 * @Description: 一个简单的基于京东ACES中间件实现的加密解密服务
 * <link>https://cf.jd.com/pages/viewpage.action?pageId=104955500#id-%E6%95%B0%E6%8D%AE%E6%9D%83%E9%99%90%E7%AE%A1%E6%8E%A7%E4%B8%8E%E5%8A%A0%E8%A7%A3%E5%AF%86%E6%9C%8D%E5%8A%A1%E7%B3%BB%E7%BB%9F%EF%BC%88ACES%EF%BC%89%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E-%E9%9B%86%E6%88%90ACES-SDK</link>
 * @Author： wuzuxiang
 * @CreateDate 2021/9/22 19:16
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Slf4j
@Service
public class TdeService {

    @Qualifier("tdeClient")
    @Autowired
    private TDEClient tdeClient;

    /**
     * 加密.
     * @param str 需要加密的字符串
     * @return 加密后的字符串
     */
    public String encrypt(final String str) {
        String res = null;
        try {
            if (str != null) {
                res = this.tdeClient.encryptString(str);
            }
        } catch (final GeneralSecurityException | NoValidKeyException | InvalidKeyPermission | InvalidTokenException | MalformedException | ServiceErrorException | UnsupportedEncodingException | CorruptKeyException exp) {
            log.error("encrypt error", exp);
        }
        return res;
    }

    /**
     * 解密.
     * @param str 需要解密的字符串
     * @return 解密后的字符串
     */
    public String decrypt(final String str) {
        String res = null;
        try {
            if (TDEClient.CipherStatus.Decryptable.equals(this.tdeClient.isDecryptable(str))) {
                res = this.tdeClient.decryptString(str);
            } else {
                // 自定义处理错，根据自己的逻辑处理错误
                log.error("invalid encrypt value {}", str);
            }
        } catch (final GeneralSecurityException | NoValidKeyException | InvalidKeyPermission | InvalidTokenException | ServiceErrorException | CorruptKeyException | IOException | MalformedException exp) {
            log.error("decrypt error", exp);
        }
        return res;
    }
}
