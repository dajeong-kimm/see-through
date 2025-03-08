package com.seethrough.api.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class SliceRequestDto {
	@Builder.Default
	Integer page = 1;
	@Builder.Default
	Integer size = 10;
	@Builder.Default
	String sortBy = "createdAt";
	@Builder.Default
	String sortDirection = "DESC";

	/**
	 * Spring Data JPA의 Pageable 객체로 변환
	 * JPA의 페이지 번호는 0부터 시작하므로 조정 (page - 1)
	 */
	public Pageable toPageable() {
		Sort.Direction direction = Sort.Direction.fromString(sortDirection);
		return PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
	}
}