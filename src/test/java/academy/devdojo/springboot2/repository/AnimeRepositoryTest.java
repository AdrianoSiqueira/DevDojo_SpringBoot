package academy.devdojo.springboot2.repository;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.util.AnimeCreator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    void delete_RemovesAnime_WhenSuccessful() {
        Anime anime = AnimeCreator.createAnimeToSave();
        anime = animeRepository.save(anime);

        assertThat(animeRepository.findAll()).isNotEmpty();
        animeRepository.deleteById(anime.getId());
        assertThat(animeRepository.findAll()).isEmpty();
    }

    @Test
    void findByName_ReturnsEmptyList_WhenAnimeIsNotFound() {
        assertThat(animeRepository.findByName("Not Found")).isEmpty();
    }

    @Test
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        Anime anime = AnimeCreator.createAnimeToSave();
        animeRepository.save(anime);

        assertThat(animeRepository.findByName(anime.getName())).isNotEmpty();
    }

    @Test
    void save_PersistAnime_WhenSuccessful() {
        Anime animeToSave = AnimeCreator.createAnimeToSave();
        Anime savedAnime  = animeRepository.save(animeToSave);

        assertThat(savedAnime).isNotNull();
        assertThat(savedAnime.getId()).isNotNull();
        assertThat(savedAnime.getName()).isEqualTo(animeToSave.getName());
    }

    @Test
    void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {
        Anime anime = new Anime();

        /*
         * Pode verificar se o método lança uma exceção, ou pode verificar
         * se uma exceção é lançada pelo método. Só pode usar uma abordagem,
         * senão o método de teste falha.
         */

        // Método lança exceção
        assertThatThrownBy(() -> animeRepository.save(anime))
                .isInstanceOf(ConstraintViolationException.class);

        // Exceção é lançada pelo método
//        assertThatExceptionOfType(ConstraintViolationException.class)
//                .isThrownBy(() -> animeRepository.save(anime));
    }

    @Test
    void save_UpdateAnime_WhenSuccessful() {
        Anime anime = AnimeCreator.createAnimeToSave();
        anime = animeRepository.save(anime);

        anime.setName("Test 2");
        Anime updatedAnime = animeRepository.save(anime);

        assertThat(updatedAnime).isNotNull();
        assertThat(updatedAnime.getId()).isNotNull();
        assertThat(updatedAnime.getName()).isEqualTo(anime.getName());
    }
}
