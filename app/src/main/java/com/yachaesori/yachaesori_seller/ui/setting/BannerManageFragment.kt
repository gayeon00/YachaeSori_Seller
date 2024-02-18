package com.yachaesori.yachaesori_seller.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.Banner
import com.yachaesori.yachaesori_seller.databinding.FragmentBannerManageBinding
import com.yachaesori.yachaesori_seller.databinding.RowBannerBinding
import com.yachaesori.yachaesori_seller.databinding.RowBannerEditBinding
import com.yachaesori.yachaesori_seller.util.setImageFromUrl

class BannerManageFragment : Fragment() {
    private lateinit var binding: FragmentBannerManageBinding
    private val bannerViewModel: BannerViewModel by viewModels { BannerViewModelFactory() }
    private val bannerListEditAdapter by lazy { BannerListEditAdapter() }
    private val bannerListAdapter by lazy { BannerListAdapter(
        //popupmenu에서 삭제 버튼을 클릭하면 배너 삭제 하도록
        onDeleteItemClick = {
            bannerViewModel.deleteBanner(it)
        }
    ) }
    private val itemTouchHelper by lazy { ItemTouchHelper(ItemTouchCallback(bannerListEditAdapter)) }

    //편집모드인지 아닌지
    private var isEditMode = false

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val uri = result.data?.data

                uri?.let {
                    uploadBanner(uri)
                }
            }
        }

    /**
     * uri로 storage에 업로드 하고, 그 downloadUrl로 Banner 객체 만들어서 realtime에 넣기
     */
    private fun uploadBanner(uri: Uri) {
        bannerViewModel.uploadBanner(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBannerManageBinding.inflate(layoutInflater)

        bannerViewModel.bannerList.observe(viewLifecycleOwner) {
            bannerListAdapter.submitList(it)
            bannerListEditAdapter.submitList(it)
            Log.d("what", it.toString())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setNavigationIcon()
        setAddBanner()
        setBannerList()
        setEditButton()

    }

    /**
     * 편집 버튼 누르면 배너 편집 모드로
     */
    private fun setEditButton() {
        binding.toolbarManageBanner.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_banner_edit -> {
                    isEditMode = true

                    binding.toolbarManageBanner.menu.findItem(R.id.item_banner_edit)
                        .setVisible(false)
                    binding.toolbarManageBanner.menu.findItem(R.id.item_banner_edit_complete)
                        .setVisible(true) //완료 아이콘이 보이도록
                    binding.listBanners.adapter = bannerListEditAdapter // 배너 목록도 갱신
                }

                R.id.item_banner_edit_complete -> {
                    //배너 순서 업데이트
                    updateBannerOrder()

                    isEditMode = false
                    binding.toolbarManageBanner.menu.findItem(R.id.item_banner_edit)
                        .setVisible(true)
                    binding.toolbarManageBanner.menu.findItem(R.id.item_banner_edit_complete)
                        .setVisible(false) //편집 아이콘이 보이도록
                    binding.listBanners.adapter = bannerListAdapter // 배너 목록도 갱신


                }
            }

            true
        }
    }

    //바뀐 순서 db에 저장
    private fun updateBannerOrder() {
        Log.d("BannerManageFragment", binding.listBanners.adapter.toString())
        val bannerList = (binding.listBanners.adapter as BannerListEditAdapter).currentList
        bannerViewModel.updateBannerOrder(bannerList)
    }

    private fun setBannerList() {
        binding.listBanners.run {
            adapter = bannerListAdapter
            val divider = MaterialDividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            //마지막 아이템은 구분선 없도록
            divider.isLastItemDecorated = false
            //구분선 추가
            addItemDecoration(
                divider
            )
        }

        // 리싸이클러뷰에 itemTouchHelper 연결
        itemTouchHelper.attachToRecyclerView(binding.listBanners)
    }

    /**
     * 배너 추가 버튼 클릭 시
     *
     * 갤러리로 가서 이미지 선택하기
     * 맨 마지막 순서로 추가되기
     */
    private fun setAddBanner() {
        binding.fabAddBanner.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                }

            galleryLauncher.launch(galleryIntent)
        }
    }

    /**
     * 뒤로가기 클릭 시
     */
    private fun setNavigationIcon() {
        binding.toolbarManageBanner.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }
}

class BannerListAdapter(
    private val onDeleteItemClick: (Banner) -> Unit
) :
    ListAdapter<Banner, BannerListAdapter.BannerViewHolder>(BannerDiffUtil()) {
    inner class BannerViewHolder(
        private val binding: RowBannerBinding
    ) : ViewHolder(binding.root) {
        fun bind(item: Banner) {
            binding.run {
                tvOrder.text = item.order.toString()
                imageView.setImageFromUrl(item.imageUrl)
                btnMore.setOnClickListener {
                    showPopupMenu(it, getItem(adapterPosition))
                }
            }
        }


    }

    private fun showPopupMenu(view: View, item: Banner) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_banner_delete, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.item_delete -> {
                    onDeleteItemClick.invoke(item)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(
            RowBannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BannerListEditAdapter :
    ListAdapter<Banner, BannerListEditAdapter.BannerEditViewHolder>(BannerDiffUtil()),
    ItemTouchHelperListener {
    inner class BannerEditViewHolder(
        private val binding: RowBannerEditBinding
    ) : ViewHolder(binding.root) {
        fun bind(item: Banner) {
            binding.run {
                tvOrder.text = item.order.toString()
                imageView.setImageFromUrl(item.imageUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerEditViewHolder {
        return BannerEditViewHolder(
            RowBannerEditBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BannerEditViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onItemMove(from: Int, to: Int) {
        val item = currentList[from]
        val newList = ArrayList<Banner>()
        newList.addAll(currentList)
        newList.removeAt(from)
        newList.add(to,item)
        submitList(newList)
    }

}

class BannerDiffUtil : DiffUtil.ItemCallback<Banner>() {
    override fun areItemsTheSame(oldItem: Banner, newItem: Banner): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Banner, newItem: Banner): Boolean {
        return oldItem == newItem
    }
}
