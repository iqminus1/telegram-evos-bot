package uz.pdp.telegramevosbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;
import uz.pdp.telegramevosbot.entity.abs.AbsIntEntity;
import uz.pdp.telegramevosbot.enums.ProductStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity(name = "product_basket")
public class ProductBasket extends AbsIntEntity {
    @ManyToOne
    private TgUser tgUser;
    @ManyToOne
    private Product product;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private ProductStatusEnum productStatusEnum;
}
