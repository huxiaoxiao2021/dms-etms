package com.jd.bluedragon.core.base;


import com.jd.bluedragon.core.jsf.adapter.AdapterOutOfPlatformDecryRouter;
import com.jd.bluedragon.core.jsf.adapter.AdapterRequestOfPlatformDecryRouter;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/11/8
 * @Description:
 *
 *   商家服务
 */
public interface AdapterApiManager {

    /**
     * 字节和得物相关运单的收件人信息 解密服务
     *
     * 给其他系统提供服务包装接口尽量不要用string，如果用了那最好把实体放依赖包里，与人方便自己方便。
     *
     *
     * https://joyspace.jd.com/pages/YFpNlkVE7RzzK36YGS7z
     *
     * @param request
     * @return
     */
    AdapterOutOfPlatformDecryRouter commonAdapterExecuteOfPlatformDecryRouter(AdapterRequestOfPlatformDecryRouter request);
}
