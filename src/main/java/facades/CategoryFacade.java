package facades;

import dto.CategoryDTO;
import entities.Category;
import errorhandling.AlreadyExists;
import errorhandling.NotFound;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public class CategoryFacade {
    
    private static EntityManagerFactory emf;
    private static CategoryFacade instance;

    private CategoryFacade() {
    }

    public static CategoryFacade getCategoryFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new CategoryFacade();
        }
        return instance;
    }
    
    public List<CategoryDTO> getAllCategories() {
        EntityManager em = emf.createEntityManager();
        try {
            Query q = em.createQuery("SELECT c FROM Category c");
            List<Category> cats = q.getResultList();
            List<CategoryDTO> catDTOs = new ArrayList<>();

            for (Category cat : cats) {
                catDTOs.add(new CategoryDTO(cat));
            }
            return catDTOs;
        } finally {
            em.close();
        }
    }
    
    public CategoryDTO addCategory(CategoryDTO catDTO) throws AlreadyExists {
        EntityManager em = emf.createEntityManager();
        checkIfExists(em, catDTO);
        Category cat = new Category(catDTO.getName());
        try {
            em.getTransaction().begin();
            em.persist(cat);
            em.getTransaction().commit();
            return new CategoryDTO(cat);
        } finally {
            em.close();
        }
    }
    
    public CategoryDTO deleteCategory(int id) throws NotFound {
        EntityManager em = emf.createEntityManager();
        Category cat = em.find(Category.class, id);
        isNotNull(cat);
        
        try {
            em.getTransaction().begin();
            em.remove(cat);
            em.getTransaction().commit();
            return new CategoryDTO(cat);
        } finally {
            em.close();
        }
    }
    
    public CategoryDTO updateCategory(CategoryDTO catDTO) throws NotFound {
        EntityManager em = emf.createEntityManager();
        Category cat = em.find(Category.class, catDTO.getId());
        isNotNull(cat);
        cat.setName(catDTO.getName());
        try {
            em.getTransaction().begin();
            em.persist(cat);
            em.getTransaction().commit();
            return new CategoryDTO(cat);
        } finally {
            em.close();
        }
    }
    
    public void checkIfExists(EntityManager em, CategoryDTO catDTO) throws AlreadyExists {
        Query q = em.createQuery("SELECT c FROM Category c WHERE c.name = :name");
        q.setParameter("name", catDTO.getName());
        
        if (q.getResultList().size() > 0) {
            throw new AlreadyExists("This category already exists.");
        }
    }
    
    public void isNotNull(Category cat) throws NotFound {
        if (cat == null) {
            throw new NotFound("The category with the provided ID does not exist.");
        }
    }
    
}

