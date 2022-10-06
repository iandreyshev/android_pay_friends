package ru.iandreyshev.payfriends.data.storage.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class ComputationEntity : RealmObject {
    @PrimaryKey
    var id: String = ""
    var title: String = ""
    var creationDate: String = ""
    var isCompleted: Boolean = false
    var members = realmListOf<MemberEntity>()
    var bills = realmListOf<BillEntity>()
}
