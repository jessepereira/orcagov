package br.com.orcagov.api.repository;

import br.com.orcagov.api.entity.Usuario;
import br.com.orcagov.api.entity.enums.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar por username
    Optional<Usuario> findByUserName(String userName);
    
    // Buscar por email
    Optional<Usuario> findByEmail(String email);
    
    // Buscar por username e senha (para login)
    Optional<Usuario> findByUserNameAndPassword(String userName, String password);
    
    // Buscar usuários ativos
    List<Usuario> findByAtivoTrue();
    
    // Buscar por tipo de usuário
    List<Usuario> findByTipoUser(TipoUsuario tipoUser);
    
    // Buscar usuários ativos por tipo
    List<Usuario> findByTipoUserAndAtivoTrue(TipoUsuario tipoUser);
    
    // Verificar se username já existe
    boolean existsByUserName(String userName);
    
    // Verificar se email já existe
    boolean existsByEmail(String email);
    
    // Consulta customizada para buscar usuários por termo
    @Query("SELECT u FROM Usuario u WHERE " +
           "u.ativo = true AND (" +
           "LOWER(u.userName) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Usuario> buscarPorTermo(@Param("termo") String termo);
}