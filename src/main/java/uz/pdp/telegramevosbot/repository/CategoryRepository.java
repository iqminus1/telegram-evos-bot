package uz.pdp.telegramevosbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.telegramevosbot.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    Optional<Category> findByNameAndParentCategoryId(String name, Integer parentCategory_id);
    List<Category> findByParentCategoryIsNull() ;
    boolean existsByNameAndParentCategoryIdIsNull(String name);
    boolean existsByNameAndParentCategoryId(String name, Integer parentCategory_id);
    Optional<Category> findByNameAndParentCategoryIsNull(String name);
}
