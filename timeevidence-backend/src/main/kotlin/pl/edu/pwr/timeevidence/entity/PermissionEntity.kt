package pl.edu.pwr.timeevidence.entity

import javax.persistence.*

@Entity
@Table(name = "permission", schema = "adm")
class PermissionEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "permission_generator")
    @SequenceGenerator(
        name = "permission_generator",
        sequenceName = "permission_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    var id: Short? = null,
    @Column(name = "name", nullable = false, unique = true)
    var name: String,
    @Column(name = "description", nullable = true)
    var description: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.toInt() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}