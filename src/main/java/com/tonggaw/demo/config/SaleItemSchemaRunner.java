package com.tonggaw.demo.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SaleItemSchemaRunner implements CommandLineRunner {

    private static final List<String> EXPECTED_PRIMARY_KEY_COLUMNS = List.of(
            "sale_id",
            "sale_item_spu",
            "sale_item_sku"
    );
    private static final String SALE_ITEMS_PRIMARY_KEY_CONSTRAINT = "sale_items_pkey";

    private final JdbcTemplate jdbcTemplate;

    public SaleItemSchemaRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        if (!saleItemsTableExists()) {
            return;
        }

        List<String> primaryKeyColumns = getSaleItemsPrimaryKeyColumns();
        if (EXPECTED_PRIMARY_KEY_COLUMNS.equals(primaryKeyColumns)) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE sale_items DROP CONSTRAINT IF EXISTS " + SALE_ITEMS_PRIMARY_KEY_CONSTRAINT);
        jdbcTemplate.execute("ALTER TABLE sale_items ALTER COLUMN sale_id SET NOT NULL");
        jdbcTemplate.execute(
                "ALTER TABLE sale_items ADD CONSTRAINT " + SALE_ITEMS_PRIMARY_KEY_CONSTRAINT
                        + " PRIMARY KEY (sale_id, sale_item_spu, sale_item_sku)"
        );
    }

    private boolean saleItemsTableExists() {
        Integer tableCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = current_schema()
                  AND table_name = 'sale_items'
                """,
                Integer.class
        );

        return tableCount != null && tableCount > 0;
    }

    private List<String> getSaleItemsPrimaryKeyColumns() {
        return jdbcTemplate.queryForList(
                """
                SELECT kcu.column_name
                FROM information_schema.table_constraints tc
                JOIN information_schema.key_column_usage kcu
                  ON tc.constraint_name = kcu.constraint_name
                 AND tc.table_schema = kcu.table_schema
                WHERE tc.table_schema = current_schema()
                  AND tc.table_name = 'sale_items'
                  AND tc.constraint_type = 'PRIMARY KEY'
                ORDER BY ordinal_position
                """,
                String.class
        );
    }
}
