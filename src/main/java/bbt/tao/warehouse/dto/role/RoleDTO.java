package bbt.tao.warehouse.dto.role;

import bbt.tao.warehouse.dto.permission.PermissionDTO;
import bbt.tao.warehouse.model.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long id;
    private RoleType role;
    private String description;
    private List<PermissionDTO> permissions;
}


