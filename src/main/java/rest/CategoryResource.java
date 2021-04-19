package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CategoryDTO;
import errorhandling.AlreadyExists;
import facades.CategoryFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("category")
public class CategoryResource {


    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final CategoryFacade CAT_FACADE = CategoryFacade.getCategoryFacade(EMF);


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllCategories() {
        List<CategoryDTO> catDTOs = CAT_FACADE.getAllCategories();
        return GSON.toJson(catDTOs);
    }

    @POST
    @RolesAllowed("admin")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addCategory(String category) throws AlreadyExists{
        CategoryDTO catDTO = GSON.fromJson(category, CategoryDTO.class);
        CategoryDTO newCat = CAT_FACADE.addCategory(catDTO);
        return GSON.toJson(newCat);
    }

    @DELETE
    @RolesAllowed("admin")
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteCategory(@PathParam("id") int id) throws errorhandling.NotFound {
        CategoryDTO delCatDTO = CAT_FACADE.deleteCategory(id);
        return GSON.toJson(delCatDTO);
    }

    @PUT
    @RolesAllowed("admin")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String updateCategory(String category) throws errorhandling.NotFound {
        CategoryDTO catDTO = GSON.fromJson(category, CategoryDTO.class);
        CategoryDTO editedDTO = CAT_FACADE.updateCategory(catDTO);
        return GSON.toJson(editedDTO);
    }
}

