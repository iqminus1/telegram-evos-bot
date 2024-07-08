package uz.pdp.telegramevosbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.telegramevosbot.entity.Product;
import uz.pdp.telegramevosbot.entity.ProductBasket;

import java.util.List;

    public interface ProductBasketRepository extends JpaRepository<ProductBasket,Integer> {
        List<ProductBasket> findAllByTgUserId(Integer tgUser_id);
    }
