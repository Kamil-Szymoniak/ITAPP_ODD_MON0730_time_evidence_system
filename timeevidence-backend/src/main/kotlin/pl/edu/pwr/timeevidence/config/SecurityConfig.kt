package pl.edu.pwr.timeevidence.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
class SecurityConfig @Autowired constructor(
    private val userDetailsServiceImplementation: UserDetailsServiceImplementation,
    private val jwtEntryPoint: JwtEntryPoint
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun jwtFilter() = JwtFilter()

    @Bean
    fun passwordEncoder(): PasswordEncoder = Pbkdf2PasswordEncoder("secret", 100000, 32)

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    @Throws(Exception::class)
    public override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
            .userDetailsService<UserDetailsService>(userDetailsServiceImplementation)
            .passwordEncoder(passwordEncoder())
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .cors()
            .and()
            .csrf()
            .disable()
            .exceptionHandling()
            .authenticationEntryPoint(jwtEntryPoint)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(*URL_WHITELIST)
            .permitAll()
            .anyRequest()
            .authenticated()
        http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    companion object {
        private val URL_WHITELIST = arrayOf(
            "/auth/**",
            "/heartbeat",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/api-docs"
        )
    }
}