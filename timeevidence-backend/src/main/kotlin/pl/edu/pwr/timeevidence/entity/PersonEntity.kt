package pl.edu.pwr.timeevidence.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "person", schema = "ppl")
class PersonEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "person_generator")
    @SequenceGenerator(
        name = "person_generator",
        sequenceName = "person_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    var id: Int? = null,
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "surname", nullable = false)
    var surname: String,
    @Column(name = "phone", nullable = true)
    var phone: String?,
    @Column(name = "birthday", nullable = false)
    var birthday: Date,
    @OneToOne(mappedBy = "person")
    var user: UserEntity? = null,
) {
    constructor() : this(null, "", "", "", Date())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (surname != other.surname) return false
        if (phone != other.phone) return false
        if (birthday != other.birthday) return false
        if (user != other.user) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + birthday.hashCode()
        result = 31 * result + (user?.hashCode() ?: 0)
        return result
    }

}