/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.dao.login;


import hrms.model.login.UserRole;
import hrms.model.login.Users;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 *
 * @author Surendra
 */
@Service("loginService")
public class LoginServiceImpl implements UserDetailsService {

    
    private LoginDAO loginDao;

    public LoginDAO getLoginDao() {
        return loginDao;
    }

    public void setLoginDao(LoginDAO loginDao) {
        this.loginDao = loginDao;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        Users user = null;
        List<GrantedAuthority> authorities = null;
        try {
            user = loginDao.findByUserName(username);
            authorities = buildUserAuthority(user.getUserRole());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildUserForAuthentication(user, authorities);

    }

    private User buildUserForAuthentication(Users user,
            List<GrantedAuthority> authorities) {
        return new User(user.getUserName(), user.getUserPassword(),
                user.isEnabled(), true, true, true, authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles) {

        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

        // Build user's authorities  
        for (UserRole userRole : userRoles) {
            setAuths.add(new SimpleGrantedAuthority(userRole.getRole()));
        }

        List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(
                setAuths);

        return Result;
    }

}
