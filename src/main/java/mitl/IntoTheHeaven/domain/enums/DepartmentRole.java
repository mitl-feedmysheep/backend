package mitl.IntoTheHeaven.domain.enums;

public enum DepartmentRole {
    MEMBER(1),
    LEADER(2),
    ADMIN(3);

    private final int level;

    DepartmentRole(int level) {
        this.level = level;
    }

    public boolean hasPermissionOver(DepartmentRole required) {
        return this.level >= required.level;
    }
}
