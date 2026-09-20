Feature: Simple Books API
  Scenario Outline: User calls /books GET request, checks response headers

    Given access to granted to the API
    When a GET request is sent to <url>

    Examples :
    | test name                   | url                                       |
    | 'Check GET response header' | 'http://simple-books-api.glitch.me/books' |