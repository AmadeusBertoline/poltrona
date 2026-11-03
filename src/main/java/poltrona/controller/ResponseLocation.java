package poltrona.controller;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

final class ResponseLocation {

    private ResponseLocation() {
    }

    static URI forId(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
