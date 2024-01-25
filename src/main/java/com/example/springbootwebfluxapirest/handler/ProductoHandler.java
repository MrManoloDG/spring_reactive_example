package com.example.springbootwebfluxapirest.handler;

import com.example.springbootwebfluxapirest.models.documents.Producto;
import com.example.springbootwebfluxapirest.models.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ProductoHandler {

    @Autowired
    private ProductoService service;

    public Mono<ServerResponse> listar(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(service.findAll(), Producto.class);
    }

    public Mono<ServerResponse> ver(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.findById(id).flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromValue(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> crear(ServerRequest request) {
        Mono<Producto> producto = request.bodyToMono(Producto.class);
        return producto.flatMap(p -> {
            if (p.getCreateAt() == null) {
                p.setCreateAt(new Date());
            }
            return service.save(p);
        }).flatMap(p -> ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId()))).body(fromValue(p)));
    }
}
