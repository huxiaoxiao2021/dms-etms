package com.jd.bluedragon.distribution.command;

public interface JdCommandService {
	/**
	 * 执行json格式的命令
	 * @param jsonCommand
	 * @return
	 */
	String execute(String jsonCommand);
}
