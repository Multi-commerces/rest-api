package fr.commerces.microservices.product.categorie;

import static fr.commerces.commons.utilities.UtilityTest.CATEGORY_CREATED_20000001;
import static fr.commerces.commons.utilities.UtilityTest.CATEGORY_CREATED_20000002;
import static fr.commerces.commons.utilities.UtilityTest.CATEGORY_CREATED_20000003;
import static fr.commerces.commons.utilities.UtilityTest.CATEGORY_CREATED_20000004;
import static fr.commerces.commons.utilities.UtilityTest.CATEGORY_ID_20000001;
import static fr.commerces.commons.utilities.UtilityTest.CATEGORY_ID_20000002;
import static fr.commerces.commons.utilities.UtilityTest.CATEGORY_ID_20000003;
import static fr.commerces.commons.utilities.UtilityTest.CATEGORY_ID_20000004;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.LanguageCode;

import fr.commerces.microservices.product.categories.CategoryData;
import fr.commerces.microservices.product.categories.CategoryManager;
import fr.commerces.microservices.product.categories.CategorySubCategoriesPairData;
import fr.commerces.microservices.product.entity.Category;
import fr.commerces.microservices.product.entity.CategoryLang;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CategoryManagerTest {

	@Inject
	CategoryManager manager;

	/**
	 * Vérification de l'arbre hiérarchique des catégories
	 */
	@Test
	public void testFindCategoryHierarchy() {
		// Execution -----------------------------------------
		final Map<Long, CategorySubCategoriesPairData> categories = manager.findCategoryHierarchy();

		// Verification --------------------------------------
		assertNotNull(categories);
		assertThat(categories.size(), is(3));

		CategorySubCategoriesPairData categorySubCategories;

		// CATEGORY_ID_20000001
		categorySubCategories = categories.get(CATEGORY_ID_20000001);
		assertNotNull(categorySubCategories);
		assertThat(categorySubCategories.getCategory().getCreated(), is(CATEGORY_CREATED_20000001));

		// CATEGORY_ID_20000002
		categorySubCategories = categories.get(CATEGORY_ID_20000002);
		assertNotNull(categorySubCategories);
		assertThat(categorySubCategories.getCategory().getCreated(), is(CATEGORY_CREATED_20000002));

		// CATEGORY_ID_20000003
		categorySubCategories = categories.get(CATEGORY_ID_20000003);
		assertNotNull(categorySubCategories);
		assertThat(categorySubCategories.getCategory().getCreated(), is(CATEGORY_CREATED_20000003));

		final Map<Long, CategorySubCategoriesPairData> subCategories = categorySubCategories.getSubCategories();
		assertThat(subCategories.size(), is(1));
		assertNotNull(subCategories.get(CATEGORY_ID_20000004));
	}

	@Test
	public void testFindById() {
		// Execution -----------------------------------------
		final CategorySubCategoriesPairData categorySubCategories = manager.findById(CATEGORY_ID_20000003);

		// Verification --------------------------------------
		assertNotNull(categorySubCategories);

		final Map<Long, CategorySubCategoriesPairData> subCategories = categorySubCategories.getSubCategories();
		assertThat(subCategories.size(), is(1));
		assertNotNull(subCategories.get(CATEGORY_ID_20000004));
	}

	@Test
	@TestTransaction
	public void testupdate() throws ParseException, InterruptedException {

		LocalDateTime dateUpdate = LocalDateTime.now();
		String nameUpdate = "NAME UPDATE";
		String descUpdate = "DESCRIPTION UPDATE";
		int positionUpdate = 2;
		boolean displayedUpdate = false;

		// Preparation --------------------------------------
		final CategoryData data = new CategoryData();
		data.setName(nameUpdate);
		data.setDescription(descUpdate);
		data.setPosition(positionUpdate);
		data.setDisplayed(displayedUpdate);
		data.setCreated(LocalDateTime.now()); // Ignore
		data.setUpdated(LocalDateTime.now()); // Ignore

		// Execution -----------------------------------------
		manager.update(CATEGORY_ID_20000004, LanguageCode.fr, data);

		// Verification --------------------------------------
		final CategoryLang categoryLang = CategoryLang.findByCategoryLangPK(CATEGORY_ID_20000004, LanguageCode.fr).get();
		final Category category = categoryLang.getCategory();
		assertTrue(dateUpdate.isBefore(category.getUpdated()), "La date de mise semble ne pas être mise à jour");

		assertThat(categoryLang.getName(), is(nameUpdate));
		assertThat(categoryLang.getDescription(), is(descUpdate));
		assertThat(category.getCreated(), is(CATEGORY_CREATED_20000004));
		assertThat(category.getPosition(), is(positionUpdate));
		assertThat(category.isDisplayed(), is(displayedUpdate));
	}

	@Test
	@TestTransaction
	public void testDeleteChildrenCategory() {
		// Execution -----------------------------------------
		manager.delete(CATEGORY_ID_20000004);

		// Verification --------------------------------------
		// N'existe plus
		assertNull(Category.findById(CATEGORY_ID_20000004)); 
		
		// Aucune autre catégorie en moins
		assertThat(Category.listAll().size(), is(3)); 
		
		// La traduction ne peut exister sans la catégorie
		assertThat(CategoryLang.listAll().size(), is(3)); 

		// Ne fait plus partie des enfants de CATEGORY_ID_20000003
		final Category entity = Category.findById(CATEGORY_ID_20000003);
		assertThat(entity.getChildrenCategory().size(), is(0)); 
	}

	@Test
	@TestTransaction
	public void testCreateChildrenCategory() {
		LocalDateTime toDay = LocalDateTime.now();

		// Preparation --------------------------------------
		final CategoryData data = new CategoryData();
		data.setName("BIDON NAME");
		data.setDescription("BIDON DESC");
		data.setPosition(2);
		data.setDisplayed(true);
		data.setCreated(toDay); // Ignore
		data.setUpdated(toDay); // Ignore

		// Execution -----------------------------------------
		// parentCategory not null lors de l'enregistrement
		manager.createCategory(CATEGORY_ID_20000003, data);

		// Verification --------------------------------------
		final List<Category> categories = Category.findCategoryHierarchy().list();
		assertThat(categories.size(), is(3)); 
		
		final Category category = Category.findById(CATEGORY_ID_20000003);
		assertThat(category.getChildrenCategory().size(), is(2));

	}
	
	@Test
	@TestTransaction
	public void testCreateRootCategory() {
		LocalDateTime toDay = LocalDateTime.now();

		// Preparation --------------------------------------
		final CategoryData data = new CategoryData();
		data.setName("BIDON NAME");
		data.setDescription("BIDON DESC");
		data.setPosition(2);
		data.setDisplayed(true);
		data.setCreated(toDay); // Ignore
		data.setUpdated(toDay); // Ignore

		// Execution -----------------------------------------
		// parentCategory is null lors de l'enregistrement
		manager.createCategory(null, data);

		// Verification --------------------------------------
		final List<Category> categories = Category.findCategoryHierarchy().list();
		assertThat(categories.size(), is(4)); 
	}

}