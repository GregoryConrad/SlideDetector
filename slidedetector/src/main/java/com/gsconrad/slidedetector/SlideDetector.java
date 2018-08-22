package com.gsconrad.slidedetector;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

@SuppressWarnings("unused")
public class SlideDetector implements View.OnTouchListener {
    /**
     * Represents the types of slides that can occur and can be accepted
     */
    public enum SlideMode {
        HORIZONTAL, VERTICAL, ANY
    }

    /**
     * Represents what the current state is; for internal use
     */
    private enum SlideType {
        SLIDING, NOT_SLIDING, NOT_SET
    }

    /**
     * A listener used for when different touch events occur
     */
    public interface OnSlideListener {
        /**
         * Called when the view is pressed
         *
         * @param detector the SlideDetector that fired the method
         */
        void onDown(SlideDetector detector);

        /**
         * Called when the finger slides after touching the view
         *
         * @param detector the SlideDetector that fired the method
         * @param slideX   the amount the finger slid in the x direction
         * @param slideY   the amount the finger slid in the y direction
         */
        void onSlide(SlideDetector detector, float slideX, float slideY);

        /**
         * Called when the finger is released after touching the view
         *
         * @param detector the SlideDetector that fired the method
         * @param didSlide true if the finger slid, false otherwise
         */
        void onRelease(SlideDetector detector, boolean didSlide);
    }

    /**
     * The main constructor that assumes SlideMode.ANY and sets the listener
     *
     * @param listener the listener to call when an event occurs
     */
    public SlideDetector(@NonNull OnSlideListener listener) {
        setOnSlideListener(listener);
    }

    /**
     * Constructs a SlideDetector with a custom slideMode
     *
     * @param slideMode the SlideMode that specifies what types of slides to listen for
     * @param listener  the listener to call when an event occurs
     */
    public SlideDetector(@NonNull SlideMode slideMode, @NonNull OnSlideListener listener) {
        setSlideMode(slideMode);
        setOnSlideListener(listener);
    }

    private Float startX = null, startY = null, currentX = null, currentY = null;
    private OnSlideListener slideListener;
    private SlideMode slideMode = SlideMode.ANY;
    private SlideType slideType = SlideType.NOT_SET;

    /**
     * @return the starting x position of the finger from onDown
     */
    public Float getStartX() {
        return startX;
    }

    /**
     * @return the starting y position of the finger from onDown
     */
    public Float getStartY() {
        return startY;
    }

    /**
     * @return the most recent x value of the finger
     */
    public Float getCurrentX() {
        return currentX;
    }

    /**
     * @return the most recent y value of the finger
     */
    public Float getCurrentY() {
        return currentY;
    }

    /**
     * @return the SlideMode specifying what types of slides this SlideDetector should acknowledge
     */
    public SlideMode getSlideMode() {
        return slideMode;
    }

    /**
     * Sets the OnSlideListener
     *
     * @param listener the listener to call when an event occurs
     */
    @SuppressWarnings("WeakerAccess")
    public void setOnSlideListener(@NonNull OnSlideListener listener) {
        //noinspection ConstantConditions
        if (listener == null) throw new IllegalArgumentException("OnSlideListener cannot be null");
        this.slideListener = listener;
    }

    /**
     * Sets the SlideMode
     *
     * @param slideMode specifies what types of slides this SlideDetector should acknowledge
     */
    public void setSlideMode(@NonNull SlideMode slideMode) {
        //noinspection ConstantConditions
        if (slideMode == null) throw new IllegalArgumentException("SlideMode cannot be null");
        this.slideMode = slideMode;
    }

    /**
     * The convenience method that calls the internal onTouch
     * without having to make a separate OnTouchListener
     *
     * @param v     the view that was touched
     * @param event the MotionEvent
     * @return whether the event was handled
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return onTouch(event);
    }

    /**
     * Processes the MotionEvents and determines if a slide occurred
     *
     * @param event the MotionEvent from the OnTouchListener
     * @return true if the event was absorbed, false otherwise
     */
    @SuppressWarnings("WeakerAccess")
    public boolean onTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_CANCEL:
                startX = null;
                startY = null;
                currentX = null;
                currentY = null;
                break;
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                currentX = event.getRawX();
                currentY = event.getRawY();
                slideType = SlideType.NOT_SET;
                slideListener.onDown(this);
                return true;
            case MotionEvent.ACTION_UP:
                slideListener.onRelease(this, slideType == SlideType.SLIDING);
                startX = null;
                startY = null;
                currentX = null;
                currentY = null;
                if (slideType == SlideType.SLIDING) return true;
                break;
            case MotionEvent.ACTION_MOVE:
                float oldX = currentX, oldY = currentY;
                currentX = event.getRawX();
                currentY = event.getRawY();
                if (slideType == SlideType.NOT_SET) {
                    float deltaX = Math.abs(currentX - startX),
                            deltaY = Math.abs(currentY - startY);
                    if (deltaX == 0 && deltaY == 0) return true;
                    switch (slideMode) {
                        case ANY:
                            slideType = SlideType.SLIDING;
                            break;
                        case HORIZONTAL:
                            slideType = (deltaX >= deltaY) ?
                                    SlideType.SLIDING : SlideType.NOT_SLIDING;
                            break;
                        case VERTICAL:
                            slideType = (deltaY >= deltaX) ?
                                    SlideType.SLIDING : SlideType.NOT_SLIDING;
                            break;
                    }
                }
                if (slideType == SlideType.SLIDING) {
                    slideListener.onSlide(this,
                            currentX - oldX, currentY - oldY);
                    return true;
                }
                break;
        }
        return false;
    }
}
