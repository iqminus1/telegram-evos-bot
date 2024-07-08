package uz.pdp.telegramevosbot.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uz.pdp.telegramevosbot.payload.ApiResultDTO;
import uz.pdp.telegramevosbot.payload.AttachmentDTO;

import java.io.IOException;

public interface AttachmentService {
    ApiResultDTO<AttachmentDTO> create(HttpServletRequest req) throws IOException;
    void read(Integer id, HttpServletResponse resp);
    ApiResultDTO<AttachmentDTO> update(Integer id,HttpServletRequest req) throws IOException;
    void delete(Integer id);
}
