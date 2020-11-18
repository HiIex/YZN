package com.example.yzn.controller;

import com.example.yzn.service.UserService;
import com.example.yzn.entity.*;
import com.example.yzn.security.AESCipher;
import com.example.yzn.security.Config;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;


@RestController
@RequestMapping("/sys/user")
public class MyController {

    public final static String latestVersion="1.4";

    @Autowired
    public UserService userService;

    //登录
    @RequestMapping("login")
    public LoginResult login(@RequestBody LoginRequest loginRequest) {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);

        //通过id取出clientsk解密得到cypher
        String cypher=null;
        User user=null;
        try{
            user=userService.select(loginRequest.getId());
            //RSAKey rsaKey=userService.findKeyByID(user.getId());
            cypher=loginRequest.getCypher();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                    +loginRequest.getId()+": "+"login fail!");
            return new LoginResult(LoginResult.FAIL);
        }
        if(user.getCypher().equals(cypher)){
            System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                    +loginRequest.getId()+": "+"login success!");
            return new LoginResult(LoginResult.SUCCESS);
        }else{
            System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                    +loginRequest.getId()+": "+"login fail!");
            return new LoginResult(LoginResult.FAIL);
        }

    }


    //注册
    @RequestMapping("register")
    public RegisterResult register(@RequestBody User userModel){

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        User user=new User();
        try{
            //用serversk解密
            //数据库aes加密
            user.setId(userModel.getId());
            user.setPhone(AESCipher.encrypt(Config.AES_KEY.getBytes(),userModel.getPhone()));
            user.setCypher(userModel.getCypher());
            user.setSalt(userModel.getSalt());
            user.setNickname(AESCipher.encrypt(Config.AES_KEY.getBytes(),userModel.getNickname()));

            //自动生成一对clientsk，clientpk
            /*
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            byte[] publicBytes = pair.getPublic().getEncoded();
            byte[] privateBytes = pair.getPrivate().getEncoded();
            System.out.println("public key: " + Base64.getEncoder().encodeToString(publicBytes));
            System.out.println("private key: " + Base64.getEncoder().encodeToString(privateBytes));
             */
            userService.insert(user);
            //userService.insertRSAKey(new RSAKey(user.getId(),Base64.getEncoder().encodeToString(publicBytes),Base64.getEncoder().encodeToString(privateBytes)));
            System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                    +userModel.getId()+": "+"register success!");
            //用serverpk加密clientpk后返回
            return new RegisterResult(RegisterResult.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                    +userModel.getId()+": "+"register fail!");
            return new RegisterResult(RegisterResult.ERROR);
        }
    }

    //异地同步：通过手机号请求MD5 id和salt
    @RequestMapping("id")
    public IDResponse requestID(@RequestBody IDRequest idRequest){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        //用serverpk加密nickname和salt
        try{
            User user=userService.findByPhone(AESCipher.encrypt(Config.AES_KEY.getBytes(),idRequest.getPhone()));
            if(user!=null){
                System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                        +idRequest.getPhone()+": "+"request id and salt success!");
                return new IDResponse(IDResponse.MATCH,user.getId(),user.getSalt()
                        ,AESCipher.decrypt(Config.AES_KEY.getBytes(),user.getNickname()));
            }else{
                System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                        +idRequest.getPhone()+": "+"request id and salt fail!");
                return new IDResponse(IDResponse.ERROR,null,null,null);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                    +idRequest.getPhone()+": "+"request id and salt fail!");
            return new IDResponse(IDResponse.ERROR,null,null,null);
        }
    }

    //注册时判断手机号是否已存在
    @RequestMapping("phone")
    public PhoneResponse requestPhone(@RequestBody PhoneRequest phoneRequest){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        try{
            String phone=phoneRequest.getPhone();
            User user=userService.findByPhone(AESCipher.encrypt(Config.AES_KEY.getBytes(),phone));
            if(user==null){
                System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                        +phone+": "+"phone permit!");
                return new PhoneResponse(PhoneResponse.PERMIT);
            }else{
                System.out.println(year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second+"  "
                        +phone+": "+"phone exit!");
                return new PhoneResponse(PhoneResponse.EXIST);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new PhoneResponse(PhoneResponse.EXIST);
        }
    }

    //上传订单
    @RequestMapping("upload/bill")
    public String uploadBill(@RequestBody BillJson billJson) throws Exception {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);

        BillDatabase billDatabase=new BillDatabase();
        billDatabase.setBillID(billJson.getBillID());
        billDatabase.setCurrency(billJson.getCurrency());
        billDatabase.setDetail(AESCipher.encrypt(Config.AES_KEY.getBytes(),billJson.getDetail()));
        billDatabase.setIssuerID(billJson.getIssuerID());
        billDatabase.setMiddleName(billJson.getMiddleName());
        billDatabase.setProductName(AESCipher.encrypt(Config.AES_KEY.getBytes(),billJson.getProductName()));
        if(billJson.isTaken()){
            billDatabase.setTaken(1);
        }else{
            billDatabase.setTaken(0);
        }
        billDatabase.setPrice(AESCipher.encrypt(Config.AES_KEY.getBytes(),billJson.getPrice()));
        billDatabase.setType(billJson.getType());
        if(billJson.getBase64Str()!=null) {
            billDatabase.setImageUrl("d:\\YZNData\\reduce\\" + billJson.getBillID() + ".jpg");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] bytes = Base64.getDecoder().decode(billJson.getBase64Str());
                        // 调整异常数据
                        for (int i = 0; i < bytes.length; ++i) {
                            if (bytes[i] < 0) {
                                bytes[i] += 256;
                            }
                        }
                        OutputStream outputStream = new FileOutputStream("d:\\YZNData\\original\\" + billJson.getBillID() + ".jpg");
                        outputStream.write(bytes);
                        outputStream.flush();
                        outputStream.close();
                        Thumbnails.of("d:\\YZNData\\original\\" + billJson.getBillID() + ".jpg").scale(1f).outputQuality(0.2f)
                                .toFile("d:\\YZNData\\reduce\\" + billJson.getBillID() + ".jpg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            try {
                userService.insertBill(billDatabase);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                        + billJson.getBillID() + ": " + "upload fail!");
                return "upload fail";
            }

            System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                    + billJson.getBillID() + ": " + "upload complete!");
            return "upload complete";
        }else{
            billDatabase.setImageUrl(null);
            try {
                userService.insertBill(billDatabase);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                        + billJson.getBillID() + ": " + "upload fail!");
                return "upload fail";
            }

            System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                    + billJson.getBillID() + ": " + "upload complete!");
            return "upload complete";
        }
    }

    //下载全部订单
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public List<BillJson> downloadBills() throws Exception {
        List<BillDatabase> billDatabaseList=userService.findAllBill();
        List<BillJson> billJsonList=new ArrayList<>();
        for(int i=0;i<billDatabaseList.size();i++){
            Calendar calendar=Calendar.getInstance();
            int year=calendar.get(Calendar.YEAR);
            int month=calendar.get(Calendar.MONTH)+1;
            int date=calendar.get(Calendar.DATE);
            int hour=calendar.get(Calendar.HOUR_OF_DAY);
            int minute=calendar.get(Calendar.MINUTE);
            int second=calendar.get(Calendar.SECOND);
            InputStream inputStream = null;
            byte[] data = null;
            String base64Str=null;
            boolean isTaken=false;
            BillDatabase billDatabase=billDatabaseList.get(i);
            if(billDatabase==null){
                break;
            }
            try {
                String imageUrl=null;
                imageUrl=billDatabase.getImageUrl();
                if(imageUrl!=null){
                    inputStream = new FileInputStream(billDatabase.getImageUrl());
                    data = new byte[inputStream.available()];
                    inputStream.read(data);
                    inputStream.close();
                }else{
                    data=null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            //转base64
            if(data!=null){
                base64Str=Base64.getEncoder().encodeToString(data);
            }else{
                base64Str=null;
            }

            if(billDatabase.isTaken()==0){
                isTaken=false;
            }else{
                isTaken=true;
            }
            BillJson billJson=new BillJson(billDatabase.getBillID(),billDatabase.getIssuerID()
                    ,AESCipher.decrypt(Config.AES_KEY.getBytes(),billDatabase.getProductName()),
                    AESCipher.decrypt(Config.AES_KEY.getBytes(),billDatabase.getPrice()),billDatabase.getCurrency(),billDatabase.getType()
                    ,billDatabase.getMiddleName(), base64Str,AESCipher.decrypt(Config.AES_KEY.getBytes(),billDatabase.getDetail()),isTaken);
            billJsonList.add(billJson);
            System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                    + billJson.getBillID() + ": " + "list download complete!");
        }
        return billJsonList;
    }

    //请求原图
    @RequestMapping(value = "/image",method = RequestMethod.GET)
    public OriginalImage getOriginalImage(String billID){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        InputStream inputStream = null;
        byte[] data = null;
        String base64Str=null;
        try{
            inputStream = new FileInputStream("d:\\YZNData\\original\\" + billID + ".jpg");
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        //转base64
        if(data!=null){
            base64Str=Base64.getEncoder().encodeToString(data);
        }else{
            base64Str=null;
        }

        System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                + billID + ": " + "original download complete!");
        return new OriginalImage(base64Str);

    }

    //上传头像
    @RequestMapping("upload/head")
    public String uploadHead(@RequestBody HeadJson headJson) throws Exception {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);


        HeadDatabase headDatabase=new HeadDatabase();
        headDatabase.setId(headJson.getId());

        HeadDatabase headSelect=null;
        headSelect=userService.findHeadByID(headJson.getId());
        
        if(headJson.getBase64Str()!=null) {
            if(headSelect==null){
                headDatabase.setImageUrl("d:\\YZNData\\head\\" + headJson.getId() + ".jpg");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File file=new File("d:\\YZNData\\head\\" + headJson.getId() + ".jpg");
                            if(file.exists()){
                                file.delete();
                            }
                            byte[] bytes = Base64.getDecoder().decode(headJson.getBase64Str());
                            // 调整异常数据
                            for (int i = 0; i < bytes.length; ++i) {
                                if (bytes[i] < 0) {
                                    bytes[i] += 256;
                                }
                            }
                            OutputStream outputStream = new FileOutputStream("d:\\YZNData\\head\\" + headJson.getId() + ".jpg");
                            outputStream.write(bytes);
                            outputStream.flush();
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

                try {
                    userService.insertHead(headDatabase);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                            + headJson.getId() + ": " + "upload head image fail!");
                    return "upload fail";
                }

                System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                        + headJson.getId() + ": " + "upload head image complete!");
                return "upload complete";
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File file=new File("d:\\YZNData\\head\\" + headJson.getId() + ".jpg");
                            if(file.exists()){
                                file.delete();
                            }
                            byte[] bytes = Base64.getDecoder().decode(headJson.getBase64Str());
                            // 调整异常数据
                            for (int i = 0; i < bytes.length; ++i) {
                                if (bytes[i] < 0) {
                                    bytes[i] += 256;
                                }
                            }
                            OutputStream outputStream = new FileOutputStream("d:\\YZNData\\head\\" + headJson.getId() + ".jpg");
                            outputStream.write(bytes);
                            outputStream.flush();
                            outputStream.close();
                            System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                                    + headJson.getId() + ": " + "upload head image complete!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                                    + headJson.getId() + ": " + "upload head image fail!");
                        }

                    }
                }).start();
                return "upload complete";
            }

        }else{
            if(headSelect==null){
                headDatabase.setImageUrl(null);
                try {
                    userService.insertHead(headDatabase);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                            + headDatabase.getId() + ": " + "upload head image fail!");
                    return "upload fail";
                }

                System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                        + headJson.getId() + ": " + "upload head image complete!");
                return "upload complete";
            }else{
                System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                        + headJson.getId() + ": " + "upload head image complete!");
                return "upload complete";
            }

        }
    }

    //请求头像
    @RequestMapping(value = "/head",method = RequestMethod.GET)
    public HeadJson getHead(String id){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        InputStream inputStream = null;
        byte[] data = null;
        String base64Str=null;
        try{
            //判断指定路径文件是否存在
            File file=new File("d:\\YZNData\\head\\" + id + ".jpg");
            if(file.exists()){
                inputStream = new FileInputStream("d:\\YZNData\\head\\" + id + ".jpg");
                data = new byte[inputStream.available()];
                inputStream.read(data);
                inputStream.close();
                //转base64
                if(data!=null){
                    base64Str=Base64.getEncoder().encodeToString(data);
                }else{
                    base64Str=null;
                }
            }else{
                base64Str=null;
            }

        }catch (Exception e){
            e.printStackTrace();
            return new HeadJson(id,null);
        }

        System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                + id + ": " + "head image download complete!");
        return new HeadJson(id,base64Str);

    }

    //检查是否有更新的app版本
    @RequestMapping(value = "/update/check",method = RequestMethod.GET)
    public ApkVersion updateCheck(String version){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                + ": " + "current version is: "+version+", the latest is "+latestVersion);
        if(version.equals(latestVersion)){
            return new ApkVersion(true,latestVersion);
        }else{
            //boolean isTaken=apkVersion.isLatest();
            //String version1=apkVersion.getLatestVersion();
            return new ApkVersion(false,latestVersion);
        }
    }


    //下载最新APP版本
    @RequestMapping("/update/download")
    @ResponseBody
    public void updateDownload(HttpServletResponse response){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        String fileName=null;
        InputStream inputStream=null;
        ServletOutputStream servletOutputStream=null;
        try{
            fileName=new String("YZN.apk".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }catch (Exception e){
            e.printStackTrace();
        }
        String filePath="D:\\YZNData\\apk\\"+"YZN.apk";
        File file=new File(filePath);
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int)file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        try{
            inputStream=new FileInputStream(new File(filePath));
            servletOutputStream=response.getOutputStream();
            org.apache.tomcat.util.http.fileupload.IOUtils.copy(inputStream,servletOutputStream);
            response.flushBuffer();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                    servletOutputStream = null;
                }
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                        + ": " + "YZN "+latestVersion+" download complete!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /*
    //异地同步：通过MD5 id请求clientpk
    @RequestMapping(value = "/clientpk",method = RequestMethod.GET)
    public RSAKeyResult getClientPKByID(String id) throws Exception {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        RSAKey rsaKey=userService.findKeyByID(id);
        if(rsaKey!=null){
            System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                    + ": " + id+": "+rsaKey.getClientpk());
            //返回clientpk时用serverpk加密
            return new RSAKeyResult(RSACipher.encrypt(Config.SERVER_PUBLIC_KEY,rsaKey.getClientpk()));
        }else{
            System.out.println(year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + "  "
                    + ": " + id+": 请求clientpk失败！");
            return new RSAKeyResult(null);
        }
    }

     */

    private static String byteToString(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return "";
        }
        String strContent = "";
        strContent = new String(bytes, StandardCharsets.UTF_8);
        return strContent;
    }


}