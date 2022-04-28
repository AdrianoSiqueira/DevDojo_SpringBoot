package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/*
 * @ExtendWith é usado para não carregar totalmente o spring, isso fará os testes
 * serem executados mais rápido.
 */
@ExtendWith(value = SpringExtension.class)
class AnimeControllerTest {

    /*
     * @InjectMocks é usado para testar a classe em si (no caso AnimeController)
     */
    @InjectMocks
    private AnimeController animeController;

    /*
     * @Mock é usado para testar as classes dentro da classe que está sendo
     * testada.
     *
     * Como a classe AnimeController faz uso da classe AnimeService, vamos
     * declará-la como mock. Esse AnimeService irá se sobrepor sobre o que foi
     * declarado na classe AnimeController.
     *
     * Vamos usar esse AnimeService para interceptar as chamadas a seus métodos
     * para alterar o comportamento durante os testes.
     */
    @Mock
    private AnimeService animeServiceMock;

    @Test
    void findById_ReturnsAnime_WhenSuccessful() {
        Long  expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime      = animeController.findById(0).getBody();

        assertThat(anime).isNotNull();

        assertThat(anime.getId()).isNotNull()
                                 .isEqualTo(expectedId);
    }

    @Test
    void findByName_ReturnsEmptyListOfAnimes_WhenAnimeIsNotFound() {
        // Sobrescreve o comportamento do mockito apenas para esse método
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                  .thenReturn(Collections.emptyList());

        List<Anime> animes = animeController.findByName("").getBody();

        assertThat(animes).isNotNull()
                          .isEmpty();
    }

    @Test
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        List<Anime> animes = animeController.findByName("").getBody();

        assertThat(animes).isNotNull()
                          .isNotEmpty()
                          .hasSize(1);
    }

    @Test
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        List<Anime> page = animeController.list().getBody();

        assertThat(page).isNotNull()
                        .isNotEmpty()
                        .hasSize(1);
    }

    @Test
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String      expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> page         = animeController.list(null).getBody();

        assertThat(page).isNotNull();

        assertThat(page.toList())
                .isNotEmpty()
                .hasSize(1);

        assertThat(page.toList().get(0).getName())
                .isEqualTo(expectedName);
    }

    @Test
    void replace_UpdatesAnime_WhenSuccessful() {
        // Pode ser feito assim
        assertThatCode(() -> animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                .doesNotThrowAnyException();

        // Ou assim
        ResponseEntity<Void> response = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void save_ReturnsAnime_WhenSuccessful() {
        Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimePostRequestBody())
                                     .getBody();

        assertThat(anime).isNotNull()
                         .isEqualTo(AnimeCreator.createValidAnime());
    }

    /*
     * Intercepta as chamadas ao animeServiceMock e modifica o valor de retorno.
     */
    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
                  .thenReturn(animePage);

        BDDMockito.when(animeServiceMock.listAllNonPageable())
                  .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                  .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                  .thenReturn(List.of(AnimeCreator.createValidAnime()));

        // Só vai dar um trigger no mockito se o objeto for instância de AnimePostRequestBody
        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                  .thenReturn(AnimeCreator.createValidAnime());

        // A sintaxe é diferente quando o método retorna void
        BDDMockito.doNothing()
                  .when(animeServiceMock)
                  .replace(ArgumentMatchers.any(AnimePutRequestBody.class));
    }
}
