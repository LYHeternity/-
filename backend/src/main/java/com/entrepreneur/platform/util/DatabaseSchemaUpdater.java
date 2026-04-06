package com.entrepreneur.platform.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSchemaUpdater implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        updateResourceTable();
    }

    private void updateResourceTable() {
        try {
            List<String> columns = jdbcTemplate.queryForList("SHOW COLUMNS FROM resource LIKE 'service_process'", String.class);
            if (columns.isEmpty()) {
                jdbcTemplate.execute("ALTER TABLE resource ADD COLUMN service_process TEXT COMMENT '服务流程，JSON格式'");
                System.out.println("Added service_process column to resource table");
            }
        } catch (Exception e) {
            System.out.println("service_process column may already exist: " + e.getMessage());
        }

        try {
            List<String> columns = jdbcTemplate.queryForList("SHOW COLUMNS FROM resource LIKE 'delivery_standards'", String.class);
            if (columns.isEmpty()) {
                jdbcTemplate.execute("ALTER TABLE resource ADD COLUMN delivery_standards TEXT COMMENT '交付标准，JSON格式'");
                System.out.println("Added delivery_standards column to resource table");
            }
        } catch (Exception e) {
            System.out.println("delivery_standards column may already exist: " + e.getMessage());
        }

        try {
            List<String> columns = jdbcTemplate.queryForList("SHOW COLUMNS FROM resource LIKE 'service_highlights'", String.class);
            if (columns.isEmpty()) {
                jdbcTemplate.execute("ALTER TABLE resource ADD COLUMN service_highlights TEXT COMMENT '服务亮点，JSON格式'");
                System.out.println("Added service_highlights column to resource table");
            }
        } catch (Exception e) {
            System.out.println("service_highlights column may already exist: " + e.getMessage());
        }
    }
}
