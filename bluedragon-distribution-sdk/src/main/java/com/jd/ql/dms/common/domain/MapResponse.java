package com.jd.ql.dms.common.domain;

import java.util.Map;
/**
 * 
 * @ClassName: MapResponse
 * @Description: map类型的返回结果
 * @author wuyoude
 * @date 2017年6月1日 下午6:03:18
 * 
 * @param <K>
 * @param <V>
 */
public class MapResponse<K,V> extends JdResponse<Map<K,V>>{
    
    private static final long serialVersionUID = 1L;
    
    public MapResponse() {
		super();
	}

	public MapResponse(Integer code, String message) {
		super(code, message);
	}
	
	public MapResponse(Integer code, String message, Map<K,V> data) {
		super(code, message, data);
	}
}
