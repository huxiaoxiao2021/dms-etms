package com.jd.bluedragon.distribution.framework;

import java.util.Comparator;

import com.jd.tbschedule.redis.template.TaskEntry;

public interface TaskComparator<T> {

	public Comparator<TaskEntry<T>> getComparator();
}
