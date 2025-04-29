package tw.eeits.unhappy.eee.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.repository.UserMemberRepository;

@Service
@RequiredArgsConstructor
public class UserMemberService {
    private final UserMemberRepository userMemberRepository;

    public UserMember findUserById(Integer id) {
        return userMemberRepository.findById(id).orElse(null);
    }
}