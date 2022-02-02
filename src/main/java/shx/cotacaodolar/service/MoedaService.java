package shx.cotacaodolar.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shx.cotacaodolar.model.Moeda;
import shx.cotacaodolar.model.Periodo;
import shx.cotacaodolar.repository.MoedaRepository;



@Service
public class MoedaService {

    @Autowired
    private MoedaRepository moedaRepository;
    
    public Number currentValue() throws IOException, MalformedURLException, ParseException{
        String urlString ="https://economia.awesomeapi.com.br/last/USD-BRL";
        JsonObject moeda = connectToUrlAndReturnJsonObject(urlString);

        String res = String.format("%.2f", moeda.get("bid").getAsDouble());
        NumberFormat doubleFormat = NumberFormat.getInstance(Locale.getDefault());
        Number value = doubleFormat.parse(res);
        return value.doubleValue();
    }

    public String currentValueAndDate() throws IOException, MalformedURLException{
        String urlString ="https://economia.awesomeapi.com.br/last/USD-BRL";
        JsonObject moeda = connectToUrlAndReturnJsonObject(urlString);
    
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date data = new Date();
        String dataAtual = dateFormat.format(data);

        String res = moeda.get("code").toString() +" - R$: " + String.format("%.2f", moeda.get("bid").getAsDouble());
        res += " - " + dataAtual;
        return res;
    }

    public List<Moeda> valuesInTimePeriod(String startDate, String endDate) throws IOException, MalformedURLException, ParseException{
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

    public Moeda save(Moeda moeda){
        return moedaRepository.save(moeda);
    }

    private JsonObject connectToUrlAndReturnJsonObject(String urlString) throws MalformedURLException, IOException{
        URL url = new URL("https://economia.awesomeapi.com.br/last/USD-BRL");
        HttpURLConnection request = (HttpURLConnection)url.openConnection();
        request.connect();

        JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
        JsonObject rootObj = root.getAsJsonObject();
        JsonObject moeda = rootObj.get("USDBRL").getAsJsonObject();
        return moeda;
    }

   

}
