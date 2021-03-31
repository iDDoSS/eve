package kz.balthazar.eve.repository;

import kz.balthazar.eve.entity.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
