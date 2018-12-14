# PITT

![logo](Logo.png)

## Structure

## Implementation detail

## remaining works
 - 헤더 처리 (~~connection~~, ~~last-modified~~)
   - handle_connection 수정하기
 - ~~socket close 제대로 하기~~
 - ~~uri access 제한~~ ~~maybe~~
 - ~~Cache : memory 제한 필요~~
 - ~~read buffer 확장~~, ~~request timeout~~
 - ~~Task의 결과가 새로운 event로 공급되어 event loop내에서 처리되도록 구현 (양방향 통신)~~
 - Benchmarking
    - ~~AWS 열기~~
    - ~~Node.js Server 구축~~
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
  - ~~debugging : until 11/30~~
* Step 3
  - ~~Cacheing : until 12/7?~~
  - Benchmark : until 12/14

## Benchmark
> AWS EC2 : 8GiB RAM, ubuntu 18.04
> in AWS, `java`, `node`, `apache2`, `jmeter` installed
> ~~apache server not yet structured~~

### PITT Server
`http://ec2-3-17-77-2.us-east-2.compute.amazonaws.com:1111`
 - Directory location is `data/`
 - main page setting : `data/index.html`

### Apache Server
`http://ec2-3-17-77-2.us-east-2.compute.amazonaws.com/`
 - Directory location is `var/www/html`

### Node.js Server
`http://ec2-3-17-77-2.us-east-2.compute.amazonaws.com:3000`
 - prerequisite : `PITT/express_server$ npm install`
 - Directory location is `nodejs_server/views/`

### AWS
`ec2-3-17-77-2.us-east-2.compute.amazonaws.com`
 - `chmod PITT.pem`
 - `ssh -i PITT.pem ubuntu@3.17.77.2`

### JMeter
> Windows 10, 16GiB RAM. jmeter GUI mode
 
### killing background
 ```
 jobs
 kill -9 %number (e.g. kill -9 %2)
 ```
