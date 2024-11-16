package org.example.photoredactor.presence.color;

public class SaturationSetting extends LuminanceSetting {
    @Override
    public double[] changePixel(double[] rgb) {
        changeRGB = (color, coef) ->
                greyComponent + coef * (color - greyComponent);

        return super.changePixel(rgb);
    }
}
