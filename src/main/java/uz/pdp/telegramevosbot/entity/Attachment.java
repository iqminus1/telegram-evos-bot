package uz.pdp.telegramevosbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import uz.pdp.telegramevosbot.entity.abs.AbsIntEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "attachment")
public class Attachment extends AbsIntEntity {
    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contentType;

    private long size;

    @Column(columnDefinition = "varchar(1000)")
    private String path;
}
