package com.tarlad.client.di

import com.tarlad.client.AppDatabase
import com.tarlad.client.api.UsersApi
import com.tarlad.client.repos.ChatsRepo
import com.tarlad.client.repos.UsersRepo
import com.tarlad.client.repos.impl.ChatsRepoImpl
import com.tarlad.client.repos.impl.UsersRepoImpl
import com.tarlad.client.ui.views.addChat.AddChatActivity
import com.tarlad.client.ui.views.addChat.AddChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.ScopeID
import org.koin.dsl.module
import retrofit2.Retrofit

val addChatModule = module {
    scope<AddChatActivity> {

    }

    viewModel { (scopeId: ScopeID) ->
        AddChatViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
}