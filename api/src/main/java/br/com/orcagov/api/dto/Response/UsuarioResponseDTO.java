package br.com.orcagov.api.dto.Response;

import br.com.orcagov.api.entity.enums.TipoUsuario;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String userName;
    private String email;
    private TipoUsuario tipoUser;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    // Senha nunca deve ser exposta!
}
