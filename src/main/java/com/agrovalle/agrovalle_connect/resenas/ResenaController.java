package com.agrovalle.agrovalle_connect.resenas;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping
    public ResponseEntity<ResenaResponse> crear(@Valid @RequestBody ResenaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(request));
    }

    @GetMapping
    public List<ResenaResponse> listarPorAgricultor(@RequestParam Long agricultorId) {
        return resenaService.listarPorAgricultor(agricultorId);
    }
}
