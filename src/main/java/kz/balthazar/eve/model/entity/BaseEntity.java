package kz.balthazar.eve.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
    @Id
    @GeneratedValue
    Long id;

    OffsetDateTime createdDate;

    @OneToOne
    User createdUser;

    OffsetDateTime updatedDate;

    @OneToOne
    User updatedUser;

    OffsetDateTime deletedDate;

    @OneToOne
    User deletedUser;

}
