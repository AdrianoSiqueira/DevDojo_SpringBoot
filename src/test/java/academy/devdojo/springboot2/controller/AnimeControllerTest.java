package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    }
}
