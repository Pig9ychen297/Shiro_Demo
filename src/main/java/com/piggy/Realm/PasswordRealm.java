package com.piggy.Realm;

import com.piggy.pojo.User;
import com.piggy.util.DBUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.sql.Connection;
import java.sql.SQLException;

public class PasswordRealm extends AuthorizingRealm{
    @Override
    public String getName() {
        return "PwdRealm";
    }
//授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }
//认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {


// 通过传递的token中的用户名去数据库查询用户信息，封装成一个AuthenticationInof对象返回，用于与token中的信息进行对比;
        String username = (String)authenticationToken.getPrincipal();
//       通过用户名查询数据库，将数据库里的数据(账号和密码)查询返回包装成Info对象

        QueryRunner queryRunner = new QueryRunner();
        Connection connection = DBUtils.getConnection();
        String sql = "select * from user where d_name = ?";
        User user = null;
        String password = "";
        try {
            user = queryRunner.query(connection, sql, new BeanHandler<>(User.class), username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(user==null) {
            return null;
        }else{
//          数据库中的密码
            password = user.getD_password();
        }
        /**@username : 数据库中用户信息，(一般为user对象)
         * @password : 数据库密码
         * @getName(): 当前Realm的名字
         */
//        比对的方法,密码加密之后,要把加的盐给info进行封装
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, ByteSource.Util.bytes(username), getName());
        System.out.println(authenticationToken);
        return info;
    }
}

