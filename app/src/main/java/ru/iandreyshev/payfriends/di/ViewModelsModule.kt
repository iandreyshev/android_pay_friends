package ru.iandreyshev.payfriends.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.iandreyshev.payfriends.presentation.billEditor.BillEditorViewModel
import ru.iandreyshev.payfriends.presentation.computation.ComputationViewModel
import ru.iandreyshev.payfriends.presentation.computationEditor.ComputationEditorViewModel
import ru.iandreyshev.payfriends.presentation.computationsList.ComputationsListViewModel

@Module
interface ViewModelsModule {

    @Binds
    @[IntoMap ViewModelKey(ComputationsListViewModel::class)]
    fun bindComputationsList(viewModel: ComputationsListViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(ComputationEditorViewModel::class)]
    fun bindComputationEditor(viewModel: ComputationEditorViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(ComputationViewModel::class)]
    fun bindComputation(viewModel: ComputationViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(BillEditorViewModel::class)]
    fun bindBillEditor(viewModel: BillEditorViewModel): ViewModel

}
