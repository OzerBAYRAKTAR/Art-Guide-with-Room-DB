package com.example.kotlinartbookfragment.Fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.Person.fromBundle
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.kotlinartbookfragment.Activity.MainActivity
import com.example.kotlinartbookfragment.Fragments.UploadFragmentArgs.Companion.fromBundle
import com.example.kotlinartbookfragment.Model.Art
import com.example.kotlinartbookfragment.R
import com.example.kotlinartbookfragment.RoomDb.ArtDao
import com.example.kotlinartbookfragment.RoomDb.ArtDatabase
import com.example.kotlinartbookfragment.databinding.FragmentUploadBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.nio.file.Files.delete

class UploadFragment : Fragment() {

    var selectedBitmap:Bitmap?=null
    var selectedPicture: Uri?=null
    private  var _binding: FragmentUploadBinding?=null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var artDatabase: ArtDatabase
    private lateinit var artDao:ArtDao

    private val mDisposable =CompositeDisposable()
    var artFromMain:Art? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerLauncher()

        artDatabase=Room.databaseBuilder(requireContext(),ArtDatabase::class.java,"Arts").build()
        artDao=artDatabase.artDao()





    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding=FragmentUploadBinding.inflate(layoutInflater,container,false)
        val view=binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveButton.setOnClickListener { save(view) }
        binding.imageView.setOnClickListener { selectImage(view) }
        binding.deleteButton.setOnClickListener { delete(view) }

       arguments?.let {
        val info = UploadFragmentArgs.fromBundle(it).info

        if (info.equals("new")) {
            //NEW
            binding.textArtName.setText("")
            binding.textArtistName.setText("")
            binding.textYear.setText("")
            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE

            val selectedImageBackground = BitmapFactory.decodeResource(context?.resources,
                R.drawable.select
            )
            binding.imageView.setImageBitmap(selectedImageBackground)

        } else {
            //OLD
            binding.saveButton.visibility = View.GONE
            binding.deleteButton.visibility = View.VISIBLE

            val selectedId = UploadFragmentArgs.fromBundle(it).id
            mDisposable.add(artDao.getArtById(selectedId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseWithOldArt))

        }
    }
    }
    private fun handleResponseWithOldArt(art : Art) {
        artFromMain = art
        binding.textArtName.setText(art.artName)
        binding.textArtistName.setText(art.artistName)
        binding.textYear.setText(art.year)
        art.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    fun save(view : View){

            val artName=binding.textArtName.text.toString()
            val artistName=binding.textArtistName.text.toString()
            val year=binding.textYear.text.toString()

            if (selectedBitmap != null){
                val smallBitmap=makeSmallerBitmap(selectedBitmap!!,300)

                val outputStream= ByteArrayOutputStream()
                smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                val byteArray=outputStream.toByteArray()

                val art = Art(artName,artistName,year,byteArray)

                mDisposable.add(artDao.insert(art)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse))
            }
     }
    fun selectImage(view: View){
        activity?.let {

            if (ContextCompat.checkSelfPermission(requireContext().applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"PERMİSSİON NEEDED FOR GALLERY",Snackbar.LENGTH_INDEFINITE).setAction("give permission",View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }
    private fun handleResponse() {
            val action = UploadFragmentDirections.actionUploadFragmentToMainFragment()
            Navigation.findNavController(requireView()).navigate(action)
}

    //Bitmap olan image istiyoruz. sonrasında bitmap döndürüyoruz.
    fun makeSmallerBitmap(image : Bitmap,maximumSize: Int):Bitmap {

        var width=image.width
        var height=image.height

        var bitmapRatio : Double =width.toDouble() /height.toDouble()

        if (bitmapRatio > 1){
            //landscope(yatay)

            //yatay olursa bulunan oranı genişliğe böl ve bunu yüksekliğe ata.
            width=maximumSize
            val scaledHeight=width/bitmapRatio
            height=scaledHeight.toInt()

                  }else{
                      //dikey olursa bulunan oranı uzunluğa böl ve genişliğe ata.
                      height=maximumSize
                      val scaledWidth=height/bitmapRatio
                      width=scaledWidth.toInt()

                  }


                  return Bitmap.createScaledBitmap(image,width,height,true)
              }
    //global olarak tanımlanan activityresultlauncherı register(initilaze) etmemiz lazım. uzun old. için fonks. olarak yazdık.
    private fun registerLauncher(){

            //en sona activityresultcallback yazabilridik. fakat lambda gösterimi de yapılabilir.
            activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
                //kullanıcı galeriye gitti mi diye kontrol ettik.
                if (result.resultCode== RESULT_OK){
                    val intentResult=result.data
                    if (intentResult !=null){

                        val imageData=intentResult.data
                        //binding.imageView.setImageURI(imageData) bunun yerine bitmape çevirip onu imageviewde gösteririz.
                        //uri'ı bitmape çevireceğiz.
                        if(imageData !=null){

                        try {
                            if (Build.VERSION.SDK_INT>=28) {
                                val source = ImageDecoder.createSource(requireActivity().contentResolver, imageData)
                                selectedBitmap =ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }else{
                                selectedBitmap=MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }

                        }catch (e:Exception){
                            e.printStackTrace()

                        }
                        }

                    }



                }

            }
            permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
                    if (result){
                            //permission granted
                        val intenGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(intenGallery)

                    }else{
                        //permission denied
                        Toast.makeText(getActivity(), "Permission Needed!", Toast.LENGTH_LONG).show()

                    }

            }


        }

    fun delete(view :View){
        artFromMain?.let {
            mDisposable.add(artDao.delete(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }





 }