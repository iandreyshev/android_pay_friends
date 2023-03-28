package ru.iandreyshev.payfriends.domain.computation

import ru.iandreyshev.payfriends.domain.core.*
import javax.inject.Inject

class GetComputationHistoryUseCase
@Inject constructor() {

    operator fun invoke(computation: Computation): List<HistoryBill> =
        computation.bills
            .sortedByDescending { it.creationDate.value }
            .mapIndexed { i, bill ->
                HistoryBill(
                    id = bill.id,
                    title = bill.title,
                    number = i + 1,
                    transfers = bill.payments.map { payment ->
                        HistoryTransfer(
                            transfer = Transfer(
                                participants = Participants(
                                    bill.producer,
                                    payment.receiver
                                ),
                                cost = payment.cost
                            ),
                            description = payment.description,
                        )
                    },
                    date = bill.creationDate
                )
            }

}
