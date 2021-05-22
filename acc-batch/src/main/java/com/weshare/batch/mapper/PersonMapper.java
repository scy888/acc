package com.weshare.batch.mapper;

import com.weshare.batch.entity.Person;
import org.apache.ibatis.annotations.Mapper;


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
}
