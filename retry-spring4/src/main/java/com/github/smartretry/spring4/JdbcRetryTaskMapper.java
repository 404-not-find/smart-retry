package com.github.smartretry.spring4;

import com.github.smartretry.core.RetryTask;
import com.github.smartretry.core.RetryTaskMapper;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

/**
 * @author yuni[mn960mn@163.com]
 */
public class JdbcRetryTaskMapper extends JdbcTemplate implements RetryTaskMapper, EnvironmentAware {

    public static final String INSERT_SQL_KEY = "INSERT_SQL";

    public static final String UPDATE_SQL_KEY = "UPDATE_SQL";

    public static final String QUERY_NEEDRETRYTASK_LIST_SQL_KEY = "QUERY_NEEDRETRYTASK_LIST_SQL";

    public static final String PRIMARY_KEY_KEY = "PRIMARY_KEY";

    private String primaryKeyKey;

    private Properties sqlMappingProperties;

    private Environment environment;

    public JdbcRetryTaskMapper(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void initSqlMappingProperties() {
        this.sqlMappingProperties = getSqlMappingProperties();
        this.primaryKeyKey = sqlMappingProperties.getProperty(PRIMARY_KEY_KEY);
    }

    private Properties getSqlMappingProperties() {
        String filepath;
        if (environment.containsProperty(EnvironmentConstants.RETRY_SQLMAPPING_FILEPATH_KEY)) {
            filepath = environment.getProperty(EnvironmentConstants.RETRY_SQLMAPPING_FILEPATH_KEY);
        } else {
            try (Connection conn = this.getDataSource().getConnection()) {
                String databaseProductName = conn.getMetaData().getDatabaseProductName();
                DatabaseDriverEnum databaseDriverEnum = DatabaseDriverEnum.fromProductName(databaseProductName);
                if (databaseDriverEnum == null) {
                    throw new IllegalArgumentException("无法根据数据库的databaseProductName=" + databaseProductName + "判断数据库类型，请在配置文件中使用retry.sqlMapping.filepath配置");
                }
                filepath = "META-INF/sqlprops/" + databaseDriverEnum.getDriverClassName() + ".properties";
            } catch (SQLException e) {
                throw new IllegalArgumentException("无法获取数据库连接", e);
            }
        }
        Properties properties = new Properties();
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(filepath)) {
            if (input == null) {
                throw new IllegalArgumentException("系统SQL不存在：" + filepath + "，请检查retry.sqlMapping.filepath配置的文件是否存在");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return properties;
    }

    @Override
    public int insert(RetryTask retryTask) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = conn -> {
            String sql = sqlMappingProperties.getProperty(INSERT_SQL_KEY);
            PreparedStatement ps = conn.prepareStatement(sql, new String[]{primaryKeyKey});
            ps.setString(1, retryTask.getIdentity());
            ps.setString(2, retryTask.getParams());
            ps.setInt(3, RetryTask.STATUS_INIT);
            ps.setString(4, retryTask.getRemark());
            return ps;
        };
        int rows = update(psc, keyHolder);
        retryTask.setTaskId(keyHolder.getKey().longValue());
        return rows;
    }

    @Override
    public int update(RetryTask retryTask) {
        String sql = sqlMappingProperties.getProperty(UPDATE_SQL_KEY);
        return this.update(sql, ps -> {
            ps.setInt(1, retryTask.getStatus());
            ps.setInt(2, retryTask.getRetryCount());
            ps.setString(3, retryTask.getRemark());
            ps.setLong(4, retryTask.getTaskId());
        });
    }

    @Override
    public List<RetryTask> queryNeedRetryTaskList(String identity, int retryCount, int initialDelay) {
        String sql = sqlMappingProperties.getProperty(QUERY_NEEDRETRYTASK_LIST_SQL_KEY);
        return this.query(sql, ps -> {
            ps.setString(1, identity);
            ps.setInt(2, retryCount);
            ps.setObject(3, LocalDateTime.now().minusSeconds(initialDelay));
        }, (rs, rowNum) -> {
            RetryTask task = new RetryTask();
            task.setTaskId(rs.getLong("TaskId"));
            task.setIdentity(rs.getString("IdentityName"));
            task.setParams(rs.getString("Params"));
            task.setStatus(rs.getInt("Status"));
            task.setRemark(rs.getString("Remark"));
            task.setRetryCount(rs.getInt("RetryCount"));
            task.setCreateDate(rs.getObject("CreateDate", LocalDateTime.class));
            return task;
        });
    }
}
