package com.weshare.batch.mapper;

import com.weshare.batch.entity.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.mapper
 * @date: 2021-05-22 16:04:10
 * @describe:
 */
@Mapper
public interface PersonMapper {
    void addPerson(Person person);

    @Select("select batch_date batchDate,create_date createDate,status from tb_person")
    List<Person> selectAllPerson();

}
