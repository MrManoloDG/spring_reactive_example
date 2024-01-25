package com.example.springbootwebfluxapirest.controller;

import com.example.springbootwebfluxapirest.models.documents.Producto;
import com.example.springbootwebfluxapirest.models.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Producto>>> lista() {
        return Mono.just(ResponseEntity.ok(service.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> ver(@PathVariable String id){
        return service.findById(id)
                .map(p -> ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Producto>> crear(@RequestBody Producto producto){
        if(producto.getCreateAt() == null){
            producto.setCreateAt(new Date());
        }

        return service.save(producto)
                .map(p -> ResponseEntity.created(URI.create("/api/productos/" + p.getId()))
                    .contentType(MediaType.APPLICATION_JSON).body(p));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> editar(@RequestBody Producto producto, @PathVariable String id){
        return  service.findById(id).flatMap(p -> {
            p.setNombre(producto.getNombre());
            p.setCategoria(producto.getCategoria());
            p.setPrecio(producto.getPrecio());
            return service.save(p);
        }).map(p -> ResponseEntity.created(URI.create("/api/productos/" + p.getId())).contentType(MediaType.APPLICATION_JSON).body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> eliminar(@PathVariable String id){
        return service.findById(id).flatMap(p -> {
            return service.delete(p).then(Mono.just(ResponseEntity.noContent().build()));
        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
