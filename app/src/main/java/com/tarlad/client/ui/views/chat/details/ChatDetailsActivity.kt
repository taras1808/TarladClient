package com.tarlad.client.ui.views.chat.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tarlad.client.R
import com.tarlad.client.databinding.ActivityChatDetailsBinding
import com.tarlad.client.ui.adapters.ChatDetailsAdapter
import com.tarlad.client.ui.views.chat.participants.ChatAddParticipantsActivity
import kotlinx.android.synthetic.main.sheet_details.view.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class ChatDetailsActivity : AppCompatActivity() {

    private val adapter = ChatDetailsAdapter(arrayListOf())

    private val vm by viewModel<ChatDetailsViewModel> { parametersOf(lifecycleScope.id) }

    var chatId: Long = -1

    lateinit var binding: ActivityChatDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatId = intent.getLongExtra("ID",-1L)
        vm.title.value = "Details"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_details)
        binding.vm = vm
        binding.lifecycleOwner = this

        setSupportActionBar(binding.toolbarInclude.toolbar)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP

        binding.participantsRecycler.adapter = adapter

        adapter.userId = vm.appSession.userId

        vm.loadChatTitle(chatId)
        vm.loadUsers(chatId)
        vm.loadAdmin(chatId)

        observeUsers()
        observeAdmin()

        vm.chatTitle.observe(this, Observer {
            invalidateOptionsMenu()
        })

        vm.chatTitleSaved.observe(this, Observer {
            binding.chatTitle.clearFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(binding.chatTitle.windowToken, 0)
            invalidateOptionsMenu()
        })

        adapter.listener = { id -> runOnUiThread { show(id) } }
    }

    private fun show(id: Long) {

        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.sheet_details, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetView.delete.setOnClickListener {
            vm.removeParticipant(chatId, id)
            bottomSheetDialog.dismiss()
        }
        bottomSheetView.cancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

        val bottomSheetDialogFrameLayout =
            bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheetDialogFrameLayout?.background = null
    }

    fun openAddParticipants(v: View) {
        val intent = Intent(this, ChatAddParticipantsActivity::class.java)
        intent.putExtra("ID", chatId)
        startActivity(intent)
    }

    private fun observeUsers() {
        vm.users.observe(this , Observer {
            adapter.users.clear()
            adapter.users.addAll(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun observeAdmin() {
        vm.admin.observe(this, Observer {
            adapter.id = it
            adapter.notifyDataSetChanged()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (vm.chatTitle.value != vm.chatTitleSaved.value)
            menuInflater.inflate(R.menu.menu_chat_create, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add_chat_ok -> vm.changeTitle(chatId)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}
