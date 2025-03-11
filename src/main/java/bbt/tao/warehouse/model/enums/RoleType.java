package bbt.tao.warehouse.model.enums;


public enum RoleType {
    ADMIN,
    MANAGER,
    WAREHOUSE_WORKER;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
