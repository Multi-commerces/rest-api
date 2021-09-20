package fr.commerces.microservices.catalog.products.manager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import com.neovisionaries.i18n.LanguageCode;

import fr.commerces.commons.logged.ManagerInterceptor;
import fr.commerces.microservices.catalog.products.entity.Product;
import fr.commerces.microservices.catalog.products.entity.ProductLang;
import fr.commerces.microservices.catalog.products.mapper.ProductBasicMapper;
import fr.webmaker.microservices.catalog.products.data.ProductBasicData;

@ManagerInterceptor
@ApplicationScoped
public class ProductBasicManager {

	@Inject
	ProductBasicMapper mapper;

	/**
	 * Otenir un produit (données de base) dans une langue
	 * 
	 * @param productId    identifiant
	 * @param languageCode code langue
	 * @return
	 * @throws NotFoundException Produit non trouvé
	 */
	public final ProductBasicData findProductBasicByProductAndLang(@NotNull final Long productId,
			@NotNull final LanguageCode languageCode) throws NotFoundException {
		
		return ProductLang.findByIdProductAndLanguageCode(productId, languageCode)
				.map(mapper::toData)
				.orElseThrow(NotFoundException::new);
	}

	/* ################ Opérations de mise à jour ################ */

	/**
	 * Opération de mise à jour de base d'un produit traduit dans une langue
	 * 
	 * @param language  langue
	 * @param productId identifiant technique du produit
	 * @param data      données produit
	 * @throws NotFoundException produit introuvable
	 */
	@Transactional
	public final void updateProductBasic(@NotNull final LanguageCode language, @NotNull final Long productId,
			@NotNull @Valid final ProductBasicData data) throws NotFoundException {
		/*
		 * MAJ PRODUIT
		 */
		Product.<Product>findByIdOptional(productId).map(pojo -> mapper.toProduct(data, pojo))
				.orElseThrow(NotFoundException::new);

		/*
		 * MAJ PRODUIT LANG
		 */
		ProductLang.findByIdProductAndLanguageCode(productId, language).map(pojo -> mapper.toProductLang(data, pojo))
				.orElseThrow(NotFoundException::new);
	}

}