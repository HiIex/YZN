package com.example.yzn.service;

import com.example.yzn.entity.*;
import com.example.yzn.mapper.MessageMapper;
import com.example.yzn.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    public MessageMapper messageMapper;

    public MessageService(){}

    public int insertMessage(Message message){
        return messageMapper.insertMessage(message);
    }

    public List<Message> selectMessage(String id){
        return messageMapper.selectMessage(id);
    }

    public int insertFriend(FriendJson friendJson){
        return messageMapper.insertFriend(friendJson);
    }

    public List<FriendJson> selectFriend(String id){
        return messageMapper.selectFriend(id);
    }

    public int deleteMessage(String toid){
        return messageMapper.deleteMessage(toid);
    }

    public int updateFriendTime(String id,String friendid,String time){
        return messageMapper.updateFriendTime(id,friendid,time);
    }

    public List<FriendApply> selectApply(String toid){
        return messageMapper.selectApply(toid);
    }

    public int insertApplication(FriendApply friendApply){
        return messageMapper.insertApplication(friendApply);
    }

    public int deleteApplication(String toid){
        return messageMapper.deleteApplication(toid);
    }

    public List<FriendApply> getConfirm(String fromid){
        return messageMapper.selectConfirm(fromid);
    }

    public int insertConfirm(FriendApply friendApply){
        return messageMapper.insertConfirm(friendApply);
    }

    public int deleteConfirm(String fromid,String toid){
        return messageMapper.deleteConfirm(fromid,toid);
    }

    public int insertInfo(Info info){
        return messageMapper.insertInfo(info);
    }

    public Info selectInfo(String id){
        return messageMapper.selectInfo(id);
    }

    public int upgradeInfo(Info info){
        return messageMapper.upgradeInfo(info.getId(),info.getNickname(),info.getSex(),info.getBirthday(),info.getCompany(),info.getJob(),info.getProvince(),info.getCity());
    }





}
