package clientlogin;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class Dbutil {

    private static Dbutil dbUtil;
    private ComboPooledDataSource connectionPool = new ComboPooledDataSource();

    private Dbutil() throws PropertyVetoException {
        this.connectionPool.setDriverClass("com.mysql.jdbc.Driver");
        this.connectionPool.setJdbcUrl("jdbc:mysql://localhost:3306/biblioteka");
        this.connectionPool.setUser("root");
        this.connectionPool.setPassword("Server123");
        this.connectionPool.setInitialPoolSize(5);
        this.connectionPool.setMinPoolSize(5);
        this.connectionPool.setMaxPoolSize(20);
        this.connectionPool.setAcquireIncrement(5);
        this.connectionPool.setMaxIdleTime(3600);
    }

    public Connection getConnection() throws SQLException {
        return this.connectionPool.getConnection();
    }

    public void close() {
        this.connectionPool.close();
    }

    public static Dbutil getInstance() {
        if (dbUtil == null) {
            try {
                dbUtil = new Dbutil();
            } catch (PropertyVetoException var1) {
                var1.printStackTrace();
            }
        }

        return dbUtil;
    }
}
