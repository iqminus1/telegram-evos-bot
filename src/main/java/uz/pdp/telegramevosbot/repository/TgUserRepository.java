package uz.pdp.telegramevosbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.telegramevosbot.entity.TgUser;

import java.util.Optional;

public interface TgUserRepository extends JpaRepository<TgUser,Integer> {
    Optional<TgUser> findByUserId(Long userId);
}
