package com.example.yzn.mapper;

import com.example.yzn.entity.BillDatabase;
import com.example.yzn.entity.HeadDatabase;
import com.example.yzn.entity.RSAKey;
import com.example.yzn.entity.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface UserMapper {
    int insert (User user);
    User select (String id);
    User findByPhone(String phone);
    int update (User user);
    int delete(String id);
    List<User> findAll();
    int insertBill(BillDatabase billDatabase);
    List<BillDatabase> findAllBill();
    int insertRSAKey(RSAKey rsaKey);
    RSAKey findKeyByID(String id);

    int insertHead(HeadDatabase headDatabase);
    HeadDatabase findHeadByID(String id);

}
