package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class ReportCommentId extends BaseId {
    private ReportCommentId(UUID value) {
        super(value);
    }

    public static ReportCommentId from(UUID value) {
        return new ReportCommentId(value);
    }
}
