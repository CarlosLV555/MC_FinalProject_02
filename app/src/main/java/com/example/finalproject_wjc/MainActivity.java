package com.example.finalproject_wjc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    BottomNavigationView bottomNavigationView;

    private GoogleMap mMap;

    private CameraPosition lastCameraPosition;

    ActivityResultLauncher<String[]> locationPermissionRequest;

    static List<MuseumGalleryItem> dd_mus_gal = Arrays.asList(
            new MuseumGalleryItem(51.06676651, 13.7575478, "FOTOFORUM", "www.fotoforumdresden.de"),
            new MuseumGalleryItem(51.08137985, 13.75141221, "Sächsische Landesärztekammer", "www.slaek.de"),
            new MuseumGalleryItem(51.06169247, 13.74272186, "Kulturrathaus", "www.dresden.de/kulturrathaus"),
            new MuseumGalleryItem(51.05678536, 13.74567381, "Sächsisches Staatsministerium der Finanzen", "www.sachsen.de"),
            new MuseumGalleryItem(51.07749098, 13.72106638, "Kreative Werkstatt Dresden e.V.", "www.kreative-werkstatt.de"),
            new MuseumGalleryItem(51.06355597, 13.75844552, "bautzner69/publish&print", "www.bautzner69.de"),
            new MuseumGalleryItem(51.05510154, 13.72680737, "Kunstraum: Schützenplatz 1", "www.saechsischer-kunstverein.de"),
            new MuseumGalleryItem(51.0573206, 13.74926636, "Sächsische Staatskanzlei", "www.sachsen.de"),
            new MuseumGalleryItem(51.03767133, 13.76268278, "Palais im Großen Garten", "www.grosser-garten-dresden.de"),
            new MuseumGalleryItem(51.0638258, 13.74562501, "Erich Kästner Museum", "www.erich-kaestner-museum.de/"),
            new MuseumGalleryItem(51.0501547, 13.82041729, "Josef-Hegenbarth-Archiv", "kupferstich-kabinett.skd.museum/ausstellungen/josef-hegenbarth-archiv/"),
            new MuseumGalleryItem(51.05718938, 13.81272337, "Schillerhäuschen", "www.museen-dresden.de"),
            new MuseumGalleryItem(51.01678712, 13.86453536, "Carl-Maria-von-Weber-Museum", "www.stadtmuseum-dresden.de/webermuseum"),
            new MuseumGalleryItem(51.05935624, 13.74089598, "Kunsthaus Dresden", "www.kunsthausdresden.de"),
            new MuseumGalleryItem(51.05025225, 13.74270808, "Städtische Galerie Dresden - Kunstsammlung", "www.galerie-dresden.de"),
            new MuseumGalleryItem(51.05693383, 13.72098823, "Schulmuseum Dresden", "www.schulmuseum-dresden.de"),
            new MuseumGalleryItem(51.05497123, 13.816742, "Leonhardi-Museum", "www.leonhardi-museum.de"),
            new MuseumGalleryItem(51.07503353, 13.768494, "Kunstfonds", "kunstfonds.skd.museum/"),
            new MuseumGalleryItem(51.05233507, 13.7375216, "Münzkabinett", "muenzkabinett.skd.museum/"),
            new MuseumGalleryItem(51.07036099, 13.76297758, "Kraszewski-Museum", "www.museen-dresden.de"),
            new MuseumGalleryItem(51.05328613, 13.73292774, "Mathematisch-Physikalischer Salon", "mathematisch-physikalischer-salon.skd.museum/"),
            new MuseumGalleryItem(51.05229171, 13.73933074, "Verkehrsmuseum Dresden", "www.verkehrsmuseum-dresden.de"),
            new MuseumGalleryItem(51.05343762, 13.73483137, "Gemäldegalerie Alte Meister mit Skulpturensammlung bis 1800", "gemaeldegalerie.skd.museum/besuch/"),
            new MuseumGalleryItem(51.05203826, 13.73703091, "Rüstkammer", "ruestkammer.skd.museum/"),
            new MuseumGalleryItem(51.05746691, 13.74374159, "Museum für sächsische Volkskunst", "volkskunst.skd.museum/"),
            new MuseumGalleryItem(51.06280433, 13.76127438, "Kunst im Diakonissenkrankenhaus", "www.diako-dresden.de/medizinische-versorgung/diakonissenkrankenhaus-dresden/wir-ueber-uns/"),
            new MuseumGalleryItem(51.0601161, 13.74297609, "Galerie Kunst & Eros - Kleingalerie und Atelier", "www.kunstunderos.de"),
            new MuseumGalleryItem(51.06769541, 13.75455225, "Ulrike Rüttinger - Galerie für Textilkunst", "ulrike-ruettinger.de"),
            new MuseumGalleryItem(51.06743392, 13.76819126, "Kunstausstellung Kühl", "www.kunstausstellung-kuehl.de"),
            new MuseumGalleryItem(51.06785264, 13.75465825, "blue child - Laden & Atelier im Kunsthof", "www.bluechild.de"),
            new MuseumGalleryItem(51.06438095, 13.75494629, "Kunsthaus Raskolnikow", "www.galerie-raskolnikow.de"),
            new MuseumGalleryItem(51.11382482, 13.78266391, "Galerie Manto Sillack", "www.galerie-sillack.de"),
            new MuseumGalleryItem(51.05597446, 13.72067845, "Galerie Adlergasse", "riesa-efau.de/kunst-erleben/galerie-adlergasse/aktuelles-vorschau/"),
            new MuseumGalleryItem(51.0286746, 13.73687464, "Buchmuseum der Sächsischen Landesbibliothek", "www.slub-dresden.de"),
            new MuseumGalleryItem(51.05809829, 13.77773434, "Galerie im Herzzentrum", "www.herzzentrum-dresden.com"),
            new MuseumGalleryItem(51.07470257, 13.74704338, "St. Pauli Salon", "www.theaterruine.de"),
            new MuseumGalleryItem(51.05790052, 13.74291737, "Galerie Gebr. Lehmann", "www.galerie-gebr-lehmann.de"),
            new MuseumGalleryItem(51.06601595, 13.76591012, "Galerie Kunstkeller - aktfotoARTdresden", "www.kunstkeller-dresden.de"),
            new MuseumGalleryItem(51.0604766, 13.74047286, "Galerie Ines Schulz", "www.galerie-ines-schulz.de"),
            new MuseumGalleryItem(51.00851248, 13.87223805, "Kunstgewerbemuseum, Schloss Pillnitz", "kunstgewerbemuseum.skd.museum/"),
            new MuseumGalleryItem(51.05627253, 13.72080611, "Motorenhalle - Projektzentrum für zeitgenössische Kunst", "riesa-efau.de/kunst-erleben/motorenhalle/aktuell-vorschau/"),
            new MuseumGalleryItem(51.06146008, 13.7401707, "Museum Körnigreich", "www.koernigreich.org"),
            new MuseumGalleryItem(51.00718498, 13.79331482, "Galerie im Palitzschhof", "www.jks-dresden.de"),
            new MuseumGalleryItem(51.02938945, 13.72241569, "Gedenkstätte Münchner Platz", "www.muenchner-platz-dresden.de"),
            new MuseumGalleryItem(51.03194249, 13.77613562, "Galerie Magarete Friesen - Galerie für zeitgenössische Kunst und Klassische Moderne", "www.galerie-friesen.de"),
            new MuseumGalleryItem(51.04109055, 13.8006375, "Galerie im Medienkulturhaus Pentacon", "www.medienkulturhaus.de"),
            new MuseumGalleryItem(51.06014358, 13.74137004, "Galerie Finckenstein", "www.galerie-finckenstein.com"),
            new MuseumGalleryItem(51.05452734, 13.81411246, "Galerie am Damm", "www.galerie-am-damm.de"),
            new MuseumGalleryItem(51.06002743, 13.74287807, "Museum der Dresdner Romantik - Kügelgenhaus", "www.museen-dresden.de"),
            new MuseumGalleryItem(51.05709915, 13.72786604, "Kunsthalle im Penck Hotel Dresden", "penckhoteldresden.de/gallery/"),
            new MuseumGalleryItem(51.06683193, 13.76050587, "galerie kunstgehæuse", "www.kunstgehaeuse.de"),
            new MuseumGalleryItem(51.10714883, 13.75886825, "Deutsche Werkstätten Hellerau - Werkstättengalerie", "www.dwh.de"),
            new MuseumGalleryItem(51.06077422, 13.74084003, "Studio + Galerie Härtel", "www.haertel-grafik.de"),
            new MuseumGalleryItem(51.0528702, 13.74353564, "Kunsthalle im Lipsiusbau", "lipsiusbau.skd.museum/besuch/"),
            new MuseumGalleryItem(51.04552712, 13.67669539, "Galerie im Club Passage", "www.club-passage.de"),
            new MuseumGalleryItem(51.04552712, 13.67669539, "Galerie im Club Passage", "www.club-passage.de"),
            new MuseumGalleryItem(51.01026594, 13.82526355, "Ausstellung im Bürgersaal Rathaus Leuben", "www.dresden.de/de/rathaus/stadtbezirksaemter/leuben.php"),
            new MuseumGalleryItem(51.02782475, 13.72534554, "ALTANAGalerie", "de-de.facebook.com/altana.galerie/"),
            new MuseumGalleryItem(51.07619263, 13.75103745, "DRESDEN PUBLIC ART VIEW", "www.crockefeller.org"),
            new MuseumGalleryItem(51.04832421, 13.74115927, "Institut Francaise de Dresde", "dresden.institutfrancais.de"),
            new MuseumGalleryItem(51.06353821, 13.74938936, "art & form", "www.artundform.de"),
            new MuseumGalleryItem(51.0608863, 13.75225915, "Abstrakte Momente", "www.abstrakte-momente.de"),
            new MuseumGalleryItem(51.06759652, 13.76063394, "GALERIE DREI - Dresdner Sezession 89 e.V.", "www.sezession89.de"),
            new MuseumGalleryItem(51.06267627, 13.82440781, "Rahmen & Bild Maria Arlt", "www.arlt-bilderrahmen.de"),
            new MuseumGalleryItem(51.05783478, 13.79594715, "Haus der Architekten", "www.aksachsen.org/index.php?id=428"),
            new MuseumGalleryItem(51.06074017, 13.74368266, "Galerie in der Dreikönigskirche", "www.hdk-dkk.de"),
            new MuseumGalleryItem(51.06209601, 13.75389666, "Galerie Emmagoss", "www.arth-of-sites.com/emmanuelpg/"),
            new MuseumGalleryItem(51.05324525, 13.73878251, "Ausstellung im Oberlandesgericht Dresden im Ständehaus", "www.justiz.sachsen.de/olg/content/576.htm"),
            new MuseumGalleryItem(51.05364753, 13.81487098, "KunstGalerieHans", "www.kunstgaleriehans.de"),
            new MuseumGalleryItem(51.08125878, 13.7576122, "Stadtarchiv Dresden", "www.dresden.de/stadtarchiv"),
            new MuseumGalleryItem(51.06084956, 13.7395696, "Sächsische Akademie der Künste", "www.sadk.de"),
            new MuseumGalleryItem(51.05802961, 13.74273253, "Galerie Ursula Walter", "www.galerieursulawalter.de"),
            new MuseumGalleryItem(51.0700484, 13.69895246, "Ostrale - Zentrum für Zeitgenössische Kunst", "www.ostrale.de"),
            new MuseumGalleryItem(51.07406826, 13.75351698, "Ausstellung im Goethe-Institut Dresden", "www.goethe.de/ins/de/ort/dre/deindex.htm"),
            new MuseumGalleryItem(51.0653368, 13.73788085, "Galerie Blaue Fabrik", "www.blauefabrik.de"),
            new MuseumGalleryItem(51.05316252, 13.74248181, "Hochschule für Bildende Künste Dresden", "www.hfbk-dresden.de"),
            new MuseumGalleryItem(51.05356508, 13.81439566, "Galerie Hieronymus", "www.galerie-hieronymus.de"),
            new MuseumGalleryItem(51.06245685, 13.7478572, "Galerie Dresdner Volksbank Raiffeisenbank - Villa Eschebach", "www.volksbank-dresden-bautzen.de/bank-vor-ort/veranstaltungen/Veranstaltungen_Jahr_2020.html"),
            new MuseumGalleryItem(51.06612083, 13.7652656, "ELLY BROSE-EIERMANN I Büro für Kunst", "www.elly-brose-eiermann.de"),
            new MuseumGalleryItem(51.047108, 13.77156662, "Galerie Mitte", "www.galerie-mitte.de"),
            new MuseumGalleryItem(51.04423291, 13.74679733, "Deutsches Hygiene-Museum Dresden", "www.dhmd.de"),
            new MuseumGalleryItem(51.05590544, 13.72103336, "Runde Ecke - Ausstellungs- und Projektraum des riesa efau", "riesa-efau.de/kunst-erleben/galerie-adlergasse/aktuelles-vorschau/"),
            new MuseumGalleryItem(51.05678536, 13.74567381, "Sächsisches Staatsministerium für Kultus und Sport", "www.smk.sachsen.de"),
            new MuseumGalleryItem(51.05330626, 13.81699798, "Galerie Felix", "www.galerie-felix.de"),
            new MuseumGalleryItem(51.06345151, 13.82078943, "Projektraum am Weißen Hirsch - Galerie Grafikladen", "www.grafikladen.com"),
            new MuseumGalleryItem(51.0599907, 13.74174261, "Galerie Himmel", "www.galerie-himmel.de"),
            new MuseumGalleryItem(51.0420396, 13.79767573, "Technische Sammlungen der Stadt Dresdenn", "www.tsd.de"),
            new MuseumGalleryItem(51.11364329, 13.7530756, "Hellerau - Europäisches Zentrum der Künste Dresden", "www.hellerau.org"),
            new MuseumGalleryItem(51.04759145, 13.74025793, "Galerie 2. Stock im Rathaus", "www.dresden.de/de/rathaus/aktuelles/galerie-2-stock_137059.php"),
            new MuseumGalleryItem(51.06418854, 13.75801344, "Ausstellung bei Hatikva", "www.hatikva.de"),
            new MuseumGalleryItem(51.05224984, 13.72358105, "Kraftwerk Mitte Kunsthalle", "www.kraftwerk-mitte-dresden.de"),
            new MuseumGalleryItem(51.02635668, 13.83675577, "DIVERSO - die LadenGALERIE", "www.sabine-just.de/ladengalerie-diverso.html"),
            new MuseumGalleryItem(51.06032133, 13.74091827, "Galerie Flox", "www.galerie-flox.de"),
            new MuseumGalleryItem(51.06315633, 13.75588074, "Galerie nEUROPA", "www.kulturaktiv.org/galerie-neuropa"),
            new MuseumGalleryItem(51.05393339, 13.7238779, "Ausstellung im Dresdner FriedrichStaTT Palast", "www.dresdner-friedrichstatt-palast.de"),
            new MuseumGalleryItem(51.05804903, 13.74247608, "Galerie Stephanie Kelly", "www.stephanie-kelly.de"),
            new MuseumGalleryItem(51.05309696, 13.81451167, "Galerie Alte Feuerwache Loschwitz", "www.Feuerwache-Loschwitz.de"),
            new MuseumGalleryItem(51.05989628, 13.74092671, "GALERIE HOLGER JOHN", "www.galerie-holgerjohn.com"),
            new MuseumGalleryItem(51.06283139, 13.75001877, "Kunstgalerie Centaura", "www.seel-art.de"),
            new MuseumGalleryItem(51.03318566, 13.71117292, "Galerie Falkenbrunnen", ""),
            new MuseumGalleryItem(51.02180512, 13.75906213, "Kunst + Bau", "www.freie-akademie-dresden.de"),
            new MuseumGalleryItem(51.06012958, 13.74392699, "Künstlerbund Dresden", "www.kuenstlerbund-dresden.de"),
            new MuseumGalleryItem(51.05106625, 13.74008711, "Galerie Forum Waldenburg", "www.forum-waldenburg.de"),
            new MuseumGalleryItem(51.05211127, 13.74441529, "Galerie Neue Meister mit Skulpturensammlung ab 1800", "albertinum.skd.museum/"),
            new MuseumGalleryItem(51.06310129, 13.84627227, "art-erlebnis", "www.art-erlebnis.de"),
            new MuseumGalleryItem(51.06946019, 13.75667814, "Galerie Gaia", "www.galerie-gaia.de"),
            new MuseumGalleryItem(51.05364736, 13.81549755, "Galerie am Blauen Wunder", ""),
            new MuseumGalleryItem(51.00926656, 13.8761058, "Kunstraum Pillnitz", "www.kunstraum-pillnitz.de"),
            new MuseumGalleryItem(51.07337229, 13.73063865, "geh8 - Ateliergemeinschaft und Kunstraum", "www.geh8.de"),
            new MuseumGalleryItem(51.05137474, 13.73382926, "art'SAP Dresden", "www.facebook.com/ArtSAPDD/"),
            new MuseumGalleryItem(51.08130072, 13.76039225, "Zeitenströmung", "www.zeitenstroemung.de"),
            new MuseumGalleryItem(51.03941885, 13.7051948, "Galerie kraussERBEN", ""),
            new MuseumGalleryItem(51.05619497, 13.73315174, "Sächsischer Landtag", "www.landtag.sachsen.de/de/index.aspx")
    );


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.btm_nav);

        // Clear any selected item in BottomNavigationView immediately after it's created
        bottomNavigationView.setSelectedItemId(-1); // Clear any selection

        // Now set the item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.cluster) {
                fragment = new ClusterFragment();
            } else if (item.getItemId() == R.id.heatmap) {
                fragment = new HeatmapFragment();
            }

            if (fragment != null) {
                // Update the camera position before switching fragments
                if (fragment instanceof ClusterFragment) {
                    ((ClusterFragment) fragment).setLastCameraPosition(lastCameraPosition);
                } else if (fragment instanceof HeatmapFragment) {
                    ((HeatmapFragment) fragment).setLastCameraPosition(lastCameraPosition);
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.map, fragment)
                        .commit();
            }

            return true;
        });

        // Your existing map fragment setup
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Location permission setup
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if ((fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted)) {
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    } else {
                        Toast.makeText(this,
                                "Location cannot be obtained due to missing permission.",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Adjust control padding after layout is rendered
        View textView = findViewById(R.id.text_view_1);
        View navigationBar = findViewById(R.id.btm_nav);

        textView.post(() -> {
            int topPadding = textView.getHeight(); // Height of the TextView
            int bottomPadding = navigationBar.getHeight(); // Height of the navigation bar
            int additionalBottomPadding = 150; // Optional extra space for zoom controls

            // Apply padding to map controls
            mMap.setPadding(0, topPadding, 0, bottomPadding + additionalBottomPadding);
        });

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.051879, 13.740115))
                .icon(BitmapDescriptorFactory.defaultMarker(210))
                .alpha(0.5f));

        CameraPosition cam_pos =
                new CameraPosition.Builder()
                        .target(new LatLng(51.051879, 13.740115))
                        .zoom(15)
                        .tilt(20)
                        .bearing(0)
                        .build();

        mMap.setBuildingsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam_pos));
        MarkerOptions myMarker = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(210))
                .position(new LatLng(51.035353, 13.728437))
                .anchor(0.5f, 1);
        mMap.addMarker(myMarker);

        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        locationPermissionRequest.launch(PERMISSIONS);

        mMap.setOnCameraIdleListener(() -> {
            lastCameraPosition = mMap.getCameraPosition();
        });
    }

    private class DownloadGeoJsonFile extends AsyncTask<String, Void, GeoJsonLayer> {

        @Override
        protected GeoJsonLayer doInBackground(String... params) {
            try {
                InputStream stream = new URL(params[0]).openStream();

                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                stream.close();

                return new GeoJsonLayer(mMap, new JSONObject(result.toString()));
            } catch (IOException e) {
                Log.e("mLogTag", "GeoJSON file could not be read");
            } catch (JSONException e) {
                Log.e("mLogTag",
                        "GeoJSON file could not be converted to a JSONObject");
            }
            return null;
        }

        @Override
        protected void onPostExecute(GeoJsonLayer layer) {
            if (layer != null) {
                GeoJsonLineStringStyle lineStringStyle =
                        layer.getDefaultLineStringStyle();
                lineStringStyle.setColor(Color.RED);
                lineStringStyle.setWidth(10f);

                layer.addLayerToMap();
            }
        }

    }
}