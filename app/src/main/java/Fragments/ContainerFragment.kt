package Fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.utn.tp3.R

/**
 * A simple [Fragment] subclass.
 */
class ContainerFragment : Fragment() {

    lateinit var view_cf: View
    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout

    lateinit var mp: MediaPlayer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view_cf = inflater.inflate(R.layout.fragment_container, container, false)

        tabLayout = view_cf.findViewById(R.id.tab_layout)
        viewPager = view_cf.findViewById(R.id.view_pager)

        mp = MediaPlayer.create(requireActivity(), R.raw.pumpit)

        return view_cf
    }

    override fun onStart() {
        super.onStart()

        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        when (pref.getString("music", "default")){
            "pumpit" -> {mp = MediaPlayer.create(requireActivity(), R.raw.pumpit)}
            "misionimposible" -> {mp = MediaPlayer.create(requireActivity(), R.raw.misionimposible)}
            "rocky" -> {mp = MediaPlayer.create(requireActivity(), R.raw.rocky)}
        }

        if(pref.getBoolean("sonido", false)) {
            mp.start()
        }

        viewPager.setAdapter(createCardAdapter())
        // viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager,
                TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                    when (position) {
                        0 -> tab.text = "DESCRIPCIÓN"
                        1 -> tab.text = "+INFO"
                        else -> ""
                    }
                }).attach()

    }

    private fun createCardAdapter(): ViewPagerAdapter? {
        return ViewPagerAdapter(requireActivity())
    }

    class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
            FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {

            return when(position){
                0 -> FragmentTab1()
                1 -> FragmentTab2()

                else -> FragmentTab1()
            }
        }

        override fun getItemCount(): Int {
            return TAB_COUNT
        }

        companion object {
            private const val TAB_COUNT = 2
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        mp.stop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}
