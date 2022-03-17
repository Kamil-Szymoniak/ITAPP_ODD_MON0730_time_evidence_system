package pl.edu.pwr.timeevidence.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import io.swagger.models.auth.In.HEADER
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.service.contexts.SecurityContext

@Configuration
@Import(
    BeanValidatorPluginsConfiguration::class
)
class SwaggerConfig {
    private val appVersion: String = "1.0.0"
    @Bean
    fun api(): Docket {
        val docket = Docket(DocumentationType.SWAGGER_2)
        val apiInfo = ApiInfoBuilder()
            .title("TIME EVIDENCE SYSTEM")
            .description("REST interface of time evidence system")
            .contact(Contact("Time Evidence System", "", "269853@student.pwr.edu.pl"))
            .version(appVersion)
            .build()
        val securityScheme = listOf(
            ApiKey("JWT", HttpHeaders.AUTHORIZATION, HEADER.name) as SecurityScheme
        )
        val securityContext = listOf(
            SecurityContext.builder().securityReferences(
                listOf(
                    SecurityReference.builder()
                        .reference("JWT")
                        .scopes(arrayOfNulls<AuthorizationScope>(0))
                        .build()
                )
            ).build()
        )
        return docket
            .groupName("API")
            .protocols(setOf("http"))
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.regex("/api/.*"))
            .build()
            .apiInfo(apiInfo)
            .useDefaultResponseMessages(false)
            .securitySchemes(securityScheme)
            .securityContexts(securityContext)
    }
}