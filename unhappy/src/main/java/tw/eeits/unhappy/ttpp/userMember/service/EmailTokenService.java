package tw.eeits.unhappy.ttpp.userMember.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp.userMember.model.EmailToken;
import tw.eeits.unhappy.ttpp.userMember.repository.EmailTokenRepository;

@Service
@RequiredArgsConstructor
public class EmailTokenService {
    private final EmailTokenRepository emailTokenRepository;

    public EmailToken findTokenByEmail(String email) {
        return emailTokenRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public void deleteByEmail(String email) {
        emailTokenRepository.deleteByEmail(email);
    }
}
