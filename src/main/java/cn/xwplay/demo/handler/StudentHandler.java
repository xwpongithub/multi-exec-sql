package cn.xwplay.demo.handler;

import cn.hutool.db.handler.BeanHandler;
import cn.hutool.db.handler.RsHandler;
import cn.xwplay.demo.pojo.Student;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentHandler implements RsHandler<Student> {

  @Override
  public Student handle(ResultSet resultSet) throws SQLException {
    BeanHandler<Student> handler = new BeanHandler<>(Student.class);
    return handler.handle(resultSet);
  }

}
