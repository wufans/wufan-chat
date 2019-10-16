package com.wufan.chat.wfchatrepository.mapper;

import com.wufan.chat.wfchatcommon.model.FriendRequest;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: wufan
 * @Date: 2019/7/7 13:57
 */
@Repository
@Mapper
public interface FriendDao {
    /**
     * 查询自己发出的好友请求
     * @param fromusername
     * @return
     */
    @Results(id = "FriendRequestMap",
            value = {
            @Result(property = "requestid", column = "requestid"),
            @Result(property = "fromusername", column = "fromusername"),
            @Result(property = "tousername", column = "tousername"),
            @Result(property = "requesttime", column = "requesttime"),
            @Result(property = "isread", column = "isread"),
            @Result(property = "isaccept", column = "isaccept"),
            @Result(property = "latestmessageid", column = "latestmessageid")

    })
    @Select("SELECT * FROM friend WHERE fromusername = #{fromusername}")
    List<FriendRequest> getSendFriendRequestByName(String fromusername);

    /**
     * 查询好友列表
     * @param fromusername
     * @return
     */
    @ResultMap("FriendRequestMap")
    @Select("SELECT * FROM friend WHERE (fromusername = #{fromusername} or tousername = #{fromusername} ) and isaccept = 1 and isread != 10")
    List<FriendRequest> getTrueFriendByNama(String fromusername);



    //查询收到的好友请求列表
    @ResultMap("FriendRequestMap")
    @Select("SELECT * FROM friend WHERE tousername = #{tousername}")
    List<FriendRequest> getReceiveFriendRequestByName(String tousername);

    //回复好友请求
    @ResultMap("FriendRequestMap")
    @Update("UPDATE friend SET isread = 1, isaccept = #{isaccept} where requestid = #{requestid}")
    Integer replyFriendRequest(@Param("requestid")Long requestid, @Param("isaccept")int isaccept);

    //插入好友请求
    @ResultMap("FriendRequestMap")
    @Insert("INSERT INTO friend(fromusername, tousername, requesttime, isread, isaccept, latestmessageid) " +
            "VALUES (#{fromusername}, #{tousername},#{requesttime},#{isread},#{isaccept},#{latestmessageid})")
    Integer insertFriendRequest(FriendRequest friendRequest);

    //利用requestid查询好友请求
    @ResultMap("FriendRequestMap")
    @Select("SELECT * FROM friend WHERE requestid = #{requestid}")
    FriendRequest getNameByRequestId(Long requestid);

    //查找用户名对应的未处理的好友申请
    @ResultMap("FriendRequestMap")
    @Select("SELECT * FROM friend WHERE fromusername = #{fromusername} and tousername = #{tousername} and isread = 0")
    List<FriendRequest> getUnconfirmRequest(@Param("fromusername")String fromusername, @Param("tousername")String tousername);

    //根据ID删除好友
    @ResultMap("FriendRequestMap")
    @Delete("DELETE FROM friend WHERE requestid = #{requestid}")
    Integer deleteFriendRequest(Long requestid);

    //根据name删除好友
    @ResultMap("FriendRequestMap")
    @Delete("DELETE FROM friend WHERE tousername = #{tousername}")
    Integer deleteByName(String tousername);

    //根据name删除好友
    @ResultMap("FriendRequestMap")
    @Delete("DELETE FROM friend WHERE fromusername = #{fromusername} and tousername = #{tousername} and isread = 10")
    Integer exitGroup(@Param("fromusername")String fromusername, @Param("tousername")String tousername);

    //判断两个用户是否是好友
    @ResultMap("FriendRequestMap")
    @Select("SELECT * FROM friend WHERE " +
            "(fromusername = #{fromusername} and tousername = #{tousername} and isaccept = 1)" +
            "or"+
            "(fromusername = #{tousername} and tousername = #{fromusername} and isaccept = 1)")
    List<FriendRequest> isFriendOrNot(@Param("fromusername")String fromusername, @Param("tousername")String tousername);

    //判断好友请求是否处理过
    @ResultMap("FriendRequestMap")
    @Select("select * from friend where requestid = #{requestid}")
    FriendRequest isHandled(@Param("requestid")Long requestid);

    //查询群成员列表
    @ResultMap("FriendRequestMap")
    @Select("SELECT * FROM friend WHERE tousername = #{groupname} and isread = 10")
    List<FriendRequest> getMemberList(String groupname);

    //获取用户对应的Group列表
    @ResultMap("FriendRequestMap")
    @Select("SELECT * FROM friend WHERE fromusername = #{membername} and isread = 10 and isaccept = 1")
    List<FriendRequest> getUserGroup(String membername);

    //获取用户某个组对应的未读消息数 tousername 表示是groupname
    @ResultMap("FriendRequestMap")
    @Select("SELECT * FROM friend WHERE fromusername = #{fromusername} and tousername = #{tousername}")
    FriendRequest getGroupUnreadNumer(@Param("fromusername")String fromusername, @Param("tousername")String tousername);

    //将用户和群组的聊天记录设置为已读
    @ResultMap("FriendRequestMap")
    @Update("UPDATE friend SET latestmessageid = 0 where fromusername = #{fromusername} and tousername = #{tousername}")
    Integer setGroupUnreadNumber(@Param("fromusername")String fromusername, @Param("tousername")String tousername);

    //增加用户和群组的聊天记录
    @ResultMap("FriendRequestMap")
    @Update("UPDATE friend SET latestmessageid = latestmessageid +1 where fromusername = #{fromusername} and tousername = #{tousername}")
    Integer addGroupUnreadNumber(@Param("fromusername")String fromusername, @Param("tousername")String tousername);
}
