package academy.devdojo.springboot2.requests;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class AnimePostRequestBody {

    @NotEmpty(message = "Anime name cannot be empty nor null")
    private String name;
}
