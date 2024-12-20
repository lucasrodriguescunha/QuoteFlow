package com.workspacepi.apiquoteflow.adapters.jdbc.usuarios;

import com.workspacepi.apiquoteflow.adapters.http.allErrors.ErrorHandler;
import com.workspacepi.apiquoteflow.domain.usuarios.Permissoes;
import com.workspacepi.apiquoteflow.domain.usuarios.Usuarios;
import com.workspacepi.apiquoteflow.domain.usuarios.UsuariosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.workspacepi.apiquoteflow.adapters.jdbc.usuarios.UsuariosSqlExpressions.*;

@Repository
public class UsuariosJDBCRepository implements UsuariosRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    public UsuariosJDBCRepository(NamedParameterJdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    private RowMapper<Usuarios> createUsuariosRowMapper() {
        return (rs, rowNum) -> {
            UUID id_usuario = UUID.fromString(rs.getString("id_usuario"));
            String nome = rs.getString("nome");
            String email = rs.getString("email");
            String senha = rs.getString("senha");

            String telefone = rs.getString("telefone");
            if (telefone == null || telefone.trim().isEmpty()) {
                telefone = null;
            }

            String idEmpresaStr = rs.getString("id_empresa");
            UUID id_empresa;
            if (idEmpresaStr != null && !idEmpresaStr.trim().isEmpty()) {
                id_empresa = UUID.fromString(idEmpresaStr);
            } else {
                id_empresa = null;
            }

            Permissoes permissao = Permissoes.valueOf(rs.getString("permissao"));

            return new Usuarios(id_usuario, nome, email, senha, telefone, id_empresa, permissao);
        };
    }

    private MapSqlParameterSource parameterSource(Usuarios usuario) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id_usuario", usuario.getId_usuario());
        params.addValue("nome", usuario.getNome());
        params.addValue("email", usuario.getEmail());
        params.addValue("senha", usuario.getSenha());

        // Campos opcionais
        if (usuario.getTelefone() != null) {
            params.addValue("telefone", usuario.getTelefone());
        } else {
            params.addValue("telefone", null);
        }

        if (usuario.getId_empresa() != null) {
            params.addValue("id_empresa", usuario.getId_empresa());
        } else {
            params.addValue("id_empresa", null);
        }

        params.addValue("permissao", usuario.getPermissao().name());

        return params;
    }

    @Override
    public List<Usuarios> findAll() {
        List<Usuarios> usuarios = List.of();
        try {
            usuarios = jdbcTemplate.query(sqlSelectAllUsers(), createUsuariosRowMapper());
            return usuarios;
        } catch (Exception e) {
            LOGGER.error("Houver um erro ao consultar os usuários: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Usuarios findById(UUID id_usuario) {
        List<Usuarios> usuarios;
        try {
            MapSqlParameterSource params = new MapSqlParameterSource("id_usuario", id_usuario);
            usuarios = jdbcTemplate.query(sqlSelectUserById(), params, createUsuariosRowMapper());
            return usuarios.isEmpty() ? null : usuarios.get(0);
        } catch (Exception e) {
            LOGGER.error("Houve um erro ao consultar o  usuário: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public Optional<UserDetails> findByEmail(String email) {
        List<Usuarios> usuarios;
        try {
            MapSqlParameterSource params = new MapSqlParameterSource("email", email);
            usuarios = jdbcTemplate.query(sqlSelectUserByEmail(), params, createUsuariosRowMapper());

            // Retorna um Optional: se a lista estiver vazia, retorna Optional.empty(); caso contrário, retorna o primeiro usuário como Optional
            return usuarios.isEmpty() ? Optional.empty() : Optional.of(usuarios.get(0));
        } catch (Exception e) {
            LOGGER.error("Houve um erro ao consultar o usuário: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public Boolean cadastrarUsuario(Usuarios usuario) {
        try {
            MapSqlParameterSource params = parameterSource(usuario);
            int numLinhasAfetadas = jdbcTemplate.update(sqlNewUser(), params);
            return numLinhasAfetadas > 0;
        } catch (Exception e) {
            LOGGER.error("Houve um erro ao cadastrar o usuário: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Boolean modificarUsuario(Usuarios usuario) {
        try {
            MapSqlParameterSource params = parameterSource(usuario);
            int numLinhasAfetadas = jdbcTemplate.update(sqlUpdateUser(), params);
            return numLinhasAfetadas > 0;
        } catch (Exception e) {
            LOGGER.error("Houver um erro ao atualizar o usuário: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Boolean deleteUsuarioById(UUID id_usuario) {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource("id_usuario", id_usuario);
            int numLinhasAfetas = jdbcTemplate.update(sqlDeleteUserById(), params);
            return numLinhasAfetas == 1;
        } catch (Exception e) {
            LOGGER.error("Houve um erro ao deletar o usuario: " + id_usuario + ". \n" + e.getMessage());
            throw e;
        }
    }
}
