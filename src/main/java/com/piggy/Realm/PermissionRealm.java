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
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PermissionRealm extends AuthorizingRealm {

//   realmy可能有多个，取个名字来区分
    @Override
    public String getName() {
        return "PermissionRealm";
    }

    //    授权操作的方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
//       此方法需要的参数 principal就是底下认证方法中返回的第一个参数
        User user = (User)principalCollection.getPrimaryPrincipal();

//        认证通过之后查询数据库中角色和权限
//        下面四行代码是模拟数据库中的角色和权限
        List<String> roles = new ArrayList<String>();
        List<String> permission = new ArrayList<String>();
//        假设数据库中有role1角色
        roles.add("role1");
//        假设数据库中有delete权限
        permission.add("user:delete");
//        返回用户数据库中的角色和权限
//        将查询出来的角色和权限包装成info对象，返回
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles);
        info.addStringPermissions(permission);
        return null;
    }















    /**
     *认证操作的方法
     * @param authenticationToken 里面是前台传递的用户账号密码
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
//        通过传递的token中的用户名去数据库查询用户信息，封装成一个AuthenticationInof对象返回，
//        用于与token中的信息进行对比
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
            password = user.getD_password();
        }
        /**@username : 数据库中用户信息，(一般为user对象)
         * @password : 数据库密码
         * @getName(): 当前Realm的名字
         */
//        比对的方法
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
        System.out.println(authenticationToken);
        return info;
    }
}
