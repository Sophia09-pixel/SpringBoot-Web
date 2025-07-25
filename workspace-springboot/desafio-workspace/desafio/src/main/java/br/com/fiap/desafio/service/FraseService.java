package br.com.fiap.desafio.service;

import br.com.fiap.desafio.dto.FraseDTO;
import br.com.fiap.desafio.model.Frase;
import br.com.fiap.desafio.repository.FraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FraseService {

    @Autowired
    private FraseRepository fraseRepository;

    public FraseDTO obterFraseAleatoria() {
        Frase frase = fraseRepository.buscaFraseAleatoria();

        return converteDadosParaDTO(frase);
    }

    private FraseDTO converteDadosParaDTO(Frase frase) {
        return new FraseDTO(frase.getTitulo(), frase.getFrase(), frase.getPersonagem(), frase.getPersonagem());
    }
}
