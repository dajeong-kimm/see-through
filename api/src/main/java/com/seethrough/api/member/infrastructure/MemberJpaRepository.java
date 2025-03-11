package com.seethrough.api.member.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.seethrough.api.member.domain.Member;

public interface MemberJpaRepository extends JpaRepository<Member, String> {

	Slice<Member> findAllByDeletedAtIsNull(Pageable pageable);

	Optional<Member> findByMemberIdAndDeletedAtIsNull(UUID memberId);
}
