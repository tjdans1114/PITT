# PITT

![logo](Logo.png)

## Structure

## Implementation detail

## remaining works
 - 헤더 처리 (connection, ~~last-modified~~)
   - handle_connection 수정하기
 - ~~socket close 제대로 하기~~
 - ~~uri access 제한~~ ~~maybe~~
 - ~~Cache : memory 제한 필요~~
 - ~~read buffer 확장~~, ~~request timeout~~
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
 - Directory location is `data/`
 - main page setting : `data/index.html`

### Apache Server
`http://localhost/~seongmoon/`
 - prerequisite : `apachectl start`
 - Directory location is `~/Sites/`

### Node.js Server
`http://localhost:3000/`
 - prerequisite : `PITT/express_server$ npm install`
 - Directory location is `nodejs_server/views/`

### JMeter
 - prerequisite :
    - `brew install jmeter`
 - `JVM_ARGS="-Xms4G -Xmx8G" jmeter -n -t Test\ Plan.jmx -l output.jlt`
 - `set JVM_ARGS="-Xms4096m -Xmx8192m"`
 - `ulimit -u 1024`
