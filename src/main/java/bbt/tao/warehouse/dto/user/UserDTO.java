package bbt.tao.warehouse.dto.user;

import bbt.tao.warehouse.dto.role.RoleDTO;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private List<RoleDTO> roles;
}


