package com.mortypoul.education.service;

import com.mortypoul.education.configuration.MyUserDetails;
import com.mortypoul.education.dto.UserDto;
import com.mortypoul.education.dto.UserDtoPersister;
import com.mortypoul.education.entity.User;
import com.mortypoul.education.exceptions.CustomException;
import com.mortypoul.education.exceptions.ResourceNotFoundException;
import com.mortypoul.education.mapper.UserMapper;
import com.mortypoul.education.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    public static final String ACCESS_IS_DENIED = "Access is denied!";
    public static final String ADMIN = "ADMIN";
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAll() {
        User authorizedUser = getCurrentUser();
        if (!authorizedUser.getRoles().contains(ADMIN)) {
            throw new CustomException(ACCESS_IS_DENIED, HttpStatus.UNAUTHORIZED);
        }
        List<User> users = userRepository.findAll();
        return UserMapper.INSTANCE.toDto(users);
    }

    public UserDto getById(Long id) {
        User authorizedUser = getCurrentUser();
        if (!authorizedUser.getRoles().contains(ADMIN) && !authorizedUser.getId().equals(id)) {
            throw new CustomException(ACCESS_IS_DENIED, HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", id));
        return UserMapper.INSTANCE.toDto(user);
    }

    @Transactional
    public UserDto save(UserDtoPersister input) {
        User authorizedUser = getCurrentUser();
        if (!authorizedUser.getRoles().contains(ADMIN)) {
            throw new CustomException(ACCESS_IS_DENIED, HttpStatus.UNAUTHORIZED);
        }
        input.setPassword((new BCryptPasswordEncoder()).encode(input.getPassword()));
        User result = userRepository.save(UserMapper.INSTANCE.toEntity(input));
        return UserMapper.INSTANCE.toDto(result);
    }

    @Transactional
    public void changePassword(Long userId, String password) {
        User authorizedUser = getCurrentUser();
        if (!authorizedUser.getRoles().contains(ADMIN) && !authorizedUser.getId().equals(userId)) {
            throw new CustomException(ACCESS_IS_DENIED, HttpStatus.UNAUTHORIZED);
        }
        if (password.isEmpty()) {
            throw new CustomException("password is required!", HttpStatus.BAD_REQUEST);
        }
        Pattern BCRYPT_PATTERN = Pattern.compile("\\$2[aby]?\\$\\d{2}\\$[./A-Za-z0-9]{22}[./A-Za-z0-9]{31}");
        if (!BCRYPT_PATTERN.matcher(password).matches()) {
            throw new CustomException("Password must have a BCrypt format!", HttpStatus.BAD_REQUEST);
        }
        Optional<User> result = userRepository.findById(userId);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("user", userId);
        }
        User user = result.get();
        user.setPassword(password);
        userRepository.save(user);
        if (!authorizedUser.getRoles().contains(ADMIN)) {
            setCurrentUser(user);
        }
    }

    @Transactional
    public void changeRule(Long userId, String roles) {
        User authorizedUser = getCurrentUser();
        if (!authorizedUser.getRoles().contains(ADMIN)) {
            throw new CustomException(ACCESS_IS_DENIED, HttpStatus.UNAUTHORIZED);
        }
        Optional<User> result = userRepository.findById(userId);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("user", userId);
        }
        User user = result.get();
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User authorizedUser = getCurrentUser();
        if (!authorizedUser.getRoles().contains(ADMIN)) {
            throw new CustomException(ACCESS_IS_DENIED, HttpStatus.UNAUTHORIZED);
        }
        userRepository.deleteById(id);
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof MyUserDetails)) {
            throw new ResourceNotFoundException("logged in user");
        }
        return ((MyUserDetails) principal).getUser();
    }

    private void setCurrentUser(User user) {
        UserDetails myUserDetails = new MyUserDetails(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
