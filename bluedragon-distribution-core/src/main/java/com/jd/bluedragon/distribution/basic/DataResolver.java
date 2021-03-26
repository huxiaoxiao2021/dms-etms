package com.jd.bluedragon.distribution.basic;

import java.io.InputStream;
import java.util.List;

public interface DataResolver {

	public <T> List<T> resolver(InputStream in, Class<T> cls, MetaDataFactory metaDataFactory) throws Exception;

    /**
     *
     * @param in
     * @param cls
     * @param metaDataFactory
     * @param validateIsContinue 校验失败 是跳过 还是返回异常
     * @param resultMessages 返回此行是否转换成功
     * @param <T>
     * @return
     * @throws Exception
     */
	public <T> List<T> resolver(InputStream in, Class<T> cls, MetaDataFactory metaDataFactory,boolean validateIsContinue,List<String> resultMessages) throws Exception;
}
