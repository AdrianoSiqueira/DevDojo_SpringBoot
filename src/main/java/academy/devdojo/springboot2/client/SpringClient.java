package academy.devdojo.springboot2.client;

import academy.devdojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Essa classe simula um cliente se conectando com nosso servidor. Para ela
 * funcionar, o servidor precisa estar executando.
 */
@Log4j2
public class SpringClient {

    public static void main(String[] args) {
//        getRequest();
        postRequest();
    }

    private static void getRequest() {
        String getById = "http://localhost:8080/animes/2";
        String getAll  = "http://localhost:8080/animes/all";

        // O jackson irá converter o json para Anime automaticamente
        ResponseEntity<Anime> response = new RestTemplate().getForEntity(getById, Anime.class);
        log.info(response);

        Anime anime = new RestTemplate().getForObject(getById, Anime.class);
        log.info(anime);

        Anime[] animesArray = new RestTemplate().getForObject(getAll, Anime[].class);
        log.info(Arrays.toString(animesArray));

        ResponseEntity<List<Anime>> animeListResponse = new RestTemplate().exchange(getAll,
                                                                                    HttpMethod.GET,
                                                                                    null,
                                                                                    new ParameterizedTypeReference<>() {});
        log.info(animeListResponse.getBody());  // getBody retorna a lista
    }

    private static void postRequest() {
        String post = "http://localhost:8080/animes";

        Anime anime = Anime.builder().name("Kingdom").build();
        anime = new RestTemplate().postForObject(post, anime, Anime.class);
        log.info("Saved anime: {}", anime);
    }
}
