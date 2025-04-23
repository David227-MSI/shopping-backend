package tw.eeits.unhappy.ra.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.eeits.unhappy.ra.media.model.ProductMedia;

@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMedia, Integer> {

}
