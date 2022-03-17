package pl.edu.pwr.timeevidence.dao

import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import pl.edu.pwr.timeevidence.entity.UserEntity
import java.util.*

interface UserRepository : CrudRepository<UserEntity, Int>, JpaSpecificationExecutor<UserEntity> {
    fun findUserByUsernameIgnoreCase(username: String): Optional<UserEntity>
    fun findByEmailIgnoreCase(email: String): Optional<UserEntity>
    fun existsByEmailIgnoreCase(email: String): Boolean
    fun existsByUsernameIgnoreCase(username: String): Boolean
}