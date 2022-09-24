package com.example.kotlinartbookfragment.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.kotlinartbookfragment.Model.Art
import com.example.kotlinartbookfragment.Adapter.ArtAdapter
import com.example.kotlinartbookfragment.RoomDb.ArtDao
import com.example.kotlinartbookfragment.RoomDb.ArtDatabase
import com.example.kotlinartbookfragment.databinding.FragmentMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private  val mDisposable = CompositeDisposable()
    private lateinit var artDao:ArtDao
    private lateinit var db:ArtDatabase
    private lateinit var artAdapter: ArtAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db=Room.databaseBuilder(requireContext(),ArtDatabase::class.java,"Arts").build()
        artDao=db.artDao()





    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding=FragmentMainBinding.inflate(inflater,container,false)

        val view=binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getFromSQL()
    }

    fun getFromSQL() {
        mDisposable.add(artDao.getArtWithNameAndId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse))
    }

    private fun handleResponse(artList: List<Art>) {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        artAdapter = ArtAdapter(artList)
        binding.recyclerview.adapter = artAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }



}
