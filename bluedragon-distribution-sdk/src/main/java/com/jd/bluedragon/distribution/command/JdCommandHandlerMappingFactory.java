package com.jd.bluedragon.distribution.command;

import java.util.Map;

import com.jd.bluedragon.distribution.handler.HandlerMapping;
import com.jd.bluedragon.distribution.handler.HandlerMappingFactory;

public class JdCommandHandlerMappingFactory implements HandlerMappingFactory{
	
	private Map<Integer,HandlerMapping> handlerMappings;
	
	@Override
	public HandlerMapping getHandlerMapping(JdCommand jdCommand) {
		return handlerMappings.get(jdCommand.getBusinessType());
	}
	/**
	 * @return the handlerMappings
	 */
	public Map<Integer, HandlerMapping> getHandlerMappings() {
		return handlerMappings;
	}

	/**
	 * @param handlerMappings the handlerMappings to set
	 */
	public void setHandlerMappings(Map<Integer, HandlerMapping> handlerMappings) {
		this.handlerMappings = handlerMappings;
	}

}
