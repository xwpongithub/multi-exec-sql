package cn.xwplay.demo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.db.handler.NumberHandler;
import cn.hutool.db.handler.RsHandler;
import cn.xwplay.demo.handler.ClassesHandler;
import cn.xwplay.demo.handler.ClassesListHandler;
import cn.xwplay.demo.handler.StudentHandler;
import cn.xwplay.demo.handler.StudentListHandler;
import cn.xwplay.demo.pojo.Classes;
import cn.xwplay.demo.pojo.Student;
import cn.xwplay.demo.util.MultiSqlFactory;
import cn.xwplay.demo.util.SqlQuerySet;
import cn.xwplay.demo.util.SqlUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestCases {

    private SqlUtil sqlUtil;

    @Before
    public void before() {
        sqlUtil = MultiSqlFactory.DB.getInstance();
    }

    // 执行多个不同的insert或update语句（参数不编译）
    @Test
    public void testExecMultiManipulation() {
        List<String> sqlList = CollUtil.newArrayList();
        sqlList.add("insert into classes(id,name,class_no) values (3,'文科1班','W20200701')");
        sqlList.add("insert into student(id,name,birthday,user_no,class_id) values (4,'尹航','1994-06-12','2020070301',3)");
        sqlList.add("update student set name = '朱潮' where id = 4");
        List<Object> rs = sqlUtil.execMultiManipulation(sqlList);
        Integer count = (Integer)rs.get(0);
        int[] rowArray = (int[])rs.get(1);
        System.out.println("影响总行数："+count);
        System.out.println("每条sql语句影响的行数："+Arrays.toString(rowArray));
    }

    // 执行多个不同的insert或update语句（参数编译）
    @Test
    public void testExecMultiManipulationWithParameters() {
        Map<String,List<Object>> sql1 = MapUtil.newHashMap(1);
        sql1.put("insert into classes(id,name,class_no) values (?,?,?)",CollUtil.newArrayList(3,"文科1班","W20200701"));
        Map<String,List<Object>> sql2 = MapUtil.newHashMap(1);
        sql2.put("insert into student(id,name,birthday,user_no,class_id) values (?,?,?,?,?)",CollUtil.newArrayList(4,"尹航", DateUtil.date(),"2020070301",3));
        Map<String,List<Object>> sql3 = MapUtil.newHashMap(1);
        sql3.put("update student set name = ? where id = ?",CollUtil.newArrayList("朱潮",4));
        Map<String,List<Object>> sql4 = MapUtil.newHashMap(1);
        sql4.put("insert into student(id,name,birthday,user_no,class_id) values (?,?,?,?,?)",CollUtil.newArrayList(5,"尹航", "1992-09-16","2020070302",3));
        List<Map<String,List<Object>>> sqlList = CollUtil.newArrayList(sql1,sql2,sql3,sql4);
        List<Object> rs = sqlUtil.execMultiManipulationWithParameters(sqlList);
        Integer count = (Integer)rs.get(0);
        int[] rowArray = (int[])rs.get(1);
        System.out.println("影响总行数："+count);
        System.out.println("每条sql语句影响的行数："+Arrays.toString(rowArray));
    }

    // 执行多个select语句（参数不编译）
    @Test
    public void testExecMultiQuery() {
        String sql1 = "select * from classes";
        Map<String, RsHandler<?>> sqlMap1 = MapUtil.newHashMap(1);
        sqlMap1.put(sql1,new ClassesListHandler());

        String sql2 = "select * from student";
        Map<String, RsHandler<?>> sqlMap2 = MapUtil.newHashMap(1);
        sqlMap2.put(sql2,new StudentListHandler());

        String sql3 = "select count(id) from classes";
        Map<String, RsHandler<?>> sqlMap3 = MapUtil.newHashMap(1);
        sqlMap3.put(sql3,new NumberHandler());

        String sql4 = "select count(id) from student";
        Map<String, RsHandler<?>> sqlMap4 = MapUtil.newHashMap(1);
        sqlMap4.put(sql4,new NumberHandler());

        String sql5 = "select id,name,class_no from classes";
        Map<String, RsHandler<?>> sqlMap5 = MapUtil.newHashMap(1);
        sqlMap5.put(sql5,new ClassesHandler());

        List<Map<String, RsHandler<?>>> sqlList = CollUtil.newArrayList(sqlMap1,sqlMap2,sqlMap3,sqlMap4,sqlMap5);
        List<Object> resultList = sqlUtil.execMultiQuery(sqlList);

        List<Classes> classesList = (List<Classes>)resultList.get(0);
        System.out.println(classesList);
        List<Student> studentList = (List<Student>)resultList.get(1);
        System.out.println(studentList);
        Number number1 = (Number)resultList.get(2);
        System.out.println(number1);
        Number number2 = (Number)resultList.get(3);
        System.out.println(number2);
        Classes classes = (Classes) resultList.get(4);
        System.out.println(classes);
    }

    // 执行多个select语句（参数编译）
    @Test
    public void testExecMultiQueryWithParameters() {
        String sql1 = "select id,name,birthday,user_no,class_id from student where birthday < ?";
        Map<String, SqlQuerySet> sqlMap1 = MapUtil.newHashMap(1);
        SqlQuerySet querySet1 = new SqlQuerySet();
        querySet1.setParams(CollUtil.newArrayList(DateUtil.date()));
        querySet1.setRsHandler(new StudentListHandler());
        sqlMap1.put(sql1,querySet1);
        String sql2 = "select id,name,birthday,user_no,class_id from student where id = ? and birthday < ?";
        Map<String, SqlQuerySet> sqlMap2 = MapUtil.newHashMap(1);
        SqlQuerySet querySet2 = new SqlQuerySet();
        querySet2.setParams(CollUtil.newArrayList(2,DateUtil.date()));
        querySet2.setRsHandler(new StudentHandler());
        sqlMap2.put(sql2,querySet2);
        String sql3 = "select count(id) from student where id = ? and birthday < ?";
        Map<String, SqlQuerySet> sqlMap3 = MapUtil.newHashMap(1);
        SqlQuerySet querySet3 = new SqlQuerySet();
        querySet3.setParams(CollUtil.newArrayList(2,DateUtil.date()));
        querySet3.setRsHandler(new NumberHandler());
        sqlMap3.put(sql3,querySet3);
        List<Map<String,SqlQuerySet>> queryMap = CollUtil.newArrayList(sqlMap1,sqlMap2,sqlMap3);
        List<Object> resultList = sqlUtil.execMultiQueryWithParameters(queryMap);

        List<Student> studentList = (List<Student>)resultList.get(0);
        System.out.println(studentList);
        Student student = (Student) resultList.get(1);
        System.out.println(student);
        Number number = (Number)resultList.get(2);
        System.out.println(number);
    }

    // 批量执行同一个insert或update数据（参数编译）
    @Test
    public void testExecBatchInsert() {
        String insertSql = "insert into student (id,name,birthday,user_no,class_id) values (?,?,?,?,?)";
        List<Object> params1 = CollUtil.newArrayList(4,"尹航","1995-07-16","2020070301",3);
        List<Object> params2 = CollUtil.newArrayList(5,"谢靖宇",DateUtil.date(),"2020070302",3);
        List<Object> params3 = CollUtil.newArrayList(6,"江芸",DateUtil.date(),"2020070303",3);
        List<Object> params4 = CollUtil.newArrayList(7,"王杰","1992-08-14","2020070304",3);
        List<List<Object>> paramList = CollUtil.newArrayList();
        paramList.add(params1);
        paramList.add(params2);
        paramList.add(params3);
        paramList.add(params4);

        boolean success =sqlUtil.execBatch(insertSql,paramList);
        System.out.println("执行结果："+success);
    }

    // 执行多个select，insert或update语句（参数不编译）
    @Test
    public void testExecMulti() {
         String insertSql = "insert into student(id,name,birthday,user_no,class_id) values (4,'尹航','1994-06-12','2020070301',3)";
         String createTableSql = "create table course(id bigint,name varchar(16),primary key(id))";
         String studentSql = "select * from student";
         Map<String,SqlQuerySet> sqlMap1 = MapUtil.newHashMap(1);
         sqlMap1.put(insertSql,null);
         Map<String,SqlQuerySet> sqlMap2 = MapUtil.newHashMap(1);
         sqlMap2.put(createTableSql,null);
         Map<String,SqlQuerySet> sqlMap3 = MapUtil.newHashMap(1);
         SqlQuerySet sqlQuerySet3 = new SqlQuerySet();
         sqlQuerySet3.setRsHandler(new StudentListHandler());
         sqlMap3.put(studentSql,sqlQuerySet3);
         List<Map<String,SqlQuerySet>> queryMap = CollUtil.newArrayList(sqlMap1,sqlMap2,sqlMap3);

         List<Object> rsList = sqlUtil.execMulti(queryMap);
         int insertCount = (int)rsList.get(0);
         int createCount = (int)rsList.get(1);
         List<Student> studentList= (List<Student>) rsList.get(2);
         System.out.println(insertCount);
         System.out.println(createCount);
         System.out.println(studentList);
    }

    // 执行多个select，insert或update语句（参数编译）
    @Test
    public void testExecMultiWithParameters() {
        String insertSql = "insert into student(id,name,birthday,user_no,class_id) values (?,?,?,?,?)";
        String studentSql = "select * from student where id=?";
        Map<String,SqlQuerySet> sqlMap1 = MapUtil.newHashMap(1);
        SqlQuerySet sqlQuerySet1 = new SqlQuerySet();
        sqlQuerySet1.setParams(CollUtil.newArrayList(4,"尹航","1994-06-12","2020070301",3));
        sqlMap1.put(insertSql,sqlQuerySet1);

        Map<String,SqlQuerySet> sqlMap2 = MapUtil.newHashMap(1);
        SqlQuerySet sqlQuerySet2 = new SqlQuerySet();
        sqlQuerySet2.setRsHandler(new StudentHandler());
        sqlQuerySet2.setParams(CollUtil.newArrayList(1));
        sqlMap2.put(studentSql,sqlQuerySet2);
        List<Map<String,SqlQuerySet>> queryMap = CollUtil.newArrayList(sqlMap1,sqlMap2);

        List<Object> rsList = sqlUtil.execMultiWithParameters(queryMap);
        int insertCount = (int)rsList.get(0);
        Student student= (Student) rsList.get(1);
        System.out.println(insertCount);
        System.out.println(student);
    }
}
