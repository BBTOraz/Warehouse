package bbt.tao.warehouse.security.service;

import bbt.tao.warehouse.mapper.UserMapper;
import bbt.tao.warehouse.model.User;
import bbt.tao.warehouse.repository.UserRepository;
import bbt.tao.warehouse.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        log.info("UserDTO info: {}", userMapper.toDTO(user));
        log.info("User info: {}", user.getPassword());
        return new CustomUserDetails(userMapper.toDTO(user));
    }
}
