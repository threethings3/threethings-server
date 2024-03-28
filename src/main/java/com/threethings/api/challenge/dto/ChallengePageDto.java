package com.threethings.api.challenge.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Getter
public class ChallengePageDto {
	private Integer pageNumber;
	private Integer pageSize;
	private Boolean isFirst;
	private Boolean isLast;
	private Integer totalPages;
	private Long totalElements;
	private List<ChallengeSummaryResponseDto> contents;

	public static ChallengePageDto toDto(Page<ChallengeSummaryResponseDto> contents) {
		return ChallengePageDto.builder()
			.pageNumber(contents.getNumber())
			.pageSize(contents.getSize())
			.isFirst(contents.isFirst())
			.isLast(contents.isLast())
			.totalPages(contents.getTotalPages())
			.totalElements(contents.getTotalElements())
			.contents(contents.getContent()).build();
	}
}
