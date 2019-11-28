//package com.piggy;
//
//import com.piggy.Realm.MyRealm;
//import com.piggy.Realm.PermissionRealm;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.IncorrectCredentialsException;
//import org.apache.shiro.authc.UnknownAccountException;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.authz.UnauthorizedException;
//import org.apache.shiro.mgt.DefaultSecurityManager;
//import org.apache.shiro.realm.text.IniRealm;
//import org.apache.shiro.subject.Subject;
//import org.junit.Test;
//
//import java.util.Arrays;
//
//public class PermissionTest {
//    private static Log log = LogFactory.getLog(ShiroTest.class);
//
//    @Test
//    public void testPermission(){
////        判断用户是否拥有权限之前必须是在用户登录之后判断
//        DefaultSecurityManager dsm = new DefaultSecurityManager();
////        IniRealm ir = new IniRealm("classpath:shiro-permission.ini");
//        dsm.setRealm(new PermissionRealm());
//        SecurityUtils.setSecurityManager(dsm);
//        Subject subject = SecurityUtils.getSubject();
//        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan", "666");
//        try {
//            subject.login(token);
//
//        } catch (UnknownAccountException e) {
//            log.info("用户不存在");
//        } catch (IncorrectCredentialsException e1) {
//            log.info("密码不正确");
//        }
//
////        8. 判断是否认证成功
//        boolean b = subject.isAuthenticated();
//        if (b){
////            表示认证登录成功，开始判断用户是否拥有角色身份
////
//////            第一种方式
//////            判断用户是否存在某个角色，返回true表示有，false表示没有
//            log.info(subject.hasRole("role1"));
////            log.info(subject.hasAllRoles(Arrays.asList("role1","role2")));
////            log.info(Arrays.toString(subject.hasRoles(Arrays.asList("role1","role2","role3"))));
////
//////           第二种，如果有角色，无返回值，没有角色，直接报错
////            try{
////                subject.checkRole("role1");
////                log.info("role1角色存在");
////                subject.checkRoles("role1","role2","role3");
////                log.info("走到这里说明三个角色都有");
////            }catch (UnauthorizedException e){
////                log.info("相关用户不存在");
////            }
//
//
////            ---------------------------
////            登录之后判断用户是否拥有权限
//            log.info(subject.isPermitted("user:delete"));
////            返回true表示全部拥有权限，false表示不全部有用
////            log.info(subject.isPermittedAll("user:update","user:delete"));
////            返回一个boolean数组
////            log.info(Arrays.toString(subject.isPermitted("user:create","user:update","user:delete")));
//
////            第二种是没有权限抛出异常
////            try{
////                subject.checkPermission("user:update");
////                log.info("拥有更新权限");
////                subject.checkPermission("user:select");
////                log.info("走到这里说明拥有查询权限");
////            }catch (UnauthorizedException e){
////                log.info("没有拥有相应的权限");
////            }
//
//        }
//
//
//    }
//}
