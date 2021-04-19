package facades;

import dto.CategoryDTO;
import dto.ThreadDTO;
import entities.CatThread;
import entities.Category;
import entities.Post;
import entities.User;
import errorhandling.AlreadyExists;
import errorhandling.NotFound;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import security.errorhandling.AuthenticationException;

public class ThreadFacade {
    
    private static EntityManagerFactory emf;
    private static ThreadFacade instance;

    private ThreadFacade() {
    }

    public static ThreadFacade getThreadFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ThreadFacade();
        }
        return instance;
    }
    
    public List<ThreadDTO> getAllThreadsByCatId(int catId) throws NotFound {
        EntityManager em = emf.createEntityManager();
        List<ThreadDTO> threadDTOs = new ArrayList();
        
        try {
            Query q = em.createQuery("SELECT t FROM CatThread t WHERE t.category.id = :id");
            q.setParameter("id", catId);
            List<CatThread> threads = q.getResultList();
            
            if (threads.isEmpty()) {
                throw new NotFound("Could not find any threads with this category ID.");
            }

            for (CatThread c : threads) {
                threadDTOs.add(new ThreadDTO(c));
            }
            return threadDTOs;
        } finally {
            em.close();
        }
    }
    
    public ThreadDTO addThread(ThreadDTO threadDTO, String email) throws NotFound, AlreadyExists {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, email);
        Category cat = em.find(Category.class, threadDTO.getCategory().getId());
        categoryExists(cat);
        checkIfExists(em, threadDTO);
        
        CatThread thread = new CatThread(threadDTO.getTitle(), user, cat);
        Post post = new Post(thread, user, threadDTO.getPosts().get(0).getContent());
        thread.getPosts().add(post);
        
        try {
            em.getTransaction().begin();
            em.persist(thread);
            em.getTransaction().commit();
            return new ThreadDTO(thread);
        } finally {
            em.close();
        }
    }
    
    public ThreadDTO deleteMyThread(int threadId, String email) throws AuthenticationException, NotFound {
        EntityManager em = emf.createEntityManager();
        CatThread thread = em.find(CatThread.class, threadId);
        threadExists(thread);
        verifyThreadOwnership(thread.getUser().getEmail(), email, "delete");
        
        try {
            em.getTransaction().begin();
            em.remove(thread);
            em.getTransaction().commit();
            return new ThreadDTO(thread);
        } finally {
            em.close();
        }
    }
    
    public ThreadDTO deleteThread(int threadId) throws NotFound {
        EntityManager em = emf.createEntityManager();
        CatThread thread = em.find(CatThread.class, threadId);
        threadExists(thread);
        
        try {
            em.getTransaction().begin();
            em.remove(thread);
            em.getTransaction().commit();
            return new ThreadDTO(thread);
        } finally {
            em.close();
        }
    }
    
    public ThreadDTO editMyThread(ThreadDTO threadDTO, String email) throws NotFound, AuthenticationException, AlreadyExists {
        EntityManager em = emf.createEntityManager();
        CatThread thread = em.find(CatThread.class, threadDTO.getId());
        Category cat = em.find(Category.class, threadDTO.getCategory().getId());
        threadExists(thread);
        categoryExists(cat);
        verifyThreadOwnership(thread.getUser().getEmail(), email, "edit");
        
        if (!threadDTO.getTitle().equals(thread.getTitle())) {
            checkIfExists(em, threadDTO);
        }
        
       try {
           em.getTransaction().begin();
           thread.setTitle(threadDTO.getTitle());
           em.persist(thread);
           em.getTransaction().commit();
           return new ThreadDTO(thread);
       } finally {
           em.close();
       }
    }
    
    public void threadExists(CatThread thread) throws NotFound {
        if (thread == null) {
            throw new NotFound("No thread with this ID.");
        }
    }
    
    public void categoryExists(Category cat) throws NotFound {
        if (cat == null) {
            throw new NotFound("No category with this ID.");
        }
    }
    
    public void verifyThreadOwnership(String threadEmail, String userEmail, String operation) throws AuthenticationException {
        if (!threadEmail.equals(userEmail)) {
            throw new AuthenticationException("Something went wrong. Could not " + operation + " thread.");
        }
    }
    
      public void checkIfExists(EntityManager em, ThreadDTO threadDTO) throws AlreadyExists {
        Query q = em.createQuery("SELECT t FROM CatThread t WHERE t.title = :title AND t.category.id = :id");
        q.setParameter("title", threadDTO.getTitle()).setParameter("id", threadDTO.getCategory().getId());
        
        if (q.getResultList().size() > 0) {
            throw new AlreadyExists("A thread with this title already exists in this category.");
        }
    }
    
}
