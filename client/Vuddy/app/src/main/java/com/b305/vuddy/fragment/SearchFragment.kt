package com.b305.vuddy.fragment

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.b305.vuddy.R
import com.b305.vuddy.databinding.FragmentSearchBinding
import com.b305.vuddy.model.FriendProfile
import com.b305.vuddy.model.SearchResponse
import com.b305.vuddy.util.RetrofitAPI
import com.b305.vuddy.util.SearchAdapter
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var otherSearchAdapter: SearchAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var otherRecyclerView: RecyclerView
    private var friendList: ArrayList<FriendProfile> = arrayListOf()
    private var noFriendList: ArrayList<FriendProfile> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)

        binding.ivMap.setOnClickListener {
            it.findNavController().navigate(R.id.action_searchFragment_to_mapFragment)
        }
        binding.ivFriend.setOnClickListener {
            it.findNavController().navigate(R.id.action_searchFragment_to_friendFragment)
        }
        binding.ivWrite.setOnClickListener {
            it.findNavController().navigate(R.id.action_searchFragment_to_writeFeedFragment)
        }
        binding.ivMessage.setOnClickListener {
            it.findNavController().navigate(R.id.action_searchFragment_to_messageFragment)
        }
        binding.ivProfile.setOnClickListener {
            it.findNavController().navigate(R.id.action_searchFragment_to_profileFragment)
        }
        binding.searchInputText.doOnTextChanged { text, _, _, _ ->
            val nickname = text?.toString()
            if (nickname?.length == 0) {
                friendList.clear()
                searchAdapter.notifyDataSetChanged()
                noFriendList.clear()
                searchAdapter.notifyDataSetChanged()
            } else if (nickname != null) {
                searchUsers(nickname)
            }
            Log.d(ContentValues.TAG, "입력값 $nickname")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myLayoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.friend_list)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.setHasFixedSize(true)
        searchAdapter = SearchAdapter(friendList)
        recyclerView.adapter = searchAdapter

        val otherLayoutManager = LinearLayoutManager(context)
        otherRecyclerView = view.findViewById<RecyclerView>(R.id.no_friend_list)
        otherRecyclerView.layoutManager = otherLayoutManager
        otherRecyclerView.setHasFixedSize(true)
        otherSearchAdapter = SearchAdapter(noFriendList)
        otherRecyclerView.adapter = otherSearchAdapter
    }

    private fun searchUsers(nickname: String) {
        val service = RetrofitAPI.friendService

        service.search(nickname).enqueue(object : Callback<SearchResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val searchData = result?.data

                    Log.d(ContentValues.TAG, "검색 친구 목록 ${searchData?.friends}")
                    Log.d(ContentValues.TAG, "검색 친구 목록 ${searchData?.noFriends}")

                    searchData?.friends?.let {
                        friendList.clear()
                        friendList.addAll(it)
                        searchAdapter.notifyDataSetChanged()
                    }

                    searchData?.noFriends?.let {
                        noFriendList.clear()
                        noFriendList.addAll(it)
                        otherSearchAdapter.notifyDataSetChanged()
                    }
                } else {
                    val errorMessage = JSONObject(response.errorBody()?.string()!!)
                    Toast.makeText(context, errorMessage.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Toast.makeText(context, "검색 실패", Toast.LENGTH_SHORT).show()
            }
        })

    }
}