package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private ConsumoApi consumo = new ConsumoApi();

    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=6585022c";

    private List<DadosSerie> listaDadosSerie = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> listaSerie = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var menu = """
             1 - Buscar séries
             2 - Buscar episódios
             3 - Listar séries buscadas
             4 - Buscar série por titulo
             5 - Buscar séries por ator
             6 - listar top 5 Séries
             7 - Buscar série por categoria
             8 - Filtrar série por temporadas e avaliação
             9 - Buscar episódio por trecho
             10 - top 5 episódios por Série
             11 - Buscar episódios a partir de uma data
    
             0 - Sair                                 
             """;

        int opcao;
        do {
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarPorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarPorTrecho();
                    break;
                case 10:
                    buscarTop5EpisodiosPorSeries();
                    break;
                case 11:
                    buscarEpisodiosPorData();
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        } while (opcao != 0);
    }




    private void listarSeriesBuscadas() {
        listaSerie = repositorio.findAll();
        listaSerie.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        //listaDadosSerie.add(dados);
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();


        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        }else{
            System.out.println("Série não encontrada");
        }
    }


    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série "+ serieBusca.get());
        }else{
            System.out.println("Série não encontrada");
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.println("Digite o nome do ator: ");
        var nomeAtor = leitura.nextLine();

        System.out.println("Digite tambem a avaliação que gostaria de ver");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que "+ nomeAtor + "trabalhou: ");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " - avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByAvaliacaoDesc();

        topSeries.forEach(s -> System.out.println(s.getTitulo() + " - avaliação: " + s.getAvaliacao()));
    }

    private void buscarPorCategoria() {
        System.out.println("Digite a categoria da série: ");
        var categoriaSerie = leitura.nextLine();
        Categoria categoria = Categoria.fromStringPt(categoriaSerie);

        List<Serie> series = repositorio.findByGenero(categoria);

        series.forEach(s -> System.out.println(s.getTitulo() + " - Categoria: "+ s.getGenero()));
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.println("Filtrar até quantas temporadas?");
        var temporadas = leitura.nextInt();
        leitura.nextLine();

        System.out.println("Com qual valor de avaliação?");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(temporadas, avaliacao);

        System.out.println("=== Séries filtradas ===");
        filtroSeries.forEach(s -> System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
    }

    private void buscarPorTrecho(){
        System.out.println("Digite o nome do apisódio para busca: ");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->  System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                e.getSerie().getTitulo(), e.getTemporada(),
                e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void buscarTop5EpisodiosPorSeries(){
        buscarSeriePorTitulo();

        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();

            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);

            topEpisodios.forEach(e ->  System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliação %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(),
                    e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosPorData(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento: ");
            var anoLimite = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosPorAno = repositorio.episodiosPorAno(serie, anoLimite);
            episodiosPorAno.forEach(e ->  System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliação %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(),
                    e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }
}