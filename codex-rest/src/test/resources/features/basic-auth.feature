Feature: Security auth can be applied to endpoints

  Background:
    Given initial conditions

  Scenario: A request sent to a secured endpoint with correct credentials returns ok
    Given basic auth user "master" and password "123456"
    When a GET request is sent to "/api/secured/admin"
    Then http status matches 200

  Scenario: A request sent to a secured endpoint without credentials returns status 401 unauthorized
    When a GET request is sent to "/api/secured/admin"
    Then http status matches 401

  Scenario: A request sent to a secured endpoint with incorrect credentials returns status 403
    Given basic auth user "master" and password "bad-password"
    When a GET request is sent to "/api/secured/admin"
    Then http status matches 403

  Scenario: User role needs to match the required values
    Given basic auth user "john" and password "secret"
    When a GET request is sent to "/api/secured/admin"
    Then http status matches 403

  Scenario: Basic user can access a service with a matching role
    Given basic auth user "john" and password "secret"
    When a GET request is sent to "/api/secured/user"
    Then http status matches 200

  Scenario: Admin user can access a service requiring a lower role
    Given basic auth user "master" and password "123456"
    When a GET request is sent to "/api/secured/user"
    Then http status matches 200
