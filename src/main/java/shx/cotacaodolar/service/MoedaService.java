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
        Periodo periodo = new Periodo(startDate, endDate);

        String urlString = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/CotacaoDolarPeriodo(dataInicial=@dataInicial,dataFinalCotacao=@dataFinalCotacao)?%40dataInicial='" + periodo.getDataInicial() + "'&%40dataFinalCotacao='" + periodo.getDataFinal() + "'&%24format=json&%24skip=0&%24top=" + periodo.getDiasEntreAsDatasMaisUm();

        URL url = new URL(urlString);
        HttpURLConnection request = (HttpURLConnection)url.openConnection();
        request.connect();

        JsonElement response = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
        JsonObject rootObj = response.getAsJsonObject();
        JsonArray cotacoesArray = rootObj.getAsJsonArray("value");

        List<Moeda> moedasLista = new ArrayList<Moeda>();

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

    //Metodo para retornar a cotação atual sem passar parâmetros.
    //Caso não seja encnontrada a cotação do dia, retorna a ultima cotação encontrada.
public Moeda getCotacaoAtual() throws IOException, MalformedURLException, ParseException {
    Periodo periodo = new Periodo();
    Moeda moedaAtual = null;
    int diasParaSubtrair = 0;

    while (moedaAtual == null) {
        String dataFormatada = periodo.obterDataFormatadaComSubtracao(diasParaSubtrair);
        String urlString = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/CotacaoDolarDia" +
                "(dataCotacao=@dataCotacao)?@dataCotacao='" + dataFormatada + "'&$format=json";

        URL url = new URL(urlString);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonElement response = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootObj = response.getAsJsonObject();
        JsonArray cotacoesArray = rootObj.getAsJsonArray("value");

        for(JsonElement obj : cotacoesArray){
            moedaAtual = new Moeda();
            Date data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(obj.getAsJsonObject().get("dataHoraCotacao").getAsString());

            moedaAtual.preco = obj.getAsJsonObject().get("cotacaoCompra").getAsDouble();
            moedaAtual.data = new SimpleDateFormat("dd/MM/yyyy").format(data);
            moedaAtual.hora = new SimpleDateFormat("HH:mm:ss").format(data);
        }
        diasParaSubtrair++;
    }
    return moedaAtual;
}

    //Metodo para retornar as cotações menores que a cotação atual.
    public List<Moeda> getCotacoesMenoresAtual(String startDate, String endDate) throws IOException,
            MalformedURLException, ParseException {
        Moeda moedaAtual = getCotacaoAtual();
        List<Moeda> cotacoesPeriodo = getCotacoesPeriodo(startDate, endDate);

        List<Moeda> cotacoesMenores = new ArrayList<>();
        for (Moeda moeda : cotacoesPeriodo) {
            if (moeda.preco < moedaAtual.preco) {
                cotacoesMenores.add(moeda);
            }
        }

        return cotacoesMenores;
    }
}

