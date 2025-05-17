package tw.eeits.unhappy.ttpp.userMember.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.eeits.unhappy.ttpp.userMember.model.EmailToken;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Integer> {
    Optional<EmailToken> findByEmail(String email);
    
    void deleteByEmail(String email);
}
