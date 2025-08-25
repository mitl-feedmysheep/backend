package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChurchMemberService {

    @Cacheable(value = "member-roles", key = "#memberId + ':' + #churchId")
    public ChurchRole getCurrentRole(MemberId memberId, ChurchId churchId) {
        // TODO: Implement real lookup via ChurchMemberPort
        return ChurchRole.ADMIN; // Temporary stub for wiring
    }
}


