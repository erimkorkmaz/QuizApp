package com.erimkorkmaz.quizapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.erimkorkmaz.quizapp.ModelPreferencesManager
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.makeCircularAnonymousImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_card_profile.*


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var user: User
    private lateinit var selectedBitmap: Bitmap
    private var selectedPicture: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        storage = Firebase.storage
        db = Firebase.firestore
        user = ModelPreferencesManager.get<User>("KEY_USER")!!
        populateView()
        img_profile.setOnClickListener {
            selectImage()
        }
        button_logout.setOnClickListener {
            logout()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPicture = data.data
            selectedBitmap = if (Build.VERSION.SDK_INT >= 28) {
                val source =
                    ImageDecoder.createSource(requireActivity().contentResolver, selectedPicture!!)
                ImageDecoder.decodeBitmap(source)

            } else {
                MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    selectedPicture
                )
            }
            uploadImageToFirestore()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGallery, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun populateView() {
        text_profile_name.text = user.userName
        text_profile_email.text = user.email
        populateImage(user.profileImageUrl)
    }

    private fun populateImage(imageUrl: String?) {
        Glide.with(this)
            .load(imageUrl).apply(
                RequestOptions().circleCrop()
                    .placeholder(
                        makeCircularAnonymousImage(
                            context!!,
                            R.drawable.ic_anonymous
                        )
                    )
                    .error(makeCircularAnonymousImage(context!!, R.drawable.ic_anonymous))
            )
            .into(img_profile)
    }

    private fun selectImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intentToGallery, 2)
        }
    }

    private fun uploadImageToFirestore() {
        val storageRef = storage.reference
        val imagesRef: StorageReference? = storageRef.child("images")
        val uploadTask = imagesRef?.putFile(selectedPicture!!)
        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                populateImage(downloadUri.toString())
                user.profileImageUrl = downloadUri.toString()
                ModelPreferencesManager.put(user, "KEY_USER")
                saveImageToDb(downloadUri!!)
            }
        }
    }

    private fun saveImageToDb(uri: Uri) {
        db.collection("Users").document(auth.currentUser?.uid!!)
            .update("profileImageUrl", uri.toString())
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
    }

    private fun logout() {
        ModelPreferencesManager.preferences.edit().clear().apply()
        auth.signOut()
        startActivity(Intent(context, RegisterActivity::class.java))
        requireActivity().finish()
    }
}