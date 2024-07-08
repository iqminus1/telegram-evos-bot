package uz.pdp.telegramevosbot.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachmentDTO {
    private Integer id;
    private String originalName;
    private String name;
    private String contentType;
    private long size;
    private String path;
}
