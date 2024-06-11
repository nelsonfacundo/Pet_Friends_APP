package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.TabsViewPagerAdapter
import com.example.petfriendsapp.fragments.Historial
import com.example.petfriendsapp.fragments.SolicitudesEnviadas
import com.example.petfriendsapp.fragments.SolicitudesRecibidas
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Actividad : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: TabsViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_actividad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabs)
        adapter = TabsViewPagerAdapter(requireActivity())

        // Add your fragments and titles
        adapter.addFragment(SolicitudesRecibidas(), "Solicitudes Recibidas")
        adapter.addFragment(SolicitudesEnviadas(), "Solicitudes Enviadas")
        adapter.addFragment(Historial(), "Historial")

        viewPager.adapter = adapter

        // Link the TabLayout and the ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }



}