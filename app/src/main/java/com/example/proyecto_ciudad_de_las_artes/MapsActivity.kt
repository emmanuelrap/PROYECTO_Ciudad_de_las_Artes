package com.example.proyecto_ciudad_de_las_artes

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyecto_ciudad_de_las_artes.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var baseRemota = FirebaseFirestore.getInstance()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val posicion = ArrayList<Data>()

    var mMarkers: Map<String, Int> = HashMap()

    companion object{ const val REQUEST_CODE_LOCATION = 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        //FIREBASE
        baseRemota.collection("cdArtes")
            .addSnapshotListener { value, error ->
                if(error != null){
                    Toast.makeText(this,error.message!!, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                var res=""
                posicion.clear()
                for(document in value!!){
                    var data=Data()
                    data.nombre = document.getString("nombre").toString()
                    data.posicion1=document.getGeoPoint("p1")!!
                    data.posicion2=document.getGeoPoint("p2")!!

                    res+=data.toString()+"\n\n"
                    posicion.add(data)

                }
                Toast.makeText(this,"Al Ejecutar \n\n"+res, Toast.LENGTH_LONG).show()
            }

        //CREACION DEL FRAGMENT
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }//fin_onCreate

    //EVENTO MAPAREADY
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled=true

        mMap.setOnMyLocationButtonClickListener(this)
        enableLocation()

        crearMarcadores()

        //EVENTO CLIC EN MAPA
        mMap.setOnMapClickListener {
            miUbicacion()
        }
    }

    //CREAR LOS MARCADORES
    private fun crearMarcadores() {
        val coordenadasPrincipales = LatLng(21.51108496011311, -104.90307329600743)

        val jardinDignidad=MarkerOptions().position(LatLng(21.512381454724817, -104.90404305341924)).title("Jardín de la Dignidad")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            .snippet("Parque")
            .flat(true).rotation(0f)


        val cdArtes=MarkerOptions().position(LatLng(21.51103975981268, -104.90306478583207)).title("Ciudad de las Artes")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            .snippet("Parque")

        val afterTacos=MarkerOptions().position(LatLng(21.51165095509239, -104.90403546698616)).title("Tacos El After")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            .snippet("Alimentos")

        val skatePark=MarkerOptions().position(LatLng(21.51197562204376, -104.9027154276223)).title("Skate Park")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            .snippet("Zona Recreativa")

        val estatuas=MarkerOptions().position(LatLng(21.510740472140885, -104.90317061360524)).title("Estatuas Conmemorativas")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            .snippet("Zona Conmemorativa")

        val escuelaMusica=MarkerOptions().position(LatLng(21.511986208992873, -104.90210851297405)).title("Escuela de Musica")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            .snippet("Istitución")

        val zonaTacos =MarkerOptions().position(LatLng(    21.51091873667444, -104.9037786567254)).title("Zona de Tacos")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            .snippet("Alimentos")

        val dolores =MarkerOptions().position(LatLng(    21.510396830580504, -104.90331087782104)).title("Dolores")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            .snippet("Alimentos")

        val laSanta =MarkerOptions().position(LatLng(    21.510330049125745, -104.90314384918146)).title("La Santa")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            .snippet("Bar")

        val cremeria =MarkerOptions().position(LatLng(    21.51248992670049, -104.90184382014519)).title("Cremeria Yoli")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            .snippet("Negocio")

        mMap.setOnMapClickListener {
            Toast.makeText(this,"Clic en el Mapa", Toast.LENGTH_SHORT).show()
        }



        mMap.addMarker(cdArtes)
        mMap.addMarker(jardinDignidad)
        mMap.addMarker(afterTacos)
        mMap.addMarker(skatePark)
        mMap.addMarker(escuelaMusica)
        mMap.addMarker(estatuas)
        mMap.addMarker(dolores)
        mMap.addMarker(zonaTacos)
        mMap.addMarker(laSanta)
        mMap.addMarker(cremeria)

        //ANIMACION DE CAMARA
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordenadasPrincipales,18f),3000,null //Cuanto zoom y cuando durarà
        )
    }


    //////////////    VIDEO YB PARA PERMISOS ///////////////////
    //Para saber si ya existe el permiso
    private fun isLocationPersmissionAceptados()=ContextCompat.checkSelfPermission(
        this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


    private fun enableLocation(){
        if(!::mMap.isInitialized){
            return
        }
        if(isLocationPersmissionAceptados()) {
            mMap.isMyLocationEnabled = true //no importa este error(si es que da)
        }
        else{
         requestLocationPermissions()
        }
    }

    private fun requestLocationPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            //Ya se los habiamos pedido pero se negó
            Toast.makeText(this,"Ve a Ajustes y Acepta los Permisos para poder continuar", Toast.LENGTH_SHORT).show()
        }else{
            //Es la primera ves
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

    //CUANDO ACEPTA PERMISOS
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                mMap.isMyLocationEnabled=true //ignorar error si sale
            }else{
                Toast.makeText(this,"Ve a Ajustes y Acepta los Permisos para poder continuar", Toast.LENGTH_SHORT).show()
            }
            else->{}
        }
    }

    //Cuando se crea el Mapa
    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::mMap.isInitialized){
            return
        }

        if(!isLocationPersmissionAceptados()){
            mMap.isMyLocationEnabled=false
            Toast.makeText(this,"Acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }


    //Evento clic en boton mi ubicacion (arriba a la Derecha)
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this,"Esta es tu Ubicación Actual", Toast.LENGTH_SHORT).show()
      return false
    }

//-------------FIN CODIGO YB (Administracion de Permsisos)


//-------------CODIGO MAESTRO para comparar ubicacion con la zona de los marcadores
   private  fun miUbicacion(){
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation.addOnSuccessListener {
                var geoPos = GeoPoint(it.latitude,it.longitude)
               //  Toast.makeText(this,""+it.latitude+""+""+it.longitude,Toast.LENGTH_SHORT).show()
                var bandera=false
                    for(item in posicion){
                        AlertDialog.Builder(this)
                            .setMessage("Array posicion Data:\n"+posicion.toString())
                            .setPositiveButton("OK"){p,q-> }
                            .show()

                        //SI ENTRA
                        if(item.estoyEn(geoPos)){
                            AlertDialog.Builder(this)
                                .setMessage("Usted esta en "+item.nombre)
                                .setPositiveButton("OK"){p,q-> }
                                .show()
                            bandera=true


                        }
                    }

                if(bandera==false){
                    AlertDialog.Builder(this)
                        .setMessage("No se encontro ninguna Ubicacion Cercana")
                        .setPositiveButton("OK"){p,q-> }
                        .show()
                }
        }.addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage("ERROR DE UBICACION")
                    .setPositiveButton("OK"){p,q-> }
                    .show()
        }
    }





}