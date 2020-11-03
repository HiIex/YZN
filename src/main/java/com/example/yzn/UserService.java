package com.example.yzn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    public UserMapper userMapper;

    public UserService() {
    }

    public int insert(User user){
        return userMapper.insert(user);
    }

    public int insertBill(BillDatabase billDatabase){
        return userMapper.insertBill(billDatabase);
    }


    public User select(String id){
        return userMapper.select(id);
    }

    public int update(User user){
        return userMapper.update(user);
    }

    public int delete(String id){
        return userMapper.delete(id);
    }

    public List<User> findAll(){
        return userMapper.findAll();
    }

    public User findByPhone(String phone){
        return userMapper.findByPhone(phone);
    }


    public List<BillDatabase> findAllBill(){
        return userMapper.findAllBill();
    }
}
