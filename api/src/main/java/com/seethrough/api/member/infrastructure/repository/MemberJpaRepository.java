package com.seethrough.api.member.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.seethrough.api.member.infrastructure.entity.MemberEntity;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, String> {

	Slice<MemberEntity> findAllByDeletedAtIsNull(Pageable pageable);

	Optional<MemberEntity> findByMemberIdAndDeletedAtIsNull(String memberId);
}
