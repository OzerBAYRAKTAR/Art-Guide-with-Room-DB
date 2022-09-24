package com.example.kotlinartbookfragment.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinartbookfragment.Model.Art
import com.example.kotlinartbookfragment.Fragments.MainFragmentDirections
import com.example.kotlinartbookfragment.databinding.RecyclerRowBinding

class ArtAdapter(val artList:List<Art>) : RecyclerView.Adapter<ArtAdapter.PostHolder> (){

    class PostHolder(val binding: RecyclerRowBinding) :RecyclerView.ViewHolder(binding.root){

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {

        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.textView.text=artList.get(position).artName
        holder.itemView.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToUploadFragment("old",0)
            Navigation.findNavController(it).navigate(action)

        }

        }

    override fun getItemCount(): Int {
        return artList.size
    }


}





