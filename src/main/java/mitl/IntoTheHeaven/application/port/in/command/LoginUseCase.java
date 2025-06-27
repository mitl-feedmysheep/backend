package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginResponse;

public interface LoginUseCase {
    LoginResponse login(LoginRequest request);
} 