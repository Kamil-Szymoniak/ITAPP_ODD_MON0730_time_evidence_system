package pl.edu.pwr.timeevidence.entity

import javax.persistence.*

@Entity
@Table(name = "user", schema = "adm")
class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_generator")
    @SequenceGenerator(
        name = "user_generator",
        sequenceName = "user_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    var id: Int? = null,
    @Column(name = "username", nullable = false, unique = true)
    var username: String,
    @Column(name = "email", nullable = false, unique = true)
    var email: String,
    @Column(name = "password", nullable = false)
    var password: String,
    //@Column(name = "id_person", nullable = false)
    //var person: PersonEntity,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "many_user_has_many_role",
        schema = "adm",
        joinColumns = [JoinColumn(name = "id_user", referencedColumnName = "id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "id_role", referencedColumnName = "id", nullable = false)]
    )
    var roles: List<RoleEntity> = emptyList()
) {
    constructor() : this(null, "", "", "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        if (id != other.id) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (password != other.password) return false
        //if (person != other.person) return false
        if (roles != other.roles) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        //result = 31 * result + person.hashCode()
        result = 31 * result + roles.hashCode()
        return result
    }
}