package com.threethings.api.challenge.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ChallengeLikeRequestDto {
	private Long challengeId;
	private Boolean liked;
}
