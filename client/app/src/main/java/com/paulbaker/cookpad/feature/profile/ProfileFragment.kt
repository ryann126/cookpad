package com.paulbaker.cookpad.feature.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
//import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.paulbaker.cookpad.HomeScreenActivity
import com.paulbaker.cookpad.R
import com.paulbaker.cookpad.core.DATA_USER
import com.paulbaker.cookpad.core.extensions.Status
import com.paulbaker.cookpad.core.utils.Utils
import com.paulbaker.cookpad.data.datasource.local.UpdateUser
import com.paulbaker.cookpad.data.datasource.local.User
import com.paulbaker.cookpad.data.datasource.remote.UserProfileResponse
import com.paulbaker.cookpad.databinding.FragmentProfileBinding
import com.paulbaker.cookpad.feature.creation.fragment.CreateNewFood
import com.paulbaker.cookpad.feature.login.viewmodel.UserViewModel
import com.paulbaker.library.core.extension.Utils.Companion.toBase64
import com.paulbaker.library.core.extension.isNotNull
import com.paulbaker.library.core.extension.isValidValue
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(), View.OnClickListener, View.OnTouchListener {

    companion object{
        const val REQUEST_CODE_PICK_PROFILE = 2023
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var user: User? = null
    private var uri: Uri? = null

    private var startClickTime = 0L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        getProfile()
        observerUser()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun observerUser() {
        user = Gson().fromJson(
            HomeScreenActivity.saveUser?.getString(DATA_USER, Gson().toJson(User())),
            User::class.java
        )
    }


    private fun getProfile() {
        val userId = Gson().fromJson(
            HomeScreenActivity.saveUser?.getString(DATA_USER, Gson().toJson(User())),
            User::class.java
        ).id
        if (userId == null) {
            binding.imgError.visibility = View.VISIBLE
            binding.rootContent.visibility = View.GONE
            return
        }
        userViewModel.getProfile(userId).observe(viewLifecycleOwner) { resourceResponse ->
            resourceResponse.let { resources ->
                when (resources.status) {
                    Status.LOADING -> {
                        binding.rootProfile.isClickable = false
                        binding.pbLoading.visibility = View.VISIBLE
                        binding.imgError.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        if (resources.data?.body()?.success == true) {
                            binding.pbLoading.visibility = View.GONE
                            binding.rootProfile.isClickable = true
                            updateUI(resources.data.body())
                        } else {
                            binding.pbLoading.visibility = View.GONE
                            binding.rootProfile.isClickable = true
                            binding.imgError.visibility = View.VISIBLE
                        }
                    }
                    Status.ERROR -> {
                        binding.imgError.visibility = View.VISIBLE
                        binding.rootContent.visibility = View.GONE
                        binding.rootProfile.isClickable = true
                        Log.d("TAG", "error message: ${resources.message}")
                    }
                }
            }
        }
    }

    private fun updateUI(data: UserProfileResponse?) {
        if (data?.data?.avatar.toString().isValidValue() && data?.data?.avatar.toString()
                .isNotNull()
        ) {
            binding.userAnhNguoiDung.setImageBitmap(
                com.paulbaker.library.core.extension.Utils.decodeBase64ToBitMap(data?.data?.avatar.toString())
            )
        }
        binding.userTextTenNguoiDung.text = data?.data?.name
        binding.userTenNguoiDungCoTheNhap.setText(data?.data?.name)
        binding.edtAddress.setText(data?.data?.address)
        if (data?.data?.cookpadId?.isValidValue() == true) {
            binding.userIDCoTheNhap.setText(data.data.cookpadId)
        }
        binding.userMailCoTheNhap.setText(data?.data?.address)
        binding.edtAbout.setText(data?.data?.about)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListener() {
        binding.userButtonCapNhap.setOnClickListener(this)
        binding.rootContent.setOnTouchListener(this)
        binding.userAnhNguoiDung.setOnClickListener(this)
        setupTextWatch()
    }

    private fun setupTextWatch() {
        binding.userTenNguoiDungCoTheNhap.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.userButtonCapNhap.isEnabled =
                    user?.name != binding.userTenNguoiDungCoTheNhap.text.toString() &&
                            binding.userTenNguoiDungCoTheNhap.text.toString().isValidValue() &&
                            binding.userTenNguoiDungCoTheNhap.text.toString().isNotNull()
            }
        })
        binding.userIDCoTheNhap.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.userButtonCapNhap.isEnabled =
                    user?.cookPadId != binding.userIDCoTheNhap.text.toString() &&
                            binding.userIDCoTheNhap.text.toString().isValidValue() &&
                            binding.userIDCoTheNhap.text.toString().isNotNull()
            }
        })
        binding.userMailCoTheNhap.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.userButtonCapNhap.isEnabled =
                    user?.email != binding.userMailCoTheNhap.text.toString() &&
                            binding.userMailCoTheNhap.text.toString().isValidValue() &&
                            binding.userMailCoTheNhap.text.toString().isNotNull()
            }
        })
        binding.edtAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.userButtonCapNhap.isEnabled =
                    user?.about != binding.edtAddress.text.toString() &&
                            binding.edtAddress.text.toString().isValidValue() &&
                            binding.edtAddress.text.toString().isNotNull()
            }
        })
        binding.edtAbout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.userButtonCapNhap.isEnabled =
                    user?.about != binding.edtAbout.text.toString() &&
                            binding.edtAbout.text.toString().isValidValue() &&
                            binding.edtAbout.text.toString().isNotNull()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.user_button_CapNhap -> {
                updateUserProfile()
            }
            R.id.user_AnhNguoiDung -> {
                loadAndUpdateImage(REQUEST_CODE_PICK_PROFILE)
            }
        }
    }

    private fun loadAndUpdateImage(requestCode: Int) {
//        ImagePicker.with(this)
//            .crop()
//            .compress(1024)
//            .maxResultSize(
//                1080,
//                1080
//            )
//            .start()
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }

    private fun updateUserProfile() {
        user?.id?.let {
            userViewModel.updateProfile(
                userId = it, UpdateUser(
                    name = binding.userTenNguoiDungCoTheNhap.text.toString(),
                    email = binding.userMailCoTheNhap.text.toString(),
                    cookpadId = binding.userIDCoTheNhap.text.toString(),
                    about = binding.edtAbout.text.toString(),
                    address = binding.edtAddress.text.toString(),
                    avatar = uri?.toFile()?.toBase64()
                )
            ).observe(viewLifecycleOwner) { resourceResponse ->
                resourceResponse.let { resources ->
                    when (resources.status) {
                        Status.LOADING -> {
                            binding.rootProfile.isClickable = false
                            binding.pbLoading.visibility = View.VISIBLE
                            binding.imgError.visibility = View.GONE
                            binding.userButtonCapNhap.isEnabled = false
                        }
                        Status.SUCCESS -> {
                            if (resources.data?.body()?.success == true) {
                                binding.pbLoading.visibility = View.GONE
                                binding.rootProfile.isClickable = true
                                updateUI(resources.data.body())
                            } else {
                                binding.pbLoading.visibility = View.GONE
                                binding.rootProfile.isClickable = true
                                binding.imgError.visibility = View.VISIBLE
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                "Có lỗi xảy ra vui lòng thử lại sau !",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.imgError.visibility = View.VISIBLE
                            binding.rootContent.visibility = View.GONE
                            binding.rootProfile.isClickable = true
                            binding.userButtonCapNhap.isEnabled = true
                            Log.d("TAG", "error message: ${resources.message}")
                        }
                    }
                }

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            startClickTime = System.currentTimeMillis()
        } else if (event?.action == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                Utils.hideKeyboard(requireContext(), binding.userTenNguoiDungCoTheNhap)
                activity?.window?.decorView?.clearFocus()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        uri = data?.data!!
                        binding.userAnhNguoiDung.setImageBitmap(
                            com.paulbaker.library.core.extension.Utils.decodeBase64ToBitMap(
                                uri?.toFile()?.toBase64()
                            )
                        )
                    }
                }
            }
            else -> {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}