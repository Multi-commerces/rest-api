package fr.commerces.microservices.catalog.products.openapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import fr.webmaker.commons.identifier.LangID;
import fr.webmaker.commons.identifier.LongID;
import fr.webmaker.commons.request.PageRequest;
import fr.webmaker.commons.response.CollectionResponse;
import fr.webmaker.microservices.catalog.categories.data.CategoryLangData;
import fr.webmaker.microservices.catalog.products.data.ProductLangData;

/**
 * Interface resource API pour les produits
 * @author Julien ILARI
 *
 */
@Path(ProductResourceApi.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Resource Produits", description = "Resource de gestion des produits")
public interface ProductResourceApi {

	public static final String PATH = "/products";
	
	@Path("/{productId}")
	@GET
	@Operation(operationId = "getProductById", summary = "Recherche un produit", description = "Retourne les informations du produit.")
	@Tag(ref = "Resource Produits")
	@APIResponses(value = { 
			@APIResponse(responseCode = "200", description = "[OK] - Opération de recherche effectuée avec succès"),
			@APIResponse(responseCode = "404", description = "[NOK] - Aucun porduit trouvé avec les paramètres fournis") 
	})
	Response getProductById(
			/*
			 * Langue @HeaderParam(HttpHeaders.CONTENT_LANGUAGE) String contentLanguage
			 */
			@Parameter(description = "Langue du produit") 
			@QueryParam("languageCode") 
			@DefaultValue("fr") 
			@NotNull String languageCode,
			/*
			 * Identifiant
			 */
			@Parameter(description = "Identifiant du produit") 
			@PathParam("productId") Long id,
			/*
			 * language
			 */
			@Parameter(description = "Permet d'indiquer d'inclure seulement les informations de base du produit") 
			@QueryParam("includeBasic") 
			@DefaultValue("true") 
			Boolean includeBasic
			);

	/* ############################################################################################################# */

	@GET
	@Path("/") 
	@Operation(operationId = "getProducts", summary = "Recherche les produits", 
		description = "Retourne les informations des produits dans un langue précise (par défaut celle du client).")
	@Tag(ref = "Resource Produits")
	@APIResponses(value = { 
			@APIResponse(responseCode = "200", description = "[OK] - Opération de recherche effectuée avec succès"),
			@APIResponse(responseCode = "404", description = "[NOK] - Aucun porduit trouvé avec les critères de recherche") 
	})
	CollectionResponse<ProductLangData, LangID> getProducts(
			/*
			 * language
			 */
			@Parameter(description = "Langue des produits (langue par défaut 'français')") 
			@QueryParam("languageCode") 
			@DefaultValue("fr") 
			@NotNull
			String languageCode,
			@BeanParam @Valid PageRequest page);

	/* ############################################################################################################# */

//	@RolesAllowed({ "gestionnaire" })
	@POST
	@Path("/{languageCode}") 
	@Operation(operationId = "createProduct", summary = "Création d'un produit", description = "Opération de création d'un nouveau produit .")
	@Tag(ref = "Resource Produits")
	@APIResponses(value = { 
			@APIResponse(responseCode = "201", description = "[OK] - Opération de création effectuée avec succès") 
	})
	Response createProductLang(
			/*
			 * language
			 */
			@Parameter(description = "Code de la langue") 
			@PathParam("languageCode") 
			@DefaultValue("fr") String languageCode,
			/*
			 * Product Data
			 */
			@NotNull @Valid ProductLangData data);

	/* ############################################################################################################# */
	
	@PUT
	@Path("/{productId}")
	@Operation(operationId = "updateProduct", summary = "Modification d'un produit", description = "Opération de modification d'un produit existant.")
	@Tag(ref = "Resource Produits")
	@APIResponses(value = { 
			@APIResponse(responseCode = "204", description = "[OK] - Opération de mise à jour effectuée avec succès") 
	})
	Response patchProductLang(
			/*
			 * language
			 */
			@Parameter(description = "Code de la langue") 
			@QueryParam("languageCode") 
			@DefaultValue("fr") String languageCode,
			/*
			 * Identifiant
			 */
			@Parameter(description = "Identifiant du produit") 
			@PathParam(value = "productId") Long productId, 
			/*
			 * Data
			 */
			@NotNull @Valid ProductLangData data);

	
	@DELETE
	@Path("/{productId}")
	@Operation(operationId = "deleteProductLang", summary = "Suppression (douce) d'un produit", description = "Opération de suppression d'un produit existant dans une langue spécifique.")
	@Tag(ref = "Resource Produits")
	@APIResponses(value = { 
			@APIResponse(responseCode = "204", description = "[OK] - Opération de suppression effectuée avec succès"),
			@APIResponse(responseCode = "404", description = "[NOK] - Suppression du produit impossible car introuvable")
	})
	Response deleteProductLang(
			/*
			 * language
			 */
			@Parameter(description = "Code de la langue") 
			@QueryParam("languageCode") 
			@DefaultValue("fr") String languageCode,
			/*
			 * Identifiant
			 */
			@Parameter(description = "Identifiant du produit") 
			@PathParam(value = "productId") Long productId);
	
	
	@Operation(
			operationId = "getCategories", 
			summary = "Recherche les catégories du produit", 
			description = "Retourne les informations des catégories.")
	@Path("/{productId}/categories")
	@GET
	CollectionResponse<CategoryLangData, LongID> getCategories(@PathParam("productId") @NotNull Long productId);
	

}
