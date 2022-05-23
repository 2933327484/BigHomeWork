package com.example.DataBase;

import android.util.Log;
import android.widget.ImageView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DBUtil {
    private static final String TAG = "DBUtils";
    private static Connection conn=null;

    private static final String database_user = "android_project";
    private static final String database_password = "CST@201202";
    private static PreparedStatement stmt = null;
    private static ResultSet res;
    public static boolean rs=false;
    static public String email;
    static public String phone;
    static public String username;
    static public String password;
    static public int id_from;
    static public int id_to;
    static public int id=66;
    static public int id_image;
    static public String message;
    static public List<String> list=new ArrayList();
    static public ImageView imageview;
    static public String imagestring;


    public void getConnection(){
        String url = "jdbc:mysql://rm-bp1zq475tmxkyv8o2mo.mysql.rds.aliyuncs.com:3306/android_database" ;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.d(TAG, "加载JDBC驱动成功");
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "加载JDBC驱动失败");
        }
        try {
            DBUtil.conn = DriverManager.getConnection(url, database_user, database_password);
            Log.d(TAG, "数据库连接成功");

        } catch (SQLException e) {
            Log.d(TAG, "数据库连接失败");
        }
    }

    public  boolean register(String username,String phone,String password) {
                                                                                //功能:向数据库中注册用户（用户名不能重复）
        DBUtil.phone=phone;                                                  //若用户名重复返回false,否则返回true
        DBUtil.username=username;
        DBUtil.password=password;
        Thread_Register thread_register =new Thread_Register();
        thread_register.start();

        try {
            while (!thread_register.getState().toString().equals("TERMINATED")) {
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public  boolean SignIn(String username,String password){

        DBUtil.username=username;               //根据用户名和密码登录
        DBUtil.password=password;               //数据库中有该用户的信息返回true,否则返回false
        Thread_SignIn  thread_sigin=new Thread_SignIn();
        thread_sigin.start();
        try {
            while (!thread_sigin.getState().toString().equals("TERMINATED")) {
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public void sendmessage(int id_to,int id_from,String message){
        DBUtil.id_to=id_to;                     //功能：根据id_to、id_from发送message
        DBUtil.id_from=id_from;                 //
        DBUtil.message=message;

        Thread_SendMessage thread_sendmessage=new Thread_SendMessage();
        thread_sendmessage.start();
    }

    public List<String> getmessage(int id_to){
        DBUtil.id_to=id_to;                     //功能：根据id_to查找message;
        DBUtil.list.clear();                    //返回list<string>.该list的项为message.有所有的留言;

        Thread_GetMessage thread_getmessage=new Thread_GetMessage();
        thread_getmessage.start();
        try {
            while (!thread_getmessage.getState().toString().equals("TERMINATED")) {
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return DBUtil.list;
    }

    public void delete_message(int id_to,String message){
        DBUtil.id_to=id_to;                     //功能：根据id_to和具体的message内容删除某一条消息
        DBUtil.message=message;

        Thread_DeleteMessage thread_deletemessage=new Thread_DeleteMessage();
        thread_deletemessage.start();
        try {
            while (!thread_deletemessage.getState().toString().equals("TERMINATED")) {
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void set_image(String imagestring){  //功能：设置用户的image
        DBUtil.imagestring=imagestring;         //需配合image_listener使用

        Thread_SetImage thread_setimage=new Thread_SetImage();
        thread_setimage.start();
        try {
            while (!thread_setimage.getState().toString().equals("TERMINATED")) {
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public String get_image(int id_image){  //功能：得到图像
        DBUtil.id_image=id_image;           //需配合getimage(int id_image,int imageview_id)使用
        Thread_GetImage thread_getimage=new Thread_GetImage();
        thread_getimage.start();

        try {
            while (!thread_getimage.getState().toString().equals("TERMINATED")) {
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return DBUtil.imagestring;
    }

    class Thread_Register extends Thread{
        public void run() {

            getConnection();
            try {
                String sql1 = "select id from user_p_table where username ='" + DBUtil.username+"'";
                stmt = conn.prepareStatement(sql1);
                // 关闭事务自动提交
                conn.setAutoCommit(false);
                res = stmt.executeQuery();//创建数据对象
                if (res.next()) {
                    DBUtil.rs = false;
                } else {
                    DBUtil.rs = true;
                }

                conn.commit();
                res.close();
                stmt.close();
                if (DBUtil.rs) {
                    String sql = "insert into user_p_table(username,phone,password) values(?,?,?,?)";
                    stmt = conn.prepareStatement(sql);
                    conn.setAutoCommit(false);
                    stmt.setString(1, DBUtil.username);
                    stmt.setString(2, DBUtil.phone);
                    stmt.setString(3, DBUtil.password);
                    stmt.addBatch();
                    stmt.executeBatch();
                    conn.commit();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread_SignIn extends Thread{
        public void run() {
            getConnection();
            try {
                String sql = "select * from user_p_table where username = '"+ DBUtil.username+"'";
                stmt = conn.prepareStatement(sql);
                // 关闭事务自动提交
                conn.setAutoCommit(false);
                res = stmt.executeQuery();//创建数据对象
                res.next();
                if(res.getString("password").equals(DBUtil.password)){
                    DBUtil.rs=true;
                    DBUtil.id=res.getInt("id");
                }else{
                    DBUtil.rs=false;
                }
                conn.commit();
                res.close();
                stmt.close();

            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    class Thread_SendMessage extends Thread{
        @Override
        public void run() {
            getConnection();
            try {
                String sql = "insert into leave_message(id_to,id_from,message) values(?,?,?)";
                stmt = conn.prepareStatement(sql);
                conn.setAutoCommit(false);
                stmt.setInt(1, DBUtil.id_to);
                stmt.setInt(2, DBUtil.id_from);
                stmt.setString(3, DBUtil.message);
                stmt.addBatch();
                stmt.executeBatch();
                conn.commit();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread_GetMessage extends Thread{
        @Override
        public void run() {
            getConnection();
            try {
                String sql = "select message from leave_message where id_to ="+ DBUtil.id_to;
                stmt = conn.prepareStatement(sql);
                // 关闭事务自动提交
                conn.setAutoCommit(false);
                res = stmt.executeQuery();//创建数据对象

                while (res.next()){
                    DBUtil.list.add(res.getString("message"));
                }

                res.close();
                conn.commit();
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread_DeleteMessage extends Thread{
        public void run() {
            getConnection();
            try {
                String sql = "delete from leave_message where id_to="+DBUtil.id_to+" and message ='" +DBUtil.message+"'";
                stmt = conn.prepareStatement(sql);
                conn.setAutoCommit(false);
                stmt.addBatch();
                stmt.executeBatch();
                conn.commit();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class Thread_SetImage extends Thread{//intent imageview
        @Override
        public void run() {
            getConnection();
            try {
                String sql =  "update user_p_table set image=? where id ="+DBUtil.id;
                stmt = conn.prepareStatement(sql);
                conn.setAutoCommit(false);
                stmt.setString(1,imagestring);
                stmt.addBatch();
                stmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread_GetImage extends Thread{
        public void run(){
            getConnection();
            try {
                String sql = "select image from user_p_table where id ="+ DBUtil.id_image;
                stmt = conn.prepareStatement(sql);
                // 关闭事务自动提交
                conn.setAutoCommit(false);
                res = stmt.executeQuery();//创建数据对象

                res.next();
                DBUtil.imagestring=res.getString("image");

                res.close();
                conn.commit();
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}