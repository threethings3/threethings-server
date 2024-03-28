package com.threethings.api.challenge.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.threethings.api.challenge.domain.Challenge;
import com.threethings.api.challenge.dto.ChallengeSummaryResponseDto;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
	@Query(value = """
			SELECT new com.threethings.api.challenge.dto.ChallengeSummaryResponseDto(
				c.id, c.challengeProfile, c.title, c.beginChallengeDate, c.endChallengeDate, c.certificationTime,
				SIZE(c.members),
			CASE WHEN EXISTS (SELECT 1 FROM c.favoriteMembers fm WHERE fm.id = :memberId) THEN true ELSE false END)
			FROM  Challenge c
			WHERE c.title LIKE '%' || :keyword || '%'
				AND c.endChallengeDate > :currentDate
				AND c.isPublic = true
			ORDER BY SIZE(c.members) desc, c.beginChallengeDate desc
		""")
	Page<ChallengeSummaryResponseDto> findByKeywordAndEndDateAfterAndIsPublicTrueWithMembers(
		@Param("keyword") String keyword,
		@Param("memberId") Long memberId,
		@Param("currentDate") LocalDate currentDate,
		Pageable pageable);
}
