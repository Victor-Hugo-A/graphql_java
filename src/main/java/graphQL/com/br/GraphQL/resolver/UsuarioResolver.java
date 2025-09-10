package graphQL.com.br.GraphQL.resolver;

import graphQL.com.br.GraphQL.entity.Usuario;
import graphQL.com.br.GraphQL.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UsuarioResolver {

    private final UsuarioRepository usuarioRepository;

    @QueryMapping
    public List<Usuario> todosUsuarios() {
        return usuarioRepository.findAll();
    }

    @QueryMapping
    public Usuario usuarioPorId(@Argument Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @MutationMapping
    public Usuario criarUsuario(@Argument String nome,
                                @Argument String email,
                                @Argument Integer idade) {
        try {
            // Verifica se email já existe
            if (usuarioRepository.existsByEmail(email)) {
                throw new RuntimeException("EMAIL_JA_CADASTRADO:O email " + email + " já está cadastrado no sistema");
            }

            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setIdade(idade);
            return usuarioRepository.save(usuario);

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("ERRO_INTEGRIDADE:Erro de integridade de dados. Verifique os campos únicos");
        } catch (Exception e) {
            throw new RuntimeException("ERRO_INTERNO:Erro interno ao criar usuário: " + e.getMessage());
        }
    }

    @MutationMapping
    public Usuario atualizarUsuario(@Argument Long id,
                                    @Argument String nome,
                                    @Argument String email,
                                    @Argument Integer idade) {
        try {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();

                // Verifica se email já existe em outro usuário
                if (email != null && !email.equals(usuario.getEmail())) {
                    if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
                        throw new RuntimeException("EMAIL_JA_CADASTRADO:O email " + email + " já está cadastrado para outro usuário");
                    }
                }

                if (nome != null) usuario.setNome(nome);
                if (email != null) usuario.setEmail(email);
                if (idade != null) usuario.setIdade(idade);
                return usuarioRepository.save(usuario);
            }
            throw new RuntimeException("USUARIO_NAO_ENCONTRADO:Usuário com ID " + id + " não encontrado");

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("ERRO_INTEGRIDADE:Erro de integridade de dados");
        } catch (Exception e) {
            throw new RuntimeException("ERRO_INTERNO:Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    @MutationMapping
    public Boolean deletarUsuario(@Argument Long id) {
        try {
            if (usuarioRepository.existsById(id)) {
                usuarioRepository.deleteById(id);
                return true;
            }
            throw new RuntimeException("USUARIO_NAO_ENCONTRADO:Usuário com ID " + id + " não encontrado");

        } catch (Exception e) {
            throw new RuntimeException("ERRO_INTERNO:Erro ao deletar usuário: " + e.getMessage());
        }
    }
}