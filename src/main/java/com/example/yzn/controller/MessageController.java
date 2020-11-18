package com.example.yzn.controller;

import com.example.yzn.entity.*;
import com.example.yzn.security.AESCipher;
import com.example.yzn.security.Config;
import com.example.yzn.service.MessageService;
import com.example.yzn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    public MessageService messageService;

    @Autowired
    public UserService userService;

    public static final String INIT_REMARK="你们已经是好友啦~";

    //向消息缓存数据库发消息
    @RequestMapping("/post")
    public String postMessage(@RequestBody Message message){
        if(message.getFromid()!=null&&message.getToid()!=null&&message.getContent()!=null&&message.getTime()!=null){
            try{
                messageService.insertMessage(message);
                //更新friend中的时间
                messageService.updateFriendTime(message.getToid(),message.getFromid(),message.getTime());
                messageService.updateFriendTime(message.getFromid(),message.getToid(),message.getTime());
                log(message.getFromid()+" to "+message.getToid(),"message post success");
                return "message post success";
            }catch (Exception e){
                e.printStackTrace();
                log("message post fail");
                return "message insert error";
            }
        }else{
            log("message nullPoint error!");
            return "message nullPoint error";
        }
    }

    //向消息缓存数据库获得消息,并从缓存中删除
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    public List<Message> getMessage(String id){
        List<Message> messageList=new ArrayList<>();
        try{
            messageList=messageService.selectMessage(id);
            messageService.deleteMessage(id);
            log(id,"message get complete");
            return messageList;
        }catch (Exception e){
            e.printStackTrace();
            log(id,"message get fail");
            return null;
        }
    }

    //同意好友添加好友记录
    @RequestMapping(value = "/friend/post",method = RequestMethod.POST)
    public String postFriend(@RequestBody FriendJson friendJson){
        //加好友插入两条数据库
        try{

            String cypher_nickname=null;
            if(friendJson.getNickname()!=null){
                cypher_nickname=AESCipher.encrypt(Config.AES_KEY.getBytes(),friendJson.getNickname());
            }
            String cypher_remark=null;
            if(friendJson.getRemark()!=null){
                cypher_remark=AESCipher.encrypt(Config.AES_KEY.getBytes(),friendJson.getRemark());
            }else{
                cypher_remark=AESCipher.encrypt(Config.AES_KEY.getBytes(),INIT_REMARK);
            }
            String cypher_time=null;
            if(friendJson.getTime()!=null){
                cypher_time=AESCipher.encrypt(Config.AES_KEY.getBytes(),friendJson.getTime());
            }else{
                Calendar calendar=Calendar.getInstance();
                String month_current=String.valueOf(calendar.get(Calendar.MONTH)+1);
                String date_current=String.valueOf(calendar.get(Calendar.DATE));
                String hour_current=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                String minute_current=String.valueOf(calendar.get(Calendar.MINUTE));
                String time=month_current+"-"+date_current+"-"+hour_current+"-"+minute_current;
                cypher_time=AESCipher.encrypt(Config.AES_KEY.getBytes(),transformDate(time));
            }
            FriendJson friendJson1=new FriendJson(friendJson.getId(),friendJson.getFriendid(),cypher_nickname,cypher_remark,cypher_time);
            messageService.insertFriend(friendJson1);
            User user=userService.select(friendJson.getId());
            FriendJson friendJson2=new FriendJson(friendJson.getFriendid(),friendJson.getId(),user.getNickname(),cypher_remark,cypher_time);
            messageService.insertFriend(friendJson2);
            log(friendJson.getId()+" and "+friendJson.getFriendid(),"friend relationship connect success");
            return "friend post success";
        }catch (Exception e){
            e.printStackTrace();
            log(friendJson.getId()+" and "+friendJson.getFriendid(),"friend relationship connect fail");
            return "friend post fail";
        }
    }

    //获得好友列表
    @RequestMapping(value = "/friend/get",method = RequestMethod.GET)
    public List<FriendJson> getFriend(String id) throws Exception {
        List<FriendJson> friendJsonList=new ArrayList<>();
        friendJsonList=messageService.selectFriend(id);
        List<FriendJson> friendJsonList1=new ArrayList<>();
        for (FriendJson friendJson : friendJsonList) {
            String nickname=null;
            if(friendJson.getNickname()!=null){
                nickname=AESCipher.decrypt(Config.AES_KEY.getBytes(),friendJson.getNickname());
            }
            String remark=null;
            if(friendJson.getRemark()!=null){
                remark=AESCipher.decrypt(Config.AES_KEY.getBytes(),friendJson.getRemark());
            }
            String time=null;
            if(friendJson.getTime()!=null){
                time=AESCipher.decrypt(Config.AES_KEY.getBytes(),friendJson.getTime());
            }
            friendJsonList1.add(new FriendJson(friendJson.getId(),friendJson.getFriendid(),nickname,remark,time));
        }
        log(id,"get friend list complete");
        return friendJsonList1;
    }

    //获得好友列表的头像
    @RequestMapping(value = "/friend/head/get",method = RequestMethod.GET)
    public List<String> getFriendHead(String id){
        List<String> headList=new ArrayList<>();
        List<FriendJson> friendJsonList=new ArrayList<>();
        try{
            friendJsonList=messageService.selectFriend(id);
            for (FriendJson friendJson : friendJsonList) {
                InputStream inputStream = null;
                byte[] data = null;
                String base64Str = null;
                String friendID = friendJson.getFriendid();
                try {
                    //判断指定路径文件是否存在
                    File file = new File("d:\\YZNData\\head\\" + friendID + ".jpg");
                    if (file.exists()) {
                        inputStream = new FileInputStream("d:\\YZNData\\head\\" + friendID + ".jpg");
                        data = new byte[inputStream.available()];
                        inputStream.read(data);
                        inputStream.close();
                        //转base64
                        if (data != null) {
                            base64Str = Base64.getEncoder().encodeToString(data);
                        } else {
                            base64Str = null;
                        }
                    } else {
                        base64Str = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    base64Str = null;
                }
                headList.add(base64Str);
            }
            log(id,"get friend head complete");
            return headList;
        }catch (Exception e){
            e.printStackTrace();
            log(id,"get friend head fail");
            return null;
        }
    }

    //申请好友
    @RequestMapping(value = "/friend/apply/post",method = RequestMethod.POST)
    public String applyFriend(@RequestBody  FriendApply friendApply){
        if(friendApply.getFromid()!=null&&friendApply.getToid()!=null){
            try{
                messageService.insertApplication(friendApply);
                log(friendApply.getFromid()+" and "+friendApply.getToid(),"friend apply complete");
                return "friend apply complete";
            }catch (Exception e){
                e.printStackTrace();
                log(friendApply.getFromid()+" and "+friendApply.getToid(),"friend apply fail");
                return "friend apply fail";
            }
        }else{
            log("friend application include null variable");
            return "friend apply fail";
        }
    }

    //获得好友申请
    @RequestMapping(value = "/friend/apply/get",method = RequestMethod.GET)
    public List<FriendApply> getFriendApply(String toid){
        try{
            List<FriendApply> friendApplyList=new ArrayList<>();
            friendApplyList=messageService.selectApply(toid);
            if(friendApplyList.size()>0){
                messageService.deleteApplication(toid);
            }
            log(toid,"get friend application complete");
            return friendApplyList;
        }catch (Exception e){
            e.printStackTrace();
            log(toid,"get friend application fail");
            return null;
        }
    }

    //同意好友申请
    @RequestMapping(value = "/friend/confirm/post",method = RequestMethod.POST)
    public String confirmFriend(@RequestBody FriendApply friendApply){
        try{
            messageService.insertConfirm(friendApply);
            log(friendApply.getFromid()+" and "+friendApply.getToid(),"friend confirm post complete");
            return "friend confirm success";
        }catch (Exception e){
            e.printStackTrace();
            log(friendApply.getFromid()+" and "+friendApply.getToid(),"friend confirm post fail");
            return "friend confirm fail";
        }
    }

    //获得同意的好友申请
    @RequestMapping(value = "/friend/confirm/get",method = RequestMethod.GET)
    public List<FriendApply> getConfirm(String fromid){
        try{
            List<FriendApply> friendApplyList=new ArrayList<>();
            friendApplyList=messageService.getConfirm(fromid);
            for (FriendApply friendApply : friendApplyList) {
                messageService.deleteConfirm(friendApply.getFromid(), friendApply.getToid());
            }
            log(fromid,"get friend confirm complete");
            return friendApplyList;
        }catch (Exception e){
            e.printStackTrace();
            log(fromid,"get friend confirm fail");
            return null;
        }
    }

    //上传个人信息
    @RequestMapping(value = "/info/post",method = RequestMethod.POST)
    public String postInfo(@RequestBody Info info){
        try{
            String phone=null;
            if(info.getPhone()!=null){
                phone=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getPhone());
            }
            String nickname=null;
            if(info.getNickname()!=null){
                nickname=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getNickname());
            }
            String birthday=null;
            if(info.getBirthday()!=null){
                birthday=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getBirthday());
            }
            String company=null;
            if(info.getCompany()!=null){
                company=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getCompany());
            }
            String job=null;
            if(info.getJob()!=null){
                job=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getJob());
            }
            String province=null;
            if(info.getProvince()!=null){
                province=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getProvince());
            }
            String city=null;
            if(info.getCity()!=null){
                city=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getCity());
            }
            messageService.insertInfo(new Info(info.getId(),phone,nickname,info.getSex(),birthday,company,job,province,city));
            log(info.getId(),"info post complete");
            return "info post complete";
        }catch (Exception e){
            e.printStackTrace();
            log(info.getId(),"info post fail");
            return "info post fail";
        }
    }

    //获得个人信息
    @RequestMapping(value = "/info/get",method = RequestMethod.GET)
    public Info getInfo(String id){
        try{
            Info info=new Info();
            info=messageService.selectInfo(id);
            String phone=null;
            if(info.getPhone()!=null){
                phone=AESCipher.decrypt(Config.AES_KEY.getBytes(),info.getPhone());
            }
            String nickname=null;
            if(info.getNickname()!=null){
                nickname=AESCipher.decrypt(Config.AES_KEY.getBytes(),info.getNickname());
            }
            String birthday=null;
            if(info.getBirthday()!=null){
                birthday=AESCipher.decrypt(Config.AES_KEY.getBytes(),info.getBirthday());
            }
            String company=null;
            if(info.getCompany()!=null){
                company=AESCipher.decrypt(Config.AES_KEY.getBytes(),info.getCompany());
            }
            String job=null;
            if(info.getJob()!=null){
                job=AESCipher.decrypt(Config.AES_KEY.getBytes(),info.getJob());
            }
            String province=null;
            if(info.getProvince()!=null){
                province=AESCipher.decrypt(Config.AES_KEY.getBytes(),info.getProvince());
            }
            String city=null;
            if(info.getCity()!=null){
                city=AESCipher.decrypt(Config.AES_KEY.getBytes(),info.getCity());
            }
            log(id,"info get complete");
            return new Info(info.getId(),phone,nickname,info.getSex(),birthday,company,job,province,city);
        }catch (Exception e){
            e.printStackTrace();
            log(id,"info get fail");
            return null;
        }
    }

    //更新个人信息
    @RequestMapping(value = "/info/upgrade",method = RequestMethod.POST)
    public String upgradeInfo(@RequestBody Info info){
        try{
            String phone=null;
            if(info.getPhone()!=null){
                phone=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getPhone());
            }
            String nickname=null;
            if(info.getNickname()!=null){
                nickname=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getNickname());
            }
            String birthday=null;
            if(info.getBirthday()!=null){
                birthday=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getBirthday());
            }
            String company=null;
            if(info.getCompany()!=null){
                company=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getCompany());
            }
            String job=null;
            if(info.getJob()!=null){
                job=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getJob());
            }
            String province=null;
            if(info.getProvince()!=null){
                province=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getProvince());
            }
            String city=null;
            if(info.getCity()!=null){
                city=AESCipher.encrypt(Config.AES_KEY.getBytes(),info.getCity());
            }
            messageService.upgradeInfo(new Info(info.getId(),phone,nickname,info.getSex(),birthday,company,job,province,city));
            log(info.getId(),"info upgrade complete");
            return "info upgrade complete";
        }catch (Exception e){
            e.printStackTrace();
            log(info.getId(),"info upgrade fail");
            return "info upgrade fail";
        }
    }

    private String transformDate(String time){
        ArrayList<String> list=stringToArrayList(time,"-");
        Calendar calendar=Calendar.getInstance();
        String month_current=String.valueOf(calendar.get(Calendar.MONTH)+1);
        String date_current=String.valueOf(calendar.get(Calendar.DATE));
        String hour_current=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute_current=String.valueOf(calendar.get(Calendar.MINUTE));
        if(month_current.equals(list.get(0))&&date_current.equals(list.get(1))){
            return hour_current+":"+minute_current;
        }else{
            return month_current+"-"+date_current;
        }

    }

    public static ArrayList<String> stringToArrayList(String str, String separator) {
        ArrayList<String> arr = new ArrayList<String>();
        if ((str == null) || (separator == null)) {
            return arr;
        }
        StringTokenizer st = new StringTokenizer(str, separator);
        while (st.hasMoreTokens()) {
            arr.add(st.nextToken());
        }
        return arr;
    }



    public void log(String id,String event){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  " +id+": "+event);
    }

    public void log(String event){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  " +": "+event);
    }

}
