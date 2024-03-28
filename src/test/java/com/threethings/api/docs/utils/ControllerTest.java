package com.threethings.api.docs.utils;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ControllerTest {
	@Autowired
	protected Gson gson;

	@Autowired
	protected MockMvc mockMvc;

	@PersistenceContext
	private EntityManager em;

	@Transactional
	protected void resetTableAutoIncrementValue(List<String> tableNames) {
		em.flush();
		em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE ").executeUpdate();

		for (String tableName : tableNames) {
			em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
			em.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1").executeUpdate();
		}
		em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
	}
}
