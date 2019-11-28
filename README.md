
# <center> 权限管理Shiro
[!toc]
--------
<!-- TOC -->
- [1. Shior是什么](#1-shior是什么)
    - [1.1 Shior概念简介](#11-shior概念简介)
    - [1.2 shiro 功能简介](#12-shiro-功能简介)
    - [1.3 工作原理图](#13-工作原理图)
- [2. Shiro认证模块](#2-shiro认证模块)
    - [2.1 Shiro认证流程](#21-shiro认证流程)
    - [2.2 使用ini完成认证](#22-使用ini完成认证)
    - [2.3 登录认证执行原理过程如下](#23-登录认证执行原理过程如下)
    - [2.4 对上面的认证进行改进](#24-对上面的认证进行改进)
    - [2.5 自定义Realm的步骤](#25-自定义realm的步骤)
    - [2.6 密码加密算法](#26-密码加密算法)
- [3. 授权模块](#3-授权模块)
    - [3.1 RBAC介绍](#31-rbac介绍)
    - [3.2 授权流程](#32-授权流程)
    - [3.3 授权方式(三种)](#33-授权方式三种)
    - [3.4 判断是否拥有角色身份](#34-判断是否拥有角色身份)
    - [3.5 判断是否拥有权限](#35-判断是否拥有权限)

<!-- /TOC -->
## 1. Shior是什么

### 1.1 Shior概念简介
`Apache Shiro`是一个强大的Java**安全框架**，提供了**认证**、**授权**、**加密**、和**会话管理**等功能

>Shiro可能没有Spring Security功能强大，但是他简洁易用，能完成很多的实际应用。

### 1.2 shiro 功能简介

![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127094453.png)

+ `Authentication`：身份认证 / 登录，验证用户是不是拥有相应的身份；

+ `Authorization`：授权，即权限验证，验证某个已认证的用户是否拥有某个权限；即判断用户是否能做事情，常见的如：验证某个用户是否拥有某个角色。或者细粒度的验证某个用户对某个资源是否具有某个权限；

+ `Session Manager`：会话管理，即用户登录后就是一次会话，在没有退出之前，它的所有信息都在会话中；会话可以是普通 JavaSE 环境的，也可以是如 Web 环境的；

+ `Cryptography`：加密，保护数据的安全性，如密码加密存储到数据库，而不是明文存储；

+ `Web Support`：Web 支持，可以非常容易的集成到 Web 环境；

+ `Caching`：缓存，比如用户登录后，其用户信息、拥有的角色 / 权限不必每次去查，这样可以提高效率；

### 1.3 工作原理图
![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127095104.png)

+ **`Subject`**：与应用代码直接交互的对象是`Subject`,表示主体，是一个抽象概念，即与当前应用交互的任何东西都是`Subject`,网络爬虫、机器人等。**所有的Subject都绑定了`SecurityManager` ，应用程序与`Subject`的交互都委托给了`SecurityManager`,它才是真正的执行者，而`Subject`只是个中间人**

+ **`SecurityManager`**：`Shiro`的核心，安全相关的操作都会进入到`SecurityManager`，并且管理着所有的`Subject`,**负责与其他组件交互，可以把它看成`SpringMVC`中的`DispatcherServlet`**

+ `Realm` :域，`Shiro`从`Realm`获取安全数据（如用户、角色、权限），就是说 SecurityManager 要验证用户身份，那么它需要从 Realm 获取相应的用户进行比较以确定用户身份是否合法；也需要从 Realm 得到用户相应的角色 / 权限进行验证用户是否能进行操作；可以把 Realm 看成 DataSource，即安全数据源。

**应用原理总结：**

+ 应用代码通过 `Subject` 来进行认证和授权，而 `Subject` 又委托给 `SecurityManager`；

+ 我们需要给 `Shiro` 的 `SecurityManager` 注入 `Realm`，从而让 `SecurityManager` 能得到合法的用户及其权限进行判断。

## 2. Shiro认证模块

### 2.1 Shiro认证流程
  1. 构造`SercurityManager`环境  
  2. 主体`Subject`调用方法(比如登录方法)提交认证请求
  3. 主体委托`SercurityManager`安全管理器去执行认证操作(**分配工作的，自己不执行操作**)
  4. `SercurityManager`分配给认证器`Authenticator`去执行认证
  5. `Authenticator`认证器拿到主体传递的信息，通过`Realm`域 在数据库中进行查询获得的数据进行对比，如果两者一致表示认证成功，否则认证失败

### 2.2 使用ini完成认证
**先不使用数据库查询，使用`ini`文件完成认证测试**

1. **为了简便,创建一个`maven quickstart`项目,导入相关依赖**

![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127120948.png)

1. **导入三个依赖，分别是 `juint`测试，`logging`日志 `shiro-core`核心**
```xml
<dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.12</version>
      <scope>test</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/commons-logging/commons-logging -->
    <dependency>
      <groupId>commons-logging</groupId>
      <artifactId>commons-logging</artifactId>
      <version>1.2</version>
    </dependency>
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-nop</artifactId>
      <version>1.7.24</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.shiro/shiro-core -->
    <dependency>
      <groupId>org.apache.shiro</groupId>
      <artifactId>shiro-core</artifactId>
      <version>1.4.0</version>
    </dependency>
  </dependencies>
```
3. **添加`Shiro.ini`配置文件**

在`main`目录下创建一个资源文件夹`resources`然后在创建一个`shiro.ini`配置文件
![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127121540.png)

`Shiro.ini`配置文件内容如下
```ini
[users]
#模拟数据库用户列表
zhangsan=666
lisi=888
```
4. 编写测试类
```java
/*
测试shiro的认证
 */
public class ShiroTest {

    private  static Log log = LogFactory.getLog(ShiroTest.class);

    @Test
    public void testLogin() throws Exception{

//        1. 创建DefaultSecurityManager对象 用来引用ini配置文件
        DefaultSecurityManager dsm = new DefaultSecurityManager();
//        2.读取ini文件
        IniRealm ir = new IniRealm("classpath:shiro.ini");
//        3.将Realm获取的安全数据源(ini文件)给SecurityManager
        dsm.setRealm(ir);
//        4.将安全管理器绑定到util
        SecurityUtils.setSecurityManager(dsm);
//        5.创建主体对象 主体需要认证才能登录
        Subject subject = SecurityUtils.getSubject();

//        7.生成一个token认证令牌

        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan","666");

//        6.login方法的参数是一个token令牌，所以在这之前要设置一个token往上进行第七步

        try{
            subject.login(token);

        }catch (UnknownAccountException e){
            log.info("用户不存在");
        }catch (IncorrectCredentialsException e1){
            log.info("密码不正确");
        }

//        8. 判断是否认证成功
        boolean b= subject.isAuthenticated();
        log.info("验证是否成功"+b);

        subject.logout();
    }
}
```

### 2.3 登录认证执行原理过程如下
通过在`subject.login(token)`设置断点得知
1. 前台传递了`token`传递给`subject`
   ![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127155252.png)
2. `subject`执行方法其实是委托`SecurityManager`去执行，并且此时`SecurityManager`获取`Realm`对象
3. `SecurityManager`对象将`reaml`和`token`委托给`authenticator`认证器执行`doSingleRealmAuthentication`方法进行对比
4. 认证器通过`token`给的`username`去`realm`中查找相应的用户，找到的话将用户的账号和密码存储在`AuthenticationInfo`对象中，找不到的话抛出一个`UnknownAccountException`异常
5. `AuthenticationInfo`对比`token`与封装了数据库信息的`info`对象中的密码，匹配成功就登录成功，否则抛出`IncorrectCredentialsException`异常，表示密码错误，登录失败
![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127164148.png)

![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127164410.png)

### 2.4 对上面的认证进行改进
使用数据库来替换`ini`文件、且密码不用明文方式显示，也就是密码进行加密操作

在查询数据库时，不能继续使用`Shiro`提供的`IniRealm`而是要自己定义一个`Realm`，自定义的域需要实现三个功能:
**认证、授权、缓存**，**有一种很好的办法就是继承`AuthorizingReaml`就能同时拥有三个功能**

![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127165540.png)

### 2.5 自定义Realm的步骤

**1. 创建一个类继承`AuthorizingReaml`类重写三个方法**
   + `getName()`方法 ：表识自定义`Realm`的名字
   + `doGetAuthenticationInfo()` 认证的方法
   + `doGetAuthorizationInfo()` 授权的方法
  ![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127210200.png)

  ![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127210222.png)

**2. 引入自定义`Realm`**
![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191127210424.png)


### 2.6 密码加密算法
**散列算法**：一般是不可逆的算法，适合存储密码之类的数据常见的有`MD5` 还有`SHA`一般进行散列的时候要加`salt`盐，加盐的作用是为了防止密码被破解，所以盐也成为加密干扰项，可以是用户的ID+`username`，生成的散列对象是"密码+用户名+ID",这样大大防止了被破解的可能性

+ 一个简单的加密例子
![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191128101124.png)

+ 对应的三个加密方案的结果，当然是用最后一种方案更安全
  ![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191128101252.png)

 使用自定义`realm`+加密操作其实很简单，和之前的差不多一致
  ```java

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
  ```
  + 第二步： 使用了自定义`Realm`
  + 第三步：创建了凭证器传递给`Realm`然后`Realm`会将相应的算法应用在`token`的密码上，然后存在`SimpleAuthenticationInfo`对象中的数据库中的密码进行匹配。
  + 说白了就是将`token`中的明文密码。加密和数据库进行匹配。
  + 值得注意的是，在自定义`Realm`中，包装`Info`对象的时候要传递加密所需的盐
  
  ![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191128102222.png)

## 3. 授权模块
### 3.1 RBAC介绍
`RBAC`就是基于角色的权限访问控制(`Role-Based Access Control`)
+  张三下载高清无码的电影需要vip
+  张三：用户
+  下载：权限
+  vip：角色

### 3.2 授权流程
![](https://raw.githubusercontent.com/Pig9ychen297/Picture/master/data/20191128104101.png)

### 3.3 授权方式(三种)
+ 3.3.1 编程方式：通过`if/else`完成授权
  ```java
  if(subject.hasRole("admin"){
    //有权限
  }else{
    //没权限
  }
  ```
+ 3.3.2 注解方式：在执行方法之前需要有相应权限
  ```java
  @RequireRoles("admin")
  public void hello(){
    ......
    ......
  }
  ```

+ 3.3.3 JSP标签方式：在页面中，根据不同的权限，显示不同的页面内容，比如，同一个页面，管理员有删除和添加按钮，而用户没有
  ```jsp
  <shiro:hasRole name="admin">
  <!-- 有权限 -->
  </shiro:hasRole>
  ```


### 3.4 判断是否拥有角色身份
> ps 判断的前提应该是用户登录之后才判断是否拥有角色身份
+ 配置文件`shiro-permission.ini`文件内容如下：
```ini
[users]
#zhansan有role1 和role2两个角色
zhangsan= 666,role1,role2
lisi=888

[roles]
#role1有创建和更新的权限
role1=user:create,user:update
role2=user:create,user:delete
role3=user:create

```

有**两种**方式进行判断:
+ 判断是否有角色，有就返回`true`,没有就返回`false`

+ 判断是否拥有角色，有的话无返回值，没有的话直接报`UnauthorizedException`异常
  ```java
  public class PermissionTest {
    private static Log log = LogFactory.getLog(ShiroTest.class);

    @Test
    public void testPermission(){
    //        判断用户是否拥有权限之前必须是在用户登录之后判断
        DefaultSecurityManager dsm = new DefaultSecurityManager();
        IniRealm ir = new IniRealm("classpath:shiro-permission.ini");
        dsm.setRealm(ir);
        SecurityUtils.setSecurityManager(dsm);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan", "666");
        try {
            subject.login(token);

        } catch (UnknownAccountException e) {
            log.info("用户不存在");
        } catch (IncorrectCredentialsException e1) {
            log.info("密码不正确");
        }

    //        8. 判断是否认证成功
        boolean b = subject.isAuthenticated();
        if (b){
    //            表示认证登录成功
    //            第一种
    //            判断用户是否存在某个角色，返回true表示有，false表示没有
            log.info(subject.hasRole("role1"));
            log.info(subject.hasAllRoles(Arrays.asList("role1","role2")));
            log.info(Arrays.toString(subject.hasRoles(Arrays.asList("role1","role2","role3"))));

    //           第二种，如果有角色，无返回值，没有角色，直接报错
            subject.checkRole("role1");
            log.info("role1角色存在");
            subject.checkRoles("role1","role2","role3");
            log.info("走到这里说明三个角色都有");
        }
    }
  }
  ```
  + 判断用户流程
    **首先加载配置文件将用户信息加载进来，进行登录，判断里面登录的`zhangsan`用户拥有的角色,值得注意的是`hasRoles`方法返回的是一个`boolean`类型的数组，`checkRole`系列方法，无返回值，判断不到角色就报错**

### 3.5 判断是否拥有权限
  和判断是否有角色身份很相似，直接贴代码了

  ```java 
    //            登录之后判断用户是否拥有权限
            log.info(subject.isPermitted("user:delete"));
    //            返回true表示全部拥有权限，false表示不全部有用
            log.info(subject.isPermittedAll("user:update","user:delete"));
    //            返回一个boolean数组
            log.info(Arrays.toString(subject.isPermitted("user:create","user:update","user:delete")));

    //            第二种是没有权限抛出异常
            try{
                subject.checkPermission("user:update");
                log.info("拥有更新权限");
                subject.checkPermission("user:select");
                log.info("走到这里说明拥有查询权限");
            }catch (UnauthorizedException e){
                log.info("没有拥有相应的权限");
            }

  ```
  
















