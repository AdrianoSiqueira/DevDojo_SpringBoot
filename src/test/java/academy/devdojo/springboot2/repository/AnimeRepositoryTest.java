package academy.devdojo.springboot2.repository;

import academy.devdojo.springboot2.domain.Anime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    void save_PersistAnime_WhenSuccessful() {
        Anime animeToSave = createAnime();
        Anime savedAnime  = animeRepository.save(animeToSave);

        assertThat(savedAnime).isNotNull();
        assertThat(savedAnime.getId()).isNotNull();
        assertThat(savedAnime.getName()).isEqualTo(animeToSave.getName());
    }

    private Anime createAnime() {
        return Anime.builder()
                    .name("Test")
                    .build();
    }
}
