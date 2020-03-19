package com.github.smartretry.spring4;

import java.util.stream.Stream;

/**
 * @author yuni[mn960mn@163.com]
 *
 * @see JdbcRetryTaskMapper
 */
public enum DatabaseDriverEnum {

    SQLSERVER("Microsoft SQL Server", "sqlserver"),

    POSTGRESQL("PostgreSQL", "postgresql"),

    MYSQL("MySQL", "mysql");

    private final String productName;

    private final String driverClassName;

    DatabaseDriverEnum(String productName, String driverClassName) {
        this.productName = productName;
        this.driverClassName = driverClassName;
    }

    public static DatabaseDriverEnum fromProductName(String productName) {
        return Stream.of(DatabaseDriverEnum.values()).filter(dbd -> dbd.getProductName().equalsIgnoreCase(productName)).findAny().orElse(null);
    }

    public String getProductName() {
        return productName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }
}