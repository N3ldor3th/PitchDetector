package cz.osu.kuznikjan.pitchdetector;

public class NoteResult {
    private double pitch = 0.00f; // pitch of the note in Hz
    private boolean isPitched = false;
    private String note = ""; // name of the closest note
    private double probability = 0.00f; // probability of the note in %
    private double closenessPercent = 0.00f; // closeness to pitch in %
    private double closenessHz = 0.00f; // closeness to pitch in Hz
    private double closenessCents = 0.00f; //closeness to pitch in cents

    public NoteResult() {
    }

    public NoteResult(double pitch, boolean isPitched, String note, double probability, double closenessPercent, double closenessHz, double closenessCents) {
        this.pitch = pitch;
        this.isPitched = isPitched;
        this.note = note;
        this.probability = probability;
        this.closenessPercent = closenessPercent;
        this.closenessHz = closenessHz;
        this.closenessCents = closenessCents;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getClosenessPercent() {
        return closenessPercent;
    }

    public void setClosenessPercent(double closenessPercent) {
        this.closenessPercent = closenessPercent;
    }

    public double getClosenessHz() {
        return closenessHz;
    }

    public void setClosenessHz(double closenessHz) {
        this.closenessHz = closenessHz;
    }

    public double getClosenessCents() {
        return closenessCents;
    }

    public void setClosenessCents(double closenessCents) {
        this.closenessCents = closenessCents;
    }

    public boolean isPitched() {
        return isPitched;
    }

    public void setPitched(boolean pitched) {
        isPitched = pitched;
    }

    @Override
    public String toString() {
        return "NoteResult{" +
                "pitch=" + pitch +
                ", isPitched=" + isPitched +
                ", note='" + note + '\'' +
                ", probability=" + probability +
                ", closenessPercent=" + closenessPercent +
                ", closenessHz=" + closenessHz +
                ", closenessCents=" + closenessCents +
                '}';
    }
}
