package cn.xwplay.demo.handler;

import cn.hutool.db.handler.BeanHandler;
import cn.hutool.db.handler.RsHandler;
import cn.xwplay.demo.pojo.Classes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassesHandler implements RsHandler<Classes> {

  @Override
  public Classes handle(ResultSet resultSet) throws SQLException {
    BeanHandler<Classes> handler = new BeanHandler<>(Classes.class);
    return handler.handle(resultSet);
  }

}
