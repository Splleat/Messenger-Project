package me.splleat.messengerproject.domain.member;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.domain.member.exception.MemberAlreadyExistsException;
import me.splleat.messengerproject.infrastructure.persistence.jpa.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member register(Member member) {
        if (memberRepository.existsByUserIdAndChannelId(member.getUserId(), member.getChannelId())) {
            throw new MemberAlreadyExistsException();
        }

        return memberRepository.save(member);
    }

    public List<Member> getAllMember(long channelId) {
        return memberRepository.findAllByChannelId(channelId);
    }
}
