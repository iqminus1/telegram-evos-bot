package uz.pdp.telegramevosbot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.pdp.telegramevosbot.service.AttachmentService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attachment")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @GetMapping("/{id}")
    public void read(@PathVariable Integer id, HttpServletResponse resp) {
        attachmentService.read(id, resp);
    }

    @PostMapping
    public Object create(HttpServletRequest req) {
        try {
            return attachmentService.create(req);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @PutMapping("/{id}")
    public Object update(@PathVariable Integer id,HttpServletRequest req){
        try {
            return attachmentService.update(id,req);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        attachmentService.delete(id);
    }

}
