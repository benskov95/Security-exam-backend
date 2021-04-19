package dto;

import entities.Post;
import java.util.Date;

public class PostDTO {
    
    private int id;
    private String user;
    private String content;
    private Date postedOn;
    private int threadId;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.user = post.getUser().getUsername();
        this.content = post.getContent();
        this.threadId = post.getCatThread().getId();
        this.postedOn = post.getPostedOn();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
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

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }
    
}
