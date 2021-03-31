package kz.balthazar.eve;

import kz.balthazar.eve.entity.model.Role;
import kz.balthazar.eve.entity.model.User;
import kz.balthazar.eve.repository.RoleRepo;
import kz.balthazar.eve.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class App {

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @PostConstruct
    public void init() {

        Role adminRole = new Role();
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        roleRepo.save(userRole);
        adminRole.setName("ROLE_ADMIN");
        roleRepo.save(adminRole);

        User user = new User();
        user.setEmail("qaidosp@gmail.com");
        user.setPassword(passwordEncoder.encode("asdasd"));
        user.setEnabled(true);
        user.setRole(adminRole);
        user.setLogin("asdasd");
        userRepo.save(user);
        String s = "s";
        for(int i=0; i<3; i++) {
            user = new User();
            user.setFirstName(s+=s);
            user.setEmail("qaidosp@gmail.com");
            user.setPassword(passwordEncoder.encode("asdasd"));
            user.setEnabled(true);
            user.setRole(adminRole);
            user.setLogin(s);
            userRepo.save(user);
        }
    }
}
