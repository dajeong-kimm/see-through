package com.seethrough.api.ingredient.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface IngredientRepository {

	Slice<Ingredient> findIngredients(Pageable pageable);
}
