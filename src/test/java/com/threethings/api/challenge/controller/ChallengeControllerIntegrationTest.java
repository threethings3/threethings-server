package com.threethings.api.challenge.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.threethings.api.challenge.dto.ChallengeCreateRequestDto;
import com.threethings.api.challenge.dto.ChallengeLikeRequestDto;
import com.threethings.api.challenge.factory.ChallengeCreateRequestFactory;
import com.threethings.api.docs.utils.RestDocsTest;
import com.threethings.api.helper.TokenProvider;
import com.threethings.api.init.ChallengeTestInitDB;
import com.threethings.api.init.MemberTestInitDB;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Transactional
public class ChallengeControllerIntegrationTest extends RestDocsTest {
	private static final String MEMBER_TABLE_NAME = "member";
	private static final String CHALLENGE_TABLE_NAME = "challenge";
	private static final String CHALLENGE_MEMBER_TABLE_NAME = "challenge_member";
	@Autowired
	MemberTestInitDB memberTestInitDB;
	@Autowired
	ChallengeTestInitDB challengeTestInitDB;
	@PersistenceContext
	EntityManager em;

	@BeforeEach
	void initDB() {
		resetTableAutoIncrementValue(List.of(MEMBER_TABLE_NAME, CHALLENGE_TABLE_NAME, CHALLENGE_MEMBER_TABLE_NAME));
		memberTestInitDB.initDB();
		challengeTestInitDB.initDB();
	}

	@Test
	@DisplayName("챌린지 생성 테스트 - POST /api/challenge")
	void createChallengeTest() throws Exception {
		// given
		final String url = "/api/challenge";
		final ChallengeCreateRequestDto requestDto = ChallengeCreateRequestFactory.createChallengeCreateRequestDto();

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
			.header("Authorization", TokenProvider.getValidAccessToken())
			.contentType(MediaType.APPLICATION_JSON)
			.content(gson.toJson(requestDto)));

		// then
		resultActions.andExpect(status().isOk())
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName("Authorization").description("AccessToken")
					),
					requestFields(
						fieldWithPath("challengeProfile.challengeCategory")
							.description("link:common/ChallengeCategory.html[카테고리,role=\"popup\"]"),
						fieldWithPath("challengeProfile.categoryImageId").description("이미지 고유 넘버"),
						fieldWithPath("title").description("챌린지 제목"),
						fieldWithPath("goal").description("챌린지 목표"),
						fieldWithPath("goal.perfect").description("최고 목표"),
						fieldWithPath("goal.better").description("중간 목표"),
						fieldWithPath("goal.good").description("최저 목표"),
						fieldWithPath("certificationTime").description("인증 시간"),
						fieldWithPath("certificationTime.startTime").description("인증 시작 시간"),
						fieldWithPath("certificationTime.endTime").description("인증 종료 시간"),
						fieldWithPath("challengePeriodWeeks").description("챌린지 진행"),
						fieldWithPath("cycleDays").description("수행 요일 [1,2,3,4] 와 같이 정렬해야 함"),
						fieldWithPath("isPublic").description("공개 여부"),
						fieldWithPath("maxParticipants").description("최대 참가자 수")
					),
					responseFields(
						fieldWithPath("success").description("성공 여부")
					)
				)
			);
	}

	@Test
	@DisplayName("좋아요 테스트 - POST /api/challenge/like")
	void likeChallengeTest() throws Exception {
		// given
		final String url = "/api/challenge/like";
		final ChallengeLikeRequestDto req = new ChallengeLikeRequestDto(1L, Boolean.FALSE);

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
			.header("Authorization", TokenProvider.getValidAccessToken())
			.contentType(MediaType.APPLICATION_JSON)
			.content(gson.toJson(req)));

		// then
		resultActions.andExpect(status().isOk())
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName("Authorization").description("AccessToken")
					),
					requestFields(
						fieldWithPath("challengeId").description("챌린지 고유 넘버"),
						fieldWithPath("liked").description("현재 좋아요 여부")
					),
					responseFields(
						fieldWithPath("success").description("성공 여부")
					)
				)
			);
	}

	@Test
	@DisplayName("좋아요 실패 테스트 (챌린지 없음) - POST /api/challenge/like")
	void likeChallengeFailTest() throws Exception {
		// given
		final String url = "/api/challenge/like";
		final ChallengeLikeRequestDto req = new ChallengeLikeRequestDto(-1L, Boolean.FALSE);

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
			.header("Authorization", TokenProvider.getValidAccessToken())
			.contentType(MediaType.APPLICATION_JSON)
			.content(gson.toJson(req)));

		// then
		resultActions.andExpect(status().isNotFound())
			.andDo(
				restDocs.document(
					requestHeaders(
						headerWithName("Authorization").description("AccessToken")
					),
					requestFields(
						fieldWithPath("challengeId").description("챌린지 고유 넘버"),
						fieldWithPath("liked").description("현재 좋아요 여부")
					),
					responseFields(
						fieldWithPath("success").description("성공 여부"),
						fieldWithPath("result.msg").description("실패 메시지")
					)
				)
			);
	}

	@Test
	@DisplayName("챌린지 검색 테스트")
	void searchChallengeTest() throws Exception {
		// given
		final String url = "/api/challenge";
		final String keyword = "밥";

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url)
			.header("Authorization", TokenProvider.getValidAccessToken())
			.param("page", "0")
			.param("keyword", keyword));

		// then
		resultActions.andExpect(status().isOk()).andDo(
			restDocs.document(
				requestHeaders(
					headerWithName("Authorization").description("AccessToken")
				),
				queryParameters(
					parameterWithName("page").description("페이지 번호"),
					parameterWithName("keyword").description("검색 키워드")
				),
				responseFields(
					fieldWithPath("success").description("요청 성공 여부"),
					fieldWithPath("result.data.pageNumber").description("현재 페이지 번호"),
					fieldWithPath("result.data.pageSize").description("페이지 크기"),
					fieldWithPath("result.data.isFirst").description("첫 번째 페이지 여부"),
					fieldWithPath("result.data.isLast").description("마지막 페이지 여부"),
					fieldWithPath("result.data.totalPages").description("전체 페이지 수"),
					fieldWithPath("result.data.totalElements").description("전체 요소 수"),
					fieldWithPath("result.data.contents[].challengeId").description("챌린지 ID"),
					fieldWithPath("result.data.contents[].challengeProfile.challengeCategory")
						.description("챌린지 카테고리"),
					fieldWithPath("result.data.contents[].challengeProfile.categoryImageId").description("챌린지 이미지 ID"),
					fieldWithPath("result.data.contents[].title").description("챌린지 제목"),
					fieldWithPath("result.data.contents[].beginChallengeDate")
						.description("챌린지 시작일"),
					fieldWithPath("result.data.contents[].endChallengeDate")
						.description("챌린지 종료일"),
					fieldWithPath("result.data.contents[].certificationTime.startTime").description("인증 시작 시간"),
					fieldWithPath("result.data.contents[].certificationTime.endTime").description("인증 마감 시간"),
					fieldWithPath("result.data.contents[].participants").description("참가자 수"),
					fieldWithPath("result.data.contents[].liked").description("좋아요 여부")
				)
			));
	}

	@ParameterizedTest
	@MethodSource("provideInvalidAuthorizationValue")
	@DisplayName("챌린지 생성 실패 테스트")
	void createChallengeFailTest(String invalidAuthorizationValue) throws Exception {
		// given
		final String url = "/api/challenge";
		final ChallengeCreateRequestDto requestDto = ChallengeCreateRequestFactory.createChallengeCreateRequestDto();

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(url)
			.header("Authorization", invalidAuthorizationValue)
			.contentType(MediaType.APPLICATION_JSON)
			.content(gson.toJson(requestDto)));

		// then
		resultActions.andExpect(status().isUnauthorized());
	}

	private static Stream<Arguments> provideInvalidAuthorizationValue() {
		return Stream.of(
			Arguments.of(TokenProvider.getExpiredAccessToken()),
			Arguments.of(TokenProvider.getIncorrectSignatureToken())
		);
	}

}
