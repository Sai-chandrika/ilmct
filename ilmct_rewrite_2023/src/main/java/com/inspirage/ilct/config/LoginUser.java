package com.inspirage.ilct.config;

import com.inspirage.ilct.documents.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class LoginUser extends org.springframework.security.core.userdetails.User {
  private final User user;

  public LoginUser(User user) {
    super(user.getUserId(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public String getRole() {
    return user.getRole().toString();
  }
  public String getLangauge(){
    return user.getPreferredLanguage();
  }
}
