package com.threethings.api.challenge.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threethings.api.challenge.domain.Challenge;
import com.threethings.api.challenge.dto.ChallengeCreateRequestDto;
import com.threethings.api.challenge.dto.ChallengeLikeRequestDto;
import com.threethings.api.challenge.dto.ChallengeSummaryResponseDto;
import com.threethings.api.challenge.service.ChallengeService;
import com.threethings.api.challengemember.domain.ChallengeMember;
import com.threethings.api.challengemember.service.ChallengeMemberService;
import com.threethings.api.member.domain.Member;
import com.threethings.api.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeFacade {

	private final ChallengeService challengeService;
	private final MemberService memberService;
	private final ChallengeMemberService challengeMemberService;

	private static final int CHALLENGE_PER_PAGE = 10;

	@Transactional
	public void createChallenge(Long memberId, ChallengeCreateRequestDto req) {
		Member member = memberService.findMember(memberId);
		Challenge challenge = ChallengeCreateRequestDto.toEntity(req);
		ChallengeMember challengeMember = new ChallengeMember(challenge, member);
		challengeService.saveChallenge(challenge);
		challengeMemberService.saveChallengeMember(challengeMember);
	}

	@Transactional
	public void likeChallenge(Long memberId, ChallengeLikeRequestDto req) {
		Member member = memberService.findMember(memberId);
		Challenge challenge = challengeService.findChallenge(req.getChallengeId());
		if (!req.getLiked()) {
			challenge.addFavoriteMember(member);
		} else {
			challenge.removeFavoriteMember(member);
		}
	}

	public Page<ChallengeSummaryResponseDto> searchChallenge(int page, String keyword, Long memberId) {
		return challengeService.searchChallenge(keyword, memberId, PageRequest.of(page, CHALLENGE_PER_PAGE));
	}
}
