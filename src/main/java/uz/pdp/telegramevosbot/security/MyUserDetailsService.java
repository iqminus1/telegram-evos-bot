package uz.pdp.telegramevosbot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import uz.pdp.telegramevosbot.entity.User;
import uz.pdp.telegramevosbot.exceptions.NotFoundException;
import uz.pdp.telegramevosbot.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> byUsername = userRepository.findByUsername(username);

        if (byUsername.isEmpty()) {
            throw new NotFoundException();
        }

        User user = byUsername.get();

        return org.springframework.security.core.userdetails.User.withUsername(username)
                .password(user.getPassword()).build();
    }
}
