package fr.commerces.microservices.authentification.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.neovisionaries.i18n.LanguageCode;

import fr.commerces.commons.mapper.DefaultMappingConfig;
import fr.commerces.microservices.catalog.categories.Category;
import fr.commerces.microservices.catalog.categories.CategoryLang;
import fr.webmaker.commons.identifier.LangID;
import fr.webmaker.microservices.catalog.categories.data.CategoryData;
import fr.webmaker.microservices.catalog.categories.data.CategoryHierarchyData;

@ApplicationScoped
@Mapper(config = DefaultMappingConfig.class)
public abstract class CustomerMapper {

	/*
	 * Mapper pour opération de création
	 */

	public abstract Category toEntity(CategoryData data);

	@Mapping(target = "category", source = ".")
	public abstract CategoryLang toEntityLang(CategoryData data);

	/*
	 * Mapper pour opération de mise à jour
	 */

	public abstract CategoryData toData(Category entity);
	
	@Mapping(target = ".", source = "category")
	public abstract CategoryData toData(CategoryLang entity);

	@Mapping(target = "category", source = ".")
	public abstract CategoryLang toEntity(CategoryData data, @MappingTarget CategoryLang entity);

	@Mapping(target = "updated", ignore = true)
	@Mapping(target = "created", ignore = true)
	public abstract Category toEntity(CategoryData data, @MappingTarget Category entity);

	/*
	 * Mapper pour opération de lecture
	 */
	public CategoryHierarchyData toCategoryHierarchyData(CategoryLang entity)
	{
		final CategoryHierarchyData categoryHierarchyData = new CategoryHierarchyData(
				new LangID(entity.getId(), entity.getLang()), // identifier
				toData(entity), // category
				subCategories(entity)); // subCategories
		

		categoryHierarchyData.setSubCategories(subCategories(entity));
		
		return categoryHierarchyData;
	}


	private List<CategoryHierarchyData> subCategories(final CategoryLang entity) {
		
		final List<CategoryHierarchyData> subCategories = new ArrayList<>();
		if (CollectionUtils.isEmpty(entity.getChildrenCategory())) {
			return subCategories;
		}
		
		final LanguageCode lang = entity.getLang();
		
		// Pour chacune des sous-catégories
		entity.getChildrenCategory().forEach(root -> {
			// Traitement (la langue doit exister)
			root.getCategoryLang().stream()
				.filter(o -> o.getLang().equals(lang))
				.findAny()
				.ifPresent(rootLang -> {
					CategoryHierarchyData c = toCategoryHierarchyData(rootLang);
					subCategories.add(c);
			});
		});

		return subCategories;
	}

}