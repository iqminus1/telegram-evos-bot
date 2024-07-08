package uz.pdp.telegramevosbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.telegramevosbot.entity.HistoryProductBasket;

import java.util.List;
import java.util.Optional;

public interface HistoryProductBasketRepository extends JpaRepository<HistoryProductBasket, Integer> {
    List<HistoryProductBasket> findByTgUserId(Integer tgUserId);
}
