package com.jd.bluedragon.distribution.jy.service.task;

import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/7/26
 * @Description: 清理作业APP 发车 任务相关实现
 */
@Service("jyBizSendTaskCleanService")
public class JYBizSendTaskCleanServiceImpl implements JYBizTaskCleanService{

    /**
     * 清理数据
     *
     * @return
     */
    @Override
    public boolean clean() {
        return true;
    }
}
