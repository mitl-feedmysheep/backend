package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.application.port.in.query.ChurchQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChurchQueryService implements ChurchQueryUseCase {

    private final ChurchPort churchPort;

    @Override
    public List<Church> getChurchesByMemberId(MemberId memberId) {
        return churchPort.findChurchesByMemberId(memberId.getValue());
    }

    /* ADMIN */
    @Cacheable(value = "member-roles", key = "#memberId + ':' + #churchId")
    public ChurchRole getCurrentRole(MemberId memberId, ChurchId churchId) {
        return churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId)
                .getRole();
    }

    @Override
    public List<Church> getAdminChurches(MemberId memberId) {
        return churchPort.findChurchMembersByMemberIdAndRole(memberId, ChurchRole.ADMIN)
                .stream()
                .map(ChurchMember::getChurchId)
                .map(id -> churchPort.findById(id.getValue()))
                .toList();
    }

    /* ADMIN */
    @Override
    public List<MemberWithGroups> searchChurchMembers(ChurchId churchId, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return List.of();
        }
        return churchPort.findMembersByChurchIdAndSearch(churchId.getValue(), searchText.trim());
    }
}