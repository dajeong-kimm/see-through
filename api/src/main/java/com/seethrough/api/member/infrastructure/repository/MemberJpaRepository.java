package com.seethrough.api.member.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seethrough.api.member.infrastructure.entity.MemberEntity;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, String> {
}
