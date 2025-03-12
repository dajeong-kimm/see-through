package com.seethrough.api.ingredient.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.seethrough.api.ingredient.domain.IngredientLog;
import com.seethrough.api.ingredient.domain.IngredientLogRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class IngredientLogRepositoryImpl implements IngredientLogRepository {

	@PersistenceContext
	private final EntityManager entityManager;
	private final IngredientLogJpaRepository ingredientLogJpaRepository;

	@Override
	public void saveAll(List<IngredientLog> ingredientLogs) {
		log.debug("[Repository] saveAll 호출: {} 개의 로그", ingredientLogs.size());

		for (IngredientLog ingredientLog : ingredientLogs) {
			entityManager.persist(ingredientLog);
		}
	}
}
