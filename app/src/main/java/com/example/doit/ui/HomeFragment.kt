package com.example.doit.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.doit.R
import com.example.doit.databinding.FragmentHomeBinding
import com.example.doit.ui.adapters.HomePagerAdapter
import com.example.doit.utils.Constants
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)

        setUpTabLayoutWithViewPager()

        return binding.root
    }

    private fun setUpTabLayoutWithViewPager() {
        binding.viewPager.adapter = HomePagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView = createCustomTab(position)
        }.attach()
    }

    @SuppressLint("InflateParams")
    private fun createCustomTab(position: Int): View {
        val customTabView =
            LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)

        val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
        val tabTitle = customTabView.findViewById<TextView>(R.id.tab_title)

        val taskStatus = Constants.tabs.keys.elementAt(position)
        tabTitle.text = taskStatus
        Constants.tabs[taskStatus]?.let { tabIcon.setImageResource(it) }

        return customTabView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}