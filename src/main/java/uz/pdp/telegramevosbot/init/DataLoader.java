package uz.pdp.telegramevosbot.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.telegramevosbot.entity.User;
import uz.pdp.telegramevosbot.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Optional<User> admin = userRepository.findByUsername("admin");
        if (admin.isPresent()) return;

        User user = new User();
        user.setPassword(passwordEncoder.encode("123"));
        user.setUsername("admin");
        userRepository.save(user);
    }
}
