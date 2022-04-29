package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.repository.AnimeRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
 * @ExtendWith é usado para não carregar totalmente o spring, isso fará os testes
 * serem executados mais rápido.
 */
@ExtendWith(value = SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepositoryMock;


    @Test
    void delete_RemovesAnime_WhenSuccessful() {
        assertThatCode(() -> animeService.delete(0))
                .doesNotThrowAnyException();
    }

    @Test
    void findByIdOrThrowBadRequestException_ReturnsAnime_WhenSuccessful() {
        Long  expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime      = animeService.findByIdOrThrowBadRequestException(0);

        assertThat(anime).isNotNull();

        assertThat(anime.getId()).isNotNull()
                                 .isEqualTo(expectedId);
    }

    @Test
    void findByIdOrThrowBadRequestException_ThrowsBadRequestException_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                  .thenReturn(Optional.empty());

        assertThatThrownBy(() -> animeService.findByIdOrThrowBadRequestException(1000))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void findByName_ReturnsEmptyListOfAnimes_WhenAnimeIsNotFound() {
        // Sobrescreve o comportamento do mockito apenas para esse método
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                  .thenReturn(Collections.emptyList());

        List<Anime> animes = animeService.findByName("");

        assertThat(animes).isNotNull()
                          .isEmpty();
    }

    @Test
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        List<Anime> animes = animeService.findByName("");

        assertThat(animes).isNotNull()
                          .isNotEmpty()
                          .hasSize(1);
    }

    @Test
    void listAllNonPageable_ReturnsListOfAnimes_WhenSuccessful() {
        List<Anime> page = animeService.listAllNonPageable();

        assertThat(page).isNotNull()
                        .isNotEmpty()
                        .hasSize(1);
    }

    @Test
    void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String      expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> page         = animeService.listAll(PageRequest.of(1, 1));

        assertThat(page).isNotNull();

        assertThat(page.toList())
                .isNotEmpty()
                .hasSize(1);

        assertThat(page.toList().get(0).getName())
                .isEqualTo(expectedName);
    }

    @Test
    void replace_UpdatesAnime_WhenSuccessful() {
        assertThatCode(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                .doesNotThrowAnyException();
    }

    @Test
    void save_ReturnsAnime_WhenSuccessful() {
        Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());

        assertThat(anime).isNotNull()
                         .isEqualTo(AnimeCreator.createValidAnime());
    }


    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                  .thenReturn(animePage);

        BDDMockito.when(animeRepositoryMock.findAll())
                  .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                  .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                  .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                  .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing()
                  .when(animeRepositoryMock)
                  .delete(ArgumentMatchers.any(Anime.class));
    }
}
