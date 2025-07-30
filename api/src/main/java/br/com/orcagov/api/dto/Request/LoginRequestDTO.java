package br.com.orcagov.api.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "Nome de usuário é obrigatório")
    private String userName;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}