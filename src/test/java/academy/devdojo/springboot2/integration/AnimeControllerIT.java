package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class AnimeControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    void findById_ReturnsAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToSave());
        Long  expectedId = savedAnime.getId();

        Anime anime = restTemplate.getForObject("/animes/{id}",
                                                Anime.class,
                                                expectedId);

        assertThat(anime)
                .isNotNull();

        assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    void findByName_ReturnsEmptyListOfAnimes_WhenAnimeIsNotFound() {
        List<Anime> animes = restTemplate.exchange("/animes/find?name=notFound",
                                                   HttpMethod.GET,
                                                   null,
                                                   new ParameterizedTypeReference<List<Anime>>() {})
                                         .getBody();

        assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        Anime  savedAnime   = animeRepository.save(AnimeCreator.createAnimeToSave());
        String expectedName = savedAnime.getName();
        String url          = String.format("/animes/find?name=%s", expectedName);


        List<Anime> animes = restTemplate.exchange(url,
                                                   HttpMethod.GET,
                                                   null,
                                                   new ParameterizedTypeReference<List<Anime>>() {})
                                         .getBody();

        assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        Anime  savedAnime   = animeRepository.save(AnimeCreator.createAnimeToSave());
        String expectedName = savedAnime.getName();

        List<Anime> animes = restTemplate.exchange("/animes/all",
                                                   HttpMethod.GET,
                                                   null,
                                                   new ParameterizedTypeReference<List<Anime>>() {})
                                         .getBody();

        assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(animes.get(0).getName())
                .isEqualTo(expectedName);
    }

    @Test
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime  savedAnime   = animeRepository.save(AnimeCreator.createAnimeToSave());
        String expectedName = savedAnime.getName();

        Page<Anime> page = restTemplate.exchange("/animes",
                                                 HttpMethod.GET,
                                                 null,
                                                 new ParameterizedTypeReference<PageableResponse<Anime>>() {})
                                       .getBody();

        assertThat(page).isNotNull();

        assertThat(page.toList())
                .isNotEmpty()
                .hasSize(1);

        assertThat(page.toList().get(0).getName())
                .isEqualTo(expectedName);
    }
}
