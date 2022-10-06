package ru.iandreyshev.payfriends.data.storage.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class PaymentEntity : RealmObject {
    @PrimaryKey
    var id: String = ""
    var receiver: MemberEntity? = null
    var cost: Int = 0
    var description: String = ""
    var creationDate: String = ""
}
