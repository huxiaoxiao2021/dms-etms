
package com.jd.bluedragon.core.objectid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.dbs.service.ObjectIdService;

@Service("genObjectId")
public class DbsGenerateObjectId implements IGenerateObjectId {
	private static Map<String, AtomicInteger> nextIncMap = Collections
			.synchronizedMap(new HashMap<String, AtomicInteger>());
	private static Map<String, Long> firstMap = Collections
			.synchronizedMap(new HashMap<String, Long>());
	private int maxValue = 999;

    private ObjectIdService objectIdService;

    public ObjectIdService getObjectIdService() {
        return objectIdService;
    }

    public void setObjectIdService(ObjectIdService objectIdService) {
        this.objectIdService = objectIdService;
    }

    @Override
	public synchronized long getObjectId(String tableName) {
		if (!firstMap.containsKey(tableName)) {
			firstMap.put(tableName, objectIdService.getNextFirstId(tableName));
			nextIncMap.put(tableName, new AtomicInteger(0));
		}
		int lastId = nextIncMap.get(tableName).addAndGet(1);
		if (lastId > maxValue) {
			firstMap.put(tableName, objectIdService.getNextFirstId(tableName));
			nextIncMap.put(tableName, new AtomicInteger(1));
			lastId = 1;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(firstMap.get(tableName));

		int idLen = (lastId + "").length();
		int zeroLen = (maxValue + "").length() - idLen;
		for (int j = 0; j < zeroLen; j++) {
			sb.append("0");
		}
		sb.append(lastId);
		long objectId = Long.parseLong(sb.toString());
		return objectId;
	}
}
