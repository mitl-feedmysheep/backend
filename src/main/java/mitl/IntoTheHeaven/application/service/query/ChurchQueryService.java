package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.ChurchQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.MemberId;

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
}