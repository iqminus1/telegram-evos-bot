package uz.pdp.telegramevosbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import uz.pdp.telegramevosbot.entity.abs.AbsIntEntity;
import uz.pdp.telegramevosbot.enums.StateEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "tg_user")
public class TgUser extends AbsIntEntity {
    private Long userId;

    @Enumerated(EnumType.STRING)
    private StateEnum stateEnum;

    private Integer categoryId;
    private Integer productWithQueryId;

    public TgUser(Long userId, StateEnum stateEnum, Integer categoryId) {
        this.userId = userId;
        this.stateEnum = stateEnum;
        this.categoryId = categoryId;
    }
}
