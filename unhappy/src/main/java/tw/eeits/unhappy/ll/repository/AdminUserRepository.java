package tw.eeits.unhappy.ll.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.eeits.unhappy.ll.model.AdminUser;

public interface AdminUserRepository
		extends JpaRepository<AdminUser, Integer> {
			Optional<AdminUser> findByUsername(String username);

}