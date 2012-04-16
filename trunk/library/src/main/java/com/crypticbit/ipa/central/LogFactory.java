package com.crypticbit.ipa.central;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public abstract class LogFactory {

	private static java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger("ipa");


	static {
		for(Handler h : logger.getParent().getHandlers()){
		    if(h instanceof ConsoleHandler){
		        h.setLevel(Level.WARNING);
		    }
		} 
	}
	
	public static java.util.logging.Logger getLogger() {
				return logger;
	}

}
