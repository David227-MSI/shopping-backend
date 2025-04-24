package tw.eeits.unhappy.gy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.eeits.unhappy.gy.domain.UserMember;

@Repository
public interface UserRepository extends JpaRepository<UserMember, Integer> {
}
