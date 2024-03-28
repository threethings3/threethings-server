package com.threethings.api.init;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.threethings.api.challenge.domain.CertificationTime;
import com.threethings.api.challenge.domain.Challenge;
import com.threethings.api.challenge.domain.ChallengeCategory;
import com.threethings.api.challenge.domain.ChallengeProfile;
import com.threethings.api.challenge.domain.Goal;
import com.threethings.api.challenge.repository.ChallengeRepository;
import com.threethings.api.challengemember.domain.ChallengeMember;
import com.threethings.api.challengemember.repository.ChallengeMemberRepository;
import com.threethings.api.member.domain.Member;
import com.threethings.api.member.repository.MemberRepository;

@Component
public class ChallengeTestInitDB {
	@Autowired
	ChallengeRepository challengeRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ChallengeMemberRepository challengeMemberRepository;

	public void initDB() {
		initTestChallenge();
	}

	private void initTestChallenge() {
		Member member1 = memberRepository.findById(1L).orElseThrow();
		Member member2 = memberRepository.findById(2L).orElseThrow();
		Challenge challengeTestData1 = Challenge.builder()
			.challengeProfile(new ChallengeProfile(ChallengeCategory.GROWTH, 1L))
			.title("삼시 세끼 밥 먹기")
			.goal(new Goal("하루 세 끼 다 먹기", "하루 두 끼 다 먹기", "하루 한 끼 다 먹기"))
			.certificationTime(new CertificationTime(LocalTime.of(8, 0), LocalTime.of(22, 0)))
			.cycleDays(List.of(1, 2, 3, 4, 5, 6, 7))
			.challengePeriodWeeks(4)
			.isPublic(Boolean.TRUE)
			.maxParticipants(30).build();
		ChallengeMember challengeMember1 = new ChallengeMember(challengeTestData1, member1);
		ChallengeMember challengeMember2 = new ChallengeMember(challengeTestData1, member2);

		Challenge challengeTestData2 = Challenge.builder()
			.challengeProfile(new ChallengeProfile(ChallengeCategory.GROWTH, 1L))
			.title("공부하기")
			.goal(new Goal("3시간 공부하기", "2시간 공부하기", "1시간 공부하기"))
			.certificationTime(new CertificationTime(LocalTime.of(8, 0), LocalTime.of(22, 0)))
			.cycleDays(List.of(1, 3, 5, 6, 7))
			.challengePeriodWeeks(3)
			.isPublic(Boolean.TRUE)
			.maxParticipants(20).build();
		ChallengeMember challengeMember3 = new ChallengeMember(challengeTestData2, member1);

		challengeRepository.saveAll(List.of(challengeTestData1, challengeTestData2));
		challengeMemberRepository.saveAll(List.of(challengeMember1, challengeMember2, challengeMember3));
	}
}
