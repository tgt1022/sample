package com.jiggy.sample.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Form based authentication realm.
 * 
 * Created on Sept 1, 2012
 * 
 * @author jmalkan
 * @version $Revision$
 */
public class UserRealm extends AuthorizingRealm {
  private static final Logger logger = LoggerFactory.getLogger(UserRealm.class);
  @Autowired private ApplicationContext context;
  @Autowired private UserService userService;
  @Autowired private UserCredentialsService userCredentialsService;
  
  
  /**
   * Creates a new instance of com.jiggy.sample.security.UserRealm.java and Performs Initialization
   */
  public UserRealm() {
    logger.info("Creates a new instance of com.jiggy.sample.security.UserRealm.java and Performs Initialization");
    setAuthenticationTokenClass(UsernamePasswordToken.class);
  }


  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    logger.info("begin doGetAuthorizationInfo");
//    UserProfile userProfile = SessionUtil.getUserProfile();
//    UserPrincipal userPrincipal = (UserPrincipal) getAvailablePrincipal(principals);
//    
//    if (userProfile == null) {
//      User user = this.userService.findById(userPrincipal.getId());
//      userProfile = new UserProfile(user);
//    }
    
    Set<String> roles = new HashSet<String>();
    Set<Permission> permissions = new HashSet<Permission>();
    Set<org.apache.shiro.authz.Permission> shiroPermissions = new HashSet<org.apache.shiro.authz.Permission>();
    
//    for (Role role : userProfile.getUser().getRoles()) {
//      roles.add(role.getName());
//      
//      for (Permission permission : role.getPermissions()) {
//        permissions.add(permission);
//      }
//    }

    roles.add("ADMIN");
    permissions.add(new Permission("todos", "read"));
    
    for (Permission permission : permissions) {
      shiroPermissions.add(new WildcardPermission(permission.getPermissionValue()));
    }
    
    SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo(roles);
    
    authInfo.setObjectPermissions(shiroPermissions);
    
    return authInfo;
  }
  
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
    logger.info("begin doGetAuthenticationInfo getCredentialsMatcher {}", getCredentialsMatcher());
    
    User user = null;
    UserPrincipal userPrincipal = null;
    SimpleAuthenticationInfo simpleAuthenticationInfo = null;
//    UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
    
//    SearchCriteria userCredSearchCriteria = new DefaultSearchCriteria();
//    userCredSearchCriteria.addFilter("userName", usernamePasswordToken.getUsername());
    UserCredentials userCredentials = null; //userCredentialsService.findOne(userCredSearchCriteria);
    logger.warn("userCredentials=", userCredentials);
    
    if (userCredentials == null) {
      String cred = "admin";
      Sha256Hash sha256Hash = new Sha256Hash(cred);
      logger.info("password={}", sha256Hash.toHex());
      
      userCredentials = new UserCredentials(sha256Hash.toHex(), Boolean.FALSE, user);
    }
    
    if (userCredentials != null) {
      logger.warn("Validating user credential against Credentials.");
      user = userCredentials.getUser();
      
      userPrincipal = new UserPrincipal(1l);
//      userPrincipal = new UserPrincipal(user.getId());
      
      simpleAuthenticationInfo = new SimpleAuthenticationInfo(userPrincipal, userCredentials.getPassword(), UserRealm.class.getSimpleName());
    }
    
    return simpleAuthenticationInfo;
  }
}