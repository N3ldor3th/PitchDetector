package cz.osu.kuznikjan.pitchlibrary;


import be.tarsos.dsp.pitch.PitchDetectionResult;

public class Note{

    private int noteIndex = -1;
    private String noteName = "?";
    private double differenceHz = 0.00f;
    private double differenceCents = 0.00f;
    private double differencePercent = 0.00f;
    private static final int LOG10_CENT_CONSTANT = 3986; // 1200/log10(2)

    private static String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C"};

    public Note(){

    }

    public Note(double[] frequencies, PitchDetectionResult pitchDetectionResult) {
        getNote(frequencies,pitchDetectionResult);
    }

    private void getNote(double[] frequencies, PitchDetectionResult pitchDetectionResult){
        setDifferenceHz(Integer.MAX_VALUE);
        for (int i = 0; i < frequencies.length; i++) {
            double diff = Math.abs(pitchDetectionResult.getPitch() - frequencies[i]);
            if (diff < differenceHz) {
                setNoteIndex(i);
                setDifferenceHz(diff);
            }
        }
        setNoteName(noteNames[noteIndex]);
        setDifferenceCents(LOG10_CENT_CONSTANT * Math.log10(pitchDetectionResult.getPitch()/frequencies[noteIndex]));
        setDifferencePercent((pitchDetectionResult.getPitch() / frequencies[noteIndex]) * 100);
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

    @Override
    public String toString() {
        return "Note{" +
                "noteIndex=" + noteIndex +
                ", noteName='" + noteName + '\'' +
                ", differenceHz=" + differenceHz +
                ", differenceCents=" + differenceCents +
                ", differencePercent=" + differencePercent +
                '}';
    }
}
