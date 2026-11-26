package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 나의 기도 작성/수정 요청 */
public record SavePrayerRequest(
        @NotBlank(message = "prayerRequest is required")
        @Size(max = 200, message = "prayerRequest must be at most 200 characters")
        String prayerRequest
) {}
