package uz.pdp.telegramevosbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.telegramevosbot.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    Optional<Product> findByNameAndCategoryId(String name, Integer category_id);
}
