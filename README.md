# PITT

![logo](Logo.png)

## Structure

## Implementation detail

## remaining works
 - 헤더 처리 (connection, ~~last-modified~~)
 - ~~socket close 제대로 하기~~
 - uri access 제한
 - ~~Cache : memory 제한 필요~~
 - read buffer 확장, ~~request timeout~~
 - ~~Task의 결과가 새로운 event로 공급되어 event loop내에서 처리되도록 구현 (양방향 통신)~~
 - Benchmarking
    - Node.js Server 구축
    - ~~Apache Server 구축~~
    - JMeter로 benchmark


## Documents
watch `/Documents`

## Readings
Refer to Issue #1

## TODOs : tentative
* Step 1
  - ~~implementing basic server-client model~~
  - ~~Event Queue~~
  - ~~HTTP parser : until 10/22~~
* Step 2
  - ~~Event definition: until 10/24~~
  - ~~Event Loop Implementation : until 11/9~~
  - ~~HTTP response composition & Interpreter : until 11/16~~ at 11/30
  - ~~Main server execution? : until 11/23~~ at 11/30
  - debugging : until 11/30
* Step 3
  - ~~Cacheing : until 12/7?~~
  - Benchmark : until 12/7?

## Benchmark
> Seongmoon's Macbook : macOS Mojave, 10.14.1

### PITT Server
`http://localhost:1111/`
 - main page setting : `index.html`

### Apache Server
`http://localhost/~seongmoon/`
 - Directory location is `~/Sites/`

### Node.js Server
TODO

### JMeter
TODO


<!-- ## Remark-->
  <!-- * Read Request : from client socket-->
  <!-- * Parse Request : HTTPParser-->
  <!-- * Find Resource : for file request, (In Thread?), determine location of the resource-->
  <!-- * Read File : in Thread, File IO-->
  <!-- * Make Response : after interpreting request, construct response-->
  <!-- * Write Response : HTTP Response to client socket?-->



<!-- ## FLOW-->
<!--in main(event loop),-->
<!--if readable : parse string. enqueue that into event-->
<!--if writable : ???-->
<!--dequeued event case, run HTTPInterpreter. create_response -> respond : socket.write occurs in respond-->
<!--NON_IO : direct response message return!-->
<!--IO : open file, etc... then response message. may result in continuation-->
<!--continuation : file is already open. just write body message. may result in another continuation-->
<!--finished : done. nothing to process more-->

<!--MainServer : only reads inputs from client sockets (then enQ to EQ). calls Eventloop.start().-->
<!--EventLoop : Thread-using class. process events from EQ.dQ-->


<!--## Summary  ~~연수 마크다운 배운 기념으로 ~~-->
<!--* `HTTPParser` : parse HTTP request string(buffer) into `Event`. preprocess into `IO` or `NON_IO`-->
<!--* `Event` : `IO` or `NON_IO`.-->
<!--* HTTPInterpreter : interpret parsed HTTP Request & return HTTP Response-->
  <!--- contained in main thread, and supplementary Thread Pool-->
  <!--- return HTTP Response to the client-->

<!--* `EventLoop` : Event Loop Architecture. exploits `HTTPParser`, `HTTPInterpreter`-->
<!--- contains EventQueue.-->
<!--+ EventQueue : simple Non-blocking Queue (ADT) that contains `Event`-->
<!--- if dequeued NON_IO `Event`-->
<!--+ dispatch the Event to the Thread Pool (Worker Thread).-->
<!--+ HTTP Response is returned to client by Worker Thread.-->
<!--- if dequeued IO `Event`,-->
<!--+ main thread processes HTTP Response to the client.-->

<!--* 현재 제작시 고려해야 할 사항 : 평가표를 기준으로-->
<!--- event loop내에서 처리하기에 적절하지 않은 Task에 대해서 이해하고 이를 Thread pool에 맡겨서 처리하였고 Task의 결과가 새로운 event로 공급되어   event loop내에서 처리되도록 구현했다. (양방향 통신) -> 아마 이부분이 다시 피니시 된 것도 루프에 넣으라는 의미인가 싶은데, 아니면 컨티뉴만 넣으라는 말인 것 같기도 함.-->
<!--- Web Browser가 보낸 http request header를 파싱하고 Method, URI, Protocol에 대한 정보를 추출하였으며 'Connection' header에 대한 동작을 구현하였다.-->
<!--또한 http request header의 크기가 사실상 무한히 커질 수 있음을 이해하고 이를 적절히 제한하여 Memory 관련 문제를 회피하기 위한 구현을 하였다. ->현재 이 부분이 잘 구현되어있는지 모르겠음. (헤더 크기가 커질때 이걸 어떻게 처리해야 할 지 고민을 좀 더 해봐야 할 것 같음.)-->


<!--## Remark-->
<!-- * DMA : file이 커야 효율 나옴-->
<!-- * 501 vs 405-->
<!-- Basically, the rule is: <p>-->
<!-- HTTP 501 for methods we don't recognize <p>-->
<!-- HTTP 405 for methods we don't recognize for one particular resource. <p>-->

<!--- once the socketchannel is closed, it never will be recovered-->