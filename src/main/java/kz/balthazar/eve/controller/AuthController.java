package kz.balthazar.eve.controller;

import kz.balthazar.eve.entity.dto.AuthRequest;
import kz.balthazar.eve.entity.model.ConfirmationToken;
import kz.balthazar.eve.repository.ConfirmationTokenRepo;
import kz.balthazar.eve.repository.UserRepo;
import kz.balthazar.eve.security.jwt.JwtProvider;
import kz.balthazar.eve.entity.model.User;
import kz.balthazar.eve.service.EmailSenderService;
import kz.balthazar.eve.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    ConfirmationTokenRepo tokenRepo;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/registration")
    public String registerUser(@RequestBody @Valid User user) {
        String login = user.getLogin();
        if(userService.findByLogin(login) != null)
            throw new RuntimeException("Login already exists");
        userService.saveUser(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        tokenRepo.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("qaidosp@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:9192/confirm-account?token="+confirmationToken.getConfirmationToken());
        emailSenderService.sendEmail(mailMessage);
        return "OK";
    }

    @PostMapping("/reset_password")
    public String reset(String email, String token, String password) {
        if(email != null) {
            User user = userRepo.findByEmailIgnoreCase(email);
            if (user == null) {
                return "User doesn't exist!";
            } else {
                ConfirmationToken confirmationToken = new ConfirmationToken(user);
                tokenRepo.save(confirmationToken);
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(email);
                mailMessage.setSubject("Change password link");
                mailMessage.setFrom("qaidosp@gmail.com");
                mailMessage.setText("To change password, please click here : "
                        + "http://localhost:9192/reset_password?token=" + confirmationToken.getConfirmationToken());
                emailSenderService.sendEmail(mailMessage);
                return "Reset link sent to email";
            }
        }
        else if (token != null && password != null) {
            ConfirmationToken tokenn = tokenRepo.findByConfirmationToken(token);
            User user = userRepo.findByEmailIgnoreCase(tokenn.getUser().getEmail());
            user.setPassword(passwordEncoder.encode(password));
            userRepo.save(user);
            return  "Password changed";
        }
        throw new RuntimeException("No email or token/password provided");
    }

    @PostMapping(value="/confirm-account")
    public String confirmUserAccount(@RequestParam String token)
    {
        ConfirmationToken tokenn = tokenRepo.findByConfirmationToken(token);
        String response;
        if(tokenn != null)
        {
            User user = userRepo.findByEmailIgnoreCase(tokenn.getUser().getEmail());
            user.setEnabled(true);
            userRepo.save(user);
            response = "Account verified";
        }
        else
        {
            response = "The link is invalid or broken!";
        }

        return response;
    }

    @PostMapping("/login")
    public String auth(@RequestBody AuthRequest request) {
        User user = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
        if (user == null) throw new RuntimeException("Invalid credentials");
        return jwtProvider.generateToken(user.getLogin());
    }
}