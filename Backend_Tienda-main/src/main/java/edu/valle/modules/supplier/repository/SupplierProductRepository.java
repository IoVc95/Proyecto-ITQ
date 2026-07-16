package edu.valle.modules.supplier.repository;

import edu.valle.modules.supplier.model.SupplierProduct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SupplierProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public SupplierProductRepository(
            @Qualifier("supplierJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<SupplierProduct> findBySku(String sku) {
        try {
            SupplierProduct product = jdbcTemplate.queryForObject("""
                    select id, product_name, size, color, sku, price, available_stock, active
                    from supplier_products
                    where lower(sku) = lower(?)
                    """, this::map, sku);
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public boolean decreaseStock(String sku, int quantity) {
        return jdbcTemplate.update("""
                update supplier_products
                set available_stock = available_stock - ?, updated_at = current_timestamp
                where lower(sku) = lower(?)
                  and active = true
                  and available_stock >= ?
                """, quantity, sku, quantity) == 1;
    }

    public boolean restoreStock(String sku, int quantity) {
        return jdbcTemplate.update("""
                update supplier_products
                set available_stock = available_stock + ?, updated_at = current_timestamp
                where lower(sku) = lower(?)
                """, quantity, sku) == 1;
    }

    private SupplierProduct map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new SupplierProduct(
                resultSet.getLong("id"),
                resultSet.getString("product_name"),
                resultSet.getString("size"),
                resultSet.getString("color"),
                resultSet.getString("sku"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("available_stock"),
                resultSet.getBoolean("active")
        );
    }
}
