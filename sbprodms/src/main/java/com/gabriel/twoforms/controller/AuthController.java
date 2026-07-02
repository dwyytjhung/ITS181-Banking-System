package com.gabriel.twoforms.controller;

import com.gabriel.twoforms.entity.UserData;
import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserDataRepository userDataRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Optional<UserData> optionalUser = userDataRepository.findByUsername(username);
        if (optionalUser.isPresent() && optionalUser.get().getPassword().equals(password)) {
            UserData u = optionalUser.get();
            User userDto = new User(u.getId(), u.getUsername(), u.getPassword(), u.getRole(), u.getFullName());
            userDto.setEmail(u.getEmail());
            userDto.setPhone(u.getPhone());
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, User>> getAllUsers() {
        Map<String, User> userMap = new HashMap<>();
        userDataRepository.findAll().forEach(u -> {
            User userDto = new User(u.getId(), u.getUsername(), u.getPassword(), u.getRole(), u.getFullName());
            userDto.setEmail(u.getEmail());
            userDto.setPhone(u.getPhone());
            userMap.put(u.getUsername(), userDto);
        });
        return ResponseEntity.ok(userMap);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        UserData u = new UserData();
        u.setId(user.getId());
        u.setUsername(user.getUsername());
        u.setPassword(user.getPassword());
        u.setRole(user.getRole());
        u.setFullName(user.getFullName());
        u.setEmail(user.getEmail());
        u.setPhone(user.getPhone());
        userDataRepository.save(u);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
        Optional<UserData> uOpt = userDataRepository.findById(id);
        if (uOpt.isPresent()) {
            UserData u = uOpt.get();
            if (user.getUsername() != null && !user.getUsername().isBlank()) {
                u.setUsername(user.getUsername());
            }
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                u.setPassword(user.getPassword());
            }
            if (user.getFullName() != null && !user.getFullName().isBlank()) {
                u.setFullName(user.getFullName());
            }
            userDataRepository.save(u);
            User updated = new User(u.getId(), u.getUsername(), u.getPassword(), u.getRole(), u.getFullName());
            updated.setEmail(u.getEmail());
            updated.setPhone(u.getPhone());
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        Optional<UserData> uOpt = userDataRepository.findByUsername(username);
        if (uOpt.isPresent()) {
            userDataRepository.delete(uOpt.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
