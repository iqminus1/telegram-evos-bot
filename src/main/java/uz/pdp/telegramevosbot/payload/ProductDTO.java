package uz.pdp.telegramevosbot.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    private Integer attachmentId;

    @NotNull
    private Integer categoryId;
}
