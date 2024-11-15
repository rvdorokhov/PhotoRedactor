package org.example.photoredactor.presence;

public class SaturationSetting extends LuminanceSetting {
    @Override
    public double[] change(double[] rgb) {
        changeRGB = (color, coef) ->
                greyComponent + coef * (color - greyComponent);

        return super.change(rgb);
    }
}
