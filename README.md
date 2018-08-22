# SlideDetector
A simple library that detects when a finger slides on a view

# Obligatory demo
<img height="400" alt="Demo gif" src="https://raw.githubusercontent.com/GregoryConrad/SlideDetector/master/demo.gif">

# Adding to your project
Add this to your module's build.gradle file under `dependencies`:

`implementation 'com.gsconrad:slidedetector:1.0.0'`

If that does not work, ensure `jcenter()` is in your project build.gradle's `repositories` section—it should be by default.

And if for some reason, you still can't get it to work, try using [Jitpack](https://jitpack.io/#gregoryconrad/slidedetector/v1.0.0).

# Usage
For a full working example (where the screenshots came from), see the example app.
You can clone this repository and open it in Android Studio to see the example app. But, here is an easy code snippet:
```
// MainActivity.java
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
```

# More information
There isn't much else you need to know, as this library is very simple. If you find any issues, please file an issue. Otherwise, here are some mock code snippets of features you can use:
```
/**
 * @return the starting x position of the finger from onDown
 */
public Float getStartX()

/**
 * @return the starting y position of the finger from onDown
 */
public Float getStartY()

/**
 * @return the most recent x value of the finger
 */
public Float getCurrentX()

/**
 * @return the most recent y value of the finger
 */
public Float getCurrentY()

/**
 * @return the SlideMode specifying what types of slides the SlideDetector should acknowledge
 */
public SlideMode getSlideMode()

/**
 * Sets the OnSlideListener
 *
 * @param listener the listener to call when an event occurs
 */
public void setOnSlideListener(@NonNull OnSlideListener listener)

/**
 * Sets the SlideMode
 *
 * @param slideMode specifies what types of slides this SlideDetector should acknowledge
 */
public void setSlideMode(@NonNull SlideMode slideMode)
```
