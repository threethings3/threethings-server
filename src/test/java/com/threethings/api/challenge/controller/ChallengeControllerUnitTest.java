package com.threethings.api.challenge.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.threethings.api.challenge.dto.ChallengeCreateRequestDto;
import com.threethings.api.challenge.dto.ChallengeLikeRequestDto;
import com.threethings.api.challenge.facade.ChallengeFacade;
import com.threethings.api.challenge.factory.ChallengeCreateRequestFactory;
import com.threethings.api.config.LocalDateSerializer;
import com.threethings.api.config.LocalTimeSerializer;

@ExtendWith(MockitoExtension.class)
public class ChallengeControllerUnitTest {
	@InjectMocks
	ChallengeController challengeController;

	@Mock
	ChallengeFacade challengeFacade;

	MockMvc mockMvc;

	GsonBuilder gsonBuilder = new GsonBuilder();
	Gson gson;

	@BeforeEach
	void init() {
		mockMvc = MockMvcBuilders.standaloneSetup(challengeController).build();
		gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
		gsonBuilder.registerTypeAdapter(LocalTime.class, new LocalTimeSerializer());
		gson = gsonBuilder.setPrettyPrinting().create();
	}

	@Test
	@DisplayName("챌린지 생성")
	void createChallengeTest() throws Exception {
		// given
		final ChallengeCreateRequestDto req = ChallengeCreateRequestFactory.createChallengeCreateRequestDto();

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/challenge")
			.contentType(MediaType.APPLICATION_JSON)
			.content(gson.toJson((req))));

		// then
		resultActions.andExpect(status().isOk());
		then(challengeFacade).should(times(1)).createChallenge(any(), any());
	}

	@Test
	@DisplayName("좋아요 등록")
	void likeChallengeTest() throws Exception {
		// given
		final ChallengeLikeRequestDto req = new ChallengeLikeRequestDto(1L, Boolean.FALSE);

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/challenge/like")
			.contentType(MediaType.APPLICATION_JSON)
			.content(gson.toJson(req)));

		// then
		resultActions.andExpect(status().isOk());
		then(challengeFacade).should(times(1)).likeChallenge(any(), any());
	}
}
