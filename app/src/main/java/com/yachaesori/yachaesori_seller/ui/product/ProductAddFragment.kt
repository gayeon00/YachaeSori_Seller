package com.yachaesori.yachaesori_seller.ui.product

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.yachaesori.yachaesori_seller.MainActivity
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.Image
import com.yachaesori.yachaesori_seller.data.model.Product
import com.yachaesori.yachaesori_seller.databinding.FragmentProductAddBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductAddFragment : Fragment() {
    private val productViewModel: ProductViewModel by activityViewModels { ProductViewModelFactory() }

    private var _fragmentProductAddBinding: FragmentProductAddBinding? = null
    private val fragmentProductAddBinding get() = _fragmentProductAddBinding!!
    private lateinit var mainActivity: MainActivity

    // 대표 이미지 1개, 설명 이미지 1개
    private var mainImage: Image? = null
    private var descriptionImage: Image? = null

    // 메인이미지, 상세이미지용 갤러리 실행 설정
    private lateinit var mainGalleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var descriptionGalleryLauncher: ActivityResultLauncher<Intent>

    // 화면에 추가된 옵션 목록
    private var addOptionList = mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProductAddBinding = FragmentProductAddBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        return fragmentProductAddBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (productViewModel.isEditMode.value == true) {

            fragmentProductAddBinding.buttonNext.text = "수정하기"

            // Observer
            productViewModel.selectedProduct.observe(viewLifecycleOwner) { product ->

                fragmentProductAddBinding.run {
                    productViewModel.loadImage(product.mainImageUrl) {
                        mainImage = Image(it.toString(),product.mainImageUrl)
                        Log.d("what mainImage", mainImage.toString())
                    }

                    showMainImage(product)

                    textInputEditTextProductName.setText(product.name)
                    addOptionList = product.options.toMutableList()
                    showOption(addOptionList)
                    textInputEditTextPrice.setText(product.price.toString())

                    productViewModel.loadImage(product.detailImageUrl) {
                        descriptionImage = Image(it.toString(),product.detailImageUrl)
                        Log.d("what descImage", descriptionImage.toString())
                    }

                    showDetailImage(product)

                }
            }
        }

        mainGalleryLauncher = mainImageGallerySetting()
        descriptionGalleryLauncher = descriptionImageGallerySetting()

        addOptionList.clear()

        fragmentProductAddBinding.run {
            TooltipCompat.setTooltipText(
                infoIconDescription,
                getString(R.string.tooltip_description_image)
            )

            toolbarProductAdd.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            buttonCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            // 입력시 실시간 유효성 검사
            textInputEditTextProductName.addTextChangedListener {
                validateEditText(it.toString(), textInputLayoutName, "상품명을 입력해 주세요")
            }
            textInputEditTextPrice.addTextChangedListener {
                validateEditText(it.toString(), textInputLayoutPrice, "가격을 입력해 주세요")
            }
            textInputEditTextDescription.addTextChangedListener {
                validateEditText(it.toString(), textInputLayoutDescription, "상품 설명을 입력해 주세요")
            }

            // 해시태그 Chip 추가
            textInputEditTextOptions.setOnEditorActionListener { _, _, _ ->
                chipGroupOption.removeAllViews()
                addOption(addOptionList)
                true
            }
            buttonAddOption.setOnClickListener {
                chipGroupOption.removeAllViews()
                addOption(addOptionList)
            }

            // 대표 이미지 추가
            buttonAddMainImage.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                    }
                mainGalleryLauncher.launch(galleryIntent)
            }

            // 설명 이미지 추가
            buttonAddDescImage.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                    }
                descriptionGalleryLauncher.launch(galleryIntent)
            }

            buttonNext.setOnClickListener {
                // 최종 유효성 검사
                if (mainImage == null) {
                    buttonAddMainImage.requestFocus()
                    scrollViewProductAdd.scrollTo(0, buttonAddMainImage.top)
                    Snackbar.make(
                        fragmentProductAddBinding.root,
                        "대표 이미지를 등록해 주세요",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                if (!validateEditText(
                        textInputEditTextProductName.text.toString(),
                        textInputLayoutName,
                        "상품명을 입력해 주세요"
                    )
                ) {
                    return@setOnClickListener
                }
                if (!validateEditText(
                        textInputEditTextPrice.text.toString(),
                        textInputLayoutPrice,
                        "가격을 입력해 주세요"
                    )
                ) {
                    return@setOnClickListener
                }
                if (descriptionImage == null) {
                    buttonAddDescImage.requestFocus()
                    scrollViewProductAdd.scrollTo(0, buttonAddDescImage.bottom)
                    Snackbar.make(
                        fragmentProductAddBinding.root,
                        "상세 이미지를 등록해 주세요",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                // 상품 등록 진행중 표시
                progressBar.visibility = View.VISIBLE

                // 상품 식별, 파일명에 사용할 고유 코드
                val code = System.currentTimeMillis().toString()

                if(mainImage!!.fileName!!.isEmpty()) mainImage!!.fileName = "image/${code}_main_image.jpg"
                if(descriptionImage!!.fileName!!.isEmpty()) descriptionImage!!.fileName = "image/${code}_description_image.jpg"

                val product = Product(
                    "",
                    textInputEditTextProductName.text.toString(),
                    textInputEditTextPrice.text.toString().toLong(),
                    mainImage!!.fileName!!,
                    descriptionImage!!.fileName!!,
                    SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(Date()),
                    0L,
                    addOptionList
                )

                // DB에 저장할 product, image 리스트 전달
                val imageArrayList = arrayListOf<Image>()
                imageArrayList.add(mainImage!!)
                imageArrayList.add(descriptionImage!!)

                Log.d("what imageList", imageArrayList.toString())
                saveOrUpdateData(product, imageArrayList)

            }
        }
    }

    private fun createChip(option: String): Chip {
        val chip = layoutInflater.inflate(
            R.layout.item_chip_input,
            fragmentProductAddBinding.chipGroupOption,
            false
        ) as Chip
        chip.text = option
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            // Chip 삭제
            fragmentProductAddBinding.chipGroupOption.removeView(chip)
            addOptionList.remove(option)
        }
        return chip
    }

    private fun showDetailImage(product: Product) {
        //TODO: 상세 이미지 보여주기
        val dPreviewImageView = makeDetailImageCardView()

        // 메인 이미지 다운 받아서 표시
        val dImageStoragePath = product.detailImageUrl
        Log.d("ProductDetailFragment", product.toString())
        if (dImageStoragePath.isNotEmpty()) {
            productViewModel.loadImage(dImageStoragePath) {
                Glide.with(dPreviewImageView.context)
                    .load(it)
                    .placeholder(R.drawable.loading_placeholder)
                    .fitCenter()
                    .into(dPreviewImageView)
            }

        }
        fragmentProductAddBinding.buttonAddDescImage.text = "1/1"
    }

    private fun showMainImage(product: Product) {
        val mPreviewImageView = makeMainImageCardView()

        // 메인 이미지 다운 받아서 표시
        val mImageStoragePath = product.mainImageUrl
        Log.d("ProductDetailFragment", product.toString())
        if (mImageStoragePath.isNotEmpty()) {
            productViewModel.loadImage(mImageStoragePath) {
                Glide.with(mPreviewImageView.context)
                    .load(it)
                    .placeholder(R.drawable.loading_placeholder)
                    .fitCenter()
                    .into(mPreviewImageView)
            }
        }
        fragmentProductAddBinding.buttonAddMainImage.text = "1/1"
    }

    private fun saveOrUpdateData(product: Product, imageArrayList: ArrayList<Image>) {
        // 상품 정보 DB 저장
        productViewModel.saveOrUpdateProduct(product)
        // 이미지 업로드
        productViewModel.uploadImageList(imageArrayList)

        Toast.makeText(context, "상품이 등록 되었습니다", Toast.LENGTH_LONG).show()
        findNavController().popBackStack(R.id.item_home, false)
    }

    private fun showOption(list: MutableList<String>) {
        fragmentProductAddBinding.run {
            for (inputOption in list) {
                if(inputOption.isNotEmpty()) {
                    val chip = createChip(inputOption)
                    chipGroupOption.addView(chip)
                }
            }
        }
    }
    private fun addOption(list: MutableList<String>) {
        fragmentProductAddBinding.run {
            // 옵션는 최대 10개까지 등록 가능
            if (list.size < 10) {
                // 중복 아니게 추가된 옵션 저장
                if(!list.contains(textInputEditTextOptions.text.toString())) {
                    list.add(textInputEditTextOptions.text.toString())
                }

                for (inputOption in list) {
                    if(inputOption.isNotEmpty()) {
                        val chip = createChip(inputOption)
                        chipGroupOption.addView(chip)
                    }
                }
            }
            textInputEditTextOptions.setText("")
        }
    }

    // EditText 유효성 검사
    private fun validateEditText(
        input: String,
        textInputLayout: TextInputLayout,
        errorMessage: String
    ): Boolean {
        return if (input.isNotBlank()) {
            textInputLayout.isErrorEnabled = false
            textInputLayout.error = null
            true
        } else {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = errorMessage
            mainActivity.showSoftInput(textInputLayout)
            false
        }
    }

    // 메인 이미지 갤러리 설정
    private fun mainImageGallerySetting(): ActivityResultLauncher<Intent> {
        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // 메인 이미지 최대 5개까지만 첨부 가능
                if (mainImage == null) {
                    if (it.resultCode == AppCompatActivity.RESULT_OK && it.data?.data != null) {
                        val uri = it.data?.data!!
                        var bitmap: Bitmap? = null

                        val previewMainImageView = makeMainImageCardView()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val source =
                                ImageDecoder.createSource(mainActivity.contentResolver, uri)
                            bitmap = ImageDecoder.decodeBitmap(source)
                            previewMainImageView.setImageBitmap(bitmap)
                        } else {
                            val cursor =
                                mainActivity.contentResolver.query(uri, null, null, null, null)
                            if (cursor != null) {
                                cursor.moveToNext()

                                val colIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                                val source = cursor.getString(colIdx)

                                bitmap = BitmapFactory.decodeFile(source)
                                previewMainImageView.setImageBitmap(bitmap)
                            }
                        }

                        // 이미지 정보 저장 해두기
                        mainImage = Image(uri.toString(), "")
                        Log.d("what mainImage", mainImage.toString())
                        fragmentProductAddBinding.buttonAddMainImage.text = "1/1"
                    }
                }
            }
        return galleryLauncher
    }

    private fun makeMainImageCardView(): ImageView {
        // 이미지 카드뷰 추가
        val previewLinearLayout = layoutInflater.inflate(
            R.layout.item_imageview_delete,
            fragmentProductAddBinding.linearMainImage,
            false
        ) as LinearLayout
        val previewImageView =
            previewLinearLayout.findViewById<ImageView>(R.id.imageViewDelete)
        val previewButton =
            previewLinearLayout.findViewById<Button>(R.id.buttonDelete)
        previewButton.setOnClickListener {
            // 이미지 카드뷰 삭제, 리스트에서 제거
            fragmentProductAddBinding.linearMainImage.removeView(
                previewLinearLayout
            )
            mainImage = null
            Log.d("what mainImage", mainImage.toString())
            fragmentProductAddBinding.buttonAddMainImage.text = "0/1"
        }
        fragmentProductAddBinding.linearMainImage.addView(previewLinearLayout)
        return previewImageView
    }

    // 설명 이미지 갤러리 설정
    private fun descriptionImageGallerySetting(): ActivityResultLauncher<Intent> {
        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                // 갤러리에서 사진을 선택했고, 이미 등록된 설명 이미지가 없을 경우
                if (descriptionImage == null) {
                    if (it.resultCode == AppCompatActivity.RESULT_OK && it.data?.data != null) {
                        val uri = it.data?.data!!
                        // RecyclerView 표시용 저장
                        var bitmap: Bitmap? = null
                        val previewImageView = makeDetailImageCardView()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val source =
                                ImageDecoder.createSource(mainActivity.contentResolver, uri)
                            bitmap = ImageDecoder.decodeBitmap(source)
                            previewImageView.setImageBitmap(bitmap)
                        } else {
                            val cursor =
                                mainActivity.contentResolver.query(uri, null, null, null, null)
                            if (cursor != null) {
                                cursor.moveToNext()

                                val colIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                                val source = cursor.getString(colIdx)

                                bitmap = BitmapFactory.decodeFile(source)
                                previewImageView.setImageBitmap(bitmap)
                            }
                        }

                        // 이미지 정보 저장 해두기
                        descriptionImage = Image(uri.toString(), "")
                        Log.d("what descImage", descriptionImage.toString())
                        fragmentProductAddBinding.buttonAddDescImage.text = "1/1"
                    }
                }
            }

        return galleryLauncher
    }

    private fun makeDetailImageCardView(): ImageView {
        // 이미지 카드뷰 추가
        val previewLinearLayout = layoutInflater.inflate(
            R.layout.item_imageview_delete,
            fragmentProductAddBinding.linearDescriptionImage,
            false
        ) as LinearLayout
        val previewImageView =
            previewLinearLayout.findViewById<ImageView>(R.id.imageViewDelete)
        val previewButton =
            previewLinearLayout.findViewById<Button>(R.id.buttonDelete)
        previewButton.setOnClickListener {
            // 이미지 카드뷰 삭제, 리스트에서 제거
            fragmentProductAddBinding.linearDescriptionImage.removeView(
                previewLinearLayout
            )
            descriptionImage = null
            Log.d("what descImage", descriptionImage.toString())
            fragmentProductAddBinding.buttonAddDescImage.text = "0/1"
        }
        fragmentProductAddBinding.linearDescriptionImage.addView(previewLinearLayout)
        return previewImageView
    }
}