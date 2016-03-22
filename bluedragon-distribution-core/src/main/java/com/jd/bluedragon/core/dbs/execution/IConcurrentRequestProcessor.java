package com.jd.bluedragon.core.dbs.execution;

import java.util.List;

public interface IConcurrentRequestProcessor {
	List<Object> process(List<ConcurrentRequest> requests);

	Object executeWith(ConcurrentRequest request);

}
