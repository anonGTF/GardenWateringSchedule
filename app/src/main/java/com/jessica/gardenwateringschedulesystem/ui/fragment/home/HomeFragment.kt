package com.jessica.gardenwateringschedulesystem.ui.fragment.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.here.android.mpa.common.*
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.guidance.NavigationManager.MapUpdateMode
import com.here.android.mpa.guidance.NavigationManager.NavigationManagerEventListener
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapMarker
import com.here.android.mpa.mapping.MapRoute
import com.here.android.mpa.routing.*
import com.jessica.gardenwateringschedulesystem.R
import com.jessica.gardenwateringschedulesystem.databinding.FragmentHomeBinding
import com.jessica.gardenwateringschedulesystem.foreground.ForegroundService
import com.jessica.gardenwateringschedulesystem.utils.*
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mMap: Map
    private lateinit var mNavigationmanager: NavigationManager
    private lateinit var mGeoboundingbox: GeoBoundingBox
    private var hasForegroundServiceStarted: Boolean = false
    private var isStarted: Boolean = false
    private var mRoute: Route? = null
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timeExtra = arguments?.getString("time")
        if (timeExtra != null) {
            showNotif(timeExtra)
        }
        showNotif("08:00")
        MapEngine.getInstance().init(ApplicationContext(binding.root.context), initMap())
    }

    private fun initMap() = OnEngineInitListener { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                binding.extMapview.map = Map()
                mMap = binding.extMapview.map!!
                mMap.setCenter(
                    GeoCoordinate(HOME_LATITUDE, HOME_LONGITUDE),
                    Map.Animation.NONE
                )
                mMap.zoomLevel = 13.2
                mMap.addMapObject(MapMarker(GeoCoordinate(HOME_LATITUDE, HOME_LONGITUDE)))
                mNavigationmanager = NavigationManager.getInstance()
                db.collection(ROUTES).document(auth.currentUser!!.uid)
                    .collection(ROUTE_WAYPOINTS).orderBy("id").get()
                    .addOnSuccessListener { collection ->
                        createRoute(collection.documents)
                    }
            } else {
                AlertDialog.Builder(binding.root.context)
                    .setMessage("Error : ${error.name} ${error.details}")
                    .setTitle("Engine init error")
                    .setNegativeButton(android.R.string.cancel) { _, _ -> activity?.finish() }
                    .create().show()
            }
        }

    private fun createRoute(waypoints: List<DocumentSnapshot>) {
        val coreRouter = CoreRouter()
        val routePlan = RoutePlan()
        val routeOptions = RouteOptions()
        routeOptions.transportMode = RouteOptions.TransportMode.CAR
        routeOptions.setHighwaysAllowed(false)
        routeOptions.routeType = RouteOptions.Type.SHORTEST
        routeOptions.routeCount = 1
        routePlan.routeOptions = routeOptions
        waypoints.forEach { data ->
            val latitude = data.data?.get("latitude").toString().toDouble()
            val longitude = data.data?.get("longitude").toString().toDouble()
            routePlan.addWaypoint(RouteWaypoint(GeoCoordinate(latitude, longitude)))
        }

        coreRouter.calculateRoute(routePlan,mRouterListener)
    }

    private val mRouterListener = object : Router.Listener<List<RouteResult>, RoutingError> {
        override fun onProgress(p0: Int) {
        }

        override fun onCalculateRouteFinished(routeResults: List<RouteResult>, routingError: RoutingError) {
            if (routingError == RoutingError.NONE) {
                mRoute = routeResults[0].route
                val mapRoute = MapRoute(routeResults[0].route)
                mapRoute.isManeuverNumberVisible = true
                mMap.addMapObject(mapRoute)
                mGeoboundingbox = routeResults[0].route.boundingBox!!
                mMap.zoomTo(
                    mGeoboundingbox, Map.Animation.NONE,
                    Map.MOVE_PRESERVE_ORIENTATION
                )
                binding.btnMulai.setOnClickListener {
                    binding.cvReminder.visibility = View.INVISIBLE
                    if (!isStarted) {
                        binding.btnMulai.setText(R.string.stop)
                        startNavigation()
                        isStarted = true
                    } else {
                        mNavigationmanager.stop()
                        mMap.zoomTo(mGeoboundingbox, Map.Animation.NONE, 0f)
                        binding.btnMulai.setText(R.string.mulai)
                        isStarted = false
                    }
                }
            } else {
                Toast.makeText(binding.root.context, "Error:route calculation returned error code: $routingError",
                    Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun startForegroundService() {
        if (!hasForegroundServiceStarted) {
            hasForegroundServiceStarted = true
            val startIntent = Intent(binding.root.context, ForegroundService::class.java)
            startIntent.action = ForegroundService.START_ACTION
            activity?.applicationContext?.startService(startIntent)
        }
    }

    private fun stopForegroundService() {
        if (hasForegroundServiceStarted) {
            hasForegroundServiceStarted = false
            val stopIntent = Intent(binding.root.context, ForegroundService::class.java)
            stopIntent.action = ForegroundService.STOP_ACTION
            activity?.applicationContext?.startService(stopIntent)
        }
    }

    private fun startNavigation() {
        binding.btnMulai.text = resources.getString(R.string.stop)
        mNavigationmanager.setMap(mMap)
        binding.extMapview.map?.positionIndicator?.isVisible = true
//        mNavigationmanager.startNavigation(mRoute!!)
        mNavigationmanager.simulate(mRoute!!, 50L)
        mMap.tilt = 60f
        startForegroundService()
        mNavigationmanager.mapUpdateMode = MapUpdateMode.ROADVIEW

        addNavigationListeners()
    }

    private fun addNavigationListeners() {
        mNavigationmanager.addNavigationManagerEventListener(
            WeakReference(mNavigationmanagereventlistener)
        )

        mNavigationmanager.addNewInstructionEventListener(
            WeakReference(mNewInstructionListener)
        )
    }

    private val mNewInstructionListener = object : NavigationManager.NewInstructionEventListener() {
            override fun onNewInstructionEvent() {
                super.onNewInstructionEvent()
                val maneuver: Maneuver? = mNavigationmanager.nextManeuver
                if (maneuver != null) {
                    if (maneuver.action === Maneuver.Action.END) {
                        binding.tvManeuverInfo.text = "Penyiraman telah selesai"
                        binding.tvManeuverStreetName.text = "Selamat beristirahat"
                    } else {
                        val info = StringBuilder()
                            .append(maneuver.distanceFromPreviousManeuver)
                            .append(" Meter, ")
                            .append(turnToText(maneuver.turn))
                            .toString()
                        val road = StringBuilder()
                            .append("ke ")
                            .append(maneuver.roadNames[0])
                            .toString()
                        binding.tvManeuverInfo.text = info
                        binding.tvManeuverStreetName.text = road
                        Glide.with(binding.root.context)
                            .load(turnIcon(maneuver.turn))
                            .override(38, 42)
                            .into(binding.icManeuver)
                    }
                    binding.cvManeuver.visibility = View.VISIBLE
                }
            }
        }

    private val mNavigationmanagereventlistener = object : NavigationManagerEventListener() {
            override fun onEnded(navigationMode: NavigationManager.NavigationMode) {
                Toast.makeText(binding.root.context, "$navigationMode was ended", Toast.LENGTH_SHORT).show()
                stopForegroundService()
                binding.cvManeuver.visibility = View.INVISIBLE
            }
        }

    override fun onResume() {
        super.onResume()
        MapEngine.getInstance().onResume()
        binding.extMapview.onResume()
    }

    override fun onPause() {
        MapEngine.getInstance().onPause()
        super.onPause()
    }

    private fun showNotif(time: String) {
        val date = getCurrentDateTime().toString("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, time.split(":")[0].toInt())
        calendar.set(Calendar.MINUTE, time.split(":")[1].toInt())
        calendar.add(Calendar.HOUR_OF_DAY, 7)
        val endTime = calendar.time.toString("HH:mm")
        val endText = SpannableStringBuilder("Perkiraan selesai pukul $endTime WIB")
        endText.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorGreen)),
            10,
            17,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        val suggestion = SpannableStringBuilder("Klik mulai untuk memulai perjalanan Anda")
        suggestion.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorGreen)),
            5,
            10,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        binding.tvTanggalReminder.text = date
        binding.tvPerkiraanSelesai.text = endText
        binding.tvSuggestion.text = suggestion
        binding.cvMulaiRute.visibility = View.VISIBLE
        binding.cvReminder.visibility = View.VISIBLE
    }
}