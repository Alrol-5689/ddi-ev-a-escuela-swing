package com.primertrimestre.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.primertrimestre.model.Enrollment;
import com.primertrimestre.model.Module;
import com.primertrimestre.model.Student;
import com.primertrimestre.model.Teacher;
import com.primertrimestre.persistence.dao.EnrollmentDao;
import com.primertrimestre.persistence.util.JdbcUtil;

public class EnrollmentDaoJdbc extends GenericDaoJdbc<Enrollment> implements EnrollmentDao {

    private static final String SELECT_WITH_RELATIONS = """
            SELECT e.id AS enrollment_id, e.grade,
                   s.id AS student_id, s.username AS student_username,
                   s.password AS student_password, s.full_name AS student_full_name,
                   m.id AS module_id, m.credits_ects, m.name AS module_name, m.code AS module_code,
                   t.id AS teacher_id, t.username AS teacher_username,
                   t.password AS teacher_password, t.full_name AS teacher_full_name
            FROM enrollments e
            INNER JOIN students s ON s.id = e.student_id
            INNER JOIN modules m ON m.id = e.module_id
            LEFT JOIN teachers t ON t.id = m.teacher_id
            """;

    @Override
    protected String tableName() {
        return "enrollments";
    }

    @Override
    protected Enrollment map(ResultSet rs) throws SQLException {
        return mapWithRelations(rs);
    }

    @Override
    protected Long getId(Enrollment entity) {
        return entity.getId();
    }

    @Override
    public Enrollment findById(Long id) {
        if (id == null) return null;
        String sql = SELECT_WITH_RELATIONS + " WHERE e.id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? mapWithRelations(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error buscando matricula", e);
        }
    }

    @Override
    public List<Enrollment> findAll() {
        String sql = SELECT_WITH_RELATIONS + " ORDER BY e.id";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<Enrollment> enrollments = new ArrayList<>();
            while (rs.next()) {
                enrollments.add(mapWithRelations(rs));
            }
            return enrollments;
        } catch (SQLException e) {
            throw new IllegalStateException("Error listando matriculas", e);
        }
    }

    @Override
    public Enrollment create(Enrollment entity) {
        if (entity == null) return null;
        String sql = "INSERT INTO enrollments (module_id, student_id, grade) VALUES (?, ?, ?)";
        try {
            Long id = executeInsert(sql, statement -> {
                statement.setLong(1, entity.getModule().getId());
                statement.setLong(2, entity.getStudent().getId());
                bindGrade(statement, 3, entity.getGrade());
            });
            entity.setId(id);
            return findById(id);
        } catch (SQLException e) {
            throw new IllegalStateException("Error creando matricula", e);
        }
    }

    @Override
    public Enrollment update(Enrollment entity) {
        if (entity == null || entity.getId() == null) return entity;
        String sql = "UPDATE enrollments SET module_id = ?, student_id = ?, grade = ? WHERE id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, entity.getModule().getId());
            statement.setLong(2, entity.getStudent().getId());
            bindGrade(statement, 3, entity.getGrade());
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
            return findById(entity.getId());
        } catch (SQLException e) {
            throw new IllegalStateException("Error actualizando matricula", e);
        }
    }

    @Override
    public List<Enrollment> findByStudentId(Long studentId) {
        if (studentId == null) return Collections.emptyList();
        return findMany(SELECT_WITH_RELATIONS + " WHERE e.student_id = ? ORDER BY m.name", studentId);
    }

    @Override
    public List<Enrollment> findByModuleId(Long moduleId) {
        if (moduleId == null) return Collections.emptyList();
        return findMany(SELECT_WITH_RELATIONS + " WHERE e.module_id = ? ORDER BY s.full_name", moduleId);
    }

    @Override
    public Enrollment findByStudentAndModule(Long studentId, Long moduleId) {
        if (studentId == null || moduleId == null) return null;
        String sql = SELECT_WITH_RELATIONS + " WHERE e.student_id = ? AND e.module_id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            statement.setLong(2, moduleId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? mapWithRelations(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error buscando matricula por estudiante y modulo", e);
        }
    }

    @Override
    public List<Module> findAvailableModulesByStudentId(Long studentId) {
        if (studentId == null) return Collections.emptyList();
        String sql = """
                SELECT m.*, t.id AS teacher_id, t.username AS teacher_username,
                       t.password AS teacher_password, t.full_name AS teacher_full_name
                FROM modules m
                LEFT JOIN teachers t ON t.id = m.teacher_id
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM enrollments e
                    WHERE e.module_id = m.id AND e.student_id = ?
                )
                ORDER BY m.name
                """;
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                List<Module> modules = new ArrayList<>();
                ModuleDaoJdbc mapper = new ModuleDaoJdbc();
                while (rs.next()) {
                    modules.add(mapper.mapWithTeacher(rs));
                }
                return modules;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error listando modulos disponibles", e);
        }
    }

    private List<Enrollment> findMany(String sql, Long id) {
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                List<Enrollment> enrollments = new ArrayList<>();
                while (rs.next()) {
                    enrollments.add(mapWithRelations(rs));
                }
                return enrollments;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error listando matriculas", e);
        }
    }

    private Enrollment mapWithRelations(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getLong("enrollment_id"));
        enrollment.setGrade(readNullableDouble(rs, "grade"));

        Student student = new Student();
        student.setId(rs.getLong("student_id"));
        student.setUsername(rs.getString("student_username"));
        student.setPassword(rs.getString("student_password"));
        student.setFullName(rs.getString("student_full_name"));
        enrollment.setStudent(student);

        Module module = new Module();
        module.setId(rs.getLong("module_id"));
        module.setCreditsECTS(rs.getInt("credits_ects"));
        module.setName(rs.getString("module_name"));
        module.setCode(rs.getString("module_code"));

        Long teacherId = readNullableLong(rs, "teacher_id");
        if (teacherId != null) {
            Teacher teacher = new Teacher();
            teacher.setId(teacherId);
            teacher.setUsername(rs.getString("teacher_username"));
            teacher.setPassword(rs.getString("teacher_password"));
            teacher.setFullName(rs.getString("teacher_full_name"));
            module.setTeacher(teacher);
        }

        enrollment.setModule(module);
        return enrollment;
    }

    private void bindGrade(PreparedStatement statement, int index, Double grade) throws SQLException {
        if (grade == null) {
            statement.setNull(index, Types.DOUBLE);
        } else {
            statement.setDouble(index, grade);
        }
    }

    private Long readNullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private Double readNullableDouble(ResultSet rs, String column) throws SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }
}
