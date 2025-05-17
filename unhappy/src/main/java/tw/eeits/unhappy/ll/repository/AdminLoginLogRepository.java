package tw.eeits.unhappy.ll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.eeits.unhappy.ll.model.AdminLoginLog;

public interface AdminLoginLogRepository
		extends JpaRepository<AdminLoginLog, Integer> {

}