package uz.pdp.telegramevosbot.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResultDTO<T> {
    private boolean success;
    private T data;
    private String errorMessage;

    public static <T> ApiResultDTO<T> success(T data) {
        ApiResultDTO<T> apiResultDTO = new ApiResultDTO<>();
        apiResultDTO.setSuccess(true);
        apiResultDTO.setData(data);
        return apiResultDTO;
    }

    public static ApiResultDTO<?> error(String errorMessage) {
        ApiResultDTO<Object> apiResultDTO = new ApiResultDTO<>();
        apiResultDTO.setErrorMessage(errorMessage);
        apiResultDTO.setSuccess(false);
        return apiResultDTO;
    }
}
