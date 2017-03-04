package cz.osu.kuznikjan.pitchdetector;


import be.tarsos.dsp.pitch.PitchDetectionResult;

public class Note {

    private int noteIndex = Integer.MIN_VALUE;
    private String noteName = "?";
    private double differenceHz = Double.MAX_VALUE;
    private double differenceCents = Double.MAX_VALUE;
    private double differencePercent = Double.MAX_VALUE;

    private static String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C"};

    public Note(double[] frequencies, PitchDetectionResult pitchDetectionResult) {
        for (int i = 0; i < frequencies.length; i++) {
            double diff = Math.abs(pitchDetectionResult.getPitch() - frequencies[i]);
            if (diff < differenceHz) {
                noteIndex = i;
                differenceHz = diff;
            }
        }
        noteName = noteNames[noteIndex];
        differenceCents = 1200 * Math.log(pitchDetectionResult.getPitch()/frequencies[noteIndex]);
        differencePercent = (pitchDetectionResult.getPitch() / frequencies[noteIndex]) * 100;
    }

    public int getNoteIndex() {
        return noteIndex;
    }

    public void setNoteIndex(int noteIndex) {
        this.noteIndex = noteIndex;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public double getDifferenceHz() {
        return differenceHz;
    }

    public void setDifferenceHz(double differenceHz) {
        this.differenceHz = differenceHz;
    }

    public double getDifferenceCents() {
        return differenceCents;
    }

    public void setDifferenceCents(double differenceCents) {
        this.differenceCents = differenceCents;
    }

    public double getDifferencePercent() {
        return differencePercent;
    }

    public void setDifferencePercent(double differencePercent) {
        this.differencePercent = differencePercent;
    }
}
