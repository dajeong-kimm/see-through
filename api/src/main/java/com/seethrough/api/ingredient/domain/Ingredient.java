package com.seethrough.api.ingredient.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.seethrough.api.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "ingredients")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Ingredient {
	@Id
	@Column(name = "ingredient_id", columnDefinition = "VARCHAR(36)", nullable = false)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID ingredientId;

	@Column(name = "member_id", columnDefinition = "VARCHAR(36)", nullable = false)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID memberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", insertable = false, updatable = false)
	@ToString.Exclude
	private Member member;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "image_path", length = 255, nullable = false)
	private String imagePath;

	@Builder.Default
	@Column(name = "inbound_at", nullable = false, updatable = false)
	private LocalDateTime inboundAt = LocalDateTime.now();

	@Column(name = "expiration_at")
	private LocalDateTime expirationAt;
}
