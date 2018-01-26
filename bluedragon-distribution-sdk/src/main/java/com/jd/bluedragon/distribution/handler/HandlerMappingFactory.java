package com.jd.bluedragon.distribution.handler;

import com.jd.bluedragon.distribution.command.JdCommand;

public interface HandlerMappingFactory<T extends HandlerMapping> {
	
	T getHandlerMapping(JdCommand jdCommand);
}
