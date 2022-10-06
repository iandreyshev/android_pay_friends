package ru.iandreyshev.payfriends.data.storage.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class MemberEntity() : RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""

    constructor(name: String) : this() {
        this.name = name
    }

}
