package tw.eeits.unhappy.eeit198product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.eeits.unhappy.eeit198product.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByParentCategory_Id(Integer parentId);

    // 【新增】根據分類名稱查找分類
    Optional<Category> findByName(String name);
}
