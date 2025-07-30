package br.com.orcagov.api.dto.Request;

import br.com.orcagov.api.entity.enums.TipoUsuario;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "Nome de usuário é obrigatório")
    @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Nome de usuário deve conter apenas letras, números, pontos, hífens e underscores")
    private String userName;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    private String password;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    private String email;

    private TipoUsuario tipoUser = TipoUsuario.USER;
}