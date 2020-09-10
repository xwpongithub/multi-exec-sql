package cn.xwplay.demo.handler;

import cn.hutool.db.handler.BeanListHandler;
import cn.hutool.db.handler.RsHandler;
import cn.xwplay.demo.pojo.Classes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ClassesListHandler implements RsHandler<List<Classes>> {

  @Override
  public List<Classes> handle(ResultSet resultSet) throws SQLException {
    BeanListHandler<Classes> handler = new BeanListHandler<>(Classes.class);
    return handler.handle(resultSet);
  }

}
