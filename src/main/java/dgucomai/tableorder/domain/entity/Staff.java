package dgucomai.tableorder.domain.entity;

import dgucomai.tableorder.domain.type.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Staff {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "staff_id")
  private Long staffId;

  @Column(name = "password")
  private String password;

  @Column(name = "staff_name")
  private String staffName;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Role role;
}
