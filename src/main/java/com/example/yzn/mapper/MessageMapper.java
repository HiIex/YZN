package com.example.yzn.mapper;

import com.example.yzn.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insertMessage(Message message);
    List<Message> selectMessage(String id);
    int insertFriend(FriendJson friendJson);
    List<FriendJson> selectFriend(String id);
    int deleteMessage(String toid);
    int updateFriendTime(@Param("id") String id,@Param("friendid")String friendid,@Param("time") String time);
    int insertApplication(FriendApply friendApply);
    int deleteApplication(String toid);
    List<FriendApply> selectApply(String toid);
    int insertConfirm(FriendApply friendApply);
    int deleteConfirm(@Param("fromid")String fromid,@Param("toid")String toid);
    List<FriendApply> selectConfirm(String fromid);
    int insertInfo(Info info);
    int upgradeInfo(@Param("id")String id,@Param("nickname")String nickname,@Param("sex") int sex,@Param("birthday")String birthday
            ,@Param("company")String company,@Param("job")String job,@Param("province")String province,@Param("city")String city);
    Info selectInfo(String id);
}
