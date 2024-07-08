package uz.pdp.telegramevosbot.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.pdp.telegramevosbot.entity.abs.AbsIntEntity;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity(name = "category")
public class Category extends AbsIntEntity {
    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Attachment attachment;

    @ManyToOne(cascade = CascadeType.ALL)
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.EAGER)
    private List<Category> categories;

    @OneToMany(mappedBy = "category",fetch = FetchType.EAGER)
    private List<Product> products;
}
