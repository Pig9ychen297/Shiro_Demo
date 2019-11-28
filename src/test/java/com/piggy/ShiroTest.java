package com.piggy;

import com.piggy.Realm.MyRealm;
import com.piggy.Realm.PasswordRealm;
import com.piggy.pojo.User;
import com.piggy.util.DBUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.RealmFactory;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.realm.text.TextConfigurationRealm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/*
测试shiro的认证
 */
public class ShiroTest {

    private static Log log = LogFactory.getLog(ShiroTest.class);

    @Test
    public void testLogin() throws Exception {

//        1. 创建DefaultSecurityManager对象 用来引用ini配置文件
        DefaultSecurityManager dsm = new DefaultSecurityManager();
//        2.读取ini文件
//       IniRealm ir = new IniRealm("classparh:shiro.ini");

//        3.将Realm获取的安全数据源(ini文件)给SecurityManager
//        dsm.setRealm(ir);

//       换成了自定义Realm而不是使用IniRealm
        dsm.setRealm(new MyRealm());
//        4.将安全管理器绑定到util
        SecurityUtils.setSecurityManager(dsm);
//        5.创建主体对象 主体需要认证才能登录
        Subject subject = SecurityUtils.getSubject();

//        7.生成一个token认证令牌

        UsernamePasswordToken token = new UsernamePasswordToken("abc111", "123456");

//        6.login方法的参数是一个token令牌，所以在这之前要设置一个token往上进行第七步

        try {
            subject.login(token);

        } catch (UnknownAccountException e) {
            log.info("用户不存在");
        } catch (IncorrectCredentialsException e1) {
            log.info("密码不正确");
        }

//        8. 判断是否认证成功
        boolean b = subject.isAuthenticated();
        log.info("验证是否成功" + b);

        subject.logout();
    }



//测试PasswordRealm的方法
    @Test
    public void testMD5(){

        PasswordRealm passwordRealm = new PasswordRealm();
//        1. 创建DefaultSecurityManager对象
        DefaultSecurityManager dsm = new DefaultSecurityManager();
//
//       2.换成了自定义Realm而不是使用IniRealm
        dsm.setRealm(passwordRealm);

//        3.对密码进行加密
//        创建凭证匹配器
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
//        加密算法
        matcher.setHashAlgorithmName("md5");
//        散列次数
        matcher.setHashIterations(3);
//       将凭证匹配器传递给自定义Realm
        passwordRealm.setCredentialsMatcher(matcher);
//
//          4.将安全管理器绑定到util
        SecurityUtils.setSecurityManager(dsm);
//        5.创建主体对象 主体需要认证才能登录
        Subject subject = SecurityUtils.getSubject();

//        7.生成一个token认证令牌

        UsernamePasswordToken token = new UsernamePasswordToken("abc111", "123456");

//        6.login方法的参数是一个token令牌，所以在这之前要设置一个token往上进行第七步

        try {
            subject.login(token);

        } catch (UnknownAccountException e) {
            log.info("用户不存在");
        } catch (IncorrectCredentialsException e1) {
            log.info("密码不正确");
        }

//        8. 判断是否认证成功
        boolean b = subject.isAuthenticated();
        log.info("验证是否成功" + b);

        subject.logout();
    }


//    用来测试我的JDBCUtil的方法
    @Test
    public void test11() throws SQLException {
        QueryRunner runner = new QueryRunner();
        Connection connection = DBUtils.getConnection();
        String sql = "select * from user";
        List<User> lists = runner.query(connection, sql, new BeanListHandler<>(User.class));
        for (User user : lists) {
            System.out.println(user.getD_name());
        }
        String sql1 = "update user set d_password = ?, d_age=? where d_name = ?";
        int i = runner.update(connection, sql1, "39bb4a641bc7058a2eb2746b6c52185b", 100, "abc111");
        log.info("id值为"+i);
    }
}
