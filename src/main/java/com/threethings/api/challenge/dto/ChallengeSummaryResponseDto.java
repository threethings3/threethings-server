package com.threethings.api.challenge.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.threethings.api.challenge.domain.CertificationTime;
import com.threethings.api.challenge.domain.ChallengeProfile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ChallengeSummaryResponseDto {
	private Long challengeId;
	private ChallengeProfile challengeProfile;
	private String title;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy:MM:dd", timezone = "Asia/Seoul")
	private LocalDate beginChallengeDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy:MM:dd", timezone = "Asia/Seoul")
	private LocalDate endChallengeDate;
	private CertificationTime certificationTime;
	private Integer participants;
	private Boolean liked;
}
