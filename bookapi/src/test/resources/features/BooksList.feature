Feature: Simple Books API

  Scenario Outline: User calls /books GET request, checks response header attributes

  # Response header attributes:
  #   http Status code = status, 
  #   response.header.date = time, 
  #   response.header.content-type = type, 
  #   response.header.content-length = size

    Given access to granted to the API
    When GET request is sent to 'http://simple-books-api.glitch.me/books'
    
    Then the response header should match with <expect_header_attribs>

    
    And the response body should not be empty
    And each book in the response should have an "id" and a "name"

    @smoke
    Examples:
    | test name                           | expect_header_attribs |
    | 'Check GET response header -smoke'  | 'status=200'          |
    
    @regression
    Examples:
    | test name                               | expect_header_attribs                                           |
    | 'Check GET response header -regression' | 'status=200, size>0, time<now()-2minute, type=application/json' |



  Scenario Outline: User calls /books GET request, checks response content

    Given access to granted to the API
    When GET request is sent to 'http://simple-books-api.glitch.me/books'
    Then the response body should contains <expected_item_properties> items to be more than <expected_contains_number>
    # Then the response body should contains not <expected_filter_out_item_properties> items to be more than <expected_not_contains_number>

    @smoke
    Examples:
    | test name                               | expected_item_properties  | expected_contains_number  | expected_filter_out_item_properties | expected_not_contains_number |
    | 'Check GET response content -smoke'     | 'id:1'                    | '0'                       | ''                                  | ''                           |

    