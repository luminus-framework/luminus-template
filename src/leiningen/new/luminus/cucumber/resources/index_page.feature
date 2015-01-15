Feature: Cukes
  An example of testing a compojure app with cucumber.

  Scenario: Index Page
    Given I am at the "homepage"
    Then I should see "Welcome to {{name}}"

