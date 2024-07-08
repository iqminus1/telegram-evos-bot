package uz.pdp.telegramevosbot.entity;

import jakarta.persistence.Entity;
import jakarta.ws.rs.GET;
import lombok.*;
import uz.pdp.telegramevosbot.entity.abs.AbsIntEntity;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity(name = "history_product_basket")
public class HistoryProductBasket extends AbsIntEntity {
    private Integer tgUserId;
    private String productName;
    private Double productPrice;
    private Integer attachmentId;
    private Integer categoryId;
    private Integer quantity;
}
