package com.threethings.api.challenge.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.threethings.api.challenge.domain.Challenge;
import com.threethings.api.challenge.dto.ChallengeSummaryResponseDto;
import com.threethings.api.challenge.factory.ChallengeFactory;
import com.threethings.api.challengemember.domain.ChallengeMember;
import com.threethings.api.challengemember.repository.ChallengeMemberRepository;
import com.threethings.api.config.JpaAuditingConfig;
import com.threethings.api.member.domain.Member;
import com.threethings.api.member.factory.domain.MemberFactory;
import com.threethings.api.member.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Import(JpaAuditingConfig.class)
public class ChallengeRepositoryTest {
	private static final String START_DATE = "beginChallengeDate";
	private static final String MAX_PARTICIPANTS = "maxParticipants";
	@Autowired
	ChallengeRepository challengeRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ChallengeMemberRepository challengeMemberRepository;

	@PersistenceContext
	EntityManager em;

	private void clear() {
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("챌린지 검색 정렬 테스트")
	void searchChallengeTest() throws NoSuchFieldException, IllegalAccessException {
		// given
		Member member = MemberFactory.createMember();
		Member member2 = MemberFactory.anotherMember();
		memberRepository.save(member);
		memberRepository.save(member2);

		// 진행전 챌린지(그룹)
		Challenge c2 = ChallengeFactory.createChallenge();
		c2.addFavoriteMember(member);
		changePrivateFieldWithReflection(c2, START_DATE, LocalDate.now().plusDays(3));
		ChallengeMember cm2 = new ChallengeMember(c2, member);
		ChallengeMember cm3 = new ChallengeMember(c2, member2);

		// 진행중 챌린지(그룹)
		Challenge c3 = ChallengeFactory.createChallenge();
		changePrivateFieldWithReflection(c3, START_DATE, LocalDate.now().minusDays(3));
		ChallengeMember cm4 = new ChallengeMember(c3, member);
		ChallengeMember cm5 = new ChallengeMember(c3, member2);

		// 진행전 챌린지(혼자)
		Challenge c4 = ChallengeFactory.createChallenge();
		changePrivateFieldWithReflection(c4, START_DATE, LocalDate.now().plusDays(3));
		changePrivateFieldWithReflection(c4, MAX_PARTICIPANTS, 1);
		ChallengeMember cm6 = new ChallengeMember(c4, member);

		// 진행중 챌린지(혼자)
		Challenge c5 = ChallengeFactory.createChallenge();
		changePrivateFieldWithReflection(c5, START_DATE, LocalDate.now().minusDays(1));
		changePrivateFieldWithReflection(c5, MAX_PARTICIPANTS, 1);
		ChallengeMember cm7 = new ChallengeMember(c5, member);
		List<Challenge> challengeList = List.of(c2, c3, c4, c5);
		challengeRepository.saveAll(challengeList);
		challengeMemberRepository.saveAll(List.of(cm2, cm2, cm3, cm4, cm5, cm6, cm7));

		clear();
		// when
		PageRequest pageable = PageRequest.of(0, 10);
		Page<ChallengeSummaryResponseDto> res =
			challengeRepository.findByKeywordAndEndDateAfterAndIsPublicTrueWithMembers("밥", 1L,
				LocalDate.now(), pageable);

		// then
		for (int i = 0; i < challengeList.size(); i++) {
			assertEquals(challengeList.get(i).getId(), res.getContent().get(i).getChallengeId());
		}
		System.out.println("123");
	}

	private void changePrivateFieldWithReflection(Challenge challenge, String fieldName, Object newValue) throws
		NoSuchFieldException, IllegalAccessException {
		Field field = challenge.getClass().getDeclaredField(fieldName);
		field.setAccessible(true); // private 필드에 접근하기 위해 설정

		if (field.getType() == String.class) {
			field.set(challenge, (String)newValue);
		} else if (field.getType() == LocalDate.class) {
			field.set(challenge, (LocalDate)newValue);
		} else if (field.getType() == Integer.class) {
			field.set(challenge, (Integer)newValue);
		}
	}
}
