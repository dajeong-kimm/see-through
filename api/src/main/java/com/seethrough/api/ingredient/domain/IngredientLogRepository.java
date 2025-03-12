package com.seethrough.api.ingredient.domain;

import java.util.List;

public interface IngredientLogRepository {

	void saveAll(List<IngredientLog> ingredientLogs);
}
