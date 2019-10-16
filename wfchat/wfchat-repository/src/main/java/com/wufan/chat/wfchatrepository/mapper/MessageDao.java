package com.wufan.chat.wfchatrepository.mapper;

import com.wufan.chat.wfchatcommon.model.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: wufan
 * @Date: 2019/7/8 15:32
 */
@Repository
@Mapper
public interface MessageDao {
    //聊天记录： 某用户fromusername发送给tousername所有message
    @Results(id = "MessageMap",
            value = {
                    @Result(property = "messageid", column = "messageid"),
                    @Result(property = "fromusername", column = "fromusername"),
                    @Result(property = "tousername", column = "tousername"),
                    @Result(property = "message", column = "message"),
                    @Result(property = "sendtime", column = "sendtime"),
                    @Result(property = "isread", column = "isread"),
            })
    @Select("SELECT * FROM message WHERE " +
            "fromusername = #{fromusername} and tousername = #{tousername}" +
            "order by sendtime desc")
    List<Message> getSendMessage(@Param("fromusername")String fromusername, @Param("tousername") String tousername);


    //聊天记录：某用户收到tousername发来的所有message,包含未读message！
    @ResultMap("MessageMap")
    @Select("SELECT * FROM message WHERE " +
            "tousername = #{fromusername} and fromusername = #{tousername}" +
            "order by sendtime desc")
    List<Message> getReceiveMessage(@Param("fromusername")String fromusername, @Param("tousername") String tousername);


    //聊天记录： 两个用户之间的所有聊天记录
    @ResultMap("MessageMap")
    @Select("SELECT * FROM message WHERE " +
            "(tousername = #{fromusername} and fromusername = #{tousername})" +
            "or (fromusername = #{fromusername} and tousername = #{tousername})" +
            "order by sendtime desc")
    List<Message> getAllMessage(@Param("fromusername")String fromusername, @Param("tousername") String tousername);

    //分页查询聊天记录
    @ResultMap("MessageMap")
    @Select("SELECT * FROM message WHERE " +
            "(tousername = #{fromusername} and fromusername = #{tousername}) " +
            "or (fromusername = #{fromusername} and tousername = #{tousername}) " +
            "order by sendtime desc "+
            "limit #{page}, #{pagNum}")
    List<Message> getMessagesByPage(@Param("fromusername")String fromusername, @Param("tousername") String tousername,
                                    @Param("page") Integer page, @Param("pagNum")Integer pagNum);

    //分页查询Group聊天记录
    @ResultMap("MessageMap")
    @Select("SELECT * FROM message WHERE " +
            "tousername = #{tousername} " +
            "order by sendtime desc "+
            "limit #{page}, #{pagNum}")
    List<Message> getGroupMessagesByPage(@Param("tousername") String tousername,
                                    @Param("page") Integer page, @Param("pagNum")Integer pagNum);

    //插入一条message
    @ResultMap("MessageMap")
    @Insert("INSERT INTO message(fromusername, tousername, message, sendtime, isread) " +
            "VALUES (#{fromusername}, #{tousername},#{message},#{sendtime},#{isread})")
    Integer insertMessage(Message message);

    //将某一条消息变为已读
    @Deprecated
    @ResultMap("MessageMap")
    @Update("UPDATE message SET isread = 1 where messageid = #{messageid}")
    Integer setRequestRead(Long messageid);

    //获取未读消息的数量
    @ResultMap("MessageMap")
    @Select("SELECT * FROM message WHERE " +
            "(tousername = #{fromusername} and fromusername = #{tousername} and isread = 0)")
    List<Message> getUnreadMessageNumber(@Param("fromusername")String fromusername, @Param("tousername") String tousername);

    //获取某用户的最近所有聊天消息对象
    @ResultMap("MessageMap")
    @Select("select * from " +
            "(select messageid, fromusername, tousername, sendtime, message, isread from message where tousername = #{fromusername} " +
            "union " +
            "select messageid, tousername, tousername, sendtime, message, isread from message where fromusername = #{fromusername} " +
            "order by sendtime desc) as t " +
            "group by fromusername")
    List<Message> getRecentChatName(String fromusername);

    //将用户 fromusername 收到的来自 tousername 的消息置为已读
    @ResultMap("MessageMap")
    @Update("UPDATE message SET isread = 1 where tousername = #{fromusername} and fromusername = #{tousername} and isread = 0")
    Integer setMessagesRead(@Param("fromusername")String fromusername, @Param("tousername") String tousername);

    //删除聊天记录
    @ResultMap("MessageMap")
    @Delete("DELETE FROM message WHERE"+
            "(tousername = #{fromusername} and fromusername = #{tousername}) " +
            "or (fromusername = #{fromusername} and tousername = #{tousername})")
    Integer deleteMessages(@Param("fromusername")String fromusername, @Param("tousername") String tousername);

    //删除聊天记录
    @ResultMap("MessageMap")
    @Delete("DELETE FROM message WHERE tousername = #{tousername}")
    Integer deleteGroupMessages(String tousername);

    //获取某个group的最近一次聊天记录
    @ResultMap("MessageMap")
    @Select("SELECT * FROM message WHERE " +
            "tousername = #{groupname}" +
            "order by sendtime desc limit 1")
    Message getRecentMessageOfGroup(String groupname);


}
