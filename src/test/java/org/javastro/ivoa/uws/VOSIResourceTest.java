package org.javastro.ivoa.uws;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class VOSIResourceTest {
    @Test
    void testCapabilitiesEndpoint() {
        given()
          .when().get("/VOSI/capabilities")
          .then()
             .statusCode(200)
             ;//TODO test return
    }

}