package com.threethings.api.challenge.facade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.threethings.api.challenge.domain.Challenge;
import com.threethings.api.challenge.dto.ChallengeCreateRequestDto;
import com.threethings.api.challenge.dto.ChallengeLikeRequestDto;
import com.threethings.api.challenge.factory.ChallengeCreateRequestFactory;
import com.threethings.api.challenge.factory.ChallengeFactory;
import com.threethings.api.challenge.service.ChallengeService;
import com.threethings.api.challengemember.service.ChallengeMemberService;
import com.threethings.api.member.domain.Member;
import com.threethings.api.member.factory.domain.MemberFactory;
import com.threethings.api.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class ChallengeFacadeTest {
	@InjectMocks
	ChallengeFacade challengeFacade;
	@Mock
	ChallengeService challengeService;
	@Mock
	MemberService memberService;
	@Mock
	ChallengeMemberService challengeMemberService;

	@Test
	@DisplayName("챌린지 생성 (파사드 테스트)")
	void createChallengeFacadeTest() {
		// given
		final Long memberId = 1L;
		final ChallengeCreateRequestDto requestDto = ChallengeCreateRequestFactory.createChallengeCreateRequestDto();

		// when
		given(memberService.findMember(memberId)).willReturn(MemberFactory.createMember());
		challengeFacade.createChallenge(memberId, requestDto);

		// then
		then(challengeService).should(times(1)).saveChallenge(any());
		then(challengeMemberService).should(times(1)).saveChallengeMember(any());
	}

	@Test
	@DisplayName("챌린지 좋아요 설정")
	void setLikeChallenge() {
		// given
		final ChallengeLikeRequestDto requestDto = new ChallengeLikeRequestDto(1L, Boolean.FALSE);
		Challenge challenge = ChallengeFactory.createChallenge();
		Member member = MemberFactory.createMember();

		// when
		given(memberService.findMember(anyLong())).willReturn(member);
		given(challengeService.findChallenge(anyLong())).willReturn(challenge);
		challengeFacade.likeChallenge(1L, requestDto);

		// then
		assertThat(member.getFavoriteChallenge().size()).isEqualTo(1);
		assertThat(challenge.getFavoriteMembers().size()).isEqualTo(1);
		assertThat(member.getFavoriteChallenge().contains(challenge)).isTrue();
		assertThat(challenge.getFavoriteMembers().contains(member)).isTrue();
	}
}
