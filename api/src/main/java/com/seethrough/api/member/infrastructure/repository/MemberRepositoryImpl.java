package com.seethrough.api.member.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.seethrough.api.common.value.UUID;
import com.seethrough.api.member.domain.Member;
import com.seethrough.api.member.domain.MemberRepository;
import com.seethrough.api.member.infrastructure.entity.MemberEntity;
import com.seethrough.api.member.infrastructure.mapper.MemberEntityMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

	private final MemberJpaRepository memberJpaRepository;
	private final MemberEntityMapper memberEntityMapper;

	@Override
	public Slice<Member> findMembers(Pageable pageable) {
		log.debug("[Repository] findMembers 호출: page={}, size={}, sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

		Slice<MemberEntity> entities = memberJpaRepository.findAllByDeletedAtIsNull(pageable);

		log.debug("[Repository] 조회된 엔티티 수: {}, 남은 데이터 여부: {}", entities.getNumberOfElements(), entities.hasNext());

		if (!entities.getContent().isEmpty()) {
			log.debug("[Repository] 첫 번째 엔티티 상세 정보:{}", entities.getContent().get(0));
		}

		return entities.map(memberEntityMapper::toDomain);
	}

	@Override
	public Optional<Member> findByMemberId(UUID memberId) {
		log.debug("[Repository] findByMemberId 호출: memberId={}", memberId);

		Optional<MemberEntity> entity = memberJpaRepository.findByMemberIdAndDeletedAtIsNull(memberId.value());

		log.debug("[Repository] 조회된 엔티티: {}", entity);

		return entity.map(memberEntityMapper::toDomain);
	}
}
