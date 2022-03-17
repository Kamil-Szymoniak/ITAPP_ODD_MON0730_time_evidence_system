package pl.edu.pwr.timeevidence.dao

import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import pl.edu.pwr.timeevidence.entity.RoleEntity

interface RoleRepository : CrudRepository<RoleEntity, Short>, JpaSpecificationExecutor<RoleEntity> {

}