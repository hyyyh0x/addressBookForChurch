package addressBookForChurch.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserDTO(
    Long id,
    @NotBlank(message = "이름은 필수 항목입니다.")
    String name,
    @NotBlank(message = "전화번호는 필수 항목입니다.")
    @Pattern(regexp = "010-?\\d{4}-?\\d{4}", message = "전화번호 형식을 지켜주세요.")
    String phone,
    String prayerNote,
    byte[] picture) {

}
