package com.c4_soft.dzone_oauth2_spring.official_greeting_api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockAuthentication;

@SpringBootTest
@AutoConfigureMockMvc
class OfficialGreetingApiApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void givenSecurityContextIsEmpty_whenGetGreeting_thenUnauthorized() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithAnonymousUser
	void givenSecurityContextIsAnonymous_whenGetGreeting_thenUnauthorized() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockAuthentication(name = "brice", authorities = { "NICE" })
	void givenUserHasNiceAuthority_whenGetGreeting_thenOk() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Hello brice! You are granted with [NICE]."));
	}

	@Test
	@WithMockAuthentication("AUTHOR")
	void givenUserDoesNotHaveNiceAuthority_whenGetGreeting_thenForbidden() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isForbidden());
	}

	@Test
	@WithJwt("brice.json")
	void givenUserIsBrice_whenGetGreeting_thenOk() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Hello brice! You are granted with [NICE, ACTOR]."));
	}

	@Test
	@WithJwt("igor.json")
	void givenUserIsIgor_whenGetGreeting_thenForbidden() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isForbidden());
	}

}
