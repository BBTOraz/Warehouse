package bbt.tao.warehouse.model.enums;


public enum Role_Type {
    ADMIN,
    MANAGER,
    WAREHOUSE_WORKER,
    ACCOUNTANT,
    VIEWER;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
