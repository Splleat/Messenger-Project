package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {}
