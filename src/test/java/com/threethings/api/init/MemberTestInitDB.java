package com.threethings.api.init;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.threethings.api.challenge.domain.ChallengeCategory;
import com.threethings.api.member.domain.Member;
import com.threethings.api.member.domain.Provider;
import com.threethings.api.member.repository.MemberRepository;

@Component
public class MemberTestInitDB {
	@Autowired
	MemberRepository memberRepository;

	public void initDB() {
		initTestMember();
	}

	private void initTestMember() {
		Member member1 = Member.builder()
			.nickname("testMember1")
			.provider(Provider.NAVER)
			.socialCode("12345678")
			.profileImageId(1L)
			.favoriteChallengeCategories(Set.of(ChallengeCategory.EXERCISE, ChallengeCategory.GROWTH))
			.build();

		Member member2 = Member.builder()
			.nickname("testMember2")
			.provider(Provider.KAKAO)
			.socialCode("-12345678")
			.profileImageId(2L)
			.favoriteChallengeCategories(Set.of(ChallengeCategory.MINDFULNESS, ChallengeCategory.GROWTH))
			.build();

		memberRepository.saveAll(List.of(member1, member2));
	}
}
