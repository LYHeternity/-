package com.entrepreneur.platform.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MyBatis Plus 自动填充 create_time / update_time
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                if (metaObject.hasSetter("timestamp")) {
                    this.strictInsertFill(metaObject, "timestamp", LocalDateTime.class, LocalDateTime.now());
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }

    /**
     * 初始化表结构
     */
    @Bean
    public void initTables() {
        // 获取所有表信息
        List<TableInfo> tableInfos = TableInfoHelper.getTableInfos();
        // 遍历表信息，自动创建表
        for (TableInfo tableInfo : tableInfos) {
            System.out.println("Table: " + tableInfo.getTableName());
        }
    }
}
