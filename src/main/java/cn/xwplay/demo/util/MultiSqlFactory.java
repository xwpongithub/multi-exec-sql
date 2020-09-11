package cn.xwplay.demo.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public enum MultiSqlFactory {

    DB;

    private SqlUtil sqlUtil;

    MultiSqlFactory() {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl("jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai");
      config.setUsername("root");
      config.setPassword("123456");
      config.setDriverClassName("com.mysql.cj.jdbc.Driver");
      HikariDataSource ds = new HikariDataSource(config);
      sqlUtil = new SqlUtil(ds);
    }

    public SqlUtil getInstance() {
        return sqlUtil;
    }

}
