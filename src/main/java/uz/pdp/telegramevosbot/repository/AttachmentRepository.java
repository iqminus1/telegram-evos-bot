package uz.pdp.telegramevosbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.telegramevosbot.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
}
