package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@NamedQuery (name = "User.deleteAllRows", query = "DELETE FROM User")
@Table(name = "user")
public class User implements Serializable {


  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "user_name", length = 25)
  private String username;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "user_pass")
  private String userPass;
  @JoinTable(name = "user_roles", joinColumns = {
    @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
    @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
  @ManyToMany (cascade = CascadeType.PERSIST)
  private List<Role> roleList = new ArrayList<>();
  
  @OneToMany(mappedBy = "user")
  private List<CatThread> threads = new ArrayList<>();
    
  @OneToMany(mappedBy = "user")
  private List<Post> posts = new ArrayList<>();

  public List<String> getRolesAsStrings() {
    if (roleList.isEmpty()) {
      return null;
    }
    List<String> rolesAsStrings = new ArrayList<>();
    roleList.forEach((role) -> {
        rolesAsStrings.add(role.getRoleName());
      });
    return rolesAsStrings;
  }

  public User() {}

   public boolean verifyPassword(String pw){
       boolean matches = BCrypt.checkpw(pw, this.userPass);
       return(matches);
    }

  public User(String username, String userPass) {
    this.username = username;
    this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt(12));
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String userName) {
    this.username = userName;
  }

  public String getUserPass() {
    return this.userPass;
  }

  public void setUserPass(String userPass) {
    this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt(12));
  }

  public List<Role> getRoleList() {

    return roleList;
  }

  public void setRoleList(List<Role> roleList) {
    this.roleList = roleList;
  }

  public void addRole(Role userRole) {
    roleList.add(userRole);
  }

    public List<CatThread> getThreads() {
        return threads;
    }

    public void setThreads(List<CatThread> threads) {
        this.threads = threads;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

}
