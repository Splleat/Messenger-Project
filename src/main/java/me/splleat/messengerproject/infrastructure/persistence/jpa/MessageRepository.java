package me.splleat.messengerproject.infrastructure.persistence.jpa;

import me.splleat.messengerproject.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {}
