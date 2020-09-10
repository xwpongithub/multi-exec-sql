package cn.xwplay.demo.handler;

import cn.hutool.db.handler.BeanListHandler;
import cn.hutool.db.handler.RsHandler;
import cn.xwplay.demo.pojo.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StudentListHandler implements RsHandler<List<Student>> {

  @Override
  public List<Student> handle(ResultSet resultSet) throws SQLException {
    BeanListHandler<Student> handler = new BeanListHandler<>(Student.class);
    return handler.handle(resultSet);
  }

}
