package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.member.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

}
