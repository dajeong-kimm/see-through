package com.seethrough.api.member.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.seethrough.api.common.value.UUID;

public interface MemberRepository {
	Slice<Member> findMembers(Pageable pageable);

	Optional<Member> findByMemberId(UUID memberId);
}
