package ru.iandreyshev.payfriends.data.storage.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class BillEntity : RealmObject {
    @PrimaryKey
    var id: String = ""
    var title: String = ""
    var backer: MemberEntity? = null
    var creationDate: String = ""
    var payments: RealmList<PaymentEntity> = realmListOf()
}
