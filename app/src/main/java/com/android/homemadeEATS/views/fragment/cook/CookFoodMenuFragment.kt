package com.android.homemadeEATS.views.fragment.cook

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentCookFoodMenuBinding
import com.android.homemadeEATS.model.common.Meal
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.viewmodel.cook.CookMenuViewModel
import com.android.homemadeEATS.views.activity.common.ViewAddressActivity
import com.android.homemadeEATS.views.activity.cook.CookNavActivity
import com.android.homemadeEATS.views.fragment.common.BaseFragment
import com.android.homemadeEATS.views.fragment.common.ImageDisplayFragment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_cook_food_menu.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CookFoodMenuFragment : BaseFragment() {
    private var menuPostLiveData: MutableLiveData<PostResponse> = MutableLiveData()
    private var currentMealType: String = ""
    private val BREAKFAST: String = "Breakfast"
    private val LUNCH: String = "Lunch"
    private val DINNER: String = "Dinner"
    private var imageUriBreakfast: ArrayList<Uri> = ArrayList()
    private var imageUriLunch: ArrayList<Uri> = ArrayList()
    private var imageUriDinner: ArrayList<Uri> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CookMenuViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentCookFoodMenuBinding>(
            inflater,
            R.layout.fragment_cook_food_menu,
            container,
            false
        ).apply {
            viewModel = this@CookFoodMenuFragment.viewModel as CookMenuViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocation()
        handleButtonClickListeners()
    }

    private fun getLocation() {
        (viewModel as CookMenuViewModel).getSelectedLocation().observe(
            viewLifecycleOwner,
            { result ->
                if (result != null) {
                    selectedLocation.text = result.saveAs
                }
            }
        )
    }

    private fun handleButtonClickListeners() {
        attachBreakfastFoodImage.setOnClickListener {
            checkPermission(1)
        }

        attach_lunch_food_image.setOnClickListener {
            checkPermission(2)
        }

        attach_dinner_food_image.setOnClickListener {
            checkPermission(3)
        }

        outerLlBreakfastImages.setOnClickListener {
            val fragment = ImageDisplayFragment()
            val bundle = Bundle()
            bundle.putSerializable("images", imageUriBreakfast)
            fragment.arguments = bundle
            fragment.show((context as CookNavActivity).supportFragmentManager, "")
        }

        add_breakfast_item_to_chips.setOnClickListener {
            if (et_breakfast_item.text.toString().isEmpty() || et_breakfast_item_qty.text.toString()
                    .isEmpty()
            ) {
                breakfast_add_error.visibility = View.VISIBLE
            } else {
                breakfast_add_error.visibility = View.GONE
                addToChip(
                    et_breakfast_item.text.toString() + ": " + et_breakfast_item_qty.text.toString(),
                    chipGroupBreakfast
                )
                et_breakfast_item.setText("")
                et_breakfast_item_qty.setText("")
            }
        }

        add_lunch_item_to_chips.setOnClickListener {
            if (et_lunch_item.text.toString().isEmpty() || et_lunch_item_qty.text.toString()
                    .isEmpty()
            ) {
                lunch_add_error.visibility = View.VISIBLE
            } else {
                lunch_add_error.visibility = View.GONE
                addToChip(
                    et_lunch_item.text.toString() + ": " + et_lunch_item_qty.text.toString(),
                    chipGroupLunch
                )
                et_lunch_item.setText("")
                et_lunch_item_qty.setText("")
            }
        }

        add_dinner_item_to_chips.setOnClickListener {
            if (et_dinner_item.text.toString().isEmpty() || et_dinner_item_qty.text.toString()
                    .isEmpty()
            ) {
                dinner_add_error.visibility = View.VISIBLE
            } else {
                dinner_add_error.visibility = View.GONE
                addToChip(
                    et_dinner_item.text.toString() + ": " + et_dinner_item_qty.text.toString(),
                    chipGroupdinner
                )
                et_dinner_item.setText("")
                et_dinner_item_qty.setText("")
            }
        }

        btn_save_breakfast.setOnClickListener {
            if (checkSaveMenuValidations(chipGroupBreakfast, et_breakfast_item_price)) {
                val message =
                    getString(R.string.menu_saving_before, getString(R.string.menu_breakfast_title))
                showWarningDialogBox(
                    message,
                    BREAKFAST,
                    chipGroupBreakfast,
                    et_breakfast_item_price.text.toString(),
                    imageUriBreakfast
                )
            } else {
                val message = getString(R.string.menu_saving_validation)
                showDialogBox(message)
            }

        }
        btn_save_lunch.setOnClickListener {
            if (checkSaveMenuValidations(chipGroupLunch, et_lunch_item_price)) {
                val message =
                    getString(R.string.menu_saving_before, getString(R.string.menu_lunch_title))
                showWarningDialogBox(
                    message,
                    LUNCH,
                    chipGroupLunch,
                    et_lunch_item_price.text.toString(),
                    imageUriLunch
                )
            } else {
                val message = getString(R.string.menu_saving_validation)
                showDialogBox(message)
            }
        }
        btn_save_dinner.setOnClickListener {
            if (checkSaveMenuValidations(chipGroupdinner, et_dinner_item_price)) {
                val message =
                    getString(R.string.menu_saving_before, getString(R.string.menu_dinner_title))
                showWarningDialogBox(
                    message,
                    DINNER,
                    chipGroupdinner,
                    et_dinner_item_price.text.toString(),
                    imageUriDinner
                )
            } else {
                val message = getString(R.string.menu_saving_validation)
                showDialogBox(message)
            }
        }

        (viewModel as CookMenuViewModel)._selectedLocation.observe(
            viewLifecycleOwner
        ) {
            val address = it
            llCookLocation.setOnClickListener {
                val intent = Intent(requireContext(), ViewAddressActivity::class.java)
                intent.putExtra("Selected Address", address)
                startActivity(intent)
            }
        }

    }

    private fun setImages(imageSlider: ImageSlider, imageUriBreakfast: ArrayList<Uri>) {
        if (imageUriBreakfast.isNotEmpty()) {
            imageSlider.visibility = View.VISIBLE
        } else {
            imageSlider.visibility = View.GONE
        }

        val imageListSlideModel = ArrayList<SlideModel>()
        if (!imageUriBreakfast.isNullOrEmpty()) {
            for (i in imageUriBreakfast) {
                imageListSlideModel.add(SlideModel(i.toString(), ScaleTypes.CENTER_CROP))
            }
        }
        imageSlider.setImageList(imageListSlideModel)
    }

    private fun getMenuItemsList(chipGroup: ChipGroup): ArrayList<Meal> {
        val menuItemsList: ArrayList<Meal> = ArrayList()
        for (id in 0 until chipGroup.childCount) {
            val chip: Chip = chipGroup.getChildAt(id) as Chip
            val chipSplitArray: List<String> = chip.text.toString().split(": ")
            menuItemsList.add(
                Meal(
                    chipSplitArray.get(0),
                    chipSplitArray.get(1),
                )
            )
        }
        return menuItemsList
    }

    private fun checkPermission(requestCode: Int) {
        // Checking if permission is not granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), requestCode
            )
        } else {
            openSomeActivityForResult(requestCode)
        }
    }

    private fun saveFoodMenu(
        mealType: String,
        chipGroup: ChipGroup,
        price: String,
        uris: ArrayList<Uri>
    ) {
        val foodMenu = getMenuItemsList(chipGroup)

        if (menuPostLiveData.hasObservers()) {
            menuPostLiveData.removeObservers(this)
        }

        val parts: ArrayList<MultipartBody.Part?> = ArrayList()
        for (uri: Uri in uris) {
            val originalFile = File(getPathFromURI(uri))
            val filePart = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                originalFile
            )
            parts.add(MultipartBody.Part.createFormData("photos", mealType, filePart))
        }

        menuPostLiveData = (viewModel as CookMenuViewModel).setFoodMenu(
            MultipartBody.Part.createFormData("mealType", mealType),
            MultipartBody.Part.createFormData(
                "meal",
                Gson().toJson(foodMenu)
            ),
            MultipartBody.Part.createFormData("price", price),
            parts
        )

        menuPostLiveData.observe(viewLifecycleOwner) {
            it?.message?.let { it1 ->
                showDialogBox(it1)
            }
            if (!it?.error.isNullOrEmpty()) {
                Toast.makeText(context, it?.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openSomeActivityForResult(requestCode)
        } else {
            Toast.makeText(
                context,
                "Permission Denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkSaveMenuValidations(chipGroup: ChipGroup, price: EditText): Boolean {
        return chipGroup.childCount != 0 && price.text.toString().isNotEmpty()
    }

    private var resultLauncher1 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result?.resultCode
            val intent: Intent? = result.data
            if (resultCode == RESULT_OK) {
                val selectedImages: ClipData = intent?.clipData!!
                if (selectedImages.itemCount > 4 || ll_breakfast_images.childCount + selectedImages.itemCount > 4) {
                    showAddImageErrorDialog()
                } else {
                    outerLlBreakfastImages.visibility = View.VISIBLE
                    updateUI(
                        selectedImages,
                        ll_breakfast_images,
                        BREAKFAST
                    )
                }
            } else {
                Toast.makeText(
                    context,
                    "Sorry! unable to upload image/images",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    private var resultLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result?.resultCode
            val intent: Intent? = result.data
            if (resultCode == RESULT_OK) {
                val selectedImages: ClipData = intent?.clipData!!
                if (selectedImages.itemCount > 4 || ll_lunch_images.childCount + selectedImages.itemCount > 4) {
                    showAddImageErrorDialog()
                } else {
                    outer_ll_lunch_images.visibility = View.VISIBLE
                    updateUI(
                        selectedImages,
                        ll_lunch_images,
                        LUNCH
                    )
                }
            } else {
                Toast.makeText(
                    context,
                    "Sorry! unable to upload image/images",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    private var resultLauncher3 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result?.resultCode
            val intent: Intent? = result.data
            if (resultCode == RESULT_OK) {
                val selectedImages: ClipData = intent?.clipData!!
                if (selectedImages.itemCount > 4 || ll_dinner_images.childCount + selectedImages.itemCount > 4) {
                    showAddImageErrorDialog()
                } else {
                    outer_ll_dinner_images.visibility = View.VISIBLE
                    updateUI(
                        selectedImages,
                        ll_dinner_images,
                        DINNER
                    )
                }
            } else {
                Toast.makeText(
                    context,
                    "Sorry! unable to upload image/images",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun openSomeActivityForResult(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        Intent.createChooser(intent, "Select Picture")
        when (requestCode) {
            1 -> {
                resultLauncher1.launch(intent)
            }

            2 -> {
                resultLauncher2.launch(intent)
            }

            3 -> {
                resultLauncher3.launch(intent)
            }
        }
    }

    private fun getPathFromURI(uri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = requireActivity().contentResolver
            .query(uri, proj, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            res =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            cursor.close()
        }
        return res
    }

    private fun updateUI(
        selectedImages: ClipData,
        llImages: LinearLayout,
        mealType: String
    ) {
        for (i: Int in 0 until selectedImages.itemCount) {
            val imageView = AppCompatImageView(requireContext())
            imageView.setImageURI(selectedImages.getItemAt(i).uri)
            imageView.layoutParams = FrameLayout.LayoutParams(
                100,
                100
            )
            llImages.addView(imageView)

            val item: ClipData.Item = selectedImages.getItemAt(i)
            val mimeTypes = arrayOf("image/*")
            val data = ClipData(
                imageView.getTag() as? CharSequence,
                mimeTypes,
                item
            )

            imageView.setOnLongClickListener {
                val shadowBuilder = DragShadowBuilder(imageView)
                currentMealType = mealType
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageView.startDragAndDrop(
                        data,
                        shadowBuilder,
                        iv_delete_image,
                        0
                    )
                } else {
                    imageView.startDrag(
                        data,
                        shadowBuilder,
                        iv_delete_image,
                        0
                    )
                }
                return@setOnLongClickListener true
            }
            when (mealType) {
                BREAKFAST -> {
                    imageUriBreakfast.add(selectedImages.getItemAt(i).uri)
                    setImages(imageSliderBreakfast, imageUriBreakfast)
                    iv_delete_image.setOnDragListener(
                        dragListener()
                    )
                }

                LUNCH -> {
                    imageUriLunch.add(selectedImages.getItemAt(i).uri)
                    setImages(imageSliderLunch, imageUriLunch)
                    iv_delete_image.setOnDragListener(
                        dragListener()
                    )
                }

                DINNER -> {
                    imageUriDinner.add(selectedImages.getItemAt(i).uri)
                    setImages(imageSliderDinner, imageUriDinner)
                    iv_delete_image.setOnDragListener(
                        dragListener()
                    )
                }
            }

        }
    }

    private fun dragListener(
    ): View.OnDragListener {
        var imageUri: ArrayList<Uri>
        var llImages: LinearLayout
        var outerll: LinearLayout
        when (currentMealType) {
            LUNCH -> {
                imageUri = imageUriLunch
                llImages = ll_lunch_images
                outerll = outer_ll_lunch_images
            }

            DINNER -> {
                imageUri = imageUriDinner
                llImages = ll_dinner_images
                outerll = outer_ll_dinner_images
            }

            else -> {
                imageUri = imageUriBreakfast
                llImages = ll_breakfast_images
                outerll = outerLlBreakfastImages
            }
        }
        return View.OnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    when (currentMealType) {
                        LUNCH -> {
                            imageUri = imageUriLunch
                            llImages = ll_lunch_images
                            outerll = outer_ll_lunch_images
                        }

                        DINNER -> {
                            imageUri = imageUriDinner
                            llImages = ll_dinner_images
                            outerll = outer_ll_dinner_images
                        }

                        else -> {
                            imageUri = imageUriBreakfast
                            llImages = ll_breakfast_images
                            outerll = outerLlBreakfastImages
                        }
                    }
                    iv_delete_image.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.holo_red_light
                        )
                    )
                    sv_food_menu.alpha = 0.2f
                    iv_delete_image.alpha = 1f
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    iv_delete_image.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.white
                        )
                    )
                    iv_delete_image.alpha = 0f
                    ll_food_menu.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.white
                        )
                    )
                    sv_food_menu.alpha = 1f
                    true
                }

                DragEvent.ACTION_DROP -> {
                    for (i: Int in 0 until imageUri.size) {
                        if (imageUri.get(i).equals(event.clipData.getItemAt(0).uri)) {
                            llImages.removeView(llImages.getChildAt(i))
                            imageUri.removeAt(i)
                            break
                        }
                    }
                    if (llImages.childCount == 0) {
                        iv_delete_image.alpha = 0f
                        outerll.visibility = View.GONE
                    }
                    true
                }

                else -> {
                    //do nothing
                    true
                }
            }
        }
    }

    private fun showAddImageErrorDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.important_note_dialog_title))
        builder.setMessage(getString(R.string.image_selection_limit_error))
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
            //do nothing
        }
        builder.setCancelable(false);
        builder.show()

    }

    private fun showDialogBox(message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.great_dialog_title))
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
            //do nothing
        }
        builder.setCancelable(false);
        builder.show()
    }

    private fun showWarningDialogBox(
        message: String,
        mealType: String,
        chipGroup: ChipGroup,
        price: String,
        uris: ArrayList<Uri>
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.important_note_dialog_title))
        builder.setMessage(message)
        builder.setPositiveButton("SAVE") { dialogInterface: DialogInterface, i: Int ->
            saveFoodMenu(mealType, chipGroup, price, uris)
        }
        builder.setNegativeButton("CANCEL") { dialogInterface: DialogInterface, i: Int ->
            //do nothing
        }
        builder.setCancelable(false);
        builder.show()
    }

    private fun addToChip(itemName: String, chipGroup: ChipGroup) {
        val chip = Chip(context)
        chip.text = itemName
        chip.isCloseIconVisible = true
        chip.setChipIconTintResource(R.color.dark_brown)
        chip.isClickable = true
        chip.isCheckable = false
        chipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener { chipGroup.removeView(chip as View) }
    }
}