package mitl.IntoTheHeaven.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.FindEmailResponse;
import mitl.IntoTheHeaven.application.port.in.query.MemberQueryUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

  private final MemberQueryUseCase memberQueryUseCase;

  @GetMapping("/find-email")
  public FindEmailResponse findEmail() {
    return new FindEmailResponse();
  }
}
