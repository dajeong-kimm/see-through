package com.seethrough.api.member.infrastructure.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

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
public class MemberEntity {
	@Id
	@Column(name = "member_id", length = 36, nullable = false)
	private String memberId;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "age", nullable = false)
	private Integer age;

	@Column(name = "image_path", length = 255)
	private String imagePath;

	@Column(name = "preferred_foods", columnDefinition = "json", nullable = false)
	@Convert(converter = JsonArrayConverter.class)
	private List<String> preferredFoods;

	@Column(name = "disliked_foods", columnDefinition = "json", nullable = false)
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
}
