package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class ReportId extends BaseId {
    private ReportId(UUID value) {
        super(value);
    }

    public static ReportId from(UUID value) {
        return new ReportId(value);
    }
}
