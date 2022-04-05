package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.mapper.AnimeMapper;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;

    public void delete(long id) {
        animeRepository.delete(findByIdOrThowBadRequestException(id));
    }

    public Anime findByIdOrThowBadRequestException(long id) {
        return animeRepository.findById(id)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anime not found"));
    }

    public List<Anime> listAll() {
        return animeRepository.findAll();
    }

    public void replace(AnimePutRequestBody requestBody) {
        Anime savedAnime = findByIdOrThowBadRequestException(requestBody.getId());

        Anime anime = AnimeMapper.INSTANCE.toAnime(requestBody);
        anime.setId(savedAnime.getId());

        animeRepository.save(anime);
    }

    public Anime save(AnimePostRequestBody requestBody) {
        return animeRepository.save(AnimeMapper.INSTANCE.toAnime(requestBody));
    }
}
