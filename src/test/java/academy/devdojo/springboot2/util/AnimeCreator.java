package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeToSave() {
        return Anime.builder()
                    .name("Test")
                    .build();
    }

    public static Anime createUpdatedAnime() {
        return Anime.builder()
                    .id(1L)
                    .name("Test 2")
                    .build();
    }

    public static Anime createValidAnime() {
        return Anime.builder()
                    .id(1L)
                    .name("Test")
                    .build();
    }
}
