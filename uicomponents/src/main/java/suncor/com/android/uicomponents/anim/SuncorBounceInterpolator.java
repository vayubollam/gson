package suncor.com.android.uicomponents.anim;

import android.view.animation.Interpolator;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;

public class SuncorBounceInterpolator implements Interpolator {
    private int mBounces;
    private double mEnergy;

    /**
     * Have more control over how to bounce your values.
     */
    public SuncorBounceInterpolator() {
        this(3);
    }

    /**
     * Have more control over how to bounce your values.
     *
     * @param bounces number of times to bounce before coming to a rest
     */
    public SuncorBounceInterpolator(int bounces) {
        this(bounces, 0.3f);
    }

    /**
     * Have more control over how to bounce your values.
     *
     * @param bounces      number of times to bounce before coming to a rest
     * @param energyFactor control how the bounce loses momentum. 0 is lose energy linearly. 1 energy loss slows down over time, -1 energy loss speeds up over time. Values greater than 1 and less than -1 cause over/under bounce
     */
    public SuncorBounceInterpolator(int bounces, double energyFactor) {
        mBounces = bounces;
        mEnergy = energyFactor + 0.5;
    }

    @Override
    public float getInterpolation(float x) {
        return (float) (1d + (-abs(cos(x * 10 * mBounces / PI)) * getCurveAdjustment(x)));
    }

    private double getCurveAdjustment(double x) {
        return -(2 * (1 - x) * x * mEnergy + x * x) + 1;
    }
}