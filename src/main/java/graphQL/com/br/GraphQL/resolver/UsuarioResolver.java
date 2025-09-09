package graphQL.com.br.GraphQL.resolver;


import graphQL.com.br.GraphQL.entity.Usuario;
import graphQL.com.br.GraphQL.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
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
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setIdade(idade);
        return usuarioRepository.save(usuario);
    }

    @MutationMapping
    public Usuario atualizarUsuario(@Argument Long id,
                                    @Argument String nome,
                                    @Argument String email,
                                    @Argument Integer idade) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            if (nome != null) usuario.setNome(nome);
            if (email != null) usuario.setEmail(email);
            if (idade != null) usuario.setIdade(idade);
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    @MutationMapping
    public Boolean deletarUsuario(@Argument Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}