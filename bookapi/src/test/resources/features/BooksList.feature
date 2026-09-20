Feature: Simple Books API

  Scenario Outline: User calls /books GET request, checks response status, time and body

    Given access to granted to the API
    When a GET request is sent to <url>
    Then the response status code should be 200
    And the response time should be less than 2000 ms
    And the response body should not be empty
    And each book in the response should have an "id" and a "name"

    Examples:
    | test name                   | url                                       | expect_header_attribs |
    | 'Check GET response header' | 'http://simple-books-api.glitch.me/books' | 'status=0, size>0, time>now()-1day' |


 #asserting the following response elements, http status code, 

 #http status code, time, size
# 
  # !=
  # = 
  # <
  # <=
  # >
  # >=
  # isEmpty
  # isNotEmpty
  # now()
  # now()-1hour
  # now()-1minute
  # now()-1day
# 
# 
  