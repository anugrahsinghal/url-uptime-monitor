package com.xebia.xup.controller;


import com.xebia.xup.exchanges.ExceptionWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

	@ExceptionHandler( {Exception.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ExceptionWrapper> handleBadRequests(Exception ex) {
		log.error("Exception ", ex);
		return new ResponseEntity<>(getFormattedData(ex), HttpStatus.BAD_REQUEST);
	}


	private ExceptionWrapper getFormattedData(Exception e) {
		String message;
		if (e.getCause() != null) {
			message = e.getCause().toString() + " " + e.getMessage();
		} else {
			message = e.getMessage();
		}
		return new ExceptionWrapper(message);

	}
}