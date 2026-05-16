package com.primertrimestre.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.primertrimestre.model.Teacher;
import com.primertrimestre.persistence.dao.TeacherDao;
import com.primertrimestre.persistence.util.JdbcUtil;

public class TeacherDaoJdbc extends GenericDaoJdbc<Teacher> implements TeacherDao {

    @Override
    protected String tableName() {
        return "teachers";
    }

    @Override
    protected Teacher map(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getLong("id"));
        teacher.setUsername(rs.getString("username"));
        teacher.setPassword(rs.getString("password"));
        teacher.setFullName(rs.getString("full_name"));
        return teacher;
    }

    @Override
    protected Long getId(Teacher entity) {
        return entity.getId();
    }

    @Override
    public Teacher create(Teacher entity) {
        if (entity == null) return null;
        String sql = "INSERT INTO teachers (username, password, full_name) VALUES (?, ?, ?)";
        try {
            Long id = executeInsert(sql, statement -> {
                statement.setString(1, entity.getUsername());
                statement.setString(2, entity.getPassword());
                statement.setString(3, entity.getFullName());
            });
            entity.setId(id);
            return entity;
        } catch (SQLException e) {
            throw new IllegalStateException("Error creando profesor", e);
        }
    }

    @Override
    public Teacher update(Teacher entity) {
        if (entity == null || entity.getId() == null) return entity;
        String sql = "UPDATE teachers SET username = ?, password = ?, full_name = ? WHERE id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getFullName());
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new IllegalStateException("Error actualizando profesor", e);
        }
    }

    @Override
    public Teacher findByUsername(String username) {
        if (username == null) return null;
        String sql = "SELECT * FROM teachers WHERE username = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error buscando profesor por usuario", e);
        }
    }
}
