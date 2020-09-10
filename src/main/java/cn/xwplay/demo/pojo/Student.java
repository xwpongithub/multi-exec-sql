package cn.xwplay.demo.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Student {

    private Long id;
    private String name;
    private Date birthday;
    private String userNo;
    private Long classId;

}
