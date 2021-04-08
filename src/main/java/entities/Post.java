package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    private CatThread catThread;

    @ManyToOne
    private User user;
    
    private String content;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedOn;

    public Post() {
    }

    public Post(CatThread catThread, User user, String content) {
        this.catThread = catThread;
        this.user = user;
        this.content = content;
        this.postedOn = new Date();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CatThread getCatThread() {
        return catThread;
    }

    public void setCatThread(CatThread catThread) {
        this.catThread = catThread;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Date postedOn) {
        this.postedOn = postedOn;
    }

}
