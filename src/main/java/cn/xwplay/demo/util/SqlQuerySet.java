package cn.xwplay.demo.util;

import cn.hutool.db.handler.RsHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
public class SqlQuerySet {

    private List<Object> params;
    private RsHandler<?> rsHandler;

}
