package tw.eeits.unhappy.eee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.eeits.unhappy.eee.domain.UserMember;

@Repository
public interface UserMemberRepository extends JpaRepository<UserMember, Integer> {
    Optional<UserMember> findByEmail(String email);
}
