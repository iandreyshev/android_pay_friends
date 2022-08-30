package ru.iandreyshev.payfriends.data.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ComputationEntity : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var title: String = ""
}
