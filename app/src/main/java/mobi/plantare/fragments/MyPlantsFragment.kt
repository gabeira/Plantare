package mobi.plantare.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mobi.plantare.R
import mobi.plantare.adapters.ManagerListAdapter
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyPlantsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyPlantsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyPlantsFragment : Fragment() {


    private var recyclerView: RecyclerView? = null
    private var adapter: ManagerListAdapter? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_plants, container, false)
        //We can use this list to show another share option
        recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        val itens = ArrayList<String>()
        itens.add("Teste 1")
        itens.add("Teste 2")
        adapter = ManagerListAdapter(itens)
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager = LinearLayoutManager(this.activity)
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance() = MyPlantsFragment()
    }
}
