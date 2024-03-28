package com.threethings.api.challenge.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.threethings.api.challenge.converter.DaysOfWeekConverter;
import com.threethings.api.challengemember.domain.ChallengeMember;
import com.threethings.api.global.common.BaseEntity;
import com.threethings.api.member.domain.Member;

import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private ChallengeProfile challengeProfile;

	private String title;

	@Embedded
	private Goal goal;

	@Embedded
	private CertificationTime certificationTime;

	@Convert(converter = DaysOfWeekConverter.class)
	private List<DayOfWeek> cycleDays;

	private LocalDate beginChallengeDate;
	private LocalDate endChallengeDate;
	private Boolean isPublic;
	private Integer maxParticipants;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge")
	private List<ChallengeMember> members = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "favorite_challenges", joinColumns = @JoinColumn(name = "challenge_id"),
		inverseJoinColumns = @JoinColumn(name = "member_id"))
	private Set<Member> favoriteMembers = new HashSet<>();

	@Builder
	public Challenge(ChallengeProfile challengeProfile, String title, Goal goal,
		CertificationTime certificationTime,
		List<Integer> cycleDays, Integer challengePeriodWeeks, Boolean isPublic, Integer maxParticipants) {
		this.challengeProfile = challengeProfile;
		this.title = title;
		this.goal = goal;
		this.certificationTime = certificationTime;
		this.cycleDays = getDayOfWeekList(cycleDays);
		this.beginChallengeDate = calculateBeginDateTime(this.cycleDays, certificationTime,
			SystemTimeProvider.getInstance());
		this.endChallengeDate = calculateEndDateTime(this.beginChallengeDate, challengePeriodWeeks, this.cycleDays);
		this.isPublic = isPublic;
		this.maxParticipants = maxParticipants;
	}

	private LocalDate calculateBeginDateTime(List<DayOfWeek> cycleDays, CertificationTime certificationTime,
		TimeProvider timeProvider) {
		LocalDate today = timeProvider.getDate();
		LocalTime currentTime = timeProvider.getTime();
		DayOfWeek todayDayOfWeek = today.getDayOfWeek();

		// 오늘이 포함되어 있고, 현재 시간이 챌린지 수행 시작 시간보다 이전일 때
		if (cycleDays.contains(todayDayOfWeek) && currentTime.isBefore(certificationTime.getStartTime())) {
			return today;
		}
		// 오늘을 제외한, 다음으로 가까운 요일 찾기
		for (DayOfWeek dayOfWeek : cycleDays) {
			if (dayOfWeek.getValue() > todayDayOfWeek.getValue()) {
				return today.with(TemporalAdjusters.next(dayOfWeek));
			}
		}

		// 주어진 요일 중 오늘 이후로 가장 가까운 요일이 없으면, 가장 첫 번째 요일로 설정
		return today.with(TemporalAdjusters.next(cycleDays.get(0)));
	}

	private LocalDate calculateEndDateTime(LocalDate beginChallenge, int challengePeriodWeeks,
		List<DayOfWeek> cycleDays) {
		int beginIndex = cycleDays.indexOf(beginChallenge.getDayOfWeek());
		int endIndex = (beginIndex - 1 + cycleDays.size()) % cycleDays.size();
		return beginChallenge.with(TemporalAdjusters.next(cycleDays.get(endIndex)))
			.plusWeeks(challengePeriodWeeks - 1);
	}

	private List<DayOfWeek> getDayOfWeekList(List<Integer> values) {
		return values.stream().map(DayOfWeek::of).toList();
	}

	public void addMember(ChallengeMember member) {
		if (members.size() < maxParticipants) {
			members.add(member);
		}
	}

	public void addFavoriteMember(Member member) {
		favoriteMembers.add(member);
		member.addFavoriteChallenge(this);
	}

	public void removeFavoriteMember(Member member) {
		favoriteMembers.remove(member);
		member.removeFavoriteChallenge(this);
	}

}
