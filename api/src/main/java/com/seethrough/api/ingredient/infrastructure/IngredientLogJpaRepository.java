package com.seethrough.api.ingredient.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seethrough.api.ingredient.domain.IngredientLog;

public interface IngredientLogJpaRepository extends JpaRepository<IngredientLog, Integer> {
}
