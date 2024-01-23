package principal;

import model.Dados;
import model.Modelos;
import model.Veiculo;
import service.ConsumoApi;
import service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();

    private ConverteDados conversor = new ConverteDados();
    private final String UrlBase = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){
        var menu = """
                *** OPÇÕES ***
                CARRO
                MOTO
                CAMINHÃO
                
                Digite uma das opções para consultar:
                """;

        System.out.println(menu);
        var opcao = leitura.nextLine();
        String endereco ;

        if (opcao.toLowerCase().contains("carr")){
            endereco = UrlBase + "carros/marcas";
        }else if (opcao.toLowerCase().contains("mot")){
            endereco = UrlBase + "motos/marcas";
        }else {
            endereco = UrlBase + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o codigo da marca para consulta: ");
        var codigoMarca = leitura.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\n Digite um trecho do nome do veiculo a ser buscado");
        var nomeVeiculo =  leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o codigo do modelo para buscar valores de avaliação");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/"  + "anos";
                json = consumo.obterDados(endereco);
                List<Dados> anos = conversor.obterLista(json, Dados.class);
                List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("Todos os veiculos filtrados com avaliações por ano");
        veiculos.forEach(System.out::println);

    }
}
