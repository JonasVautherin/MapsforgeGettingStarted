package org.mapsforge.gettingstarted.android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

public class MainActivity extends ActionBarActivity {

    private MapView mMapView;
    private TileCache mTileCache;
    private TileRendererLayer mTileRendererLayer;

    private File getMapFile()
    {
        File file = new File("/sdcard/germany.map");
        return file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidGraphicFactory.createInstance(this.getApplication());

        mMapView = new MapView(this);
        setContentView(mMapView);

        mMapView.setClickable(true);
        mMapView.getMapScaleBar().setVisible(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.getMapZoomControls().setZoomLevelMin((byte) 10);
        mMapView.getMapZoomControls().setZoomLevelMax((byte) 20);

        // Create a cache of suitable size
        mTileCache = AndroidUtil.createTileCache(this, "mapcache",
                mMapView.getModel().displayModel.getTileSize(), 1f,
                mMapView.getModel().frameBufferModel.getOverdrawFactor());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mTileCache.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        mMapView.getModel().mapViewPosition.setCenter(new LatLong(52.517037, 13.38886));
        mMapView.getModel().mapViewPosition.setZoomLevel((byte) 12);

        // Tile renderer layer using internal render theme
        mTileRendererLayer = new TileRendererLayer(mTileCache,
                mMapView.getModel().mapViewPosition, false, AndroidGraphicFactory.INSTANCE);
        mTileRendererLayer.setMapFile(getMapFile());
        mTileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // Associate a layer to the MapView
        mMapView.getLayerManager().getLayers().add(mTileRendererLayer);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        mMapView.getLayerManager().getLayers().remove(mTileRendererLayer);
        mTileRendererLayer.onDestroy();
    }
}
