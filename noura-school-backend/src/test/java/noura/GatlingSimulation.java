package noura;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Simulation Gatling : tests de charge (task.md FOLDER 12).
 * Lancer : mvn gatling:test -Dgatling.simulationClass=noura.GatlingSimulation
 */
public class GatlingSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private final ScenarioBuilder scn = scenario("Noura School API Load")
            .exec(http("Health").get("/q/health/ready").check(status().is(200)))
            .exec(http("OpenAPI").get("/q/openapi").check(status().is(200)))
            .exec(http("Login")
                    .post("/api/v1/auth/login")
                    .body(StringBody("{\"login\":\"admin\",\"password\":\"Admin123!\"}"))
                    .check(status().is(200)));

    {
        setUp(scn.injectOpen(
                rampUsersPerSec(1).to(10).during(30),
                constantUsersPerSec(10).during(60)
        )).protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile4().lt(3000),
                        global().successfulRequests().percent().gt(90.0)
                );
    }
}
