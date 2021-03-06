package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;


// AGREGAR en MapaFragment una interface MapaFragment.OnMapaListener con el método coordenadasSeleccionadas 
// IMPLEMENTAR dicho método en esta actividad.

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, NuevoReclamoFragment.OnNuevoLugarListener, MapaFragment.OnAbrirMapaListener {
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.navview);
        BienvenidoFragment fragmentInicio = new BienvenidoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragmentInicio)
                .commit();

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                        Fragment fragment = null;
                        String tag = "";
                        switch (menuItem.getItemId()) {
                            case R.id.optNuevoReclamo:
                                tag = "nuevoReclamoFragment";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                if(fragment==null) {
                                    fragment = new NuevoReclamoFragment();
                                    ((NuevoReclamoFragment) fragment).setListener(MainActivity.this);
                                }

                                fragmentTransaction = true;
                                break;
                            case R.id.optListaReclamo:
                                tag="listaReclamos";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                if(fragment==null) fragment = new ListaReclamosFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.optVerMapa:
                                tag="mapaReclamos";
                                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                if (fragment == null){
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("tipo_mapa", 2);
                                    fragment = new MapaFragment();
                                    fragment.setArguments(bundle);
                                }
                                ((MapaFragment)fragment).setListener(MainActivity.this);
                                fragmentTransaction = true;
                                break;
                            case R.id.optHeatMap:
                                tag="mapaReclamos";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                if (fragment == null){
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("tipo_mapa", 4);
                                    fragment = new MapaFragment();
                                    fragment.setArguments(bundle);
                                }
                                ((MapaFragment)fragment).setListener(MainActivity.this);
                                fragmentTransaction = true;
                                break;
                            case R.id.optBuscar:
                                tag="BuscarReclamos";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                if (fragment == null){
                                    fragment = new BuscarFragment();
                                }
                                ((BuscarFragment)fragment).setListener(MainActivity.this);
                                fragmentTransaction = true;
                                break;
                        }

                        if(fragmentTransaction) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.contenido, fragment,tag)
                                    .addToBackStack(tag)
                                    .commit();

                            menuItem.setChecked(true);

                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

        @Override
        public void obtenerCoordenadas() {
            Fragment fragment = null;
            String tag="mapaReclamos";
            fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment == null){
                Bundle bundle = new Bundle();
                bundle.putInt("tipo_mapa", 1);
                fragment = new MapaFragment();
                fragment.setArguments(bundle);
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenido, fragment, tag)
                    .addToBackStack(tag)
                    .commit();
            ((MapaFragment)fragment).setListener(MainActivity.this);
            // para que el usuario vea el mapa y con el click largo pueda acceder
            // a seleccionar la coordenada donde se registra el reclamo
            // configurar a la actividad como listener de los eventos del mapa ((MapaFragment) fragment).setListener(this);
        }

        @Override
        public void coordenadasSeleccionadas(LatLng c) {
            boolean esNuevo=false;
            String tag = "nuevoReclamoFragment";
            Fragment fragment =  getSupportFragmentManager().findFragmentByTag(tag);
            if(fragment==null) {
                fragment = new NuevoReclamoFragment();
                ((NuevoReclamoFragment) fragment).setListener(MainActivity.this);
                esNuevo=true;
                System.out.println("Es nuevo qliau");
            }
            Bundle bundle = fragment.getArguments();
            if(bundle == null) bundle = new Bundle();
            DecimalFormat numberFormat = new DecimalFormat("#.000");
            bundle.putString("latLng",numberFormat.format(c.latitude)+";"+numberFormat.format(c.longitude));
            fragment.setArguments(bundle);
            if(esNuevo) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contenido, fragment, tag)
                        .addToBackStack(tag)
                        .commit();
            }
            else getSupportFragmentManager().popBackStack();
        }

        public void mapaPorTipos(int pos){

            String tag="mapaReclamos";
            Fragment fragment =  getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment == null){
                Bundle bundle = new Bundle();
                bundle.putInt("tipo_mapa", 5);
                bundle.putInt("id_tipo", pos);
                fragment = new MapaFragment();
                fragment.setArguments(bundle);
            }
            ((MapaFragment)fragment).setListener(MainActivity.this);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contenido, fragment,tag)
                        .addToBackStack(tag)
                        .commit();
                getSupportActionBar().setTitle("Mapa por tipos");
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String tag = "nuevoReclamoFragment";
                    NuevoReclamoFragment f =  (NuevoReclamoFragment)getSupportFragmentManager().findFragmentByTag(tag);
                    f.sacarFoto();
                }break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String tag = "nuevoReclamoFragment";
                    NuevoReclamoFragment f =  (NuevoReclamoFragment)getSupportFragmentManager().findFragmentByTag(tag);
                    f.grabarAudio();
                }break;
        }
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

}
