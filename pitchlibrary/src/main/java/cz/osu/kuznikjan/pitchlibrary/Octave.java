package cz.osu.kuznikjan.pitchlibrary;


import be.tarsos.dsp.pitch.PitchDetectionResult;

public class Octave {

    private int octaveIndex = -1;
    private double[] octaveFrequencies = oct0;

    private static final double ALLOWABLE_ERROR = 0.5; //hz

    private static double[] oct0 = {16.35, 17.32, 18.35, 19.45, 20.60, 21.83, 23.12, 24.50, 25.96, 27.50, 29.14, 30.87, 32.70};
    private static double[] oct1 = {32.70, 34.65, 36.71, 38.89, 41.20, 43.65, 46.25, 49, 51.91, 55, 58.27, 61.74, 65.41};
    private static double[] oct2 = {65.41, 69.30, 73.42, 77.78, 82.41, 87.31, 92.50, 98, 103.8, 110, 116.5, 123.5, 130.8};
    private static double[] oct3 = {130.8, 138.6, 146.8, 155.6, 164.8, 174.6, 185.0, 196, 207.7, 220, 233.1, 246.9, 261.6};
    private static double[] oct4 = {261.6, 277.2, 293.7, 311.1, 329.6, 349.2, 370, 392, 415.3, 440, 466.2, 493.9, 523.3};
    private static double[] oct5 = {523.3, 554.4, 587.3, 622.3, 659.3, 698.5, 740, 784, 830.6, 880, 932.3, 987.8, 1047};
    private static double[] oct6 = {1047, 1109, 1175, 1245, 1319, 1397, 1480, 1568, 1661, 1760, 1865, 1976, 2093};
    private static double[] oct7 = {2093, 2217, 2349, 2489, 2637, 2794, 2960, 3136, 3322, 3520, 3729, 3951, 4186};
    private static double[] oct8 = {4186, 4435, 4699, 4978, 5274, 5588, 5920, 6272, 6645, 7040, 7459, 7902, 8372};

    private static double[][] notes = {oct0, oct1, oct2, oct3, oct4, oct5, oct6, oct7, oct8};

    public Octave(){

    }

    public Octave(PitchDetectionResult pitchDetectionResult) {
        getOctave(pitchDetectionResult);
    }

    private void getOctave(PitchDetectionResult pitchDetectionResult){
        for (int i = 0; i < notes.length; i++) {
            octaveFrequencies = notes[i];
            if ((pitchDetectionResult.getPitch() > (octaveFrequencies[0] - ALLOWABLE_ERROR)) &&
                    (pitchDetectionResult.getPitch() < (octaveFrequencies[notes[i].length - 1] + ALLOWABLE_ERROR))) {
                octaveIndex = i;
                break;
            }
        }
    }

    public void increaseOctaveIndex(){
        octaveIndex++;
    }

    public int getOctaveIndex() {
        return octaveIndex;
    }

    public void setOctaveIndex(int octaveIndex) {
        this.octaveIndex = octaveIndex;
    }

    public double[] getOctaveFrequencies() {
        return octaveFrequencies;
    }

    public void setOctaveFrequencies(double[] octaveFrequencies) {
        this.octaveFrequencies = octaveFrequencies;
    }
}
