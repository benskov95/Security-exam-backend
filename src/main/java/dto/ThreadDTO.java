package dto;

import entities.CatThread;
import entities.Post;
import java.util.ArrayList;
import java.util.List;

public class ThreadDTO {
    
    private int id;
    private String title;
    private List<PostDTO> posts;
    private CategoryDTO category;
    private String user;

    public ThreadDTO(CatThread thread) {
        this.id = thread.getId();
        this.title = thread.getTitle();
        this.posts = postsToDTO(thread.getPosts()); 
        this.category = new CategoryDTO(thread.getCategory());
        this.user = thread.getUser().getUsername();
    }
    
    private List<PostDTO> postsToDTO(List<Post> posts) {
        List<PostDTO> postDTOs = new ArrayList<>();
        for (Post post : posts) {
            postDTOs.add(new PostDTO(post));
        }
        return postDTOs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
}
