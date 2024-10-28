package com.demo.msgvalidationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Stuti
 * 
 * To Handle message validation exceptions
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MsgFormatException extends Exception {
	private static final long serialVersionUID = 1L;

	public MsgFormatException(String message){
    	super(message);
    }
	public MsgFormatException(String message, Throwable exception) {
    	super(message, exception);
    }
}
