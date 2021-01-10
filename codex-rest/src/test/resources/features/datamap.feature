Feature: The endpoint 'datamap' responds to basic CRUD operations

  Background:
    Given initial conditions

  Scenario: Requesting all values when nothing has been stored returns an empty response
    When I request all map values
    Then http status matches 200
    And value is equal to test

  Scenario: Requesting existing values return an empty list
    When a GET request is sent to "api/datamap"
    Then http status matches 200
    And json path "failure" matches boolean false
    And json path "timestamp" does not exist
    And json path "epochMilli" exists
    And json path "data" contains 0 keys

  Scenario: Create and retrieve a value
    Given body content
    """
    value-1
    """
    And header "content-type" with value "text/plain"
    When a POST request is sent to "api/datamap/one"
    Then http status matches 201
    # retrieve the value created
    When a GET request is sent to "api/datamap/one"
    Then http status matches 200
    And json path "epochMilli" exists
    And json path "failure" matches boolean false
    And json path "data" matches string "value-1"

  Scenario: Creating a new value returns the locator header.
    This creates a new resource, and then takes the header 'location', with the resource access URI to
    send a request to retrieve it (to check that the location is correct and the value is present).
    Given body content
    """
    value-1
    """
    And header "content-type" with value "text/plain"
    When a POST request is sent to "api/datamap/one"
    Then http status matches 201
    And extract header location and name it target
    When a GET request is sent to "resolve:target"
    Then http status matches 200
    And json path "epochMilli" exists
    And json path "failure" matches boolean false
    And json path "data" matches string "value-1"
