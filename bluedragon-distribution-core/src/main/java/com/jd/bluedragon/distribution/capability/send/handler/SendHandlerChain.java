package com.jd.bluedragon.distribution.capability.send.handler;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/24
 * @Description:  发货处理责任链
 *
 */
public class SendHandlerChain {


    private static final Logger logger = LoggerFactory.getLogger(SendHandlerChain.class);

    private List<ISendHandler> chain = new ArrayList<>();

    /**
     * 添加链
     * @param handler
     */
    public void addHandler(ISendHandler handler) {
        this.chain.add(handler);
    }

    /**
     * 处理链
     * @param context
     */
    public void handle(SendOfCAContext context) {
        //需要考虑存在不允许跳过的链
        boolean handled = Boolean.TRUE;
        for (ISendHandler handler : chain) {
            if(handled || handler.mustHandler()){
                infoLog( handler.getClass() + ".doHandler begin barCode:{} dimension:{}",context.getBarCode(),context.getSendDimension());
                handled = handler.doHandler(context);
                infoLog( handler.getClass() + ".doHandler end barCode:{} dimension:{} handled:{}",context.getBarCode(),context.getSendDimension(),handled);
            }
        }
    }

    private void infoLog(String format,Object... args){
        if(logger.isInfoEnabled()){
            logger.info(format,args);
        }
    }

}
