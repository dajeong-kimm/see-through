package com.seethrough.api.member.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberRepository {
	Slice<Member> findMembers(Pageable pageable);
}
