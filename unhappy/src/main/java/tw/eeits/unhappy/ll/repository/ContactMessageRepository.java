package tw.eeits.unhappy.ll.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.eeits.unhappy.ll.model.ContactMessage;

public interface ContactMessageRepository
		extends JpaRepository<ContactMessage, Integer> {
	List<ContactMessage> findByIsHandledFalse();

}