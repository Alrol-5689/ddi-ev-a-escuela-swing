package com.primertrimestre.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.primertrimestre.model.Module;
import com.primertrimestre.model.Teacher;
import com.primertrimestre.persistence.dao.ModuleDao;
import com.primertrimestre.persistence.util.JdbcUtil;

public class ModuleDaoJdbc extends GenericDaoJdbc<Module> implements ModuleDao {

    private static final String SELECT_WITH_TEACHER = """
            SELECT m.*, t.id AS teacher_id, t.username AS teacher_username,
                   t.password AS teacher_password, t.full_name AS teacher_full_name
            FROM modules m
            LEFT JOIN teachers t ON t.id = m.teacher_id
            """;

    @Override
    protected String tableName() {
        return "modules";
    }

    @Override
    protected Module map(ResultSet rs) throws SQLException {
        return mapWithTeacher(rs);
    }

    Module mapWithTeacher(ResultSet rs) throws SQLException {
        Module module = new Module();
        module.setId(rs.getLong("id"));
        module.setCreditsECTS(rs.getInt("credits_ects"));
        module.setName(rs.getString("name"));
        module.setCode(rs.getString("code"));

        Long teacherId = readNullableLong(rs, "teacher_id");
        if (teacherId != null) {
            Teacher teacher = new Teacher();
            teacher.setId(teacherId);
            teacher.setUsername(rs.getString("teacher_username"));
            teacher.setPassword(rs.getString("teacher_password"));
            teacher.setFullName(rs.getString("teacher_full_name"));
            module.setTeacher(teacher);
        }
        return module;
    }

    @Override
    protected Long getId(Module entity) {
        return entity.getId();
    }

    @Override
    public Module findById(Long id) {
        if (id == null) return null;
        String sql = SELECT_WITH_TEACHER + " WHERE m.id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? mapWithTeacher(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error buscando modulo", e);
        }
    }

    @Override
    public List<Module> findAll() {
        String sql = SELECT_WITH_TEACHER + " ORDER BY m.name";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<Module> modules = new ArrayList<>();
            while (rs.next()) {
                modules.add(mapWithTeacher(rs));
            }
            return modules;
        } catch (SQLException e) {
            throw new IllegalStateException("Error listando modulos", e);
        }
    }

    @Override
    public Module create(Module entity) {
        if (entity == null) return null;
        String sql = "INSERT INTO modules (credits_ects, name, code, teacher_id) VALUES (?, ?, ?, ?)";
        try {
            Long id = executeInsert(sql, statement -> {
                statement.setInt(1, entity.getCreditsECTS());
                statement.setString(2, entity.getName());
                statement.setString(3, entity.getCode());
                bindTeacherId(statement, 4, entity);
            });
            entity.setId(id);
            return entity;
        } catch (SQLException e) {
            throw new IllegalStateException("Error creando modulo", e);
        }
    }

    @Override
    public Module update(Module entity) {
        if (entity == null || entity.getId() == null) return entity;
        String sql = "UPDATE modules SET credits_ects = ?, name = ?, code = ?, teacher_id = ? WHERE id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, entity.getCreditsECTS());
            statement.setString(2, entity.getName());
            statement.setString(3, entity.getCode());
            bindTeacherId(statement, 4, entity);
            statement.setLong(5, entity.getId());
            statement.executeUpdate();
            return findById(entity.getId());
        } catch (SQLException e) {
            throw new IllegalStateException("Error actualizando modulo", e);
        }
    }

    @Override
    public List<Module> findByTeacherId(Long teacherId) {
        if (teacherId == null) return Collections.emptyList();
        String sql = SELECT_WITH_TEACHER + " WHERE m.teacher_id = ? ORDER BY m.name";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, teacherId);
            try (ResultSet rs = statement.executeQuery()) {
                List<Module> modules = new ArrayList<>();
                while (rs.next()) {
                    modules.add(mapWithTeacher(rs));
                }
                return modules;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error listando modulos por profesor", e);
        }
    }

    private void bindTeacherId(PreparedStatement statement, int index, Module module) throws SQLException {
        if (module.getTeacher() == null || module.getTeacher().getId() == null) {
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, module.getTeacher().getId());
        }
    }

    private Long readNullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }
}
