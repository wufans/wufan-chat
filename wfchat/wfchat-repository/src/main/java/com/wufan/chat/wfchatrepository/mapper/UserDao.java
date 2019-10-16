package com.wufan.chat.wfchatrepository.mapper;

import com.wufan.chat.wfchatcommon.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @Author: wufan
 * @Date: 2019/7/4 22:45
 */
@Repository
@Mapper
public interface UserDao {
    @Results(id = "UserMap",value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "email", column = "email"),
            @Result(property = "profile", column = "profile"),
            @Result(property = "sex", column = "sex")

    })
    @Select("SELECT * FROM user WHERE username = #{username}")
    User getByName(String username);

    @Insert("INSERT INTO user(username, password, email, profile,sex) VALUES (#{username}, #{password},#{email},#{profile},#{sex})")
    @ResultMap("UserMap")
    Integer insertUser(User user);

    @ResultMap("UserMap")
    @Update("UPDATE user SET password = #{password} where username = #{username}")
    Integer updatePwd(@Param("username")String username, @Param("password")String password);

    @ResultMap("UserMap")
    @Delete("Delete FROM user WHERE username = #{username}")
    Integer deleteUser(String username);



}
