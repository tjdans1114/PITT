# PITT

![logo](Logo.png)


## Documents
watch `/Documents`

## Readings
Refer to Issue #1

Need to study Thread...

## TODOs : tentative
* Step 1
  - ~~implementing basic server-client model~~
  - ~~Event Queue~~
  - ~~HTTP parser : until 10/22~~
* Step 2
  - Event definition : ~~until 10/24~~ future works necessary
  - HTTP response composition
  - Event Loop Implementation
  - Main server execution?
* Step 3
  - Cacheing
  - Benchmark

## Summary
1. Event : HTTPEvent or IOEvent
2. HTTPParser : parse string into HTTPEvent
3. HTTPResponse : interpret parsed HTTP Request & return HTTP Response
  * if HTTP req entails I/O, return (I/O continuation)(???)
  * else return HTTP Response
4. EventLoop : Event Loop Architecture. exploits HTTPParser, HTTPResponse
  * 요 부분을 어떻게 구성할지? 잘 모르겟음
  * if dequeued HTTP Event
    - if HTTPResponse returns normal resp, then write that to socket.
    - else (if returns IO continuation), enqueue that to the Queue
  * if dequeued IO Event,
