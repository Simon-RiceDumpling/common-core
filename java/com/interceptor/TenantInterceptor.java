package com.interceptor;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.apache.ibatis.reflection.SystemMetaObject.DEFAULT_OBJECT_FACTORY;
import static org.apache.ibatis.reflection.SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class TenantInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler,DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        // 获取 SQL 命令类型
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 仅拦截 SELECT 类型的语句
        if (sqlCommandType == SqlCommandType.SELECT) {
            BoundSql boundSql = statementHandler.getBoundSql();
            String originalSql = boundSql.getSql();

            try {
                Statement statement = CCJSqlParserUtil.parse(originalSql);
                if (statement instanceof Select) {
                    Select selectStatement = (Select) statement;
                    SelectBody selectBody = selectStatement.getSelectBody();

                    if (selectBody instanceof PlainSelect) {
                        PlainSelect plainSelect = (PlainSelect) selectBody;

                        // 获取所有的表名
                        List<Table> tables = getTables(plainSelect);

                        // 获取数据库连接
                        Connection connection = (Connection) invocation.getArgs()[0];
                        DatabaseMetaData metaData = connection.getMetaData();

                        // 遍历每张表，检查是否包含租户字段，并添加租户条件
                        for (Table table : tables) {
                            if (hasTenantColumn(metaData, table, "tenant_id")) {
                                // 构造租户条件
                                Column tenantColumn = new Column(table, "tenant_id");
                                EqualsTo tenantEquals = new EqualsTo();
                                tenantEquals.setLeftExpression(tenantColumn);
                                tenantEquals.setRightExpression(new net.sf.jsqlparser.expression.StringValue("'abc123'"));

                                // 添加租户条件到 WHERE 子句
                                Expression whereExpression = plainSelect.getWhere();
                                if (whereExpression == null) {
                                    plainSelect.setWhere(tenantEquals);
                                } else {
                                    plainSelect.setWhere(new AndExpression(whereExpression, tenantEquals));
                                }
                            }
                        }

                        // 更新 SQL 语句
                        String newSql = selectStatement.toString();
                        metaObject.setValue("delegate.boundSql.sql", newSql);
                    }
                }

            } catch (JSQLParserException | SQLException e) {
                e.printStackTrace();
            }
        }

        // 继续执行原始 SQL 查询
        return invocation.proceed();
    }

    // 获取所有表名
    private List<Table> getTables(PlainSelect plainSelect) {
        return plainSelect.getIntoTables();
    }

    // 检查表是否包含指定字段
    private boolean hasTenantColumn(DatabaseMetaData metaData, Table table, String columnName) throws SQLException {
        String tableName = table.getName();
        try (ResultSet rs = metaData.getColumns(null, null, tableName, columnName)) {
            return rs.next(); // 如果有结果，说明表中包含该字段
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以在这里初始化一些配置参数
    }
}
