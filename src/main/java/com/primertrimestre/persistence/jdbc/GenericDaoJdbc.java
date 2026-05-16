package com.primertrimestre.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.primertrimestre.persistence.dao.GenericDao;
import com.primertrimestre.persistence.util.JdbcUtil;

public abstract class GenericDaoJdbc<T> implements GenericDao<T, Long> {

    protected abstract String tableName();

    protected abstract T map(ResultSet rs) throws SQLException;

    protected abstract Long getId(T entity);

    @Override
    public T findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM " + tableName() + " WHERE id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error buscando en " + tableName(), e);
        }
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + tableName() + " ORDER BY id";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Error listando " + tableName(), e);
        }
    }

    @Override
    public T createOrUpdate(T entity) {
        if (entity == null) return null;
        return getId(entity) == null ? create(entity) : update(entity);
    }

    @Override
    public void delete(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM " + tableName() + " WHERE id = ?";
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error borrando en " + tableName(), e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + tableName();
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Error contando " + tableName(), e);
        }
    }

    protected Long executeInsert(String sql, SqlConsumer<PreparedStatement> binder) throws SQLException {
        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            binder.accept(statement);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : null;
            }
        }
    }

    @FunctionalInterface
    protected interface SqlConsumer<TStatement> {
        void accept(TStatement statement) throws SQLException;
    }
}
