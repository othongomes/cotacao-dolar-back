package shx.cotacaodolar;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class CotacaoDolarApplication {

	public static void main(String[] args) {
		SpringApplication.run(CotacaoDolarApplication.class, args);
	}

	@Bean
	public CorsFilter corsFilter() {
		//Uma nova configuração CORS é criada e armazenada na variável corsConfiguration.
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		//setAllowCredentials(true) permite que as credenciais (como cookies) sejam incluídas nas solicitações.
		corsConfiguration.setAllowCredentials(true);
		//setAllowedOrigins especifica quais origens (domínios) são permitidas a acessar a API. Neste caso, apenas http://localhost:4200 está autorizado.
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		//setAllowedHeaders especifica quais cabeçalhos de solicitação são permitidos.
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		//setExposedHeaders especifica quais cabeçalhos de resposta podem ser expostos ao cliente.
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		//setAllowedMethods especifica quais métodos HTTP são permitidos.
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		//Uma nova fonte de configuração CORS baseada em URL é criada.
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		//registerCorsConfiguration registra a configuração CORS definida anteriormente para todos os caminhos (/**).
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		//Finalmente, um novo CorsFilter é criado e retornado como um bean gerenciado pelo Spring.
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

}
