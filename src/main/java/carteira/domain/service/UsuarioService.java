package carteira.domain.service;

import carteira.domain.enums.RoleEnum;
import carteira.domain.exception.NegocioException;
import carteira.domain.model.Usuario;
import carteira.domain.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UsuarioService {

    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        Usuario usu = repository.findByUsuario(usuario.getUsuario()).orElse(null);

        if (usu != null && !usu.equals(usuario)) {
            throw new NegocioException("Já existe um registro com o usuário informado");
        }

        if (usu == null && usuario.getRoles() == null) {
            usuario.addRoles(RoleEnum.ROLE_USER);
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return repository.save(usuario);
    }

    @Transactional
    public List<Usuario> buscar() {
        return repository.findAll();
    }

    @Transactional
    public Usuario buscar(String usuarioId) {
        if (usuarioId == null) {
            throw new NegocioException("Id não informado");
        }
        return repository.findById(usuarioId)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado"));
    }

    @Transactional
    public void verificarSeUsuarioExiste(String usuarioId) {
        if (usuarioId == null) {
            throw new NegocioException("Id não informado");
        }

        if (!repository.existsById(usuarioId)) {
            throw new NegocioException("Usuário não encontrado");
        }
    }

    @Transactional
    public void excluir(String usuarioId) {
        if (usuarioId == null) {
            throw new NegocioException("Id não informado");
        }
        repository.deleteById(usuarioId);
    }
}
