package graphQL.com.br.GraphQL.controller;

import graphQL.com.br.GraphQL.entity.Usuario;
import graphQL.com.br.GraphQL.exception.GraphQLCustomException;
import graphQL.com.br.GraphQL.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

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
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new GraphQLCustomException(
                        "Usuário não encontrado", // Mensagem simples
                        "USUARIO_NAO_ENCONTRADO",
                        "Usuário com ID " + id + " não encontrado" // Mensagem técnica
                ));
    }

    @MutationMapping
    public Usuario criarUsuario(@Argument String nome,
                                @Argument String email,
                                @Argument Integer idade) {
        try {
            // Verifica se email já existe
            if (usuarioRepository.existsByEmail(email)) {
                throw new GraphQLCustomException(
                        "E-mail já cadastrado", // Mensagem simples
                        "EMAIL_JA_CADASTRADO",
                        "O email " + email + " já está cadastrado no sistema" // Mensagem técnica
                );
            }

            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setIdade(idade);
            return usuarioRepository.save(usuario);

        } catch (DataIntegrityViolationException e) {
            throw new GraphQLCustomException(
                    "Erro ao processar os dados", // Mensagem simples
                    "ERRO_INTEGRIDADE",
                    "Erro de integridade de dados. Verifique os campos únicos" // Mensagem técnica
            );
        } catch (Exception e) {
            throw new GraphQLCustomException(
                    "E-mail já cadastrado", // Mensagem simples
                    "ERRO_INTERNO",
                    "Erro interno ao criar usuário: " + e.getMessage() // Mensagem técnica
            );
        }
    }

    @MutationMapping
    public Usuario atualizarUsuario(@Argument Long id,
                                    @Argument String nome,
                                    @Argument String email,
                                    @Argument Integer idade) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new GraphQLCustomException(
                            "Usuário não encontrado", // Mensagem simples
                            "USUARIO_NAO_ENCONTRADO",
                            "Usuário com ID " + id + " não encontrado" // Mensagem técnica
                    ));

            // Verifica se email já existe em outro usuário
            if (email != null && !email.equals(usuario.getEmail())) {
                if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
                    throw new GraphQLCustomException(
                            "E-mail já cadastrado", // Mensagem simples
                            "EMAIL_JA_CADASTRADO",
                            "O email " + email + " já está cadastrado para outro usuário" // Mensagem técnica
                    );
                }
            }

            if (nome != null) usuario.setNome(nome);
            if (email != null) usuario.setEmail(email);
            if (idade != null) usuario.setIdade(idade);
            return usuarioRepository.save(usuario);

        } catch (DataIntegrityViolationException e) {
            throw new GraphQLCustomException(
                    "Erro ao processar os dados", // Mensagem simples
                    "ERRO_INTEGRIDADE",
                    "Erro de integridade de dados" // Mensagem técnica
            );
        } catch (Exception e) {
            throw new GraphQLCustomException(
                    "Erro interno do sistema", // Mensagem simples
                    "ERRO_INTERNO",
                    "Erro ao atualizar usuário: " + e.getMessage() // Mensagem técnica
            );
        }
    }

    @MutationMapping
    public Boolean deletarUsuario(@Argument Long id) {
        try {
            if (!usuarioRepository.existsById(id)) {
                throw new GraphQLCustomException(
                        "Usuário não encontrado", // Mensagem simples
                        "USUARIO_NAO_ENCONTRADO",
                        "Usuário com ID " + id + " não encontrado" // Mensagem técnica
                );
            }

            usuarioRepository.deleteById(id);
            return true;

        } catch (Exception e) {
            throw new GraphQLCustomException(
                    "Erro interno do sistema", // Mensagem simples
                    "ERRO_INTERNO",
                    "Erro ao deletar usuário: " + e.getMessage() // Mensagem técnica
            );
        }
    }
}