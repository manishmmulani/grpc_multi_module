### Multi-module gradle project with three modules

- gRPC Interface
- gRPC Server
- gRPC Client

## References : 

- https://yidongnan.github.io/grpc-spring-boot-starter/en/server/getting-started.html

- https://developers.google.com/maps-booking/samples-tutorials/booking-server-code-samples/gRPC-v2/java.grpc-server

- https://www.baeldung.com/grpc-introduction

#### Running the server

> ./gradlew grpc_server:run

#### Running the client

> ./gradlew grpc_client:run

## grpcurl

For testing without grpc client via grpcurl

https://hub.docker.com/r/networld/grpcurl

> docker pull networld/grpcurl

Volume needs to be mounted to be able to share .proto file

> docker run -it --name grpcurl -v ~/work/proto/grpc_project:/var/grpc_project networld/grpcurl

`/#`

> ./grpcurl -proto /var/grpc_project/grpc_interface/src/main/proto/GreetingService.proto 172.17.0.1:8080 list

`com.mulani.grpc_project.GreetingService`

> ./grpcurl -proto /var/grpc_project/grpc_interface/src/main/proto/GreetingService.proto 172.17.0.1:8080 list com.mulani.grpc_project.GreetingService

`sayHello`

> ./grpcurl -proto /var/grpc_project/grpc_interface/src/main/proto/GreetingService.proto 172.17.0.1:8080 describe com.mulani.grpc_project.GreetingService
```
com.mulani.grpc_project.GreetingService is a service:
{
  "name": "GreetingService",
  "method": [
    {
      "name": "sayHello",
      "inputType": ".com.mulani.grpc_project.GreetingRequest",
      "outputType": ".com.mulani.grpc_project.GreetingResponse",
      "options": {
        
      }
    }
  ],
  "options": {
    
  }
}
```

> ./grpcurl -proto /var/grpc_project/grpc_interface/src/main/proto/GreetingService.proto -d '{"name":"mulani"}' 172.17.0.1:8080 com.mulani.grpc_project.GreetingService.sayHello

`Failed to dial target host "172.17.0.1:8080": tls: first record does not look like a TLS handshake`

> ./grpcurl -proto /var/grpc_project/grpc_interface/src/main/proto/GreetingService.proto -d '{"name":"mulani"}' -plaintext 172.17.0.1:8080 com.mulani.grpc_project.GreetingService.sayHello
```
{
  "message": "Hello world!! Mr. mulani"
}
``` 

## Streaming use-cases
https://grpc.io/docs/languages/java/basics/

