package com.threethings.api.member.factory.domain;

import java.util.Set;

import com.threethings.api.challenge.domain.ChallengeCategory;
import com.threethings.api.member.domain.Member;
import com.threethings.api.member.domain.Provider;

public class MemberFactory {
	public static Member createMember() {
		return Member.builder()
			.nickname("member")
			.provider(Provider.NAVER)
			.socialCode("12345678")
			.profileImageId(1L)
			.favoriteChallengeCategories(Set.of(ChallengeCategory.EXERCISE, ChallengeCategory.GROWTH)).build();
	}

	public static Member anotherMember() {
		return Member.builder()
			.nickname("member1")
			.provider(Provider.NAVER)
			.socialCode("-12345678")
			.profileImageId(2L)
			.favoriteChallengeCategories(Set.of(ChallengeCategory.EXERCISE, ChallengeCategory.GROWTH)).build();
	}
}
