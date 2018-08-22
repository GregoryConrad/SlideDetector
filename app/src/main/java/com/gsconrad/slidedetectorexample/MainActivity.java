package com.gsconrad.slidedetectorexample;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gsconrad.slidedetector.SlideDetector;

public class MainActivity extends AppCompatActivity {
    private SlideDetector slideDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSlideDetector();
        setupTabs();
    }

    /**
     * Configures the SlideDetector
     */
    private void setupSlideDetector() {
        final View view = findViewById(R.id.view);
        SlideDetector.OnSlideListener slideListener = new SlideDetector.OnSlideListener() {
            /**
             * Called when the finger presses the view
             * @param detector the slide detector that fired this method
             */
            @Override
            public void onDown(SlideDetector detector) {
            }

            /**
             * Called when the finger is dragged/slides across the screen after touching the view
             * @param detector the slide detector that fired this method
             * @param slideX the amount the finger slid in the x direction
             * @param slideY the amount the finger slid in the y direction
             */
            @Override
            public void onSlide(SlideDetector detector, float slideX, float slideY) {
                // NOTE: SlideMode is used to determine what types of finger movements to listen for
                //       If you do not care about the initial direction the finger moves in,
                //       Use the one-arg SlideDetector constructor or use setSlideMode(ANY)
                switch (detector.getSlideMode()) {
                    case ANY:
                        // follow finger in both x and y
                        view.setX(view.getX() + slideX);
                        view.setY(view.getY() + slideY);
                        break;
                    case HORIZONTAL:
                        // follow finger in x direction
                        view.setX(view.getX() + slideX);
                        break;
                    case VERTICAL:
                        // follow finger in y direction
                        view.setY(view.getY() + slideY);
                        break;
                }
            }

            /**
             * Called when the finger is released from the view
             * @param detector the slide detector that fired this method
             * @param didSlide true if the finger slid, false otherwise
             */
            @Override
            public void onRelease(SlideDetector detector, boolean didSlide) {
                if (didSlide) {
                    // move view back to center
                    view.animate().translationX(0).setDuration(100);
                    view.animate().translationY(0).setDuration(100);
                } else {
                    // todo handle a typical onClick event
                    Log.d("MainActivity", "View was clicked but did not slide. " +
                            "Essentially an onClick occurred");
                }
            }
        };
        slideDetector = new SlideDetector(SlideDetector.SlideMode.HORIZONTAL, slideListener);
        // Forward the touch event to the slide detector to process
        view.setOnTouchListener(slideDetector);
        // Instead of using slideDetector as the touch listener, you can also
        //     make your own touch listener and then forward the calls onto the slide detector
        //     with return slideDetector.onTouch(motionEvent);
        //     You may want to do this for registering double taps or something similar
    }

    /**
     * Sets up the tabs and the tab listener
     */
    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_horizontal));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_vertical));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_any));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Set the slide mode based on the tab
                slideDetector.setSlideMode(SlideDetector.SlideMode.values()[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
