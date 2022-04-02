package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;

import java.util.List;

public class AnimeService {

    public List<Anime> listAll() {
        return List.of(
                new Anime(1L, "DBZ"),
                new Anime(2L, "Berserk")
        );
    }
}
