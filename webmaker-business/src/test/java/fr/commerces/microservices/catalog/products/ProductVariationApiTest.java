package fr.commerces.microservices.catalog.products;

import static fr.commerces.commons.utilities.UtilityTest.PRODUCT_ID_10000001;
import static fr.commerces.commons.utilities.UtilityTest.PRODUCT_ID_BIDON;

import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.models.PathItem.HttpMethod;
import org.junit.jupiter.api.Test;

import fr.commerces.commons.abstracts.AbtractQuarkusApiTest;
import fr.commerces.microservices.catalog.products.relationships.variations.ProductVariationApi;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;

@TestHTTPEndpoint(ProductVariationApi.class)
@QuarkusTest
public class ProductVariationApiTest extends AbtractQuarkusApiTest {

	@Test
	@TestSecurity(authorizationEnabled = false)
	public void testGetProductByIdEndpoint_StatusCode404() {
		putPathParam("productId", PRODUCT_ID_BIDON);
		testEndpoint(HttpMethod.GET, Status.OK);
	}

	@Test
	@TestSecurity(authorizationEnabled = false)
	public void testGetProductByIdEndpoint_OK() {
		putPathParam("productId", PRODUCT_ID_10000001);
		testEndpoint(HttpMethod.GET, Status.OK);
	}

}