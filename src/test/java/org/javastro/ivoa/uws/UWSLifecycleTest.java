package org.javastro.ivoa.uws;


/*
 * Created on 23/01/2026 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
public class UWSLifecycleTest {
   private static final String JOBS_ENDPOINT = "/jobs/";

   @Test
   public void testFullJobLifecycle() {
      // 1. Create a Job (POST to /jobs)
      // Note: UWS usually returns 303 See Other with Location header
      Response createResponse = given()
            .formParam("jdl", "value1")
            .redirects().follow(false)
            .when()
            .post(JOBS_ENDPOINT)
            .then()
            .statusCode(303)
            .header("Location", notNullValue())
            .extract().response();

      String jobFullUrl = createResponse.getHeader("Location");
      String jobId = jobFullUrl.substring(jobFullUrl.lastIndexOf('/') + 1);

      System.out.println(jobFullUrl);
      String jobUrl = JOBS_ENDPOINT+jobId;
      // 2. Verify Initial State (PENDING)
      given()
            .when().get(jobUrl)
            .then()
            .statusCode(200)
            .body("job.phase", equalTo("PENDING"));

      // 3. Start the Job (POST to /phase with PHASE=RUN)
      given()
            .contentType(ContentType.URLENC)
            .redirects().follow(false)
            .formParam("PHASE", "RUN")
            .when()
            .post(jobUrl + "/phase")
            .then()
            .statusCode(303);

      // 4. Poll until COMPLETED using Awaitility
      await().atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .untilAsserted(() -> {
               given()
                     .when().get(jobUrl + "/phase")
                     .then()
                     .statusCode(200)
                     .body(containsString("COMPLETED"));
            });

      // 5. Retrieve Results
      given()
            .when().get(jobUrl + "/results")
            .then()
            .log().ifValidationFails(LogDetail.BODY)
            .statusCode(200)
            .body("results.result.size()", greaterThan(0));
   }

   @Test
   public void testAbortJob() {
      // Implementation for POST /phase with PHASE=ABORT
      // Verify state transitions to ABORTED
      fail("Not yet implemented");
   }
}
