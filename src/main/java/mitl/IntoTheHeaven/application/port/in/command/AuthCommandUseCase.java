package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginResponse;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;

public interface AuthCommandUseCase {
    LoginResponse login(LoginRequest request);

    LoginResponse adminLogin(LoginRequest request);

    /* ADMIN */
    LoginResponse selectChurch(MemberId memberId, ChurchId churchId);
}