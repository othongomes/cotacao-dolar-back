package shx.cotacaodolar.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shx.cotacaodolar.model.Moeda;
import shx.cotacaodolar.service.MoedaService;


@RestController
@RequestMapping(value = "/")
public class MoedaController {

    @Autowired
    private MoedaService moedaService;

    //Rotina que retorna cotações em um período de tempo
    @GetMapping("/moeda/{data1}&{data2}")
    public List<Moeda> getCotacoesPeriodo(@PathVariable("data1") String startDate, @PathVariable("data2") String endDate) throws IOException, MalformedURLException, ParseException{
        return moedaService.getCotacoesPeriodo(startDate, endDate);
    }
    
    //Rotina que retorna a ultima cotação disponível
    @GetMapping("/moeda/atual")
    public ResponseEntity<Moeda> getCotacaoAtual() throws IOException, MalformedURLException, ParseException {
        Moeda moedaAtual = moedaService.getCotacaoAtual();
        return new ResponseEntity<>(moedaAtual, HttpStatus.OK);
    }
    
    //Rotina que recebe um período e retorna uma lista de cotações, somente com as cotações menores que a ultima
    // cotação disponível.
    @GetMapping("/moeda/menores/{data1}&{data2}")
    public List<Moeda> getCotacoesMenoresAtual(@PathVariable("data1") String startDate, @PathVariable("data2") String endDate) throws IOException, MalformedURLException, ParseException{
        return moedaService.getCotacoesMenoresAtual(startDate, endDate);
    }


}
