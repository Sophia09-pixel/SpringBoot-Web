package br.com.fiap.desafio.controller;

import br.com.fiap.desafio.dto.FraseDTO;
import br.com.fiap.desafio.model.Frase;
import br.com.fiap.desafio.service.FraseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FraseController {

    @Autowired
    private FraseService service;

    @GetMapping("/series/frases")
    public FraseDTO Obterfrases() {
        return service.obterFraseAleatoria();
    }
}
