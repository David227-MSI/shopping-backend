package tw.eeits.unhappy.eee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.eeits.unhappy.eee.domain.CustomerServiceBean;

public interface CustomerServiceRepository extends JpaRepository<CustomerServiceBean, Integer> {

}
