package mitl.IntoTheHeaven.domain.enums;

public enum ChurchRole {
    MEMBER(1),
    LEADER(2),
    ADMIN(3);

    private final int level;

    ChurchRole(int level) {
        this.level = level;
    }

    public boolean hasPermission(ChurchRole required) {
        return this.level >= required.level;
    }
}


