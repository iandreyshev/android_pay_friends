package ru.iandreyshev.payfriends.domain.computation

import ru.iandreyshev.payfriends.domain.core.*
import javax.inject.Inject

class GetComputationHistoryUseCase
@Inject constructor() {

    operator fun invoke(computation: Computation): List<HistoryBill> =
        computation.bills
            .sortedByDescending { it.creationDate.value }
            .map { bill ->
                HistoryBill(
                    id = bill.id,
                    transfers = bill.payments.map { payment ->
                        HistoryTransfer(
                            transfer = Transfer(
                                participants = Participants(
                                    bill.backer,
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
