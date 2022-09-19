package ru.iandreyshev.payfriends.data.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.*

open class ComputationEntity : RealmObject {
    @PrimaryKey
    var id: String = ""
    var title: String = ""
    var creationDate: String = ""
    var isCompleted: Boolean = false
    var members = realmListOf<MemberEntity>()
    var bills = realmListOf<BillEntity>()
}
