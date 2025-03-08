package com.seethrough.api.member.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.seethrough.api.common.converter.JsonArrayConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "members")
public class Member {
	@Id
	@Column(name = "member_id", columnDefinition = "VARCHAR(36)", nullable = false)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID memberId;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "age", nullable = false)
	private Integer age;

	@Column(name = "image_path", length = 255)
	private String imagePath;

	@Column(name = "preferred_foods", columnDefinition = "JSON", nullable = false)
	@Convert(converter = JsonArrayConverter.class)
	private List<String> preferredFoods;

	@Column(name = "disliked_foods", columnDefinition = "JSON", nullable = false)
	@Convert(converter = JsonArrayConverter.class)
	private List<String> dislikedFoods;

	@Column(name = "is_registered", nullable = false)
	private Boolean isRegistered;

	@Column(name = "recognition_times", nullable = false)
	private Integer recognitionTimes;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	// TODO: 검증 로직
}
