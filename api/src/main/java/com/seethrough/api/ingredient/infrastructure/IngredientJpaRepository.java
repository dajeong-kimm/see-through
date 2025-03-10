package com.seethrough.api.ingredient.infrastructure;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.seethrough.api.ingredient.domain.Ingredient;

public interface IngredientJpaRepository extends JpaRepository<Ingredient, String> {

	Slice<Ingredient> findAllBy(Pageable pageable);
}
