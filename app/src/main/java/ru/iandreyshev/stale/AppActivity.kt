package ru.iandreyshev.stale

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.iandreyshev.stale.domain.calc.CalcResultUseCase
import ru.iandreyshev.stale.domain.core.Member
import ru.iandreyshev.stale.domain.core.Transaction
import ru.iandreyshev.stale.domain.core.TransactionParticipants

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
