package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {
    @Autowired
    private SerieService serieService;

    @GetMapping
    public List<SerieDTO> series() {
        return serieService.Obterseries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> top5() {
        return serieService.top5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> lancamentos() {
        return serieService.lancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id) {
        return serieService.obterSeriePorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTemporadasTodas(@PathVariable Long id) {
        return serieService.obterTodasTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodioDTO> obterTemporadasPorNumeoro(@PathVariable Long id, @PathVariable Long numero){
        return serieService.obterEpisodiosPorTemporada(id, numero);
    }

    @GetMapping("/categoria/{nomeCategoria}")
    public List<SerieDTO> obterSeriePorCategoria(@PathVariable String nomeCategoria) {
        return serieService.obterSeriePorCategoria(nomeCategoria);
    }
}
