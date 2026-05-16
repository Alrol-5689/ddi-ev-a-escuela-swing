package com.primertrimestre.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.primertrimestre.model.Administrator;
import com.primertrimestre.persistence.dao.AdministratorDao;
import com.primertrimestre.persistence.util.JdbcUtil;

public class AdministratorDaoJdbc extends GenericDaoJdbc<Administrator> implements AdministratorDao {

    @Override
    protected String tableName() {
        return "administrators";
    }

    @Override
    protected Administrator map(ResultSet rs) throws SQLException {
        Administrator administrator = new Administrator();
        administrator.setId(rs.getLong("id"));
        administrator.setUsername(rs.getString("username"));
        administrator.setPassword(rs.getString("password"));
        administrator.setFullName(rs.getString("full_name"));
        return administrator;
    }

    @Override
    protected Long getId(Administrator entity) {
        return entity.getId();
    }

    @Override
    public Administrator create(Administrator entity) {
        if (entity == null) return null;
        String sql = "INSERT INTO administrators (username, password, full_name) VALUES (?, ?, ?)";
        try {
            Long id = executeInsert(sql, statement -> {
                statement.setString(1, entity.getUsername());
                statement.setString(2, entity.getPassword());
                statement.setString(3, entity.getFullName());
            });
            entity.setId(id);
            return entity;
        } catch (SQLException e) {
            throw new IllegalStateException("Error creando administrador", e);
        }
    }

    @Override
    public Administrator update(Administrator entity) {
        if (entity == null || entity.getId() == null) return entity;
        String sql = "UPDATE administrators SET username = ?, password = ?, full_name = ? WHERE id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getFullName());
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new IllegalStateException("Error actualizando administrador", e);
        }
    }

    @Override
    public Administrator findByUsername(String username) {
        if (username == null) return null;
        String sql = "SELECT * FROM administrators WHERE username = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error buscando administrador por usuario", e);
        }
    }
}
