package com.seethrough.api.member.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberRepository {
	Slice<Member> findMembers(Pageable pageable);

	Optional<Member> findByMemberId(UUID memberId);
}
