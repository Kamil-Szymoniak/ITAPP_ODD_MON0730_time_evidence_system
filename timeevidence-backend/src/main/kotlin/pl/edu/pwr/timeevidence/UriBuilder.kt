package pl.edu.pwr.timeevidence

import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

object UriBuilder {
    fun getUri(path: String, value: Any?): URI {
        return ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("api/$path")
            .buildAndExpand(value)
            .toUri()
    }
}