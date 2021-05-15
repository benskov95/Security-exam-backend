package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;
import security.errorhandling.AuthenticationException;

@Entity
@NamedQuery (name = "User.deleteAllRows", query = "DELETE FROM User")
@Table(name = "user")
public class User implements Serializable {


  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "email", length = 25)
  private String email;

  @Column(name = "user_name", length = 25)
  private String username;

  @Column(name = "auth", length = 25)
  private String auth;

  @Column(name = "phone", length = 25)
  private String phone;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "user_pass")
  private String userPass;
  @JoinTable(name = "user_role", joinColumns = {
    @JoinColumn(name = "email", referencedColumnName = "email")}, inverseJoinColumns = {
    @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
  @ManyToOne (cascade = CascadeType.PERSIST)
  private Role role;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<CatThread> threads = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Post> posts = new ArrayList<>();

  private String imageUrl;
  
  public User() {}

   public boolean verifyPassword(String pw){
       boolean matches = BCrypt.checkpw(pw, this.userPass);
       return(matches);
    }

  public User(String email, String username, String userPass) {
      this.email = email;
      this.username = username;
      this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt(12));
      this.imageUrl = "https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg";
      this.phone = "None";
      this.auth = "None";
  }

  public void changePw (String oldPW, String newPW) throws AuthenticationException {
      if(BCrypt.checkpw(oldPW, this.userPass)){
          this.userPass = BCrypt.hashpw(newPW, BCrypt.gensalt(12));
      }else{
          throw new AuthenticationException("Old password is incorrect");
      }
  }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getAuth() { return auth; }

    public void setAuth(String auth) { this.auth = auth; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
