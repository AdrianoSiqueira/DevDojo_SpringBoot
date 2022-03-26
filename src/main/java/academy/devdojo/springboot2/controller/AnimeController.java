package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnimeController {

    public List<Anime> list(){
        return List.of(
                new Anime("DBZ"),
                new Anime("Berserk")
        );
    }
}
