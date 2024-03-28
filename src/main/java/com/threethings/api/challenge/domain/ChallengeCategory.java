package com.threethings.api.challenge.domain;

import com.threethings.api.global.common.DocsEnumType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChallengeCategory implements DocsEnumType {
	EXERCISE(1, "운동"),
	DIET(2, "식습관"),
	MINDFULNESS(3, "마음챙김"),
	HOBBY(4, "취미"),
	GROWTH(5, "성장");

	private final int code;
	private final String name;

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getDescription() {
		return name;
	}
}
