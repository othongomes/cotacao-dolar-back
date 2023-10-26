package shx.cotacaodolar.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import shx.cotacaodolar.model.Moeda;
import shx.cotacaodolar.model.Periodo;



@Service
public class MoedaService {

	// o formato da data que o método recebe é "MM-dd-yyyy"
    // Metodo para retornar todas as cotações em um determinado período.
    public List<Moeda> getCotacoesPeriodo(String startDate, String endDate) throws IOException, MalformedURLException, ParseException{
        Periodo periodo = new Periodo(startDate, endDate); /*Isso cria uma instância da classe Periodo com as
        datas de início e fim fornecidas como argumentos. */

        String urlString = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/CotacaoDolarPeriodo(dataInicial=@dataInicial,dataFinalCotacao=@dataFinalCotacao)?%40dataInicial='" + periodo.getDataInicial() + "'&%40dataFinalCotacao='" + periodo.getDataFinal() + "'&%24format=json&%24skip=0&%24top=" + periodo.getDiasEntreAsDatasMaisUm();
        /*Isso cria uma URL para uma fonte de dados online que fornece cotações de moeda para o período especificado. Ele constrói a URL com base nas datas de início e fim, bem como em outros parâmetros, como o formato de saída e o número de resultados a serem pulados.*/

        URL url = new URL(urlString); /*Isso cria uma instância da classe URL com a URL da fonte de dados.*/
        HttpURLConnection request = (HttpURLConnection)url.openConnection(); /*Isso cria uma conexão HTTP com a URL. A URL é aberta e uma conexão HTTP é estabelecida.*/
        request.connect(); /*Isso estabelece a conexão HTTP com a fonte de dados e faz a solicitação.*/

        JsonElement response = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent())); /*Isso lê a resposta da solicitação HTTP e a analisa em formato JSON usando a biblioteca Gson (Google Gson). Ele converte a resposta da fonte de dados em um objeto JsonElement.*/
        JsonObject rootObj = response.getAsJsonObject(); /*Isso obtém o objeto JSON raiz da resposta.*/
        JsonArray cotacoesArray = rootObj.getAsJsonArray("value"); /*Aqui, você obtém um array JSON chamado "value" do objeto JSON raiz.*/

        List<Moeda> moedasLista = new ArrayList<Moeda>(); /*Você cria uma lista vazia chamada moedasLista, que será preenchida com objetos do tipo Moeda.*/

        /*Este é um loop for que percorre o array cotacoesArray. Para cada elemento do array, você cria uma instância de Moeda chamada moedaRef. Você também extrai informações como o preço, a data e a hora da cotação do objeto JSON e as atribui ao objeto moedaRef. Em seguida, você adiciona esse objeto moedaRef à lista moedasLista.*/
        for(JsonElement obj : cotacoesArray){
            Moeda moedaRef = new Moeda();
            Date data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(obj.getAsJsonObject().get("dataHoraCotacao").getAsString());

            moedaRef.preco = obj.getAsJsonObject().get("cotacaoCompra").getAsDouble();
            moedaRef.data = new SimpleDateFormat("dd/MM/yyyy").format(data);
            moedaRef.hora = new SimpleDateFormat("HH:mm:ss").format(data);
            moedasLista.add(moedaRef);
        }
        return moedasLista;
    }

//________________________________________________________________________________________________________________________________
    //Metodo para retornar a cotação atual sem passar parâmetros.
    public Moeda getCotacaoAtual() throws IOException, MalformedURLException, ParseException {
        Periodo periodo = new Periodo();

        // URL para obter a cotação atual
        String urlString = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/CotacaoDolarDia" +
                "(dataCotacao=@dataCotacao)?@dataCotacao='" + periodo.obterDataAtualFormatada() + "'&$format=json";

        // Cria uma URL e faz uma solicitação HTTP
        URL url = new URL(urlString);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Analisa a resposta JSON
        JsonElement response = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootObj = response.getAsJsonObject();

        // Cria um objeto Moeda e atribui a cotação atual
        Moeda moedaAtual = new Moeda();

        moedaAtual.preco = rootObj.getAsJsonObject().get("value").getAsJsonArray()
                .get(0).getAsJsonObject().get("cotacaoCompra").getAsDouble();

        return moedaAtual;
    }

//________________________________________________________________________________________________________________________________
    //Metodo para retornar as cotações menores que a cotação atual.
    public List<Moeda> getCotacoesMenoresAtual(String startDate, String endDate) throws IOException,
            MalformedURLException, ParseException {
        Moeda moedaAtual = getCotacaoAtual(); // Obtém a cotação atual
        List<Moeda> cotacoesPeriodo = getCotacoesPeriodo(startDate, endDate); // Use as datas de início e término fornecidas

        // Filtra as cotações que são menores que a cotação atual
        List<Moeda> cotacoesMenores = new ArrayList<>();
        for (Moeda moeda : cotacoesPeriodo) {
            if (moeda.preco < moedaAtual.preco) {
                cotacoesMenores.add(moeda);
            }
        }

        return cotacoesMenores;
    }
}

