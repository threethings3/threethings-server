package com.threethings.api.challenge.exception;

import org.springframework.http.HttpStatus;

import com.threethings.api.global.exception.DomainExceptionTypeInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public enum ChallengeExceptionType implements DomainExceptionTypeInterface {
	CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "챌린지가 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public void printErrorLog() {
		log.warn("Challenge Domain Error ========> HttpStatus: {}, Message: {}", this.httpStatus, this.message);
	}
}
