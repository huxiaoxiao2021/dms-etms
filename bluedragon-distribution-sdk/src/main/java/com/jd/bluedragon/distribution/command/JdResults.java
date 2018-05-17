package com.jd.bluedragon.distribution.command;

import java.io.Serializable;

/**
 * 
 * @ClassName: JdResult
 * @Description: 处理结果类
 * @author: wuyoude
 * @date: 2018年1月25日 下午11:00:07
 *
 */
public class JdResults{
	public static final JdResult REST_SUC_DATA_NULL = new JdResult(JdResult.CODE_SUC,201,"201-调用服务成功，数据为空！");
	public static final JdResult REST_FAIL_PARAM_ERROR = new JdResult(JdResult.CODE_FAIL,401,"401-调用服务失败，参数错误！");
	public static final JdResult REST_FAIL_SERVER_NOT_FIND = new JdResult(JdResult.CODE_FAIL,404,"404-请求的服务不存在！");
	public static final JdResult REST_ERROR_SERVER_EXCEPTION = new JdResult(JdResult.CODE_ERROR,501,"501-请求的服务异常！");
}
