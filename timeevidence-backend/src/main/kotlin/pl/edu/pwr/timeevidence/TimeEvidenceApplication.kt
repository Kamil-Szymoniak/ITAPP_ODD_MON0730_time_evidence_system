package pl.edu.pwr.timeevidence

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(exclude = [RepositoryRestMvcAutoConfiguration::class])class TimeEvidenceApplication

fun main(args: Array<String>) {
	runApplication<TimeEvidenceApplication>(*args)
}
