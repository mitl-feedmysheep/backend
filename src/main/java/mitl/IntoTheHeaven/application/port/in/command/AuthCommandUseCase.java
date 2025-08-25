package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginResponse;

public interface AuthCommandUseCase {
    LoginResponse login(LoginRequest request);
    LoginResponse adminLogin(LoginRequest request);
} 