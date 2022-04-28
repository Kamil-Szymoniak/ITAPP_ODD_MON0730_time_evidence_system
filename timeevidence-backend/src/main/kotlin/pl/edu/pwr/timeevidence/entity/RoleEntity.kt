package pl.edu.pwr.timeevidence.entity

import javax.persistence.*

@Entity
@Table(name = "role", schema = "adm")
class RoleEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "role_generator")
    @SequenceGenerator(
        name = "role_generator",
        sequenceName = "role_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    var id: Short? = null,
    @Column(name = "name", nullable = false, unique = true)
    var name: String,
    @Column(name = "description", nullable = true)
    var description: String? = null,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "many_role_has_many_permission",
        schema = "adm",
        joinColumns = [JoinColumn(name = "id_role", referencedColumnName = "id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "id_permission", referencedColumnName = "id", nullable = false)]
    )
    var permissions: List<PermissionEntity> = emptyList()
) {
    constructor() : this(null, "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoleEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (permissions != other.permissions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.toInt() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + permissions.hashCode()
        return result
    }

    override fun toString(): String {
        return "RoleEntity(id=$id, name='$name', description=$description, permissions=${permissions.map { it.id }})"
    }


}