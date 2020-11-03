/*
 * Copyright (c) 2000-2017 TeamDev Ltd. All rights reserved.
 * Use is subject to Apache 2.0 license terms.
 */
package com.teamdev.jxmaps.examples;

import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapReadyHandler;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.MapViewOptions;
import com.teamdev.jxmaps.StreetViewAddressControlOptions;
import com.teamdev.jxmaps.StreetViewPanoramaOptions;
import com.teamdev.jxmaps.StreetViewPov;
import com.teamdev.jxmaps.swing.MapView;

import javax.swing.*;
import java.awt.*;

/**
 * This example demonstrates how to display street view panorama with a map.
 *
 * @author Vitaly Eremenko
 */
public class StreetViewExample extends MapView {
    public StreetViewExample() {
    	super();
    	init();
    }
    public StreetViewExample(MapViewOptions options) {
    	super(options);
    	init();
    }
    private void init(){

        // Setting of a ready handler to MapView object. onMapReady will be called when map initialization is done and
        // the map object is ready to use. Current implementation of onMapReady customizes the map object.
        setOnMapReadyHandler(new MapReadyHandler() {
            @Override
            public void onMapReady(MapStatus status) {
                // Check if the map is loaded correctly
                if (status == MapStatus.MAP_STATUS_OK) {
                    // Getting the associated map object
                    Map map = getMap();
                    // Creating a map options object
                    MapOptions mapOptions = new MapOptions();
                    // Creating a map type control options object
                    MapTypeControlOptions controlOptions = new MapTypeControlOptions();
                    // Changing position of the map type control
                    controlOptions.setPosition(ControlPosition.TOP_RIGHT);
                    // Setting map type control options
                    mapOptions.setMapTypeControlOptions(controlOptions);
                    // Setting map options
                    map.setOptions(mapOptions);
                    // Setting the map center
                    map.setCenter(new LatLng(51.500871, -0.1222632));
                    // Setting initial zoom value
                    map.setZoom(13.0);
                    // Creating a street view panorama options object
                    StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
                    // Creating a street view address control options object
                    StreetViewAddressControlOptions svControlOptions = new StreetViewAddressControlOptions();
                    // Changing position of the address control on the panorama
                    svControlOptions.setPosition(ControlPosition.TOP_RIGHT);
                    // Setting address control options
                    options.setAddressControlOptions(svControlOptions);
                    // Setting street view panorama options
                    getPanorama().setOptions(options);
                    // Setting initial position of the street view
                    getPanorama().setPosition(map.getCenter());
                    // Creating point of view object
                    StreetViewPov pov = new StreetViewPov();
                    // Setting heading for the point of view
                    pov.setHeading(270);
                    // Setting pitch for the point of view
                    pov.setPitch(20);
                    // Applying the point of view to the panorama object
                    getPanorama().setPov(pov);
                }
            }
        });
    }

    public static void main(String[] args) {
    	String googleAPIKey = 0<args.length ? args[0] : null;
    	StreetViewExample sample;
    	
    	if(null==googleAPIKey) {
    		sample = new StreetViewExample();
    	}
    	else {
    		MapViewOptions options = new MapViewOptions();
            options.importPlaces();
            options.setApiKey(googleAPIKey);
    		sample = new StreetViewExample(options);
    	}

        JFrame frame = new JFrame("Street View");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(sample, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
