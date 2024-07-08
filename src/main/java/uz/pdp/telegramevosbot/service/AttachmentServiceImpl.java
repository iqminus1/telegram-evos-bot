package uz.pdp.telegramevosbot.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.telegramevosbot.entity.Attachment;
import uz.pdp.telegramevosbot.exceptions.NotFoundException;
import uz.pdp.telegramevosbot.finals.BasePath;
import uz.pdp.telegramevosbot.payload.ApiResultDTO;
import uz.pdp.telegramevosbot.payload.AttachmentDTO;
import uz.pdp.telegramevosbot.repository.AttachmentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;

    @Override
    public ApiResultDTO<AttachmentDTO> create(HttpServletRequest req) {
        return ApiResultDTO.success(toDTO(copyFile(new Attachment(), req, false)));
    }


    @Override
    public void read(Integer id, HttpServletResponse resp) {
        Attachment byId = findById(id);
        try {
            Files.copy(Path.of(byId.getPath()), resp.getOutputStream());
        } catch (IOException e) {
            throw new NotFoundException("Attachment not found with path");
        }
    }

    @Override
    public ApiResultDTO<AttachmentDTO> update(Integer id, HttpServletRequest req) {
        return ApiResultDTO.success(toDTO(copyFile(findById(id), req, true)));
    }

    @Override
    public void delete(Integer id) {
        Attachment byId = findById(id);
        try {
            Files.delete(Path.of(byId.getPath()));
            attachmentRepository.delete(byId);
        } catch (IOException e) {
            throw new NotFoundException("Attachment not found with path");
        }
    }

    private Attachment copyFile(Attachment attachment, HttpServletRequest req, boolean update) {
        if (update) {
            try {
                Files.delete(Path.of(attachment.getPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Part file = req.getPart("file");
            String contentType = file.getContentType();
            String originalName = file.getSubmittedFileName();
            long size = file.getSize();

            String[] split = originalName.split("\\.");
            String s = split[split.length - 1];

            UUID uuid = UUID.randomUUID();

            String name = uuid + "." + s;

            String pathString = BasePath.FILES + "/" + name;

            Path path = Path.of(pathString);

            Files.copy(file.getInputStream(), path);

            attachment.setName(name);
            attachment.setSize(size);
            attachment.setPath(pathString);
            attachment.setContentType(contentType);
            attachment.setOriginalName(originalName);

            attachmentRepository.save(attachment);

            return attachment;
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Attachment findById(Integer id) {
        return attachmentRepository.findById(id).orElseThrow(
                () -> new NotFoundException("attachment not found"));
    }

    private AttachmentDTO toDTO(Attachment attachment) {
        return new AttachmentDTO(attachment.getId(),
                attachment.getOriginalName(),
                attachment.getName(),
                attachment.getContentType(),
                attachment.getSize(),
                attachment.getPath());
    }
}
