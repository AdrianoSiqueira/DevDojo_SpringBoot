package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {

    private static final DevDojoUser USER = DevDojoUser.builder()
                                                       .name("Adriano")
                                                       .username("adriano")
                                                       .password("{bcrypt}$2a$10$4HHHjOKXmcF/6caRCSw4NOJxsGin3xVQu1rLiJKz1.5m9HfgzwEiS")
                                                       .authorities("ROLE_USER")
                                                       .build();

    private static final DevDojoUser ADMIN = DevDojoUser.builder()
                                                        .name("DevDojo Academy")
                                                        .username("devdojo")
                                                        .password("{bcrypt}$2a$10$4HHHjOKXmcF/6caRCSw4NOJxsGin3xVQu1rLiJKz1.5m9HfgzwEiS")
                                                        .authorities("ROLE_USER,ROLE_ADMIN")
                                                        .build();

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate restTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate restTemplateRoleAdmin;

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private DevDojoUserRepository userRepository;

    @Test
    void delete_RemovesAnime_WhenSuccessful() {
        userRepository.save(ADMIN);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToSave());

        ResponseEntity<Void> response = restTemplateRoleAdmin.exchange("/animes/admin/{id}",
                                                                       HttpMethod.DELETE,
                                                                       null,
                                                                       Void.class,
                                                                       savedAnime.getId());

        assertThat(response)
                .isNotNull();

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void delete_Returns403_WhenUserIsNotAdmin() {
        userRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToSave());

        ResponseEntity<Void> response = restTemplateRoleUser.exchange("/animes/admin/{id}",
                                                                      HttpMethod.DELETE,
                                                                      null,
                                                                      Void.class,
                                                                      savedAnime.getId());

        assertThat(response)
                .isNotNull();

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void findById_ReturnsAnime_WhenSuccessful() {
        userRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToSave());
        Long  expectedId = savedAnime.getId();

        Anime anime = restTemplateRoleUser.getForObject("/animes/{id}",
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
        userRepository.save(USER);

        List<Anime> animes = restTemplateRoleUser.exchange("/animes/find?name=notFound",
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
        userRepository.save(USER);

        Anime  savedAnime   = animeRepository.save(AnimeCreator.createAnimeToSave());
        String expectedName = savedAnime.getName();
        String url          = String.format("/animes/find?name=%s", expectedName);


        List<Anime> animes = restTemplateRoleUser.exchange(url,
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
        userRepository.save(USER);

        Anime  savedAnime   = animeRepository.save(AnimeCreator.createAnimeToSave());
        String expectedName = savedAnime.getName();

        List<Anime> animes = restTemplateRoleUser.exchange("/animes/all",
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
        userRepository.save(USER);

        Anime  savedAnime   = animeRepository.save(AnimeCreator.createAnimeToSave());
        String expectedName = savedAnime.getName();

        Page<Anime> page = restTemplateRoleUser.exchange("/animes",
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

    @Test
    void replace_UpdatesAnime_WhenSuccessful() {
        userRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToSave());
        savedAnime.setName("New Name");

        ResponseEntity<Void> response = restTemplateRoleUser.exchange("/animes",
                                                                      HttpMethod.PUT,
                                                                      new HttpEntity<>(savedAnime),
                                                                      Void.class);

        assertThat(response)
                .isNotNull();

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void save_ReturnsAnime_WhenSuccessful() {
        userRepository.save(USER);

        AnimePostRequestBody requestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();

        ResponseEntity<Anime> response = restTemplateRoleUser.postForEntity("/animes",
                                                                            requestBody,
                                                                            Anime.class);

        assertThat(response)
                .isNotNull();

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().getId())
                .isNotNull();
    }

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder builder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("devdojo", "123");

            return new TestRestTemplate(builder);
        }

        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder builder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("adriano", "123");

            return new TestRestTemplate(builder);
        }
    }
}
