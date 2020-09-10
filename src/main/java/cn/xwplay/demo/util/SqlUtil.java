package cn.xwplay.demo.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SqlUtil {

    private Connection connection;

    private static final int EXEC_BATCH_SUCCESS_INFO = -2;

    public List<Object> execMultiQuery(List<Map<String, RsHandler<?>>> sqlList) {
      List<Object> rs = new ArrayList<>(sqlList.size());
      try {
           initConnection(true);
           connection.setReadOnly(true);
           Statement statement = connection.createStatement();
           for (Map<String,RsHandler<?>> sqlMap: sqlList) {
               for (String sql : sqlMap.keySet()) {
                   RsHandler<?> rsHandler = sqlMap.get(sql);
                   log.debug("【execMultiQuery：{}】",sql);
                   ResultSet resultSet = statement.executeQuery(sql);
                   rs.add(rsHandler.handle(resultSet));
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       } finally {
           DbUtil.close(connection);
       }
       return rs;
    }

    public List<Object> execMultiQueryWithParameters(List<Map<String,SqlQuerySet>> sqlList) {
        List<Object> rs = new ArrayList<>(sqlList.size());
        try {
            initConnection(true);
            connection.setReadOnly(true);
            for (Map<String, SqlQuerySet> sqlMap : sqlList) {
                for (String sql : sqlMap.keySet()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    SqlQuerySet querySet = sqlMap.get(sql);
                    List<Object> params = querySet.getParams();
                    RsHandler<?> rsHandler =querySet.getRsHandler();
                    log.debug("【execMultiQueryWithParameters：{},执行参数：{}】",sql,params);
                    for (int j = 0; j < params.size(); j++) {
                        setSqlParam(params.get(j), preparedStatement, j);
                    }
                    ResultSet resultSet = preparedStatement.executeQuery();
                    rs.add(rsHandler.handle(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(connection);
        }
        return rs;
    }

    public List<Object> execMultiManipulation(List<String> sqlList) {
        int affectCount = 0;
        List<Object> countResult = new ArrayList<>(2);
        try {
            int[] rowsArray = new int[sqlList.size()];
            initConnection(false);
            Statement statement = connection.createStatement();
            for (int i = 0; i < sqlList.size(); i++) {
                log.info("【execMultiManipulation：{}】",sqlList.get(i));
                rowsArray[i] = statement.executeUpdate(sqlList.get(i));
                affectCount+=rowsArray[i];
            }
            countResult.add(affectCount);
            countResult.add(rowsArray);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            DbUtil.close(connection);
        }
        return countResult;
    }

    public List<Object> execMultiManipulationWithParameters(List<Map<String,List<Object>>> sqlList) {
        int affectCount = 0;
        List<Object> countResult = new ArrayList<>(2);
        try {
            int[] rowsArray = new int[sqlList.size()];
            initConnection(false);
            for (int i = 0; i < sqlList.size(); i++) {
                Map<String, List<Object>> sqlMap = sqlList.get(i);
                for (String sql : sqlMap.keySet()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    List<Object> params = sqlMap.get(sql);
                    log.debug("【execMultiManipulationWithParameters：{},执行参数：{}】",sql,params);
                    for (int j = 0; j < params.size(); j++) {
                        setSqlParam(params.get(j), preparedStatement, j);
                    }
                    rowsArray[i] = preparedStatement.executeUpdate();
                    affectCount+=rowsArray[i];
                }
            }
            countResult.add(affectCount);
            countResult.add(rowsArray);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            DbUtil.close(connection);
        }
        return countResult;
    }

    public boolean execBatch(String sql,List<List<Object>> paramList) {
        boolean isSuccess = true;
        try {
            initConnection(false);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            read_data:
            for (int i = 0; i < paramList.size(); i++) {
                List<Object> params = paramList.get(i);
                log.debug("【execMultiManipulationWithParameters：{},执行参数：{}】",sql,params);
                for (int j = 0; j < params.size(); j++) {
                    setSqlParam(params.get(j), preparedStatement, j);
                }
                preparedStatement.addBatch();
                // 每500条执行一次，避免内存不够的情况，可参考，Eclipse设置JVM的内存参数
                if (i > 0 && i % 500 == 0) {
                    int[] batch500 = preparedStatement.executeBatch();
                    for (int i1 : batch500) {
                        if (i1 != EXEC_BATCH_SUCCESS_INFO) {
                            isSuccess = false;
                            break read_data;
                        }
                    }
                }
            }
            int[] countList = preparedStatement.executeBatch();
            for (int i : countList) {
                if (i != EXEC_BATCH_SUCCESS_INFO) {
                    isSuccess = false;
                    break;
                }
            }
            log.debug("【executeBatch：{},执行结果:{}】",sql,isSuccess);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            isSuccess=false;
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            DbUtil.close(connection);
        }
        return isSuccess;
    }

    public List<Object> execMultiWithParameters(List<Map<String,List<Object>>> sqlList) {
        List<Object> objectList = new ArrayList<>(sqlList.size());
        try {
            initConnection(false);
            for (Map<String, List<Object>> sqlMap : sqlList) {
                for (String sql : sqlMap.keySet()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    List<Object> params = sqlMap.get(sql);
                    log.debug("【execMultiWithParameters：{}】",sql);
                    for (int j = 0; j < params.size(); j++) {
                        setSqlParam(params.get(j), preparedStatement, j);
                    }
                    boolean flag = preparedStatement.execute();
                    // dql
                    if (flag) {
                        objectList.add(preparedStatement.getResultSet());
                        // ddl dml
                    } else {
                        int updateCount = preparedStatement.getUpdateCount();
                        // 大于0为dml语句，等于0为ddl语句
                        objectList.add(Math.max(updateCount, 0));
                    }
                }
            }
            log.debug("【execMultiWithParameters：执行结果：{}】",objectList);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            DbUtil.close(connection);
        }
        return objectList;
    }

    private void initConnection(boolean autoCommit) throws SQLException {
      connection = SpringUtil.getBean(DataSource.class).getConnection();
      connection.setAutoCommit(autoCommit);
    }

    private void setSqlParam(Object oPram, PreparedStatement statement, int count) throws SQLException {
        int parameterIndex = count+1;
        if ((oPram instanceof byte[])) {
            ByteArrayInputStream isParam = new ByteArrayInputStream(
                    (byte[]) oPram);
            statement.setBinaryStream(parameterIndex, isParam,
                    ((byte[]) oPram).length);
        } else if ((oPram instanceof Long)) {
            statement.setLong(parameterIndex, (Long) oPram);
        } else if ((oPram instanceof Boolean)) {
            statement.setBoolean(parameterIndex, (Boolean) oPram);
        } else if ((oPram instanceof String)) {
            statement.setString(parameterIndex, (String) oPram);
        } else if ((oPram instanceof BigDecimal)) {
            statement.setBigDecimal(parameterIndex, (BigDecimal) oPram);
        } else if ((oPram instanceof Date)) {
            statement.setDate(parameterIndex, (Date) oPram);
        } else if ((oPram instanceof Time)) {
            statement.setTime(parameterIndex, (Time) oPram);
        } else if ((oPram instanceof Timestamp)) {
            statement.setTimestamp(parameterIndex, (Timestamp) oPram);
        } else if ((oPram instanceof Double)) {
            statement.setDouble(parameterIndex, (Double) oPram);
        } else if ((oPram instanceof Integer)) {
            statement.setInt(parameterIndex, (Integer) oPram);
        } else if ((oPram instanceof Float)) {
            statement.setFloat(parameterIndex, (Float) oPram);
        } else if ((oPram != null)) {
            statement.setObject(parameterIndex, oPram);
        } else {
            throw new SQLException();
        }
    }

}
