package thewolf

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class TeleSalesPut extends Simulation {

val scn = scenario("telesales scenario")
          .exec(http("telesales")
            .put("http://10.234.34.134:8080/orders/0/release"))

setUp(scn.inject(
rampUsersPerSec(1) to(10) during(10), 
constantUsersPerSec(10) during(10)))
}