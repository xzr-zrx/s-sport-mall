package cn.bugstack.api.dto.auth;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterRequestDTO {
    @NotBlank @Size(min=3,max=64) private String username;
    @NotBlank @Size(min=6,max=64) private String password;
    @Size(max=64) private String nickname;
}
