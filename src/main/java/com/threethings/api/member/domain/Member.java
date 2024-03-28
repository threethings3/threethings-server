package com.threethings.api.member.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.threethings.api.challenge.domain.Challenge;
import com.threethings.api.challenge.domain.ChallengeCategory;
import com.threethings.api.challengemember.domain.ChallengeMember;
import com.threethings.api.global.common.BaseEntity;
import com.threethings.api.member.converter.ProviderConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 12, unique = true)
	private String nickname;

	private String socialCode;

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	private Set<MemberRole> roleSet = new HashSet<>();

	@Convert(converter = ProviderConverter.class)
	private Provider provider;

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	private Set<ChallengeCategory> favoriteChallengeCategories = new HashSet<>();

	private Long profileImageId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
	private List<ChallengeMember> challengeMemberList = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "favoriteMembers")
	private Set<Challenge> favoriteChallenge = new HashSet<>();

	@Builder
	public Member(String nickname, String socialCode, Provider provider,
		Set<ChallengeCategory> favoriteChallengeCategories, Long profileImageId) {
		this.nickname = nickname;
		this.socialCode = socialCode;
		this.provider = provider;
		addFavoriteChallengeCategories(favoriteChallengeCategories);
		this.profileImageId = profileImageId;
		addRole(MemberRole.ROLE_NORMAL);
	}

	public void addRole(MemberRole role) {
		this.roleSet.add(role);
	}

	public void addFavoriteChallengeCategories(Set<ChallengeCategory> newCategories) {
		this.favoriteChallengeCategories.clear();
		this.favoriteChallengeCategories.addAll(newCategories);
	}

	public void addFavoriteChallenge(Challenge challenge) {
		favoriteChallenge.add(challenge);
	}

	public void removeFavoriteChallenge(Challenge challenge) {
		favoriteChallenge.remove(challenge);
	}

}
