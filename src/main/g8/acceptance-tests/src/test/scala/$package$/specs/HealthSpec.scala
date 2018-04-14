package $package$.specs

import $package$.client.ServiceClient

class HealthSpec extends BaseSpec {

  feature("Application Health Check") {

    info("In order to ensure the service is healthy")
    info("As a Dev Ops engineer")
    info("I want to be able to query the application health")

    scenario("Application Health can be checked from response code") {
      Given("the service is running")
      When(s"we send a GET request to the health endpoint \${ServiceClient.HealthEndpoint}")
      val Right((resp, _)) = ServiceClient.applicationHealthCheck

      Then("the response code should be 200")
      assert(resp.is200)
    }

    scenario("Application Uptime is exposed") {
      Given("the Color service is running")
      When(s"we send a GET request to the health endpoint \${ServiceClient.HealthEndpoint}")
      val Right((_, healthState)) = ServiceClient.applicationHealthCheck

      Then("the response shows the length of time the server has been up")
      healthState.is_healthy shouldBe true
    }
  }
}
