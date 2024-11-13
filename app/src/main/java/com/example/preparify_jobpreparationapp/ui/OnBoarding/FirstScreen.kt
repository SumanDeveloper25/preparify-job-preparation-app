import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.preparify_jobpreparationapp.R
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController

class FirstScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first_screen, container, false)

        // Get ViewPager2 from the parent activity
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        // Reference btnNext1 using findViewById
        val btnNext = view.findViewById<Button>(R.id.btnNext1)

        // Set the click listener for btnNext1 to navigate to the next page
        btnNext.setOnClickListener {
            viewPager?.currentItem = 1
        }

        view.findViewById<LinearLayout>(R.id.btnSkip).setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_loginOptionsFragment)
        }

        return view
    }
}
