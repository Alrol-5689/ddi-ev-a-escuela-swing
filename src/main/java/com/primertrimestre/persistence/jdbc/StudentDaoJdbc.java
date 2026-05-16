package com.primertrimestre.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.primertrimestre.model.Module;
import com.primertrimestre.model.Student;
import com.primertrimestre.persistence.dao.StudentDao;
import com.primertrimestre.persistence.util.JdbcUtil;

public class StudentDaoJdbc extends GenericDaoJdbc<Student> implements StudentDao {

    @Override
    protected String tableName() {
        return "students";
    }

    @Override
    protected Student map(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setUsername(rs.getString("username"));
        student.setPassword(rs.getString("password"));
        student.setFullName(rs.getString("full_name"));
        return student;
    }

    @Override
    protected Long getId(Student entity) {
        return entity.getId();
    }

    @Override
    public Student create(Student entity) {
        if (entity == null) return null;
        String sql = "INSERT INTO students (username, password, full_name) VALUES (?, ?, ?)";
        try {
            Long id = executeInsert(sql, statement -> {
                statement.setString(1, entity.getUsername());
                statement.setString(2, entity.getPassword());
                statement.setString(3, entity.getFullName());
            });
            entity.setId(id);
            return entity;
        } catch (SQLException e) {
            throw new IllegalStateException("Error creando estudiante", e);
        }
    }

    @Override
    public Student update(Student entity) {
        if (entity == null || entity.getId() == null) return entity;
        String sql = "UPDATE students SET username = ?, password = ?, full_name = ? WHERE id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getFullName());
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new IllegalStateException("Error actualizando estudiante", e);
        }
    }

    @Override
    public Student findByUsername(String username) {
        if (username == null) return null;
        String sql = "SELECT * FROM students WHERE username = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error buscando estudiante por usuario", e);
        }
    }

    @Override
    public List<Module> findEnrolledModulesByStudentId(Long id) {
        if (id == null) return Collections.emptyList();
        String sql = """
                SELECT m.*, t.id AS teacher_id, t.username AS teacher_username,
                       t.password AS teacher_password, t.full_name AS teacher_full_name
                FROM modules m
                INNER JOIN enrollments e ON e.module_id = m.id
                LEFT JOIN teachers t ON t.id = m.teacher_id
                WHERE e.student_id = ?
                ORDER BY m.name
                """;
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                List<Module> modules = new ArrayList<>();
                ModuleDaoJdbc mapper = new ModuleDaoJdbc();
                while (rs.next()) {
                    modules.add(mapper.mapWithTeacher(rs));
                }
                return modules;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error listando modulos matriculados", e);
        }
    }
}
