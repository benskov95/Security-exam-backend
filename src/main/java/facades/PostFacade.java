package facades;

import dto.PostDTO;
import entities.CatThread;
import entities.Category;
import entities.Post;
import entities.User;
import errorhandling.InputNotValid;
import errorhandling.NotFound;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import security.errorhandling.AuthenticationException;

public class PostFacade {
    
    private static EntityManagerFactory emf;
    private static PostFacade instance;

    private PostFacade() {
    }

    public static PostFacade getPostFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PostFacade();
        }
        return instance;
    }
    
    public List<PostDTO> getPostsByThreadId(int id) throws NotFound {
        EntityManager em = emf.createEntityManager();
        List<PostDTO> postDTOs = new ArrayList();
        
        try {
            Query q = em.createQuery("SELECT p FROM Post p WHERE p.catThread.id = :id");
            q.setParameter("id", id);
            List<Post> posts = q.getResultList();
            
            if (posts.isEmpty()) {
                throw new NotFound("No thread with this ID.");
            }

            for (Post p : posts) {
                postDTOs.add(new PostDTO(p));
            }
            return postDTOs;
        } finally {
            em.close();
        }
    }

    
    public PostDTO addPost(PostDTO postDTO, String email) throws NotFound, InputNotValid {
        EntityManager em = emf.createEntityManager();
        
        if (postDTO.getContent().length() < 1) {
            throw new InputNotValid("Post must consist of minimum 1 character.");
        }
        
        User user = em.find(User.class, email);
        CatThread thread = em.find(CatThread.class, postDTO.getThreadId());
        threadExists(thread);
        
        Post post = new Post(thread, user, postDTO.getContent());
        thread.getPosts().add(post);
        
        try {
            em.getTransaction().begin();
            em.persist(post);
            em.getTransaction().commit();
            return new PostDTO(post);
        } finally {
            em.close();
        }
    }
    
    public PostDTO deleteMyPost(int postId, String email) throws AuthenticationException, NotFound {
        EntityManager em = emf.createEntityManager();
        Post post = em.find(Post.class, postId);
        postExists(post);
        verifyPostOwnership(post.getUser().getEmail(), email, "delete");
        
        CatThread thread = post.getCatThread();
        thread.getPosts().remove(post);
        
        try {
            em.getTransaction().begin();
            em.remove(post);
            em.getTransaction().commit();
            return new PostDTO(post);
        } finally {
            em.close();
        }
    }
    
    public PostDTO deletePost(int postId) throws NotFound {
        EntityManager em = emf.createEntityManager();
        Post post = em.find(Post.class, postId);
        postExists(post);
        
        CatThread thread = post.getCatThread();
        thread.getPosts().remove(post);
        
        try {
            em.getTransaction().begin();
            em.remove(post);
            em.getTransaction().commit();
            return new PostDTO(post);
        } finally {
            em.close();
        }
    }
    
    public PostDTO editMyPost(PostDTO postDTO, String email) throws NotFound, AuthenticationException, InputNotValid {
        EntityManager em = emf.createEntityManager();
        
        if (postDTO.getContent().length() < 1) {
            throw new InputNotValid("Post must consist of minimum 1 character.");
        }
        
        Post post = em.find(Post.class, postDTO.getId());
        postExists(post);
        verifyPostOwnership(post.getUser().getEmail(), email, "edit");
        
       try {
           em.getTransaction().begin();
           post.setContent(postDTO.getContent());
           em.persist(post);
           em.getTransaction().commit();
           return new PostDTO(post);
       } finally {
           em.close();
       }
    }
    
    public void postExists(Post post) throws NotFound {
        if (post == null) {
            throw new NotFound("No post with this ID.");
        }
    }
    
    public void threadExists(CatThread thread) throws NotFound {
        if (thread == null) {
            throw new NotFound("No thread with this ID.");
        }
    }
    
    public void verifyPostOwnership(String postEmail, String userEmail, String operation) throws AuthenticationException {
        if (!postEmail.equals(userEmail)) {
            throw new AuthenticationException("Something went wrong. Could not " + operation + " post.");
        }
    }

}