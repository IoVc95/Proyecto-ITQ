package edu.valle.modules.supplier.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class SupplierProductRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void conditionalDecreaseReportsSuccessOnlyForOneUpdatedRow() {
        SupplierProductRepository repository = new SupplierProductRepository(jdbcTemplate);
        when(jdbcTemplate.update(anyString(), anyInt(), anyString(), anyInt()))
                .thenReturn(1, 0);

        assertTrue(repository.decreaseStock("SKU-1", 3));
        assertFalse(repository.decreaseStock("SKU-1", 30));
        verify(jdbcTemplate, org.mockito.Mockito.times(2))
                .update(anyString(), anyInt(), anyString(), anyInt());
    }
}
