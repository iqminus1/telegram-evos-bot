package uz.pdp.telegramevosbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import uz.pdp.telegramevosbot.entity.abs.AbsIntEntity;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity(name = "product")
public class Product extends AbsIntEntity {
    private String name;

    private Double price;

    @ManyToOne
    private Attachment attachment;

    @ManyToOne
    private Category category;
}
