package edu.valle.soap.supplier.repository;

import edu.valle.soap.supplier.model.SupplierProduct;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SupplierProductJdbcRepository {

    private static final String SELECT_FIELDS = "id, name, barcode, price, stock, active";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SupplierProduct> rowMapper = (rs, rowNum) -> {
        SupplierProduct product = new SupplierProduct();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setBarcode(rs.getString("barcode"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStock(rs.getInt("stock"));
        product.setActive(rs.getBoolean("active"));
        return product;
    };

    public SupplierProductJdbcRepository(
            @Qualifier("supplierJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SupplierProduct> findAll() {
        return jdbcTemplate.query(
                "select " + SELECT_FIELDS + " from supplier_products order by id",
                rowMapper);
    }

    public Optional<SupplierProduct> findByBarcode(String barcode) {
        try {
            SupplierProduct product = jdbcTemplate.queryForObject(
                    "select " + SELECT_FIELDS + " from supplier_products where barcode = ?",
                    rowMapper,
                    barcode);
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public void decreaseStock(String barcode, int quantity) {
        jdbcTemplate.update(
                "update supplier_products set stock = stock - ? where barcode = ?",
                quantity,
                barcode);
    }
}
