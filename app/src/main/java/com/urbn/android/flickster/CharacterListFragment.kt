package com.urbn.android.flickster

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.urbn.android.flickster.data.Character
import com.urbn.android.flickster.databinding.CharacterItemContentBinding
import com.urbn.android.flickster.databinding.FragmentCharacterListBinding
import com.urbn.android.flickster.viewmodel.CharacterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CharacterListFragment : Fragment() {

    private lateinit var binding: FragmentCharacterListBinding

    private val viewModel: CharacterViewModel by viewModels<CharacterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        * The noItemsTextView is used to display a message when the database is empty and the network
        * is not available.
        * */
        val noItemsTextView: TextView = binding.emptyList
        val recyclerView: RecyclerView = binding.itemList
        val adapter = CharacterAdapter { _, itemView ->
            showContextMenuForCharacter(itemView)
        }

        /*
        *  The stateRestorationPolicy is set to PREVENT_WHEN_EMPTY to prevent the RecyclerView from
        *  restoring its state when the adapter is empty. This will maintain the scroll position.
        * */
        recyclerView.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recyclerView.adapter = adapter

        registerForContextMenu(binding.itemList)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.fetchCharacterData()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                if (viewModel.CHARACTERSLIST.isEmpty()) {
                    noItemsTextView.visibility = View.VISIBLE
                }
                viewModel.getSnackbar(view).show()
            }
        }

        viewModel.getAllCharacters.observe(viewLifecycleOwner) { characters ->
            if (characters.isNotEmpty() && viewModel.dataFetched) {
                noItemsTextView.visibility = View.GONE
                Toast.makeText(requireContext(), "Long press to sort", Toast.LENGTH_SHORT).show()
            } else {
                noItemsTextView.visibility = View.VISIBLE
            }
            viewModel.CHARACTERSLIST = characters.toMutableList()
        }

        viewModel.currentSortingMethod.observe(viewLifecycleOwner) { method ->
            val sortedList = viewModel.updateSortedCharacters(method)
            adapter.submitList(sortedList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.currentSortingMethod.removeObservers(viewLifecycleOwner)
        viewModel.errorMessage.removeObservers(viewLifecycleOwner)
    }

    private fun showContextMenuForCharacter(anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.inflate(R.menu.context_menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sort_alphabetically, R.id.sort_reverse, R.id.sort_favorite -> {
                    menuItem.isChecked = !menuItem.isChecked
                    viewModel.updateSortingMethod(menuItem.itemId)
                    true
                }
                else -> false
            }
        }

        viewModel.currentSortingMethod.observe(viewLifecycleOwner) { method ->
            val menuItem = popupMenu.menu.findItem(method)
            menuItem.isChecked = true
        }

        popupMenu.show()
    }
}

class CharacterAdapter(
    private val onItemLongClickListener: (Character, View) -> Unit
): ListAdapter<Character, CharacterViewHolder>(CharactersDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {

        val binding =
            CharacterItemContentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CharacterViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = currentList[position]
        holder.onBind(character)

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener(character, it)
            true
        }
    }
}

class CharacterViewHolder(binding: CharacterItemContentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val nameView: TextView = binding.characterName
    private val detailsView: TextView = binding.characterDetail
    private val imageView: ImageView = binding.characterPicture
    private val favoriteView: ImageView = binding.favoriteIndicator

    fun onBind(character: Character) {
        nameView.text = character.name
        detailsView.text = character.details
        favoriteView.visibility = if (character.isFavorite) View.VISIBLE else View.GONE

        /*
        * I chose to use Coil to load images because it is a modern image loading library.
        * It is lightweight and easy to use. It also has a lot of features and is actively maintained.
        * */
        character.imageUrl?.let {
            imageView.load(it) {
                crossfade(true)
                placeholder(R.drawable.placeholder)
                error(R.drawable.noimage)
            }
        }

        itemView.setOnCreateContextMenuListener { menu, _, _ ->
            val inflater = MenuInflater(itemView.context)
            inflater.inflate(R.menu.context_menu, menu)
        }

        itemView.setOnClickListener { itemView ->
            val bundle = Bundle()

            bundle.putParcelable(
                CharacterDetailFragment.ARG_CHARACTER,
                character
            )

            itemView.findNavController().navigate(R.id.show_character_detail, bundle)
        }
    }
}

object CharactersDiffUtil: DiffUtil.ItemCallback<Character>() {

    override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
        return oldItem.name == newItem.name
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
        return oldItem.details == newItem.details &&
                oldItem.imageUrl == newItem.imageUrl &&
                oldItem.isFavorite == newItem.isFavorite
    }
}
