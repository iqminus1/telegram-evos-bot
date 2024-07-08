package uz.pdp.telegramevosbot.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
            private Integer attachmentId;
    private Integer parentCategoryId;
}
