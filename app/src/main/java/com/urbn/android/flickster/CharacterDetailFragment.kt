package com.urbn.android.flickster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import coil.load
import com.urbn.android.flickster.data.Character
import com.urbn.android.flickster.databinding.FragmentCharacterDetailBinding
import com.urbn.android.flickster.viewmodel.CharacterViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class CharacterDetailFragment : Fragment() {

    private val viewModel: CharacterViewModel by viewModels<CharacterViewModel>()

    private lateinit var binding: FragmentCharacterDetailBinding
    private val item: Character by lazy {
        arguments!!.getParcelable<Character>(ARG_CHARACTER)
            ?: throw IllegalArgumentException("Character not provided.")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        (activity as AppCompatActivity).supportActionBar?.title = item.name
        (activity as AppCompatActivity).supportActionBar?.displayOptions = view.baseline

        binding.toolbar.title = item.name
        binding.toolbar.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        binding.characterDetail.text = item.details
        updateFavoriteButtonUI(item.isFavorite)

        binding.favoriteButton.setOnClickListener {
            item.isFavorite = !item.isFavorite
            viewModel.updateCharacterFavoriteStatus(item)
            updateFavoriteButtonUI(item.isFavorite)
        }

        binding.characterPicture.load(item.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.noimage)
        }
    }

    private fun updateFavoriteButtonUI(isFavorite: Boolean) {
        if (!isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.ic_star_hollow)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.ic_star_accent)
        }
    }

    companion object {
        const val ARG_CHARACTER = "character"
    }
}
