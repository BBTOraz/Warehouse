package bbt.tao.warehouse.dto.permission;


import bbt.tao.warehouse.model.enums.PermissionType;
import lombok.Data;

@Data
public class PermissionDTO {
    private Long id;
    private PermissionType permission;
    private String description;
}
